package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.InvalidCallException;
import be.vsop.exceptions.semantic.MethodNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.LLVMTypes;
import be.vsop.semantic.LlvmWrappers;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

public class Call extends Expr {
    private Expr objExpr;
    private Id methodId;
    private ArgList argList;

    public Call(Expr objExpr, Id methodId, ArgList argList) {
        this.objExpr = objExpr;
        this.methodId = methodId;
        this.argList = argList;

        this.children = new ArrayList<>();
        this.children.add(objExpr);
        this.children.add(methodId);
        this.children.add(argList);
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        String object = objExpr.typeName;
        if (object != null) {
            Method called = classTable.get(object).lookupMethod(methodId);

            //No such method
            if (called == null) {
                errorList.add(new MethodNotDeclaredException(methodId.getName(), methodId.line, methodId.column));
                return;
            }

            if (called.nbArguments() != argList.size()) {
                errorList.add(new InvalidCallException(called.getName(),
                        line, column, called.line, called.column, "different noPercentLlvmId of arguments"));
            } else {
                StringBuilder messageEnd = new StringBuilder("argument(s) ");
                String curArgType;
                boolean invalid = false;
                for (int i = 0; i < argList.size(); i++) {
                    curArgType = argList.get(i).typeName;
                    if (curArgType != null) {
                        if (isNotChild(curArgType, called.getArgument(i).getType().getName())) {
                            invalid = true;
                            messageEnd.append((i + 1)).append(", ");
                        }
                    }
                }
                if (invalid) {
                    messageEnd.setLength(messageEnd.length() - 2);
                    messageEnd.append(" differ(s) in type");
                    errorList.add(new InvalidCallException(called.getName(),
                            line, column, called.line, called.column, messageEnd.toString()));
                } else {
                    typeName = called.returnType();
                }
            }
        }
    }

    @Override
    public void checkScope(ArrayList<SemanticException> errorList) {
        Method called = classTable.get(objExpr.typeName).lookupMethod(methodId);
        if (called == null) {
            errorList.add(new MethodNotDeclaredException(methodId.getName(), line, column));
        }

        super.checkScope(errorList);
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        if (doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("Call(");
        objExpr.print(tabLevel, false, withTypes);
        System.out.print("," + methodId.getName() + ",");
        argList.print(tabLevel, false, withTypes);
        System.out.print(")");
    }

    @Override
    public ExprEval evalExpr(InstrCounter counter) {
        //TODO call marche pas quand on apelle une fonction qui renvoie un void, on peut pas assigner qqc a un voi

        // Ids of the arguments, obtained by recursively evaluating the expressions defining their values
        ArrayList<String> argumentsIds = new ArrayList<>();
        // Llvm types of the arguments, obtained through the argList instance variable
        ArrayList<String> argumentsTypes = new ArrayList<>();
        // Called method, containing the return type as well as the (first, in the order of inheritance)
        // class containing its definition
        Method called = classTable.get(objExpr.typeName).lookupMethod(methodId);
        String implementedBy = called.scopeTable.getScopeClassType().getName();
        StringBuilder llvm = new StringBuilder();

        ExprEval curArgEval = objExpr.evalExpr(counter);
        // Evaluate the expression defining the object on which the function is called
        llvm.append(curArgEval.llvmCode);

        if (!implementedBy.equals(objExpr.typeName)) {
            // If the function is inherited (i.e., not implemented by the current object type), we need to
            // turn the type of the self pointer into the type of the implementer, before calling the function
            // It won't hurt inside the body of the function, as any variable existing in the parent type
            // also exists in the child type, and at the same place
            // It does not actually change anything in the state of the code, but it is needed to be compliant
            // with llvm type-checking
            String intPointer = counter.getNextLlvmId();
            String pointerNewType = counter.getNextLlvmId();
            // First, cast the pointer to the current object into an int, using the ptrtoint function of llvm
            // We use i64 because an i32 could overflow on most current machines
            llvm.append(llvmCast(intPointer, LLVMKeywords.PTRTOINT, VSOPTypes.getLlvmTypeName(objExpr.typeName, true),
                    LLVMTypes.INT64, curArgEval.llvmId));
            // Then, cast the obtained int into a new pointer (using inttoptr), giving it the new type
            llvm.append(llvmCast(pointerNewType, LLVMKeywords.INTTOPTR, LLVMTypes.INT64,
                    VSOPTypes.getLlvmTypeName(implementedBy, true), intPointer));
            // Add the new type pointer as first argument
            argumentsIds.add(pointerNewType);
            argumentsTypes.add(VSOPTypes.getLlvmTypeName(implementedBy, true));
        } else {
            // If the function is not inherited, simply add calling object as first argument
            argumentsIds.add(curArgEval.llvmId);
            argumentsTypes.add(VSOPTypes.getLlvmTypeName(objExpr.typeName, true));
        }

        // Append code generating all other arguments, and add them into the list of arguments
        for (int i = 0; i < argList.size(); i++) {
            curArgEval = argList.get(i).evalExpr(counter);
            llvm.append(curArgEval.llvmCode);
            argumentsIds.add(curArgEval.llvmId);
            argumentsTypes.add(VSOPTypes.getLlvmTypeName(argList.get(i).typeName, true));
        }

        String llvmId;
        if (called.returnType().equals(VSOPTypes.UNIT.getName())) {
            // If the return type is unit (void), we don't save the result anywhere
            llvmId = null;
        } else {
            llvmId = counter.getNextLlvmId();
        }
        // Append code actually calling the function (which as a unique name)
        String funcName = "@" + implementedBy + "." + methodId.getName();
        llvm.append(LlvmWrappers.call(llvmId, VSOPTypes.getLlvmTypeName(called.returnType(), true),
                funcName, argumentsIds, argumentsTypes));

        return new ExprEval(llvmId, llvm.toString());
    }

}