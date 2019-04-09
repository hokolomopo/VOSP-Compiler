package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class ASTNode {

    // Public so that the parser can set them without having to implement dedicated constructors each time
    public int line = 0;
    public int column = 0;

    protected ArrayList<ASTNode> children;
    protected ScopeTable scopeTable;
    protected HashMap<String, ClassItem> classTable;
    protected ClassItem firstParent;

    protected ASTNode(){}

    public void updateClassTable(HashMap<String, ClassItem> classTable, ArrayList<SemanticException> errorList) {
        this.classTable = classTable;
        if(children != null)
            for(ASTNode node : children)
                node.updateClassTable(classTable, errorList);
    }

    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
        this.scopeTable = scopeTable;
        if(children != null)
            for(ASTNode node : children)
                node.fillScopeTable(scopeTable, errorList);
    }

    public void checkTypes(ArrayList<SemanticException> errorList) {
        if(children != null)
            for(ASTNode node : children)
                node.checkTypes(errorList);
    }

    public void checkScope(ArrayList<SemanticException> errorList){
        if(children != null)
            for(ASTNode node : children)
                node.checkScope(errorList);
    }

    public void print(){print(0, false);}
    public abstract void print(int tabLevel, boolean doTab);

    protected String getTab(int tabLevel){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < tabLevel;i++)
            s.append('\t');
        return s.toString();
    }

    public ArrayList<ASTNode> getChildren() {
        return children;
    }
}
