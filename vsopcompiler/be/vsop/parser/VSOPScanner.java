package be.vsop.parser;

import be.vsop.lexer.VSOPLexer;
import be.vsop.tokens.Token;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

public class VSOPScanner implements Scanner {
    private VSOPLexer lexer;
    private ComplexSymbolFactory sf;
    private ComplexSymbolFactory.ComplexSymbol prev;

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
        return sf.newSymbol(t.getTokenType().toString(),
                            t.getTokenType().getSymbolValue(),
                            new ComplexSymbolFactory.Location("Line/Col", t.getLine(),  t.getColumn()),
                            new ComplexSymbolFactory.Location("Line/Col", t.getLine(),t.getColumn()+t.getLength()),
                            t);
    }

    ComplexSymbolFactory.ComplexSymbol getPrev() {
        return prev;
    }
}
