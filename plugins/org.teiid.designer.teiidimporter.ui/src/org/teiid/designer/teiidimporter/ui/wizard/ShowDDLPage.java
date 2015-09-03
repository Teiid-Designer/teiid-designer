/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.wizard;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ddl.importer.ui.DdlImportDifferencesPage;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;


/**
 * Page allows user to fetch/view the DDL and export it (if desired)
 *
 * @since 8.1
 */
public class ShowDDLPage extends AbstractWizardPage implements UiConstants {

	private final String EMPTY = StringConstants.EMPTY_STRING;
	private final int GROUP_HEIGHT_190 = 190;

    private Text ddlContentsBox;
    private Button exportDDLToFileSystemButton;
    private Button exportDDLToWorkspaceButton;
		
	private TeiidImportManager importManager;

	/**
	 * ShowDDlPage constructor
     * @param importManager the ImportManager
	 * @since 8.1
	 */
	public ShowDDLPage(TeiidImportManager importManager) {
        super(ShowDDLPage.class.getSimpleName(), Messages.ShowDDLPage_title); 
        this.importManager = importManager;
	}
	

	@Override
	public void createControl(Composite parent) {
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(1, false));
		mainPanel.setLayoutData(new GridData()); 
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
		
		// Create button panel
	    createButtonPanel(mainPanel);
	    
	    // Create DDL dispplay group
		createDDLDisplayGroup(mainPanel);
        
