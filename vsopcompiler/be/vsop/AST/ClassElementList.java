package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a list of all the elements present in a class, i.e. fields and methods
 */
public class ClassElementList extends ASTNode {
    private ArrayList<Field> fields;
    private ArrayList<Method> methods;

    /**
     * Creates a new ClassElementList by appending a field to a previous ClassElementList.
     *
     * @param cel the previous ClassElementList
     * @param f the field to add
     */
    public ClassElementList(ClassElementList cel, Field f) {
        this.fields = cel.fields;
        this.fields.add(f);
        this.methods = cel.methods;

        this.children = new ArrayList<>();
        this.children.addAll(fields);
        this.children.addAll(methods);
    }

    /**
     * Creates a new ClassElementList by appending a method to a previous ClassElementList
     *
     * @param cel the previous ClassElementList
     * @param m the method to add
     */
    public ClassElementList(ClassElementList cel, Method m) {
        this.fields = cel.fields;
        this.methods = cel.methods;
        this.methods.add(m);

        this.children = new ArrayList<>();
        this.children.addAll(fields);
        this.children.addAll(methods);
    }

    /**
     * Creates a new ClassElementList from a list of fields and a list of methods
     *
     * @param fields the fields
     * @param methods the methods
     */
    public ClassElementList(List<Field> fields, List<Method> methods) {
        this.fields = new ArrayList<>(fields);
        this.methods = new ArrayList<>(methods);

        this.children = new ArrayList<>();
        this.children.addAll(fields);
        this.children.addAll(methods);
    }

    /**
     * Creates a new empty ClassElementList. Combine with other constructors to add fields and methods
     */
    public ClassElementList() {
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    /**
     * See ASTNode, a ClassElementList is printed as [field1,field2,...],[method1,method2,...]
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print("[");
        int i;
        if (fields.size() > 0) {
            for (i = 0; i < fields.size(); i++) {
                if(i == 0)
                    fields.get(i).print(tabLevel, false, withTypes);
                else
                    fields.get(i).print(tabLevel, true, withTypes);

                if(i < fields.size() - 1) {
                    System.out.print(",");
                    System.out.println();
                }
            }
        }

        System.out.print("],");
        System.out.println();
        System.out.print(getTab(tabLevel) + "[");

        if (methods.size() > 0) {
            for (i = 0; i < methods.size(); i++) {
                if(i == 0)
                    methods.get(i).print(tabLevel, false, withTypes);
                else
                    methods.get(i).print(tabLevel, true, withTypes);

                if(i < methods.size() - 1) {
                    System.out.print(",");
                    System.out.println();
                }
            }
        }


        System.out.print("]");
    }

    /**
     * Getter for the fields of this ClassElementList
     *
     * @return the fields
     */
    ArrayList<Field> getFields() {
        return fields;
    }

    /**
     * Getter for the methods of this ClassElementList
     *
     * @return the methods
     */
    ArrayList<Method> getMethods() {
        return methods;
    }

    /**
     * See ASTNode
     */
    @Override
    public String getLlvm(InstrCounter counter) {

        //Don't generate code for the fields, this is managed by the ClassItem initialization method

        //Generate methods llvm code
        StringBuilder builder = new StringBuilder();
        if(methods != null)
            for(Method method: methods)
                builder.append(method.getLlvm(counter)).append("\n\n");

        return builder.toString();
    }
}