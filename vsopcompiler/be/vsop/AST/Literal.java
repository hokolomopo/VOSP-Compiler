package be.vsop.AST;

public abstract class Literal extends Expr {
	String value;

	Literal(String value) {
		this.value = value;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(value);
	}
}