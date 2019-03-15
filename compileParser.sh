#!/bin/bash
java -jar vsopcompiler/libs/java-cup-11b.jar -interface -destdir vsopcompiler/be/vsop/parser -package be.vsop.parser -parser VSOPParser cup/vsop.cup