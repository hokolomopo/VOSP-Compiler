package AST;

public class Field {
	private String name;
	private Type type;
	private Expr initExpr;

	public Field(String name, Type type) {
		this.name = name;
		this.type = type;
		this.initExpr = null;
	}

	public Field(String name, Type type, Expr initExpr) {
		this.name = name;
		this.type = type;
		this.initExpr = initExpr;
	}

	public void print() {
		System.out.print("Field(" + name + ",");
		type.print();
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print();
		}
		System.out.print(")");
	}
}