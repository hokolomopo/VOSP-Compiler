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
        LBRACE("{", OPERATOR),
        RBRACE("}", OPERATOR),
        LPAR("(", OPERATOR),
        RPAR(")", OPERATOR),
        COLON(":", OPERATOR),
        SEMICOLON(";", OPERATOR),
        COMMA(",", OPERATOR),
        PLUS("+", OPERATOR),
        MINUS("-", OPERATOR),
        TIMES("*", OPERATOR),
        DIV("/", OPERATOR),
        POW("^", OPERATOR),
        DOT(".", OPERATOR),
        EQUAL("=", OPERATOR),
        LOWER("<", OPERATOR),
        LOWER_EQUAL("<=", OPERATOR),
        ASSIGN("<-", OPERATOR),

        //Others
        INT_LITERAL(null, LITERAL),//TODO : Mettre la regex comme string, utile?
        HEXA_LITERAL(null, LITERAL),
        BIN_LITERAL(null, LITERAL),
        STRING_LITERAL(null, LITERAL),
        IDENTIFIER(null, ID),
        TYPE_IDENTIFIER(null, ID);

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
        
        public static HashMap<String, Tokens> getOperatorsHashMap(){
            HashMap<String, Tokens> map = new HashMap<>();

            for(Tokens t : Tokens.values())
                if(t.tokenType == OPERATOR)
                    map.put(t.stringValue, t);
            return map;
        }
    }

    private Tokens tokenType;
    private String value;
    private int line;	// Useful for instance in case of multi-line strings
    private int column;
    
    public Token(Tokens tokenType, int line, int column) {
    	this.tokenType = tokenType;
        this.value = "";
    	this.line = line;
    	this.column = column;
    }
    
    public Token(Tokens tokenType, String value, int line, int column) {
    	if (tokenType == Tokens.HEXA_LITERAL) {
        	this.value = "," + Integer.parseInt(value, 16);
        	this.tokenType = Tokens.INT_LITERAL;
        } else if (tokenType == Tokens.BIN_LITERAL) {
        	this.value = "," + Integer.parseInt(value, 2);
        	this.tokenType = Tokens.INT_LITERAL;
        } else {
        	this.tokenType = tokenType;
        	this.value = "," + value;
        }
    	
    	this.line = line;
    	this.column = column;
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
