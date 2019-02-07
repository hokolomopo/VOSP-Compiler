package tokens;

import java.util.HashMap;

import static tokens.TokenType.*;

public class Token{
    public enum Tokens {
        //TODO : Complete enum
        WHILE("while", KEYWORD),

        OPEN_BRACKET("(", SYMBOL),

        PLUS("+", OPERATOR),

        INTEGER_CONSTANT(null, CONSTANT),//TODO : Mettre la regex comme string, utile?
        STRING_CONSTANT(null, CONSTANT),
        IDENTIFIER(null, ID);

        private String stringValue;
        private TokenType tokenType;

        Tokens(String stringValue, TokenType tokenType) {
            this.stringValue = stringValue;
            this.tokenType = tokenType;
        }

        public static HashMap<String, Tokens> getKeywordsHashMap(){
            HashMap<String, Tokens> map = new HashMap<>();

            for(Tokens t : Tokens.values())
                if(t.tokenType == KEYWORD)
                    map.put(t.stringValue, t);
            return map;
        }
    }

    private Tokens tokenType;
    private String value;

    public Token(Tokens tokenType) {
        this.tokenType = tokenType;
    }

    public Token(Tokens tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public Tokens getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
