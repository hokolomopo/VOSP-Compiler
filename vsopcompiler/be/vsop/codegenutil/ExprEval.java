package be.vsop.codegenutil;

/**
 * This class is used to return a String representing a llvm code to generate the value of an expression,
 * with a String representing the llvm id to use to access the result of the expression
 */
public class ExprEval {
    public String llvmId;
    public String llvmCode;

    private boolean isLiteral = false;

    /**
     * Creates a new ExprEval from an id and a llvm code
     *
     * @param llvmId the id
     * @param llvmCode the code
     */
    public ExprEval(String llvmId, String llvmCode) {
        this.llvmId = llvmId;
        this.llvmCode = llvmCode;
    }

    /**
     * Creates a new ExprEval from an id and a llvm code
     *
     * @param llvmId the id
     * @param llvmCode the code
     * @param isLiteral whether this instance represents a literal or not
     */
    public ExprEval(String llvmId, String llvmCode, boolean isLiteral) {
        this(llvmId, llvmCode);
        this.isLiteral = isLiteral;
    }

    /**
     * whether this instance represents a literal or not
     *
     * @return true if this instance represents a literal, false otherwise
     */
    public boolean isLiteral() {
        return isLiteral;
    }
}
