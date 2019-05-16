package be.vsop.exceptions.semantic;

import be.vsop.semantic.VSOPTypes;

public class TypeNotValidException extends SemanticException {
    public TypeNotValidException(String typeName, int line, int column) {
        super(line, column);

        StringBuilder s = new StringBuilder();

        s.append("Type ").append(typeName).append(" is not valid. Types declared in VSOP are : [");
        for(VSOPTypes t : VSOPTypes.values()){
            s.append(t.getName()).append(",");
        }
        s.deleteCharAt(s.lastIndexOf(","));
        s.append("]");

        this.message = s.toString();
    }
}
