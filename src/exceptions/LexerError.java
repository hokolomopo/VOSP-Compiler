package exceptions;

public class LexerError extends Error {
    private int column, line;

    public LexerError(String errorMessage, int line, int column){
        super(errorMessage);

        this.column = column;
        this.line = line;
    }

    public int getColumn() {
        return column + 1;
    }

    public int getLine() {
        return line + 1;
    }
}
