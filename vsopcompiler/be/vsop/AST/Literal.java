package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;

public abstract class Literal extends Expr {
	String value;

	Literal(String value) {
		this.value = value;
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(value);
	}

	@Override
	public ExprEval evalExpr(InstrCounter counter) {
		return new ExprEval(getLlvmValue(), "", true);
	}

	protected abstract String getLlvmValue();

}