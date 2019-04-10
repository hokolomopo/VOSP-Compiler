package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
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
		this.children.add(id);
		this.children.add(type);

		if(initExpr != null)
			this.children.add(initExpr);

	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		this.scopeTable = scopeTable;
		// We need to check here if the variable is already defined in local scope, even if we will
		// later do the same check with the outer scope, because we can't do a lookup in local scope after
		// having added the Field : the Field would find itself.
		Formal previousDeclaration = scopeTable.lookupVariable(id.getName(), "local scope only");
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
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		if (initExpr != null && initExpr.typeName != null && isNotChild(initExpr.typeName, type.getName())) {
			errorList.add(new TypeNotExpectedException(initExpr, type.getName()));
		}
	}

	@Override
	public void checkScope(ArrayList<SemanticException> errorList) {
		Formal previousDeclaration = scopeTable.lookupVariable(id.getName(), "outer scope only");
		if (previousDeclaration != null) {
			errorList.add(new VariableAlreadyDeclaredException(id.getName(), line, column,
					previousDeclaration.line, previousDeclaration.column));
		}
		super.checkScope(errorList);
	}

	@Override
	public void print(int tabLevel, boolean doTab, boolean withTypes) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Field(" + id.getName() + ",");

		type.print(tabLevel, false, withTypes);
		if (initExpr != null) {
			System.out.print(",");
			initExpr.print(tabLevel, false, withTypes);
		}
		System.out.print(")");
	}
}