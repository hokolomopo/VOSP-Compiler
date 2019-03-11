import AST.ASTNode;
import exceptions.LexerError;
import exceptions.ParserError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java_cup.runtime.SymbolFactory;
import jdk.nashorn.internal.runtime.ParserException;
import lexer.VSOPLexer;
import parser.VSOPParser;
import parser.VSOPScanner;
import tokens.Token;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ParserMain {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Wrong Arguments : \n1) -lex for lexer usage \n2) path to the file to parse.");
            System.exit(-1);
        }

        String fileName = args[1];
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
            ASTNode tree = parser.getTree();
            tree.print();
            System.out.println();
        } catch (ParserError e) {
            System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
