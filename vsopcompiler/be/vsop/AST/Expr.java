package be.vsop.AST;

public abstract class Expr extends ASTNode{
    protected  String typeName;

    public String getTypeName() {
        return typeName;
    }
}