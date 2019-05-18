package be.vsop.codegenutil;

import be.vsop.AST.Expr;
import be.vsop.AST.Formal;

public class LlvmVar {
    public String typeName;
    public String llvmId;

    public LlvmVar(String llvmId, String typeName) {
        this.typeName = typeName;
        this.llvmId = llvmId;
    }

    public LlvmVar(String llvmId, Expr expr) {
        this.typeName = expr.getTypeName();
        this.llvmId = llvmId;
    }


}
