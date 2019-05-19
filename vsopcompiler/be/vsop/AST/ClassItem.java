package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.codegenutil.LLVMKeywords;
import be.vsop.codegenutil.LlvmWrappers;
import be.vsop.exceptions.semantic.ClassAlreadyDeclaredException;
import be.vsop.exceptions.semantic.MainException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class represent a VSOP class
 */
public class ClassItem extends ASTNode{
    private Type type;
    private Type parentType;
    private ClassElementList cel;
    private Formal vTable;

    /**
     * Creates a new ClassItem from a type and a ClassElementList. Default parent type is Object.
     *
     * @param type the type of the VSOP class represented by this object
     * @param cel the elements of the class (fields and methods)
     */
    public ClassItem(Type type, ClassElementList cel) {
        this(type, new Type("Object"), cel);
    }

    /**
     * Creates a new ClassItem from a type, a parent type and a ClassElementList
     *
     * @param type the type of the VSOP class represented by this object.
     *             If Object, parent type will be set to null
     * @param parentType the type of the parent of this class
     * @param cel the elements of the class (fields and methods)
     */
    public ClassItem(Type type, Type parentType, ClassElementList cel) {
        this.scopeTable = new ScopeTable(type);
        this.type = type;

        // This condition is due to the fact that we don't generate language features the same way as we generate
        // user-defined classes
        if (type.getName().equals("Object")) {
            this.parentType = null;
        } else {
            this.parentType = parentType;
        }
        this.cel = cel;

        this.children = new ArrayList<>();
        this.children.add(cel);

        //Don't add Types as children because we already check for missing type of class declaration in SyntaxAnalyzer
        //when checking for cyclic inheritance
    }

    /**
     * See ASTNode
     */
    @Override
    public void updateClassTable(HashMap<String, ClassItem> classTable, ArrayList<SemanticException> errorList) {
        this.classTable = classTable;

        //Check if class is already declared
        ClassItem previousDeclaration = classTable.get(type.getName());
        if(previousDeclaration != null) {
            errorList.add(new ClassAlreadyDeclaredException(type.getName(),
                    line, column, previousDeclaration.line, previousDeclaration.column));
        } else {
            classTable.put(type.getName(), this);
        }
        if(children != null)
            for(ASTNode node : children) {
                node.updateClassTable(classTable, errorList);
            }
    }

    /**
     * See ASTNode
     */
    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {

        //Link this scopeTable with its parent scopeTable
        if (parentType != null) {
            ScopeTable parentTable = classTable.get(getParentName()).scopeTable;
            this.scopeTable.setParent(parentTable);
        }

        //Add self field
        Id selfId = new Id("self");
        selfId.toVar();
        Formal self = new Formal(selfId, type);
        this.scopeTable.addVariable(self);

        //Update children
        if(children != null)
            for(ASTNode node : children) {
                node.fillScopeTable(this.scopeTable, errorList);
            }
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkScope(ArrayList<SemanticException> errorList) {

        //Check if we have a well defined main method
        if (getName().equals("Main")) {
            Method mainMethod = scopeTable.lookupMethod("main");
            if (mainMethod == null) {
                errorList.add(new MainException("The class \"Main\" should contain a \"main\" method", 0, 0));
            } else if (mainMethod.nbArguments() != 0 || !mainMethod.returnType().equals("int32")) {
                errorList.add(new MainException("The method \"main\" should have no arguments and return an int32",
                        mainMethod.line, mainMethod.column));
            }
        }


        super.checkScope(errorList);
    }

    /**
     * See ASTNode, a ClassItem is printed as Class(type, parentType, classElementList)
     */
    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("Class(" + type.getName() + ", " + parentType.getName() + ", ");

        System.out.println();
        cel.print(tabLevel +1, true, withTypes);
        System.out.print(")");
    }

    /**
     * Getter for the name of the parent type. Return null if called on Object (should not happen)
     *
     * @return the name of the type of the parent
     */
    public String getParentName() {
        if (parentType == null)
            return null;
        return parentType.getName();
    }

    /**
     * Getter for the name of the type of the VSOP class represented by this object
     *
     * @return the name of the VSOP class
     */
    public String getName() {
        return type.getName();
    }

    /**
     * Getter for the Type of the parent
     *
     * @return the type of the parent
     */
    Type getParentType() {
        return parentType;
    }

