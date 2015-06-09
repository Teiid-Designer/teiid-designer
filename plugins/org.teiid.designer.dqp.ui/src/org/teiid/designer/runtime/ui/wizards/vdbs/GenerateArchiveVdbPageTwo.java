package org.teiid.designer.runtime.ui.wizards.vdbs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.komodo.vdb.Model;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.SingleProjectOrFolderFilter;

public class GenerateArchiveVdbPageTwo extends AbstractWizardPage implements UiConstants {

	private final String EMPTY = StringConstants.EMPTY_STRING;

    private Text vdbArchiveNameFld;
    private Text vdbArchiveFileNameFld;
    private Label vdbArchiveLocationText;
    ListViewer sourceModelsViewer;
    ListViewer viewModelsViewer;
		
	private GenerateArchiveVdbManager vdbManager;

	/**
	 * ShowDDlPage constructor
     * @param importManager the ImportManager
	 * @since 8.1
	 */
	public GenerateArchiveVdbPageTwo(GenerateArchiveVdbManager vdbManager) {
        super(GenerateArchiveVdbPageTwo.class.getSimpleName(), ""); //Messages.ShowDDLPage_title); 
        this.vdbManager = vdbManager;
        setTitle("Specify VDB archive output");
	}
	

	@Override
	public void createControl(Composite parent) {
		// Create page
		final Composite mainPanel = new Composite(parent, SWT.NONE);

		mainPanel.setLayout(new GridLayout(2, false));
		mainPanel.setLayoutData(new GridData()); 
		mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		setControl(mainPanel);

		createHeaderPanel(mainPanel);
	    
	    // Create Source Models List group
	    createSourceModelsGroup(mainPanel);
	    
	    createViewModelsGroup(mainPanel);
        
		setPageComplete(false);
	}
	
