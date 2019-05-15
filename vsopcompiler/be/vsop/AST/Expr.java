package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.semantic.ScopeTable;
import javafx.util.Pair;

import java.util.ArrayList;

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
        return evalExpr(counter).llvmCode;
    }

    public abstract ExprEval evalExpr(InstrCounter counter);

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