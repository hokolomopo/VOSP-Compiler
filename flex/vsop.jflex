/* JFlex file for VSOP language */
import exceptions.LexerError;import tokens.Token;
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

hexaPrefix = 0x
binaryPrefix = 0b

tab = \t // CHARACTER TABULATION
lf = \n // LINE FEED (LF)
ff = \f // FORM FEED (FF)
cr = \r // CARRIAGE RETURN
space = " " // SPACE
Whitespace = {space} | {tab} | {lf} | {ff} | {cr} | {cr}{lf};
LineTerminator = {lf} | {cr} | {cr}{lf};

Identifier = {lowercaseLetter} ({letter} | _ | {digit})*

DecIntegerLiteral = 0 | [1-9][0-9]*

InputCharacter = [^\r\n]
LineComment = "//" {InputCharacter}* {LineTerminator}?

%state STRING
%state COMMENT
%state HEXA_LITERAL
%state BIN_LITERAL

%%

<YYINITIAL> {
  /* identifiers */
  {Identifier}                   { Tokens t = keywordsMap.get(yytext());
                                    if(t == null) return new Token(Tokens.IDENTIFIER, yytext());
                                    return new Token(t);}

  /* literals */
  {DecIntegerLiteral}            { return new Token(Tokens.INT_LITERAL, yytext()); }
  \"                             { string.setLength(0); yybegin(STRING); }
  {hexaPrefix}                   { string.setLength(0); yybegin(HEXA_LITERAL); }
  {binaryPrefix}                 { string.setLength(0); yybegin(BIN_LITERAL); }

    //TODO : Mettre tous les operators/symbols
  /* operators */
  "+"                            { return new Token(Tokens.PLUS); }

  /* whitespace */
  {Whitespace}                   { /* ignore */ }
  {LineComment}                  { /* ignore */ }


  "(*"                             { commentLevel = 1; yybegin(COMMENT);}
}

//TODO : faire la string correctement pour VSOP, j'ai juste copier coller un truc d'internet là
<STRING> {
  \"                             { yybegin(YYINITIAL);
                                     return new Token(Tokens.STRING_LITERAL, string.toString());}
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}

<COMMENT>{
  [^]                              { /* ignore */ }

  "(*"                             { commentLevel++; }
  "*)"                             { if(commentLevel == 1) yybegin(YYINITIAL);
                                        else commentLevel--;}
  <<EOF>> { throw new Error("EOF in comment"); }
}

//TODO : 0xaf0xa1 : ce sera une parse erreur c'est sûr, mais est ce que c'est aussi une lexer error? Pareil pour les 0b
<HEXA_LITERAL>{
  {hexDigit}                       {string.append(yytext());}
  [g-zG-Z]                         {throw new LexerError("Illegal symbol in hexadecimal number");}
  [^]                              {yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.HEXA_LITERAL, string.toString());}
}

<BIN_LITERAL>{
  {binDigit}                       {string.append(yytext());}
  [2-9] | {letter}                 {throw new LexerError("Illegal symbol in binary number");}
  [^]                              {yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.BIN_LITERAL, string.toString());}
}

/* error fallback */
[^]                              { throw new LexerError("Illegal character <"+
                                                    yytext()+">"); }
