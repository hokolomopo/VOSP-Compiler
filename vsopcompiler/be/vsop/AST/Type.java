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

	public Type(String name, int line, int column) {
		super(line, column);
		this.name = name;
	}

	@Override
	public void checkScope(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
		this.scopeTable = scopeTable;
		if(Character.isUpperCase(name.charAt(0))) {
			if (scopeTable.lookupClass(name) == null)
				errorList.add(new ClassNotDeclaredException(name, line, column));
		}
		else{
			if (scopeTable.lookupType(name) == null)
				errorList.add(new TypeNotValidException(name, line, column));
		}
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}

	public String getName() {
		return name;
	}
}