package be.vsop.codegenutil;

public enum LLVMTypes {
    FLOAT("float"),
    BOOL("i1"),
    STRING("i8*"),
    INT32("i32"),
    INT64("i64"),
    NULL("null"),
    UNIT("void"),
    PRINTF("i32 (i8*, ...)");

    private String llvmName;

    LLVMTypes(String llvmName) {
        this.llvmName = llvmName;
    }

    public String getLlvmName() {
        return llvmName;
    }
}
