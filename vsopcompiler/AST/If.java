package AST;

public class If extends Expr {
	private Expr condExpr;
	private Expr thenExpr;
	private Expr elseExpr;

	public If(Expr condExpr, Expr thenExpr) {
		this.condExpr = condExpr;
		this.thenExpr = thenExpr;
		this.elseExpr = null;
	}

	public If(Expr condExpr, Expr thenExpr, Expr elseExpr) {
		this.condExpr = condExpr;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}

	public void print() {
		System.out.print("If(");
		condExpr.print();
		System.out.print(",");
		thenExpr.print();
		if (elseExpr != null) {
			System.out.print(",");
			elseExpr.print();
		}
		System.out.print(")");
	}
}