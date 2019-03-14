package AST;

public class While extends Expr {
	private Expr condExpr;
	private Expr bodyExpr;

	public While(Expr condExpr, Expr bodyExpr) {
		this.condExpr = condExpr;
		this.bodyExpr = bodyExpr;
	}

	@Override
	public void print(int tabLevel, boolean doTab, boolean first) {
		if (!first){
			System.out.println();
			print(tabLevel, true);
			return;
		}
		print(tabLevel, doTab);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("While(");
		condExpr.print(tabLevel, false, false);
		System.out.print(",");
		System.out.println();
		bodyExpr.print(tabLevel + 1, true, true);
		System.out.print(")");
	}
}