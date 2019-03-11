package tokens;

import exceptions.LexerError;

import java.util.ArrayList;
import java.util.HashMap;
import parser.sym;

import static tokens.TokenType.*;

public class Token{
    public enum Tokens {
        //Keywords
        AND("and", KEYWORD, sym.AND),
        BOOL("bool", KEYWORD, sym.BOOL),
        CLASS("class", KEYWORD, sym.CLASS),
        DO("do", KEYWORD, sym.DO),
        ELSE("else", KEYWORD, sym.ELSE),
        EXTENDS("extends", KEYWORD, sym.EXTENDS),
        FALSE("false", KEYWORD, sym.FALSE),
        IF("if", KEYWORD, sym.IF),
        IN("in", KEYWORD, sym.IN),
        INT32("int32", KEYWORD, sym.INT32),
        ISNULL("isnull", KEYWORD, sym.ISNULL),
        LET("let", KEYWORD, sym.LET),
        NEW("new", KEYWORD, sym.NEW),
        NOT("not", KEYWORD, sym.NOT),
        STRING("string", KEYWORD, sym.STRING),
        THEN("then", KEYWORD, sym.THEN),
        TRUE("true", KEYWORD, sym.TRUE),
        UNIT("unit", KEYWORD, sym.UNIT),
        WHILE("while", KEYWORD, sym.WHILE),

        //Operators
        LBRACE("{", SYMBOL, "lbrace", sym.LBRACE),
        RBRACE("}", SYMBOL, "rbrace", sym.RBRACE),
        LPAR("(", SYMBOL, "lpar", sym.LPAR),
        RPAR(")", SYMBOL, "rpar", sym.RPAR),
        COLON(":", SYMBOL, "colon", sym.COLON),
        SEMICOLON(";", SYMBOL, "semicolon", sym.SEMICOLON),
        COMMA(",", SYMBOL, "comma", sym.COMMA),
        PLUS("+", SYMBOL, "plus", sym.PLUS),
        MINUS("-", SYMBOL, "minus", sym.MINUS),
        TIMES("*", SYMBOL, "times", sym.TIMES),
        DIV("/", SYMBOL, "div", sym.DIV),
        POW("^", SYMBOL, "pow", sym.POW),
        DOT(".", SYMBOL, "dot", sym.DOT),
        EQUAL("=", SYMBOL, "equal", sym.EQUAL),
        LOWER("<", SYMBOL, "lower", sym.LOWER),
        LOWER_EQUAL("<=", SYMBOL, "lower-equal", sym.LOWER_EQUAL),
        ASSIGN("<-", SYMBOL, "assign", sym.ASSIGN),

        //Others
        INT_LITERAL(null, LITERAL, "integer-literal", sym.INT_LITERAL),
        STRING_LITERAL(null, LITERAL, "string-literal", sym.STRING_LITERAL),
        IDENTIFIER(null, ID, "object-identifier", sym.IDENTIFIER),
        TYPE_IDENTIFIER(null, ID, "type-identifier", sym.TYPE_IDENTIFIER);

        private String stringValue;
        private TokenType tokenType;
        private String name;
        private int symbolValue;

        Tokens(String stringValue, TokenType tokenType, int symbolValue) {
            this.stringValue = stringValue;
            this.tokenType = tokenType;
            this.name = stringValue;
            this.symbolValue = symbolValue;
        }

        Tokens(String stringValue, TokenType tokenType, String name, int symbolValue) {
            this.stringValue = stringValue;
            this.tokenType = tokenType;
            this.name = name;
            this.symbolValue = symbolValue;
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

        public static Tokens fromValue(int value){
            for (Tokens t : Tokens.values())
                if(t.getSymbolValue() == value)
                    return t;
            return null;
        }

        public String getName() {
            return name;
        }

        public int getSymbolValue() {
            return symbolValue;
        }

        public String getStringValue() {
            return stringValue;
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
    	this.value = value;
    	if(tokenType == Tokens.STRING_LITERAL)
    	    this.value = convertEscapeSymbols(this.value);
    }

    /**
     * Convert printable escape sequences to their corresponding symbols in the string
     *
     * @param str the string
     * @return the string with escape sequences converted
     */
    private String convertEscapeSymbols(String str) {

        char lastChar = 'a';
        char curr;
        int i = 0;

        ArrayList<Integer> foundCodes = new ArrayList<>();
        try {

            //Find occurences of "\x" in string, and save the escape value found
            for(i = 0;i < str.length();i++){
                curr = str.charAt(i);

                if(curr == 'x' && lastChar == '\\'){
                    String strCode = "" + str.charAt(i+1) + str.charAt(i+2);
                    int unicode = Integer.parseInt(strCode, 16);

                    //Check if it's a printable character
                    if(unicode >= 32 && unicode <=126)
                        foundCodes.add(unicode);
                }
                lastChar = curr;
            }

            //Replace escape values found with their equivalent characters
            for(int code : foundCodes)
                str = str.replace("\\x" + Integer.toString(code, 16), ""+(char)code);

        }catch (Exception e){
            throw new LexerError("Illegal escape symbol", this.line, i);
        }

        return str;
    }

    public int getLength() {
        if(tokenType.stringValue == null)
            return value.length();
        return tokenType.stringValue.length();
    }


    public Tokens getTokenType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }
    
    public int getLine() {
    	return line;
    }
    
    public int getColumn() {
    	return column;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
