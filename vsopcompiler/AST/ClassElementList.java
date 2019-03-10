package AST;

import java.util.ArrayList;

public class ClassElementList {
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

	public void print() {
		System.out.print("[");
		int i;
		if (fields.size() > 0) {
			for (i = 0; i < fields.size() - 1; i++) {
				fields.get(i).print();
				System.out.print(",");
			}
			fields.get(i).print();
		}

		System.out.print("],[");

		if (methods.size() > 0 ) {
			for (i = 0; i < methods.size() - 1; i++) {
				methods.get(i).print();
				System.out.print(",");
			}
			methods.get(i).print();
		}
		System.out.print("]");
	}
}