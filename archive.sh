#!/bin/sh
name=vsopcompiler.tar.xz
rm $name
tar cfJ $name vsopcompiler/libs/* vsopcompiler/tests vsopcompiler/*.java vsopcompiler/manifest.mf vsopcompiler/vsopc vsopcompiler/tokens/*.java vsopcompiler/exceptions/*.java vsopcompiler/AST/*.java vsopcompiler/parser/*.java vsopcompiler/lexer/*.java vsopcompiler/Makefile
