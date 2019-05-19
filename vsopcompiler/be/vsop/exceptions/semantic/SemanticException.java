package be.vsop.exceptions.semantic;

/**
 * This class represents a semantic exception, which contains information about the location of the error and a
 * user-defined message to be printed
 */
public class SemanticException extends Exception {
    protected int column, line;
    protected String message;

    public SemanticException() { super(); }

    public SemanticException(int line, int column){
        super();

        this.column = column;
        this.line = line;
    }

    public SemanticException(String errorMessage, int line, int column){
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
