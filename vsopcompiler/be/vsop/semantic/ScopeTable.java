package be.vsop.semantic;

import be.vsop.AST.Formal;
import be.vsop.AST.Method;
import be.vsop.AST.Type;

import java.util.Collections;
import java.util.HashMap;

public class ScopeTable {
    private ScopeTable parent;
    private HashMap<String, Method> methodTable = new HashMap<>();
    private HashMap<String, Formal> variableTable = new HashMap<>();

    private static final HashMap<String, Type> typeTable;
    static {
        HashMap<String, Type> map = new HashMap<>();
        for(Type defaultType : LanguageSpecs.DEFAULT_TYPES)
            map.put(defaultType.getName(), defaultType);
        typeTable = new HashMap<>(Collections.unmodifiableMap(map));
    }

    public ScopeTable() {
    }

    public void addMethod(Method method){
        this.methodTable.put(method.getName(), method);
    }

    public Method lookupMethod(String name, String scope) {
        // Much easier to debug if we put a typo in the argument
        if (!scope.equals("local scope only") && !scope.equals("outer scope only") && !scope.equals("everywhere")) {
            throw new IllegalArgumentException("unknown scope argument");
        }
        Method method = null;
        if (!scope.equals("outer scope only")) {
            method = methodTable.get(name);
        }

        if(method == null && parent != null && !scope.equals("local scope only"))
            method = this.parent.lookupMethod(name, "everywhere");

        return method;
    }

    public Method lookupMethod(String name){
        return lookupMethod(name, "everywhere");
    }

    public void addVariable(Formal var){
        this.variableTable.put(var.getName(), var);
    }

    public Formal lookupVariable(String name, String scope) {
        // Much easier to debug if we put a typo in the argument
        if (!scope.equals("local scope only") && !scope.equals("outer scope only") && !scope.equals("everywhere")) {
            throw new IllegalArgumentException("unknown scope argument");
        }
        Formal var = null;
        if (!scope.equals("outer scope only")) {
            var = variableTable.get(name);
        }

        if(var == null && parent != null && !scope.equals("local scope only"))
            var = this.parent.lookupVariable(name, "everywhere");

        return var;
    }

    public Formal lookupVariable(String name){
        return lookupVariable(name, "everywhere");
    }

    public Type lookupType(String name){
        return typeTable.get(name);
    }

    public ScopeTable getParent() {
        return parent;
    }

    public void setParent(ScopeTable parent) { this.parent = parent; }
}
