package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents a VSOP integer literal
 */
public class LiteralInteger extends Literal {
    /**
     * Creates a new LiteralInteger with the given value
     *
     * @param value the value of the literal (as String)
     */
    public LiteralInteger(String value) {
        super(value);
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = VSOPTypes.INT32.getName();
    }

    /**
     * Returns the value as a valid llvm String
     *
     * @return the llvm value
     */
    @Override
    protected String getLlvmValue() {
        return value;
    }

}
