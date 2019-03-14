#!/bin/sh
fileName=vsopcompiler.tar.xz
rm $fileName
tar cfJ $fileName vsopcompiler/libs/* vsopcompiler/tests vsopcompiler/*.java vsopcompiler/manifest.mf vsopcompiler/vsopc vsopcompiler/tokens/*.java vsopcompiler/exceptions/*.java vsopcompiler/AST/*.java vsopcompiler/parser/*.java vsopcompiler/lexer/*.java vsopcompiler/Makefile
