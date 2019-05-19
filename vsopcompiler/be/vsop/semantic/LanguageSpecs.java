package be.vsop.semantic;

import be.vsop.AST.ASTNode;
import be.vsop.AST.ClassItem;
import be.vsop.Compiler;

import java.util.HashMap;

/**
 * This class contains some important features of the VSOP language, such as the class table of the default classes
 * (currently IO and Object)
 */
public class LanguageSpecs {
    public final static String SELF = "self";

    private HashMap<String, ClassItem> classTable;

    /**
     * This enum contains all the VSOP default classes along with file in which they are defined
     */
    private enum DefaultClasses{
        OBJECT("Object","Object.vsop"),
        IO("IO","IO.vsop");

        private String name;
        private String fileName;

        /**
         * Creates a DefaultClasses from the given name and file name
         *
         * @param name the name of the default class
         * @param fileName the file name in which it is implemented
         */
        DefaultClasses(String name, String fileName) {
            this.name = name;
            this.fileName = fileName;
        }
    }

    /**
     * Constructs a new LanguageSpecs object by parsing the files in the folder languageDirPath
     *
     * @param languageDirPath the path to the folder containing the implementation of the default classes
     *                        May be null, in which case a default value is used
     */
    LanguageSpecs(String languageDirPath) {
        classTable = new HashMap<>();
        Compiler compiler;
        ASTNode tree;
        ClassItem curItem;
        for(DefaultClasses d : DefaultClasses.values()) {
            if (languageDirPath != null) {
                compiler = new Compiler(languageDirPath + d.fileName, languageDirPath);
            } else {
                compiler = new Compiler("language/" + d.fileName);
            }
            tree = compiler.buildAST();
            curItem = (ClassItem) tree.getChildren().get(0).getChildren().get(0);

            // We assume that there is no error in the default files. If there is, putting null here
            // will trigger an exception, differentiating in this way errors in input files from errors in language files.
            curItem.updateClassTable(classTable, null);
            curItem.fillScopeTable(new ScopeTable(), null);
        }
    }

    /**
     * Getter for the class table of the language classes
     *
     * @return the class table
     */
    HashMap<String, ClassItem> getLanguageClassTable() {
        return classTable;
    }

    /**
     * Whether a given type name represents a default class or not
     *
     * @param className the class name to test
     *
     * @return true if class name represents a default class, false otherwise
     */
    static boolean isDefaultClass(String className){
        for(DefaultClasses d : DefaultClasses.values())
            if(d.name.equals(className))
                return true;
        return false;
    }

    /**
     * Whether a given type name is a primitive type or not
     *
     * @param typeName the type name to test
     *
     * @return true if the given type name represent a primitive type, false otherwise
     */
    public static boolean isPrimitiveType(String typeName) {
        return VSOPTypes.getType(typeName) != null;
    }
}
