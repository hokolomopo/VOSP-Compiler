package be.vsop.AST;

import be.vsop.exceptions.semantic.InvalidOverrideException;
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
		// If two methods are defined in different scopes, it may not yet be added in the tables,
		// thus we only check local scope for now.
		Method previousDeclaration = scopeTable.lookupMethod(getName(), "local scope only");
		if (previousDeclaration != null) {
			errorList.add(new MethodAlreadyDeclaredException(getName(),
					line, column, previousDeclaration.line, previousDeclaration.column));
		} else {
			scopeTable.addMethod(this);
		}
		if(children != null)
			for(ASTNode node : children)
				node.fillScopeTable(this.scopeTable, errorList);
	}

	@Override
	public void checkScope(ArrayList<SemanticException> errorList){
		Method previousDeclaration = scopeTable.getParent().lookupMethod(getName(), "outer scope only");
		if (previousDeclaration != null) {
			if (previousDeclaration.formals.size() != this.formals.size()) {
				errorList.add(new InvalidOverrideException(getName(),
						line, column, previousDeclaration.line, previousDeclaration.column, "different number of arguments"));
			} else {
				StringBuilder messageEnd = new StringBuilder("argument(s) ");
				boolean invalid = false;
				for (int i = 0; i < formals.size(); i++) {
					if (!formals.get(i).sameType(previousDeclaration.formals.get(i))) {
						invalid = true;
						messageEnd.append(i).append(", ");
					}
				}
				if (!retType.getName().equals(previousDeclaration.retType.getName())) {
					invalid = true;
					messageEnd.append("return, ");
				}
				if (invalid) {
					messageEnd.setLength(messageEnd.length() - 2);
					messageEnd.append(" differ(s) in type");
					errorList.add(new InvalidOverrideException(getName(),
							line, column, previousDeclaration.line, previousDeclaration.column, messageEnd.toString()));
				}
			}
		}
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

	int nbArguments() {
		return formals.size();
	}
	
	String returnType() {
		return retType.getName();
	}
}