#!/bin/bash
java -jar vsopcompiler/libs/java-cup-11b.jar -interface -destdir vsopcompiler/parser -package parser -parser VSOPParser cup/vsop.cup
