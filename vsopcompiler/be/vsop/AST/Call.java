package be.vsop.AST;

import be.vsop.exceptions.semantic.InvalidCallException;
import be.vsop.exceptions.semantic.MethodNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;

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
            if (called.nbArguments() != argList.size()) {
                errorList.add(new InvalidCallException(called.getName(),
                        line, column, called.line, called.column, "different number of arguments"));
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
}