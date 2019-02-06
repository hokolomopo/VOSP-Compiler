package tokens;

public class Identifier {
    private String value = null;

    public Identifier(String value) {
        this.value = value;
    }

    public Identifier() {}

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
