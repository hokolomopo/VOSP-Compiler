package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.InvalidAssignException;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class Assign extends Expr {
    private Id id;
    private Expr expr;

    public Assign(Id id, Expr expr) {
        this.id = id;
        this.expr = expr;

        this.children = new ArrayList<>();
        this.children.add(id);
        this.children.add(expr);
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = id.typeName;
        if (typeName != null && expr.typeName != null) {
            if (isNotChild(expr.typeName, typeName)) {
                errorList.add(new InvalidAssignException("Operands of assign does not have the same type : "
                        + id.getName() + " is of type " + typeName + " and the expression evaluates to " + expr.typeName, line, column));
            }
        }
    }

    @Override
    public void checkScope(ArrayList<SemanticException> errorList) {
        if (id.getName().equals("self")) {
            errorList.add(new InvalidAssignException("Assigning to self is forbidden", line, column));
        }
        super.checkScope(errorList);
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        if (doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("Assign(" + id.getName() + ", ");

        expr.print(tabLevel, false, withTypes);
        System.out.print(")");
    }

    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {

        //Get the formal to assign
        Formal toAssign = scopeTable.lookupVariable(id.getName());

        //Evaluate the expression and store it if needed
        ExprEval eval = expr.evalExpr(counter, toAssign.getType().getName());
        String store = "";
        if (!isUnit()) {
            store = toAssign.llvmStore(eval.llvmId, counter);
        }
        ExprEval assignEval = new ExprEval(eval.llvmId, eval.llvmCode  + store);

        return castEval(assignEval, typeName, expectedType, counter);
    }

}