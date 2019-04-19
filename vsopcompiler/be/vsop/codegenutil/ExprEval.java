package be.vsop.codegenutil;

public class ExprEval {
    public String llvmId;
    public String llvmCode;

    private boolean isLiteral = false;

    public ExprEval(String llvmId, String llvmCode) {
        this.llvmId = llvmId;
        this.llvmCode = llvmCode;
    }

    public ExprEval(String llvmId, String llvmCode, boolean isLiteral) {
        this(llvmId, llvmCode);
        this.isLiteral = isLiteral;
    }

    public boolean isLiteral() {
        return isLiteral;
    }
}
