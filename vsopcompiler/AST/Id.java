package AST;

public class Id extends Expr {
	private String name;

	public Id(String name) {
		this.name = name;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}
}