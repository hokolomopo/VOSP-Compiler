package be.vsop;

import be.vsop.AST.Program;

public class TestMain {
    public static void main(String[] args) {
        String fileName = "vsopcompiler/tests/test.vsop";
        Compiler compiler = new Compiler(fileName);
        Program program = compiler.buildAST();
        program.print();
        System.out.println();
        compiler.doSemanticAnalysis(program);
    }
}
