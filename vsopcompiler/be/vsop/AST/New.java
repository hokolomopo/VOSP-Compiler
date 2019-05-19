package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LlvmWrappers;

import java.util.ArrayList;

/**
 * This class represents the VSOP new operator
 */
public class New extends Expr {
    private Type type;

    /**
     * Creates a new New of the given type
     *
     * @param type the type of the class that is being instantiated
     */
    public New(Type type) {
        this.type = type;
        typeName = type.getName();
        children = new ArrayList<>();
        children.add(this.type);
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
    }

    /**
     * See ASTNode, a New is printed as New(type)
     */
    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("New(" + type.getName() + ")");
    }

    /**
     * See Expr
     */
    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        String llvmId = counter.getNextLlvmId();
        String llvm = LlvmWrappers.callNew(llvmId, typeName);

        ExprEval eval = new ExprEval(llvmId, llvm);
        return castEval(eval, typeName, expectedType, counter);
    }
}