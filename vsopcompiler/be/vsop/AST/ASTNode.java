package be.vsop.AST;

import be.vsop.codegenutil.InstrCounter;
import be.vsop.exceptions.semantic.SemanticException;
import be.vsop.semantic.LLVMKeywords;
import be.vsop.semantic.LLVMTypes;
import be.vsop.semantic.LanguageSpecs;
import be.vsop.semantic.ScopeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class ASTNode {

    // Public so that the parser can set them without having to implement dedicated constructors each time
    public int line = 0;
    public int column = 0;

    protected ArrayList<ASTNode> children;
    ScopeTable scopeTable;
    HashMap<String, ClassItem> classTable;

    String endLine = "\n";

    protected ASTNode(){}

    public String getLlvm(InstrCounter counter){
        StringBuilder builder = new StringBuilder();

        if(children != null)
            for(ASTNode child: children)
                builder.append(child.getLlvm(counter));
        return builder.toString();
    }

    String firstCommonAncestor(String type1, String type2) {
        HashSet<String> ancestors1 = new HashSet<>();
        Type curType = classTable.get(type1).getType();
        while (curType != null) {
            ancestors1.add(curType.getName());
            curType = classTable.get(curType.getName()).getParentType();
        }
        curType = classTable.get(type2).getType();
        while (curType != null) {
            if (ancestors1.contains(curType.getName())) {
                return curType.getName();
            }
            curType = classTable.get(curType.getName()).getParentType();
        }
        // Should happen only if one type is primitive, as Object is the parent and thus a common ancestor of all classes
        return null;
    }

    boolean isNotChild(String child, String parent) {
        if (LanguageSpecs.isPrimitiveType(child) || LanguageSpecs.isPrimitiveType(parent)) {
            return !child.equals(parent);
        }
        return !firstCommonAncestor(child, parent).equals(parent);
    }

    public void updateClassTable(HashMap<String, ClassItem> classTable, ArrayList<SemanticException> errorList) {
        this.classTable = classTable;
        if(children != null)
            for(ASTNode node : children)
                node.updateClassTable(classTable, errorList);
    }

    public void fillScopeTable(ScopeTable scopeTable, ArrayList<SemanticException> errorList){
        this.scopeTable = scopeTable;
        if(children != null)
            for(ASTNode node : children)
                node.fillScopeTable(scopeTable, errorList);
    }

    public void checkTypes(ArrayList<SemanticException> errorList) {
        if(children != null)
            for(ASTNode node : children)
                node.checkTypes(errorList);
    }

    public void checkScope(ArrayList<SemanticException> errorList){
        if(children != null)
            for(ASTNode node : children)
                node.checkScope(errorList);
    }

    public void print(boolean withTypes){
        print(0, false, withTypes);
    }

    public abstract void print(int tabLevel, boolean doTab, boolean withTypes);

    protected String getTab(int tabLevel){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i < tabLevel;i++)
            s.append('\t');
        return s.toString();
    }

    public ArrayList<ASTNode> getChildren() {
        return children;
    }

    public void getStringLiteral(ArrayList<LiteralString> literalStrings){
        if(children != null)
            for(ASTNode child : children)
                child.getStringLiteral(literalStrings);
    }

    String llvmAllocation(String result, String type) {
        return result + " = " + LLVMKeywords.ALLOCATE.getLlvmName() + " " + type + endLine;
    }

    String llvmStore(LLVMTypes type, String value, String where) {
        return LLVMKeywords.STORE.getLlvmName() + " " + type.getLlvmName() + " " + value + ", " + type.getLlvmName() +
                "* " + where + endLine;
    }

    String llvmSetBool(String result, boolean value) {
        if (value) {
            return result + " = " + LLVMKeywords.ADD.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + "1" + ", " + "0" + endLine;
        } else {
            return result + " = " + LLVMKeywords.ADD.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + "0" + ", " + "0" + endLine;
        }
    }

    String llvmLabel(String label) {
        return label + ":" + endLine;
    }

    String llvmBranch(String label) {
        return LLVMKeywords.BRANCH.getLlvmName() + " " + LLVMKeywords.LABEL.getLlvmName() + " " + label + endLine;
    }

    String llvmBranch(String cond, String labelTrue, String labelFalse) {
        return LLVMKeywords.BRANCH.getLlvmName() + " " + LLVMTypes.BOOL.getLlvmName() + " " + cond + " " + ", " +
                LLVMKeywords.LABEL.getLlvmName() + " %" + labelTrue + ", " + LLVMKeywords.LABEL.getLlvmName() + " %" +
                labelFalse + endLine;
    }

    String llvmPhi(String result, LLVMTypes type, String valIfTrue, String labelIfTrue,
                   String valIfFalse, String labelIfFalse) {
        return result + " = " + LLVMKeywords.PHI.getLlvmName() + " " + type.getLlvmName() + " [" + valIfTrue +
                ", %" + labelIfTrue + "], [" + valIfFalse + ", " + labelIfFalse + "]" + endLine;
    }

    String llvmCast(String result, LLVMKeywords conversion, LLVMTypes fromType, LLVMTypes toType, String fromValue) {
        return llvmCast(result, conversion, fromType.getLlvmName(), toType.getLlvmName(), fromValue);
    }

    String llvmCast(String result, LLVMKeywords conversion, String fromType, LLVMTypes toType, String fromValue) {
        return llvmCast(result, conversion, fromType, toType.getLlvmName(), fromValue);
    }

    String llvmCast(String result, LLVMKeywords conversion, LLVMTypes fromType, String toType, String fromValue) {
        return llvmCast(result, conversion, fromType.getLlvmName(), toType, fromValue);
    }

    private String llvmCast(String result, LLVMKeywords conversion, String fromType, String toType, String fromValue) {
        return result + " = " + conversion.getLlvmName() + " " + fromType +
                " " + fromValue + " " + LLVMKeywords.TO.getLlvmName() + " " + toType + endLine;
    }

    String llvmCall(String result, String retType, String funcName, ArrayList<String> argumentsIds, ArrayList<String> argumentsTypes) {
        StringBuilder ret = new StringBuilder();
        ret.append(result).append(" = ").append(LLVMKeywords.CALL.getLlvmName()).append(" ").append(retType)
                .append(" ").append(funcName).append("(");
        for (int i = 0; i < argumentsIds.size(); i++) {
            ret.append(argumentsTypes.get(i)).append(" ").append(argumentsIds.get(i)).append(", ");
        }
        ret.setLength(ret.length() - 2);
        ret.append(")").append(endLine);
        return ret.toString();
    }
}
