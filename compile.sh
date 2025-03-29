#!/bin/bash

source $HOME/.sdkman/bin/sdkman-init.sh && sdk env

rm -rf bin && mkdir -p bin && 
    javac --enable-preview --source 24 -Xlint:all -d bin snippets/*.java && 
    javac --enable-preview --source 24 -Xlint:all -d bin snippets/**/*.java && 
    echo build done
