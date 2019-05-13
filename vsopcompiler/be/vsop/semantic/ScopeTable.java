package be.vsop.semantic;

import be.vsop.AST.Formal;
import be.vsop.AST.Method;
import be.vsop.AST.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ScopeTable {
    public enum Scope{LOCAL, GLOBAL, OUTER}

    private ScopeTable parent;
    private HashMap<String, Method> methodTable = new HashMap<>();
    // LinkedHashMap preserves insertion order, useful for initializing objects
    private LinkedHashMap<String, Formal> variableTable = new LinkedHashMap<>();


    private static final HashMap<String, Type> typeTable;
    static {
        HashMap<String, Type> map = new HashMap<>();
        for(VSOPTypes type : VSOPTypes.values())
            map.put(type.getName(), new Type(type.getName()));
        typeTable = new HashMap<>(Collections.unmodifiableMap(map));
    }

    private Type scopeClassType = null;

    public ScopeTable() {
    }

    public ScopeTable(Type scopeClassType) {
        this.scopeClassType = scopeClassType;
    }

    public void addMethod(Method method){
        this.methodTable.put(method.getName(), method);
    }

    public Method lookupMethod(String name, Scope scope) {
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

    public ArrayList<Formal> getAllVariables(Scope scope) {

        ArrayList<Formal> vars = new ArrayList<>();

        if (scope != Scope.OUTER) {
            variableTable.forEach(
                    (k, v) -> vars.add(v));
        }

        if(parent != null && scope != Scope.LOCAL)
            vars.addAll(this.parent.getAllVariables(Scope.GLOBAL));

        return vars;
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

    public Type getScopeClassType(){
        if(scopeClassType != null)
            return scopeClassType;
        return parent.scopeClassType;
    }


}
