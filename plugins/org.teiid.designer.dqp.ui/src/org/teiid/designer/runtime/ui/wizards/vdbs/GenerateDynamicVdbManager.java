/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.ListMessageDialog;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.vdb.VdbPlugin;
import org.teiid.designer.vdb.XmiVdb;
import org.teiid.designer.vdb.dynamic.DynamicVdb;

/**
 * Manager for generation of the dynamic vdb
 */
public class GenerateDynamicVdbManager extends AbstractGenerateVdbManager {

    private IFile archiveVdbFile;
    
    private String xmlFileContents;
    
    private boolean editXmlMode;
    
    private boolean excludeSourceMetadata;
    
    boolean saveToWorkspace = true;
    
    String vdbXmlFileNameName; // This is the -vdb.xml file name
    
    String fileSystemFolder;

    /**
     * @param wizard
     * @param archiveVdbFile
     * @throws Exception
     */
    public GenerateDynamicVdbManager(AbstractWizard wizard, IFile archiveVdbFile) throws Exception {
        super(wizard);
        CoreArgCheck.isNotNull(archiveVdbFile);

        this.archiveVdbFile = archiveVdbFile;
        setArchiveVdb(new XmiVdb(archiveVdbFile));

        String vdbName = getArchiveVdb().getName();
        setOutputVdbName(vdbName);
        setOutputVdbFileName(vdbName + ITeiidVdb.DYNAMIC_VDB_SUFFIX);
        setOutputLocation(this.archiveVdbFile.getParent());
        setVersion(getArchiveVdb().getVersion());
        
        editXmlMode = false;
		saveToWorkspace = true;

    }
    
    public GenerateDynamicVdbManager(IFile archiveVdbFile) throws Exception {
    	super();
    	
        CoreArgCheck.isNotNull(archiveVdbFile);

        this.archiveVdbFile = archiveVdbFile;
        setArchiveVdb(new XmiVdb(archiveVdbFile));

        String vdbName = getArchiveVdb().getName();
        setOutputVdbName(vdbName);
        setOutputVdbFileName(vdbName + ITeiidVdb.DYNAMIC_VDB_SUFFIX);
        setOutputLocation(this.archiveVdbFile.getParent());
        setVersion(getArchiveVdb().getVersion());
        
        editXmlMode = false;
		saveToWorkspace = true;
    }
    
    public boolean isEditXmlMode() {
		return editXmlMode;
	}

	public void setEditXmlMode(boolean editXmlMode) {
		this.editXmlMode = editXmlMode;
	}

	public void setXmlFileContents(String xmlFileContents) {
		this.xmlFileContents = xmlFileContents;
	}

	private void checkDynamicVdbGenerated() throws Exception {
        if (isGenerateRequired())
            throw new Exception(Messages.GenerateDynamicVdbManager_validation_noDynamicVdbGenerated);
    }

    /**
     * Generate the dynamic xml from the xmi vdb
     */
    public void generate(final boolean ignoreWarning) {
        if (!isGenerateRequired())
            return;

        if (getArchiveVdb() == null)
            return;
        
        getArchiveVdb().setExcludeSourceMetadata(excludeSourceMetadata);

        //
        // This will convert the xmi vdb and build the dynamic vdb
        //
        GeneratorCallback<DynamicVdb> callback = new GeneratorCallback<DynamicVdb>() {

            @Override
            public XmiVdb getSourceVdb() {
                return getArchiveVdb();
            }

            @Override
            public Class<DynamicVdb> getTargetType() {
                return DynamicVdb.class;
            }

            @Override
            public void onCompletion(IStatus status) {
                if (status.isOK()) {
                	DynamicVdb vdb = getResult();
                	vdb.setVersion(getVersion());
                	vdb.setName(getOutputVdbName());
                    setDynamicVdb(vdb);
    	            final IStatus vdbStatus = getDynamicVdb().getStatus();
            		
    	            // Put on SWT THread
		            if( ! vdbStatus.isOK() ) {
		            	
		            	UiUtil.runInSwtThread(new Runnable() {
		        			@Override
		        			public void run() {
		        				List<String> messages = new ArrayList<String>();
		        				for( IStatus status : vdbStatus.getChildren() ) {
		        					messages.add(status.getMessage());
		        				}
		        				
		        				String title = DqpUiConstants.UTIL.getString("GenerateDynamicVdbManager.generateDynamicVdbStatus");
		        				
				            	if( vdbStatus.getSeverity() == IStatus.WARNING && !ignoreWarning ) {
				            		ListMessageDialog.openWarning(
				            				UiUtil.getWorkbenchShellOnlyIfUiThread(), 
				            				title,
				            				null,
				            				DqpUiConstants.UTIL.getString("GenerateDynamicVdbManager.warningsOnExport"),
				            				messages,
				            				null);
				            	} else if( vdbStatus.getSeverity() == IStatus.ERROR ) {
				            		ListMessageDialog.openError(Display.getCurrent().getActiveShell(), title, vdbStatus.getMessage());
				            	}
		        			}
		        		}, false);

		            }
                }
            }
        };

        generateVdbJob(callback);
    }

