package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.exceptions.semantic.VariableAlreadyDeclaredException;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class represents a list of VSOP formals
 */
public class FormalList extends ASTNode{
    private ArrayList<Formal> formals;

    /**
     * Creates a new FormalList from a previous FormalList by adding the given formal
     *
     * @param fl the previous FormalList
     * @param f the formal to add
     */
    public FormalList(FormalList fl, Formal f) {
        this.formals = fl.formals;
        this.formals.add(f);

        this.children = new ArrayList<>(this.formals);
    }

    /**
     * Creates a new empty FormalList to be used with other constructors
     */
    public FormalList() {
        formals = new ArrayList<>();
    }

    /**
     * Add the given formal at the beginning of the list. Position is important for inheritance
     *
     * @param formal the formal to add
     */
    void addFormalAtBeginning(Formal formal){
        formals.add(0, formal);
    }

    /**
     * See ASTNode, a FormalList is printed as [formal1,formal2,...]
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("[");

        int i;
        if (formals.size() > 0) {
            for (i = 0; i < formals.size() - 1; i++) {
                formals.get(i).print(tabLevel, false, withTypes);
                System.out.print(",");
            }
            formals.get(i).print(tabLevel, false, withTypes);
        }

        System.out.print("]");
    }

    /**
     * Add errors in the error list if this list contains multiple times the same formals (with respect to their names)
     *
     * @param errorList the list of semantic errors, may be updated
     */
    void checkAllDifferent(ArrayList<SemanticException> errorList) {
        Formal formal;
        Formal other;

        // This set is used to avoid reporting multiple times similar errors if the list contains more than twice
        // the same formal. If the list contains n times the same formal, we report n - 1 errors.
        HashSet<Integer> toIgnore = new HashSet<>();

        for (int i = 0; i < formals.size(); i++) {
            if (! toIgnore.contains(i)) {
                formal = formals.get(i);
                for (int j = i + 1; j < formals.size(); j++) {
                    other = formals.get(j);

                    // Formals are considered equal if they have the same name
                    if (formal.getName().equals(other.getName())) {
                        errorList.add(new VariableAlreadyDeclaredException(formal.getName(),
                                other.line, other.column, formal.line, formal.column));
                        // We just reported an error for this formal so ignore it from now
                        toIgnore.add(j);
                    }
                }
            }
        }
    }

    /**
     * Getter for the number of formals contained in this list
     *
     * @return the size of the list
     */
    int size() {
        return formals.size();
    }

    /**
     * Getter for the index'th formal of this list. Should be in bounds
     *
     * @param index the index of the Formal
     *
     * @return the index'th Formal
     */
    Formal get(int index) {
        return formals.get(index);
    }

    /**
     * See ASTNode
     */
    @Override
    public String getLlvm(InstrCounter counter) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0;i < formals.size();i++){
            builder.append(formals.get(i).getLlvm(counter));

            // Don't add a comma after last formal
            if(i < formals.size() - 1)
                builder.append(", ");
        }
        return builder.toString();
    }

    /**
     * Creates the llvm code needed for allocating memory for all the formals
     *
     * @return the llvm code
     */
    String llvmAllocate() {
        StringBuilder builder = new StringBuilder();

        for(Formal formal : formals)
            builder.append(formal.llvmAllocate());

        return builder.toString();
    }

    /**
     * Creates the llvm code needed for storing all the formals
     *
     * @param counter an InstrCounter
     *
     * @return the llvm code
     */
    String llvmStore(InstrCounter counter) {
        StringBuilder builder = new StringBuilder();

        for(Formal formal : formals) {
            builder.append(formal.llvmStore(formal.getLlvmId(), counter));
        }

        return builder.toString();
    }
}