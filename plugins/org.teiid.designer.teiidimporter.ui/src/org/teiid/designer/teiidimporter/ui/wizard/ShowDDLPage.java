/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui.wizard;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.eclipse.core.runtime.IStatus;
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
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.ddl.importer.ui.DdlImportDifferencesPage;
import org.teiid.designer.teiidimporter.ui.Messages;
import org.teiid.designer.teiidimporter.ui.UiConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;


/**
 * Page allows user to fetch/view the DDL and export it (if desired)
 *
 * @since 8.1
 */
public class ShowDDLPage extends AbstractWizardPage implements UiConstants {

	private final String EMPTY = StringUtilities.EMPTY_STRING;
	private final int GROUP_HEIGHT_190 = 190;

    private Text ddlContentsBox;
    private Button exportDDLButton;
		
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

        exportDDLButton = new Button(buttonPanel, SWT.PUSH);
        exportDDLButton.setText(Messages.ShowDDLPage_exportDDLButton);
        exportDDLButton.setToolTipText(Messages.ShowDDLPage_exportDDLButtonTooltip);
        exportDDLButton.setLayoutData(new GridData());
        exportDDLButton.setEnabled(false);
        exportDDLButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleExportDDL();
            }

        });
        
    }
    
    /**
     * Export the current string content of the DDL display to a user-selected file
     */
    public void handleExportDDL() {
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$ 
        dlg.setText(Messages.ShowDDLPage_exportDDLDialogTitle);
        dlg.setFileName(Messages.ShowDDLPage_exportDDLDialogDefaultFileName);
        String fileStr = dlg.open();
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
        this.exportDDLButton.setEnabled(enableExportButton);
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
        	
            if(importManager.isVdbDeployed()) {
                String ddl = importManager.getDdl();
                if(ddl==null) ddl=EMPTY;
                this.ddlContentsBox.setText(ddl);
            } else {
                IStatus deployStatus = importManager.deployDynamicVdb();
                if(!deployStatus.isOK()) {
                    ddlContentsBox.setText(Messages.ShowDDLPage_vdbDeploymentErrorMsg);  
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
            setThisPageComplete(errorMsg, ERROR);
            return false;
        }
        String ddlStr = getDDL();
        if(ddlStr==null || ddlStr.trim().equals(Messages.TeiidImportManager_getDdlErrorMsg)) {
        	setThisPageComplete(Messages.TeiidImportManager_getDdlErrorMsg,ERROR);
        	return false;
        }

        setThisPageComplete(EMPTY, NONE);
		return true;
	}

	private void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}

}
