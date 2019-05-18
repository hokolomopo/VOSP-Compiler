package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LlvmWrappers;

import java.util.ArrayList;

public class New extends Expr {
    private Type type;

    public New(Type type) {
        this.type = type;
        typeName = type.getName();
        children = new ArrayList<>();
        children.add(this.type);
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("New(" + type.getName() + ")");
    }

    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        String llvmId = counter.getNextLlvmId();
        String llvm = LlvmWrappers.callNew(llvmId, typeName);

        return new ExprEval(llvmId, llvm);
    }

}