/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.preview;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.dialogs.GenerateDataServiceDialog;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;

public class GenerateDataServiceWorker extends PreviewDataWorker implements DqpUiConstants {
	public static final String THIS_CLASS = I18nUtil.getPropertyPrefix(GenerateDataServiceWorker.class);
	
	private static String VDB_XML_SUFFIX = "-vdb.xml"; //$NON-NLS-1$
	private static String DS_SUFFIX = "_DS"; //$NON-NLS-1$
	
    static String getString( String key ) {
        return UTIL.getString(THIS_CLASS + key);
    }
	
	private PreviewManager manager;
	
    String dataServiceName;

    String dataServiceFileName; // This is the -vdb.xml file name

    String versionString; // must be integer Major.Minor.Patch format (i.e. 10.0.2)
    
    IStatus dataServiceStatus;
    
    boolean saveToWorkspace = true;
    
    IContainer workspaceLocation;
    String fileSystemFolder;
	
	public GenerateDataServiceWorker() {
		super();
	}

	public void run(EObject targetObject) {
		saveToWorkspace = true;
		dataServiceStatus = null;
		
		dataServiceName = ModelObjectUtilities.getName(targetObject) + DS_SUFFIX;
		dataServiceFileName = dataServiceName + VDB_XML_SUFFIX;
		versionString = "1"; //$NON-NLS-1$

		manager = new PreviewManager(targetObject);

		try {
			internalRun(targetObject);
		} catch (ModelWorkspaceException e) {
			UTIL.log(e);
		}

	}
	
    /**
     * Open the launch configuration dialog, passing in the current workbench selection.
     * 
     * @throws ModelWorkspaceException
     */
    private void internalRun( final EObject eObject ) throws ModelWorkspaceException {
        final Shell shell = getShell();

    	
    	// Check data source deployments
    	IStatus vdbStatus = manager.createDynamicVdb();
    	if( vdbStatus.getSeverity() == IStatus.ERROR ) {
    		MessageDialog.openError(shell, getString("errorMessage_generatingXML"), vdbStatus.getMessage());
			return;
    	}
    	
    	GenerateDataServiceDialog dialog = new GenerateDataServiceDialog(shell, this);
        
        if( dialog.open() == Window.OK ) {
        	if( saveToWorkspace ) {
        		saveToWorkspace();
        	} else {
        		saveToFileSystem();
        	}
        }

    }
    
    public IStatus getDataServiceStatus(boolean regenerate) {
		try {
	    	if( dataServiceStatus != null ) {
	    		if( regenerate ) {
					dataServiceStatus = generateDataServiceXml();
	    		}
	    	} else {
				dataServiceStatus = generateDataServiceXml();
	    	}
		} catch (ModelWorkspaceException e) {
			UTIL.log(e);
		}
		return dataServiceStatus;
    }
    
    public IStatus generateDataServiceXml() throws ModelWorkspaceException {
    	return manager.createDynamicVdb(dataServiceName, versionString, null, dataServiceFileName);
    	
    }

	public String getDataServiceName() {
		return dataServiceName;
	}

	public void setDataServiceName(String dataServiceName) {
		this.dataServiceName = dataServiceName;
	}

	public String getDataServiceFileName() {
		return dataServiceFileName;
	}

	public void setDataServiceFileName(String dataServiceFileName) {
		this.dataServiceFileName = dataServiceFileName;
	}

	public String getVersionString() {
		return versionString;
	}

	public void setVersionString(String versionString) {
		this.versionString = versionString;
	}

	public boolean isSaveToWorkspace() {
		return saveToWorkspace;
	}

	public void setSaveToWorkspace(boolean saveToWorkspace) {
		this.saveToWorkspace = saveToWorkspace;
	}

    
    public void setWorkspaceLocation(IContainer workspaceLocation) {
		this.workspaceLocation = workspaceLocation;
	}

	public void setFileSystemFullPathAndFile(String fileSystemFullPathAndFile) {
		// separate the file name and location based on OS-specific file separator
		int sepIndex = fileSystemFullPathAndFile.lastIndexOf(File.separator);
		fileSystemFolder = fileSystemFullPathAndFile.substring(0, sepIndex);
		dataServiceFileName = fileSystemFullPathAndFile.substring(sepIndex + 1);
	}
	
	

	public IContainer getWorkspaceLocation() {
		return workspaceLocation;
	}

	public String getFileSystemFolder() {
		return fileSystemFolder;
	}

	private void saveToWorkspace() {
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
    	            final IFile fileToCreate = workspaceLocation.getFile(new Path(dataServiceFileName));
    	            
    	            String xml = getDataServiceStatus(false).getMessage();
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
    		new ProgressMonitorDialog(getShell()).run(false, true, op);
    	} catch (Throwable err) {
    		if (err instanceof InvocationTargetException) {
    			err = ((InvocationTargetException)err).getTargetException();
    		}
    		ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
    		WidgetUtil.showError(getString("errorMessage_savingToWorkspace")); //$NON-NLS-1$
    	}
    }
    
    /**
     * Export the current DDL to the supplied file
     * @param fileStr
     */
    private void saveToFileSystem() {
    	String fileSystemFullPathAndFile = fileSystemFolder + File.separator + dataServiceFileName;
        if (fileSystemFullPathAndFile != null) {
            FileWriter fw = null;
            BufferedWriter out = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(fileSystemFullPathAndFile);
                out = new BufferedWriter(fw);
                pw = new PrintWriter(out);
                String xml = getDataServiceStatus(false).getMessage();
                pw.write(xml);

            } catch (Exception e) {
                UiConstants.Util.log(IStatus.ERROR, e, getString("errorMessage_savingToFileSystem")); //$NON-NLS-1$
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
    
    public IStatus validate() {
    	if( StringUtilities.isEmpty(dataServiceName) ) {
    		return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, getString("errorMessage_nameUndefined")); //$NON-NLS-1$
    	} else if( StringUtilities.isEmpty(dataServiceFileName) ) {
    		return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, getString("errorMessage_fileNameUndefined")); //$NON-NLS-1$
    	} else if( !dataServiceFileName.endsWith("-vdb.xml")) {
    		return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, getString("errorMessage_invalidFileName")); //$NON-NLS-1$
    	} else if( saveToWorkspace && workspaceLocation == null ) {
    		return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, getString("errorMessage_workspaceLocationUndefined")); //$NON-NLS-1$
    	} else if( !saveToWorkspace && StringUtilities.isEmpty(fileSystemFolder) ) {
    		return new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, getString("errorMessage_fileLocationUndefined")); //$NON-NLS-1$
    	}
    	
    	return Status.OK_STATUS;
    }
}
