package be.vsop.exceptions.semantic;

import be.vsop.AST.Type;
import be.vsop.semantic.LanguageSpecs;

public class TypeNotValidException extends SemanticException {
    public TypeNotValidException(String typeName, int line, int column) {
        super(line, column);

        StringBuilder s = new StringBuilder();

        s.append("Type ").append(typeName).append(" is not valid. Types declared in VSOP are : [");
        for(Type t : LanguageSpecs.DEFAULT_TYPES){
            s.append(t.getName()).append(",");
        }
        s.deleteCharAt(s.lastIndexOf(","));
        s.append("]");

        this.message = s.toString();
    }
}
