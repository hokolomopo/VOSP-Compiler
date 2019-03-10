package AST;

public class Id extends Expr {
	private String name;

	public Call(String name) {
		this.name = name;
	}

	public void print() {
		System.out.print(name);
	}
}