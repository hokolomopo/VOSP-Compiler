package AST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassItem extends ASTNode{
	private String name;
	private String parentName;
	private ClassElementList cel;

	public ClassItem(String name, ClassElementList cel) {
		this(name, "Object", cel);
	}

	public ClassItem(String name, String parentName, ClassElementList cel) {
		this.name = name;
		this.parentName = parentName;
		this.cel = cel;

		this.children = new ArrayList<>();
		this.children.add(cel);
	}

	@Override
	public void updateClassTable(ArrayList<ClassItem> classTable) {
		classTable.add(this);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Class(" + name + "," + parentName + ",");

		System.out.println();
		cel.print(tabLevel +1, true);
		System.out.print(")");
	}

	public String getParentName() {
		return parentName;
	}

	public String getName() {
		return name;
	}
}