/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties.extension;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;

/**
 * ChooseVdbFileOptionsDialog
 * Get user's Vdb file selection choice.
 * 1) from appropriate workspace folder
 * 2) from file system
 *    - if file system, option is given whether to copy it into the workspace
 * Some options are disabled, depending on whether udf jar or userFile is being selected, the
 * current state of the workspace, etc.
 * 
 */
public class ChooseVdbFileOptionsDialog extends MessageDialog {

    private Button selectFromWorkspaceRadio;
    private Button selectFromFileSystemRadio;
    private Button copyToWorkspaceCheckbox;
    private boolean selectFromWorkspace = false;
    private boolean selectFromFileSystem = false;
    private boolean copyToWorkspace = false;
    private VdbFolders vdbFolder = null;
    private boolean disableWorkspaceOption = false;
    
    /**
     * @param parentShell the parent shell
     * @param dialogTitle the dialog title
     * @param dialogMessage the dialog message
     * @param vdbFolder type of folder being chosen from
     * @param disableWorkspaceOption 'true' if the workspace option is to be disabled
     */
    public ChooseVdbFileOptionsDialog( Shell parentShell,
                               String dialogTitle,
                               String dialogMessage,
                               VdbFolders vdbFolder, boolean disableWorkspaceOption) {
        super(parentShell, dialogTitle, null, dialogMessage, QUESTION, new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);
        this.vdbFolder = vdbFolder;
        this.disableWorkspaceOption=disableWorkspaceOption;
    }

    /**
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createMessageArea( Composite parent ) {
        super.createMessageArea(parent);
        new Composite(parent, SWT.NONE); 
        return getControlsComposite(parent);
    }

    /**
     * Create the dialog controls area
     * @param parent the parent composite
     * @return the control area composite
     */
    public Composite getControlsComposite( Composite parent ) {
        Font font = parent.getFont();

        Composite radioComposite = new Composite(parent, SWT.NONE);
        GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
                                     | GridData.VERTICAL_ALIGN_BEGINNING);
        radioComposite.setLayoutData(data);

        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 6;
        layout.numColumns = 1;
        radioComposite.setLayout(layout);
        
        String selectFromWorkspaceRadioText = StringConstants.EMPTY_STRING;
        String selectFromFileSystemRadioText = StringConstants.EMPTY_STRING;
        
        // Change radio text based on Udf or other
        if(VdbFolders.UDF.equals(vdbFolder)) {
            selectFromWorkspaceRadioText = Messages.chooseUdfFromWorkspaceRadioText;
            selectFromFileSystemRadioText = Messages.chooseUdfFromFileSystemRadioText;
        } else {
            selectFromWorkspaceRadioText = Messages.chooseFileFromWorkspaceRadioText;
            selectFromFileSystemRadioText = Messages.chooseFileFromFileSystemRadioText;
        }
        
        //-------------------------------
        // Select from Workspace Button
        //-------------------------------
        selectFromWorkspaceRadio = new Button(radioComposite, SWT.RADIO | SWT.LEFT);
        selectFromWorkspaceRadio.setFont(font);
        selectFromWorkspaceRadio.setText(selectFromWorkspaceRadioText);
        
        //-------------------------------
        // Select from File System Button
        //-------------------------------
        selectFromFileSystemRadio = new Button(radioComposite, SWT.RADIO | SWT.LEFT);
        selectFromFileSystemRadio.setFont(font);
        selectFromFileSystemRadio.setText(selectFromFileSystemRadioText);
        
        //-------------------------------
        // Copy to Workspace checkbox
        //-------------------------------
        Composite checkboxComposite = new Composite(radioComposite, SWT.LEFT);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        checkboxComposite.setLayoutData(gd);

        GridLayout layButtons = new GridLayout();
        layButtons.numColumns = 2;
        checkboxComposite.setLayout(layButtons);
        
        Label spacerLabel = new Label(checkboxComposite,SWT.NONE);
        spacerLabel.setText("     "); //$NON-NLS-1$
        
        copyToWorkspaceCheckbox = new Button(checkboxComposite, SWT.CHECK | SWT.LEFT);
        copyToWorkspaceCheckbox.setFont(font);
        copyToWorkspaceCheckbox.setText(Messages.copyToWorkspaceCheckboxText);

        //----------------------------------------------
        // Initialize Dialog selections and enablements
        //----------------------------------------------
        setInitialDialogChoices();
        
