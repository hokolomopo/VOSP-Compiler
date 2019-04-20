package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.VariableNotDeclaredException;

import java.util.ArrayList;

public class Id extends Expr {
	private String name;
	private boolean isVar;

	public Id(String name) {
		this.name = name;
		isVar = true;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		// An Id of method will have a null typeName, which is coherent because we never defined a typeName for methods
		if (isVar) {
			Formal var = scopeTable.lookupVariable(name);
			if (var == null) {
				errorList.add(new VariableNotDeclaredException(name, line, column));
			} else {
				typeName = var.getType().getName();
			}
			super.checkTypes(errorList);
		}
	}

	public String getName() {
		return name;
	}

	public void toVar() {
		isVar = true;
	}

	public void toMethod() {
		isVar = false;
	}

	@Override
	public ExprEval evalExpr(InstrCounter counter) {
		Formal thisFormal =  this.scopeTable.lookupVariable(name);

		ExprEval load =  thisFormal.llvmLoad(counter);
		return load;
	}

}