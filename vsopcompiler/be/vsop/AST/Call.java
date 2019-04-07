package be.vsop.AST;

import be.vsop.exceptions.semantic.MethodNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

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
        this(objExpr, methodId, argList, false);
    }

    public Call(Expr objExpr, Id methodId, ArgList argList, boolean onSelf) {
        super(methodId.getLine(), methodId.getColumn());
        this.objExpr = objExpr;
        this.methodId = methodId;
        this.argList = argList;
        this.onSelf = onSelf;

        this.children = new ArrayList<>();
        this.children.add(objExpr);
        this.children.add(argList);
    }


    public void checkScope(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
        if(onSelf) {
            if (scopeTable.lookupMethod(methodId.getName()) == null)
                errorList.add(new MethodNotDeclaredException(methodId.getName(), line, column));
        }
        else{
            System.out.println("STILL TODO : Call.java");
            //TODO : need to have the type of the class objExpr to get the methods of this class
        }

        super.checkScope(scopeTable, errorList);
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