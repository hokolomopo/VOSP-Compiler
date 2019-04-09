package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class Null extends Expr {
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("()");
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		typeName = "unit";
	}
}