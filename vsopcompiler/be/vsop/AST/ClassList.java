package be.vsop.AST;

import java.util.ArrayList;

/**
 * This class represents a list of VSOP classes (represented by ClassItem's)
 */
public class ClassList extends ASTNode {
    private ArrayList<ClassItem> classes;

    /**
     * Creates a new ClassList by adding a ClassItem to a previous ClassList
     *
     * @param cl the previous ClassList
     * @param ci the ClassItem to add
     */
    public ClassList(ClassList cl, ClassItem ci) {
        this.classes = cl.classes;
        this.classes.add(ci);

        this.children = new ArrayList<>(this.classes);
    }

    /**
     * Creates a new ClassList with only one ClassItem given in argument
     *
     * @param ci the ClassItem
     */
    public ClassList(ClassItem ci) {
        this.classes = new ArrayList<>();
        this.classes.add(ci);

        this.children = new ArrayList<>(this.classes);
    }

    /**
     * See ASTNode, a ClassList is printed as [class1,class2,...]
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("[");
        int i;
        if (classes.size() > 0) {
            for (i = 0; i < classes.size(); i++) {
                if(i == 0)
                    classes.get(i).print(tabLevel, false, withTypes);
                else
                    classes.get(i).print(tabLevel, true, withTypes);
                if(i < classes.size() - 1) {
                    System.out.print(",");
                    System.out.println();
                }
            }
        }
        System.out.print("]");
    }
}