package AST;

public class Field extends ASTNode {
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

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Field(" + name + ",");

		type.print(tabLevel, false);
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print(tabLevel, false);
		}
		System.out.print(")");
	}
}