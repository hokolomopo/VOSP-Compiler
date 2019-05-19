package be.vsop.parser;

import be.vsop.lexer.VSOPLexer;
import be.vsop.tokens.Token;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;

import java.io.IOException;

/**
 * This class is a bridge between the jflex lexer and the cup parser
 */
public class VSOPScanner implements Scanner {
    private VSOPLexer lexer;
    private ComplexSymbolFactory sf;
    private ComplexSymbolFactory.ComplexSymbol prev;

    /**
     * Creates a new scanner from the given lexer and symbol factory
     *
     * @param lexer the lexer that will outputs tokens
     * @param sf the symbol factory that will helps turning tokens into symbols
     */
    public VSOPScanner(VSOPLexer lexer, ComplexSymbolFactory sf){
        this.lexer = lexer;
        this.sf = sf;
    }

    /**
     * Returns the next input Symbol
     *
     * @return the next Symbol read from the input file
     * @throws IOException can be thrown by the lexer
     */
    @Override
    public Symbol next_token() throws IOException {
        Token t = lexer.yylex();
        if(t == null)
            return sf.newSymbol("EOF", sym.EOF);
        Symbol s =  toSymbol(t);
        prev = (ComplexSymbolFactory.ComplexSymbol)s;
        return s;
    }

    /**
     * Converts a Token (returned by the lexer) into a Symbol (taken as input by the parser)
     * @param t the Token to transform
     *
     * @return t converted in a Symbol
     */
    private Symbol toSymbol(Token t){
        return sf.newSymbol(t.getTokenType().toString(),
                            t.getTokenType().getSymbolValue(),
                            new ComplexSymbolFactory.Location("Line/Col", t.getLine(),  t.getColumn()),
                            new ComplexSymbolFactory.Location("Line/Col", t.getLine(),t.getColumn()+t.getLength()),
                            t);
    }

    /**
     * Returns the previous Symbol, useful in case of EOF
     *
     * @return the previous Symbol
     */
    ComplexSymbolFactory.ComplexSymbol getPrev() {
        return prev;
    }
}
