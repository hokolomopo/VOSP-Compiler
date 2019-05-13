package be.vsop.semantic;

public enum LLVMIntrinsics {
    MALLOC("@malloc"),
    POW("@llvm.powi.f32");

    private String llvmName;

    LLVMIntrinsics(String llvmName) {
        this.llvmName = llvmName;
    }

    public String getLlvmName() {
        return llvmName;
    }
}
