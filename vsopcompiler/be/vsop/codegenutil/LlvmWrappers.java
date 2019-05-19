package be.vsop.codegenutil;

import be.vsop.AST.Type;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class contains different functions and variables that generates llvm code for given operations.
 */
public class LlvmWrappers {
    private static final String endLine = "\n";
    public static final String emptyString = "getelementptr inbounds ([1 x i8], [1 x i8]* @.str.emptyStr, i32 0, i32 0)";

    /**
     * Returns the name of the llvm New function that will allocate the given class
     *
     * @param className the name of the class
     * @return the name of the new function for the class
     */
    public static String newFunctionNameFromClassName(String className) {
        // leading . so that can't conflict with user-defined functions
        return "@.New." + className;
    }

    /**
     * Returns the name of the llvm Init function that will initialise the given class
     *
     * @param className the name of the class
     * @return the name of the init function for the class
     */
    public static String initFunctionName(String className) {
        // leading . so that can't conflict with user-defined functions
        return "@.Init." + className;
    }

    /**
     * Returns the name of the llvm vTable that contains the functions of the given class
     *
     * @param className the name of the class
     * @return the name of the vTable for the class
     */
    public static String vTableName(String className) {
        return "%VTable." + className;
    }

    /**
     * Returns the llvm name of the given method from the given class
     *
     * @param method the method
     * @param parentClass the class
     *
     * @return the llvm name of the method implemented by the class (the class is important because of overriding)
     */
    public static String getMethodName(String method, String parentClass){
        return "@" + parentClass + "." + method;
    }

    /**
     * Returns the llvm code that calls the new function of the given class, and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param className the class
     *
     * @return the llvm code
     */
    public static String callNew(String result, String className) {
        return result + " = " + LLVMKeywords.CALL.getLlvmName() + " " + VSOPTypes.getLlvmTypeName(className) +
                " " + newFunctionNameFromClassName(className) + "()" + endLine;
    }

    /**
     * Returns the llvm code that allocates the given type on the stack (will be deleted automatically at the end of the
     * current function), and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param type the type
     *
     * @return the llvm code
     */
    public static String stackAllocation(String result, String type) {
        return result + " = " + LLVMKeywords.ALLOCATE.getLlvmName() + " " + type + endLine;
    }

    /**
     * Returns an ExprEval which contains the code necessary to allocate on the heap (malloc) the given type
     *
     * @param counter the InstrCounter
     * @param llvmType the type to be allocated
     *
     * @return the ExprEval
     */
    public static ExprEval heapAllocation(InstrCounter counter, String llvmType) {
        // In llvm, allocation is done in several steps :
        // First, compute the size of the type to be allocated
        // with the getelementptr trick (getelementptr from null = 0 to the second element of an array containing
        // the type we are interested in)

        // Second, allocate the type with malloc

        // Third, cast the returned pointer (i8*) to a pointer on the type, by passing through a i64
        String sizePtrId = counter.getNextLlvmId();
        String sizeI64Id = counter.getNextLlvmId();

        String allocatedI8Id = counter.getNextLlvmId();
        String allocatedI64Id = counter.getNextLlvmId();

        String resultId = counter.getNextLlvmId();
        String llvmTypePtr = llvmType + "*";
        StringBuilder llvm = new StringBuilder();

        // Computing the size
        llvm.append(sizePtrId).append(" = ").append(LLVMKeywords.GETPTR.getLlvmName()).append(" ").append(llvmType)
                .append(", ").append(llvmTypePtr).append(" ").append(LLVMTypes.NULL.getLlvmName()).append(", ")
                .append(LLVMTypes.INT32.getLlvmName()).append(" 1").append(endLine);

        llvm.append(cast(sizeI64Id, LLVMKeywords.PTRTOINT, llvmTypePtr, LLVMTypes.INT64, sizePtrId));

        // Calling malloc
        ArrayList<String> argumentsIds = new ArrayList<>();
        argumentsIds.add(sizeI64Id);

        ArrayList<String> argumentsTypes = new ArrayList<>();
        argumentsTypes.add(LLVMTypes.INT64.getLlvmName());

        llvm.append(call(allocatedI8Id, LLVMTypes.STRING.getLlvmName(), LLVMExternalFunctions.MALLOC.getLlvmName(),
                argumentsIds, argumentsTypes));

        // Casting i8* into type pointer
        llvm.append(cast(allocatedI64Id, LLVMKeywords.PTRTOINT, LLVMTypes.STRING, LLVMTypes.INT64, allocatedI8Id));
        llvm.append(cast(resultId, LLVMKeywords.INTTOPTR, LLVMTypes.INT64, llvmTypePtr , allocatedI64Id));

        return new ExprEval(resultId, llvm.toString());
    }

