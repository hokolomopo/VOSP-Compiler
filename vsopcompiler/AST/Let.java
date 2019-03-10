package AST;

public class Let extends Expr {
	private String name;
	private Type type;
	private Expr scopeExpr;
	private Expr initExpr;

	public Let(String name, Type type, Expr scopeExpr) {
		this.name = name;
		this.type = type;
		this.scopeExpr = scopeExpr;
		this.initExpr = null;
	}

	public Let(String name, Type type, Expr initExpr, Expr scopeExpr) {
		this.name = name;
		this.type = type;
		this.scopeExpr = scopeExpr;
		this.initExpr = initExpr;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("Let(" + name + ",");
		type.print(tabLevel, false);
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print(tabLevel, false);
		}
		System.out.print(",");
		scopeExpr.print(tabLevel, false);
		System.out.print(")");
	}
}