package be.vsop.semantic;

import be.vsop.AST.Formal;
import be.vsop.AST.Method;
import be.vsop.AST.Type;

import java.util.Collections;
import java.util.HashMap;

public class ScopeTable {
    public enum Scope{LOCAL, GLOBAL, OUTER}

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

    public ScopeTable(ScopeTable parent) {
        this();
        this.parent = parent;
    }

    public ScopeTable copyWithoutSelf(){
        ScopeTable copy = new ScopeTable();
        copy.parent = this.parent;
        copy.methodTable = new HashMap<>(this.methodTable);
        copy.variableTable = new HashMap<>(this.variableTable);
        copy.variableTable.remove("self");

        return copy;
    }

    public void addMethod(Method method){
        this.methodTable.put(method.getName(), method);
    }

    public Method lookupMethod(String name, Scope scope) {
        // Much easier to debug if we put a typo in the argument

        Method method = null;
        if (scope != Scope.OUTER) {
            method = methodTable.get(name);
        }

        if(method == null && parent != null && scope != Scope.LOCAL)
            method = this.parent.lookupMethod(name, Scope.GLOBAL);

        return method;
    }

    public Method lookupMethod(String name){
        return lookupMethod(name, Scope.GLOBAL);
    }

    public void addVariable(Formal var){
        this.variableTable.put(var.getName(), var);
    }

    public Formal lookupVariable(String name, Scope scope) {

        Formal var = null;
        if (scope != Scope.OUTER) {
            var = variableTable.get(name);
        }

        if(var == null && parent != null && scope != Scope.LOCAL)
            var = this.parent.lookupVariable(name, Scope.GLOBAL);

        return var;
    }

    public Formal lookupVariable(String name){
        return lookupVariable(name, Scope.GLOBAL);
    }

    public Type lookupType(String name){
        return typeTable.get(name);
    }

    public ScopeTable getParent() {
        return parent;
    }

    public void setParent(ScopeTable parent) { this.parent = parent; }


}
