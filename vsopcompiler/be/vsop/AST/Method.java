package be.vsop.AST;

import be.vsop.exceptions.semantic.MethodAlreadyDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Method extends ASTNode {
	private Id id;
	private FormalList formals;
	private Type retType;
	private ExprList block;

	public Method(Id id, FormalList formals, Type retType, ExprList block) {
		this.scopeTable = new ScopeTable();
		this.id = id;
		this.formals = formals;
		this.retType = retType;
		this.block = block;

		this.children = new ArrayList<>();
		this.children.add(block);
		this.children.add(formals);
		this.children.add(retType);
	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		this.scopeTable.setParent(scopeTable);
		if (scopeTable.lookupMethod(id.getName()) != null) {
			errorList.add(new MethodAlreadyDeclaredException(id.getName(), line, column));
		} else {
			scopeTable.addMethod(this);
		}
		if(children != null)
			for(ASTNode node : children)
				node.fillScopeTable(this.scopeTable, errorList);
	}

	@Override
	public void checkScope(ArrayList<SemanticException> errorList){
		//TODO can a formal argument be named self ? + maybe need a more general implementation
		formals.checkAllDifferent(errorList);
		super.checkScope(errorList);
	}


	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("Method(" + id.getName() + ",");
		formals.print(tabLevel, false);
		System.out.print(",");
		retType.print(tabLevel, false);
		System.out.print(",");
		System.out.println();
		block.print(tabLevel + 1, true);
		System.out.print(")");
	}

	public String getName() {
		return id.getName();
	}
}