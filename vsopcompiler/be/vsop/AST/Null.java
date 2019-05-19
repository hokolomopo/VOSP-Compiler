package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents the only value of the VSOP unit type
 */
public class Null extends Expr {
	/**
	 * See ASTNode, a Null is printed as ()
	 */
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("()");
	}

	/**
	 * See Expr
	 */
	@Override
	public ExprEval evalExpr(InstrCounter counter, String expectedType) {
		return new ExprEval("","");
	}

	/**
	 * See ASTNode
	 */
	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		typeName = VSOPTypes.UNIT.getName();
	}
}