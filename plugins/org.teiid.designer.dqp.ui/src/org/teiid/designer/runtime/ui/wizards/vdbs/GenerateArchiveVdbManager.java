/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 * Manager for generation of the archive vdb
 */
public class GenerateArchiveVdbManager extends AbstractGenerateVdbManager implements UiConstants, StringConstants {
	
	private IFile  dynamicVdbFile;

	private String delegateArchiveVdbName;
	
    /**
	 * @param dynamicVdbFile
	 * @throws Exception
	 */
	public GenerateArchiveVdbManager(IFile dynamicVdbFile) throws Exception {
		super();
		this.dynamicVdbFile = dynamicVdbFile;
		setDynamicVdb(new DynamicVdb(this.dynamicVdbFile));

		String dynName = getDynamicVdb().getName();
		delegateArchiveVdbName = dynName + UNDERSCORE + getVersion();
		setOutputName(dynName + ITeiidVdb.VDB_DOT_EXTENSION);
		setOutputLocation(this.dynamicVdbFile.getParent());
	}

	 /**
     * @return dynamic vdb name
     */
	public IFile getDynamicVdbFile() {
		return dynamicVdbFile;
	}

	/**
	 * @param dynamicVdbFile
	 */
	public void setDynamicVdbFile(IFile dynamicVdbFile) {
		this.dynamicVdbFile = dynamicVdbFile;
	}

	/**
	 * @return proposed archive vdb name
	 */
	public String getDelegateArchiveVdbName() {
		return delegateArchiveVdbName;
	}

	/**
	 * @param delegateArchiveVdbName
	 */
	public void setDelegateArchiveVdbName(String delegateArchiveVdbName) {
		this.delegateArchiveVdbName = delegateArchiveVdbName;
	}

	/**
     * @return whether archive vdb generation is required
     */
    public boolean isGenerateRequired() {
        return getArchiveVdb() == null;
    }

	/**
     * Generate the archive vdb from the dynamic vdb
     * @throws Exception if error occurs
     */
	public void generate() throws Exception {
	    if (!isGenerateRequired())
            return;

        if (getDynamicVdb() == null)
            return;

        //
        // This will convert the dynamic vdb and build the xmi vdb
        //
        XmiVdb xmiVdb = getDynamicVdb().convert(XmiVdb.class, getDestination());

        //
        // Save the vdb
        //
        xmiVdb.save();

        //
        // Refresh to display the vdb
        //
        getOutputLocation().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        setArchiveVdb(xmiVdb);
	}
	
	/**
     * Validate the manager's settings
     */
	public void validate() {
		setStatus(Status.OK_STATUS);
		
		// Check dynamic vdb name
		String proposedVdbName = getOutputName();
		String validationMessage = nameValidator.checkValidName(proposedVdbName);
		if( validationMessage != null ) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
            return;
        } 
		
        validationMessage = ModelUtilities.vdbNameReservedValidation(proposedVdbName);
        if( validationMessage != null ) {
        	setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
        	return;
        }
        
        // Check Version # is an integer
        try {
        	Integer.parseInt(getVersion());
        } catch (NumberFormatException nfe) {
        	setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
        			NLS.bind(Messages.GenerateArchiveVdbWizard_validation_versionNotInteger, getVersion())));
        	return;
        }
        
		// output location can't be null
        if( getOutputLocation() == null ) {
        	setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.
        			GenerateArchiveVdbWizard_validation_targetLocationUndefined));
        	return;
        }
        
        // vdb archive file name
        // can't be null && must end with -vdb.xml
        if( getOutputName() == null ) {
        	setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
        			Messages.GenerateArchiveVdbWizard_validation_vdbFileNameUndefined));
        	return;
        }
        
        if( getOutputName().contains(DOT) && ! getOutputName().toLowerCase().endsWith(ITeiidVdb.VDB_DOT_EXTENSION)) {
        	setStatus(new Status(IStatus.ERROR, PLUGIN_ID, 
        			Messages.GenerateArchiveVdbWizard_validation_vdbMissingVdbExtension));
        	return;
        }
	}
}
