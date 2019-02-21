package exceptions;

public class LexerError extends Error {
    private int column, line;

    public LexerError(String errorMessage, int line, int column){
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
