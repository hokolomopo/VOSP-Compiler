package be.vsop.semantic;

public enum LLVMExternalFunctions {
    PRINTF("@printf"),
    MALLOC("@malloc"),
    POW("@llvm.powi.f32");

    private String llvmName;

    LLVMExternalFunctions(String llvmName) {
        this.llvmName = llvmName;
    }

    public String getLlvmName() {
        return llvmName;
    }
}
