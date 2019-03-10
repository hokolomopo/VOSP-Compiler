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

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("BinOp(" + name + ",");

		lhs.print(tabLevel, false);
		System.out.print(",");
		rhs.print(tabLevel, false);
		System.out.print(")");
	}
}