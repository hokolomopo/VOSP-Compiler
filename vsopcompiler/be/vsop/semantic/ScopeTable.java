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

    public ScopeTable(ScopeTable parent) {
        this.parent = parent;
    }

    public ScopeTable() {
    }

    public void addMethod(Method method){
        this.methodTable.put(method.getName(), method);
    }

    public Method lookupMethod(String name){
        Method method = methodTable.get(name);

        if(method == null && parent != null)
            method = this.parent.lookupMethod(name);

        return method;
    }

    public void addVariable(Formal var){
        this.variableTable.put(var.getName(), var);
    }

    public Formal lookupVariable(String name, boolean inScope) {
        Formal var = variableTable.get(name);

        if(var == null && parent != null && !inScope)
            var = this.parent.lookupVariable(name);

        return var;
    }

    public Formal lookupVariable(String name){
        return lookupVariable(name, false);
    }

    public Type lookupType(String name){
        return typeTable.get(name);
    }

    public void setParent(ScopeTable parent) { this.parent = parent; }
}
