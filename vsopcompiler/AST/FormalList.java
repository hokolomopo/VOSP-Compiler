package AST;

import java.util.ArrayList;

public class FormalList {
	private ArrayList<Formal> formals;

	public FormalList(FormalList fl, Formal f) {
		this.formals = fl.formals;
		this.formals.add(f);
	}

	public FormalList() {
		formals = new ArrayList<Formal>();
	}

	public void print() {
		System.out.print("[");
		int i;
		if (formals.size() > 0) {
			for (i = 0; i < formals.size() - 1; i++) {
				formals.get(i).print();
				System.out.print(",");
			}
			formals.get(i).print();
		}
		System.out.print("]");
	}
}