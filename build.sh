#!/bin/bash

set -Eeuo pipefail
shopt -s expand_aliases

rm -rf bin && mkdir -p bin && javac --enable-preview --source 23 -Xlint:all -d bin snippets/*.java && echo build done

alias marp="docker run --rm -v $PWD:/home/marp/app/ -e MARP_USER="$(id -u):$(id -g)" -e LANG=$LANG marpteam/marp-cli"

marp slides.md -o slides.html
marp slides.md -o slides.pdf