        //-----------------------------------------------
        // Add radio and checkbox listeners
        //-----------------------------------------------
        selectFromWorkspaceRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleRadioSelectionChanged();
            }
        });
        selectFromFileSystemRadio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleRadioSelectionChanged();
            }
        });
        copyToWorkspaceCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent event ) {
                handleCheckboxSelectionChanged();
            }
        });
        
        return radioComposite;
    }

    /*
     * Initialize the Dialog selections and enablements
     */
    private void setInitialDialogChoices() {
        // Udf Jar selection Mode
        if (VdbFolders.UDF.equals(vdbFolder)) {
            // if workspace option is disabled, 
            //   -initial radio choice is 'file system'
            //   -copy to workspace is checked and disabled
            if (this.disableWorkspaceOption) {
                selectFromWorkspaceRadio.setSelection(false);
                this.selectFromWorkspace = false;
                selectFromFileSystemRadio.setSelection(true);
                this.selectFromFileSystem = true;
                copyToWorkspaceCheckbox.setSelection(true);
                this.copyToWorkspace = true;
                selectFromWorkspaceRadio.setEnabled(false);
                copyToWorkspaceCheckbox.setEnabled(false);
                // if workspace option is available
                //   -initial radio choice is 'workspace'
                //   -copy to workspace is unchecked and disabled
            } else {
                selectFromWorkspaceRadio.setSelection(true);
                this.selectFromWorkspace = true;
                selectFromFileSystemRadio.setSelection(false);
                this.selectFromFileSystem = false;
                copyToWorkspaceCheckbox.setSelection(false);
                this.copyToWorkspace = false;
                selectFromWorkspaceRadio.setEnabled(true);
                copyToWorkspaceCheckbox.setEnabled(false);
            }
        } else {
            // File selection Mode
            // -initial radio choice is 'file system'
            // -copy to workspace is unchecked
            selectFromWorkspaceRadio.setSelection(false);
            this.selectFromWorkspace = false;
            selectFromFileSystemRadio.setSelection(true);
            this.selectFromFileSystem = true;
            copyToWorkspaceCheckbox.setSelection(false);
            this.copyToWorkspace = false;
            selectFromWorkspaceRadio.setEnabled(true);
            copyToWorkspaceCheckbox.setEnabled(true);
            // disable workspace radio if necessary
            if (this.disableWorkspaceOption) {
                this.selectFromWorkspaceRadio.setEnabled(false);
            }
        }
    }
    
    /*
     * Handler for Radio Selection events
     */
    private void handleRadioSelectionChanged() {
        // If FileSystem selection, enable the copy to workspace checkbox
        if(selectFromFileSystemRadio.getSelection()) {
            this.selectFromFileSystem=true;
            this.selectFromWorkspace=false;
            // Udf from fileSystem must be copied to workspace
            if (VdbFolders.UDF.equals(vdbFolder)) {
                this.copyToWorkspaceCheckbox.setSelection(true);
                this.copyToWorkspace = true;
                this.copyToWorkspaceCheckbox.setEnabled(false);
            } else {
                this.copyToWorkspaceCheckbox.setEnabled(true);
            }
        }
        
        // If Workspace selection, disable the copy to workspace checkbox
        if(selectFromWorkspaceRadio.getSelection()) {
            this.selectFromFileSystem=false;
            this.selectFromWorkspace=true;
            this.copyToWorkspaceCheckbox.setSelection(false);
            this.copyToWorkspace=false;
            this.copyToWorkspaceCheckbox.setEnabled(false);
        }
        
    }
    
    /*
     * Handler for Checkbox selection
     */
    private void handleCheckboxSelectionChanged() {
        // If FileSystem selection, enable the copy to workspace checkbox
        if(copyToWorkspaceCheckbox.getSelection()) {
            this.copyToWorkspace=true;
        } else {
            this.copyToWorkspace=false;
        }
    }
    
    /**
     * @return 'true' if select from workspace is selected
     */
    public boolean selectFromWorkspaceSelected() {
        return this.selectFromWorkspace;
    }
    
    /**
     * @return 'true' if select from file system is selected
     */
    public boolean selectFromFileSystemSelected() {
        return this.selectFromFileSystem;
    }
    
    /**
     * @return 'true' if copy to workspace is selected
     */
    public boolean copyToWorkspaceSelected() {
        return this.copyToWorkspace;
    }
    
}
