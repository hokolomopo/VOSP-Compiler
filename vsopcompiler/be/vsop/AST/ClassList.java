package be.vsop.AST;

import java.util.ArrayList;

public class ClassList extends ASTNode {
    private ArrayList<ClassItem> classes;

    public ClassList(ClassList cl, ClassItem ci) {
        if(cl == null)
            this.classes = new ArrayList<>();
        else
            this.classes = cl.classes;

        if(ci != null)
            this.classes.add(ci);

        this.children = new ArrayList<>(this.classes);
    }

    public ClassList(ClassItem ci) {
        this(null, ci);
    }

    public ClassList(ArrayList<ClassItem> classes) {
        this.classes = classes;

        this.children = new ArrayList<>(this.classes);
    }

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