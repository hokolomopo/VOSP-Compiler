package be.vsop.AST;

public abstract class Expr extends ASTNode{
    public Expr(int line, int column){
        super(line, column);
    }

    public Expr(){}
}