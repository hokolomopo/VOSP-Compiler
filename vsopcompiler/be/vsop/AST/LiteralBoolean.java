package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

public class LiteralBoolean extends Literal {
    enum BooleanValues {
        TRUE("true", "1"),
        FALSE("false", "0");

        private String vsopName;
        private String llvmName;

        BooleanValues(String vsopName, String llvmName) {
            this.vsopName = vsopName;
            this.llvmName = llvmName;
        }

        static String getLlvmName(String vsopName) {
            for (BooleanValues bl : BooleanValues.values()) {
                if (bl.vsopName.equals(vsopName)) {
                    return bl.llvmName;
                }
            }
            return null;
        }
    }
    public LiteralBoolean(String value) {
        super(value);
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = VSOPTypes.BOOL.getName();
    }

    @Override
    protected String getLlvmValue() {
        return BooleanValues.getLlvmName(value);
    }
}
