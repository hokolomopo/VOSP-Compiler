import AST.ASTNode;
import exceptions.LexerError;
import exceptions.ParserError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.SymbolFactory;
import lexer.VSOPLexer;
import parser.VSOPParser;
import parser.VSOPScanner;
import tokens.Token;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class vsopc {

    public static void main(String[] args) {

    	if (args.length < 2) {
            System.err.println("Wrong Arguments :\n1) -lex for lexer usage, -parser for parser usage\n2) path to the file to parse.");
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

        //TODO use compiler class
    	if (args[0].equals("-lex")) {
            VSOPLexer lexer = new VSOPLexer(reader);

            try {
                while (true) {
                    Token t = lexer.yylex();
                    if (t == null)
                        break;

                    if(t.getTokenType() == Token.Tokens.STRING_LITERAL)
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName() + "," +
                                convertToEscapeSymbols(t.getValue()));
                    else if (t.getValue() != null)
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName() + "," + t.getValue());
                    else
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName());

                }
            }catch (LexerError e){
                System.err.println(fileName+":"+e.getLine()+":"+e.getColumn()+": lexical error :" + e.getMessage());
                System.exit(-1);
            }catch (IOException e){
                System.err.println("IOException during lexing in " + fileName);
                System.exit(-1);
            }
            System.exit(0);
    	}
    	
    	if (args[0].contentEquals("-parse")) {
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

    private static String convertToEscapeSymbols(String str){
        String result = str;
        result = result.replace("\\b", "\\x08");
        result = result.replace("\\f", "\\x0c");
        result = result.replace("\\n", "\\x0a");
        result = result.replace("\\r", "\\x0d");
        result = result.replace("\\t", "\\x09");
        result = result.replace("\\v", "\\x0b");

        return result;
    }

}
