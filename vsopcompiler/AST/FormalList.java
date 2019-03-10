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

		if(formals.size() > 0)
			System.out.println();

		int i;
		if (formals.size() > 0) {
			for (i = 0; i < formals.size() - 1; i++) {
				formals.get(i).print(tabLevel + 1, true);
				System.out.print(",");
				System.out.println();
			}
			formals.get(i).print(tabLevel + 1, true);
		}

		if(formals.size() > 0){
			System.out.println();
			System.out.print(getTab(tabLevel));
		}
		System.out.print("]");
	}
}