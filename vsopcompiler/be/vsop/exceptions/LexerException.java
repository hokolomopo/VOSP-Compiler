package be.vsop.exceptions;

import java.io.IOException;

public class LexerException extends IOException {
    private int column, line;

    public LexerException(String errorMessage, int line, int column){
        super(errorMessage);

        this.column = column;
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }
}
