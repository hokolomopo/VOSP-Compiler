package AST;

import java.util.ArrayList;

public class ExprList extends Expr {
	private ArrayList<Expr> expressions;

	public ExprList(ExprList el, Expr e) {
		this.expressions = el.expressions;
		this.expressions.add(e);
	}

	public ExprList() {
		this.expressions = new ArrayList<Expr>();
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("[");

		if(expressions.size() > 0)
			System.out.println();

		int i;
		if (expressions.size() > 0) {
			for (i = 0; i < expressions.size() - 1; i++) {
				expressions.get(i).print(tabLevel + 1, true);
				System.out.print(",");
				System.out.println();
			}
			expressions.get(i).print(tabLevel + 1, true);
		}

		if(expressions.size() > 0) {
			System.out.println();
			System.out.print(getTab(tabLevel));
		}
		System.out.print("]");
	}
}