package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class ASTNode {

    // Public so that the parser can set them without having to implement dedicated constructors each time
    public int line = 0;
    public int column = 0;

    protected ArrayList<ASTNode> children;
    ScopeTable scopeTable;
    HashMap<String, ClassItem> classTable;

    protected ASTNode(){}

    String firstCommonAncestor(String type1, String type2) {
        HashSet<String> ancestors1 = new HashSet<>();
        Type curType = classTable.get(type1).getType();
        while (curType != null) {
            ancestors1.add(curType.getName());
            curType = classTable.get(curType.getName()).getParentType();
        }
        curType = classTable.get(type2).getType();
        while (curType != null) {
            if (ancestors1.contains(curType.getName())) {
                return curType.getName();
            }
            curType = classTable.get(curType.getName()).getParentType();
        }
        // Should happen only on primitive types, as Object is the parent and thus a common ancestor of all classes
        return "";
    }

    boolean isNotChild(String child, String parent) {
        if (LanguageSpecs.isPrimitiveType(child)) {
            return !child.equals(parent);
        }
        return !firstCommonAncestor(child, parent).equals(parent);
    }

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
