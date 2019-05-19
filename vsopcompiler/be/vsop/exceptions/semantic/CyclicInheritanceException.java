package be.vsop.exceptions.semantic;

import be.vsop.AST.ClassItem;

import java.util.ArrayList;

/**
 * This class represents a semantic exception which occurs when a class inherits from one of its children
 */
public class CyclicInheritanceException extends SemanticException {
    public CyclicInheritanceException(ArrayList<ClassItem> inCycle) {
        this.line = inCycle.get(0).line;
        this.column = inCycle.get(0).column;

        StringBuilder msg = new StringBuilder();
        msg.append("Cyclic inheritance: ");
        for (ClassItem classItem : inCycle) {
            msg.append("class ").append(classItem.getName()).append(" extends ").append(classItem.getParentName());
            msg.append(" (").append(classItem.line).append(":").append(classItem.column).append("); ");
        }
        msg.setLength(msg.length() - 2);

        this.message = msg.toString();
    }
}
