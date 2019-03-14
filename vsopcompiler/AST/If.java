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


	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("If(");
		condExpr.print(tabLevel, false);
		System.out.print(",");
		System.out.println();
		thenExpr.print(tabLevel + 1, true);
		if (elseExpr != null) {
			System.out.print(",");
			System.out.println();
			elseExpr.print(tabLevel + 1, true);
		}
		System.out.print(")");
	}
}