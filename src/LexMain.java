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
        //TODO : counting lines/columns : useful to keep it ?(ajouter attributs au Token), ou juste le faire ici à l'arrache
            System.out.println(t.getTokenType() + "  " + t.getValue());
        }
    }
}
