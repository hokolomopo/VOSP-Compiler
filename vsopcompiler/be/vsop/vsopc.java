package be.vsop;

import be.vsop.exceptions.LexerException;
import be.vsop.lexer.VSOPLexer;
import be.vsop.tokens.Token;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class vsopc {
    public static void main(String[] args) {
        String fileName = null;
        String mode = "";
        for (String arg : args) {
            if (arg.contains("-")) {
                mode = arg;
            } else {
                fileName = arg;
            }
        }

        if (fileName == null) {
            System.err.println("Missing Argument : path to the file to parse");
            System.exit(-1);
        }

        FileReader reader = null;
        try {
            reader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.err.println("File " + fileName + " not found.");
            System.exit(-1);
        }

        Compiler compiler = new Compiler(fileName);

        if (mode.contentEquals("-lex")) {
            VSOPLexer lexer = new VSOPLexer(reader);

            try {
                while (true) {
                    Token t = lexer.yylex();
                    if (t == null)
                        break;

                    if(t.getTokenType() == Token.Tokens.STRING_LITERAL)
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName() + "," +
                                convertToEscapeSymbols(t.getValue()));
                    else if (t.getValue() != null)
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName() + "," + t.getValue());
                    else
                        System.out.println(t.getLine() + "," + t.getColumn() + "," + t.getTokenType().getName());

                }
            }catch (LexerException e){
                System.err.println(fileName+":"+e.getLine()+":"+e.getColumn()+": lexical error :" + e.getMessage());
                System.exit(-1);
            }catch (IOException e){
                System.err.println("IOException during lexing in " + fileName);
                System.exit(-1);
            }
            System.exit(0);
        }

        if (mode.contentEquals("-parse")) {
            /*TODO improve errors : for instance if a className does not start with an uppercase letter we get :
             * tests/test.vsop:7:7: be.vsop.semantic error :Symbol found is : IDENTIFIER expected Symbols are [] */
            compiler.buildAST().print(false);
        }

        else if(mode.contentEquals("-check")) {
            if (args.length > 2) {
                compiler.doSemanticAnalysis(null, args[2] + "/language/");
            } else {
                compiler.doSemanticAnalysis(null, null);
            }
        }

        else if(mode.contentEquals("-llvm")) {
            if (args.length > 2) {
                compiler.doSemanticAnalysis(null, args[2] + "/language/", false);
            } else {
                compiler.doSemanticAnalysis(null, null, false);
            }

            String llvm = compiler.generateLlvm();
            System.out.println(llvm);

            FileWriter writer;
            try {
                writer = new FileWriter("llvm/result.ll", false);
                writer.write(llvm);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }//TODO else generate executable

    }

    private static String convertToEscapeSymbols(String str){
        String result = str;
        result = result.replace("\\b", "\\x08");
        result = result.replace("\\f", "\\x0c");
        result = result.replace("\\n", "\\x0a");
        result = result.replace("\\r", "\\x0d");
        result = result.replace("\\t", "\\x09");
        result = result.replace("\\v", "\\x0b");

        return result;
    }

}