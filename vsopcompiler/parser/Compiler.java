package parser;

import AST.ASTNode;
import exceptions.LexerError;
import exceptions.ParserError;
import java_cup.runtime.ComplexSymbolFactory;
import lexer.VSOPLexer;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Compiler {
    private String fileName;

    public Compiler(String fileName) {
        this.fileName = fileName;
    }

    public ASTNode getAST(){
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
        } catch (ParserError e) {
            System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        }catch (LexerError e){
            System.err.println(fileName+":"+e.getLine()+":"+e.getColumn()+": lexical error :" + e.getMessage());
            System.exit(-1);
        }catch (IOException e){
            System.err.println("IOException during lexing in " + fileName);
            System.exit(-1);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        return parser.getTree();
    }
}
