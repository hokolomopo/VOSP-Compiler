package AST;

import java.util.ArrayList;

public abstract class ASTNode {

    //TODO put column/line in constructor to report syntax error
    protected int line = 0;
    protected int column = 0;

    protected ArrayList<ASTNode> children;

    public void print(){print(0, false);}
    public abstract void print(int tabLevel, boolean doTab);

    public void updateClassTable(ArrayList<ClassItem> classTable){
        if(children != null)
            for(ASTNode node : children)
                node.updateClassTable(classTable);
    }

    protected String getTab(int tabLevel){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < tabLevel;i++)
            s.append('\t');
        return s.toString();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public ArrayList<ASTNode> getChildren() {
        return children;
    }
}
