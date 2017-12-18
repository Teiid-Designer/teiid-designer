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
import java.io.InputStream;

public class TempInputStream {
	File realTempFile;
	InputStream realInputStream;
	
	public TempInputStream(InputStream realInputStream, File realTempFile) throws IOException {
		super();
		
		this.realTempFile = realTempFile;
		this.realInputStream = realInputStream;
	}
	
	public InputStream getRealInputStream() {
		return this.realInputStream;
	}
	
	public boolean deleteTempFile() {
    	// Clean up temporary VDB file
    	if( realTempFile.exists() ) {
    		return realTempFile.delete();
    	}
    	
    	return false;
	}
}
