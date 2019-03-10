package AST;

public class ClassItem extends ASTNode{
	private String name;
	private String parentName;
	private ClassElementList cel;

	public ClassItem(String name, ClassElementList cel) {
		this.name = name;
		this.parentName = "Object";
		this.cel = cel;
	}

	public ClassItem(String name, String parentName, ClassElementList cel) {
		this.name = name;
		this.parentName = parentName;
		this.cel = cel;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Class(" + name + "," + parentName + ",");

		cel.print(tabLevel +1, true);
		System.out.print(")");
	}
}