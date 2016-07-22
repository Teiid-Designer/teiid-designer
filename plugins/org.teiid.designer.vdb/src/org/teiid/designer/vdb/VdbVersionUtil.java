/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.util.StringTokenizer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 * 
 * 
 * 
 * >> FROM https://teiid.gitbooks.io/documents/content/v/9.0.x/admin/VDB_Versioning.html
 * 
	The vdb version is set in either the vdb.xml, which is useful for an xml file deployment, 
	or through a naming convention of the deployment name - vdbname.version.vdb, e.g. marketdata.2.vdb. 
	The deployer is responsible for choosing an appropriate version number. If there is already a VDB 
	name/version that matches the current deployment, then connections to the previous VDB will be 
	terminated and its cache entries will be flushed. Any new connections will then be made to the new VDB.

	A simple integer version actually treated as the semantic version X.0.0. If desired a full semantic 
	version can be used instead. A semantic version is up to three integers separated by periods.

	Trailing version components that are missing are treated as zeros - version 1 is the same as 
	1.0.0 and version 1.1 is the same as 1.1.0.

	JDBC and ODBC clients may use a version restriction - -vdbname.X. or vdbname.X.X. - 
		note the trailing '.' which means a VDB that must match the partial version specified. 
		For example vdbname.1.2. could match any 1.2.X version, but would not allow 1.3+ or 1.1 and earlier.
 */

	
	
	// So there will be 2 validations
	
	// if server version < 9.0 then version can be

public class VdbVersionUtil implements VdbConstants {
	
	
	/**
	 * Checks the version string to look for either a single integer value for server version < 9.0 or
	 *   a MVN build version containing 1, 2 or 3 digits separated by a '.' .... like >   2.5.10
	 * @param serverVersion
	 * @param versionStr
	 * @return IStatus a warning if the VDB's version is invalid for the target server version
	 */
	public static IStatus isVdbVersionValid(ITeiidServerVersion serverVersion, String versionStr) {
		
		if( serverVersion.isLessThan(Version.TEIID_9_0) ) {
			// look only at the Integer value
			try {
				Integer.valueOf(versionStr);
			} catch (Exception e) {
				return new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
						"VDB version " + versionStr + " is not valid for the " + serverVersion.toString() + " Server");
			}

		} else {
			// Need to do a 3 digit analysis of the string
			StringTokenizer tokenizer = new StringTokenizer(versionStr, StringConstants.DOT);
			int nTokens = tokenizer.countTokens();
			if( nTokens == 0 || nTokens > 3 ) {
				return new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
						"VDB version " + versionStr + " is not valid for the " + serverVersion.toString() + " Server"); 
			}
			
			while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken();
    			try {
    				Integer.valueOf(word);
    			} catch (Exception e) {
    				return new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
    						"VDB version " + versionStr + " is not valid for the " + serverVersion.toString() + " Server");
    			}
			}
		}
		
		return Status.OK_STATUS;
	}
	
	/**
	 * 
	 * 
	 * @param serverVersion
	 * @param versionStr
	 * @return IStatus a warning if the VDB's version is invalid for the target server version
	 */
	public static IStatus isVdbNameWithVersionValid(ITeiidServerVersion serverVersion, String versionStr) {
		// VDB.xml files that are deployed use the "version" property in the VDB, so only *.vdb file names can contain
		// version numbers between the vdb/file name and the .vdb extension
		//
		// So before 9.0, he limitation was that a VDB can have single digit versions like:
		//    myVdb.1.vdb
		//    myOtherVdb.225.vdb
		//
		// In 9.0 Teiid, the addition of 2 more version segments were added.
		//    myVdb.2.0.1.vdb
		//
		
		// So we need to 
		// 1) Strip the file extension off (if exists)
		// 2) Check for '.' in name
		// 3) Tokenize the string
		// 4) ignore the first segment/token
		// 5) check that 
		
		if( serverVersion.isLessThan(Version.TEIID_9_0) ) {
			// look only at the Integer value
			try {
				Integer.valueOf(versionStr);
			} catch (Exception e) {
				return new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
						"VDB version " + versionStr + " is not valid for the " + serverVersion.toString() + " Server");
			}
		} else {
			// Need to do a 3 digit analysis of the string
			StringTokenizer tokenizer = new StringTokenizer(versionStr, StringConstants.DOT);
			int nTokens = tokenizer.countTokens();
			if( nTokens == 0 || nTokens > 3 ) {
				return new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
						"VDB version " + versionStr + " is not valid for the " + serverVersion.toString() + " Server"); 
			} else if( versionStr.endsWith(StringConstants.DOT) ){
				return new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
						"VDB version " + versionStr + " is not valid for the " + serverVersion.toString() + " Server");
			}
			
			while (tokenizer.hasMoreTokens()) {
                String word = tokenizer.nextToken();
    			try {
    				Integer.valueOf(word);
    			} catch (Exception e) {
    				return new Status(IStatus.WARNING, VdbConstants.PLUGIN_ID,
    						"VDB version " + versionStr + " is not valid for the " + serverVersion.toString() + " Server");
    			}
			}
		}
		
		return Status.OK_STATUS;
	}

}
