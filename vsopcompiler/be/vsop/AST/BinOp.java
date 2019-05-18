package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.TypeNotExpectedException;
import be.vsop.semantic.*;

import java.util.ArrayList;
import java.util.HashMap;

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
    private String llvmId;
    private boolean primitiveOps = false;

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

    private static void checkExpr(Expr expr, String expectedType, ArrayList<SemanticException> errorList) {
        if (expr.typeName != null) {
            if (!expr.typeName.equals(expectedType)) {
                errorList.add(new TypeNotExpectedException(expr, expectedType));
            }
        }
    }

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
            llvm += llvmBranch(leftPair.llvmId, labels.get(InstrCounter.COND_IF_LABEL),
                    labels.get(InstrCounter.COND_ELSE_LABEL));

            // If first operand is true, result is the second operand.
            // Exec code to generate right operand value and then simply branch, the phi function will do the rest.
            llvm += llvmLabel(labels.get(InstrCounter.COND_IF_LABEL)) + rightPair.llvmCode;
            llvm += llvmBranch(labels.get(InstrCounter.COND_END_LABEL));

            // If first operand is false, the result is false. Simply branch and let the phi function do the rest.
            llvm += llvmLabel(labels.get(InstrCounter.COND_ELSE_LABEL)) +
                    llvmBranch(labels.get(InstrCounter.COND_END_LABEL));

            // Set result to false if previous block was the else one (first operand is false),
            // and set result to second operand if the previous block was the if one.
            llvm += llvmLabel(labels.get(InstrCounter.COND_END_LABEL));
            llvm += llvmPhi(llvmId, LLVMTypes.BOOL, rightPair.llvmId, labels.get(InstrCounter.COND_IF_LABEL),
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
                //Both operand are unit type, condition always true
                if(rhs.getTypeName().equals(VSOPTypes.UNIT.getName())){
                    return llvmBinOp(llvmId, "0", "0", LLVMKeywords.EQ, LLVMTypes.BOOL.getLlvmName());//TODO, Greg, tu sais comment faire plus propore que 0 == 0 ?
                }


                if (primitiveOps) {
                    return llvmBinOp(llvmId, leftId, rightId, LLVMKeywords.EQ, VSOPTypes.getLlvmTypeName(rhs.typeName));
                }
                String llvmTypeLeft = VSOPTypes.getLlvmTypeName(lhs.typeName, true);
                String llvmTypeRight = VSOPTypes.getLlvmTypeName(rhs.typeName, true);
                String leftPointerValue = llvmId;
                // Turn class pointers to i64, then compare the i64 values (i.e., check if addresses are equal)
                ret = llvmCast(leftPointerValue, LLVMKeywords.PTRTOINT, llvmTypeLeft,
                        LLVMTypes.INT64, leftId);
                String rightPointerValue = counter.getNextLlvmId();
                ret += llvmCast(rightPointerValue, LLVMKeywords.PTRTOINT, llvmTypeRight,
                        LLVMTypes.INT64, rightId);
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
                String firstArgFloatId = llvmId;
                ret = llvmCast(firstArgFloatId, LLVMKeywords.INTTOFLOAT, LLVMTypes.INT32, LLVMTypes.FLOAT, leftId);

                ArrayList<String> argumentsIds = new ArrayList<>();
                argumentsIds.add(firstArgFloatId);
                argumentsIds.add(rightId);
                ArrayList<String> argumentsTypes = new ArrayList<>();
                argumentsTypes.add(LLVMTypes.FLOAT.getLlvmName());
                argumentsTypes.add(LLVMTypes.INT32.getLlvmName());
                String resultFloatId = counter.getNextLlvmId();
                ret += llvmCall(resultFloatId, LLVMTypes.FLOAT.getLlvmName(), LLVMIntrinsics.POW.getLlvmName(),
                        argumentsIds, argumentsTypes);

                llvmId = counter.getNextLlvmId();
                ret += llvmCast(llvmId, LLVMKeywords.FLOATTOINT, LLVMTypes.FLOAT, LLVMTypes.INT32, resultFloatId);

                return ret;
        }

        return null;
    }

    private String llvmBinOp(String result, String operand1, String operand2, LLVMKeywords operation) {
        return llvmBinOp(result, operand1, operand2, operation, LLVMTypes.INT32.getLlvmName());
    }

    private String llvmBinOp(String result, String operand1, String operand2, LLVMKeywords operation, String llvmType) {
        return result + " = " + operation.getLlvmName() + " " + llvmType + " " + operand1 + ", " + operand2 + endLine;
    }
}