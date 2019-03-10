package AST;

public class New extends Expr {
	private String typeName;

	public New(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {

		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("New(" + typeName + ")");
	}
}