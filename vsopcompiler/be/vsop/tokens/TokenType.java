package be.vsop.tokens;

/**
 * This enum represents the 4 different types of token that we defined for distinguishing keywords from identifiers
 * for instance. Be careful not to confound this enum with the one in Token.java
 */
public enum TokenType{
    ID,
    KEYWORD,
    SYMBOL,
    LITERAL,
}
