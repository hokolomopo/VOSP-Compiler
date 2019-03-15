package be.vsop.AST;

import be.vsop.exceptions.semantic.ClassAlreadyDeclaredException;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;

public class ClassItem extends ASTNode{
	private Type type;
	private Type parentType;
	private ClassElementList cel;

	public ClassItem(Type type, ClassElementList cel) {
		this(type, new Type("Object"), cel);
	}

	public ClassItem(Type type, Type parentType, ClassElementList cel) {
		super(type.getLine(), type.getColumn());
		this.type = type;
		this.parentType = parentType;
		this.cel = cel;

		this.children = new ArrayList<>();
		this.children.add(cel);

		//Don't add Types as children because we already check for missing type of class declaration in SyntaxAnalyzer
		//when checking for cyclic inheritance
	}

	@Override
	public void updateClassTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		this.scopeTable = scopeTable;
		if(scopeTable.lookupClass(type.getName()) != null)
			errorList.add(new ClassAlreadyDeclaredException(type.getName(), line, column));
		else
			scopeTable.addClass(this);
	}

	@Override
	public void updateClassItems(ScopeTable scopeTable, ArrayList<SemanticException> errorList) {
		//Create new ScopeTable for this class because every class has it's own scope
		this.scopeTable = new ScopeTable(scopeTable);

		//Add self field
		Formal self = new Formal(new Id("self"), type);
		this.scopeTable.addVariable(self);

		//Update table with Methods and fields
		if(children != null)
			for(ASTNode node : children)
				node.updateClassItems(this.scopeTable, errorList);

	}

	@Override
	public void checkScope(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
		super.checkScope(this.scopeTable, errorList);
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

}