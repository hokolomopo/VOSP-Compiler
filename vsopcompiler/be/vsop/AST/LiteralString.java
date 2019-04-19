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
    public ExprEval evalExpr(InstrCounter counter) {
        String value = String.format("getelementptr inbounds ([%d x i8], [%d x i8]* %s, i32 0, i32 0)", getRawValue().length() + 1, getRawValue().length() + 1, stringId);
        return new ExprEval(value, "", true);
    }

    /**
     * @return String value without ""
     */
    public String getRawValue(){
        return value.substring(1, value.length() - 1);
    }
}
