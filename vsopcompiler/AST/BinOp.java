package AST;

public class BinOp extends Expr {
	private String name;
	private Expr lhs;
	private Expr rhs;

	public BinOp(String name, Expr lhs, Expr rhs) {
		this.name = name;
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public void print() {
		System.out.print("BinOp(" + name + ",");
		lhs.print();
		System.out.print(",");
		rhs.print();
		System.out.print(")");
	}
}