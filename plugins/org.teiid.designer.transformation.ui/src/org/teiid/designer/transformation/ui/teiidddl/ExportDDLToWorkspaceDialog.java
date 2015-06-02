/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui.teiidddl;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.product.ProductCustomizerMgr;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;

/**
 *  ExportDDLToWorkspaceDialog
 *  This Dialog allows user to export the DDL to a project or folder on their workspace
 */
public class ExportDDLToWorkspaceDialog extends TitleAreaDialog implements Listener, UiConstants {

	private Text containerText;
    private Text fileNameText;
	private IContainer container;
	private String fileName;

    /**
     * ExportDDLToWorkspaceDialog constructor
     * @param shell the shell
     */
    public ExportDDLToWorkspaceDialog(Shell shell) {
        super(shell);
    }
        
    /**
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Util.getString("ExportDDLToWorkspaceDialog_title")); 
    }
    
    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createButtonBar( Composite parent ) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(OK).setEnabled(false);

        return buttonBar;
    }
   
    /**
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite pnl = new Composite(parent, SWT.NONE);
        pnl.setLayout(new GridLayout(1, false));
        pnl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createDialogPanel(pnl);

        // set title and initial message
        setTitle(Util.getString("ExportDDLToWorkspaceDialog_title"));
        setErrorMessage(Util.getString("ExportDDLToWorkspaceDialog_errorSelectLocation"));

        return pnl;
    }
    
    /*
     * Create Panel that contains the project/folder selection and fileName controls
     * @param parent the parent Composite
     */
    private void createDialogPanel(Composite parent) {
    	
        Composite dialogComposite = new Composite(parent, SWT.NULL);
        GridData topCompositeGridData = new GridData(GridData.FILL_HORIZONTAL);
        dialogComposite.setLayoutData(topCompositeGridData);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns = 3;
        dialogComposite.setLayout(topLayout);
        GridData gd = null;

        if (!ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            Label locationLabel = new Label(dialogComposite, SWT.NULL);
            locationLabel.setText(Util.getString("ExportDDLToWorkspaceDialog_locationLabel")); 

            containerText = new Text(dialogComposite, SWT.BORDER | SWT.SINGLE);
            gd = new GridData(GridData.FILL_HORIZONTAL);
            containerText.setLayoutData(gd);
            containerText.addModifyListener(new ModifyListener() {
                @Override
				public void modifyText( ModifyEvent e ) {
                    updateStatus();
                }
            });
            containerText.setEditable(false);

            Button browseButton = new Button(dialogComposite, SWT.PUSH);
            GridData buttonGridData = new GridData();
            // buttonGridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;
            browseButton.setLayoutData(buttonGridData);
            browseButton.setText(Util.getString("ExportDDLToWorkspaceDialog_browseButton")); 
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleBrowseForTargetLocation();
                }
            });
        }

        Label fileLabel = new Label(dialogComposite, SWT.NULL);
        fileLabel.setText(Util.getString("ExportDDLToWorkspaceDialog_fileNameLabel")); 

        fileNameText = new Text(dialogComposite, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        fileNameText.setLayoutData(gd);
        fileNameText.setText(Util.getString("ExportDDLToWorkspaceDialog_defaultFileName"));
        fileNameText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( ModifyEvent e ) {
                updateStatus();
            }
        });

    	
    }
    
    @Override
    public void handleEvent( Event event ) {
        updateStatus();
    }
    
    private void handleBrowseForTargetLocation() {
        final IContainer container = WidgetUtil.showFolderSelectionDialog(ModelerCore.getWorkspace().getRoot(),
        		new ModelingResourceFilter(),
                new ModelProjectSelectionStatusValidator());

        if (container != null && containerText != null) {
        	this.container = container;
            containerText.setText(container.getFullPath().makeRelative().toString());
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#getShellStyle()
     */
    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

    /**
     * Update the Status
     */
    private void updateStatus() {
    	// Validate the location
    	IStatus status = validateLocation();
    	
    	// If location is OK, validate the Name
    	if(status.isOK()) {
    		status = validateName();
    	}
        
    	// Update dialog status
        if(status.isOK()) {
            getButton(OK).setEnabled(true);
            setErrorMessage(null);
            setMessage(status.getMessage());
        } else {
            getButton(OK).setEnabled(false);
            setErrorMessage(status.getMessage());
        }
    }
    
    /*
     * Validate the target location
     * @return the location status
     */
    private IStatus validateLocation() {
    	String locationText = this.containerText.getText();
        // Check if null or empty
        if(CoreStringUtil.isEmpty(locationText)) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("ExportDDLToWorkspaceDialog_errorSelectLocation"));
        }
        
        return new Status(IStatus.OK, PLUGIN_ID, Util.getString("ExportDDLToWorkspaceDialog_Ok"));        
    }
    
    /*
     * Validate the Filename
     * @return the name status
     */
    private IStatus validateName() {
    	this.fileName = this.fileNameText.getText();
    	
        // Check if null or empty
        if(CoreStringUtil.isEmpty(fileName)) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("ExportDDLToWorkspaceDialog_errorEnterFileName"));
        }
        
        // Check if the file already exists
        return checkFileExists(this.container,this.fileName);
    }

    private IStatus checkFileExists(final IContainer container, final String fileName) {
    	String errorMsg = null;
    	boolean exists = false;
    	IResource[] resrcs;
		try {
			resrcs = container.members();
		} catch (CoreException ex) {
	        return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("ExportDDLToWorkspaceDialog_errorCouldNotGetMembers"));
		}

    	for (int ndx = resrcs.length;  --ndx >= 0;) {
    		if (resrcs[ndx].getName().equalsIgnoreCase(fileName)) {
    			exists = true;
    			errorMsg = WidgetUtil.getFileExistsMessage(container.getFile(new Path(fileName)));
    			break;
    		}
    	}

    	// check to see if it exists just on file system and not in workspace
    	if (!exists) {
    		exists = container.getLocation().append(fileName).toFile().exists();
    		errorMsg = WidgetUtil.getFileExistsButNotInWorkspaceMessage(container.getFile(new Path(fileName)));
    	}

    	if(!exists) {
            return new Status(IStatus.OK, PLUGIN_ID, Util.getString("ExportDDLToWorkspaceDialog_Ok"));        
    	}
        return new Status(IStatus.ERROR, PLUGIN_ID, errorMsg);
    }
    
    /**
     * Get the target container selected by the user
     * @return the target container
     */
    public IContainer getTargetContainer() {
    	return this.container;
    }
    
    /**
     * Get the filename selected by the user
     * @return the fileName
     */
    public String getFileName() {
    	return this.fileName;
    }
    
}
