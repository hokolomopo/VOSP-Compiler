package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;

/**
 * This class represents a VSOP Literal expression, whether integer, boolean or string
 */
public abstract class Literal extends Expr {
    String value;

    /**
     * Creates a new Literal with the given value
     *
     * @param value the value of the literal (as String)
     */
    Literal(String value) {
        this.value = value;
    }

    /**
     * See ASTNode, a Literal is printed as value except for the strings which needs to convert escape symbols
     */
    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print(value);
    }

    /**
     * See Expr
     */
    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        return new ExprEval(getLlvmValue(), "", true);
    }

    /**
     * Returns the value as a valid llvm String
     *
     * @return the llvm value
     */
    protected abstract String getLlvmValue();
}