    /**
     * Getter for the VSOP Type of the class represented by this object
     *
     * @return the VSOP Type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the column of the first letter of the parent name, useful for reporting errors
     *
     * @return the column of the first letter of the parent name
     */
    public int parentNameColumn() {
        return column + getName().length() + " extends ".length();
    }

    /**
     * Returns the Method object corresponding to the given methodId Id
     *
     * @param methodId the id of the method to search for
     *
     * @return the corresponding Method object
     */
    Method lookupMethod(Id methodId) {
        return scopeTable.lookupMethod(methodId.getName());
    }

    /**
     * See ASTNode
     */
    @Override
    public void prepareForLlvm() {

        //Add a Vtable Formal to the ClassItem
        addVTable();

        super.prepareForLlvm();
    }

    public ArrayList<Method> getMethods(){
        return cel.getMethods();
    }


    /**
     * Get the llvm code that declares this class.
     * The class is declared with a structure for its fields, and a vTable for its methods
     *
     * @return the llvm code
     */
    public String getClassDeclaration(){

        //Get all class fields
        ArrayList<Formal> fieldFormals = getFormalsList();

        //Get vTable declaration

        return declareVtable() +

                //Get the class structure declaration
                getLlvmClassStructure(fieldFormals);
    }

    /**
     * See ASTNode
     */
    @Override
    public String getLlvm(InstrCounter counter) {

        //Get all class fields
        ArrayList<Formal> fieldFormals = getFormalsList();

        // Generate New method

        return getNew() +

                //Generate initialization method
                getInitializer(fieldFormals) +
                cel.getLlvm(new InstrCounter());
    }

    /**
     * Get the llvm code that declare the Structure of the fields of the class
     *
     * @param fieldFormals the formals available in this scope
     * @return the llvm code
     */
    private String getLlvmClassStructure(ArrayList<Formal> fieldFormals){
        StringBuilder fieldsTypeList = new StringBuilder();

        //Generate list of types of fields to define the Structure representing the Object  in llvm
        for (Formal fieldFormal : fieldFormals) {
            fieldsTypeList.append(fieldFormal.getType().getLlvmName());
            fieldsTypeList.append(", ");
        }

        //Remove last comma if needed
        if (fieldFormals.size() > 0) {
            fieldsTypeList.setLength(fieldsTypeList.length() - 2);
        }

        //Declare Structure of the object in llvm
        return "%class." + getName() + " = type { " + fieldsTypeList + " }\n\n";
    }

    /**
     * Returns the llvm code that declares the vTable of the class
     *
     * @return the llvm code of the declaration
     */
    private String declareVtable(){

        //Get the list of the methods
        ArrayList<Method> methods = getFullMethodList();



        //Generate list of types of fields to define the Structure representing the Object  in llvm
        ArrayList<Method> tmp = new ArrayList<>();
        for (Method m : methods) {
            //Skip overridden methods
            if (m.isOverride())
                continue;

            tmp.add(m);

        }

        //Sort the Methods by index
        tmp.sort(Comparator.comparingInt(Method::getLlvmNumber));

        //Replace overridden methods
        for(Method method : methods){
            if(!method.isOverride())
                continue;
            Method overrided = tmp.get(method.getLlvmNumber());

            if(isNotChild(method.getParentClassName(), overrided.getParentClassName()))
                continue;

            tmp.remove(method.getLlvmNumber());
            tmp.add(method.getLlvmNumber(), method);
        }

        //Declares the vTable
        StringBuilder llvm = new StringBuilder();

        llvm.append(LlvmWrappers.vtableName(type.getName())).append(" = type { ");
        for(Method m : tmp){
            llvm.append(m.getLlvmSignature(true));
            llvm.append(", ");
        }

        //Remove the last comma if needed
        if(tmp.size() > 0)
            llvm.delete(llvm.length() - 2, llvm.length());

        llvm.append("}\n\n");

        return llvm.toString();

    }

