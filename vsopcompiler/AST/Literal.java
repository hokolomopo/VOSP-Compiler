package AST;

public class Literal extends Expr {
	private String value;

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