/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.komodo.vdb.ddl;

import java.io.File;
import org.eclipse.core.resources.IFile;

public class DdlVdb {
//extends BasicVdb {

	File ddlZipFile;
	// XMI VDB requires managing (reading & saving) a "*.vdb" archive file

	/**
	 * 
	 */
	public DdlVdb() {
		super();
	}
	
	public DdlVdb(IFile file) {
		this(file.getFullPath().toFile());
	}
	
	public DdlVdb(File file) throws IllegalArgumentException {
		super();
		if(! file.getAbsolutePath().endsWith("*.vdb") ) {
			throw new IllegalArgumentException("The file " + file.getName() + " is not a VDB archive file");
		}
		this.ddlZipFile = file;
		
	}

	public void export(File destination) {
		// TODO Auto-generated method stub
		
	}
}