    /**
     * Get the llvm "New" function for this class.
     * This function malloc space for the object structure and vTable, and call the init function
     *
     * @return the llvm code of the "New" function
     */
    private String getNew(){
        StringBuilder llvm = new StringBuilder();
        InstrCounter counter = new InstrCounter();

        // Header of initialization method
        llvm.append(LLVMKeywords.DEFINE.getLlvmName()).append(" ")
                .append(VSOPTypes.getLlvmTypeName(type.getName())).append(" ")
                .append(LlvmWrappers.newFunctionNameFromClassName(type.getName())).append("() {").append(endLine);

        //Allocate memory for the object structure
        ExprEval heapAllocationExpr = LlvmWrappers.heapAllocation(counter, VSOPTypes.getLlvmTypeName(type.getName(), false));
        String retLlvmId = heapAllocationExpr.llvmId;
        llvm.append(heapAllocationExpr.llvmCode);

        //Allocate memory for the vTable
        ExprEval heapAllocationVTable = LlvmWrappers.heapAllocation(counter, this.vTable.getType().getName());
        llvm.append(heapAllocationVTable.llvmCode);
        llvm.append(vTable.llvmStore(heapAllocationVTable.llvmId, retLlvmId, counter));


        //Call init function
        String initCall = String.format("%s %s %s (%s %s)\n", LLVMKeywords.CALL.getLlvmName(),
                VSOPTypes.UNIT.getLlvmName(), LlvmWrappers.initFunctionName(type.getName()),
                VSOPTypes.getLlvmTypeName(type.getName()), retLlvmId);

        llvm.append(initCall);


        //Return initialized object
        llvm.append(LLVMKeywords.RET.getLlvmName()).append(" ").append(VSOPTypes.getLlvmTypeName(type.getName()))
                .append(" ").append(retLlvmId).append(endLine);

        //End of initialization method
        llvm.append("}").append(endLine).append(endLine);


        return llvm.toString();
    }

    /**
     * Returns the llvm code corresponding to the initializer function
     *
     * @param fieldFormals the list of formals of the Structure representing the class
     *
     * @return the llvm code
     */
    private String getInitializer(ArrayList<Formal> fieldFormals){


        StringBuilder llvm = new StringBuilder();
        InstrCounter counter = new InstrCounter();
        String self = "%self";

        // Header of initialization method
        llvm.append(LLVMKeywords.DEFINE.getLlvmName()).append(" ")
                .append(VSOPTypes.UNIT.getLlvmName()).append(" ")
                .append(LlvmWrappers.initFunctionName(type.getName())).append("(")
                .append(VSOPTypes.getLlvmTypeName(type.getName())).append(" ").append(self)
                .append(") {").append(endLine);

        //Initialize parent
        if(parentType != null){
            ExprEval casted = Expr.castExpr(type.getName(), parentType.getName(), self, counter);
            llvm.append(casted.llvmCode);
            String initCall = String.format("%s %s %s (%s %s)\n", LLVMKeywords.CALL.getLlvmName(),
                    VSOPTypes.UNIT.getLlvmName(), LlvmWrappers.initFunctionName(parentType.getName()),
                    VSOPTypes.getLlvmTypeName(parentType.getName()), casted.llvmId);

            llvm.append(initCall);
        }

        //Initialize the formals
        llvm.append(initFields(fieldFormals, self, counter));

        //Initialize the VTable
        llvm.append(initVTable(counter, self));

        //Return void
        llvm.append(LLVMKeywords.RET.getLlvmName()).append(" ").append(VSOPTypes.UNIT.getLlvmName()).append(endLine);

        //End of initialization method
        llvm.append("}").append(endLine).append(endLine);

        return llvm.toString();
    }

    /**
     * Initialize the methods inside the vTable
     *
     * @param counter an InstrCounter
     * @param selfId the llvm id of self
     *
     * @return the llvm code to initialize the vTable
     */
    private String initVTable(InstrCounter counter, String selfId){
        StringBuilder llvm = new StringBuilder();

        //Load the vTable
        ExprEval vTableLoad = this.vTable.llvmLoad(selfId, counter);
        String vTableId = vTableLoad.llvmId;
        llvm.append(vTableLoad.llvmCode);

        //Store the methods in the vTable
        ArrayList<Method> methods = cel.getMethods();
        for(Method method : methods){
            llvm.append(method.storeInVTable(this.vTable, vTableId, counter));
        }

        return llvm.toString();
    }

    /**
     * Initialize the fields of the class
     *
     * @param fieldFormals the fields of the class
     * @param selfId the llvm id of self
     * @param counter an InstrCounter
     *
     * @return the llvm code to Initialize the fields
     */
    private String initFields(ArrayList<Formal> fieldFormals, String selfId, InstrCounter counter){
        StringBuilder llvm = new StringBuilder();

        //Get local fields, because we only want to initialize the local fields, the inherited fields will
        // be initialized with the function parent.init
        ArrayList<Formal> localFormals = new ArrayList<>();
        for(Field field : this.cel.getFields())
            for(Formal formal : fieldFormals)
                if(field.getFormal().equals(formal))
                    localFormals.add(formal);

        //Build fields initializing expressions
        for(Formal formal : localFormals){

            //Get the field corresponding to the formal
            Field field = getField(formal);
            if(field == null)
                continue;

            //Initialize the field and store it
            ExprEval evalFieldInitExpr = field.getInitLlvm(counter);
            llvm.append(evalFieldInitExpr.llvmCode);
            String store = formal.llvmStore(evalFieldInitExpr.llvmId, selfId, counter);
            llvm.append(store);
        }

        return llvm.toString();
    }

