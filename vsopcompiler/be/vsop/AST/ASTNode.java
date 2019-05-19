package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Abstract base class of all the ASTNodes. It contains the definition of the different passes, their default
 * behaviour being to delegate to children.
 */
public abstract class ASTNode {

    // Public because a private variable + a getter and a setter would be non-sense
    public int line = 0;
    public int column = 0;

    protected ArrayList<ASTNode> children;

    protected ScopeTable scopeTable;
    HashMap<String, ClassItem> classTable;

    static final String endLine = "\n";

    protected ASTNode(){}


    /**
     * Add the classes of the program to the classTable
     *
     * @param classTable the classTable to fill
     * @param errorList a List containing all the semantic errors, could be updated
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
     * @param errorList a List containing all the semantic errors, could be updated
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
     * @param errorList a List containing all the semantic errors, could be updated
     */
    public void checkTypes(ArrayList<SemanticException> errorList) {
        if(children != null)
            for(ASTNode node : children)
                node.checkTypes(errorList);
    }

    /**
     * Check for scope consistency
     *
     * @param errorList a List containing all the semantic errors, could be updated
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
     *
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
     *
     * @return their first common ancestor, or null if one type is primitive
     */
    String firstCommonAncestor(String type1, String type2) {
        // Add all the ancestors of type1 in a HashSet for constant-time access and lookup
        HashSet<String> ancestors1 = new HashSet<>();
        Type curType = classTable.get(type1).getType();
        while (curType != null) {
            ancestors1.add(curType.getName());
            curType = classTable.get(curType.getName()).getParentType();
        }

        // Goes sequentially (from type2 up to Object) through all ancestors of type2 and check if it is an ancestor of type1
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
     *
     * @return false if child is a child of parent, true otherwise
     */
    boolean isNotChild(String child, String parent) {
        if (LanguageSpecs.isPrimitiveType(child) || LanguageSpecs.isPrimitiveType(parent)) {
            // There is no inheritance in primitive types so simply check for equality
            return !child.equals(parent);
        }
        // child is a child of parent if their first common ancestor is parent
        return !firstCommonAncestor(child, parent).equals(parent);
    }

    /**
     * Getter for the children of the node. The exact content of the array depends on the node type.
     * For instance, it will bill the list of classes for a ClassList object.
     *
     * @return the children of the current node
     */
    public ArrayList<ASTNode> getChildren() {
        return children;
    }

    /**
     * Update the argument by adding all the literal strings present in the sub-tree rooted by this node.
     * This will be used to generate the llvm code corresponding to these strings in llvmDeclareStrings
     *
     * @param literalStrings The array of LiteralString to be updated
     */
    public void getStringLiteral(ArrayList<LiteralString> literalStrings){
        if(children != null)
            for(ASTNode child : children)
                child.getStringLiteral(literalStrings);
    }

    /**
     * Setter for the scope table of the current node
     *
     * @param scopeTable the scopeTable to be retained in the node
     */
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

    /**
     * Getter for the class table of the current node (it should be shared by all nodes though)
     *
     * @return the class table
     */
    public HashMap<String, ClassItem> getClassTable() {
        return classTable;
    }

    /**
     * Print the tree, see statement 2 for details
     *
     * @param withTypes if true the expressions are printed as expr : type. If false they are printed simply as expr
     */
    public void print(boolean withTypes){
        print(0, false, withTypes);
    }

    /**
     * Print the tree, with the possibility of tabulating to make the result easier to read (by humans).
     * See statement 2 for details
     *
     * @param tabLevel The current level of tabulation
     * @param doTab if true, print tabulations to make the result easier to read
     * @param withTypes iif true the expressions are printed as expr : type. If false they are printed simply as expr
     */
    public abstract void print(int tabLevel, boolean doTab, boolean withTypes);

    /**
     * Returns a String containing tabulations corresponding to the given tabLevel
     *
     * @param tabLevel the tabulation level
     *
     * @return A String containing tabulations corresponding to the given tabLevel
     */
    protected String getTab(int tabLevel){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < tabLevel;i++)
            s.append('\t');
        return s.toString();
    }
}
