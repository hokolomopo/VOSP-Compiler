package be.vsop.AST;

import java.util.ArrayList;

public class Assign extends Expr {
	private Id id;
	private Expr expr;

	public Assign(Id id, Expr expr) {
		this.id = id;
		this.expr = expr;

		this.children = new ArrayList<>();
		this.children.add(expr);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Assign(" + id.getName() + ",");

		expr.print(tabLevel, false);
		System.out.print(")");
	}
}