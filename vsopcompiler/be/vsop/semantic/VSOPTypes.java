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
        for (VSOPTypes type : VSOPTypes.values())
            if(type.typeName.equals(typeName))
                return type.llvmName;
        return "%class." + typeName;

    }
}
