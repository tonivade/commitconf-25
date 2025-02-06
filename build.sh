#!/bin/bash

set -Eeuo pipefail

npm i
marp --engine ./engine.mjs --theme ./themes/custom.css slides.md
