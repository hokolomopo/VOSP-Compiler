package AST;

public class New extends Expr {
	private String typeName;

	public New(String typeName) {
		this.typeName = typeName;
	}

	public void print() {
		System.out.print("New(" + typeName + ")");
	}
}