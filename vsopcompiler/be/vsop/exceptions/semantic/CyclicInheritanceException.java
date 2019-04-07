package be.vsop.exceptions.semantic;

import java.util.ArrayList;
import be.vsop.AST.ClassItem;

public class CyclicInheritanceException extends SemanticException {
    public CyclicInheritanceException(ArrayList<ClassItem> inCycle) {
        super();
        this.line = inCycle.get(0).getLine();
        this.column = inCycle.get(0).getColumn();

        StringBuilder msg = new StringBuilder();
        msg.append("Cyclic inheritance: ");
        for (ClassItem classItem : inCycle) {
            msg.append("class ").append(classItem.getName()).append(" extends ").append(classItem.getParentName());
            msg.append(" (").append(classItem.getLine()).append(":").append(classItem.getColumn()).append("); ");
        }
        msg.setLength(msg.length() - 2);

        this.message = msg.toString();
    }
}
