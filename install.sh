#!/bin/bash

set -Eeuo pipefail

sudo cp slides.{html,pdf} /srv/http/commitconf25/
sudo cp images/*.png /srv/http/commitconf25/images/

echo done
