package AST;

public class Assign extends Expr {
	private String name;
	private Expr expr;

	public Assign(String name, Expr expr) {
		this.name = name;
		this.expr = expr;
	}

	public void print() {
		System.out.print("Assign(" + name + ",");
		expr.print();
		System.out.print(")");
	}
}