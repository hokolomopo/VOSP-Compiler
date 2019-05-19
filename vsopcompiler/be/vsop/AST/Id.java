package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.VariableNotDeclaredException;

import java.util.ArrayList;

/**
 * This class represents an llvm identifier, whether variable or method identifier
 */
public class Id extends Expr {
	private String name;
	private boolean isVar;

	/**
	 * Creates a new identifier of the given name. By default, this is a variable identifier
	 *
	 * @param name the name of this identifier
	 */
	public Id(String name) {
		this.name = name;
		isVar = true;
	}

	/**
	 * See ASTNode, an Id is printed as name
	 */
	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print(name);
	}

	/**
	 * See ASTNode
	 */
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

	/**
	 * Getter for the name of this identifier
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Tells this identifier that he is now a variable identifier
	 */
	void toVar() {
		isVar = true;
	}

	/**
	 * Tells this identifier that he is now a method identifier
	 */
	public void toMethod() {
		isVar = false;
	}

	/**
	 * See Expr
	 */
	@Override
	public ExprEval evalExpr(InstrCounter counter, String expectedType) {
		// We don't evaluate variables of unit type, they will be replaced by their only possible value
		if (isUnit()) {
			return new ExprEval("","");
		}

		//Get the formal corresponding to this Id and load it
		Formal thisFormal =  this.scopeTable.lookupVariable(name);
		ExprEval eval = thisFormal.llvmLoad(counter);

		return castEval(eval, typeName, expectedType, counter);
	}
}