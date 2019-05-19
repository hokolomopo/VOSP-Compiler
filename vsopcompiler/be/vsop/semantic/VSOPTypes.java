package be.vsop.semantic;

import be.vsop.codegenutil.LlvmWrappers;

/**
 * This enumeration contains the primitive types of the VSOP language, along with their equivalent in llvm
 * and some methods to manipulate them
 */
public enum VSOPTypes{
    BOOL("bool", "i1"),
    STRING("string", "i8*"),
    INT32("int32", "i32"),
    UNIT("unit", "void");

    private String typeName;
    private String llvmName;

    /**
     * Creates a VSOPTypes from a given name and a given llvm name
     *
     * @param typeName the name of the type in VSOP
     * @param llvmName the name of the type in llvm
     */
    VSOPTypes(String typeName, String llvmName) {
        this.typeName = typeName;
        this.llvmName = llvmName;
    }

    /**
     * Returns the VSOPType object corresponding to the given type name
     *
     * @param typeName the type name to search for
     *
     * @return the VSOPType object, or null if not found
     */
    public static VSOPTypes getType(String typeName){
        for (VSOPTypes type : VSOPTypes.values())
            if(type.typeName.equals(typeName))
                return type;
        return null;
    }

    /**
     * Getter for the VSOP name of a VSOPType
     *
     * @return the VSOP name of the type
     */
    public String getName() {
        return typeName;
    }

    /**
     * Getter for the llvm name of a VSOPType
     *
     * @return the llvm name of the type
     */
    public String getLlvmName() {
        return llvmName;
    }

    /**
     * Computes a llvm type name corresponding to the given type name. The difference with getLlvmName
     * is that it can handle non-primitive types. Convenience method for setting pointerOnClass to true
     *
     * @param typeName the VSOP type name to use as a basis
     *
     * @return the equivalent llvm type name
     */
    public static String getLlvmTypeName(String typeName){
        return getLlvmTypeName(typeName, true);
    }

    /**
     * Computes a llvm type name corresponding to the given type name. The difference with getLlvmName
     * is that it can handle non-primitive types.
     *
     * @param typeName the VSOP type name to use as a basis
     * @param pointerOnClass if true, return a pointer on types that are not primitive rather than the structure type
     *
     * @return the equivalent llvm type name
     */
    public static String getLlvmTypeName(String typeName, boolean pointerOnClass){
        for (VSOPTypes type : VSOPTypes.values()) {
            if(type.typeName.equals(typeName)) {
                return type.llvmName;
            }
        }

        // if this is not a primitive type but neither a VSOP type (and thus a type used only for compiling)
        if(pointerOnClass && !Character.isUpperCase(typeName.charAt(0)))
            return typeName + "*";

        if (pointerOnClass) {
            return "%class." + typeName + "*";
        }
        return "%class." + typeName;
    }

    /**
     * Returns the default initialisation of the given type name. Can handle non-primitive types.
     *
     * @param typeName the type name to default initialise
     *
     * @return the initialisation llvm string for this type name
     */
    public static String getLlvmDefaultInit(String typeName) {
        // The default values are given in the manual
        if (typeName.equals(BOOL.typeName)) {
            return "0";
        } else if (typeName.equals(STRING.typeName)) {
            return LlvmWrappers.emptyString;
        } else if (typeName.equals(INT32.getName())) {
            return "0";
        } else if (typeName.equals(UNIT.getName())) {

            // Don't initialise unit-type variables, they will be statically replaced by their only possible value ()
            return "";
        } else {
            return "null";
        }
    }
}
