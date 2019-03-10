package AST;

public class While extends Expr {
	private Expr condExpr;
	private Expr bodyExpr;

	public While(Expr condExpr, Expr bodyExpr) {
		this.condExpr = condExpr;
		this.bodyExpr = bodyExpr;
	}

	public void print() {
		System.out.print("While(");
		condExpr.print();
		System.out.print(",");
		bodyExpr.print();
		System.out.print(")");
	}
}