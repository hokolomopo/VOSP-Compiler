#!/bin/bash
rm vsopcompiler.tar.xz
tar --exclude='*.class' --exclude='TestMain.java' -cJf vsopcompiler.tar.xz vsopcompiler/*
