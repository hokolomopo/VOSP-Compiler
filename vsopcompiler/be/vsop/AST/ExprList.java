package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class ExprList extends Expr {
    private ArrayList<Expr> expressions;

    public ExprList(ExprList el, Expr e) {
        if(el == null)
            this.expressions = new ArrayList<>();
        else
            this.expressions = el.expressions;
        this.expressions.add(e);

        this.children = new ArrayList<>(this.expressions);
    }

    public ExprList() {
        this.expressions = new ArrayList<>();
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = expressions.get(expressions.size() - 1).typeName;
    }

    /**
     * See Expr, here we override because we don't want the type to be printed
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        // A block does not print its type, as it is always the same as the type of the last expression
        this.withTypes = withTypes;
        print(tabLevel, doTab);
    }

    /**
     * See ASTNode, an ExprList is printed as expr or [expr1,expr2,...] depending on whether or not there is only
     * 1 expression in the block
     */
    @Override
    public void print(int tabLevel, boolean doTab) {
        // Just print an expression if there is only 1 expression in the list
        if(expressions.size() == 1){
            expressions.get(0).print(tabLevel, doTab, withTypes);
            return;
        }

        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("[");

        int i;
        if (expressions.size() > 0) {
            for (i = 0; i < expressions.size(); i++) {
                if(i == 0)
                    expressions.get(i).print(tabLevel, false, withTypes);
                else
                    expressions.get(i).print(tabLevel, true, withTypes);

                if(i < expressions.size() - 1) {
                    System.out.print(",");
                    System.out.println();
                }
            }
        }

        System.out.print("]");
        if (withTypes) {
            System.out.print(" : " + typeName);
        }

    }

    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        StringBuilder builder = new StringBuilder();

        //Get the llvm code of the expressions
        ExprEval lastEval = null;
        for(int i = 0;i < expressions.size();i++){
            Expr e = expressions.get(i);

            //We don't care of the return types of the expressions except for the last one
            String type = null;
            if(i == (expressions.size() - 1))
                type = this.typeName;

            lastEval = e.evalExpr(counter, type);
            builder.append(lastEval.llvmCode);
        }

        if(lastEval == null)
            return new ExprEval("", "");

        //Cast and return the eval
        ExprEval eval = new ExprEval(lastEval.llvmId, builder.toString(), lastEval.isLiteral());
        return castEval(eval, expressions.get(expressions.size() - 1).typeName, expectedType, counter);
    }

}