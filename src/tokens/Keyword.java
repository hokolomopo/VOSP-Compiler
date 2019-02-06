package tokens;

public class Keyword {
    //TODO : HashMap to optimize
    private enum Keywords{
        WHILE("while");

        private String value;

        Keywords(String value){
            this.value = value;
        }
    }
}
