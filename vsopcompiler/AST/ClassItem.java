package AST;

public class ClassItem {
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

	public void print() {
		System.out.print("Class(" + name + "," + parentName + ",");
		cel.print();
		System.out.print(")");
	}
}