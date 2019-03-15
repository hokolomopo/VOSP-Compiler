package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.VariableNotDeclaredException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Id extends Expr {
	private String name;

	public Id(String name) {
		this.name = name;
	}

	public Id(String name, int line, int column) {
		super(line, column);
		this.name = name;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}

	@Override
	public void checkScope(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
		if(scopeTable.lookupVariable(name) == null)
			errorList.add(new VariableNotDeclaredException(name, line, column));
	}


	public String getName() {
		return name;
	}
}