package AST;

public class Type extends ASTNode{
	private String name;

	public Type(String name) {
		this.name = name;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}
}