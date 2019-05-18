package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.LLVMTypes;
import be.vsop.semantic.VSOPTypes;

public abstract class Expr extends ASTNode{
    protected String typeName;
    protected boolean withTypes;


    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        this.withTypes = withTypes;
        print(tabLevel, doTab);
        if (withTypes) {
            System.out.print(" : " + typeName);
        }
    }

    public abstract void print(int tabLevel, boolean doTab);

    public String getTypeName() {
        return typeName;
    }

    protected boolean isLlvmLiteral(){
        return false;
    }

    @Override
    public String getLlvm(InstrCounter counter) {
        return evalExpr(counter, typeName).llvmCode;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public abstract ExprEval evalExpr(InstrCounter counter, String expectedType);

    public String getLlvmTypeName(boolean pointerOnClass) {
        return VSOPTypes.getLlvmTypeName(typeName, pointerOnClass);
    }

    /**
     * Cast an ExprEval in llvm
     *
     * @param eval the ExprEval to cast
     * @param realType the type of the ExprEval
     * @param expectedType the type we want to cast to. null means anything is fine
     * @param counter the InstrCounter
     * @return the input ExprEval with a cast added
     */
    protected ExprEval castEval(ExprEval eval, String realType, String expectedType, InstrCounter counter){

        //ExpectedType to null means anything is fine
        if(expectedType == null)
            return eval;

        if(!realType.equals(expectedType)) {
            ExprEval cast = castExpr(realType, expectedType, eval.llvmId, counter);
            eval.llvmCode += cast.llvmCode;
            eval.llvmId = cast.llvmId;
        }

        return eval;
    }

    protected ExprEval castExpr(String originalType, String finalType, String llvmId, InstrCounter counter){
        StringBuilder llvm = new StringBuilder();

        String intPointer = counter.getNextLlvmId();
        String pointerNewType = counter.getNextLlvmId();

        // First, cast the pointer to the current object into an int, using the ptrtoint function of llvm
        // We use i64 because an i32 could overflow on most current machines
        llvm.append(llvmCast(intPointer, LLVMKeywords.PTRTOINT, VSOPTypes.getLlvmTypeName(originalType, true),
                LLVMTypes.INT64, llvmId));

        // Then, cast the obtained int into a new pointer (using inttoptr), giving it the new type
        llvm.append(llvmCast(pointerNewType, LLVMKeywords.INTTOPTR, LLVMTypes.INT64,
                VSOPTypes.getLlvmTypeName(finalType, true), intPointer));
        return new ExprEval(pointerNewType,llvm.toString());

    }

    boolean isUnit() {
        return typeName.equals(VSOPTypes.UNIT.getName());
    }

//    /**
//     * Evaluate an expressiona nd return a pair (id of variable in llvm, llvm code of evaluation)
//     *
//     * @param expr the expression to evaluate
//     * @param counter the InstrCounter
//     * @return The pair (id of expr in llvm, llvm code of evaluation of expr)
//     */
//    protected static ExprEval evaluateExpression(Expr expr, InstrCounter counter){
//        String exprLlvm = "";
//        String exprResult;
//        if(expr.isLlvmLiteral())
//            exprResult = expr.getLlvm(counter);
//        else{
//            exprLlvm = expr.getLlvm(counter)+ "\n";
//            exprResult = counter.getLastLlvmId();
//        }
//
//        return new ExprEval(exprResult, exprLlvm);
//    }
}