package AST;

import java.util.ArrayList;

public class ClassList extends ASTNode {
	private ArrayList<ClassItem> classes;

	public ClassList(ClassList cl, ClassItem ci) {
		this.classes = cl.classes;
		this.classes.add(ci);
	}

	public ClassList(ClassItem ci) {
		this.classes = new ArrayList<ClassItem>();
		this.classes.add(ci);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("[");
		int i;
		if (classes.size() > 0) {
			for (i = 0; i < classes.size(); i++) {
				if(i == 0)
					classes.get(i).print(tabLevel, false);
				else
					classes.get(i).print(tabLevel, true);
				if(i < classes.size() - 1) {
					System.out.print(",");
					System.out.println();
				}
			}
		}
		System.out.print("]");
	}
}