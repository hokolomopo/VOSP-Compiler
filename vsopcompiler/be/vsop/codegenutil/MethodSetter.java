package be.vsop.codegenutil;

import be.vsop.AST.ClassItem;
import be.vsop.AST.Method;

import java.util.ArrayList;
import java.util.HashMap;

public class MethodSetter {
    private class Node{
        private ClassItem item;
        private int lastNumber = 0;
        private boolean alreadyBuild = false;

        public Node(ClassItem item) {
            this.item = item;
        }

    }

    private HashMap<String, ClassItem> classTable;
    private HashMap<String, Node> nodeTable;

    public MethodSetter(HashMap<String, ClassItem> classTable) {
        this.classTable = classTable;
        nodeTable = new HashMap<>();

        for(ClassItem item : classTable.values()){
            nodeTable.put(item.getName(), new Node(item));
        }
    }

    public void setupMethods(){
        for(Node node : nodeTable.values()){
            buildNode(node);

        }
    }

    private void buildNode(Node node){
        if(node == null)
            return;
        if(node.alreadyBuild)
            return;

        //Build parent
        Node parentNode = nodeTable.get(node.item.getParentName());
        buildNode(parentNode);

        int lastNumber = 0;
        if(parentNode != null)
            lastNumber = parentNode.lastNumber;

        ClassItem parent = classTable.get(node.item.getParentName());
        ArrayList<Method> parentMethods = getMethods(parent);

        ArrayList<Method> methods = node.item.getMethods();

        int i = 0;
        for(Method m : methods){
            for(Method parentMethod : parentMethods){
                if(m.getName().equals(parentMethod.getName())) {
                    m.setLlvmNumber(parentMethod.getLlvmNumber());
                    m.setOverriddenMethod(parentMethod);
                }
            }

            if(m.getLlvmNumber() == -1){
                m.setLlvmNumber(lastNumber + i);
                i++;
            }
        }

        node.alreadyBuild = true;
        node.lastNumber = lastNumber + i;
    }

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
