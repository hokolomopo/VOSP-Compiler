package tokens;

public class Operator {

    //TODO : Complete enum
    private enum Operators{
        PLUS("+");

        private String value;

        Operators(String value){
            this.value = value;
        }
    }

    private Operators type;

    public Operator(Operators type) {
        this.type = type;
    }

    public Operators getType() {
        return type;
    }

}
