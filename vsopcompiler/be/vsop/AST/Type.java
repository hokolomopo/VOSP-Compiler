package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotValidException;
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

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		// We do it here so that we can rely on the fact that no object will dereference a not existing type
		this.scopeTable = scopeTable;
		if (Character.isUpperCase(name.charAt(0))) {
			if (!classTable.containsKey(name))
				errorList.add(new ClassNotDeclaredException(name, line, column));
		} else {
			//TODO isn't it useless ? a parser error will be raised before getting here
			if (scopeTable.lookupType(name) == null)
				errorList.add(new TypeNotValidException(name, line, column));
		}
		if (children != null)
			for (ASTNode node : children)
				node.fillScopeTable(scopeTable, errorList);
	}

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

	String getLlvmPtr() {
		return getLlvmPtr(false);
	}

	String getLlvmPtr(boolean pointerOnClass) {
		return getLlvmName(pointerOnClass) + "*";
	}

	boolean isPrimitive() {
		return isPrimitive;
	}

	boolean isPointer() {
		return isPointer;
	}
}