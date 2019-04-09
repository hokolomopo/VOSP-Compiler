package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.LanguageSpecs;

import java.util.ArrayList;

public class BinOp extends Expr {
	private String name;
	private Expr lhs;
	private Expr rhs;

	public BinOp(String name, Expr lhs, Expr rhs) {
		this.name = name;
		this.lhs = lhs;
		this.rhs = rhs;

		this.children = new ArrayList<>();
		this.children.add(lhs);
		this.children.add(rhs);

	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		switch (name) {
			case "and":
				checkExpr(lhs, "bool", errorList);
				checkExpr(rhs, "bool", errorList);
				typeName = "bool";
				break;
			case "=":
				if (LanguageSpecs.isPrimitiveType(lhs.typeName) || LanguageSpecs.isPrimitiveType(rhs.typeName)) {
					checkExpr(lhs, rhs.typeName, errorList);
				}
				typeName = "bool";
				break;
			case "<":
			case "<=":
				checkExpr(lhs, "int32", errorList);
				checkExpr(rhs, "int32", errorList);
				typeName = "bool";
				break;
			case "+":
			case "-":
			case "*":
			case "/":
			case "^":
				checkExpr(lhs, "int32", errorList);
				checkExpr(rhs, "int32", errorList);
				typeName = "int32";
				break;
		}
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("BinOp(" + name + ",");

		lhs.print(tabLevel, false);
		System.out.print(",");
		rhs.print(tabLevel, false);
		System.out.print(")");
	}

	static void checkExpr(Expr expr, String expectedType, ArrayList<SemanticException> errorList) {
		if (expr.typeName != null) {
			if (!expr.typeName.equals(expectedType)) {
				errorList.add(new TypeNotExpectedException(expr, expectedType));
			}
		}
	}
}