/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.util;

import java.io.File;
import java.io.IOException;

/**
 * Simple wrapper class that can create and hold a reference to a user's temp file directory
 * and access to indirectly delete it.
 * 
 * This was needed for exporting *.vdb to -vdb.xml as well as with the MED extension/MXD file framework.
 * 
 * @author blafond
 *
 */
public class TempSystemFile {
	File realTempFile;
	
	public TempSystemFile(String name, String suffix) throws IOException {
		super();
		
		this.realTempFile = File.createTempFile(name, suffix);;
	}
	
	public File getTempFile() {
		return this.realTempFile;
	}
	
	public boolean delete() {
    	// Clean up temporary VDB file
    	if( realTempFile.exists() ) {
    		return realTempFile.delete();
    	}
    	
    	return false;
	}

}
