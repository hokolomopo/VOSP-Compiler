package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

public class Type extends ASTNode {
    private String name;
    private boolean isPrimitive = false;
    private boolean isPointer = false;

    public Type(String name) {
        if (VSOPTypes.getType(name) != null)
            isPrimitive = true;
        this.name = name;
    }

    /**
     * See ASTNode
     */
    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {

        // We do it here so that we can rely on the fact that no object will dereference a not existing type
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

    public String getName() {
        return name;
    }

    public String getLlvmName() {
        return getLlvmName(false);
    }

    public String getLlvmName(boolean pointerOnClass) {
        return VSOPTypes.getLlvmTypeName(name, pointerOnClass);
    }

    String getLlvmPtr(boolean pointerOnClass) {
        return getLlvmName(pointerOnClass) + "*";
    }

    boolean isPointer() {
        return isPointer;
    }

    boolean isUnit() {
        return name.equals(VSOPTypes.UNIT.getName());
    }
}