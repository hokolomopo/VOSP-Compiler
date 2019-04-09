package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class LiteralInteger extends Literal {
    public LiteralInteger(String value) {
        super(value);
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = "int32";
    }
}
