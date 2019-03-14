package syntax;

import AST.ASTNode;
import AST.ClassItem;
import parser.Compiler;

import java.util.ArrayList;
import java.util.List;

public class LanguageSpecs {
    private enum DefaultClasses{
        OBJECT("Object","language/Object.vsop"),
        IO("IO","language/IO.vsop");

        private String name;
        private String fileName;

        DefaultClasses(String name, String fileName) {
            this.name = name;
            this.fileName = fileName;
        }

        private ClassItem getClassItem(){
            return parseClass();
        }

        private ClassItem parseClass() {

            Compiler compiler = new Compiler(fileName);
            ASTNode tree = compiler.getAST();

            ArrayList<ASTNode> list = tree.getChildren();

            return (ClassItem) list.get(0);
        }

        public String getName() {
            return name;
        }
    }

    public static List<ClassItem> getLanguageClasses(){
        ArrayList<ClassItem> items = new ArrayList<>();

        for(DefaultClasses d : DefaultClasses.values())
            items.add(d.getClassItem());

        return items;
    }

    public static boolean isDefaultClass(String className){
        for(DefaultClasses d : DefaultClasses.values())
            if(d.name.equals(className))
                return true;
        return false;
    }
}
