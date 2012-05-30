#!/bin/bash

########
#
# A wrapper for maven
#
# This can be called by an eclipse project's external builder
# so that it gracefully exits if maven is not installed.
#
########

command -v mvn >/dev/null 2>&1 || { echo "A request was made to invoke maven externally but maven is not installed so cannot continue." >&2; exit 1; }

mvn $*
