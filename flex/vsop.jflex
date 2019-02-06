/* JFlex file for VSOP language */
import tokens.*;

%%

%class VSOPLexer
%unicode
%line
%column

%{
  StringBuffer string = new StringBuffer();

%}

lowercaseLetter = [a-z]
uppercaseLetter = [A-Z]
letter = uppercaseLetter | uppercaseLetter

binDigit = [0-1]
digit = [1-9]
hexDigit = digit | [a-fA-F]

tab = \\u0009 // CHARACTER TABULATION
lf = \\u000A // LINE FEED (LF)
ff = \\u000C // FORM FEED (FF)
cr = \\u000B // LINE TABULATION
space = \\u0020 // SPACE
Whitespace = space | tab | lf | ff | cr;

Identifier = lowercaseLetter (letter | _)*

DecIntegerLiteral = 0 | [1-9][0-9]*

%state STRING

%%

<YYINITIAL> {
  /* identifiers */
  {Identifier}                   { return symbol(new Identifier()); }

  /* literals */
  {DecIntegerLiteral}            { return symbol(new IntegerConstant()); }
  \"                             { string.setLength(0); yybegin(STRING); }

  /* operators */

  /* whitespace */
  {Whitespace}                   { /* ignore */ }
}

<STRING> {
  \"                             { yybegin(YYINITIAL);
                                   return new StringConstant(string.toString());}
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
