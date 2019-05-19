package be.vsop.codegenutil;

/**
 * This enumeration contains the different llvm types that our program uses
 */
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

    /**
     * Creates a new LLVMTypes from the given name
     *
     * @param llvmName the name
     */
    LLVMTypes(String llvmName) {
        this.llvmName = llvmName;
    }

    /**
     * Returns the llvm name of this LLVMTypes
     *
     * @return the name
     */
    public String getLlvmName() {
        return llvmName;
    }
}
