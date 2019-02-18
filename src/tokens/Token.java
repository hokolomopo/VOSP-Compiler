package tokens;

import java.util.HashMap;

import static tokens.TokenType.*;

public class Token{
    public enum Tokens {
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

        //Operators
        LBRACE("{", SYMBOL, "lbrace"),
        RBRACE("}", SYMBOL, "rbrace"),
        LPAR("(", SYMBOL, "lpar"),
        RPAR(")", SYMBOL, "rpar"),
        COLON(":", SYMBOL, "colon"),
        SEMICOLON(";", SYMBOL, "semicolon"),
        COMMA(",", SYMBOL, "comma"),
        PLUS("+", SYMBOL, "plus"),
        MINUS("-", SYMBOL, "minus"),
        TIMES("*", SYMBOL, "times"),
        DIV("/", SYMBOL, "div"),
        POW("^", SYMBOL, "pow"),
        DOT(".", SYMBOL, "dot"),
        EQUAL("=", SYMBOL, "equal"),
        LOWER("<", SYMBOL, "lower"),
        LOWER_EQUAL("<=", SYMBOL, "lower-equal"),
        ASSIGN("<-", SYMBOL, "assign"),

        //Others
        INT_LITERAL(null, LITERAL, "integer-literal"),
        STRING_LITERAL(null, LITERAL, "string-literal"),
        IDENTIFIER(null, ID, "object-identifier"),
        TYPE_IDENTIFIER(null, ID, "type-identifier");

        private String stringValue;
        private TokenType tokenType;
        private String name;

        Tokens(String stringValue, TokenType tokenType) {
            this.stringValue = stringValue;
            this.tokenType = tokenType;
            this.name = stringValue;
        }

        Tokens(String stringValue, TokenType tokenType, String name) {
            this.stringValue = stringValue;
            this.tokenType = tokenType;
            this.name = name;
        }

        public static HashMap<String, Tokens> getKeywordsHashMap(){
            HashMap<String, Tokens> map = new HashMap<>();

            for(Tokens t : Tokens.values())
                if(t.tokenType == KEYWORD)
                    map.put(t.stringValue, t);
            return map;
        }
        
        public static HashMap<String, Tokens> getOperatorsHashMap(){
            HashMap<String, Tokens> map = new HashMap<>();

            for(Tokens t : Tokens.values())
                if(t.tokenType == SYMBOL)
                    map.put(t.stringValue, t);
            return map;
        }

        public String getName() {
            return name;
        }
    }

    private Tokens tokenType;
    private String value;
    private int line;	// Useful for instance in case of multi-line strings
    private int column;
    
    public Token(Tokens tokenType, int line, int column) {
    	this.tokenType = tokenType;
    	this.line = line;
    	this.column = column;
    }
    
    public Token(Tokens tokenType, String value, int line, int column) {
    	this(tokenType, line, column);
    	this.value = "," + value;
    }

    public Tokens getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }
    
    public int getLine() {
    	return line + 1;
    }
    
    public int getColumn() {
    	return column + 1;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
