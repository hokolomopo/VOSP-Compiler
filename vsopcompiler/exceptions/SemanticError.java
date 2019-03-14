package exceptions;

import java_cup.runtime.ComplexSymbolFactory;
import tokens.Token;

import java.util.List;

public class SemanticError extends Exception {
    private int column, line;
    private String message;

    public SemanticError(int line, int column){
        super();

        this.column = column;
        this.line = line;
    }

    public SemanticError(String errorMessage, int line, int column){
        super();

        this.message = errorMessage;
        this.column = column;
        this.line = line;
    }

    @Override
    public String getMessage(){
        if(message == null)
            return line + ":" + column + ": semantic error";
        return line + ":" + column + ": semantic error : " + message;
    }

}
