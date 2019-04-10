package be.vsop;

import be.vsop.AST.ClassList;
import be.vsop.AST.Program;
import be.vsop.exceptions.LexerException;
import be.vsop.exceptions.ParserException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.lexer.VSOPLexer;
import be.vsop.parser.VSOPParser;
import be.vsop.parser.VSOPScanner;
import be.vsop.semantic.SyntaxAnalyzer;
import java_cup.runtime.ComplexSymbolFactory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Compiler {
    private String fileName;
    private Program program;

    public Compiler(String fileName) {
        this.fileName = fileName;
    }

    public Program buildAST(){
        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.err.println("File " + fileName + " not found.");
            System.exit(-1);
        }

        VSOPLexer lexer = new VSOPLexer(reader);
        ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();

        VSOPScanner scanner = new VSOPScanner(lexer, symbolFactory);
        VSOPParser parser = new VSOPParser(scanner, symbolFactory);
        parser.init(scanner, symbolFactory);

        try {
            parser.parse();
        } catch (ParserException e) {
            System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        }catch (LexerException e){
            System.err.println(fileName+":"+e.getLine()+":"+e.getColumn()+": lexical error :" + e.getMessage());
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

    void doSemanticAnalysis(Program program) {
        //TODO do we really need the argument?
        if (program == null) {
            if (this.program == null) {
                this.program = buildAST();
            }
        } else {
            this.program = program;
        }
        SyntaxAnalyzer sa = new SyntaxAnalyzer(this.program);
        sa.analyze();

        if(sa.hasError()) {
            for (SemanticException e : sa.getErrors())
                System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        }
        this.program.print(true);
    }

    public Program getAST(){
        return program;
    }

}