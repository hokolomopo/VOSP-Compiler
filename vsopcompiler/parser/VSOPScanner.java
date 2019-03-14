package parser;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;
import lexer.VSOPLexer;
import tokens.Token;

public class VSOPScanner implements Scanner {
    VSOPLexer lexer;
    ComplexSymbolFactory sf;
    ComplexSymbolFactory.ComplexSymbol prev;

    public VSOPScanner(VSOPLexer lexer, ComplexSymbolFactory sf){
        this.lexer = lexer;
        this.sf = sf;
    }

    @Override
    public Symbol next_token() throws Exception {
        Token t = lexer.yylex();
        if(t == null)
            return sf.newSymbol("EOF", sym.EOF);
        Symbol s =  toSymbol(t);
        prev = (ComplexSymbolFactory.ComplexSymbol)s;
        return s;
    }

    private Symbol toSymbol(Token t){
        Symbol s = sf.newSymbol(t.getTokenType().toString(),
                            t.getTokenType().getSymbolValue(),
                            new ComplexSymbolFactory.Location("Line/Col", t.getLine(),  t.getColumn()),
                            new ComplexSymbolFactory.Location("Line/Col", t.getLine(),t.getColumn()+t.getLength()),
                            t);
        return s;
    }

    public ComplexSymbolFactory.ComplexSymbol getPrev() {
        return prev;
    }
}
