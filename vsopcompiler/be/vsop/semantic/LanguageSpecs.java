package be.vsop.semantic;

import be.vsop.AST.ASTNode;
import be.vsop.AST.ClassItem;
import be.vsop.AST.Type;
import be.vsop.Compiler;

import java.util.HashMap;

public class LanguageSpecs {
    public final static String MAIN = "main";

    public enum VSOPTypes{
        BOOL("bool", "i1"),
        STRING("string", "i8*"),
        INT32("int32", "i32"),
        UNIT("unit", "void");

        private String typeName;
        private String llvmName;

        VSOPTypes(String typeName, String llvmName) {
            this.typeName = typeName;
            this.llvmName = llvmName;
        }

        public static VSOPTypes getType(String typeName){
            for (VSOPTypes type : VSOPTypes.values())
                if(type.typeName.equals(typeName))
                    return type;
            return null;
        }

        public String getName() {
            return typeName;
        }

        public String getLlvmName() {
            return llvmName;
        }
    }


    private HashMap<String, ClassItem> classTable;

    private enum DefaultClasses{
        OBJECT("Object","Object.vsop"),
        IO("IO","IO.vsop");

        private String name;
        private String fileName;

        DefaultClasses(String name, String fileName) {
            this.name = name;
            this.fileName = fileName;
        }
    }

    LanguageSpecs(String languageDirPath) {
        classTable = new HashMap<>();
        Compiler compiler;
        ASTNode tree;
        ClassItem curItem;
        for(DefaultClasses d : DefaultClasses.values()) {
            if (languageDirPath != null) {
                compiler = new Compiler(languageDirPath + d.fileName);
            } else {
                compiler = new Compiler("language/" + d.fileName);
            }
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
        return VSOPTypes.getType(typeName) != null;
    }
}
