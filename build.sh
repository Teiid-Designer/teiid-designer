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
	echo "Usage: $0 [-b] [-r <jbt repo branch>] [-h]"
	echo "-b - enable swt bot testing"
	echo "-r - specify a different jboss tools repository branch. By default, $0 uses 'branches/soatools-3.3.0.Beta1'"
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
# Default jbosstools branch
#
JBT_BRANCH="branches/soatools-3.3.0.Beta1"

#
# Determine the command line options
#
while getopts "b:r:h:" opt;
do
	case $opt in
	b) SKIP_SWTBOT=0 ;;
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
JBT_REQ_REPO="${JBT_REPO_URL}/requirements"
JBT_TESTS_REPO="${JBT_REPO_URL}/tests"

#
# Local target directories for the JBT checkouts
#
JBT_BUILD_DIR="${ROOT_DIR}/build"
JBT_REQ_DIR="${ROOT_DIR}/requirements"
JBT_TESTS_DIR="${ROOT_DIR}/jbosstools-tests"

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

#
# Create the backup directory
#
mkdir -p ${BACKUP_DIR}

echo "==============="

# Checkout the JBT build directory, containing the maven parent pom.xml and profiles
checkout ${JBT_BUILD_REPO} ${JBT_BUILD_DIR}

echo "==============="

# Fix a current bug in the unified target that includes several features that are
# currenlty invalid

echo "Backup up unified target"
cp ${JBT_BUILD_DIR}/target-platform/unified.target ${BACKUP_DIR}/

echo "Removing invalid feature bundles from unified target ..."
cat ${JBT_BUILD_DIR}/target-platform/unified.target | sed '/org\.drools/d; /org\.mozilla/d; /org\.guvnor/d' > ${JBT_BUILD_DIR}/target-platform/unified.target.new
mv ${JBT_BUILD_DIR}/target-platform/unified.target.new ${JBT_BUILD_DIR}/target-platform/unified.target

echo "=============="

# Checkout the JBT requirements directory. This used quietly by the swt.bot compilation
#
# Note. The swt.bot compilation tests can be skipped but the compilation MUST occur for
#       the build to complete successfully.
checkout ${JBT_REQ_REPO} ${JBT_REQ_DIR}

echo "==============="

# Checkout the JBT tests directory, containing the org.jboss.tools.ui.ext.bot plugin
checkout ${JBT_TESTS_REPO} ${JBT_TESTS_DIR}

echo "==============="

# Install the maven parent pom and profiles
echo "Install parent pom"
cd "${JBT_BUILD_DIR}/parent"
${MVN} ${MAVEN_FLAGS}

echo "==============="

# Install the org.jboss.tools.ui.ext.bot plugin
echo "Build and install the jboss tools swt bot plugin (Required for successful compilation)"
cd "${JBT_TESTS_REPO}/tests/org.jboss.tools.ui.bot.ext.test"
${MVN} ${MAVEN_FLAGS}

echo "==============="

# Build and test the teiid designer codebase
echo "Build and install the teiid designer plugins"
cd "${SRC_DIR}"
${MVN} ${MAVEN_FLAGS}
