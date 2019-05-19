package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.VSOPTypes;

import java.util.ArrayList;

/**
 * This class represents a VSOP string literal
 */
public class LiteralString extends Literal{
    private String stringId;

    /**
     * Creates a new LiteralString with the given value
     *
     * @param value the value of the literal (as String)
     */
    public LiteralString(String value) {
        super(value);
    }

    /**
     * See ASTNode
     */
    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = VSOPTypes.STRING.getName();
    }

    /**
     * See ASTNode, a string is printed as its value with escape symbols converted (see convertToEscapeSymbols)
     */
    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print(convertToEscapeSymbols(value));
    }

    /**
     * Returns the value as a valid llvm String (here this is an id because of our string-as-global-constants policy,
     * it may change)
     *
     * @return the llvm value
     */
    @Override
    protected String getLlvmValue() {
        return stringId;
    }

    /**
     * See ASTNode
     */
    @Override
    public void getStringLiteral(ArrayList<LiteralString> literalStrings) {
        literalStrings.add(this);
    }

    /**
     * Tells to this String its location in the list of strings (used to uniquely identify strings)
     *
     * @param id the number of the position of this string
     */
    void setStringId(int id){
        if(id == 0)
            this.stringId = "@.str";
        else
            this.stringId = "@.str." + id;
    }

    /**
     * See Expr
     */
    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        String value = String.format("getelementptr inbounds ([%d x i8], [%d x i8]* %s, i32 0, i32 0)",
                getLlvmLength(), getLlvmLength(), stringId);
        return new ExprEval(value, "", true);
    }

    /**
     * Returns the llvm code that declares this string (as a global constant)
     *
     * @return the llvm code
     */
    String getLlvmDeclaration(){
        String llvmString = getLlvmString();

        return stringId + " = " +
                "private unnamed_addr constant " +
                String.format("[%d x i8] c\"%s\"", getLlvmLength(), llvmString) +
                endLine;
    }

    /**
     * @return String value without the quotes symbols
     */
    private String getRawValue(){
        return value.substring(1, value.length() - 1);
    }

    /**
     * @return the String in llvm (with correct escape symbols, the final \00 and without quotes)
     */
    private String getLlvmString(){
        return convertToLlvmEscapeSymbols(this.getRawValue()) + "\\00";
    }

    /**
     * @return the length of the String in llvm (with correct escape symbols, the final \00 and without quotes)
     */
    private int getLlvmLength(){
        return removeEscapeSymbols(getRawValue()).length() + 1;
    }


    /**
     * @return the given String with escape symbols replaced by their ascii code (\x..)
     */
    private String convertToEscapeSymbols(String str){
        String result = str;
        result = result.replace("\\b", "\\x08");
        result = result.replace("\\f", "\\x0c");
        result = result.replace("\\n", "\\x0a");
        result = result.replace("\\r", "\\x0d");
        result = result.replace("\\t", "\\x09");
        result = result.replace("\\v", "\\x0b");

        return result;
    }

    /**
     * @return the given String with escape symbols replaced by their ascii code in llvm (\..)
     */
    private String convertToLlvmEscapeSymbols(String str){
        String result = str;
        result = result.replace("\\b", "\\08");
        result = result.replace("\\f", "\\0c");
        result = result.replace("\\n", "\\0a");
        result = result.replace("\\r", "\\0d");
        result = result.replace("\\t", "\\09");
        result = result.replace("\\v", "\\0b");
        result = result.replace("\\\"", "\\22");

        return result;
    }

    /**
     * @return the given String with escape symbols replaced with a single character
     */
    private String removeEscapeSymbols(String str){
        String result = str;
        result = result.replace("\\b", "0");
        result = result.replace("\\f", "0");
        result = result.replace("\\n", "0");
        result = result.replace("\\r", "0");
        result = result.replace("\\t", "0");
        result = result.replace("\\v", "0");
        result = result.replace("\\\"", "0");

        return result;
    }


}
