package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.VSOPTypes;
import javafx.util.Pair;

import java.util.ArrayList;

public class BinOp extends Expr {
	private enum BinOpTypes{
		AND("and"),
		EQUAL("="),
		LOWER("<"),
		LOWEREQ("<="),
		PLUS("+"),
		MINUS("-"),
		TIMES("*"),
		DIVIDED("/"),
		POW("^");

		private String name;

		BinOpTypes(String name) {
			this.name = name;
		}

		public static BinOpTypes getType(String type){
			for(BinOpTypes t : BinOpTypes.values())
				if(t.name.equals(type))
					return t;
			return null;
		}

		public String getName() {
			return name;
		}
	}

	private BinOpTypes type;
	private Expr lhs;
	private Expr rhs;

	public BinOp(String name, Expr lhs, Expr rhs) {
		this.type = BinOpTypes.getType(name);
		this.lhs = lhs;
		this.rhs = rhs;

		this.children = new ArrayList<>();
		this.children.add(lhs);
		this.children.add(rhs);

	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		switch (type) {
			case AND:
				checkExpr(lhs, "bool", errorList);
				checkExpr(rhs, "bool", errorList);
				typeName = "bool";
				break;
			case EQUAL:
				if (LanguageSpecs.isPrimitiveType(lhs.typeName) || LanguageSpecs.isPrimitiveType(rhs.typeName)) {
					checkExpr(lhs, rhs.typeName, errorList);
				}
				typeName = "bool";
				break;
			case LOWER:
			case LOWEREQ:
				checkExpr(lhs, "int32", errorList);
				checkExpr(rhs, "int32", errorList);
				typeName = "bool";
				break;
			case PLUS:
			case MINUS:
			case TIMES:
			case DIVIDED:
			case POW:
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
		System.out.print("BinOp(" + type.getName() + ", ");

		lhs.print(tabLevel, false, withTypes);
		System.out.print(", ");
		rhs.print(tabLevel, false, withTypes);
		System.out.print(")");
	}

	static void checkExpr(Expr expr, String expectedType, ArrayList<SemanticException> errorList) {
		if (expr.typeName != null) {
			if (!expr.typeName.equals(expectedType)) {
				errorList.add(new TypeNotExpectedException(expr, expectedType));
			}
		}
	}

	@Override
	public ExprEval evalExpr(InstrCounter counter) {

		//Evaluate left expression
		ExprEval leftPair = lhs.evalExpr(counter);

		//Evaluate right expression
		ExprEval rightPair = rhs.evalExpr(counter);

		String id = counter.getNextLlvmId();

		String llvm =  leftPair.llvmCode + rightPair.llvmCode +
				id + " = " + evaluateExpr(leftPair.llvmId, rightPair.llvmId) + endLine;

		return new ExprEval(id, llvm);
	}

	private String evaluateExpr(String leftId, String rightId){
		switch (type){
			case AND:
			case EQUAL:
			case LOWER:
			case LOWEREQ:
				return "TODO binop";//TODO
			case PLUS:
				return "add " + VSOPTypes.INT32.getLlvmName() + " " +  leftId + ", " + rightId;
			case MINUS:
				return "sub " + VSOPTypes.INT32.getLlvmName() + " " + leftId + ", " + rightId;
			case TIMES:
				return "mul " + VSOPTypes.INT32.getLlvmName() + " " + leftId + ", " + rightId;
			case DIVIDED:
				return "udiv " + VSOPTypes.INT32.getLlvmName() + " " + leftId + ", " + rightId;
			case POW:
				return "TODO pow";//TODO
		}

		return null;
	}

}