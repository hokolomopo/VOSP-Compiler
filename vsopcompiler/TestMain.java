import AST.ASTNode;
import exceptions.ParserError;
import exceptions.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import lexer.VSOPLexer;
import parser.VSOPParser;
import parser.VSOPScanner;
import syntax.SyntaxAnalyzer;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class TestMain {
    public static void main(String[] args) {
        String fileName = "vsopcompiler/tests/test.vsop";

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

            SyntaxAnalyzer sa = new SyntaxAnalyzer(tree);
            sa.analyze();

            if(sa.hasError()){
                for(SemanticError e : sa.getErrors())
                    System.err.println(fileName + ":" + e.getMessage());
                System.exit(-1);
            }

        } catch (ParserError e) {
            System.err.println(fileName + ":" + e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
