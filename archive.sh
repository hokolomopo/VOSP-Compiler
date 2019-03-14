#!/bin/sh
fileName=vsopcompiler.tar.xz
cp -r language vsopcompiler
cd vsopcompiler
tar cfJ $fileName *
cd ..
cp  vsopcompiler/$fileName .
rm vsopcompiler/$fileName