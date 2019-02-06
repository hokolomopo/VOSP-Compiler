package tokens;

public class Keyword {
    //TODO : HashMap to optimize

    //TODO : Complete enum
    private enum Keywords{
        WHILE("while");

        private String value;

        Keywords(String value){
            this.value = value;
        }
    }

    private Keywords type;

    public Keyword(Keywords type) {
        this.type = type;
    }

    public Keywords getType() {
        return type;
    }
}
