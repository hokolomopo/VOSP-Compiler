package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.VariableAlreadyDeclaredException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Field extends ASTNode {
	private Id id;
	private Type type;
	private Expr initExpr;

	public Field(Id id, Type type) {
		this(id, type, null);
	}

	public Field(Id id, Type type, Expr initExpr) {
		this.id = id;
		this.type = type;
		this.initExpr = initExpr;

		this.children = new ArrayList<>();
		this.children.add(type);

		if(initExpr != null)
			this.children.add(initExpr);

	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		this.scopeTable = scopeTable;
		Formal previousDeclaration = scopeTable.lookupVariable(id.getName(), true);
		if(previousDeclaration != null) {
			errorList.add(new VariableAlreadyDeclaredException(id.getName(), line, column,
					previousDeclaration.line, previousDeclaration.column));
		} else {
			Formal newDeclaration = new Formal(id, type);
			newDeclaration.line = line;
			newDeclaration.column = column;
			scopeTable.addVariable(newDeclaration);
		}
		if(children != null)
			for(ASTNode node : children)
				node.fillScopeTable(scopeTable, errorList);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Field(" + id.getName() + ",");

		type.print(tabLevel, false);
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print(tabLevel, false);
		}
		System.out.print(")");
	}
}