package AST;

import java.util.ArrayList;

public class ArgList {
	private ArrayList<Expr> args;

	public ArgList(ArgList al, Expr e) {
		this.args = al.args;
		this.args.add(e);
	}

	public ArgList() {
		this.args = new ArrayList<Expr>();
	}

	public void print() {
		System.out.print("[");
		int i;
		if (args.size() > 0) {
			for (i = 0; i < args.size() - 1; i++) {
				args.get(i).print();
				System.out.print(",");
			}
			args.get(i).print();
		}
		System.out.print("]");
	}
}