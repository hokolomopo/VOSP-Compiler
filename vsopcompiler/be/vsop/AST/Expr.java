package be.vsop.AST;

public abstract class Expr extends ASTNode{
    protected String typeName;
    protected boolean withTypes;

    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        this.withTypes = withTypes;
        print(tabLevel, doTab);
        if (withTypes) {
            System.out.print(" : " + typeName);
        }
    }

    public abstract void print(int tabLevel, boolean doTab);

    public String getTypeName() {
        return typeName;
    }
}