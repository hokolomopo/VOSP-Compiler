package be.vsop.exceptions.semantic;

public class ClassNotDeclaredException extends SemanticException{
    public ClassNotDeclaredException(String className, int line, int column) {
        super(line, column);

        this.message = "Class " + className + " not declared";
    }

}
