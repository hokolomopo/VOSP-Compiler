package AST;

import java.util.ArrayList;

public class ExprList {
	private ArrayList<Expr> expressions;

	public ExprList(ExprList el, Expr e) {
		this.expressions = el.expressions;
		this.expressions.add(e);
	}

	public ExprList() {
		this.expressions = new ArrayList<Expr>();
	}

	public void print() {
		System.out.print("[");
		int i;
		if (expressions.size() > 0) {
			for (i = 0; i < expressions.size() - 1; i++) {
				expressions.get(i).print();
				System.out.print(",");
			}
			expressions.get(i).print();
		}
		System.out.print("]");
	}
}