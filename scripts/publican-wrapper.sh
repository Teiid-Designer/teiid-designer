#!/bin/bash

########
#
# A wrapper for publican
#
# This can be called by an eclipse project's external builder
# so that it gracefully exits if publican is not installed.
#
########

command -v publican >/dev/null 2>&1 || { echo "publican is not installed ... cannot generate documentation. Aborting." >&2; exit 1; }

publican $*

echo "Completed... The generated content is available in the project's target directory."

