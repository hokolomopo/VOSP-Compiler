package be.vsop.AST;

public class Null extends Expr {
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("()");
	}
}