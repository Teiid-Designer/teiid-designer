/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.Messages;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.SingleProjectOrFolderFilter;

public class GenerateDynamicVdbPageTwo  extends AbstractWizardPage implements DqpUiConstants {

	private final String EMPTY = StringConstants.EMPTY_STRING;

    private Label dynamicVdbLocationText;
    private Text dynamicVdbFileName;
    
	Font monospaceFont;
    private Text xmlContentsBox;
    private Button exportXmlToFileSystemButton;
		
	private GenerateDynamicVdbManager vdbManager;

	/**
	 * ShowDDlPage constructor
     * @param importManager the ImportManager
	 * @since 8.1
	 */
	public GenerateDynamicVdbPageTwo(GenerateDynamicVdbManager vdbManager) {
        super(GenerateDynamicVdbPageTwo.class.getSimpleName(), "");  //$NON-NLS-1$
        this.vdbManager = vdbManager;
        setTitle(Messages.GenerateDynamicVdbPageTwo_title);
	}
	

	@Override
	public void createControl(Composite parent) {
        monospaceFont = new Font(null, "Monospace", 10, SWT.BOLD);  //$NON-NLS-1$
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(1, false));
		mainPanel.setLayoutData(new GridData()); 
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
		
        // Dynamic VDB Output GROUP
        {
            Composite summaryGroup = WidgetFactory.createGroup(mainPanel, 
            		Messages.GenerateDynamicVdbPageTwo_title, SWT.NO_SCROLL, 1);
            summaryGroup.setLayout(new GridLayout(3, false));
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(summaryGroup);
	        
	        // Workspace Location: MyProject/dynamic_vdbs (EDITABLE TEXT FIELD && ... Picker)
            Label locationLabel = new Label(summaryGroup, SWT.NONE);
            locationLabel.setText(Messages.GenerateDynamicVdbPageTwo_location);

            dynamicVdbLocationText = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(dynamicVdbLocationText);
            if( vdbManager.getOutputLocation() != null ) {
            	dynamicVdbLocationText.setText(vdbManager.getOutputLocation().getFullPath().toString());
            }

            Button browseButton = new Button(summaryGroup, SWT.PUSH);
            GridData buttonGridData = new GridData();
            browseButton.setLayoutData(buttonGridData);
            browseButton.setText(Messages.GenerateDynamicVdbPageTwo_browse); 
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleBrowse();
                }
            });
            
	        // File Name: ABC-xml.vdb  (EDITABLE TEXT FIELD && ... Picker)
            WidgetFactory.createLabel(summaryGroup, GridData.VERTICAL_ALIGN_CENTER, 
            		Messages.GenerateDynamicVdbPageTwo_dynamicVdbFileName);
            dynamicVdbFileName = WidgetFactory.createTextField(summaryGroup, SWT.NONE, GridData.FILL_HORIZONTAL) ;
            GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).applyTo(dynamicVdbFileName);
            dynamicVdbFileName.setText(vdbManager.getDynamicVdbFileName());
            dynamicVdbFileName.setToolTipText(Messages.GenerateDynamicVdbPageTwo_dynamicVdbFileNameToolTip);
            dynamicVdbFileName.addModifyListener(new ModifyListener() {
                @Override
    			public void modifyText( final ModifyEvent event ) {
                	vdbManager.setDynamicVdbFileName(dynamicVdbFileName.getText());
                    validatePage();
                }
            });
        }
	    
	    // Create DDL display group
		createXMLDisplayGroup(mainPanel);
        
		setPageComplete(false);
	}
	
    /*
     * Create the Group containing the DDL Contents (not editable)
     */
    private void createXMLDisplayGroup( Composite parent ) {
    	createButtonPanel(parent);
    	
        Group theGroup = WidgetFactory.createGroup(parent, Messages.GenerateDynamicVdbPageTwo_fileContents,  GridData.FILL_BOTH, 1);
        GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false).margins(10,  10).applyTo(theGroup);
        GridDataFactory.fillDefaults().span(2, 1).grab(true,  true).applyTo(theGroup);

        xmlContentsBox = WidgetFactory.createTextBox(theGroup);
        
        xmlContentsBox.setEditable(false);
        xmlContentsBox.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));

        xmlContentsBox.setFont(monospaceFont);
    }
    
    /*
     * Create the VDB Deploy button 
     */
    private void createButtonPanel(Composite parent) {
        Composite buttonPanel = new Composite(parent,SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(10,  10).applyTo(buttonPanel);
        GridDataFactory.fillDefaults().span(2, 1).applyTo(buttonPanel);

        exportXmlToFileSystemButton = new Button(buttonPanel, SWT.PUSH);
        exportXmlToFileSystemButton.setText(Messages.GenerateDynamicVdbPageTwo_exportXmlTitle);
        exportXmlToFileSystemButton.setToolTipText(Messages.GenerateDynamicVdbPageTwo_exportXmlTooltip);
        exportXmlToFileSystemButton.setLayoutData(new GridData());
        exportXmlToFileSystemButton.setEnabled(true);
        exportXmlToFileSystemButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleExportDDLToFileSystem();
            }

        });
 
    }


    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
