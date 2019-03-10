package AST;

public class Literal extends Expr {
	private String value;

	public Literal(String value) {
		this.value = value;
	}

	public void print() {
		System.out.print(value);
	}
}