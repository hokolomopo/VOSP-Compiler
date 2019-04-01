package be.vsop.AST;

import java.util.ArrayList;
import java.util.List;

public class ClassElementList extends ASTNode {
	private ArrayList<Field> fields;
	private ArrayList<Method> methods;

	public ClassElementList(ClassElementList cel, Field f) {
		this.fields = cel.fields;
		this.fields.add(f);
		this.methods = cel.methods;

		this.children = new ArrayList<>();
		this.children.addAll(fields);
		this.children.addAll(methods);
	}

	public ClassElementList(ClassElementList cel, Method m) {
		this.fields = cel.fields;
		this.methods = cel.methods;
		this.methods.add(m);

		this.children = new ArrayList<>();
		this.children.addAll(fields);
		this.children.addAll(methods);
	}

	public ClassElementList(List<Field> fields, List<Method> methods) {
		this.fields = new ArrayList<>(fields);
		this.methods = new ArrayList<>(methods);

		this.children = new ArrayList<>();
		this.children.addAll(fields);
		this.children.addAll(methods);
	}

	public ClassElementList() {
		fields = new ArrayList<>();
		methods = new ArrayList<>();
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
					fields.get(i).print(tabLevel, false);
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
					methods.get(i).print(tabLevel, false);
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