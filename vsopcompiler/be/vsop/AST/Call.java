package be.vsop.AST;

import be.vsop.exceptions.semantic.InvalidCallException;
import be.vsop.exceptions.semantic.MethodNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class Call extends Expr {
    private Expr objExpr;
    private Id methodId;
    private ArgList argList;

    /**
     * Boolean onSelf used to make the difference between self.call and OtherClass.call
     * Useful because methods are then not in the same scope
     */
    private boolean onSelf;

    public Call(Expr objExpr, Id methodId, ArgList argList) {
        this.objExpr = objExpr;
        this.methodId = methodId;
        this.argList = argList;
        this.onSelf = true;

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
                        if (!curArgType.equals(called.getArgument(i).getType().getName())) {
                            invalid = true;
                            messageEnd.append(i).append(", ");
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
        if(onSelf) {
            if (scopeTable.lookupMethod(methodId.getName()) == null) {
                errorList.add(new MethodNotDeclaredException(methodId.getName(), line, column));
            }
        }
        else{
            System.out.println("STILL TODO : Call.java");
            //TODO : need to have the type of the class objExpr to get the methods of this class
        }

        super.checkScope(errorList);
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("Call(");
        objExpr.print(tabLevel, false);
        System.out.print("," + methodId.getName() + ",");
        argList.print(tabLevel, false);
        System.out.print(")");
    }
}