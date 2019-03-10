package AST;

public class UnOp extends Expr {
	private String name;
	private Expr expr;

	public UnOp(String name, Expr expr) {
		this.name = name;
		this.expr = expr;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("UnOp(" + name + ",");
		expr.print(tabLevel, false);
		System.out.print(")");
	}
}