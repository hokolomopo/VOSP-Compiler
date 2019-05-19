package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents a VSOP Type (whether primitive or user-defined)
 */
public class Type extends ASTNode {
    private String name;

    /**
     * Creates a new Type with the given name
     *
     * @param name the name
     */
    public Type(String name) {
        this.name = name;
    }

    /**
     * See ASTNode
     */
    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
        // We do it here so that we can rely on the fact that no object will dereference a non-existing type
        this.scopeTable = scopeTable;
        if (Character.isUpperCase(name.charAt(0))) {
            if (!classTable.containsKey(name))
                errorList.add(new ClassNotDeclaredException(name, line, column));
        }

        if (children != null)
            for (ASTNode node : children)
                node.fillScopeTable(scopeTable, errorList);
    }

    /**
     * See ASTNode, a type is printed as name
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if (doTab)
            System.out.print(getTab(tabLevel));

        System.out.print(name);
    }

    /**
     * Getter for the name of this type
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a valid llvm String representing this type, convenience method for defaulting pointerOnClass to true
     *
     * @return the llvm name of this type
     */
    public String getLlvmName() {
        return getLlvmName(true);
    }

    /**
     * Returns a valid llvm String representing this type
     *
     * @param pointerOnClass whether to return class types as llvm pointers rather than directly the structure
     *
     * @return the llvm name of this type
     */
    public String getLlvmName(boolean pointerOnClass) {
        return VSOPTypes.getLlvmTypeName(name, pointerOnClass);
    }

    /**
     * Returns a valid llvm String representing a pointer on this type. For classes, it would be a pointer on pointer
     * as we set pointerOnClass to true
     *
     * @return the llvm name of a pointer on this type
     */
    String getLlvmPtr() {
        return getLlvmName() + "*";
    }

    /**
     * Whether this object represent a VSOP unit type or not
     *
     * @return true if this object represent a unit type, false otherwise
     */
    boolean isUnit() {
        return name.equals(VSOPTypes.UNIT.getName());
    }
}