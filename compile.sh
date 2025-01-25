#!/bin/bash

source $HOME/.sdkman/bin/sdkman-init.sh && sdk env

rm -rf bin && mkdir -p bin && 
    javac --enable-preview --source 23 -Xlint:all -d bin snippets/*.java && 
    javac --enable-preview --source 23 -Xlint:all -d bin snippets/**/*.java && 
    echo build done