    @Override
    public String getDynamicVdbXml() throws Exception {
        checkDynamicVdbGenerated();
        
        if( !isEditXmlMode() ) {
        	xmlFileContents =  super.getDynamicVdbXml();
        }

        return xmlFileContents;
    }

    /**
     * @param directory absolute path of a directory to export the dynamic vdb
     * @throws Exception
     */
    public void export(String directory) throws Exception {
        if (directory == null)
            return;

        checkDynamicVdbGenerated();

        File export = new File(directory, getOutputVdbFileName());

        if (export.exists()) {
        	if(overwriteExistingFiles() ) {
                if (!export.delete())
                    throw new Exception(VdbPlugin.UTIL.getString("unableToDelete", export)); //$NON-NLS-1$
        	}
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbManager_exportLocationAlreadyExists,
            		getOutputVdbFileName(),
                                         directory));
        }
        
        if (!export.createNewFile())
            throw new Exception(NLS.bind(Messages.GenerateDynamicVdbManager_exportLocationFailedToCreateFile,
            		getOutputVdbFileName(),
                                         directory));

        FileWriter writer = new FileWriter(export);
        getDynamicVdb().write(writer);
    }

    /**
     * @throws Exception
     */
    @Override
    public void write() throws Exception {
        checkDynamicVdbGenerated();
        
        // if
    	
    	if( saveToWorkspace ) {
    		saveToWorkspace(xmlFileContents, getOutputLocation(), getOutputVdbFileName());
    	} else {
    		saveToFileSystem(xmlFileContents, getFileSystemFolder(), getOutputVdbFileName());
    	}
    }

    /**
     * @return whether dynamic xml generation is required
     */
    public boolean isGenerateRequired() {
        return getDynamicVdb() == null;
    }

    /**
     * Validate the manager's settings
     */
    @Override
    public void validate() {
        setStatus(Status.OK_STATUS);

        // Check ouptut vdb name
        String proposedVdbName = getOutputVdbFileName();
        String validationMessage = nameValidator.checkValidName(proposedVdbName);
        if (validationMessage != null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
            return;
        }

        validationMessage = ModelUtilities.vdbNameReservedValidation(proposedVdbName);
        if (validationMessage != null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, validationMessage));
            return;
        }

        // Check Version # is an integer
        String version = getVersion();
        if( version == null ) {
        	// TODO: Status message (cannot be null)
        }

        // output location can't be null
        if (getOutputLocation() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateVdbWizard_validation_targetLocationUndefined));
            return;
        }

        if (!getOutputLocation().exists()) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateVdbWizard_validation_targetLocationNotExist));
            return;
        }

        if (getOutputVdbFileName() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateVdbWizard_validation_vdbFileNameUndefined));
            return;
        }

	    if (getStatus().getSeverity() > IStatus.WARNING)
	        return;

        if (!getOutputVdbFileName().toLowerCase().endsWith(ITeiidVdb.DYNAMIC_VDB_SUFFIX)) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbManager_validation_vdbMissingXmlExtension));
            return;
        }

        if (!isGenerateRequired() && getDynamicVdb() == null) {
            setStatus(new Status(IStatus.ERROR, PLUGIN_ID, Messages.GenerateDynamicVdbManager_validation_noDynamicVdbGenerated));
            return;
        }
        
        // check output location
        if( saveToWorkspace ) {
        	// check that workspace location is set
        	if( getOutputLocation() == null ) {
        		setStatus(new Status(IStatus.ERROR, PLUGIN_ID, "Workspace project or folder is not defined"));
        		return;
        	}

            IFile destination = getDestination();
            if (destination.exists()) {
            	int severity = overwriteExistingFiles() ? IStatus.WARNING : IStatus.ERROR;
                setStatus(new Status(severity, PLUGIN_ID, Messages.GenerateVdbWizard_validation_targetFileAlreadyExists));
                return;
            }
        } else {
        	if( this.fileSystemFolder == null ) {
        		setStatus(new Status(IStatus.ERROR, PLUGIN_ID, "File system folder is not defined"));
        		return;
        	}
        }
        
        // Check if existing file system vdb.xml exists
        if( !overwriteExistingFiles() && !saveToWorkspace) {
        	String fileSystemFullPathAndFile = getFileSystemFolder() + File.separator + getOutputVdbFileName();
        	File file = new File(fileSystemFullPathAndFile);
        	if( file.exists()) {
        		setStatus(new Status(IStatus.ERROR, PLUGIN_ID, "VDB File: " + getOutputVdbFileName() + " already exists"));
        	}
        }
    }
    
    
    
    @Override
	protected void setOutputVdbFileName(String outputName) {
		if( StringUtilities.areDifferent(outputName,  getOutputVdbFileName())) {
			setDynamicVdb(null);
		}
		super.setOutputVdbFileName(outputName);
	}
    
    /**
     * @param outputName the outputVdbName to set
     */
    protected void setOutputVdbName(String outputVdbName) {
		if( StringUtilities.areDifferent(outputVdbName,  getOutputVdbName())) {
			setDynamicVdb(null);
		}
        super.setOutputVdbName(outputVdbName);
    }

	@Override
	public void setVersion(String version) {
		if( StringUtilities.areDifferent(version,  getVersion())) {
			setDynamicVdb(null);
		}
		super.setVersion(version);
	}

	/**
     * 
     * @return exclude source DDL value
     */

    public boolean isExcludeSourceMetadata() {
		return excludeSourceMetadata;
	}
 
     /**
     * 
     * @param setExcludeSourceDdl
     */
	public void setExcludeSourceMetadata(boolean excludeSourceMetadata) {
		if( excludeSourceMetadata != this.excludeSourceMetadata ) {
			if( !isGenerateRequired() ) setDynamicVdb(null);
			this.excludeSourceMetadata = excludeSourceMetadata;
		}
	}
	

	public boolean isSaveToWorkspace() {
		return saveToWorkspace;
	}

	public void setSaveToWorkspace(boolean saveToWorkspace) {
		this.saveToWorkspace = saveToWorkspace;
	}

	public void setFileSystemFullPathAndFile(String fileSystemFullPathAndFile) {
		// separate the file name and location based on OS-specific file separator
		int sepIndex = fileSystemFullPathAndFile.lastIndexOf(File.separator);
		fileSystemFolder = fileSystemFullPathAndFile.substring(0, sepIndex);
		setOutputVdbFileName(fileSystemFullPathAndFile.substring(sepIndex + 1));
	}

	public String getFileSystemFolder() {
		return fileSystemFolder;
	}
	
	private void saveToWorkspace(final String xml, final IContainer workspaceLocation, final String fileName) {
    	// Create the DDL File
    	final IRunnableWithProgress op = new IRunnableWithProgress() {

    		/**
    		 * {@inheritDoc}
    		 *
    		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
    		 */
    		@Override
    		public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
    			try {
    				final IFile fileExists = workspaceLocation.getFile(new Path(fileName));
    				if( fileExists.exists() ) {
    					boolean doWrite = false;
    					if( !overwriteExistingFiles()) {
    						// Shouldn't get here, but adding a check just in case.
    						doWrite = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
    								"File Exists", "File exists, do you wish to overwrite?");  //$NON-NLS-1$  //$NON-NLS-2$
    					}
    					if( doWrite ) {
    						fileExists.delete(true, monitor);
    					} else {
    						// Do nothing
    						return;
    					}
    				}
    				
    	            final IFile fileToCreate = workspaceLocation.getFile(new Path(fileName));
    	            
    	        	InputStream istream = new ByteArrayInputStream(xml.getBytes());
    	            
    	        	fileToCreate.create(istream, false, monitor);
    	        	workspaceLocation.refreshLocal(IResource.DEPTH_INFINITE, monitor);
    			} catch (final Exception err) {
    				throw new InvocationTargetException(err);
    			} finally {
    				monitor.done();
    			}
    		}
    	};

    	try {
    		new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(false, true, op);
    	} catch (Throwable err) {
    		if (err instanceof InvocationTargetException) {
    			err = ((InvocationTargetException)err).getTargetException();
    		}
    		ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
    		WidgetUtil.showError("Error saving dynamic vdb to workspace"); //$NON-NLS-1$
    	}
    }
    
    /**
     * Export the current DDL to the supplied file
     * @param fileStr
     */
    private void saveToFileSystem(final String xml, final String fileSystemFolderUrl, final String fileName) {
    	String fileSystemFullPathAndFile = fileSystemFolderUrl + File.separator + fileName;
        if (fileSystemFullPathAndFile != null) {

        	
            FileWriter fw = null;
            BufferedWriter out = null;
            PrintWriter pw = null;
            try {
            	
                File export = new File(fileSystemFolderUrl, fileName);

                if (export.exists()) {
                	if(overwriteExistingFiles() ) {
                        if (!export.delete()) {
                            throw new Exception(VdbPlugin.UTIL.getString("unableToDelete", export)); //$NON-NLS-1$
                        }
                	} else {
	                    throw new Exception(NLS.bind(Messages.GenerateDynamicVdbManager_exportLocationAlreadyExists,
	                    		getOutputVdbFileName(),
	                    		fileSystemFolderUrl));
                	}
                }
            	
            	
                fw = new FileWriter(fileSystemFullPathAndFile);
                out = new BufferedWriter(fw);
                pw = new PrintWriter(out);
                pw.write(xml);

            } catch (Exception e) {
                UiConstants.Util.log(IStatus.ERROR, e, "Error saving dynamic vdb to file system"); //$NON-NLS-1$
            } finally {
                pw.close();
                try {
                    out.close();
                } catch (java.io.IOException e) {
                }
                try {
                    fw.close();
                } catch (java.io.IOException e) {
                }
            }
        }
    }
}
