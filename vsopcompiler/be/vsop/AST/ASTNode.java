package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.LLVMTypes;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class ASTNode {

    // Public because a private variable + a getter and a setter would be non-sense
    public int line = 0;
    public int column = 0;

    protected ArrayList<ASTNode> children;

    protected ScopeTable scopeTable;
    protected HashMap<String, ClassItem> classTable;

    static final String endLine = "\n";

    protected ASTNode(){}


    /**
     * Add the classes of the program to the classTable
     *
     * @param classTable the classTable to fill
     * @param errorList an List in which to input semantic errors
     */
    public void updateClassTable(HashMap<String, ClassItem> classTable, ArrayList<SemanticException> errorList) {
        this.classTable = classTable;
        if(children != null)
            for(ASTNode node : children)
                node.updateClassTable(classTable, errorList);
    }

    /**
     * Add the variables and methods of the program to the scopeTable
     *
     * @param scopeTable the scopeTable to fill
     * @param errorList an List in which to input semantic errors
     */
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
        this.scopeTable = scopeTable;
        if(children != null)
            for(ASTNode node : children)
                node.fillScopeTable(scopeTable, errorList);
    }

    /**
     * Check for type consistency
     *
     * @param errorList an List in which to input semantic errors
     */
    public void checkTypes(ArrayList<SemanticException> errorList) {
        if(children != null)
            for(ASTNode node : children)
                node.checkTypes(errorList);
    }

    /**
     * Check for scope consistency
     *
     * @param errorList an List in which to input semantic errors
     */
    public void checkScope(ArrayList<SemanticException> errorList){
        if(children != null)
            for(ASTNode node : children)
                node.checkScope(errorList);
    }

    /**
     * Get the llvm code of this node
     *
     * @param counter an InstrCounter
     * @return the llvm code
     */
    public String getLlvm(InstrCounter counter){
        StringBuilder builder = new StringBuilder();

        if(children != null)
            for(ASTNode child: children)
                builder.append(child.getLlvm(counter));
        return builder.toString();
    }


    /**
     * Return the first common ancestor between 2 types
     *
     * @param type1 A type
     * @param type2 Another type
     * @return their first common ancestor
     */
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
        // Should happen only if one type is primitive, as Object is the parent and thus a common ancestor of all classes
        return null;
    }

    /**
     * Check if a type is not a child of another
     *
     * @param child the child type
     * @param parent the parent type
     * @return false if the child is a child of the parent, true otherwise
     */
    boolean isNotChild(String child, String parent) {
        if (LanguageSpecs.isPrimitiveType(child) || LanguageSpecs.isPrimitiveType(parent)) {
            return !child.equals(parent);
        }
        return !firstCommonAncestor(child, parent).equals(parent);
    }

    public ArrayList<ASTNode> getChildren() {
        return children;
    }

    /**
     * Get all the string liberals of the program
     *
     * @param literalStrings An array of LiteralString
     */
    public void getStringLiteral(ArrayList<LiteralString> literalStrings){
        if(children != null)
            for(ASTNode child : children)
                child.getStringLiteral(literalStrings);
    }



    public void setScopeTable(ScopeTable scopeTable) {
        this.scopeTable = scopeTable;
    }

    /**
     * Prepare the tree for the llvm code generation. This has to be called before generating llvm code
     */
    public void prepareForLlvm(){
        if(children != null)
            for(ASTNode node : children)
                node.prepareForLlvm();
    }

    public HashMap<String, ClassItem> getClassTable() {
        return classTable;
    }

    /**
     * Print the tree
     *
     * @param withTypes if true the types of the expression will be printed too
     */
    public void print(boolean withTypes){
        print(0, false, withTypes);
    }

    /**
     * Print the tree
     *
     * @param tabLevel tabulation level
     * @param doTab if true, print tabulations before the tree
     * @param withTypes if true the types of the expression will be printed too
     */
    public abstract void print(int tabLevel, boolean doTab, boolean withTypes);

    /**
     * Get the tabulations for the tab level
     */
    protected String getTab(int tabLevel){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < tabLevel;i++)
            s.append('\t');
        return s.toString();
    }
}
