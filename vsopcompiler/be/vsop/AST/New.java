package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassNotDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class New extends Expr {
	private Type type;

	public New(Type type) {
		this.type = type;
		children = new ArrayList<>();
		children.add(this.type);
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		typeName = type.getName();
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("New(" + type.getName() + ")");
	}
}