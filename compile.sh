#!/bin/bash
compileLexer(){
	java -jar "libs/jflex-full-1.7.0.jar" -d vsopcompiler/be/vsop/lexer -nobak generator_files/vsop.jflex
}
compileParser(){
	java -jar "libs/java-cup-11b.jar" -interface -destdir vsopcompiler/be/vsop/parser -package be.vsop.parser -parser VSOPParser generator_files/vsop.cup
}
compileJava(){
	cd vsopcompiler
	make vsopc
	cd ..
}
if (($# < 1))
then
	echo "No argument given : recompiling everything. Use 'lex' to compile lexer, 'parse' to compile parser, 'java' to compile classes in vsopcompiler."
	# compile everything
	compileLexer
	compileParser
	compileJava
else
	if test "$1" = "lex"
	then
		compileLexer
	fi
	if test "$1" = "parse"
	then
		compileParser
	fi
	if test "$1" = "java"
	then
		compileJava
	fi
fi

