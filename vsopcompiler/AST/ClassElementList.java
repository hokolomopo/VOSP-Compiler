package AST;

import java.util.ArrayList;

public class ClassElementList extends ASTNode {
	private ArrayList<Field> fields;
	private ArrayList<Method> methods;

	public ClassElementList(ClassElementList cel, Field f) {
		this.fields = cel.fields;
		this.fields.add(f);
		this.methods = cel.methods;
	}

	public ClassElementList(ClassElementList cel, Method m) {
		this.fields = cel.fields;
		this.methods = cel.methods;
		this.methods.add(m);
	}

	public ClassElementList() {
		fields = new ArrayList<Field>();
		methods = new ArrayList<Method>();
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("[");
		int i;
		if (fields.size() > 0) {
			for (i = 0; i < fields.size(); i++) {
				if(i == 0)
					fields.get(i).print(tabLevel, false, true);
				else
					fields.get(i).print(tabLevel, true);

				if(i < fields.size() - 1) {
					System.out.print(",");
					System.out.println();
				}
			}
		}

		System.out.print("],");
		System.out.println();
		System.out.print(getTab(tabLevel) + "[");

		if (methods.size() > 0) {
			for (i = 0; i < methods.size(); i++) {
				if(i == 0)
					methods.get(i).print(tabLevel, false, true);
				else
					methods.get(i).print(tabLevel, true);

				if(i < methods.size() - 1) {
					System.out.print(",");
					System.out.println();
				}
			}
		}


		System.out.print("]");
	}
}