# The Teiid Designer project

## Summary

This is the official Git repository for the Teiid Designer project.

Up to our recent 7.8 release this summer (2012) the codebase was housed in our [SVN](http://anonsvn.jboss.org/repos/tdesigner/) repository

Teiid Designer is an open source visual tool that enables rapid, model-driven definition, integration, management and testing of data services without programming using the Teiid runtime framework. With Teiid Designer, not only do you create source data models and map your sources to target formats using a visual tool, but you can also:

*	create a virtual database (or VDB) containing your models which you deploy to Teiid server and then access your data.
*	resolve semantic differences
*	create virtual data structures at a physical or logical level
*	use declarative interfaces to integrate, aggregate, and transform the data on its way from source to a target format which is compatible and optimized for consumption by your applications

This allows you to abstract the structure of the information you expose to and use in your applications from the underlying physical data structures. With Teiid Designer, data services are defined quickly, the resulting artifacts are easy to maintain and reuse, and all the valuable work and related metadata are saved for later reference.

You can use Teiid Designer to integrate multiple sources, and access them using the common data access standards:

*	Web Services / SOAP / XML
*	JDBC / SQL
*	ODBC / SQL


For more information on Teiid Desginer, including getting started guides, reference guides, and downloadable binaries, visit the project's website at [http://www.jboss.org/teiiddesigner/](http://www.jboss.org/teiiddesigner/)
or follow us on our [blog](http://teiid.blogspot.com/) or on [Twitter](https://twitter.com/teiiddesigner). Or hop into our [IRC chat room](http://www.jboss.org/teiiddesigner/chat)
and talk our community of contributors and users.

## Get the code

The easiest way to get started with the code is to [create your own fork](http://help.github.com/forking/) of this repository, and then clone your fork:

	$ git clone git@github.com:<you>/teiiddesigner.git
	$ cd teiiddesigner
	$ git remote add upstream git://github.com/TeiidDesigner/teiiddesigner.git
	
At any time, you can pull changes from the upstream and merge them onto your master:

	$ git checkout master               # switches to the 'master' branch
	$ git pull upstream master          # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
	$ git push origin                   # pushes all the updates to your fork, which should be in-sync with 'upstream'

The general idea is to keep your 'master' branch in-sync with the 'upstream/master'.

## Building Teiid Designer

Then, we use Maven 3.x to build our software. The following command compiles all the code, installs the JARs into your local Maven repository, and run all of the unit tests:

	$ mvn clean install -s settings.xml

BTW, that '-s settings.xml' argument uses the 'settings.xml' file in our codebase, which is set up to use the JBoss Maven repository. 

That command takes a while -- we do have over 12K unit tests. So if need be, your builds can skip the tests:

	$ mvn clean install -s settings.xml -DskipTests
	
If you have *any* trouble building (or don't like the '-s settings.xml' usage), check the [detailed build instructions and tips](http://community.jboss.org/wiki/TeiidDesignerAndMaven).

## Contribute fixes and features

ModeShape is open source, and we welcome anybody that wants to participate and contribute!

If you want to fix a bug or make any changes, please log an issue in the [Teiid Designer JIRA](https://issues.jboss.org/browse/TEIIDDES) describing the bug
or new feature. Then we highly recommend making the changes on a topic branch named with the JIRA issue number. For example, this command creates
a branch for the TEIIDDES-1234 issue:

	$ git checkout -b teiddes-1234

After you're happy with your changes and a full build (with unit tests) runs successfully, commit your changes on your topic branch
(using [really good comments](http://community.jboss.org/wiki/TeiidDesignerDevelopmentGuidelines#Commits)). Then it's time to check for
and pull any recent changes that were made in the official repository:

	$ git checkout master               # switches to the 'master' branch
	$ git pull upstream master          # fetches all 'upstream' changes and merges 'upstream/master' onto your 'master' branch
	$ git checkout mode-1234            # switches to your topic branch
	$ git rebase master                 # reapplies your changes on top of the latest in master
	                                      (i.e., the latest from master will be the new base for your changes)

If the pull grabbed a lot of changes, you should rerun your build to make sure your changes are still good.
You can then either [create patches](http://progit.org/book/ch5-2.html) (one file per commit, saved in `~/teiddes-1234`) with 

	$ git format-patch -M -o ~/teiddes-1234 orgin/master

and upload them to the JIRA issue, or you can push your topic branch and its changes into your public fork repository

	$ git push origin teiddes-1234         # pushes your topic branch into your public fork of ModeShape

and [generate a pull-request](http://help.github.com/pull-requests/) for your changes. 

We prefer pull-requests, because we can review the proposed changes, comment on them,
discuss them with you, and likely merge the changes right into the official repository.

