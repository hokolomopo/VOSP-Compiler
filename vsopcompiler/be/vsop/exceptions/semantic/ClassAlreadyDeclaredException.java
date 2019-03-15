package be.vsop.exceptions.semantic;

public class ClassAlreadyDeclaredException extends SemanticException {
    public ClassAlreadyDeclaredException(String className, int line, int column) {
        super(line, column);

        this.message = "Class " + className + " already declared";
    }

}
