package be.vsop.codegenutil;

/**
 * This enumeration contains the different llvm keywords that our program uses
 */
public enum LLVMKeywords {
    INBOUNDS("inbounds"),
    GETPTR("getelementptr"),
    RET("ret"),
    DEFINE("define"),
    PHI("phi"),
    STORE("store"),
    ALLOCATE("alloca"),
    LABEL("label"),
    BRANCH("br"),
    CALL("call"),
    TO("to"),
    INTTOPTR("inttoptr"),
    PTRTOINT("ptrtoint"),
    FLOATTOINT("fptosi"),
    INTTOFLOAT("sitofp"),
    EQ("icmp eq"),
    LOWER("icmp slt"),
    LOWEREQ("icmp sle"),
    ADD("add"),
    SUB("sub"),
    MUL("mul"),
    DIV("div");

    private String llvmName;

    /**
     * Creates a new LLVMKeywords from the given name
     *
     * @param llvmName the name
     */
    LLVMKeywords(String llvmName) {
        this.llvmName = llvmName;
    }

    /**
     * Returns the llvm name of this LLVMKeywords
     *
     * @return the name
     */
    public String getLlvmName() {
        return llvmName;
    }
}
