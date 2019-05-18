package be.vsop.semantic;

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

    public static String getLlvmTypeName(String typeName){
        return getLlvmTypeName(typeName, false);
    }

    public static String getLlvmTypeName(String typeName, boolean pointerOnClass){
        for (VSOPTypes type : VSOPTypes.values())
            if(type.typeName.equals(typeName))
                return type.llvmName;
        if(pointerOnClass && !Character.isUpperCase(typeName.charAt(0)))
            return typeName + "*";
        if (pointerOnClass) {
            return "%class." + typeName + "*";
        }
        return "%class." + typeName;
    }

    public static String getLlvmDefaultInit(String typeName) {
        if (typeName.equals(BOOL.typeName)) {
            return "0";
        } else if (typeName.equals(STRING.typeName)) {
            return LlvmWrappers.emptyString;
        } else if (typeName.equals(INT32.getName())) {
            return "0";
        } else if (typeName.equals(UNIT.getName())) {
            return "";
        } else {
            return "null";
        }
    }
}