	private void createHeaderPanel(Composite parent) {
		
        Composite vdbInfoGroup = WidgetFactory.createGroup(parent, "VDB Details", GridData.FILL_HORIZONTAL);
        vdbInfoGroup.setLayout(new GridLayout(3, false));
        GridDataFactory.fillDefaults().grab(true,  false).span(2, 1).applyTo(vdbInfoGroup);

        WidgetFactory.createLabel(vdbInfoGroup, GridData.VERTICAL_ALIGN_CENTER, "Original VDB Name");
        Label vdbNameFld = new Label(vdbInfoGroup, SWT.NONE);
        GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).applyTo(vdbNameFld);
        vdbNameFld.setText(vdbManager.getDynamicVdb().getName());
        vdbNameFld.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);

        Label locationLabel = new Label(vdbInfoGroup, SWT.NONE);
        locationLabel.setText("Location");

        vdbArchiveLocationText = new Label(vdbInfoGroup, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true,  false).applyTo(vdbArchiveLocationText);
        if( vdbManager.getOutputLocation() != null ) {
        	vdbArchiveLocationText.setText(vdbManager.getOutputLocation().getFullPath().toString());
        }
        
        Button browseButton = new Button(vdbInfoGroup, SWT.PUSH);
        GridData buttonGridData = new GridData();
        browseButton.setLayoutData(buttonGridData);
        browseButton.setText("Change..."); 
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleBrowse();
            }
        });
        
        Label vdbVersionLabel = WidgetFactory.createLabel(vdbInfoGroup, "Version"); //$NON-NLS-1$
    	GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(vdbVersionLabel);
    	
    	final Text vdbVersionText = WidgetFactory.createTextField(vdbInfoGroup);
    	GridDataFactory.fillDefaults().span(2, 1).align(SWT.LEFT, SWT.CENTER).applyTo(vdbVersionText);
    	((GridData)vdbVersionText.getLayoutData()).widthHint = 30;
    	
    	vdbVersionText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				try {
                    int versionValue = Integer.parseInt(vdbVersionText.getText());
                    if (versionValue > -1) {
                        vdbManager.setVersion(vdbVersionText.getText());
					}
				} catch (NumberFormatException ex) {
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(),
                            "Invalid Version",
                            "Invalid Version");
					vdbVersionText.setText(vdbManager.getVersion());
				}
				
			}
		});
    	
    	vdbVersionText.setText(Integer.toString(vdbManager.getDynamicVdb().getVersion()));
    	

        WidgetFactory.createLabel(vdbInfoGroup, GridData.VERTICAL_ALIGN_CENTER, "Archive VDB Name");
        vdbArchiveNameFld = WidgetFactory.createTextField(vdbInfoGroup, SWT.NONE, GridData.FILL_HORIZONTAL) ;
        GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).applyTo(vdbArchiveNameFld);
        vdbArchiveNameFld.setText(vdbManager.getDelegateArchiveVdbName());
        vdbArchiveNameFld.setToolTipText("Specify name for VDB. This name can be the same as the dynamic VDB name above, but both VDBs cannot be deployed at the same time");
        vdbArchiveNameFld.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( final ModifyEvent event ) {
            	vdbManager.setDelegateArchiveVdbName(vdbArchiveNameFld.getText());
                validatePage();
            }
        });
        
        WidgetFactory.createLabel(vdbInfoGroup, GridData.VERTICAL_ALIGN_CENTER, "VDB Archive File Name");
        vdbArchiveFileNameFld = WidgetFactory.createTextField(vdbInfoGroup, SWT.NONE, GridData.FILL_HORIZONTAL) ;
        GridDataFactory.fillDefaults().span(2, 1).grab(true,  false).applyTo(vdbArchiveFileNameFld);
        vdbArchiveFileNameFld.setText(vdbManager.getVdbArchiveFileName());
        vdbArchiveFileNameFld.setToolTipText("Specify unique file name for VDB archive. This name will have a *.vdb extension");
        vdbArchiveFileNameFld.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( final ModifyEvent event ) {
            	vdbManager.setVdbArchiveFileName(vdbArchiveFileNameFld.getText());
                validatePage();
            }
        });
	}
	
    private void createSourceModelsGroup( Composite parent ) {

		Group group = WidgetFactory.createGroup(parent, "Source Models", SWT.FILL, 1, 1);  //$NON-NLS-1$
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(group);
		GridData gd_2 = new GridData(GridData.FILL_BOTH);
		gd_2.widthHint = 220;
		group.setLayoutData(gd_2);
		// Add a simple list box entry form with String contents
    	this.sourceModelsViewer = new ListViewer(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=1;
        this.sourceModelsViewer.getControl().setLayoutData(data);
        
        for( Object model : getSourceModels()) {
        	this.sourceModelsViewer.add(model);
        }
    }
    
    private void createViewModelsGroup( Composite parent ) {

		Group group = WidgetFactory.createGroup(parent, "View Models", SWT.FILL, 1, 1);  //$NON-NLS-1$
		GridLayoutFactory.fillDefaults().margins(10, 10).applyTo(group);
		GridData gd_2 = new GridData(GridData.FILL_BOTH);
		gd_2.widthHint = 220;
		group.setLayoutData(gd_2);
		// Add a simple list box entry form with String contents
    	this.viewModelsViewer = new ListViewer(group, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.horizontalSpan=1;
        this.viewModelsViewer.getControl().setLayoutData(data);

        for( Object model : getViewModels()) {
        	this.viewModelsViewer.add(model);
        }
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
	
	Object[] getSourceModels() {
		Collection<String> modelNames = new ArrayList<String>();
		
		for( Model model : vdbManager.getDynamicVdb().getModels() ) {
			if( model.getModelType() == Model.Type.PHYSICAL ) {
				modelNames.add(model.getName());
			}
		}
		
		return modelNames.toArray();
	}
	
	Object[] getViewModels() {
		Collection<String> modelNames = new ArrayList<String>();
		
		for( Model model : vdbManager.getDynamicVdb().getModels() ) {
			if( model.getModelType() == Model.Type.VIRTUAL ) {
				modelNames.add(model.getName());
			}
		}
		
		return modelNames.toArray();
	}
	
    void handleBrowse() {
    	IProject project = vdbManager.getDynamicVdbFile().getProject();
        final IContainer folder = WidgetUtil.showFolderSelectionDialog(project,
                                                                       new SingleProjectOrFolderFilter(project),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && vdbArchiveLocationText != null) {
        	vdbManager.setOutputLocation(folder);
        	vdbArchiveLocationText.setText(folder.getFullPath().makeRelative().toString());
        }
        

        validatePage();
    }

}
