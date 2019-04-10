package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassAlreadyDeclaredException;
import be.vsop.exceptions.semantic.MainException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ClassItem extends ASTNode{
	private Type type;
	private Type parentType;
	private ClassElementList cel;

	public ClassItem(Type type, ClassElementList cel) {
		this(type, new Type("Object"), cel);
	}

	public ClassItem(Type type, Type parentType, ClassElementList cel) {
		this.scopeTable = new ScopeTable();
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
			for(ASTNode node : children)
				node.fillScopeTable(this.scopeTable, errorList);
	}

	@Override
	public void checkScope(ArrayList<SemanticException> errorList) {
		//TODO what if the Main class has no main() method, but extends another class which has a main method?
		// simply a matter of putting "local scope only" or not in lookupMethod arguments.
		if (getName().equals("Main")) {
			Method mainMethod = scopeTable.lookupMethod("main");
			if (mainMethod == null) {
				errorList.add(new MainException("The class \"Main\" should contain a \"main\" method", 0, 0));
			} else if (mainMethod.nbArguments() != 0 || !mainMethod.returnType().equals("int32")) {
				errorList.add(new MainException("The class \"Main\" should have no arguments and return an int32",
						mainMethod.line, mainMethod.column));
			}
		}
		super.checkScope(errorList);
	}

	@Override
	public void print(int tabLevel, boolean doTab) {
		if(doTab)
			System.out.print(getTab(tabLevel));
		System.out.print("Class(" + type.getName() + "," + parentType.getName() + ",");

		System.out.println();
		cel.print(tabLevel +1, true);
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
}