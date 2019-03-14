package AST;

public abstract class ASTNode {
    public void print(){print(0, false);}
    public abstract void print(int tabLevel, boolean doTab);
    public void print(int tabLevel, boolean doTab, boolean first){print(tabLevel, doTab);}

    protected String getTab(int tabLevel){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < tabLevel;i++)
            s.append('\t');
        return s.toString();
    }
}