//            if( vdbManager.isGenerateRequired() ) {
//            	vdbManager.generate();
//            }
            String xml = vdbManager.getOutputXml() == null ? GenerateDynamicVdbManager.SAMPLE_XML : vdbManager.getOutputXml();
            this.xmlContentsBox.setText(xml);
            
            validatePage();
            
            getControl().setVisible(visible);
            
        } else {
            super.setVisible(visible);
        }
    }

    /* 
     * Validate the page
     */
	private void validatePage() {
		this.vdbManager.validate();
		IStatus status = vdbManager.getStatus();
		if( status.getSeverity() == IStatus.ERROR ) {
            this.setErrorMessage(status.getMessage());
            this.setPageComplete(false);
            return;
        } else if(status.getSeverity() == IStatus.WARNING) { 
        	this.setErrorMessage(status.getMessage());
            this.setPageComplete(true);
        } else {
        	setErrorMessage(null);
        	setThisPageComplete(EMPTY, NONE);
        }
	}

	private void setThisPageComplete(String message, int severity) {
		WizardUtil.setPageComplete(this, message, severity);
	}


	@Override
	public void dispose() {
		super.dispose();
		if(!monospaceFont.isDisposed() ) {
			monospaceFont.dispose();
		}
	}
	
    void handleBrowse() {
    	IProject project = vdbManager.getArchiveVdbFile().getProject();
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(project,
                                                                       new SingleProjectOrFolderFilter(project),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && dynamicVdbLocationText != null) {
        	vdbManager.setOutputLocation(folder);
        	dynamicVdbLocationText.setText(folder.getFullPath().makeRelative().toString());
        }

        validatePage();
    }
    
    /**
     * Export the current string content of the XML display to a user-selected file on file system
     */
    public void handleExportDDLToFileSystem() {
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$ 
        dlg.setText(Messages.GenerateDynamicVdbPageTwo_exportXmlDialogTitle);
        dlg.setFileName(vdbManager.getDynamicVdbFileName());
        String fileStr = dlg.open();
        
        // Export to the file
        exportXmlToFile(fileStr);
    }
    
    /**
     * Export the current XML to the supplied file
     * @param fileStr
     */
    private void exportXmlToFile(String fileStr) {
        if (fileStr != null) {
            FileWriter fw = null;
            BufferedWriter out = null;
            PrintWriter pw = null;
            try {
                fw = new FileWriter(fileStr);
                out = new BufferedWriter(fw);
                pw = new PrintWriter(out);
                String ddl = xmlContentsBox.getText();
                pw.write(ddl);

            } catch (Exception e) {
                MessageDialog.openError(getShell(), 
                		Messages.GenerateDynamicVdbPageTwo_exportXmlErrorMessages, e.getMessage());
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
