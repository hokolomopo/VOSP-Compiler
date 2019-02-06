package tokens;

public class Symbol {

    //TODO : Complete enum
    private enum Symbols{
        OPEN_BRACKET("(");

        private String value;

        Symbols(String value){
            this.value = value;
        }
    }

    private Symbols type;

    public Symbol(Symbols type) {
        this.type = type;
    }

    public Symbols getType() {
        return type;
    }

}
