package AST;

import java.util.ArrayList;

public class Method {
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

	public void print() {
		System.out.print("Method(" + name + ",");
		formals.print();
		System.out.print(",");
		retType.print();
		System.out.print(",");
		block.print();
		System.out.print(")");
	}
}