package AST;

public class Formal extends ASTNode{
	private String name;
	private Type type;

	public Formal(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print(name + ":");
		type.print(tabLevel, false);
	}
}