    /**
     * Get the Field object corresponding to a formal, searching in this class and all its parent
     *
     * @param formal the formal to search for
     *
     * @return the corresponding field
     */
    private Field getField(Formal formal){

        ClassItem current = this;

        while(true) {
            ArrayList<Field> fields = current.cel.getFields();

            for (Field field : fields) {
                if (field.getFormal().equals(formal))
                    return field;
            }

            if(current.parentType == null)
                return null;

            current = classTable.get(current.parentType.getName());
        }


    }

    /**
     * Get the list of formals representing the field of this class and all of its parents
     *
     * @return the list of formals
     */
    private ArrayList<Formal> getFormalsList(){

        //ArrayList of ArrayList because the order of the formals matters
        ArrayList<ArrayList<Formal>> fieldFormalsList = new ArrayList<>();

        ScopeTable scope = this.scopeTable;

        //Get the fields of all the parents of the class
        while(scope != null){
            fieldFormalsList.add(scope.getAllVariables(ScopeTable.Scope.LOCAL));
            scope = scope.getParent();
        }

        //Insert in reverted order. That way, when we have class A extends B, and that we cast A to B (for a method call)
        //the fields are exactly is the same oder and ca be accessed from B
        ArrayList<Formal> fieldFormals = new ArrayList<>();
        for(int i = fieldFormalsList.size() - 1;i >= 0;i--)
            fieldFormals.addAll(fieldFormalsList.get(i));

        //Remove self and units from fields
        ArrayList<Formal> self = new ArrayList<>();
        for(Formal field : fieldFormals) {
            if (field.getName().equals(LanguageSpecs.SELF) || field.getType().getName().equals(VSOPTypes.UNIT.getName())) {
                self.add(field);
            }
        }
        fieldFormals.removeAll(self);

        //Add the vTable to the fields
        fieldFormals.add(0, vTable);


        //Give id and parent class to fields
        int k = 0;
        for(Formal formal : fieldFormals) {
            formal.setClassFieldId(k++);
            formal.setParentClass("%class." + this.getName());
            formal.setScopeTable(this.scopeTable);
        }

        return fieldFormals;

    }

    /**
     * Get the list of methods representing the field of this class and all of its parents
     *
     * @return the list of formals
     */
    private ArrayList<Method> getFullMethodList(){

        //ArrayList of ArrayList because the order of the formals matters
        ArrayList<ArrayList<Method>> methodListList = new ArrayList<>();

        Type currentType = type;

        //Get the fields of all the parents of the class
        while(currentType != null){
            ClassItem currentClass = classTable.get(currentType.getName());
            methodListList.add(currentClass.getMethods());
            currentType = currentClass.parentType;
        }

        //Insert in reverted order. That way, when we have class A extends B, and that we cast A to B (for a method call)
        //the fields are exactly is the same oder and can be accessed from B
        ArrayList<Method> methodList = new ArrayList<>();
        for(int i = methodListList.size() - 1;i >= 0;i--)
            methodList.addAll(methodListList.get(i));

        return methodList;

    }


    /**
     * Getter for the Method object corresponding to the given method name
     *
     * @param methodName the name of the method
     *
     * @return the corresponding Method object
     */
    Method getMethod(String methodName){
        return this.scopeTable.lookupMethod(methodName, ScopeTable.Scope.GLOBAL);
    }

    /**
     * Getter for the vTable of this class
     *
     * @return the vTable
     */
    Formal getVTable() {
        return vTable;
    }

    /**
     * Set up the vTable formal for this ClassItem
     */
    private void addVTable(){
        Formal vTable = new Formal(LlvmWrappers.vtableName(type.getName()), LlvmWrappers.vtableName(type.getName()));
        vTable.toClassField();
        vTable.setParentClass("%class." + this.getName());
        vTable.setClassFieldId(0);
        this.vTable = vTable;
    }
}