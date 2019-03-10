package AST;

import java.util.ArrayList;

public class Method extends ASTNode {
	private String name;
	private FormalList formals;
	private Type retType;
	private ExprList block;

	public Method(String name, FormalList formals, Type retType, ExprList block) {
		this.name = name;
		this.formals = formals;
		this.retType = retType;
		this.block = block;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("Method(" + name + ",");
		formals.print(tabLevel, false);
		System.out.print(",");
		retType.print(tabLevel, false);
		System.out.print(",");
		System.out.println();
		block.print(tabLevel + 1, true);
		System.out.print(")");
	}
}