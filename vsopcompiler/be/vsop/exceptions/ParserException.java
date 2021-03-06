package be.vsop.exceptions;

import be.vsop.tokens.Token;
import java_cup.runtime.ComplexSymbolFactory;

import java.util.List;

/**
 * This class represents a parser exception, which contains information about the location of the error and a
 * user-defined message to be printed
 */
public class ParserException extends Error {
    private int column, line;
    private String message;

    public ParserException(int line, int column){
        super();

        this.column = column;
        this.line = line;
    }

    public ParserException(String errorMessage, int line, int column){
        super();

        this.message = errorMessage;
        this.column = column;
        this.line = line;
    }

    /**
     * See vsop.cup
     */
    public ParserException(ComplexSymbolFactory.ComplexSymbol current, List<Integer> expected, int line, int column){
        super();

        StringBuilder s = new StringBuilder();
        s.append("Symbol found is : ").append(current.getName());
        s.append(" expected Symbols are [");

        for(Integer i : expected){
            Token.Tokens t = Token.Tokens.fromValue(i);
            s.append(t.toString()).append(", ");
        }
        if (expected.size() > 0) {
            s.setLength(s.length() - 2);
        }
        s.append("]");

        this.message = s.toString();
        this.column = column;
        this.line = line;
    }

    @Override
    public String getMessage(){
        if(message == null)
            return line + ":" + column + ": syntax error";
        return line + ":" + column + ": syntax error :" + message;
    }

}
