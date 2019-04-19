package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class LiteralBoolean extends Literal {
    public LiteralBoolean(String value) {
        super(value);
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = "bool";
    }

    @Override
    protected String getLlvmValue() {
        return value;
    }

    @Override
    protected boolean isLlvmLiteral() {
        return true;
    }
}
