package exceptions;

public class LexerError extends Error {
    public LexerError(String errorMessage){
        super(errorMessage);
    }
}
