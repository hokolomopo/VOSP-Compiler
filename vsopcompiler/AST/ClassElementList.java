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

		System.out.println();
		System.out.print(getTab(tabLevel));

		System.out.print("[");
		int i;
		if (fields.size() > 0) {
			for (i = 0; i < fields.size() - 1; i++) {
				System.out.println();
				fields.get(i).print(tabLevel + 1, true);
				System.out.print(",");
			}
			System.out.println();
			fields.get(i).print(tabLevel + 1, true);
		}

		if (fields.size() > 0){
			System.out.println();
			System.out.print(getTab(tabLevel));
		}

		System.out.print("],");
		System.out.println();
		System.out.print(getTab(tabLevel) + "[");

		if (methods.size() > 0) {
			for (i = 0; i < methods.size() - 1; i++) {
				System.out.println();
				methods.get(i).print(tabLevel + 1, true);
				System.out.print(",");
			}
			System.out.println();
			methods.get(i).print(tabLevel + 1, true);
		}

		if (methods.size() > 0){
			System.out.println();
			System.out.print(getTab(tabLevel));
		}

		System.out.print("]");
	}
}