    /**
     * Returns the llvm code that stores value in where. Convenience method equivalent to
     * store(type.getLlvmName(), value, where)
     *
     * @param type the LLVMType of the value
     * @param value the value to store
     * @param where a pointer to the address at which value has to be stored
     *
     * @return the llvm code
     */
    public static String store(LLVMTypes type, String value, String where) {
        return store(type.getLlvmName(), value, where);
    }

    /**
     * Returns the llvm code that stores value in where
     *
     * @param type the type of the value
     * @param value the value to store
     * @param where a pointer to the address at which value has to be stored
     *
     * @return the llvm code
     */
    public static String store(String type, String value, String where) {
        return LLVMKeywords.STORE.getLlvmName() + " " + type + " " + value + ", " + type + "* " + where + endLine;
    }

    /**
     * Returns the llvm code that sets a bool to a given value, and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param value the boolean value to put in the bool
     *
     * @return the llvm code
     */
    public static String setBool(String result, boolean value) {
        if (value) {
            return result + " = " + LLVMKeywords.ADD.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + "1" + ", " + "0" + endLine;
        } else {
            return result + " = " + LLVMKeywords.ADD.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + "0" + ", " + "0" + endLine;
        }
    }

    /**
     * Returns the llvm code that creates a label
     *
     * @param label the label to create
     *
     * @return the llvm code
     */
    public static String label(String label) {
        return label + ":" + endLine;
    }

    /**
     * Returns the llvm code that branches unconditionally to the given label
     *
     * @param label the label to branch to
     *
     * @return the llvm code
     */
    public static String branch(String label) {
        return LLVMKeywords.BRANCH.getLlvmName() + " " + LLVMKeywords.LABEL.getLlvmName() + " %" + label + endLine;
    }

    /**
     * Returns the llvm code that branches to labelTrue if cond is true, and to labelFalse otherwise
     *
     * @param cond the condition
     * @param labelTrue the label to branch to if true
     * @param labelFalse the label to branch to if false
     *
     * @return the llvm code
     */
    public static String branch(String cond, String labelTrue, String labelFalse) {
        return LLVMKeywords.BRANCH.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + cond + ", " +
                LLVMKeywords.LABEL.getLlvmName() + " %" + labelTrue + ", " + LLVMKeywords.LABEL.getLlvmName() + " %" +
                labelFalse + endLine;
    }

    /**
     * Returns the llvm code that calls the phi function on the given type, setting result to val1 if the last
     * code executed was labeled with label1, and to val2 if the last code executed was labeled with label2.
     * Result is undefined if the last code executed was labeled with neither label1 nor label2
     *
     * @param result the llvm id in which to store the result
     * @param type the LLVMType of val1 and val2
     * @param val1 the val to use in first case
     * @param label1 the first label
     * @param val2 the val to use in second case
     * @param label2 the second label
     *
     * @return the llvm code
     */
    public static String phi(String result, LLVMTypes type, String val1, String label1,
                             String val2, String label2) {
        return result + " = " + LLVMKeywords.PHI.getLlvmName() + " " + type.getLlvmName() + " [" + val1 +
                ", %" + label1 + "], [" + val2 + ", %" + label2 + "]" + endLine;
    }

    /**
     * Returns the llvm code that casts fromValue, which should be of type fromType, to the type toType, using the
     * conversion conversion which should be chosen accordingly, and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param conversion the LLVMKeywords conversion
     * @param fromType the LLVMTypes to cast from
     * @param toType the LLVMTypes to cast to
     * @param fromValue the value to cast from
     *
     * @return the llvm code
     */
    public static String cast(String result, LLVMKeywords conversion, LLVMTypes fromType, LLVMTypes toType, String fromValue) {
        return cast(result, conversion, fromType.getLlvmName(), toType.getLlvmName(), fromValue);
    }

    /**
     * Returns the llvm code that casts fromValue, which should be of type fromType, to the type toType, using the
     * conversion conversion which should be chosen accordingly, and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param conversion the LLVMKeywords conversion
     * @param fromType the type to cast from
     * @param toType the LLVMTypes to cast to
     * @param fromValue the value to cast from
     *
     * @return the llvm code
     */
    public static String cast(String result, LLVMKeywords conversion, String fromType, LLVMTypes toType, String fromValue) {
        return cast(result, conversion, fromType, toType.getLlvmName(), fromValue);
    }

    /**
     * Returns the llvm code that casts fromValue, which should be of type fromType, to the type toType, using the
     * conversion conversion which should be chosen accordingly, and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param conversion the LLVMKeywords conversion
     * @param fromType the LLVMTypes to cast from
     * @param toType the type to cast to
     * @param fromValue the value to cast from
     *
     * @return the llvm code
     */
    public static String cast(String result, LLVMKeywords conversion, LLVMTypes fromType, String toType, String fromValue) {
        return cast(result, conversion, fromType.getLlvmName(), toType, fromValue);
    }

