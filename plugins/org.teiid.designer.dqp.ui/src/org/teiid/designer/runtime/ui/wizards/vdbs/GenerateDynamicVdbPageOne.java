package org.teiid.designer.runtime.ui.wizards.vdbs;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.SingleProjectOrFolderFilter;

public class GenerateDynamicVdbPageOne extends AbstractWizardPage implements UiConstants {

	private final String EMPTY = StringConstants.EMPTY_STRING;

    private Text dynamicVdbName;
    private Label dynamicVdbLocationText;
    private Text dynamicVdbFileName;

	private GenerateDynamicVdbManager vdbManager;

	/**
	 * ShowDDlPage constructor
     * @param importManager the ImportManager
	 * @since 8.1
	 */
	public GenerateDynamicVdbPageOne(GenerateDynamicVdbManager vdbManager) {
        super(GenerateDynamicVdbPageOne.class.getSimpleName(), ""); //Messages.ShowDDLPage_title); 
        this.vdbManager = vdbManager;
        setTitle("Dynamic VDB Contents");
	}
	

	@Override
	public void createControl(Composite parent) {

		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(1, false));
		mainPanel.setLayoutData(new GridData()); 
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);
        
        // VDB ARCHIVE GROUP

        {
            // Selected VDB: MyProject/myFolder/ABC.vdb
            Composite summaryGroup = WidgetFactory.createGroup(mainPanel, "Selected VDB Archive", SWT.NO_SCROLL, 1);
            summaryGroup.setLayout(new GridLayout(2, false));
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(summaryGroup);
            
            Label nameLabel = new Label(summaryGroup, SWT.NONE);
            nameLabel.setText("VDB");
            
            Label vdbAndLocation = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(vdbAndLocation);
            vdbAndLocation.setText(vdbManager.getArchiveVdbFile().getFullPath().toString());
            vdbAndLocation.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
            
            // VDB Name: products_info
            Label vdbNameLabel = new Label(summaryGroup, SWT.NONE);
            vdbNameLabel.setText("VDB name");
            
            Label vdbName = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(vdbName);
            vdbName.setText(vdbManager.getArchiveVdb().getName());
            vdbName.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
            
            // Version #: 25
            WidgetFactory.createLabel(summaryGroup, "Version"); //$NON-NLS-1$
        	
            Label vdbVersion = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(vdbVersion);
            vdbVersion.setText(Integer.toString(vdbManager.getArchiveVdb().getVersion()));
            vdbVersion.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        }

        
        // Dynamic VDB Output GROUP
        {
            Composite summaryGroup = WidgetFactory.createGroup(mainPanel, "Dynamic VDB Definition", SWT.NO_SCROLL, 1);
            summaryGroup.setLayout(new GridLayout(3, false));
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(summaryGroup);
            
	        // VDB Name: products_info
            WidgetFactory.createLabel(summaryGroup, GridData.VERTICAL_ALIGN_CENTER, "Dynamic VDB Name");
            dynamicVdbName = WidgetFactory.createTextField(summaryGroup, SWT.NONE, GridData.FILL_HORIZONTAL) ;
            GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).applyTo(dynamicVdbName);
            dynamicVdbName.setText(vdbManager.getDynamicVdbName());
            dynamicVdbName.setToolTipText("Specify name for Dynamic VDB. This name can be the same as the archive VDB name above.");
            dynamicVdbName.addModifyListener(new ModifyListener() {
                @Override
    			public void modifyText( final ModifyEvent event ) {
                	vdbManager.setDynamicVdbName(dynamicVdbName.getText());
                    validatePage();
                }
            });
	        
	        // VDB Workspace Location: MyProject/dynamic_vdbs (EDITABLE TEXT FIELD && ... Picker)
            Label locationLabel = new Label(summaryGroup, SWT.NONE);
            locationLabel.setText("Location");

            dynamicVdbLocationText = new Label(summaryGroup, SWT.NONE);
            GridDataFactory.fillDefaults().grab(true,  false).applyTo(dynamicVdbLocationText);
            if( vdbManager.getOutputLocation() != null ) {
            	dynamicVdbLocationText.setText(vdbManager.getOutputLocation().getFullPath().toString());
            }

            Button browseButton = new Button(summaryGroup, SWT.PUSH);
            GridData buttonGridData = new GridData();
//            buttonGridData.widthHint = 40;
            browseButton.setLayoutData(buttonGridData);
            browseButton.setText("Change..."); 
            browseButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleBrowse();
                }
            });
            
	        // File Name: ABC-xml.vdb  (EDITABLE TEXT FIELD && ... Picker)
            WidgetFactory.createLabel(summaryGroup, GridData.VERTICAL_ALIGN_CENTER, "Dynamic VDB File Name");
            dynamicVdbFileName = WidgetFactory.createTextField(summaryGroup, SWT.NONE, GridData.FILL_HORIZONTAL) ;
            GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).applyTo(dynamicVdbFileName);
            dynamicVdbFileName.setText(vdbManager.getDynamicVdbFileName());
            dynamicVdbFileName.setToolTipText("Specify name for Dynamic VDB file name. This name must end with -vdb.xml.");
            dynamicVdbFileName.addModifyListener(new ModifyListener() {
                @Override
    			public void modifyText( final ModifyEvent event ) {
                	vdbManager.setDynamicVdbFileName(dynamicVdbFileName.getText());
                    validatePage();
                }
            });
        }
	    
//	    // Create DDL display group
//		createXMLDisplayGroup(mainPanel);
        
		setPageComplete(false);
	}

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {

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

}
