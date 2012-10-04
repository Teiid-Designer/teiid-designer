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
		echo "Updating $2 from $1 ..."
		svn up $2
	fi
}

#################
#
# Show help and exit
#
#################
function show_help {
	echo "Usage: $0 [-b] [-d] [-h]"
	echo "-b - enable swt bot testing"
	echo "-d - enable maven debugging"
  exit 1
}

#
# This script should be executed from the directory
# it is located in. Try and stop alternatives
#
SCRIPT_DIR=`dirname "$0"`
SCRIPT=`basename "$0"`
ROOT_DIR="$SCRIPT_DIR/.."

if [ ! -f $SCRIPT ]; then
  echo "This script must be executed from the same directory it is located in"
  exit 1
fi

#
# By default skip swt bot tests
#
SKIP_SWTBOT=1

#
# By default debug is turned off
#
DEBUG=0

#
# Determine the command line options
#
while getopts "bdh" opt;
do
	case $opt in
	b) SKIP_SWTBOT=0 ;;
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
LOCAL_REPO="${ROOT_DIR}/m2-repository"

#
# Maven command
#
MVN="mvn clean install"

#
# Maven options
# -P <profiles> : The profiles to be used for downloading jbosstools artifacts
# -D maven.repo.local : Assign the $LOCAL_REPO as the target repository
#
MVN_FLAGS="-P jbosstools-nightly-staging-composite,jbosstools-nightly-staging-composite-soa-tooling,unified.target -Dmaven.repo.local=${LOCAL_REPO}"

#
# Determine whether to skip swt bot tests
# By default, the tests will be skipped.
#
# Use -b switch to enable to perform these tests
#
if [ "${SKIP_SWTBOT}" == "1" ]; then
  echo -e "###\n#\n# Skipping swt bot tests\n#\n###"
	MVN_FLAGS="${MVN_FLAGS} -Dswtbot.test.skip=true"
fi

echo "==============="

# Build and test the teiid designer codebase
echo "Build and install the teiid designer plugins"
cd "${SRC_DIR}"
${MVN} ${MAVEN_FLAGS}
