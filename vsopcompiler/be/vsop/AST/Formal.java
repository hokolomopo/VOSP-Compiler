package be.vsop.AST;

import java.util.ArrayList;

public class Formal extends ASTNode{
	private Id id;
	private Type type;

	public Formal(Id id, Type type) {
		this.id = id;
		this.type = type;

		this.children = new ArrayList<>();
		this.children.add(type);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print(id.getName() + ":");
		type.print(tabLevel, false);
	}

	public String getName() {
		return id.getName();
	}

	public Type getType() {
		return type;
	}
}