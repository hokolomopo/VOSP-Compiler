package be.vsop.codegenutil;

import be.vsop.AST.ClassItem;
import be.vsop.AST.Method;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Class used to set indices to methods for them to be put in the vTable
 */
public class MethodCounter {
    private class Node{
        private ClassItem item;

        //Index of the last method
        private int lastIndex = 0;

        //True if we already called buildNode() on this node
        private boolean alreadyBuild = false;

        Node(ClassItem item) {
            this.item = item;
        }

    }

    private HashMap<String, ClassItem> classTable;
    private HashMap<String, Node> nodeTable;

    public MethodCounter(HashMap<String, ClassItem> classTable) {
        this.classTable = classTable;
        nodeTable = new HashMap<>();

        for(ClassItem item : classTable.values()){
            nodeTable.put(item.getName(), new Node(item));
        }
    }

    /**
     * Gives indexes to the methods.
     *
     * Here the goal is to give indexes to the such that the lower indexes are the indexes of the ancestor classes,
     * and the highest are the methods of the class. If we do that, it becomes very easy to cast and override methods in llvm.
     *
     */
    public void setupMethods(){
        for(Node node : nodeTable.values()){
            buildNode(node);
        }
    }

    /**
     * Gives indexes to the methods of this node and its parents
     */
    private void buildNode(Node node){
        if(node == null || node.alreadyBuild)
            return;

        //Build parent
        Node parentNode = nodeTable.get(node.item.getParentName());
        buildNode(parentNode);

        //Get last index of methods of parent = first index of the methods of this node
        int lastIndex = 0;
        if(parentNode != null)
            lastIndex = parentNode.lastIndex;

        //Get methods of parent
        ClassItem parent = classTable.get(node.item.getParentName());
        ArrayList<Method> parentMethods = getMethods(parent);

        ArrayList<Method> methods = node.item.getMethods();

        int i = 0;
        for(Method m : methods){

            //Check if method is an override
            for(Method parentMethod : parentMethods){
                if(m.getName().equals(parentMethod.getName())) {
                    m.setLlvmNumber(parentMethod.getLlvmNumber());
                    m.setOverriddenMethod(parentMethod);
                }
            }

            //Not an override
            if(m.getLlvmNumber() == -1){
                m.setLlvmNumber(lastIndex + i);
                i++;
            }
        }

        //Update the node
        node.alreadyBuild = true;
        node.lastIndex = lastIndex + i;
    }

    /**
     * Get the list of method od a class and its parents
     *
     * @param item the class
     * @return a list of methods
     */
    private ArrayList<Method> getMethods(ClassItem item){
        if(item == null)
            return new ArrayList<>();

        ClassItem parent = classTable.get(item.getParentName());

        ArrayList<Method> parentMethods = new ArrayList<>();
        if(parent != null)
            parentMethods = getMethods(parent);

        ArrayList<Method> methods = item.getMethods();

        parentMethods.addAll(methods);
        return parentMethods;

    }
}
