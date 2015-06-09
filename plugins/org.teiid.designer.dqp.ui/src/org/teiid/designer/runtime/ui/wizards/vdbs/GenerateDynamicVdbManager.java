package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.komodo.vdb.dynamic.DynamicVdb;
import org.teiid.designer.komodo.vdb.xmi.XmiVdb;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;

public class GenerateDynamicVdbManager implements UiConstants{
	
	IFile  archiveVdbFile;
	
	XmiVdb archiveVdb;

	String dynamicVdbName;
	String dynamicVdbFileName;
	String version = "1";
	
//	DynamicVdb dynamicVdb;
	
	IContainer outputLocation;
	
	IStatus status = Status.OK_STATUS;
	
    private static final StringNameValidator nameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
            StringNameValidator.DEFAULT_MAXIMUM_LENGTH,
            new char[] {'_', '-', '.'});
	
	public GenerateDynamicVdbManager(IFile archiveVdbFile) {
		super();
		CoreArgCheck.isNotNull(archiveVdbFile);
		
		this.archiveVdbFile = archiveVdbFile;
		
		loadArchiveVdbManifest();

		dynamicVdbName = this.archiveVdb.getName();
		dynamicVdbFileName = this.archiveVdb.getName() + "-vdb.xml";
		outputLocation = this.archiveVdbFile.getParent();
	}
	
	private void loadArchiveVdbManifest() {
		archiveVdb = new XmiVdb();
		try {
			archiveVdb.load();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public IFile getArchiveVdbFile() {
		return archiveVdbFile;
	}

	public void setArchiveVdbFile(IFile archiveVdbFile) {
		this.archiveVdbFile = archiveVdbFile;
	}

	public String getDynamicVdbName() {
		return dynamicVdbName;
	}

	public void setDynamicVdbName(String dynamicVdbName) {
		this.dynamicVdbName = dynamicVdbName;
	}

	public String getDynamicVdbFileName() {
		return dynamicVdbFileName;
	}

	public void setDynamicVdbFileName(String dynamicVdbFileName) {
		this.dynamicVdbFileName = dynamicVdbFileName;
	}

//	public DynamicVdb getDynamicVdb() {
//		return dynamicVdb;
//	}
//
//	public void setDynamicVdb(DynamicVdb dynamicVdb) {
//		this.dynamicVdb = dynamicVdb;
//	}

	public XmiVdb getArchiveVdb() {
		return archiveVdb;
	}

	public void setArchiveVdb(XmiVdb archiveVdb) {
		this.archiveVdb = archiveVdb;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public IContainer getOutputLocation() {
		return outputLocation;
	}

	public void setOutputLocation(IContainer outputLocation) {
		this.outputLocation = outputLocation;
	}
	
	public void generate() {
		
	}
	
	public void validate() {
		status = Status.OK_STATUS;
		
		// Check dynamic vdb name
		String proposedVdbName = getDynamicVdbName();
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
        	status = new Status(IStatus.ERROR, PLUGIN_ID, "The version [" + getVersion() + "] must be a valid integer");
        	return;
        }
        
		// output location can't be null
        if( outputLocation == null ) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, "Target project of folder is undefined");
        	return;
        }
        
        // dynamic vdb file name
        // can't be null && must end with -vdb.xml
        if( dynamicVdbFileName == null ) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, "Dynamic VDB file name is undefined");
        	return;
        }
        
        if( ! dynamicVdbFileName.toLowerCase().endsWith("-vdb.xml")) {
        	status = new Status(IStatus.ERROR, PLUGIN_ID, "Dynamic VDB file name must end with -vdb.xml");
        	return;
        }
        	
		
	}

	public IStatus getStatus() {
		return status;
	}
}
