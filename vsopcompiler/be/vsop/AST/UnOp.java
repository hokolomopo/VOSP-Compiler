package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class UnOp extends Expr {
	private String name;
	private Expr expr;

	public UnOp(String name, Expr expr) {
		this.name = name;
		this.expr = expr;

		this.children = new ArrayList<>();
		this.children.add(expr);
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		switch (name) {
			case "isnull":
			case "not":
				BinOp.checkExpr(expr, "bool", errorList);
				typeName = "bool";
				break;
			case "-":
				BinOp.checkExpr(expr, "int32", errorList);
				typeName = "int32";
				break;
		}
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("UnOp(" + name + ",");
		expr.print(tabLevel, false);
		System.out.print(")");
	}
}