/* JFlex example: partial Java language lexer specification */
import java_cup.runtime.*;
import tokens.Token;
import tokens.Token.Tokens;
import java.util.HashMap;

/**
 * This class is a simple example lexer.
 */
%%

%class VSOPLexer
%unicode
%line
%column
%type Token

%{
  StringBuffer string = new StringBuffer();

  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
WhiteSpace     = {LineTerminator} | [ \t\f]

/* comments */
Comment = {TraditionalComment} | {EndOfLineComment} | {DocumentationComment}

TraditionalComment   = "/*" [^*] ~"*/" | "/*" "*"+ "/"
// Comment can be the last line of the file, without line terminator.
EndOfLineComment     = "//" {InputCharacter}* {LineTerminator}?
DocumentationComment = "/**" {CommentContent} "*"+ "/"
CommentContent       = ( [^*] | \*+ [^/*] )*

Identifier = [:jletter:] [:jletterdigit:]*

DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING

%%

/* keywords */
//<YYINITIAL> "abstract"           { return new Token(Tokens.PLUS);}
//<YYINITIAL> "boolean"            { return new Token(Tokens.PLUS);}
//<YYINITIAL> "break"              { return new Token(Tokens.PLUS); }

<YYINITIAL> {
  /* identifiers */
  {Identifier}                   {System.out.println(yytext());return new Token(Tokens.IDENTIFIER); }

  /* literals */
  {DecIntegerLiteral}            { return new Token(Tokens.INTEGER_CONSTANT); }
  \"                             { string.setLength(0); yybegin(STRING); }

  /* operators */
//  "="                            { return new Token(Tokens.) }
//  "=="                           { return symbol(sym.EQEQ); }
//  "+"                            { return symbol(sym.PLUS); }
//
  /* comments */
  {Comment}                      { /* ignore */ }

  /* whitespace */
  {WhiteSpace}                   { /* ignore */ }
}

<STRING> {
  \"                             { yybegin(YYINITIAL); 
                                   return new Token(Tokens.STRING_CONSTANT, string.toString()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}

/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                    yytext()+">"); }
