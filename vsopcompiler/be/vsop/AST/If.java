package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.codegenutil.LlvmVar;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;
import java.util.HashMap;

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


		if (condType != null && !condType.equals("bool")) {
			errorList.add(new TypeNotExpectedException(condExpr, "bool"));
		}

		if (elseExpr == null) {
			elseType = "unit";
		} else {
			elseType = elseExpr.typeName;
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

	@Override
	public ExprEval evalExpr(InstrCounter counter, String expectedType) {
		HashMap<String, String> labels = counter.getNextCondLabels();

		String llvm = "";
		String retId = null;

		String type = typeName;
		if(type.equals(VSOPTypes.UNIT.getName()))//TODO vu les codes du prof, j'ai l'impression que machin = () c'est toujours vrai ????
			type = null;


		//Allocate return value
		Formal retFormal = null;
		if(!typeName.equals(VSOPTypes.UNIT.getName())){
			retId = "%" + labels.get(InstrCounter.COND_ID);

			Type retType = new Type(this.typeName);
			retFormal = new Formal(new Id(labels.get(InstrCounter.COND_ID)), retType);
			llvm += retFormal.llvmAllocate();
		}

		//Evaluate the condition
		ExprEval condEval = condExpr.evalExpr(counter, VSOPTypes.BOOL.getName());
		llvm += condEval.llvmCode;

		//Else label == End of condition label if there is no else branch
		String elseLabel = labels.get(InstrCounter.COND_ELSE_LABEL);
		if(elseExpr == null)
			elseLabel = labels.get(InstrCounter.COND_END_LABEL);

		//Branching
		llvm += "br i1 " + condEval.llvmId + ", label %" + labels.get(InstrCounter.COND_IF_LABEL) + ", label %" + elseLabel + endLine + endLine;

		//Then condition
		ExprEval thenEval = thenExpr.evalExpr(counter, type);
		llvm += labels.get(InstrCounter.COND_IF_LABEL) + ":" + endLine +
				thenEval.llvmCode;
		if(retFormal != null)
			llvm += retFormal.llvmStore(thenEval.llvmId, counter);
		llvm += "br label %" + labels.get(InstrCounter.COND_END_LABEL) + endLine + endLine;

		//Else condition
		if(elseExpr != null){
			ExprEval elseEval = elseExpr.evalExpr(counter, type);
			llvm += labels.get(InstrCounter.COND_ELSE_LABEL) + ":" + endLine +
					elseEval.llvmCode;
			if(retFormal != null)
				llvm += retFormal.llvmStore(elseEval.llvmId, counter);
			llvm += "br label %" + labels.get(InstrCounter.COND_END_LABEL) + endLine + endLine;
		}

		//End of condition
		llvm += labels.get(InstrCounter.COND_END_LABEL) + ":" + endLine;

		//Store result of the condition if needed
		if(retFormal != null){
			ExprEval ret = retFormal.llvmLoad(counter);
			llvm += ret.llvmCode;
			retId = ret.llvmId;
		}

		ExprEval finalEval = new ExprEval(retId, llvm);


		return castEval(finalEval, thenExpr.typeName, expectedType, counter);//TODO unt ruc qui march vraiment
	}

}