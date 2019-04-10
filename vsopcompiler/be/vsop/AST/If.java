package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.LanguageSpecs;

import java.util.ArrayList;

public class If extends Expr {
	private Expr condExpr;
	private Expr thenExpr;
	private Expr elseExpr;

	public If(Expr condExpr, Expr thenExpr) {
		this(condExpr, thenExpr, null);
	}

	public If(Expr condExpr, Expr thenExpr, Expr elseExpr) {
		this.condExpr = condExpr;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;

		this.children = new ArrayList<>();
		this.children.add(condExpr);
		this.children.add(thenExpr);

		if(elseExpr != null)
			this.children.add(elseExpr);
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		String condType = condExpr.typeName;
		String thenType = thenExpr.typeName;
		String elseType;
		if (elseExpr == null) {
			elseType = "unit";
		} else {
			elseType = elseExpr.typeName;
		}

		if (condType != null && !condType.equals("bool")) {
			errorList.add(new TypeNotExpectedException(condExpr, "bool"));
		}

		if (thenType != null && elseType != null) {
			if (thenType.equals("unit") || elseType.equals("unit")) {
				typeName = "unit";
			} else if (LanguageSpecs.isPrimitiveType(thenType) || LanguageSpecs.isPrimitiveType(elseType)) {
				if (!thenType.equals(elseType)) {
					errorList.add(new TypeNotExpectedException(thenExpr, elseExpr.typeName));
				} else {
					typeName = thenType;
				}
			} else {
				typeName = firstCommonAncestor(thenType, elseType);
			}
		}
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("If(");
		condExpr.print(tabLevel, false, withTypes);
		System.out.print(",");
		System.out.println();
		thenExpr.print(tabLevel + 1, true, withTypes);
		if (elseExpr != null) {
			System.out.print(",");
			System.out.println();
			elseExpr.print(tabLevel + 1, true, withTypes);
		}
		System.out.print(")");
	}
}