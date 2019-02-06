import java.io.FileReader;
import java.io.IOException;

public class LexMain {

    public static void main(String[] args) throws IOException {

        FileReader reader = new FileReader("testFiles/factorial.vsop");

        VSOPLexer lexer = new VSOPLexer(reader);

        lexer.yylex();
    }
}
