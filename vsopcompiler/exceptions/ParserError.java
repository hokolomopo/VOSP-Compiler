package exceptions;

import java_cup.runtime.ComplexSymbolFactory;
import tokens.Token;

import java.util.List;

public class ParserError extends Error {
    private int column, line;
    private String message;

    public ParserError(int line, int column){
        super();

        this.column = column;
        this.line = line;
    }

    public ParserError(String errorMessage, int line, int column){
        super(errorMessage);

        this.column = column;
        this.line = line;
    }


    public ParserError(ComplexSymbolFactory.ComplexSymbol current, List<Integer> expected, int line, int column){
        super();

        StringBuilder s = new StringBuilder();
        s.append("Symbol found is : " + current.getName());
        s.append(" expected Symbols are [");

        for(Integer i : expected){
            System.out.println(i);
            Token.Tokens t = Token.Tokens.fromValue(i);
            s.append(t.toString() + ", ");
        }
        if (expected.size() > 0) {
            s.deleteCharAt(s.length() - 1);
            s.deleteCharAt(s.length() - 1);
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
        return line + ":" + column + ": syntax error " + message;
    }

}
