#!/bin/bash

source $HOME/.sdkman/bin/sdkman-init.sh && sdk env

rm -rf bin && mkdir -p bin && 
    javac -Xlint:all -d bin snippets/*.java && 
    javac -Xlint:all -d bin snippets/**/*.java && 
    echo build done
