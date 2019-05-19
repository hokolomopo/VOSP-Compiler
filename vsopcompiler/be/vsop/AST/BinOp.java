package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represent a binary operation such as +, and, =, <, ...
 */
public class BinOp extends Expr {
    /**
     * Enumeration containing all VSOP binary operations
     */
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

        /**
         * Creates a new binary operation with the given VSOP name
         *
         * @param name the VSOP String representing the operation
         */
        BinOpTypes(String name) {
            this.name = name;
        }

        /**
         * Returns the BinOPType corresponding to the given name
         *
         * @param type the name of the BinOpType
         *
         * @return the corresponding BinOpType
         */
        public static BinOpTypes getType(String type){
            for(BinOpTypes t : BinOpTypes.values())
                if(t.name.equals(type))
                    return t;
            return null;
        }

        /**
         * Returns the VSOP string representing a BinOpType
         *
         * @return the VSOP String representing the operation
         */
        public String getName() {
            return name;
        }
    }

    private BinOpTypes type;
    private Expr lhs;
    private Expr rhs;
    private String llvmId;
    private boolean primitiveOps = false;

    /**
     * Creates a new BinOp from a name (VSOP operation string), and the two operands (expressions)
     *
     * @param name the VSOP String representing the operation
     * @param lhs the expression representing the left-hand-side
     * @param rhs the expression representing the right-hand-side
     */
    public BinOp(String name, Expr lhs, Expr rhs) {
        this.type = BinOpTypes.getType(name);
        this.lhs = lhs;
        this.rhs = rhs;

        this.children = new ArrayList<>();
        this.children.add(lhs);
        this.children.add(rhs);

    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        switch (type) {
            case AND:
                checkExpr(lhs, VSOPTypes.BOOL.getName(), errorList);
                checkExpr(rhs, VSOPTypes.BOOL.getName(), errorList);
                typeName = VSOPTypes.BOOL.getName();
                break;

            case EQUAL:
                if (LanguageSpecs.isPrimitiveType(lhs.typeName) || LanguageSpecs.isPrimitiveType(rhs.typeName)) {
                    primitiveOps = true;
                    checkExpr(lhs, rhs.typeName, errorList);
                }
                typeName = VSOPTypes.BOOL.getName();
                break;

            case LOWER:
            case LOWEREQ:
                checkExpr(lhs, VSOPTypes.INT32.getName(), errorList);
                checkExpr(rhs, VSOPTypes.INT32.getName(), errorList);
                typeName = VSOPTypes.BOOL.getName();
                break;

            case PLUS:
            case MINUS:
            case TIMES:
            case DIVIDED:
            case POW:
                checkExpr(lhs, VSOPTypes.INT32.getName(), errorList);
                checkExpr(rhs, VSOPTypes.INT32.getName(), errorList);
                typeName = VSOPTypes.INT32.getName();
                break;
        }
    }

    /**
     * See ASTNode, a BinOp is printed as BinOp(type, left-hand-side, right-hand-side)
     */
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

    /**
     * Checks whether the expression has the type we expect, add an exception in errorList if not
     *
     * @param expr the expression to check
     * @param expectedType the expected type
     * @param errorList the list of errors, could be updated
     */
    private static void checkExpr(Expr expr, String expectedType, ArrayList<SemanticException> errorList) {
        if (expr.typeName != null) {
            if (!expr.typeName.equals(expectedType)) {
                errorList.add(new TypeNotExpectedException(expr, expectedType));
            }
        }
    }

    /**
     * See Expr
     */
    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {

        //Evaluate left expression
        ExprEval leftPair = lhs.evalExpr(counter, null);

        //Evaluate right expression
        ExprEval rightPair = rhs.evalExpr(counter, null);

        if (type == BinOpTypes.AND) {
            // Particular case useful for short-circuiting
            llvmId = counter.getNextLlvmId();
            HashMap<String, String> labels = counter.getNextCondLabels();
            String llvm = leftPair.llvmCode;
            llvm += LlvmWrappers.llvmBranch(leftPair.llvmId, labels.get(InstrCounter.COND_IF_LABEL),
                    labels.get(InstrCounter.COND_ELSE_LABEL));

            // If first operand is true, result is the second operand.
            // Exec code to generate right operand value and then simply branch, the phi function will do the rest.
            llvm += LlvmWrappers.llvmLabel(labels.get(InstrCounter.COND_IF_LABEL)) + rightPair.llvmCode;
            llvm += LlvmWrappers.llvmBranch(labels.get(InstrCounter.COND_END_LABEL));

            // If first operand is false, the result is false. Simply branch and let the phi function do the rest.
            llvm += LlvmWrappers.llvmLabel(labels.get(InstrCounter.COND_ELSE_LABEL)) +
                    LlvmWrappers.llvmBranch(labels.get(InstrCounter.COND_END_LABEL));

            // Set result to false if previous block was the else one (first operand is false),
            // and set result to second operand if the previous block was the if one.
            llvm += LlvmWrappers.llvmLabel(labels.get(InstrCounter.COND_END_LABEL));
            llvm += LlvmWrappers.llvmPhi(llvmId, LLVMTypes.BOOL, rightPair.llvmId, labels.get(InstrCounter.COND_IF_LABEL),
                    "0", labels.get((InstrCounter.COND_ELSE_LABEL)));
            return new ExprEval(llvmId, llvm);
        }

        String llvm = leftPair.llvmCode + rightPair.llvmCode +
                evaluateExpr(leftPair.llvmId, rightPair.llvmId, counter);

        return new ExprEval(llvmId, llvm);
    }

    private String evaluateExpr(String leftId, String rightId, InstrCounter counter){
        llvmId = counter.getNextLlvmId();
        String ret;
        switch (type){
            case EQUAL:
                // This case is a bit special because one need to compare addresses of classes instead of their values
                // (which would be harder). There is 3 cases, see below

                // First case : both operands are unit type, condition always true because there is only 1 possible value
                // in the unit type
                if(rhs.getTypeName().equals(VSOPTypes.UNIT.getName())){
                    return LlvmWrappers.setBool(llvmId, true);
                }

                // Second case : operands have a primitive type, simply check their values
                if (primitiveOps) {
                    return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.EQ, VSOPTypes.getLlvmTypeName(rhs.typeName));
                }

                // Third case : need to compare addresses, need to convert pointers to int value to do so
                // (otherwise we wouldn't be able to compare values of different types, which can still have the same
                // address because of inheritance)
                String llvmTypeLeft = VSOPTypes.getLlvmTypeName(lhs.typeName);
                String llvmTypeRight = VSOPTypes.getLlvmTypeName(rhs.typeName);

                // Turn class pointers to i64 : left-hand-side
                String leftPointerValue = llvmId;
                ret = LlvmWrappers.llvmCast(leftPointerValue, LLVMKeywords.PTRTOINT, llvmTypeLeft,
                        LLVMTypes.INT64, leftId);

                // Turn class pointers to i64 : right-hand-side
                String rightPointerValue = counter.getNextLlvmId();
                ret += LlvmWrappers.llvmCast(rightPointerValue, LLVMKeywords.PTRTOINT, llvmTypeRight,
                        LLVMTypes.INT64, rightId);

                // Compare addresses with icmp eq
                llvmId = counter.getNextLlvmId();
                ret += llvmBinOp(llvmId, leftPointerValue, rightPointerValue, LLVMKeywords.EQ, LLVMTypes.INT64.getLlvmName());
                return ret;

            case LOWEREQ:
                return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.LOWEREQ);
            case LOWER:
                return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.LOWER);
            case PLUS:
                return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.ADD);
            case MINUS:
                return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.SUB);
            case TIMES:
                return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.MUL);
            case DIVIDED:
                return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.DIV);

            case POW:
                // There is no ^ operator in llvm. However, there exists an intrinsic that computes the power of a
                // double to an int, and returns a double. Thus, we convert the left-hand-side to a double, we compute
                // the power, we convert the result to an int, and we finally return the result.

                // Turn left-hand-side into a double
                String firstArgFloatId = llvmId;
                ret = LlvmWrappers.llvmCast(firstArgFloatId, LLVMKeywords.INTTOFLOAT, LLVMTypes.INT32, LLVMTypes.FLOAT, leftId);

                // Calls pow intrinsics on the operands
                ArrayList<String> argumentsIds = new ArrayList<>();
                argumentsIds.add(firstArgFloatId);
                argumentsIds.add(rightId);

                ArrayList<String> argumentsTypes = new ArrayList<>();
                argumentsTypes.add(LLVMTypes.FLOAT.getLlvmName());
                argumentsTypes.add(LLVMTypes.INT32.getLlvmName());

                String resultFloatId = counter.getNextLlvmId();
                ret += LlvmWrappers.llvmCall(resultFloatId, LLVMTypes.FLOAT.getLlvmName(), LLVMExternalFunctions.POW.getLlvmName(),
                        argumentsIds, argumentsTypes);

                // Turn result into an int
                llvmId = counter.getNextLlvmId();
                ret += LlvmWrappers.llvmCast(llvmId, LLVMKeywords.FLOATTOINT, LLVMTypes.FLOAT, LLVMTypes.INT32, resultFloatId);

                return ret;
        }

        return null;
    }

    /**
     * Convenience method with type defaulting to i32, see below
     */
    private String llvmBinOp(String result, String operand1, String operand2, LLVMKeywords operation) {
        return llvmBinOp(result, operand1, operand2, operation, LLVMTypes.INT32.getLlvmName());
    }

    /**
     * Returns a String representing an llvm operation, defined by the operation argument, on the given operands.
     * The result is stored in the id given in argument (result) and the type of the operands must also be given
     * in argument
     *
     * @param result the llvm id in which to store the result
     * @param operand1 the llvm id of the left-hand-side operand
     * @param operand2 the llvm id of the right-hand-side operand
     * @param operation the operation, represented by a LLVMKeyword
     * @param llvmType the type of the operands (must be the same for both operands)
     *
     * @return a String representing the binary operation written in llvm
     */
    private String llvmBinOp(String result, String operand1, String operand2, LLVMKeywords operation, String llvmType) {
        return result + " = " + operation.getLlvmName() + " " + llvmType + " " + operand1 + ", " + operand2 + endLine;
    }
}