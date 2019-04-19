package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class ExprList extends Expr {
	private ArrayList<Expr> expressions;

	public ExprList(ExprList el, Expr e) {
		if(el == null)
			this.expressions = new ArrayList<>();
		else
			this.expressions = el.expressions;
		this.expressions.add(e);

		this.children = new ArrayList<>(this.expressions);
	}

	public ExprList() {
		this.expressions = new ArrayList<>();
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		typeName = expressions.get(expressions.size() - 1).typeName;
	}

	@Override
	public void print(int tabLevel, boolean doTab, boolean withTypes) {
		// A block does not print its type, as it is always the same as the type of the last expression
		this.withTypes = withTypes;
		print(tabLevel, doTab);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
	    // Just print an expression if there is only 1 expression in the list
	    if(expressions.size() == 1){
	        expressions.get(0).print(tabLevel, doTab, withTypes);
	        return;
        }

		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("[");

		int i;
		if (expressions.size() > 0) {
			for (i = 0; i < expressions.size(); i++) {
				if(i == 0)
					expressions.get(i).print(tabLevel, false, withTypes);
				else
					expressions.get(i).print(tabLevel, true, withTypes);

				if(i < expressions.size() - 1) {
					System.out.print(",");
					System.out.println();
				}
			}
		}

		System.out.print("]");
		if (withTypes) {
			System.out.print(" : " + typeName);
		}

	}

	@Override
	public ExprEval evalExpr(InstrCounter counter) {
		StringBuilder builder = new StringBuilder();



		ExprEval lastEval = null;
		for(Expr e : expressions){
			lastEval = e.evalExpr(counter);
			builder.append(lastEval.llvmCode);
		}

		if(lastEval == null)
			return new ExprEval("", "");
		return new ExprEval(lastEval.llvmId, builder.toString(), lastEval.isLiteral());
	}

}