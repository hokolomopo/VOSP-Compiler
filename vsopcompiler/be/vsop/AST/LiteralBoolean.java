package be.vsop.AST;

import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents a VSOP boolean literal
 */
public class LiteralBoolean extends Literal {
    /**
     * This enumeration represents the possible values of a boolean literal.
     * It is useful to avoid (or reunite) typos
     */
    enum BooleanValues {
        TRUE("true", "1"),
        FALSE("false", "0");

        private String vsopName;
        private String llvmName;

        /**
         * Creates a new boolean value ith the given VSOP and llvm names
         *
         * @param vsopName the VSOP name
         * @param llvmName the llvm name
         */
        BooleanValues(String vsopName, String llvmName) {
            this.vsopName = vsopName;
            this.llvmName = llvmName;
        }

        /**
         * Returns the llvm name corresponding to the boolean value with the given VSOP name
         *
         * @param vsopName the VSOP name to look for
         *
         * @return the llvm name of the result
         */
        static String getLlvmName(String vsopName) {
            for (BooleanValues bl : BooleanValues.values()) {
                if (bl.vsopName.equals(vsopName)) {
                    return bl.llvmName;
                }
            }
            return null;
        }
    }

    /**
     * Creates a new LiteralBoolean with the given value
     *
     * @param value the value of the literal (as String)
     */
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

    /**
     * Returns the value as a valid llvm String
     *
     * @return the llvm value
     */
    @Override
    protected String getLlvmValue() {
        return BooleanValues.getLlvmName(value);
    }
}
