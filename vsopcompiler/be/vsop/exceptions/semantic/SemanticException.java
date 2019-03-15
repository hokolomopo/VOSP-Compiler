package be.vsop.exceptions.semantic;

public class SemanticException extends Exception {
    protected int column, line;
    protected String message;

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
