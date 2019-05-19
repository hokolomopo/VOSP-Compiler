package be.vsop;

import be.vsop.AST.ClassItem;
import be.vsop.AST.ClassList;
import be.vsop.AST.Program;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.codegenutil.MethodCounter;
import be.vsop.exceptions.LexerException;
import be.vsop.exceptions.ParserException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.lexer.VSOPLexer;
import be.vsop.parser.VSOPParser;
import be.vsop.parser.VSOPScanner;
import be.vsop.semantic.SemanticAnalyzer;
import java_cup.runtime.ComplexSymbolFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    private String fileName;
    private String languageDirPath;
    private Program program;

    public Compiler(String fileName) {
        this(fileName, ".");
    }

    public Compiler(String fileName, String languageDirPath) {
        this.fileName = fileName;
        this.languageDirPath = languageDirPath;
    }

    public Program buildAST(){
        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.err.println("In Compiler.java buildAST() : file " + fileName + " not found.");
            System.exit(-1);
        }

        VSOPLexer lexer = new VSOPLexer(reader);
        ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();

        VSOPScanner scanner = new VSOPScanner(lexer, symbolFactory);
        VSOPParser parser = new VSOPParser(scanner, symbolFactory);
        parser.init(scanner, symbolFactory);

        try {
            parser.parse();
        }catch (LexerException e){
            System.err.println(fileName+":"+e.getLine()+":"+e.getColumn()+": lexical error :" + e.getMessage());
            System.exit(-1);
        }catch (ParserException e) {
            System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        }catch (IOException e){
            System.err.println("IOException during lexing in " + fileName);
            System.exit(-1);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        this.program = new Program((ClassList) parser.getTree());
        return this.program;
    }

    void doSemanticAnalysis(Program program, String languageDirPath) {
        doSemanticAnalysis(program, languageDirPath, true);
    }

    void doSemanticAnalysis(Program program, String languageDirPath, boolean print) {
        if (program == null) {
            if (this.program == null) {
                this.program = buildAST();
            }
        } else {
            this.program = program;
        }
        SemanticAnalyzer sa = new SemanticAnalyzer(this.program, languageDirPath);
        sa.analyze();

        if(sa.hasError()) {
            for (SemanticException e : sa.getErrors())
                System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        }

        if(print)
            this.program.print(true);
    }


    String generateLlvm() {
        //Set number to methods to be able to load them from the vtable
        new MethodCounter(program.getClassTable()).setupMethods();

        StringBuilder classDeclarations = new StringBuilder();

        //Prepare classes for llvm (add self to methods, numerate fields, ...)
        for(ClassItem classItem : program.getClassTable().values())
            classItem.prepareForLlvm();

        for(ClassItem classItem : program.getClassTable().values())
            classDeclarations.append(classItem.getClassDeclaration());

            return classDeclarations.toString() + writeIOCode() + this.program.getLlvm(new InstrCounter());
    }

    public Program getAST(){
        return program;
    }

    private String writeIOCode() {
        try {
            return new String(Files.readAllBytes(Paths.get(languageDirPath + "/language/llcode.ll")), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("In Compiler.java writeIOCode : file language/llcode.ll not found (containing implementation of IO's functions");
            System.exit(-1);
            // IDE wants a return there
            return "";
        }
    }

}