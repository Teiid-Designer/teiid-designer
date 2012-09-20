#!/usr/bin/perl

#####################################################################
#
# teiid-designer.target.pl
#
# Generates an eclipse target file that can be used as the target 
# platform for teiid desiger plugin development. It incorporates 
# the following:
# - jboss tools target platform, eg. multiple.target, unified.target
# - jboss tools features
#
#####################################################################

use strict;
use Getopt::Long; 
use LWP::Simple;

#################
#
# Show help and exit
#
#################
sub usage {
	print "Usage: $0 [-b <jbt repo branch>] [-t <target file to copy>] [-h]\n";
	print "-b - jboss tools branch from which to get the base target file\n";
	print "-t - base target file to copy (multiple.target) by default\n";
	exit 1;
}

# jboss tools branch to download target file from
my $jbt_branch = "tags/jbosstools-4.0.0.Alpha1";

# jboss tools target file
my $jbt_target_file = "multiple.target";

# jboss tools features required by teiid designer
my %jbt_required_features = (
	'org.jboss.ide.eclipse.as.serverAdapter.wtp.feature.source.feature.group' => '2.4.0.Alpha1-v20120910-1745-H33',
	'org.jboss.ide.eclipse.as.serverAdapter.wtp.feature.feature.group' => '2.4.0.Alpha1-v20120910-1745-H33'
);

my $help;
usage() if ( ! GetOptions('help|?' => \$help, 'b=s' => \$jbt_branch, 't=s' => \$jbt_target_file)
					   or defined $help );

# jboss tools target file url
my $jbt_target_url = "http://anonsvn.jboss.org/repos/jbosstools/$jbt_branch/build/target-platform";

print "Base target definition file is $jbt_target_file\n";
unlink("$jbt_target_file");

print "Downloading target file from $jbt_target_url/$jbt_target_file ...\n";
my $base_target = get("$jbt_target_url/$jbt_target_file");

if (length($base_target) == 0) {
  print "$jbt_target_file not found ... exiting\n";
	exit 1;
}

# 
# Construct the jboss tools required features into a location snippet
# to be included in the target file
#
my $jbt_repo_snippet = "\n\n<!-- Extra repository for jboss tools --> \
<location includeAllPlatforms=\"false\" includeConfigurePhase=\"false\" includeMode=\"slicer\" includeSource=\"true\" type=\"InstallableUnit\">\n";

my $plugin;
my $version;
while (($plugin, $version) = each(%jbt_required_features)) {
  $jbt_repo_snippet .= "\t<unit id=\"$plugin\" version=\"$version\"/>\n"; 
}

$jbt_repo_snippet .= "\t<repository location=\"http://download.jboss.org/jbosstools/updates/development/juno\"/>\n";
$jbt_repo_snippet .= "</location>\n\n\n";

#
# Update the base target by modifying its
# name and appending the jboss tools location
#
my $td_contents;
my $line;
foreach $line (split /^/ , $base_target) {
	
	# Update the name
	if ($line =~ m/^<target/) {
		$line =~ s/name=.*/name=\"teiid-designer\">/;
	}
	elsif ($line =~ m/<\/locations>/) {
		$line = $jbt_repo_snippet . $line;
	}

	$td_contents = $td_contents . $line;
}

#
# Write out the target to file
#
print "Writing target file teiid-designer.target ...\n";
open TDT, ">teiid-designer.target" || die "\nCannot create target file teiid-designer.target\n";
binmode(TDT, ":utf8");
print TDT $td_contents;
close TDT;
