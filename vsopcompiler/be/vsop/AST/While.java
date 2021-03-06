package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represent a VSOP while expression
 */
public class While extends Expr {
    private Expr condExpr;
    private Expr bodyExpr;

    /**
     * Creates a new While from the given condition and body
     *
     * @param condExpr the condition
     * @param bodyExpr the body
     */
    public While(Expr condExpr, Expr bodyExpr) {
        this.condExpr = condExpr;
        this.bodyExpr = bodyExpr;

        this.children = new ArrayList<>();
        this.children.add(condExpr);
        this.children.add(bodyExpr);
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);

        // the typeName of the cond will be null if we already found an error in it, we don't want to generate
        // irrelevant errors
        if (condExpr.typeName != null && !condExpr.getTypeName().equals(VSOPTypes.BOOL.getName())) {
            errorList.add(new TypeNotExpectedException(condExpr, VSOPTypes.BOOL.getName()));
        }

        // a while has always a type unit, otherwise it would cause problems with loops that are not executed at least
        // once
        typeName = VSOPTypes.UNIT.getName();
    }

    /**
     * See ASTNode, a While is printed as While(cond,body)
     */
    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("While(");
        condExpr.print(tabLevel, false, withTypes);
        System.out.print(",");
        System.out.println();
        bodyExpr.print(tabLevel + 1, true, withTypes);
        System.out.print(")");
    }

    /**
     * See Expr
     */
    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {

        String llvm = "";
        HashMap<String, String> labels = counter.getNextLoopLabel();

        // Must branch to the start of the loop, because otherwise llvm says that the label is an unexpected token
        llvm += "br label %" + labels.get(InstrCounter.LOOP_COND_LABEL) + endLine;

        //Evaluate loop condition
        llvm += labels.get(InstrCounter.LOOP_COND_LABEL) + ":" + endLine;
        ExprEval condEval = condExpr.evalExpr(counter, VSOPTypes.BOOL.getName());
        llvm += condEval.llvmCode;
        llvm += "br i1 " + condEval.llvmId + ", label %" + labels.get(InstrCounter.LOOP_START_LABEL) + "," +
                " label %" + labels.get(InstrCounter.LOOP_END_LABEL) + endLine + endLine;

        //Loop start label
        llvm += labels.get(InstrCounter.LOOP_START_LABEL) + ":" + endLine;

        //Evaluate body of loop
        ExprEval bodyEval = bodyExpr.evalExpr(counter, null);
        llvm += bodyEval.llvmCode;
        llvm += "br label %" + labels.get(InstrCounter.LOOP_COND_LABEL) + endLine + endLine;

        //Loop end label
        llvm += labels.get(InstrCounter.LOOP_END_LABEL) + ":" + endLine;

        return new ExprEval(null, llvm);
    }
}