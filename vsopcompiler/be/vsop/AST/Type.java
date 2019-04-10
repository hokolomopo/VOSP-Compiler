package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotValidException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Type extends ASTNode{
	private String name;

	public Type(String name) {
		this.name = name;
	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		// We do it here so that we can rely on the fact that no object will dereference a not existing type
		this.scopeTable = scopeTable;
		if(Character.isUpperCase(name.charAt(0))) {
			if (!classTable.containsKey(name))
				errorList.add(new ClassNotDeclaredException(name, line, column));
		}
		else{
			//TODO isn't it useless ? a parser error will be raised before getting here
			if (scopeTable.lookupType(name) == null)
				errorList.add(new TypeNotValidException(name, line, column));
		}
		if(children != null)
			for(ASTNode node : children)
				node.fillScopeTable(scopeTable, errorList);
	}

	@Override
	public void print(int tabLevel, boolean doTab, boolean withTypes) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}

	public String getName() {
		return name;
	}
}