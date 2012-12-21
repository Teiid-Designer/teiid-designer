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

my $line;

#################
#
# Show help and exit
#
#################
sub usage {
	print "Usage: $0 [-b <jbt repo branch>] [-h]\n";
	print "-b - jboss tools branch from which to get the base target file\n";
	exit 1;
}

# jboss tools branch to download target file from
my $jbt_branch = "jbosstools-4.0.0.Beta2x";

my $help;
usage() if ( ! GetOptions('help|?' => \$help, 'b=s' => \$jbt_branch)
					   or defined $help );

# jboss tools target file
my $jbt_target_file = "multiple.target";

# jboss tools target file url
my $jbt_target_url = "https://raw.github.com/jbosstools/jbosstools-build/$jbt_branch/target-platforms/jbosstools-JunoSR1a/multiple";

print "Base target definition file is $jbt_target_file\n";
unlink("$jbt_target_file");

print "Downloading target file from $jbt_target_url/$jbt_target_file ...\n";
`wget "$jbt_target_url/$jbt_target_file"`;

unless (-e $jbt_target_file) {
  print "$jbt_target_file not found ... exiting\n";
	exit 1;
}

open FILE, "$jbt_target_file" or die "Couldn't read $jbt_target_file file: $!";
binmode FILE;
my $base_target = join("", <FILE>);
close FILE;

# 
# Construct the jboss tools required features into a location snippet
# to be included in the target file
#
my $jbt_repo_snippet = "\n\n<!-- Extra repository for jboss tools --> \
<location includeAllPlatforms=\"false\" includeConfigurePhase=\"false\" includeMode=\"slicer\" includeSource=\"true\" type=\"InstallableUnit\">\n";

my @feature;

open(PROPERTIES, "<jboss.feature.properties") or die "Cannot open $!";
my @features = (<PROPERTIES>);
close(PROPERTIES);

foreach $line (@features) {

	if ($line =~ m/^#/) {
		next;
	}

	@feature = split(/=/, $line);

  my $name = $feature[0];
	my $version = $feature[1];

	if (defined $name && length($name) > 0 && defined $version && length($version)) {
		$jbt_repo_snippet .= "\t<unit id=\"$name\" version=\"$version\"/>\n"; 
	}
}

$jbt_repo_snippet .= "\t<repository location=\"http://download.jboss.org/jbosstools/updates/development/juno\"/>\n";
$jbt_repo_snippet .= "</location>\n\n\n";

my $mockito_unit;
my $objenesis_unit;
$mockito_unit = "\t\t\t<unit id=\"org.mockito\" version=\"1.8.4.v201102171835\"/>\n";
$objenesis_unit = "\t\t\t<unit id=\"org.objenesis\" version=\"1.0.0.v201105211943\"/>\n";

#
# Update the base target by modifying its
# name and appending the jboss tools location
#
my $td_contents;
foreach $line (split /^/ , $base_target) {
	
	# Update the name
	if ($line =~ m/^<target/) {
		$line =~ s/name=.*/name=\"teiid-designer\">/;
	}
	elsif ($line =~ m/<\/locations>/) {
		$line = $jbt_repo_snippet . $line;
	}
	elsif ($line =~ m/<!-- Orbit bundles -->/) {
		$line = $line . $mockito_unit;
		$line = $line . $objenesis_unit;
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
