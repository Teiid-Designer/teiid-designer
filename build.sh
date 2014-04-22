#!/bin/bash

#################
#
# Checkout or update from the given repository
#
# param repository url
# param target directory
#
#################
function checkout {
	if [ -z "$1" ]; then
	  echo "No repository for checkout specified ... exiting"
		exit 1
	fi

	if [ -z "$2" ]; then
	  echo "No directory for checkout specified ... exiting"
		exit 1
	fi
	
	if [ ! -d "$2" ]; then
		echo "Checking out $1 to $2 ..."
		svn co $1 $2
	else
		SVNURL=`svn info $2 | grep URL | sed 's/URL: //g'`

		if [ "$1" == "$SVNURL" ]; then
			echo "Updating $2 from $1 ..."
		  svn up $2
		else
			# Could use svn switch but risk running
			# in to conflicts
			echo "Remove different checked out branch"
			rm -rf $2
			
			echo "Checking out $1 to $2 ..."
			svn co $1 $2
		fi
	fi
}

#################
#
# Show help and exit
#
#################
function show_help {
	echo "Usage: $0 [-d] [-h]"
	echo "-d - enable maven debugging"
	echo "-s - skip test execution"
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
# Set root directory to be its parent since we are downloading
# lots of stuff and the relative path to the parent pom is
# ../build/parent/pom.xml
#
ROOT_DIR="$SCRIPT_DIR/.."

#
# By default debug is turned off
#
DEBUG=0

#
# Determine the command line options
#
while getopts "bdhs" opt;
do
	case $opt in
	d) DEBUG=1 ;;
	h) show_help ;;
  s) SKIP=1 ;;
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
LOCAL_REPO="${ROOT_DIR}/m2-repository"

#
# Maven command
#
MVN="mvn clean install"

#
# Turn on dedugging if required
#
if [ "${DEBUG}" == "1" ]; then
  MVN_FLAGS="-e -X -U"
fi

#
# Skip tests
#
if [ "${SKIP}" == "1" ]; then
  SKIP_FLAG="-DskipTests"
fi

#
# Maven options
# -P <profiles> : The profiles to be used for downloading jbosstools artifacts
# -D maven.repo.local : Assign the $LOCAL_REPO as the target repository
#
MVN_FLAGS="${MVN_FLAGS} -P target-platform,multiple.target -Dmaven.repo.local=${LOCAL_REPO} -Dno.jbosstools.site -Dtycho.localArtifacts=ignore ${SKIP_FLAG}"

echo "==============="

# Build and test the teiid designer codebase
echo "Build and install the teiid designer plugins"
cd "${SRC_DIR}"
echo "Executing ${MVN} ${MVN_FLAGS}"
${MVN} ${MVN_FLAGS}
