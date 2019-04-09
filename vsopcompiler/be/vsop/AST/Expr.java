package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;

import java.util.ArrayList;

public abstract class Expr extends ASTNode{
    protected  String typeName;

    public String getTypeName() {
        return typeName;
    }
}