    /**
     * Returns the llvm code that casts fromValue, which should be of type fromType, to the type toType, using the
     * conversion conversion which should be chosen accordingly, and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param conversion the LLVMKeywords conversion
     * @param fromType the type to cast from
     * @param toType the type to cast to
     * @param fromValue the value to cast from
     *
     * @return the llvm code
     */
    private static String cast(String result, LLVMKeywords conversion, String fromType, String toType, String fromValue) {
        return result + " = " + conversion.getLlvmName() + " " + fromType +
                " " + fromValue + " " + LLVMKeywords.TO.getLlvmName() + " " + toType + endLine;
    }

    /**
     * Returns the llvm code that that calls funcName, which should have a retType has return type, with
     * the given arguments (ids and types), which could correspond to the ones expected by the function,
     * and stores the result in the llvm id result
     *
     * @param result the llvm id in which to store the result
     * @param retType the return type of the function
     * @param funcName the name of the function
     * @param argumentsIds the list of llvm ids of the arguments
     * @param argumentsTypes the list of llvm types of the arguments
     *
     * @return the llvm code
     */
    public static String call(String result, String retType, String funcName,
                              ArrayList<String> argumentsIds, ArrayList<String> argumentsTypes) {
        // If result is null, that means that we don't need to save the result of the call in a variable
        StringBuilder ret = new StringBuilder();
        if (result != null) {
            ret.append(result).append(" = ");
        }
        ret.append(LLVMKeywords.CALL.getLlvmName()).append(" ").append(retType)
                .append(" ").append(funcName).append("(");
        for (int i = 0; i < argumentsIds.size(); i++) {
            ret.append(argumentsTypes.get(i)).append(" ").append(argumentsIds.get(i)).append(", ");
        }

        // Remove last ", "
        ret.setLength(ret.length() - 2);

        ret.append(")").append(endLine);
        return ret.toString();
    }

    /**
     * Convenience method with type defaulting to i32, see below
     */
    public static String binOp(String result, String operand1, String operand2, LLVMKeywords operation) {
        return binOp(result, operand1, operand2, operation, LLVMTypes.INT32.getLlvmName());
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
    public static String binOp(String result, String operand1, String operand2, LLVMKeywords operation, String llvmType) {
        return result + " = " + operation.getLlvmName() + " " + llvmType + " " + operand1 + ", " + operand2 + endLine;
    }

    /**
     * Returns the llvm code that prints the string content, possibly adding an endLine at the end
     *
     * @param counter an InstrCounter
     * @param content the content of the string to print
     * @param addEndLine whether to add a new line at the end of the string or not
     *
     * @return the llvm code
     */
    public static String printErrorString(InstrCounter counter, String content, boolean addEndLine) {
        // +1 for \00 as we use C strings
        int nbChars = content.length() + 1;
        if (addEndLine) {
            nbChars++;
        }

        // in llvm, strings are represented as char arrays, thus of type [a * i8] where a is the number of characters
        String type = "[" + nbChars + " x i8]";
        String llvmString = "c\"" + content;
        if (addEndLine) {

            // \0a is the code of a new line
            llvmString += "\\0a";
        }

        // all llvm strings should end with a null character (or at least the ones we use)
        llvmString += "\\00\"";
        String toPrintId = counter.getNextLlvmId();
        String toPrintPtrId = counter.getNextLlvmId();

        // we store the result of the call to printf even if we don't use it to avoid conflicts in counting
        String unusedPrintfReturnId = counter.getNextLlvmId();

        // first allocate a variable on the stack to store the string and then store the string in it
        return stackAllocation(toPrintId, type) +
                store(type, llvmString, toPrintId) +

                // get a pointer on the string to print, to pass to the printf method
                toPrintPtrId + " = " + LLVMKeywords.GETPTR.getLlvmName() + " " +
                LLVMKeywords.INBOUNDS.getLlvmName() + " " + type + ", " + type + "* " +
                toPrintId + ", " + LLVMTypes.INT32.getLlvmName() + " 0, " +
                LLVMTypes.INT32.getLlvmName() + " 0" + endLine +

                // call printf
                unusedPrintfReturnId + " = " + LLVMKeywords.CALL.getLlvmName() +
                " " + LLVMTypes.PRINTF.getLlvmName() + " " + LLVMExternalFunctions.PRINTF.getLlvmName() +
                "(" + LLVMTypes.STRING.getLlvmName() + " " + toPrintPtrId + ")" + endLine;
    }

    /**
     * Returns the llvm code that calls the exit function with the given exit code
     *
     * @param exitCode the exit code
     *
     * @return the llvm code
     */
    public static String exit(int exitCode) {
        return "call void @exit(i32 " + exitCode + ")" + endLine;
    }

    /**
     * Returns the llvm code that returns the default value for the given type
     *
     * @param type the VSOP type
     *
     * @return the llvm code
     */
    public static String returnDefault(Type type) {
        return "ret " + VSOPTypes.getLlvmTypeName(type.getName()) + " " +
                VSOPTypes.getLlvmDefaultInit(type.getName()) + endLine;
    }
}
