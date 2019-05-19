package be.vsop.codegenutil;

/**
 * This enumeration contains the different external functions (also called intrinsics) of llvm that our program uses
 */
public enum LLVMExternalFunctions {
    PRINTF("@printf"),
    MALLOC("@malloc"),
    POW("@llvm.powi.f32");

    private String llvmName;

    /**
     * Creates a new LLVMExternalFunctions from the given name
     *
     * @param llvmName the name
     */
    LLVMExternalFunctions(String llvmName) {
        this.llvmName = llvmName;
    }

    /**
     * Returns the llvm name of this LLVMExternalFunctions
     *
     * @return the name
     */
    public String getLlvmName() {
        return llvmName;
    }
}
