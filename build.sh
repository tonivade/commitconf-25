#!/bin/bash

set -Eeuo pipefail

npm i || exit 1
node_modules/.bin/marp --engine ./engine.mjs --theme ./themes/custom.css slides.md