		setPageComplete(false);
	}
	
    /*
     * Create the Group containing the DDL Contents (not editable)
     */
    private void createDDLDisplayGroup( Composite parent ) {
        Group theGroup = WidgetFactory.createGroup(parent, Messages.ShowDDLPage_DDLContentsGroup, SWT.NONE, 1, 1);
        GridData groupGD = new GridData(GridData.FILL_BOTH);
        groupGD.heightHint = GROUP_HEIGHT_190;
        groupGD.widthHint = 400;
        theGroup.setLayoutData(groupGD);

        ddlContentsBox = WidgetFactory.createTextBox(theGroup);
        ddlContentsBox.setEditable(false);
        ddlContentsBox.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
    }

    /**
     * Set the DDL display contents
     * @param ddlText the DDL to display
     */
    public void setDDL(String ddlText) {
        ddlContentsBox.setText(ddlText);
    }
    
    /**
     * Get the DDL display contents
     * @return the DDL display contents
     */
    public String getDDL() {
        return ddlContentsBox.getText();
    }
    
    /*
     * Create the VDB Deploy button 
     */
    private void createButtonPanel(Composite parent) {
        Composite buttonPanel = new Composite(parent,SWT.NONE);
        buttonPanel.setLayout(new GridLayout(2, false));
        buttonPanel.setLayoutData(new GridData()); 

        exportDDLToFileSystemButton = new Button(buttonPanel, SWT.PUSH);
        exportDDLToFileSystemButton.setText(Messages.ShowDDLPage_exportDDLToFileSystemButton);
        exportDDLToFileSystemButton.setToolTipText(Messages.ShowDDLPage_exportDDLToFileSystemButtonTooltip);
        exportDDLToFileSystemButton.setLayoutData(new GridData());
        exportDDLToFileSystemButton.setEnabled(false);
        exportDDLToFileSystemButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleExportDDLToFileSystem();
            }

        });

        exportDDLToWorkspaceButton = new Button(buttonPanel, SWT.PUSH);
        exportDDLToWorkspaceButton.setText(Messages.ShowDDLPage_exportDDLToWorkspaceButton);
        exportDDLToWorkspaceButton.setToolTipText(Messages.ShowDDLPage_exportDDLToWorkspaceButtonTooltip);
        exportDDLToWorkspaceButton.setLayoutData(new GridData());
        exportDDLToWorkspaceButton.setEnabled(false);
        exportDDLToWorkspaceButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleExportDDLToWorkspace();
            }

        });
        
    }

    /**
     * Export the current string content of the DDL display to a user-selected file on file system
     */
    public void handleExportDDLToFileSystem() {
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$ 
        dlg.setText(Messages.ShowDDLPage_exportDDLDialogTitle);
        dlg.setFileName(Messages.ShowDDLPage_exportDDLDialogDefaultFileName);
        String fileStr = dlg.open();
        
        // Export to the file
        exportDDLToFile(fileStr);
    }
    
    /**
     * Export the current string content of the DDL display to a user-selected location in their workspace
     */
    private void handleExportDDLToWorkspace() {
        // Show dialog for copying the DataSource
        ExportDDLToWorkspaceDialog dialog = new ExportDDLToWorkspaceDialog(getShell());

        dialog.open();
        
        // If Dialog was OKd, create the DataSource
        if (dialog.getReturnCode() == Window.OK) {
        	IContainer targetContainer = dialog.getTargetContainer();
        	String fileName = dialog.getFileName();
        	createDDLFile(targetContainer,fileName);
        }
    }    
    
    private void createDDLFile(final IContainer targetContainer, final String ddlFileName) {
    	// Create the DDL File
    	final IRunnableWithProgress op = new IRunnableWithProgress() {

    		/**
    		 * {@inheritDoc}
    		 *
    		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
    		 */
    		@Override
    		@SuppressWarnings("unchecked")
    		public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
    			try {
    	            final IFile ddlFileToCreate = targetContainer.getFile(new Path(ddlFileName));
    	            
    	            String ddl = getDDL();
    	        	InputStream istream = new ByteArrayInputStream(ddl.getBytes());
    	            
    	            ddlFileToCreate.create(istream, false, monitor);
    	            targetContainer.refreshLocal(IResource.DEPTH_INFINITE, monitor);
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
    		WidgetUtil.showError(Messages.ShowDDLPage_exportDDLDialogExportToWorkspaceErrorMsg);
    	}
    }
    
    /**
     * Export the current DDL to the supplied file
     * @param fileStr
     */
    private void exportDDLToFile(String fileStr) {
        // If there is no file extension, add .sql
        if (fileStr != null && fileStr.indexOf('.') == -1) {
            fileStr = fileStr + "." + Messages.ShowDDLPage_exportDDLDialogDefaultFileExt;  //$NON-NLS-1$
        }
        if (fileStr != null) {
            FileWriter fw = null;
            BufferedWriter out = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(fileStr);
                out = new BufferedWriter(fw);
                pw = new PrintWriter(out);
                String ddl = getDDL();
                pw.write(ddl);

            } catch (Exception e) {
                UTIL.log(IStatus.ERROR, e, Messages.ShowDDLPage_exportDDLDialogExportErrorMsg);
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
    
    /*
     * Set the enabled state of the buttons
     */
    private void setButtonStates() {
        boolean enableExportButton = importManager.isVdbDeployed();
        this.exportDDLToFileSystemButton.setEnabled(enableExportButton);
        this.exportDDLToWorkspaceButton.setEnabled(enableExportButton);
    }

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
        	// When this page is show, ensure that differences page is set incomplete.
        	IWizard wizard = getWizard();
        	IWizardPage differencesPage = wizard.getPage("DdlImportDifferencesPage"); //$NON-NLS-1$
        	if(differencesPage instanceof DdlImportDifferencesPage) {
        		((DdlImportDifferencesPage)differencesPage).setPageComplete(false);
        	}
            if(importManager.isVdbDeployed() && importManager.shouldRedeploy()) {
                importManager.undeployDynamicVdb();
                importManager.deleteDdlTempFile();
        		importManager.setRedeploy(false);
            }
            
            if(importManager.isVdbDeployed() ) {
                String ddl = importManager.getDdl();
                if(ddl==null) ddl=EMPTY;
                this.ddlContentsBox.setText(ddl);
            } else {
                IStatus deployStatus = importManager.deployDynamicVdb();
                if(deployStatus == null) {
                    ddlContentsBox.setText(Messages.ShowDDLPage_vdbDeploymentErrorMsg);  
                } else if(!deployStatus.isOK()) {
                	StringBuffer sb = new StringBuffer(deployStatus.getMessage());
                	// Show 'check server log' message, unless deployment was cancelled.
                	if(deployStatus.getSeverity()!=IStatus.CANCEL) {
                		sb.append("\n\n"+ Messages.ShowDDLPage_vdbDeploymentCheckServerLogMsg); //$NON-NLS-1$
                	}
                    ddlContentsBox.setText(sb.toString());  
                } else {
                    String ddl = importManager.getDdl();
                    // Consider null DDL an error..
                    if(ddl==null) ddl=Messages.TeiidImportManager_getDdlErrorMsg;
                    
                    ddlContentsBox.setText(ddl);
                    ddlContentsBox.setTopIndex(0);
                }
            }
            setButtonStates();
            validatePage();
            getControl().setVisible(visible);
        } else {
            super.setVisible(visible);
        }
    }

    /* 
     * Validate the page
     */
	private boolean validatePage() {
        // VDB deployment validation
        if(!this.importManager.isVdbDeployed()) {
            String errorMsg;
            IStatus deployStatus = importManager.getVdbDeploymentStatus();
            if(deployStatus!=null) {
                errorMsg = deployStatus.getMessage();
            } else {
                errorMsg = Messages.ShowDDLPage_vdbDeploymentErrorMsg;
            }
            // Cannot proceed if VDB is not deployed
            setThisPageComplete(errorMsg, ERROR);
            return false;
        }
        String ddlStr = getDDL();
        if(ddlStr==null || ddlStr.trim().equals(Messages.TeiidImportManager_getDdlErrorMsg)) {
        	setThisPageComplete(Messages.TeiidImportManager_getDdlErrorMsg,WARNING);
        	return false;
        }

        setThisPageComplete(EMPTY, NONE);
		return true;
	}

	private void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}

}
