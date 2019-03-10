package AST;

import java.util.ArrayList;

public class ClassList {
	private ArrayList<ClassItem> classes;

	public ClassList(ClassList cl, ClassItem ci) {
		this.classes = cl.classes;
		this.classes.add(ci);
	}

	public ClassList() {
		this.classes = new ArrayList<ClassItem>();
	}

	public void print() {
		System.out.print("[");
		int i;
		if (classes.size() > 0) {
			for (i = 0; i < classes.size() - 1; i++) {
				classes.get(i).print();
				System.out.print(",");
			}
			classes.get(i).print();
		}
		System.out.print("]");
	}
}