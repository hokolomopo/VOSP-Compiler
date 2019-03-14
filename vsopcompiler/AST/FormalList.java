package AST;

import java.util.ArrayList;

public class FormalList extends ASTNode{
	private ArrayList<Formal> formals;

	public FormalList(FormalList fl, Formal f) {
		this.formals = fl.formals;
		this.formals.add(f);
	}

	public FormalList() {
		formals = new ArrayList<Formal>();
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("[");


		int i;
		if (formals.size() > 0) {
			for (i = 0; i < formals.size() - 1; i++) {
				formals.get(i).print(tabLevel, false);
				System.out.print(",");
			}
			formals.get(i).print(tabLevel, false);
		}

		System.out.print("]");
	}
}