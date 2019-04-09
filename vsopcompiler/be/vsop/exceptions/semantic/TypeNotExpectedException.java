package be.vsop.exceptions.semantic;

import be.vsop.AST.Expr;

public class TypeNotExpectedException extends SemanticException {
    public TypeNotExpectedException(Expr expr, String expected) {
        super(expr.line, expr.column);

        this.message = "Expression has an invalid type : expected " + expected + ", got " + expr.getTypeName();
    }
}
