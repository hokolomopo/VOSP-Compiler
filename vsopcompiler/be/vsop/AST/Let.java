package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Let extends Expr {
	private Formal formal;
	private Expr scopeExpr;
	private Expr initExpr;

	public Let(Id id, Type type, Expr scopeExpr) {
		this(id, type, null, scopeExpr);
	}

	public Let(Id id, Type type, Expr initExpr, Expr scopeExpr) {
		this.formal = new Formal(id, type);
		this.scopeExpr = scopeExpr;
		this.initExpr = initExpr;

		this.children = new ArrayList<>();
		this.children.add(scopeExpr);
		this.children.add(initExpr);
		this.children.add(this.formal);
	}

	@Override
	public void checkScope(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
		this.scopeTable = scopeTable;
		formal.checkScope(scopeTable, errorList);

		if(initExpr != null)
			initExpr.checkScope(scopeTable, errorList);

		ScopeTable table = new ScopeTable(scopeTable);
		table.addVariable(formal);

		formal.checkScope(table, errorList);
	}


	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("Let(" + formal.getName() + ",");
		formal.getType().print(tabLevel, false);
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print(tabLevel, false);
		}
		System.out.print(",");
		System.out.println();
		scopeExpr.print(tabLevel + 1, true);
		System.out.print(")");
	}
}