import exceptions.LexerError;
import tokens.Token;

import java.io.FileReader;
import java.io.IOException;

public class lexerMain {

    public static void main(String[] args) throws IOException {

    	if (args.length < 2)
    		throw new IllegalArgumentException("Arguments : \n1) -lex for lexer usage \n2) path to the file to parse.");

    	String fileName = args[1];
        FileReader reader = new FileReader(fileName);

        VSOPLexer lexer = new VSOPLexer(reader);

        try {

            while (true) {
                Token t = lexer.yylex();
                if (t == null)
                    break;

                if (t.getValue() != null)
                    System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName() + t.getValue());
                else
                    System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName());

            }
        }catch (LexerError e){
            System.err.println(fileName+"::"+e.getLine()+"::"+e.getColumn()+": lexical error ");
        }
    }
}
