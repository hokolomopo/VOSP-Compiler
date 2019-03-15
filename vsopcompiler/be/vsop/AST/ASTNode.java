package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public abstract class ASTNode {

    //TODO put column/line in constructor to report semantic error #FUN (or create constructor from cup.Symbol)
    protected int line = 0;
    protected int column = 0;

    protected ArrayList<ASTNode> children;
    protected ScopeTable scopeTable;

    protected ASTNode(){}

    protected ASTNode(int line, int column){
        this.line = line;
        this.column = column;
    }

    public void updateClassTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
        this.scopeTable = scopeTable;
        if(children != null)
            for(ASTNode node : children)
                node.updateClassTable(scopeTable, errorList);
    }

    public void updateClassItems(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
        this.scopeTable = scopeTable;
        if(children != null)
            for(ASTNode node : children)
                node.updateClassItems(scopeTable, errorList);
    }

    public void checkScope(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
        this.scopeTable = scopeTable;
        if(children != null)
            for(ASTNode node : children)
                node.checkScope(scopeTable, errorList);
    }


    public void print(){print(0, false);}
    public abstract void print(int tabLevel, boolean doTab);

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
