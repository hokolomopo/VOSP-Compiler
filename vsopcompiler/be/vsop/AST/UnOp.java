package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.LLVMTypes;
import be.vsop.semantic.LanguageSpecs;
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
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("UnOp(" + opType.name + ", ");
        expr.print(tabLevel, false, withTypes);
        System.out.print(")");
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        switch (opType) {
            case ISNULL:
                checkExpr(expr, "Object", errorList);
                typeName = "bool";
                break;
            case NOT:
                checkExpr(expr, "bool", errorList);
                typeName = "bool";
                break;
            case UMINUS:
                checkExpr(expr, "int32", errorList);
                typeName = "int32";
                break;
        }
    }

    /**
     * Check the types for the semantic analysis
     *
     * @param expr the expression
     * @param expectedType the expected type
     * @param errorList the list of error of the semantic analysis
     */
    private void checkExpr(Expr expr, String expectedType, ArrayList<SemanticException> errorList) {
        if (expr.typeName != null) {
            if (LanguageSpecs.isPrimitiveType(expectedType)) {
                if (!expr.typeName.equals(expectedType)) {
                    errorList.add(new TypeNotExpectedException(expr, expectedType));
                }
            } else {
                if (isNotChild(expr.typeName, expectedType)) {
                    errorList.add(new TypeNotExpectedException(expr, expectedType));
                }
            }
        }
    }

    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        //Evaluate expression
        ExprEval eval = expr.evalExpr(counter, null);

        //Get an id
        String id = counter.getNextLlvmId();

        //Generate code
        String llvm = eval.llvmCode + id + " = " + evaluateExpr(eval.llvmId) + endLine;


        return new ExprEval(id, llvm);
    }

    /**
     * Get the llvm code of the expression
     *
     * @param exprId the expression id
     * @return the llvm code
     */
    private String evaluateExpr(String exprId){
        switch (opType){
            case NOT:
                return LLVMKeywords.ADD.getLlvmName() + " " + VSOPTypes.BOOL.getLlvmName() + " 1" + ", " + exprId;
            case UMINUS:
                return LLVMKeywords.SUB.getLlvmName() + " " + VSOPTypes.INT32.getLlvmName() + " 0" + ", " + exprId;
            case ISNULL:
                return LLVMKeywords.EQ.getLlvmName() + " " + VSOPTypes.getLlvmTypeName(expr.typeName, true) +
                        " " + LLVMTypes.NULL.getLlvmName() + ", " + exprId;
        }

        return null;
    }




}