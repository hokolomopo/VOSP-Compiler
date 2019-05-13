package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

public class UnOp extends Expr {
	private enum UnOpTypes{
		NOT("not"),
		UMINUS("-"),
		ISNULL("isnull");

		private String name;

		UnOpTypes(String name) {
			this.name = name;
		}

		public static UnOpTypes getType(String type){
			for(UnOpTypes t : UnOpTypes.values())
				if(t.name.equals(type))
					return t;
			return null;
		}

		public String getName() {
			return name;
		}
	}

	private UnOpTypes opType;
	private Expr expr;

	public UnOp(String name, Expr expr) {
		this.opType = UnOpTypes.getType(name);
		this.expr = expr;

		this.children = new ArrayList<>();
		this.children.add(expr);
	}

	@Override
	public void checkTypes(ArrayList<SemanticException> errorList) {
		super.checkTypes(errorList);
		switch (opType) {
			case ISNULL:
				BinOp.checkExpr(expr, "Object", errorList);
				typeName = "bool";
				break;
			case NOT:
				BinOp.checkExpr(expr, "bool", errorList);
				typeName = "bool";
				break;
			case UMINUS:
				BinOp.checkExpr(expr, "int32", errorList);
				typeName = "int32";
				break;
		}
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("UnOp(" + opType.name + ", ");
		expr.print(tabLevel, false, withTypes);
		System.out.print(")");
	}

	@Override
	public ExprEval evalExpr(InstrCounter counter) {
		//Evaluate expression
		ExprEval eval = expr.evalExpr(counter);

		//Get an id
		String id = counter.getNextLlvmId();

		//Generate code
		String llvm = eval.llvmCode + id + " = " + evaluateExpr(eval.llvmId) + endLine;


		return new ExprEval(id, llvm);
	}

	private String evaluateExpr(String exprId){
		switch (opType){
			case NOT:
				return "TODO NOT UnOP";//TODO
			case UMINUS:
				return LLVMKeywords.SUB.getLlvmName() + " " + VSOPTypes.INT32.getLlvmName() + " " + 0 + ", " + exprId;
			case ISNULL:
				return "TODO ISNULL UnOP";//TODO
		}

		return null;
	}

}