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

    public Program getAST(){
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

        ClassList l = (ClassList) parser.getTree();

        return new Program(l);
    }

    public void doSemanticAnalysis(Program program){
        this.program = program;
        this.doSemanticAnalysis();
    }

    public void doSemanticAnalysis(){
        if(this.program == null)
            this.program = (Program)getAST();

        SyntaxAnalyzer sa = new SyntaxAnalyzer(program);
        sa.analyze();

        if(sa.hasError()) {
            for (SemanticException e : sa.getErrors())
                System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        }
    }

}
