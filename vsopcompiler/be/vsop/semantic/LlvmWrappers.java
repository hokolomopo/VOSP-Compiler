package be.vsop.semantic;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;

import java.util.ArrayList;

public class LlvmWrappers {
    public static final String endLine = "\n";
    public static final String emptyString = "getelementptr inbounds ([1 x i8], [1 x i8]* @.str.emptyStr, i32 0, i32 0)";

    public static String newFunctionNameFromClassName(String className) {
        // leading . so that can't conflict with user-defined functions
        return "@.New." + className;
    }

    public static String callNew(String result, String className) {
        return result + " = " + LLVMKeywords.CALL.getLlvmName() + " " + VSOPTypes.getLlvmTypeName(className, true) +
                " " + newFunctionNameFromClassName(className) + "()" + endLine;
    }

    public static String stackAllocation(String result, String type) {
        return result + " = " + LLVMKeywords.ALLOCATE.getLlvmName() + " " + type + endLine;
    }

    public static ExprEval heapAllocation(InstrCounter counter, String vsopType) {
        String sizePtrId = counter.getNextLlvmId();
        String sizeI64Id = counter.getNextLlvmId();
        String allocatedI8Id = counter.getNextLlvmId();
        String allocatedI64Id = counter.getNextLlvmId();
        String resultId = counter.getNextLlvmId();
        String llvmType = VSOPTypes.getLlvmTypeName(vsopType);
        String llvmTypePtr = llvmType + "*";
        StringBuilder llvm = new StringBuilder();

        llvm.append(sizePtrId).append(" = ").append(LLVMKeywords.GETPTR.getLlvmName()).append(" ").append(llvmType)
                .append(", ").append(llvmTypePtr).append(" ").append(LLVMTypes.NULL.getLlvmName()).append(", ")
                .append(LLVMTypes.INT32.getLlvmName()).append(" 1").append(endLine);

        llvm.append(cast(sizeI64Id, LLVMKeywords.PTRTOINT, llvmTypePtr, LLVMTypes.INT64, sizePtrId));

        ArrayList<String> argumentsIds = new ArrayList<>();
        argumentsIds.add(sizeI64Id);
        ArrayList<String> argumentsTypes = new ArrayList<>();
        argumentsTypes.add(LLVMTypes.INT64.getLlvmName());
        llvm.append(call(allocatedI8Id, LLVMTypes.STRING.getLlvmName(), LLVMIntrinsics.MALLOC.getLlvmName(),
                argumentsIds, argumentsTypes));

        llvm.append(cast(allocatedI64Id, LLVMKeywords.PTRTOINT, LLVMTypes.STRING, LLVMTypes.INT64, allocatedI8Id));
        llvm.append(cast(resultId, LLVMKeywords.INTTOPTR, LLVMTypes.INT64, llvmTypePtr , allocatedI64Id));

        return new ExprEval(resultId, llvm.toString());
    }

    public static String store(LLVMTypes type, String value, String where) {
        return store(type.getLlvmName(), value, where);
    }

    public static String store(String type, String value, String where) {
        return LLVMKeywords.STORE.getLlvmName() + " " + type + " " + value + ", " + type + "* " + where + endLine;
    }

    public static String setBool(String result, boolean value) {
        if (value) {
            return result + " = " + LLVMKeywords.ADD.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + "1" + ", " + "0" + endLine;
        } else {
            return result + " = " + LLVMKeywords.ADD.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + "0" + ", " + "0" + endLine;
        }
    }

    public static String label(String label) {
        return label + ":" + endLine;
    }

    public static String branch(String label) {
        return LLVMKeywords.BRANCH.getLlvmName() + " " + LLVMKeywords.LABEL.getLlvmName() + " " + label + endLine;
    }

    public static String branch(String cond, String labelTrue, String labelFalse) {
        return LLVMKeywords.BRANCH.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + cond + " " + ", " +
                LLVMKeywords.LABEL.getLlvmName() + " %" + labelTrue + ", " + LLVMKeywords.LABEL.getLlvmName() + " %" +
                labelFalse + endLine;
    }

    public static String phi(String result, LLVMTypes type, String valIfTrue, String labelIfTrue,
                   String valIfFalse, String labelIfFalse) {
        return result + " = " + LLVMKeywords.PHI.getLlvmName() + " " + type.getLlvmName() + " [" + valIfTrue +
                ", %" + labelIfTrue + "], [" + valIfFalse + ", " + labelIfFalse + "]" + endLine;
    }

    public static String cast(String result, LLVMKeywords conversion, LLVMTypes fromType, LLVMTypes toType, String fromValue) {
        return cast(result, conversion, fromType.getLlvmName(), toType.getLlvmName(), fromValue);
    }

    public static String cast(String result, LLVMKeywords conversion, String fromType, LLVMTypes toType, String fromValue) {
        return cast(result, conversion, fromType, toType.getLlvmName(), fromValue);
    }

    public static String cast(String result, LLVMKeywords conversion, LLVMTypes fromType, String toType, String fromValue) {
        return cast(result, conversion, fromType.getLlvmName(), toType, fromValue);
    }

    private static String cast(String result, LLVMKeywords conversion, String fromType, String toType, String fromValue) {
        return result + " = " + conversion.getLlvmName() + " " + fromType +
                " " + fromValue + " " + LLVMKeywords.TO.getLlvmName() + " " + toType + endLine;
    }

    public static String call(String result, String retType, String funcName, ArrayList<String> argumentsIds, ArrayList<String> argumentsTypes) {
        StringBuilder ret = new StringBuilder();
        ret.append(result).append(" = ").append(LLVMKeywords.CALL.getLlvmName()).append(" ").append(retType)
                .append(" ").append(funcName).append("(");
        for (int i = 0; i < argumentsIds.size(); i++) {
            ret.append(argumentsTypes.get(i)).append(" ").append(argumentsIds.get(i)).append(", ");
        }
        ret.setLength(ret.length() - 2);
        ret.append(")").append(endLine);
        return ret.toString();
    }

    public static String binOp(String result, String operand1, String operand2, LLVMKeywords operation) {
        return binOp(result, operand1, operand2, operation, LLVMTypes.INT32.getLlvmName());
    }

    public static String binOp(String result, String operand1, String operand2, LLVMKeywords operation, String llvmType) {
        return result + " = " + operation.getLlvmName() + " " + llvmType + " " + operand1 + ", " + operand2 + endLine;
    }
}