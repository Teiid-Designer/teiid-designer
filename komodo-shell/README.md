# Komodo Command Shell 

## Summary

Komodo extends the S-RAMP interactive shell.  The S-RAMP interactive shell can be accessed in the S-RAMP 
distro from the "bin" directory.  The shell provides a way to connect to a Komodo repository
and perform queries and updates. 

## How It Works

First clone https://github.com/Governance/s-ramp-distro locally and then run:

    $ mvn clean package

This will create an S-RAMP distribution zip in the target folder of the s-ramp-distro project. Unzip
this file and you will see a 'bin' folder that contains the s-ramp.sh script.

Now in the komodo root, run:

	$ mvn clean install

This will build all the komodo JARs including the komodo-shell JAR which has the custom commands 
that need to be added to the shell's classpath.  This can be done by adding the resulting project JAR 
by copying it and other komodo required JARs to the interactive shell's home directory:

    $ mkdir ~/.s-ramp
    $ mkdir ~/.s-ramp/commands
    From komodo-shell directory, run the following:
    $ cp target/*.jar ~/.s-ramp/commands/.
    $ cp ../komodo-common/target/*.jar ~/.s-ramp/commands/.
	$ cp ../komodo-repository/target/*.jar ~/.s-ramp/commands/.

At this point you should use the scripts in the distribution's 'bin' directory to run 
the S-RAMP Interactive Shell.  Once running, you should see the new komodo commands
in the help and you should be able to execute them. For example:

    s-ramp> komodo:connectKomodo {URL_TO_REPOSITORY}
