package be.vsop.AST;

import java.util.ArrayList;

/**
 * This class represents the list of arguments used when calling a function.
 */
public class ArgList extends ASTNode{
	private ArrayList<Expr> args;

	/**
	 * Creates a new ArgList from a previous ArgList, adding a new expression to the list
	 *
	 * @param al the previous list
	 * @param e the expression to add to the list
	 */
	public ArgList(ArgList al, Expr e) {
		this.args = al.args;
		this.args.add(e);

		this.children = new ArrayList<>(args);
	}

	/**
	 * Creates a new empty ArgList
	 */
	public ArgList() {
		this.args = new ArrayList<>();
	}

	/**
	 * See ASTNode, an ArgList is printed as [arg1,arg2,arg3,...]
	 */
	@Override
	public void print(int tabLevel, boolean doTab, boolean withTypes) {
		if(doTab)
			System.out.print(getTab(tabLevel));

		System.out.print("[");

		int i;
		if (args.size() > 0) {
			for (i = 0; i < args.size() - 1; i++) {
				args.get(i).print(tabLevel, false, withTypes);
				System.out.print(",");
			}
			args.get(i).print(tabLevel, false, withTypes);
		}

		System.out.print("]");
	}

	/**
	 * Returns the number of arguments in this list
	 *
	 * @return the number of arguments of the list
	 */
	int size() {
		return args.size();
	}

	/**
	 * Getter for the index'th argument
	 *
	 * @param index the index of the expression required, should be in bounds
	 *
	 * @return the index'th expression
	 */
	Expr get(int index) {
		return args.get(index);
	}

}