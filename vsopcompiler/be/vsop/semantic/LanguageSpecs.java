package be.vsop.semantic;

import be.vsop.AST.ASTNode;
import be.vsop.AST.ClassItem;
import be.vsop.AST.ClassList;
import be.vsop.AST.Type;
import be.vsop.Compiler;

import java.util.ArrayList;

public class LanguageSpecs {
    public static final Type[] DEFAULT_TYPES = new Type[]{
            new Type("bool"),
            new Type("string"),
            new Type("int32"),
            new Type("unit")
    };

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
            ASTNode tree = compiler.buildAST();

            ArrayList<ASTNode> list = tree.getChildren();

            return (ClassItem) list.get(0).getChildren().get(0);
        }

        public String getName() {
            return name;
        }
    }

    static ClassList getLanguageClasses(){
        ArrayList<ClassItem> items = new ArrayList<>();

        for(DefaultClasses d : DefaultClasses.values())
            items.add(d.getClassItem());

        return new ClassList(items);
    }

    static boolean isDefaultClass(String className){
        for(DefaultClasses d : DefaultClasses.values())
            if(d.name.equals(className))
                return true;
        return false;
    }
}
