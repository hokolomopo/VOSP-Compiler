package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a VSOP If expression
 */
public class If extends Expr {
    private Expr condExpr;
    private Expr thenExpr;
    private Expr elseExpr;

    /**
     * Creates a new If from the given condition and then expression, without else expression
     * @param condExpr the condition
     * @param thenExpr the expression to evaluate if the condition is false
     */
    public If(Expr condExpr, Expr thenExpr) {
        this(condExpr, thenExpr, null);
    }

    /**
     * Creates a new If from the given condition, then expression and else expression
     * @param condExpr the condition
     * @param thenExpr the expression to evaluate if the condition is true
     * @param elseExpr the expression to evaluate if the condition is false
     */
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

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        String condType = condExpr.typeName;
        String thenType = thenExpr.typeName;
        String elseType;

        // condType will be null if we already found errors in the typing of the condition,
        // otherwise it should have type bool
        if (condType != null && !condType.equals(VSOPTypes.BOOL.getName())) {
            errorList.add(new TypeNotExpectedException(condExpr, VSOPTypes.BOOL.getName()));
        }

        if (elseExpr == null) {
            elseType = VSOPTypes.UNIT.getName();
        } else {
            elseType = elseExpr.typeName;
        }

        // They will be null if we already found errors in the typing of these expression
        if (thenType != null && elseType != null) {

            // If there is no else expression, return type of the if is unit
            if (thenType.equals(VSOPTypes.UNIT.getName()) || elseType.equals(VSOPTypes.UNIT.getName())) {
                typeName = VSOPTypes.UNIT.getName();
            } else if (LanguageSpecs.isPrimitiveType(thenType) || LanguageSpecs.isPrimitiveType(elseType)) {

                // If the return type is a primitive type, then the types of the then and of the else should match
                // and the type of the If is the common type
                if (!thenType.equals(elseType)) {
                    errorList.add(new TypeNotExpectedException(thenExpr, elseExpr.typeName));
                } else {
                    typeName = thenType;
                }
            } else {

                // If the types of the then and of the else are class types, then no error can appear as Object
                // is a common ancestor of all classes. Simply set the type of the If to the first common ancestor thus
                typeName = firstCommonAncestor(thenType, elseType);
            }
        }
    }

    /**
     * See ASTNode, an If is printed as If(cond,then,else) of If(cond,then) depending on whether or not there is
     * an else expression
     */
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

    /**
     * See Expr
     */
    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        HashMap<String, String> labels = counter.getNextCondLabels();

        String llvm = "";
        String retId = null;

        String type = typeName;
        if(type.equals(VSOPTypes.UNIT.getName())) {
            type = null;
        }

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
        return castEval(finalEval, thenExpr.typeName, expectedType, counter);
    }

}