package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.codegenutil.LlvmVar;
import be.vsop.exceptions.semantic.ClassAlreadyDeclaredException;
import be.vsop.exceptions.semantic.MainException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassItem extends ASTNode{
    private Type type;
    private Type parentType;
    private ClassElementList cel;

    public ClassItem(Type type, ClassElementList cel) {
        this(type, new Type("Object"), cel);
    }

    public ClassItem(Type type, Type parentType, ClassElementList cel) {
        this.scopeTable = new ScopeTable(type);
        this.type = type;
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

    @Override
    public void updateClassTable(HashMap<String, ClassItem> classTable, ArrayList<SemanticException> errorList) {
        this.classTable = classTable;
        ClassItem previousDeclaration = classTable.get(type.getName());
        if(previousDeclaration != null) {
            errorList.add(new ClassAlreadyDeclaredException(type.getName(),
                    line, column, previousDeclaration.line, previousDeclaration.column));
        } else {
            classTable.put(type.getName(), this);
        }
        if(children != null)
            for(ASTNode node : children)
                node.updateClassTable(classTable, errorList);
    }

    @Override
    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
        if (parentType != null) {
            ScopeTable parentTable = classTable.get(getParentName()).scopeTable;
            this.scopeTable.setParent(parentTable);
        }
        //Add self field
        Id selfId = new Id("self");
        selfId.toVar();
        Formal self = new Formal(selfId, type);
        this.scopeTable.addVariable(self);

        if(children != null)
            for(ASTNode node : children) {
                node.fillScopeTable(this.scopeTable, errorList);
            }
    }

    @Override
    public void checkScope(ArrayList<SemanticException> errorList) {
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

    @Override
    public void print(int tabLevel, boolean doTab, boolean withTypes) {
        if(doTab)
            System.out.print(getTab(tabLevel));
        System.out.print("Class(" + type.getName() + ", " + parentType.getName() + ", ");

        System.out.println();
        cel.print(tabLevel +1, true, withTypes);
        System.out.print(")");
    }

    public String getParentName() {
        return parentType.getName();
    }

    public String getName() {
        return type.getName();
    }

    Type getParentType() {
        return parentType;
    }

    public Type getType() {
        return type;
    }

    public int parentNameColumn() {
        return column + getName().length() + " extends ".length();
    }

    Method lookupMethod(Id methodId) {
        return scopeTable.lookupMethod(methodId.getName());
    }

    @Override
    public String getLlvm(InstrCounter counter) {
        StringBuilder fieldsTypeList = new StringBuilder();

        //Get all class fields
        ArrayList<Formal> fieldFormals = getFormalsList();


        //Generate list of types of fields to define the Structure representing the Object  in llvm
        for (Formal fieldFormal : fieldFormals) {
            fieldsTypeList.append(fieldFormal.getType().getLlvmName(true));
            fieldsTypeList.append(", ");
        }
        if (fieldFormals.size() > 0) {
            fieldsTypeList.setLength(fieldsTypeList.length() - 2);
        }

        //Declare Structure of the object in llvm
        StringBuilder llvm = new StringBuilder();
        llvm.append("%class.").append(getName()).append(" = type { ").append(fieldsTypeList).append(" }\n\n")
                .append(cel.getLlvm(counter)).append(endLine);

        // Generate initialization method
        String init  = getInitializer(fieldFormals);
        llvm.append(init);

        return llvm.toString();
    }

    /**
     * Get the constructor function in llvm
     *
     * @param fieldFormals the list of formals of the Structure representing the class
     * @return the llvm code
     */
    private String getInitializer(ArrayList<Formal> fieldFormals){
        StringBuilder llvm = new StringBuilder();

        // Header of initialization method
        llvm.append(LLVMKeywords.DEFINE.getLlvmName()).append(" ")
                .append(VSOPTypes.getLlvmTypeName(type.getName(), true)).append(" ")
                .append(LlvmWrappers.newFunctionNameFromClassName(type.getName())).append("() {").append(endLine);

        InstrCounter newCounter = new InstrCounter();
        ExprEval heapAllocationExpr = LlvmWrappers.heapAllocation(newCounter, type.getName());
        String retLlvmId = heapAllocationExpr.llvmId;
        llvm.append(heapAllocationExpr.llvmCode);

        //Build fields initializing expressions
        for(Formal formal : fieldFormals){

            //Get the field corresponding to the formal
            Field field = getField(formal);

            //Initialize the field and store it
            ExprEval evalFieldInitExpr = field.getInitLlvm(newCounter);
            llvm.append(evalFieldInitExpr.llvmCode);
            String store = formal.llvmStore(evalFieldInitExpr.llvmId, retLlvmId, newCounter);
            llvm.append(store);
        }


        //Return initialized object
        llvm.append(LLVMKeywords.RET.getLlvmName()).append(" ").append(VSOPTypes.getLlvmTypeName(type.getName(), true))
                .append(" ").append(retLlvmId).append(endLine);

        //End of initialization method
        llvm.append("}").append(endLine).append(endLine);

        return llvm.toString();
    }

    /**
     * Get the Field object corresponding to a formal, searching in this class and all its parent
     *
     * @param formal the formal to search
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

            current = classTable.get(current.parentType.getName());
        }


    }

    /**
     * Get the list of formals representing the field of this class and all its parents
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

        //Add a pointer to parent to the class fields
//        Formal parent = new Formal(new Id("parent"), parentType);
//        parent.setParentClass(this.type.getName());
//        parent.setClassField(true);
//        fieldFormals.add(parent);
//        scopeTable.addVariable(parent);



        //Give id and parent class to fields
        int k = 0;
        for(Formal formal : fieldFormals) {
            formal.setClassFieldId(k++);
            formal.setParentClass("%class." + this.getName());
            formal.setScopeTable(this.scopeTable);
        }

        return fieldFormals;

    }

    public Method getMethod(String methodName){
        return this.scopeTable.lookupMethod(methodName, ScopeTable.Scope.GLOBAL);
    }
}