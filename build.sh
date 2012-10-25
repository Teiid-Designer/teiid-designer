#!/bin/bash

##
#
# Default jboss tools branch
#
##
DEFAULT_JBT_BRANCH="trunk"

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
	echo "Usage: $0 [-r <jbt repo branch>] [-d] [-h]"
	echo "-d - enable maven debugging"
	echo "-r - specify a different jboss tools repository branch. By default, $0 uses '${DEFAULT_JBT_BRANCH}'"
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
# jbosstools branch
#
JBT_BRANCH="${DEFAULT_JBT_BRANCH}"

#
# Determine the command line options
#
while getopts "bdhr:" opt;
do
	case $opt in
	d) DEBUG=1 ;;
	r) JBT_BRANCH=${OPTARG} ;;
	h) show_help ;;
	*) show_help ;;
	esac
done

#
# The jboss tools repository
#
JBT_REPO_URL="http://anonsvn.jboss.org/repos/jbosstools/${JBT_BRANCH}"

#
# JBT repositories to checkout
#
JBT_BUILD_REPO="${JBT_REPO_URL}/build"

#
# Local target directories for the JBT checkouts
#
JBT_BUILD_DIR="${ROOT_DIR}/build"

#
# Backup directory for any modified files
#
BACKUP_DIR="${ROOT_DIR}/backup"

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
  MVN_FLAGS="-e -X"
fi

#
# Maven options
# -P <profiles> : The profiles to be used for downloading jbosstools artifacts
# -D maven.repo.local : Assign the $LOCAL_REPO as the target repository
#
MVN_FLAGS="${MVN_FLAGS} -P default,jbosstools-staging-aggregate -Dmaven.repo.local=${LOCAL_REPO}"

#
# Create the backup directory
#
mkdir -p ${BACKUP_DIR}

echo "==============="

# Checkout the JBT build directory, containing the maven parent pom.xml and profiles
checkout ${JBT_BUILD_REPO} ${JBT_BUILD_DIR}

echo "==============="

# Install the maven parent pom and profiles
echo "Install parent pom"
cd "${JBT_BUILD_DIR}/parent"
${MVN} ${MVN_FLAGS}
cd "${SRC_DIR}"

echo "==============="

# Build and test the teiid designer codebase
echo "Build and install the teiid designer plugins"
cd "${SRC_DIR}"
echo "Executing ${MVN} ${MVN_FLAGS}"
${MVN} ${MVN_FLAGS}
