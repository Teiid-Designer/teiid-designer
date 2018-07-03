#!/bin/bash

#################
#
# Show help and exit
#
#################
function show_help {
	echo "Usage: $0 [-d] [-h]"
	echo "-d - enable maven debugging"
  exit 1
}

#
# This script should be executed from the directory
# it is located in. Try and stop alternatives
#
SCRIPT=`basename "$0"`

if [ ! -f $SCRIPT ]; then
  echo "This script must be executed from the same directory it is located in"
  exit 1
fi

#
# We must be in the same directory as the script so work
# out the root directory and find its absolute path
#
SCRIPT_DIR=`pwd`

echo "Script directory = $SCRIPT_DIR"

#
# Set root directory to be the parent of this.
#
ROOT_DIR="$SCRIPT_DIR/../.."
echo "Root Directory: $ROOT_DIR"

#
# By default debug is turned off
#
DEBUG=0

#
# Determine the command line options
#
while getopts "dh" opt;
do
	case $opt in
	d) DEBUG=1 ;;
	h) show_help ;;
	*) show_help ;;
	esac
done

#
# Source directory containing teiid designer codebase
# Should be the same directory as the build script location
#
SRC_DIR="${SCRIPT_DIR}"

#
# Maven repository to use.
# Ensure it only contains teiid related artifacts and
# does not clutter up user's existing $HOME/.m2 repository
#
if [ -z "${M2_REPO}" ]; then
  LOCAL_REPO="${ROOT_DIR}/m2-repository"
else
  LOCAL_REPO=${M2_REPO}
fi
echo "Local Repository: $LOCAL_REPO"

#
# Maven command
#  -Denforcer.skip=true"
MVN="mvn clean verify"

#
# Turn on dedugging if required
#
if [ "${DEBUG}" == "1" ]; then
  MVN_FLAGS="-e -X"
fi

#
# Maven options
# -P <profiles> : The profiles to be used for downloading jbosstools artifacts
# -D maven.repo.local : Assign the $LOCAL_REPO as the target repository
#
MVN_FLAGS="${MVN_FLAGS} -U -P generate-target -Dmaven.repo.local=${LOCAL_REPO} -Dno.jbosstools.site -Dtycho.localArtifacts=ignore"

echo "==============="

# Build teiid designer target platform
echo "Build the teiid designer target platform"
echo "Executing ${MVN} ${MVN_FLAGS}"
${MVN} ${MVN_FLAGS}
