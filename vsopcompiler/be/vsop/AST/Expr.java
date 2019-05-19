package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.LLVMTypes;
import be.vsop.semantic.LlvmWrappers;
import be.vsop.semantic.VSOPTypes;

public abstract class Expr extends ASTNode{
    protected String typeName;
    protected boolean withTypes;

    /**
     * See ASTNode, this is used to add the types to the print without modifying each child
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        this.withTypes = withTypes;
        print(tabLevel, doTab);
        if (withTypes) {
            System.out.print(" : " + typeName);
        }
    }

    /**
     * See ASTNode
     */
    public abstract void print(int tabLevel, boolean doTab);

    public String getTypeName() {
        return typeName;
    }


    /**
     * See ASTNode
     */
    @Override
    public String getLlvm(InstrCounter counter) {
        return evalExpr(counter, typeName).llvmCode;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * Get the llvm code of an expression
     *
     * @param counter an InstrCounter
     * @param expectedType the type we want the expression to return
     * @return the ExprEval of the evaluation of the expression
     */
    public abstract ExprEval evalExpr(InstrCounter counter, String expectedType);

    /**
     * Cast an ExprEval in llvm
     * If any of the types are null or the types are equals, this will do nothing.
     *
     * @param eval the ExprEval to cast
     * @param originalType the type of the ExprEval
     * @param expectedType the type we want to cast to
     * @param counter the InstrCounter
     * @return the input ExprEval with a cast added
     */
    protected ExprEval castEval(ExprEval eval, String originalType, String expectedType, InstrCounter counter){

        //ExpectedType to null means anything is fine
        if(expectedType == null || originalType == null)
            return eval;

        if(!originalType.equals(expectedType)) {
            ExprEval cast = castExpr(originalType, expectedType, eval.llvmId, counter);
            eval.llvmCode += cast.llvmCode;
            eval.llvmId = cast.llvmId;
        }

        return eval;
    }

    /**
     * Cast an expression into another type in llvm.
     *
     * @param originalType the original type of the expression
     * @param finalType the type to cast into
     * @param llvmId the llvm register of the expression
     * @param counter an InstrCounter
     * @return the ExprEval of the cast
     */
    public static ExprEval castExpr(String originalType, String finalType, String llvmId, InstrCounter counter){
        StringBuilder llvm = new StringBuilder();

        String intPointer = counter.getNextLlvmId();
        String pointerNewType = counter.getNextLlvmId();

        // First, cast the pointer to the current object into an int, using the ptrtoint function of llvm
        // We use i64 because an i32 could overflow on most current machines
        llvm.append(LlvmWrappers.llvmCast(intPointer, LLVMKeywords.PTRTOINT, VSOPTypes.getLlvmTypeName(originalType, true),
                LLVMTypes.INT64, llvmId));

        // Then, cast the obtained int into a new pointer (using inttoptr), giving it the new type
        llvm.append(LlvmWrappers.llvmCast(pointerNewType, LLVMKeywords.INTTOPTR, LLVMTypes.INT64,
                VSOPTypes.getLlvmTypeName(finalType, true), intPointer));
        return new ExprEval(pointerNewType,llvm.toString());

    }

    /**
     * @return true if the expression type is unit, false otherwise
     */
    boolean isUnit() {
        return typeName.equals(VSOPTypes.UNIT.getName());
    }

}