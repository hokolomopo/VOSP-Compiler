package AST;

public abstract class Literal extends Expr {
	protected String value;

	public Literal(String value) {
		this.value = value;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(value);
	}
}