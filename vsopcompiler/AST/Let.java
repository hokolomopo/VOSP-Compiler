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

	public void print() {
		System.out.print("Let(" + name + ",");
		type.print();
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print();
		}
		System.out.print(",");
		scopeExpr.print();
		System.out.print(")");
	}
}