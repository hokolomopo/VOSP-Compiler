package be.vsop.semantic;

import be.vsop.AST.ASTNode;
import be.vsop.AST.ClassItem;
import be.vsop.AST.Type;
import be.vsop.Compiler;

import java.util.HashMap;

public class LanguageSpecs {
    public static final Type[] DEFAULT_TYPES = new Type[]{
            new Type("bool"),
            new Type("string"),
            new Type("int32"),
            new Type("unit")
    };
    private HashMap<String, ClassItem> classTable;

    private enum DefaultClasses{
        OBJECT("Object","language/Object.vsop"),
        IO("IO","language/IO.vsop");

        private String name;
        private String fileName;

        DefaultClasses(String name, String fileName) {
            this.name = name;
            this.fileName = fileName;
        }
    }

    LanguageSpecs() {
        classTable = new HashMap<>();
        Compiler compiler;
        ASTNode tree;
        ClassItem curItem;
        for(DefaultClasses d : DefaultClasses.values()) {
            compiler = new Compiler(d.fileName);
            tree = compiler.buildAST();
            curItem = (ClassItem) tree.getChildren().get(0).getChildren().get(0);
            // We assume that there is no error in the default files. If there is, putting null here
            // will trigger an exception, differentiating in this way errors in input files from errors in default files.
            curItem.updateClassTable(classTable, null);
            curItem.fillScopeTable(null, null);
        }
    }

    HashMap<String, ClassItem> getLanguageClassTable() {
        return classTable;
    }

    static boolean isDefaultClass(String className){
        for(DefaultClasses d : DefaultClasses.values())
            if(d.name.equals(className))
                return true;
        return false;
    }

    public static boolean isPrimitiveType(String typeName) {
        for (Type defaultType : DEFAULT_TYPES) {
            if (defaultType.getName().equals(typeName)) {
                return true;
            }
        }
        return false;
    }
}
