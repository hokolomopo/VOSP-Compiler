package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.codegenutil.LLVMKeywords;
import be.vsop.codegenutil.LLVMTypes;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents a VSOP unary operation, such a not, isnull or -
 */
public class UnOp extends Expr {
    /**
     * Enumeration containing all VSOP unary operations
     */
    private enum UnOpTypes{
        NOT("not"),
        UMINUS("-"),
        ISNULL("isnull");

        private String name;

        /**
         * Creates a new unary operation with the given VSOP name
         *
         * @param name the VSOP String representing the operation
         */
        UnOpTypes(String name) {
            this.name = name;
        }

        /**
         * Returns the UnOpType corresponding to the given name
         *
         * @param type the name of the UnOpType
         *
         * @return the corresponding UnOpType
         */
        public static UnOpTypes getType(String type){
            for(UnOpTypes t : UnOpTypes.values())
                if(t.name.equals(type))
                    return t;
            return null;
        }

        /**
         * Returns the VSOP string representing a UnOpType
         *
         * @return the VSOP String representing the operation
         */
        public String getName() {
            return name;
        }
    }

    private UnOpTypes opType;
    private Expr expr;

    /**
     * Creates a new UnOp from a name (VSOP operation string), and the two operands (expressions)
     *
     * @param name the VSOP String representing the operation
     * @param expr the expression representing the operand
     */
    public UnOp(String name, Expr expr) {
        this.opType = UnOpTypes.getType(name);
        this.expr = expr;

        this.children = new ArrayList<>();
        this.children.add(expr);
    }

    /**
     * See ASTNode, an UnOp is printed as UnOp(name, expr)
     */
    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("UnOp(" + opType.name + ", ");
        expr.print(tabLevel, false, withTypes);
        System.out.print(")");
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        switch (opType) {
            case ISNULL:
                // isnull expects an Object and returns a bool
                checkExpr(expr, "Object", errorList);
                typeName = VSOPTypes.BOOL.getName();
                break;

            case NOT:
                // not expects a bool and returns a bool
                checkExpr(expr, VSOPTypes.BOOL.getName(), errorList);
                typeName = VSOPTypes.BOOL.getName();
                break;

            case UMINUS:
                // uminus expects an int32 and returns an int32
                checkExpr(expr, VSOPTypes.INT32.getName(), errorList);
                typeName = VSOPTypes.INT32.getName();
                break;
        }
    }

    /**
     * Checks whether the expression has the type we expect, add an exception in errorList if not
     *
     * @param expr the expression to check
     * @param expectedType the expected type
     * @param errorList the list of errors, could be updated
     */
    private void checkExpr(Expr expr, String expectedType, ArrayList<SemanticException> errorList) {
        // expr typeName will be null if we already found a type error in it, we don't want to generate too much
        // errors that would not be relevant
        if (expr.typeName != null) {
            if (LanguageSpecs.isPrimitiveType(expectedType)) {

                // If the expression has a primitive type, simply check equality with the expected type
                if (!expr.typeName.equals(expectedType)) {
                    errorList.add(new TypeNotExpectedException(expr, expectedType));
                }
            } else {

                // If the expression has not a primitive type, check that its type is a child of the expected one
                // (a child is a parent).
                // Note : currently the only unary operation of VSOP on classes is isnull, which expects an Object.
                // This condition will then always be false thus this could be removed.
                if (isNotChild(expr.typeName, expectedType)) {
                    errorList.add(new TypeNotExpectedException(expr, expectedType));
                }
            }
        }
    }

    /**
     * See Expr
     */
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
                // in boolean, x + 1 and not x are equivalent
                return LLVMKeywords.ADD.getLlvmName() + " " + VSOPTypes.BOOL.getLlvmName() + " 1" + ", " + exprId;
            case UMINUS:
                // 0 - x is equivalent to -x, as well as (-1)*x
                return LLVMKeywords.SUB.getLlvmName() + " " + VSOPTypes.INT32.getLlvmName() + " 0" + ", " + exprId;
            case ISNULL:
                return LLVMKeywords.EQ.getLlvmName() + " " + VSOPTypes.getLlvmTypeName(expr.typeName) +
                        " " + LLVMTypes.NULL.getLlvmName() + ", " + exprId;
        }

        return null;
    }
}