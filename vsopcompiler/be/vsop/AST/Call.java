package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.InvalidCallException;
import be.vsop.exceptions.semantic.MethodNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.LLVMTypes;
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
            if(called == null){
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
                            messageEnd.append((i+1)).append(", ");
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
    public void checkScope(ArrayList<SemanticException> errorList){
        Method called = classTable.get(objExpr.typeName).lookupMethod(methodId);
        if (called == null) {
            errorList.add(new MethodNotDeclaredException(methodId.getName(), line, column));
        }

        super.checkScope(errorList);
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("Call(");
        objExpr.print(tabLevel, false, withTypes);
        System.out.print("," + methodId.getName() + ",");
        argList.print(tabLevel, false, withTypes);
        System.out.print(")");
    }

    @Override
    public ExprEval evalExpr(InstrCounter counter) {
        //TODO call marche pas quand on apelle une fonction qui renvoie un void, on peut pas assigner qqc a un void

        ArrayList<String> argumentsIds = new ArrayList<>();
        ArrayList<String> argumentsTypes = new ArrayList<>();
        Method called = classTable.get(objExpr.typeName).lookupMethod(methodId);
        String implementedBy = called.scopeTable.getScopeClassType().getName();

        ExprEval curArgEval = objExpr.evalExpr(counter);
        StringBuilder llvm = new StringBuilder();
        llvm.append(curArgEval.llvmCode);
        if (!implementedBy.equals(objExpr.typeName)) {
            // Turn self pointer into a compatible pointer for the inherited function
            String intPointer = counter.getNextLlvmId();
            String pointerNewType = counter.getNextLlvmId();
            llvm.append(llvmCast(intPointer, LLVMKeywords.PTRTOINT, VSOPTypes.getLlvmTypeName(objExpr.typeName, true),
                    LLVMTypes.INT64, curArgEval.llvmId));
            llvm.append(llvmCast(pointerNewType, LLVMKeywords.INTTOPTR, LLVMTypes.INT64,
                    VSOPTypes.getLlvmTypeName(implementedBy, true), intPointer));
            argumentsIds.add(pointerNewType);
            argumentsTypes.add(VSOPTypes.getLlvmTypeName(implementedBy, true));
        } else {
            argumentsIds.add(curArgEval.llvmId);
            argumentsTypes.add(VSOPTypes.getLlvmTypeName(objExpr.typeName, true));
        }

        for (int i = 0; i < argList.size(); i++) {
            curArgEval = argList.get(i).evalExpr(counter);
            llvm.append(curArgEval.llvmCode);
            argumentsIds.add(curArgEval.llvmId);
            argumentsTypes.add(VSOPTypes.getLlvmTypeName(argList.get(i).typeName, true));
        }

        String llvmId = counter.getNextLlvmId();
        String funcName = "@" + called.scopeTable.getScopeClassType().getName() + "." + methodId.getName();
        llvm.append(llvmCall(llvmId, VSOPTypes.getLlvmTypeName(called.returnType(), true), funcName, argumentsIds, argumentsTypes));

        return new ExprEval(llvmId, llvm.toString());
    }

}