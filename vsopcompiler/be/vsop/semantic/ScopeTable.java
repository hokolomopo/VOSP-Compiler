package be.vsop.semantic;

import be.vsop.AST.Formal;
import be.vsop.AST.Method;
import be.vsop.AST.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * This class is used to store the methods and fields present in any given scope,
 * and to access them with various functions
 */
public class ScopeTable {
    /**
     * This enum represents the different scopes in which one can look for methods or fields.
     * Local will only use the current scope table, global will look in all the parents as well, and outer
     * will look only in all the parents (and not in the current scope table)
     */
    public enum Scope{LOCAL, GLOBAL, OUTER}

    private ScopeTable parent;
    private HashMap<String, Method> methodTable = new HashMap<>();
    // LinkedHashMap preserves insertion order, useful for initializing objects
    private LinkedHashMap<String, Formal> variableTable = new LinkedHashMap<>();

    private Type scopeClassType = null;

    /**
     * Creates a new empty scope table
     */
    public ScopeTable() {
    }

    /**
     * Creates a new scope table with the given scopeClassTable
     *
     * @param scopeClassType the scope class table
     */
    public ScopeTable(Type scopeClassType) {
        this.scopeClassType = scopeClassType;
    }

    /**
     * Add a new method in this scope table
     *
     * @param method the method to add
     */
    public void addMethod(Method method){
        this.methodTable.put(method.getName(), method);
    }

    /**
     * Search for the method named name in the given scope
     *
     * @param name the method to search for
     * @param scope the scope to search in, see enum
     *
     * @return the method, or null if it does not exist in the given scope
     */
    public Method lookupMethod(String name, Scope scope) {
        Method method = null;
        if (scope != Scope.OUTER) {
            method = methodTable.get(name);
        }

        if(method == null && parent != null && scope != Scope.LOCAL)
            method = this.parent.lookupMethod(name, Scope.GLOBAL);

        return method;
    }

    /**
     * Search for the method named name in the current scope (and above). Convenience method for setting scope to global
     *
     * @param name the method to search for
     *
     * @return the method, or null if it does not exist
     */
    public Method lookupMethod(String name){
        return lookupMethod(name, Scope.GLOBAL);
    }

    /**
     * Add a new variable in this scope table
     *
     * @param var the variable to add
     */
    public void addVariable(Formal var){
        this.variableTable.put(var.getName(), var);
    }

    /**
     * Search for the variable named name in the given scope
     *
     * @param name the variable to search for
     * @param scope the scope to search in, see enum
     *
     * @return the variable, or null if it does not exist in the given scope
     */
    public Formal lookupVariable(String name, Scope scope) {
        Formal var = null;
        if (scope != Scope.OUTER) {
            var = variableTable.get(name);
        }

        if(var == null && parent != null && scope != Scope.LOCAL)
            var = this.parent.lookupVariable(name, Scope.GLOBAL);

        return var;
    }

    /**
     * Search for the variable named name in the current scope (and above). Convenience method for setting scope to global
     *
     * @param name the method to search for
     *
     * @return the method, or null if it does not exist
     */
    public Formal lookupVariable(String name){
        return lookupVariable(name, Scope.GLOBAL);
    }

    /**
     * Return all the variables present in the given scope
     *
     * @param scope the scope to search in, see enum
     *
     * @return the variables
     */
    public ArrayList<Formal> getAllVariables(Scope scope) {
        ArrayList<Formal> vars = new ArrayList<>();

        // adds all the variables of the current scope, if needed
        if (scope != Scope.OUTER) {
            variableTable.forEach(
                    (k, v) -> vars.add(v));
        }

        if(parent != null && scope != Scope.LOCAL)
            vars.addAll(this.parent.getAllVariables(Scope.GLOBAL));

        return vars;
    }

    /**
     * Getter for the parent scope table of this table
     *
     * @return the parent scope table
     */
    public ScopeTable getParent() {
        return parent;
    }

    /**
     * Setter for the parent scope table of this table
     *
     * @param parent the parent scope table
     */
    public void setParent(ScopeTable parent) {
        this.parent = parent;
    }

    /**
     * Getter for the type of the class containing this scope (may look in the parent scope if this scope is not
     * directly a class scope)
     *
     * @return the type of the class containing this scope
     */
    public Type getScopeClassType(){
        if(scopeClassType != null)
            return scopeClassType;
        return parent.scopeClassType;
    }


}
