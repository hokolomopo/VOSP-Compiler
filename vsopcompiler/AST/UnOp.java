package AST;

public class UnOp extends Expr {
	private String name;
	private Expr expr;

	public UnOp(String name, Expr expr) {
		this.name = name;
		this.expr = expr;
	}

	public void print() {
		System.out.print("UnOp(" + name + ",");
		expr.print();
		System.out.print(")");
	}
}