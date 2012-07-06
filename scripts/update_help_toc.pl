#!/usr/bin/perl

########
#
# Script to update the URL of the help's table of contents in
# the org.teiid.designer.help plugin.
#
# The URL can be set to either RH's product URL or the jboss community URL
#  *  http://docs.redhat.com/docs/en-US/JBoss_Enterprise_Data_Services/${version}
#  *  http://docs.jboss.org/teiid/designer/${version}/user-guide/en-US
#
# The RH product URL will be used by default. Use the -c switch for the community
# URL.
#
# Set the version number using the -v switch.
#
########

use strict;

use Getopt::Long;

#################
#
# Show help and exit
#
#################
sub usage {
	print "Usage: $0 [-c] -v number [-h]\n";
	print "\t-c Use the jboss community url, ie. docs.jboss.org\n";
	print "\t-v Specify version number to include in the url\n";
	exit 1;
}

my $tocfile = "../plugins/org.teiid.designer.help/toc.xml";

unless (-e $tocfile) {
  print "Cannot find the help toc file $tocfile\n";
	exit 1;
}

my $use_community = 0;
my $version;
my $help;

usage() if ( @ARGV < 1 or
          ! GetOptions('help|?' => \$help, 'v=s' => \$version, 'c' => \$use_community)
					          or defined $help );

print "Updating $tocfile urls to ";
if ($use_community == 1) {
	print "use the community url ";
}
else {
	print "use the redhat product url ";
}
print "at version $version ...\n";

# Get the contents of the pom
open(INF, "<$tocfile") or die "Cannot open $!";
my @contents = (<INF>);
close(INF);

# Prepare to modify the pom
open(OUF, ">$tocfile") or die "Cannot open $!";
 
my $line;
foreach $line (@contents) {
  my $x = $line;

	if ($use_community == 0) {
		$x =~ s/href=\".*\/html\//href=\"http:\/\/docs\.redhat\.com\/docs\/en-US\/JBoss_Enterprise_Data_Services\/$version\/html\//g;
	}
	else {
		$x =~ s/href=\".*\/html\//href=\"http:\/\/docs\.jboss\.org\/teiid\/designer\/$version\/user-guide\/en-US\/html\//g;
	}

	print OUF $x;
}

close OUF;
