package be.vsop;

import be.vsop.AST.Program;

import java.io.FileWriter;
import java.io.IOException;

public class TestMain {
    public static void main(String[] args) {
        String fileName = "llvm/testp.vsop";
        Compiler compiler = new Compiler(fileName);
        Program program = compiler.buildAST();
        System.out.println();
        compiler.doSemanticAnalysis(null, null);
        String llvm = compiler.generateLlvm();
        System.out.println(llvm);

        FileWriter writer;
        try {
            writer = new FileWriter("llvm/result.ll", false);
            writer.write(llvm);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
