package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.exceptions.semantic.VariableAlreadyDeclaredException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class Let extends Expr {
	private Formal formal;
	private Expr bodyExpr;
	private Expr initExpr;

	public Let(Id id, Type type, Expr bodyExpr) {
		this(id, type, null, bodyExpr);
	}

	public Let(Id id, Type type, Expr initExpr, Expr bodyExpr) {
		this.scopeTable = new ScopeTable();
		this.formal = new Formal(id, type);
		formal.line = id.line;
		formal.column = id.column;
		this.bodyExpr = bodyExpr;
		this.initExpr = initExpr;

		this.children = new ArrayList<>();
		this.children.add(bodyExpr);
		if (initExpr != null) {
			this.children.add(initExpr);
		}
		this.children.add(this.formal);
	}

	@Override
	public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		this.scopeTable.setParent(scopeTable);
		if (formal.getName().equals("self")) {
			errorList.add(new VariableAlreadyDeclaredException("self", formal.line, formal.column, 0, 0));
		}
		if (children != null) {
			for (ASTNode node : children) {
				node.fillScopeTable(this.scopeTable, errorList);
			}
		}
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		if (initExpr != null && initExpr.typeName != null && isNotChild(initExpr.typeName, formal.getType().getName())) {
			errorList.add(new TypeNotExpectedException(initExpr, formal.getType().getName()));
		}
		typeName = bodyExpr.typeName;
	}

	@Override
	public void checkScope(ArrayList<SemanticException> errorList){
		formal.checkScope(errorList);
		if(initExpr != null)
			initExpr.checkScope(errorList);
		bodyExpr.checkScope(errorList);
	}


	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("Let(" + formal.getName() + ", ");
		formal.getType().print(tabLevel, false, withTypes);
		if (initExpr != null) {
			System.out.print(", ");
			initExpr.print(tabLevel, false, withTypes);
		}
		System.out.print(", ");
		System.out.println();
		bodyExpr.print(tabLevel + 1, true, withTypes);
		System.out.print(")");
	}

	@Override
	public ExprEval evalExpr(InstrCounter counter, String expectedType) {
		String llvm = "";
		if (!formal.getType().isUnit()) {
			formal.setLlvmId(counter.getNextLlvmId());
			llvm += formal.llvmAllocate();
		}

		if(initExpr != null){
			Assign init = new Assign(new Id(formal.getName()), initExpr);
			init.setScopeTable(this.scopeTable);
			init.setTypeName(initExpr.getTypeName());
			llvm += init.getLlvm(counter);
		}

		ExprEval bodyEval = bodyExpr.evalExpr(counter, typeName);
		llvm += bodyEval.llvmCode;

		ExprEval exprEval = new ExprEval(bodyEval.llvmId, llvm);
		return castEval(exprEval, bodyExpr.typeName, expectedType, counter);
	}

}