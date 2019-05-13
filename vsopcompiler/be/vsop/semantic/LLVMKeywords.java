package be.vsop.semantic;

public enum LLVMKeywords {
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

    LLVMKeywords(String llvmName) {
        this.llvmName = llvmName;
    }

    public String getLlvmName() {
        return llvmName;
    }
}
