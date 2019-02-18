/* JFlex file for VSOP language */
import exceptions.LexerError;import tokens.Token;
import tokens.Token.Tokens;
import java.util.HashMap;

/**
 * This class is a lexer for the VSOP language.
 */
%%

%class VSOPLexer
%unicode
%line
%column
%type Token

%{
  StringBuffer string = new StringBuffer();

  HashMap<String, Tokens> keywordsMap = Token.Tokens.getKeywordsHashMap();
  HashMap<String, Tokens> operatorsMap = Token.Tokens.getOperatorsHashMap();
  int commentLevel = 0;
  int line;
  int column;

  int getLineNumber() {
    return yyline + 1;
  }

  int getColumnNumber() {
    return yycolumn + 1;
  }
%}

lowercaseLetter = [a-z]
uppercaseLetter = [A-Z]
letter = {lowercaseLetter} | {uppercaseLetter}

binDigit = [0-1]
digit = [0-9]
hexDigit = {digit} | [a-fA-F]

hexaPrefix = 0x
binaryPrefix = 0b

tab = \t // CHARACTER TABULATION
lf = \n // LINE FEED (LF)
ff = \f // FORM FEED (FF)
cr = \r // CARRIAGE RETURN
space = " " // SPACE
Whitespace = {space} | {tab} | {lf} | {ff} | {cr} | {cr}{lf}
lineTerminator = {lf} | {cr} | {cr}{lf}

Identifier = {lowercaseLetter} ({letter} | _ | {digit})*
typeIdentifier = {uppercaseLetter} ({letter} | _ | {digit})*

InputCharacter = [^\r\n]
LineComment = "//" {InputCharacter}* {lineTerminator}?

forbiddenInString = "\0" | {lineTerminator}
escapedChar = \\b | \\t | \\n | \\r | \\\" | \\\\ | \\x

%state STRING
%state COMMENT
%state HEXA_LITERAL
%state BIN_LITERAL
%state INT_LITERAL

%%

<YYINITIAL> {
  /* identifiers */
  {Identifier}                   { Tokens t = keywordsMap.get(yytext());
                                    if(t == null) return new Token(Tokens.IDENTIFIER, yytext(), yyline, yycolumn);
                                    return new Token(t, yyline, yycolumn);}
  {typeIdentifier}               { return new Token(Tokens.TYPE_IDENTIFIER, yytext(), yyline, yycolumn); }

  /* literals */
  \"                             { string.setLength(0); yybegin(STRING); line = yyline; column = yycolumn; }
  {digit}                        { string.setLength(0); yybegin(INT_LITERAL); string.append(yytext()); line = yyline; column = yycolumn; }
  {hexaPrefix}                   { string.setLength(0); yybegin(HEXA_LITERAL); line = yyline; column = yycolumn; }
  {binaryPrefix}                 { string.setLength(0); yybegin(BIN_LITERAL); line = yyline; column = yycolumn; }

  /* whitespace */
  {Whitespace}                   { /* ignore */ }
  {LineComment}                  { /* ignore */ }


  "(*"                           { commentLevel = 1; yybegin(COMMENT);}

  /* operators and error fallback */
  [^] | "<=" | "<-"              { Tokens t = operatorsMap.get(yytext());
                                 if(t == null) throw new LexerError("Illegal character :" + yytext());
                                 return new Token(t, yyline, yycolumn);}
}

<STRING> {
  \"                             { yybegin(YYINITIAL);
                                     return new Token(Tokens.STRING_LITERAL, string.toString(), line, column);}
  {forbiddenInString}            {throw new LexerError("Illegal symbol in string");}
  <<EOF>>                        {throw new LexerError("EOF in string");}
  \\{lineTerminator}             { /* ignore */ }
  {escapedChar}                  { string.append( yytext() ); } //TODO maybe modifier l'action pour écrire des \x0a etc
  \\[^]                          {throw new LexerError("Invalid escape sequence" + yytext());}
  [^]                            { string.append( yytext() ); }
}

<COMMENT>{
  "(*"                             { commentLevel++; }
  "*)"                             { if(commentLevel == 1) yybegin(YYINITIAL);
                                        else commentLevel--;}
  <<EOF>> { throw new Error("EOF in comment"); }

  [^]                              { /* ignore */ }
}

<HEXA_LITERAL>{
  {hexDigit}                       {string.append(yytext());}
  [g-zG-Z] | _                     {throw new LexerError("Illegal symbol in hexadecimal number");}
  [^]                              {yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString(), 16)), line, column);}
}

<BIN_LITERAL>{
  {binDigit}                       {string.append(yytext());}
  [2-9] | {letter} | _             {throw new LexerError("Illegal symbol in binary number" + yytext());}
  [^]                              {yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString(), 2)), line, column);}
}

<INT_LITERAL>{
  {digit}                           {string.append(yytext());}
  {letter} | _                      {throw new LexerError("Illegal symbol in decimal number");}
  [^]                               {yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.INT_LITERAL, string.toString(), line, column);}
}
