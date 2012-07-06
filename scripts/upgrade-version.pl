#!/usr/bin/perl

########
##
## Upgrade version script
##
## Script for updating the version number of teiid plugins
## to a new version number (command line argument).
##
## Specifically, this updates the manifests and poms of each
## teiid plugin. It ignores the following:
##
## * Any file not in a teiid directory, eg. site/pom.xml;
## * Any file in the deprecated directory;
## * Any file in a bin or target directory.
##
#########

use strict;

use File::Find;
use File::Basename;

#
# This script should be executed from the directory
# it is located in. Try and stop alternatives
#
my $script_dir = `dirname "$0"`;
chomp $script_dir;

my $script = `basename "$0"`;
chomp $script;

# Check we are in the scripts directory
unless (-e $script) {
  print "This script must be executed from the same directory it is located in\n";
  exit 1
}

my $root_dir = "$script_dir/..";
chomp $root_dir;

# Check we have one argument
if (($#ARGV + 1) != 1 ) {
	print "usage: $0 <new version>\n";
	exit 1;
}

# Directory names that should be ignored when 
# finding files to update
my @ignoreDirs = ('deprecated', 'bin', 'target', 'launcher');

# New version number for files
my $new_version = $ARGV[0];
chomp $new_version;

# Number of manifests processed
my $manifests = 0;

# Number of poms processed
my $poms = 0;

# Number of feature xmls processed
my $features = 0;

#
# Process and update each pom.xml file
#
sub process_pom {
  my($filename, $directories, $suffix) = fileparse($_);
  my $line;

	# Get the contents of the pom
  open(INF, "<$_") or die "Cannot open $!";
  my @contents = (<INF>);
  close(INF);

  # Prepare to modify the pom
  open(OUF, ">$_") or die "Cannot open $!";
 
  # Loop through each line and find the version tag.
	# However version tag can appear as a child of parent
	# and build tags so need to ignore these.
	
	my $ignore = 0;
  foreach $line (@contents) {
    my $x = $line;
    
		if ($x =~ m/<parent>/) {
			$ignore++;
		}
		elsif ($x =~ m/<\/parent>/) {
			$ignore--;
		}
		elsif ($x =~ m/<build>/) {
			$ignore++;
		}
		elsif ($x =~ m/<\/build>/) {
			$ignore--;
		}
		elsif ($x =~ m/<version>/ && $ignore == 0) {
			my $old_version = $x;
			$old_version =~ s/<.*?>//g;
			chomp $old_version;
			
      print "$_\t$old_version -> $new_version-SNAPSHOT\n";
      $x = "\t<version>$new_version-SNAPSHOT</version>\n";
		}
 
    print OUF $x;
  }
 
  close(OUF);

	return 1;
}

#
# Process and update each manifest file
#
sub process_manifest {
  my($filename, $directories, $suffix) = fileparse($_);

	# Ignore manifests in directories other than META-INF
	unless ($directories =~ m/\/META-INF\/$/) {
		return 0;
	}

	# Get the contents of the manifest file
	open(INF, "<$_") or die "Cannot open $!";
	my @contents = (<INF>);
	close(INF);
	
	# Prepare to modify the manifest file
	open(OUF, ">$_") or die "Cannot open $!";
	
	my $line;
  foreach $line (@contents) {
	  my $x = $line;

		# If the line starts with Bundle-Version then
		# find the old version for logging and replace
		# the line with our new version
		if ($x =~ m/^Bundle-Version:/) {
			my $old_version = $x;
			$old_version =~ s/^Bundle-Version: //g;
			chomp($old_version);

			print "$_\t$old_version -> $new_version.qualifier\n";
			$x = "Bundle-Version: $new_version.qualifier\n";
		}
		
		print OUF $x;
	}

	close(OUF);

	return 1;
}

#
# Process and update each feature xml
#
sub process_feature_xml {
  my($filename, $directories, $suffix) = fileparse($_);
	my $line;

	# Get the contents of the feature xml
  open(INF, "<$_") or die "Cannot open $!";
  my @contents = (<INF>);
  close(INF);

  # Prepare to modify the feature xml
  open(OUF, ">$_") or die "Cannot open $!";
 
  # Loop through each line and find the version tag.
	# However version tag can appear as a child of parent
	# and build tags so need to ignore these.
	
	my $allow = 0;
  foreach $line (@contents) {
    my $x = $line;

		if ($x =~ m/<feature/) {
			$allow = 1;
		}
   	elsif ($x =~ m/version=/ && $allow == 1) {
			my $old_version = $x;
			$old_version =~ s/version=|"//g;
			chomp $old_version;
			
      print "$_\t$old_version -> $new_version.qualifier\n";
      $x = "      version=\"$new_version.qualifier\"\n";

			# Other versions can appear in include section so 
			# no more updating
			$allow = 0;
		}
 
    print OUF $x;
  }
 
  close(OUF);

  return 1;
}

#
# Update the version number of the user guide
#
sub process_user_guide {
  my($filename, $directories, $suffix) = fileparse($_);
	my $line;

	unless ($directories =~ m/Teiid_Designer_User_Guide/) {
		return;
	}

	# Get the contents of the master xml
  open(INF, "<$_") or die "Cannot open $!";
  my @contents = (<INF>);
  close(INF);

  # Prepare to modify the master xml
  open(OUF, ">$_") or die "Cannot open $!";

  # Loop through each line and find the releaseinfo and
	# productnumber tags.

	my @tags = ('releaseinfo', 'productnumber');
	my $tag;
  foreach $line (@contents) {
    my $x = $line;

		foreach $tag (@tags) {
			if ($x =~ m/<$tag>/) {
				my $old_version = $x;
				$old_version =~ s/<$tag>//g;
				chomp $old_version;

				print "$_\t$old_version -> $new_version\n";
				$x = "    <$tag>$new_version\n";
			}
		}

    print OUF $x;
  }

  close(OUF);
}

sub process_about_properties {
  my($filename, $directories, $suffix) = fileparse($_);
	my $line;

	unless ($directories =~ m/\/org\.teiid\.designer\//) {
		return;
	}

	# Get the contents of the about properties
  open(INF, "<$_") or die "Cannot open $!";
  my @contents = (<INF>);
  close(INF);

  # Prepare to modify the master xml
  open(OUF, ">$_") or die "Cannot open $!";

  # Loop through each line and find the releaseinfo and
	# productnumber tags.

  foreach $line (@contents) {
    my $x = $line;

		if ($x =~ m/Version: /) {
			my $old_version = $x;
			$old_version =~ s/Version: //g;
			$old_version =~ s/\\n|\\//g;
			chomp $old_version;

			print "$_\t$old_version -> $new_version\n";
			$x = "Version: $new_version\\n\\\n";
		}

    print OUF $x;
  }

	close(OUF);
}

#
# Process each file under the root directory
#
sub process {
  my($filename, $directories, $suffix) = fileparse($_);

  # For each keyword test its presence in
	# path and return if present
	foreach (@ignoreDirs) {
		if ($directories =~ m/\/$_\//) {
			return;
		}
	}

	# Only update teiid plugins and features
	unless ($directories =~ m/[t|T]eiid|techpreview/) {
	  return;
	}

  # Process pom.xml files
	if (uc($filename) eq 'POM.XML') {
		$poms = $poms + &process_pom($File::Find::name);
		return;
	}

  # Process manifest files
	if (uc($filename) eq 'MANIFEST.MF') {
		$manifests = $manifests + &process_manifest($File::find::name);
		return;
	}

  # Process feature.xml files
	if (uc($filename) eq 'FEATURE.XML') {
		$features = $features + &process_feature_xml($File::find::name);
		return;
	}

	# Process the user guide master xml
	if (uc($filename) eq 'MASTER.XML') {
		&process_user_guide($File::find::name);
		return;
	}

	# Process the about properties
	if (uc($filename) eq 'ABOUT.PROPERTIES') {
		&process_about_properties($File::find::name);
		return;
	}
}

# Find each file under root directory and pass to process
find({ wanted => \&process, no_chdir => 1 }, $root_dir);

print "Total manifests modified $manifests\n";
print "Total poms modified $poms\n";
print "Total feature xmls modified $features\n";
print "Modified documentation master.xml file\n";
print "Modified org.teiid.designer about.properties\n";
