/* JFlex file for VSOP language */
package be.vsop.lexer;
import be.vsop.exceptions.LexerException;
import java.util.HashMap;
import be.vsop.tokens.Token.Tokens;
import be.vsop.tokens.Token;
import java.util.Stack;

/**
 * This class is a lexer for the VSOP language.
 */
%%

%class VSOPLexer
%unicode
%line
%column
%type Token
%public


%{
  StringBuffer string = new StringBuffer();

  HashMap<String, Tokens> keywordsMap = Token.Tokens.getKeywordsHashMap();
  HashMap<String, Tokens> operatorsMap = Token.Tokens.getOperatorsHashMap();

  Stack<int[]> commentStack = new Stack<int[]>();

  int commentLevel = 0;
  int line;
  int column;
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
whitespace = {space} | {tab} | {lf} | {ff} | {cr} | {cr}{lf}
lineTerminator = {lf} | {cr} | {cr}{lf}

identifier = {lowercaseLetter} ({letter} | _ | {digit})*
typeIdentifier = {uppercaseLetter} ({letter} | _ | {digit})*

inputCharacter = [^\r\n]
lineComment = "//" {inputCharacter}* {lineTerminator}?

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
  {identifier}                   { Tokens t = keywordsMap.get(yytext());
                                    if(t == null) return new Token(Tokens.IDENTIFIER, yytext(), yyline + 1, yycolumn + 1);
                                    return new Token(t, yyline + 1, yycolumn + 1);}
  {typeIdentifier}               { return new Token(Tokens.TYPE_IDENTIFIER, yytext(), yyline + 1, yycolumn + 1); }

  /* literals */
  \"                             { string.setLength(0); yybegin(STRING); line = yyline; column = yycolumn; string.append("\""); }
  {digit}                        { string.setLength(0); yybegin(INT_LITERAL); string.append(yytext()); line = yyline; column = yycolumn; }
  {hexaPrefix}                   { string.setLength(0); yybegin(HEXA_LITERAL); line = yyline; column = yycolumn; }
  {binaryPrefix}                 { string.setLength(0); yybegin(BIN_LITERAL); line = yyline; column = yycolumn; }

  /* whitespace */
  {whitespace}                   { /* ignore */ }
  {lineComment}                  { /* ignore */ }

  "(*"                           { commentLevel = 1; yybegin(COMMENT); commentStack.clear(); commentStack.push(new int[]{yyline, yycolumn});}

  /* operators and error fallback */
  [^] | "<=" | "<-"              { Tokens t = operatorsMap.get(yytext());
                                 if(t == null) throw new LexerException("Illegal character : <" + yytext()+ ">", yyline + 1, yycolumn + 1);
                                 return new Token(t, yyline + 1, yycolumn + 1);}
}

<STRING> {
  \"                             { yybegin(YYINITIAL); string.append("\"");
                                     return new Token(Tokens.STRING_LITERAL, string.toString(), line + 1, column + 1);}
  {forbiddenInString}            {throw new LexerException("Illegal symbol < " + yytext() + ">  in string", yyline + 1, yycolumn + 1);}
  <<EOF>>                        {throw new LexerException("EOF in string", line+ 1, column+ 1);}
  \\{lineTerminator}(" " | \t)*  { /* ignore */ }
  {escapedChar}                  { string.append( yytext() ); }
  \\[^]                          {throw new LexerException("Invalid escape sequence: <" + yytext() + ">", yyline + 1, yycolumn + 1);}
  [^]                            { string.append( yytext() ); }
}

<COMMENT>{
  "(*"                             { commentLevel++; commentStack.push(new int[]{yyline, yycolumn});}
  "*)"                             { if(commentLevel == 1) yybegin(YYINITIAL);
                                        else commentLevel--; commentStack.pop();}
  <<EOF>>                          { int[] lineCol = commentStack.pop(); throw new LexerException("EOF in comment", lineCol[0] + 1, lineCol[1] + 1); }
  [^]                              { /* ignore */ }
}

<HEXA_LITERAL>{
  {hexDigit}                       {string.append(yytext());}
  [g-zG-Z] | _                     {throw new LexerException("Illegal symbol < " + yytext() + "> in hexadecimal number", line + 1, column + 1);}
  [^]                              {if(string.length() == 0){throw new LexerException("Empty hexadecimal number", line + 1, column + 1);}
                                    yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString(), 16)), line + 1, column + 1);}
  <<EOF>>                          {if(string.length() == 0){throw new LexerException("Empty hexadecimal number", line + 1, column + 1);}
                                    yybegin(YYINITIAL);
                                    return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString(), 16)), line + 1, column + 1); }
}

<BIN_LITERAL>{
  {binDigit}                       {string.append(yytext());}
  [2-9] | {letter} | _             {throw new LexerException("Illegal symbol < " + yytext() + "> in binary number", line + 1, column + 1);}
  [^]                              {if(string.length() == 0){throw new LexerException("Empty binary number", line + 1, column + 1);}
                                    yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString(), 2)), line + 1, column + 1);}
  <<EOF>>                          {if(string.length() == 0){throw new LexerException("Empty binary number", line + 1, column + 1);}
                                    yybegin(YYINITIAL);
                                    return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString(), 2)), line + 1, column + 1); }
}

<INT_LITERAL>{
  {digit}                           {string.append(yytext());}
  {letter} | _                      {throw new LexerException("Illegal symbol < " + yytext() + "> in decimal number", line + 1, column + 1);}
  [^]                               {yybegin(YYINITIAL);
                                    yypushback(yylength());
                                    return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString())), line + 1, column + 1);}
  <<EOF>>                           { yybegin(YYINITIAL);
                                      return new Token(Tokens.INT_LITERAL, String.valueOf(Integer.parseInt(string.toString())), line + 1, column + 1); }
}
