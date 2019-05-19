package be.vsop.exceptions.semantic;

import be.vsop.AST.Expr;

/**
 * This class represents a semantic exception which occurs when an expression has a type which is not compatible with
 * the rest of the code
 */
public class TypeNotExpectedException extends SemanticException {
    public TypeNotExpectedException(Expr expr, String expected) {
        super(expr.line, expr.column);

        this.message = "Expression has an invalid type : expected " + expected + ", got " + expr.getTypeName();
    }
}
