import tokens.Token;

import java.io.FileReader;
import java.io.IOException;

public class LexMain {

    public static void main(String[] args) throws IOException {
    	
    	if (args.length < 1) 
    		throw new IllegalArgumentException("Missing argument : path to the file to parse");

        FileReader reader = new FileReader(args[0]);

        VSOPLexer lexer = new VSOPLexer(reader);

        while(true) {
            Token t = lexer.yylex();
            if(t == null)
                break;
            System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType() + t.getValue());
        }
    }
}
