package be.vsop.AST;

import be.vsop.codegenutil.ExprEval;
import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;

import java.util.ArrayList;

public class LiteralString extends Literal{
    private String stringId;

    public LiteralString(String value) {
        super(value);
    }

    @Override
    public void checkTypes(ArrayList<SemanticException> errorList) {
        super.checkTypes(errorList);
        typeName = "string";
    }

    @Override
    public void print(int tabLevel, boolean doTab) {
        if(doTab)
            System.out.print(getTab(tabLevel));

        System.out.print(convertToEscapeSymbols(value));
    }


    @Override
    protected String getLlvmValue() {
        return stringId;
    }

    @Override
    public void getStringLiteral(ArrayList<LiteralString> literalStrings) {
        literalStrings.add(this);
    }

    public void setStringId(int id){
        if(id == 0)
            this.stringId = "@.str";
        else
            this.stringId = "@.str." + id;
    }

    public String getStringId() {
        return stringId;
    }

    @Override
    public ExprEval evalExpr(InstrCounter counter, String expectedType) {
        String value = String.format("getelementptr inbounds ([%d x i8], [%d x i8]* %s, i32 0, i32 0)", getLlvmLength(), getLlvmLength(), stringId);
        return new ExprEval(value, "", true);
    }

    /**
     *
     * @return the declaration of the String in llvm
     */
    public String getLlvmDeclaration(){
        String llvmString = getLlvmString();

        StringBuilder declaration = new StringBuilder();
        declaration.append(getStringId()).append(" = ");
        declaration.append("private unnamed_addr constant ");
        declaration.append(String.format("[%d x i8] c\"%s\"", getLlvmLength(), llvmString));
        declaration.append(endLine);

        return declaration.toString();
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
     * @return the given String with escape symbols replaces by their ascii code (\x00)
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
     * @return the given String with escape symbols replaces by their ascii code in llvm (\00)
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
     * @return the given String with escape symbols replaced with single character
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
