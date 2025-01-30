#!/bin/bash

set -Eeuo pipefail

sudo mkdir -p /srv/http/commitconf25/images
sudo cp slides.{html,pdf} /srv/http/commitconf25/
sudo cp images/*.{jpg,png} /srv/http/commitconf25/images/

echo done
