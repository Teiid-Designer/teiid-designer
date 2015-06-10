/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.FileUtil;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.komodo.vdb.dynamic.DynamicVdb;
import org.teiid.designer.komodo.vdb.xmi.XmiVdb;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class GenerateArchiveVdbManager implements UiConstants{
	
	IFile  dynamicVdbFile;
	
	DynamicVdb dynamicVdb;
	
	String vdbArchiveFileName;
	String delegateArchiveVdbName;
	String version = "1";
	
//	XmiVdb archiveVdb;
	
	IContainer outputLocation;
	
	IStatus status = Status.OK_STATUS;
	
    private static final StringNameValidator nameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
            StringNameValidator.DEFAULT_MAXIMUM_LENGTH,
            new char[] {'_', '-', '.'});
	
	public GenerateArchiveVdbManager(IFile dynamicVdbFile) {
		super();
		this.dynamicVdbFile = dynamicVdbFile;
		loadDynamicVdb();
		
		delegateArchiveVdbName = this.dynamicVdb.getName() + "_1";
		vdbArchiveFileName = this.dynamicVdb.getName() + ".vdb";
		outputLocation = this.dynamicVdbFile.getParent();
	}
	
	private void loadDynamicVdb() {
		dynamicVdb = new DynamicVdb(this.dynamicVdbFile);
		try {
			dynamicVdb.load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IFile getDynamicVdbFile() {
		return dynamicVdbFile;
	}

	public void setDynamicVdbFile(IFile dynamicVdbFile) {
		this.dynamicVdbFile = dynamicVdbFile;
	}

	public String getDelegateArchiveVdbName() {
		return delegateArchiveVdbName;
	}

	public void setDelegateArchiveVdbName(String delegateArchiveVdbName) {
		this.delegateArchiveVdbName = delegateArchiveVdbName;
	}

	public String getVdbArchiveFileName() {
		return vdbArchiveFileName;
	}

	public void setVdbArchiveFileName(String vdbArchiveFileName) {
		this.vdbArchiveFileName = vdbArchiveFileName;
	}

	public DynamicVdb getDynamicVdb() {
		return dynamicVdb;
	}

	public void setDynamicVdb(DynamicVdb dynamicVdb) {
		this.dynamicVdb = dynamicVdb;
	}

//	public XmiVdb getArchiveVdb() {
//		return archiveVdb;
//	}
//
//	public void setArchiveVdb(XmiVdb archiveVdb) {
//		this.archiveVdb = archiveVdb;
//	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getXmlFileAsString() {
        if (dynamicVdbFile != null) {

            try {

            	return FileUtil.readSafe(dynamicVdbFile.getLocation().toFile());
            } catch (final IOException error) {
                UiConstants.Util.log(error);
            }
        }
        return StringConstants.EMPTY_STRING;
	}
	
	public IContainer getOutputLocation() {
		return outputLocation;
	}

	public void setOutputLocation(IContainer outputLocation) {
		this.outputLocation = outputLocation;
	}
	
	public void generate() {
		// TODO: perform the archive generation
		MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "GenerateArchiveVdbWizard", "finish() not yet implemented");
	}
	
	public void validate() {
		status = Status.OK_STATUS;
		
		// Check dynamic vdb name
		String proposedVdbName = getVdbArchiveFileName();
		String validationMessage = nameValidator.checkValidName(proposedVdbName);
		if( validationMessage != null ) {
            status = new Status(IStatus.ERROR, PLUGIN_ID, validationMessage);
            return;
        } 
		
        validationMessage = ModelUtilities.vdbNameReservedValidation(proposedVdbName);
        if( validationMessage != null ) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, validationMessage);
        	return;
        }
        
        // Check Version # is an integer
        try {
        	Integer.parseInt(getVersion());
        } catch (NumberFormatException nfe) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, 
        			NLS.bind(Messages.GenerateArchiveVdbWizard_validation_versionNotInteger, getVersion()));
        	return;
        }
        
		// output location can't be null
        if( outputLocation == null ) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.
        			GenerateArchiveVdbWizard_validation_targetLocationUndefined);
        	return;
        }
        
        // vdb archive file name
        // can't be null && must end with -vdb.xml
        if( vdbArchiveFileName == null ) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, 
        			Messages.GenerateArchiveVdbWizard_validation_vdbFileNameUndefined);
        	return;
        }
        
        if( vdbArchiveFileName.contains(".") && ! vdbArchiveFileName.toLowerCase().endsWith(".vdb")) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, 
        			Messages.GenerateArchiveVdbWizard_validation_vdbMissingVdbExtension);
        	return;
        }
	}
	

	public IStatus getStatus() {
		return status;
	}
}
