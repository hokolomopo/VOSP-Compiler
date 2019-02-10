package tokens;

import java.util.HashMap;

import static tokens.TokenType.*;

public class Token{
    public enum Tokens {
        //TODO : Complete enum
        //Keywords
        AND("and", KEYWORD),
        BOOL("bool", KEYWORD),
        CLASS("class", KEYWORD),
        DO("do", KEYWORD),
        ELSE("else", KEYWORD),
        EXTENDS("extends", KEYWORD),
        FALSE("false", KEYWORD),
        IF("if", KEYWORD),
        IN("in", KEYWORD),
        INT32("int32", KEYWORD),
        ISNULL("isnull", KEYWORD),
        LET("let", KEYWORD),
        NEW("new", KEYWORD),
        NOT("not", KEYWORD),
        STRING("string", KEYWORD),
        THEN("then", KEYWORD),
        TRUE("true", KEYWORD),
        UNIT("unit", KEYWORD),
        WHILE("while", KEYWORD),


        OPEN_BRACKET("(", SYMBOL),

        PLUS("+", OPERATOR),

        INT_LITERAL(null, LITERAL),//TODO : Mettre la regex comme string, utile?
        HEXA_LITERAL(null, LITERAL),
        BIN_LITERAL(null, LITERAL),
        STRING_LITERAL(null, LITERAL),
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
