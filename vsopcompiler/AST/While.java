package AST;

import java.util.ArrayList;

public class While extends Expr {
	private Expr condExpr;
	private Expr bodyExpr;

	public While(Expr condExpr, Expr bodyExpr) {
		this.condExpr = condExpr;
		this.bodyExpr = bodyExpr;

		this.children = new ArrayList<>();
		this.children.add(condExpr);
		this.children.add(bodyExpr);
	}


	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("While(");
		condExpr.print(tabLevel, false);
		System.out.print(",");
		System.out.println();
		bodyExpr.print(tabLevel + 1, true);
		System.out.print(")");
	}
}