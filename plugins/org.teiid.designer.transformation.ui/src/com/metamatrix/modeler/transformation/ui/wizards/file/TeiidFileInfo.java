/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.wizards.file;

import java.io.File;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.CoreArgCheck;

public abstract class TeiidFileInfo {
	boolean isFlatFile = false;
	
	/**
	 * 
	 * @param dataFile the Teiid-formatted data file
	 */
	public TeiidFileInfo(File dataFile, boolean isFlatFile) {
		super();
		CoreArgCheck.isNotNull(dataFile, "dataFile is null"); //$NON-NLS-1$
		this.dataFile = dataFile;
		this.isFlatFile = isFlatFile;
	}
	
    /**
     * The unique data file name containing Teiid-formatted relational table data (never <code>null</code> or empty).
     */
	private File dataFile;
	
    /**
     * The unique view table name containing the generated SELECT SQL statement that converts file data into
     * relational columns (never <code>null</code> or empty).
     */
	private String viewTableName;
	
	/**
	 * Current <code>IStatus</code> representing the state of the input values for this instance of
	 * <code>TeiidMetadataFileInfo</code>
	 */
	private IStatus status;
	
	/**
	 * 
	 * @return dataFile the teiid-formatted data <code>File</code>
	 */
	public File getDataFile() {
		return this.dataFile;
	}
	
	/**
	 * 
	 * @return viewTableName the view table name (never <code>null</code> or empty).
	 */
	public String getViewTableName() {
		return this.viewTableName;
	}

	/**
	 * 
	 * @param viewTableName (never <code>null</code> or empty).
	 */
	public void setViewTableName(String viewTableName) {
		CoreArgCheck.isNotNull(viewTableName, "viewTableName is null"); //$NON-NLS-1$
		
		this.viewTableName = viewTableName;
		validate();
	}
	
	/**
	 * 
	 * @return status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public IStatus getStatus() {
		return this.status;
	}

	/**
	 * 
	 * @param status the <code>IStatus</code> representing the validity of the data in this info object
	 */
	public void setStatus(IStatus status) {
		this.status = status;
	}
	
	public abstract void validate();
}
