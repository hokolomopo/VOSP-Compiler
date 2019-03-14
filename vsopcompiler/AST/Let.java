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

		System.out.print("Let(" + name + ",");
		type.print(tabLevel, false);
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print(tabLevel, false);
		}
		System.out.print(",");
		System.out.println();
		scopeExpr.print(tabLevel + 1, true);
		System.out.print(")");
	}
}