package be.vsop.tokens;

import be.vsop.exceptions.LexerException;
import be.vsop.parser.sym;

import java.util.ArrayList;
import java.util.HashMap;

import static be.vsop.tokens.TokenType.*;

/**
 * This class is used by the lexer to output tokens
 */
public class Token{
    /**
     * This enumeration contains all the keywords of VSOP
     */
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
        EOF(null, SYMBOL, sym.EOF),

        //Others
        INT_LITERAL(null, LITERAL, "integer-literal", sym.INT_LITERAL),
        STRING_LITERAL(null, LITERAL, "string-literal", sym.STRING_LITERAL),
        IDENTIFIER(null, ID, "object-identifier", sym.IDENTIFIER),
        TYPE_IDENTIFIER(null, ID, "type-identifier", sym.TYPE_IDENTIFIER);

        private String stringValue;
        private TokenType tokenType;
        private String name;
        private int symbolValue;

        /**
         * Creates a Token with the given arguments, its name defaulting to its stringValue
         *
         * @param stringValue the value of the token (characters that need to be written in the input file
         *                    to output this token)
         * @param tokenType the type of the Token, see TokenType
         * @param symbolValue the symbol value for the cup parser
         */
        Tokens(String stringValue, TokenType tokenType, int symbolValue) {
            this.stringValue = stringValue;
            this.tokenType = tokenType;
            this.name = stringValue;
            this.symbolValue = symbolValue;
        }

        /**
         * Creates a Token with the given arguments
         *
         * @param stringValue the value of the token (characters that need to be written in the input file
         *                    to output this token)
         * @param tokenType the type of the Token, see TokenType
         * @param name the name of the token (for printing)
         * @param symbolValue the symbol value for the cup parser
         */
        Tokens(String stringValue, TokenType tokenType, String name, int symbolValue) {
            this.stringValue = stringValue;
            this.tokenType = tokenType;
            this.name = name;
            this.symbolValue = symbolValue;
        }

        /**
         * Returns the HashMap containing all the keywords (not all the tokens).
         *
         * @return a HashMap<String, Tokens> that takes as input the value of the token and returns the corresponding
         *         token object
         */
        public static HashMap<String, Tokens> getKeywordsHashMap(){
            HashMap<String, Tokens> map = new HashMap<>();

            for(Tokens t : Tokens.values())
                if(t.tokenType == KEYWORD)
                    map.put(t.stringValue, t);
            return map;
        }

        /**
         * Returns the HashMap containing all the operators (not all the tokens).
         *
         * @return a HashMap<String, Tokens> that takes as input the value of the token and returns the corresponding
         *         token object
         */
        public static HashMap<String, Tokens> getOperatorsHashMap(){
            HashMap<String, Tokens> map = new HashMap<>();

            for(Tokens t : Tokens.values())
                if(t.tokenType == SYMBOL)
                    map.put(t.stringValue, t);
            return map;
        }

        /**
         * Take a symbol value and returns the corresponding Token
         *
         * @param value the symbol value to search for
         *
         * @return the corresponding Token object, or null if not found
         */
        public static Tokens fromValue(int value){
            for (Tokens t : Tokens.values())
                if(t.getSymbolValue() == value)
                    return t;
            return null;
        }

        /**
         * Getter for the name of this Token (used for printing)
         *
         * @return the printable name
         */
        public String getName() {
            return name;
        }

        /**
         * Getter for the Symbol value of this Token (used to pass to the cup parser)
         *
         * @return the symbol value
         */
        public int getSymbolValue() {
            return symbolValue;
        }

        /**
         * Getter for the string value of this Token (characters that need to be written in the input file
         * to output this token)
         *
         * @return the string value
         */
        public String getStringValue() {
            return stringValue;
        }
    }

    private Tokens tokenType;
    private String value;
    private int line;	// Useful for instance in case of multi-line strings
    private int column;

    /**
     * Creates a new Token with the given type (see enum above), line and column
     *
     * @param tokenType the token type, which is a member of the above enum, be careful with the other enum named tokenType
     * @param line the line used for reporting errors
     * @param column the column used for reporting errors
     */
    public Token(Tokens tokenType, int line, int column) {
    	this.tokenType = tokenType;
    	this.line = line;
    	this.column = column;
    }

    /**
     * Creates a new Token with the given type (see enum above), value, line and column
     *
     * @param tokenType the token type, which is a member of the above enum, be careful with the other enum named tokenType
     * @param value the value
     * @param line the line used for reporting errors
     * @param column the column used for reporting errors
     */
    public Token(Tokens tokenType, String value, int line, int column) throws LexerException {
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
    private String convertEscapeSymbols(String str) throws LexerException {
        char lastChar = 'a';
        char curr;
        int i = 0;

        ArrayList<Integer> foundCodes = new ArrayList<>();
        try {

            //Find occurrences of "\x" in string, and save the escape value found
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
            throw new LexerException("Illegal escape symbol", this.line, i);
        }

        return str;
    }


    /**
     * Getter for the length of this Token
     *
     * @return the length of this Token
     */
    public int getLength() {
        if(tokenType.stringValue == null)
            return value.length();
        return tokenType.stringValue.length();
    }

    /**
     * Getter for the type of this Token (see above enum)
     *
     * @return the type
     */
    public Tokens getTokenType() {
        return tokenType;
    }

    /**
     * Getter for the value of this Token
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Getter for the line of this Token
     *
     * @return the line
     */
    public int getLine() {
    	return line;
    }

    /**
     * Getter for the column of this Token
     *
     * @return the column
     */
    public int getColumn() {
    	return column;
    }

    /**
     * Setter for the value of this Token
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
