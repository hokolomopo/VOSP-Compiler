package AST;

import java.util.ArrayList;

public class ClassList extends ASTNode {
	private ArrayList<ClassItem> classes;

	public ClassList(ClassList cl, ClassItem ci) {
		this.classes = cl.classes;
		this.classes.add(ci);
	}

	public ClassList() {
		this.classes = new ArrayList<ClassItem>();
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("[");
		int i;
		if (classes.size() > 0) {
			for (i = 0; i < classes.size() - 1; i++) {
				System.out.println();
				classes.get(i).print(tabLevel + 1, true);
				System.out.print(",");
			}
			System.out.println();
			classes.get(i).print(tabLevel + 1, true);
		}
		System.out.print("]");
	}
}