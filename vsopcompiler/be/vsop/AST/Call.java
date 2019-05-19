package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.InvalidCallException;
import be.vsop.exceptions.semantic.MethodNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.*;

import java.util.ArrayList;

/**
 * This class represents a VSOP call, expr.function(arguments) or function(arguments)
 */
public class Call extends Expr {
    private Expr objExpr;
    private Id methodId;
    private ArgList argList;

    /**
     * Creates a new call on the function methodId of class objExpr, with arguments argList
     * @param objExpr the object on which the method is called
     * @param methodId the id of the called method
     * @param argList the list of arguments
     */
    public Call(Expr objExpr, Id methodId, ArgList argList) {
        this.objExpr = objExpr;
        this.methodId = methodId;
        this.argList = argList;

        this.children = new ArrayList<>();
        this.children.add(objExpr);
        this.children.add(methodId);
        this.children.add(argList);
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        String object = objExpr.typeName;

        // object is null if there is a type error in the expression representing it. It would be useless reporting
        // new errors here in such a case, as they would likely be solved by solving the error in objExpr
        if (object != null) {
            Method called = classTable.get(object).lookupMethod(methodId);

            //No such method
            if (called == null) {
                errorList.add(new MethodNotDeclaredException(methodId.getName(), methodId.line, methodId.column));
                return;
            }

            // The list of arguments given has more or less elements than the expected number of arguments
            if (called.nbArguments() != argList.size()) {
                errorList.add(new InvalidCallException(called.getName(),
                        line, column, called.line, called.column, "different number of arguments"));
            } else {
                // The number of arguments given is right, now we check if their types correspond to the ones expected
                StringBuilder messageEnd = new StringBuilder("argument(s) ");
                String curArgType;
                boolean invalid = false;
                for (int i = 0; i < argList.size(); i++) {
                    curArgType = argList.get(i).typeName;

                    // curArgType is null if there is a type error in the expression representing it.
                    // It would be useless reporting new errors here in such a case,
                    // as they would likely be solved by solving the previous error
                    if (curArgType != null) {
                        // Types may be valid without being equal. We need to check is the given argument type
                        // is a child of the expected one
                        if (isNotChild(curArgType, called.getArgument(i).getType().getName())) {
                            invalid = true;
                            messageEnd.append((i + 1)).append(", ");
                        }
                    }
                }

                // If there is at least one type error
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

    /**
     * See ASTNode
     */
    @Override
    public void checkScope(ArrayList<SemanticException> errorList) {
        Method called = classTable.get(objExpr.typeName).lookupMethod(methodId);
        if (called == null) {
            errorList.add(new MethodNotDeclaredException(methodId.getName(), line, column));
        }

        super.checkScope(errorList);
    }

    /**
     * See ASTNode, a Call is printed as Call(caller,name,argList)
     */
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
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        // Ids of the arguments, obtained by recursively evaluating the expressions defining their values
        ArrayList<String> argumentsIds = new ArrayList<>();

        // Llvm types of the arguments, obtained through the argList instance variable
        ArrayList<String> argumentsTypes = new ArrayList<>();

        // Called method, containing the return type as well as the (first, in the order of inheritance)
        // class containing its definition
        Method called = classTable.get(objExpr.typeName).lookupMethod(methodId);
        String implementedBy = called.scopeTable.getScopeClassType().getName();
        StringBuilder llvm = new StringBuilder();

        ExprEval curArgEval = objExpr.evalExpr(counter, objExpr.typeName);

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
            llvm.append(LlvmWrappers.llvmCast(intPointer, LLVMKeywords.PTRTOINT, VSOPTypes.getLlvmTypeName(objExpr.typeName, true),
                    LLVMTypes.INT64, curArgEval.llvmId));

            // Then, cast the obtained int into a new pointer (using inttoptr), giving it the new type
            llvm.append(LlvmWrappers.llvmCast(pointerNewType, LLVMKeywords.INTTOPTR, LLVMTypes.INT64,
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

        // Get the called method definition
        ClassItem classItem = this.classTable.get(objExpr.typeName);
        Method method = classItem.getMethod(methodId.getName());

        // Index of first argument = 1 because there is no "self" in arguments of the call in the VSOP code
        int firstArgIndex = 1;

        // Load the arguments of the call
        for (int i = 0; i < argList.size(); i++) {
            curArgEval = argList.get(i).evalExpr(counter, argList.get(i).typeName);

            // Cast the argument to the type defined in the definition of the method
            // (we know that it will be OK as we type-checked the call sooner)
            Formal argument = method.getArgument(firstArgIndex + i);
            curArgEval = castEval(curArgEval, argList.get(i).typeName, argument.getType().getName(), counter);

            llvm.append(curArgEval.llvmCode);
            argumentsIds.add(curArgEval.llvmId);
            argumentsTypes.add(VSOPTypes.getLlvmTypeName(argument.getType().getName(), true));
        }

        // Load the method from the vtable
        ExprEval loadMethod = called.loadMethod(argumentsIds.get(0), counter);
        llvm.append(loadMethod.llvmCode);

        String llvmId;
        if (called.returnType().equals(VSOPTypes.UNIT.getName())) {
            // If the return type is unit (void), we don't save the result anywhere
            llvmId = null;
        } else {
            llvmId = counter.getNextLlvmId();
        }


        // Append code actually calling the function
        llvm.append(LlvmWrappers.call(llvmId, VSOPTypes.getLlvmTypeName(called.returnType(), true),
            loadMethod.llvmId, argumentsIds, argumentsTypes));

        ExprEval callEval = new ExprEval(llvmId, llvm.toString());
        return castEval(callEval, typeName, expectedType, counter);
    }

}