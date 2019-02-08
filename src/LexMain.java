import tokens.Token;

import java.io.FileReader;
import java.io.IOException;

public class LexMain {

    public static void main(String[] args) throws IOException {

        FileReader reader = new FileReader("testFiles/simple.vsop");

        VSOPLexer lexer = new VSOPLexer(reader);

        while(true) {
            Token t = lexer.yylex();
            if(t == null)
                break;

            System.out.println(t.getTokenType() + "  " + t.getValue());
        }
    }
}
