/* JFlex file for VSOP language */
import tokens.Token;
import tokens.Token.Tokens;
import java.util.HashMap;
%%

%class VSOPLexer
%unicode
%line
%column
%type Token

%{
  StringBuffer string = new StringBuffer();

  HashMap<String, Tokens> keywordsMap = Token.Tokens.getKeywordsHashMap();
  int commentLevel = 0;
%}

lowercaseLetter = [a-z]
uppercaseLetter = [A-Z]
letter = {lowercaseLetter} | {uppercaseLetter}
binDigit = [0-1]
digit = [1-9]
hexDigit = {digit} | [a-fA-F]


tab = \t // CHARACTER TABULATION
lf = \n // LINE FEED (LF)
ff = \f // FORM FEED (FF)
cr = \r // CARRIAGE RETURN
space = " " // SPACE
Whitespace = {space} | {tab} | {lf} | {ff} | {cr} | {cr}{lf};
LineTerminator = {lf} | {cr} | {cr}{lf};

Identifier = {lowercaseLetter} ({letter} | _)*

DecIntegerLiteral = 0 | [1-9][0-9]*

InputCharacter = [^\r\n]
LineComment = "//" {InputCharacter}* {LineTerminator}?

%state STRING
%state COMMENT
%%

<YYINITIAL> {
  /* identifiers */
  {Identifier}                   { Tokens t = keywordsMap.get(yytext());
                                    if(t == null) return new Token(Tokens.IDENTIFIER);
                                    return new Token(t);}

  /* literals */
  {DecIntegerLiteral}            { return new Token(Tokens.INTEGER_CONSTANT); }
  \"                             { string.setLength(0); yybegin(STRING); }

  /* operators */

  /* whitespace */
  {Whitespace}                   { /* ignore */ }
  {LineComment}                  { /* ignore */ }


  "/*"                             { commentLevel = 1; yybegin(COMMENT);}
}

<STRING> {
  \"                             { yybegin(YYINITIAL);
                                   return new Token(Tokens.STRING_CONSTANT, string.toString());}
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}

<COMMENT>{
  [^]                              { /* ignore */ }

  "/*"                             { commentLevel++; }
  "*/"                             { if(commentLevel == 1) yybegin(YYINITIAL);
                                        else commentLevel--;}
  <<EOF>> { throw new Error("EOF in comment"); }
}
/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                    yytext()+">"); }
