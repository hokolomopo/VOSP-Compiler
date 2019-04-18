package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Formal extends ASTNode{
	private Id id;
	private Type type;
	private String llvmId;

	public Formal(Id id, Type type) {
		this.id = id;
		this.type = type;

		this.children = new ArrayList<>();
		this.children.add(id);
		this.children.add(type);
	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		this.scopeTable = scopeTable;
		scopeTable.addVariable(this);
		if(children != null)
			for(ASTNode node : children)
				node.fillScopeTable(scopeTable, errorList);
	}

	@Override
	public void print(int tabLevel, boolean doTab, boolean withTypes) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print(id.getName() + ":");
		type.print(tabLevel, false, withTypes);
	}

	public String getName() {
		return id.getName();
	}

	public Type getType() {
		return type;
	}

	@Override
	public String getLlvm() {
		return type.getLlvmName() + " " + "%" + id.getName();
	}
}