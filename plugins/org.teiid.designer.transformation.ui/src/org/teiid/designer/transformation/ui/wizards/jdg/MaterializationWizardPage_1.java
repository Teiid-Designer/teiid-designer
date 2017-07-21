package org.teiid.designer.transformation.ui.wizards.jdg;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.transformation.materialization.MaterializedModelManager;
import org.teiid.designer.transformation.reverseeng.ReverseEngConstants;
import org.teiid.designer.transformation.reverseeng.ReverseEngConstants.JDG_VERSION;
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.graphics.GlobalUiColorManager;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.SingleProjectOrFolderFilter;

public class MaterializationWizardPage_1 extends AbstractWizardPage implements UiConstants {
   
    private boolean synchronizing;
    
	Text modelNameField;
	Text locationField;
	Label locationLabel;
	Text cacheNameField;
	
	Combo jdgVersionCombo;
    
    private MaterializedModelManager manager;

	public MaterializationWizardPage_1(MaterializedModelManager generator) {
		super(MaterializationWizardPage_1.class.getSimpleName(), Messages.MaterializationWizardPage_1_Title);
		this.manager = generator;
	}

	@Override
	public void createControl(Composite parent) {

		final Composite hostPanel = new Composite(parent, SWT.NONE);
		hostPanel.setLayout(new GridLayout(1, false));
		hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Create page
		DefaultScrolledComposite scrolledComposite = new DefaultScrolledComposite(hostPanel);
		hostPanel.setLayout(new GridLayout(1, false));
		hostPanel.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Composite mainPanel = scrolledComposite.getPanel();
		mainPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		mainPanel.setLayout(new GridLayout(1, false));
		
		
		// Add widgets to page
    	Group viewPanel = WidgetFactory.createGroup(mainPanel, Messages.MaterializationWizardPage_1_ViewModelGroupName, SWT.NONE, 1, 2);
    	viewPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	((GridLayout)viewPanel.getLayout()).marginLeft = 10;
    	((GridLayout)viewPanel.getLayout()).marginRight = 10;
		
    	Label vModelLabel = WidgetFactory.createLabel(viewPanel, Messages.MaterializationWizardPage_1_Name);
    	vModelLabel.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_RELATIONAL_TABLE));
        ((GridData)vModelLabel.getLayoutData()).verticalAlignment = GridData.CENTER;
        vModelLabel.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.VIEW_MODEL));
        
        Text virtualModelField = WidgetFactory.createTextField(viewPanel);
        virtualModelField.setText(manager.getMaterializedViewModel().getItemName());
        virtualModelField.setEditable(false);
        virtualModelField.setBackground(viewPanel.getBackground());
        virtualModelField.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        virtualModelField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
    	Label vTableLabel = WidgetFactory.createLabel(viewPanel, Messages.MaterializationWizardPage_1_SelectedView);
    	vTableLabel.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.VIRTUAL_RELATIONAL_TABLE));
        ((GridData)vTableLabel.getLayoutData()).verticalAlignment = GridData.CENTER;
        
        Text virtualTableField = WidgetFactory.createTextField(viewPanel);
        virtualTableField.setText(manager.getVirtualTable().getName());
        virtualTableField.setEditable(false);
        virtualTableField.setBackground(viewPanel.getBackground());
        virtualTableField.setForeground(GlobalUiColorManager.EMPHASIS_COLOR);
        virtualTableField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		/* This page needs to dislpay and control
		 * 
		 * 1) The selected view (Label + Text)
		 * 2) Materialized Source model (Label + Text)
		 * 3) 
		 */
		
		// Add widgets to page
    	Group theGroup = WidgetFactory.createGroup(mainPanel, Messages.MaterializationWizardPage_1_GroupName, SWT.NONE, 1, 3);
    	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	((GridLayout)theGroup.getLayout()).marginLeft = 10;
    	((GridLayout)theGroup.getLayout()).marginRight = 10;
        
    	Label label = WidgetFactory.createLabel(theGroup, Messages.MaterializationWizardPage_1_SourceModelName); 
        label.setToolTipText(Messages.MaterializationWizardPage_1_SourceModelNameTooltip);
        ((GridData)label.getLayoutData()).verticalAlignment = GridData.CENTER;
        label.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.SOURCE_MODEL));
        
        this.modelNameField = WidgetFactory.createTextField(theGroup);
        this.modelNameField.setToolTipText(Messages.MaterializationWizardPage_1_SourceModelNameTooltip);
        
        String modelName = manager.getMaterializedSourceModelName();
        if( ! StringUtilities.isEmpty(modelName) ) {
        	this.modelNameField.setText(modelName);
        }
        this.modelNameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData)this.modelNameField.getLayoutData()).horizontalSpan = 2;
        ((GridData)this.modelNameField.getLayoutData()).verticalAlignment = GridData.CENTER;
//        GridDataFactory.swtDefaults().grab(true,  true).applyTo(modelNameField);
        
        this.modelNameField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if( synchronizing ) return;
				
				String modelName = modelNameField.getText();
				 if( ! StringUtilities.isEmpty(modelName) ) {
					
					 manager.setMaterializedSourceModelName(modelName);
				} else {
					modelName = ""; //$NON-NLS-1$
					manager.setMaterializedSourceModelName(modelName);
				}
				 validate();
			}
		});

        // Instruction label.
        locationLabel = new Label(theGroup, SWT.NULL);
        locationLabel.setText(Messages.MaterializationWizardPage_1_SourceModelLocationLabel);
        locationLabel.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.OPEN_FOLDER));
        
        locationField = new Text(theGroup, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.verticalAlignment = GridData.CENTER;
        locationField.setLayoutData(gd);
        locationField.setText(manager.getTargetLocation().getName());
        locationField.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( ModifyEvent e ) {
                validate();
            }
        });
        
        locationField.setEditable(false);

        Button browseButton = new Button(theGroup, SWT.PUSH);
        GridData buttonGridData = new GridData();
        browseButton.setLayoutData(buttonGridData);
        browseButton.setText("..."); //$NON-NLS-1$
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                try {
					handleBrowse();
				} catch (ModelWorkspaceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        
		// Add widgets to page
    	Group jdgOptionsGroup = WidgetFactory.createGroup(mainPanel, Messages.MaterializationWizardPage_1_JDGOptionsGroupName, SWT.NONE, 1, 2);
    	jdgOptionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	((GridLayout)jdgOptionsGroup.getLayout()).marginLeft = 10;
    	((GridLayout)jdgOptionsGroup.getLayout()).marginRight = 10;
    	
        // ================== ANNOTATION TYPE COMBO BOX ====================
		
        Label jdgVersionLabel = new Label(jdgOptionsGroup, SWT.NONE);
        jdgVersionLabel.setText(Messages.MaterializationWizardPage_1_JdgVersionComboLabel);
        jdgVersionLabel.setToolTipText(Messages.MaterializationWizardPage_1_JdgVersionComboTooltip);
        gd = new GridData();
        jdgVersionLabel.setLayoutData(gd);

        jdgVersionCombo = WidgetFactory.createCombo(jdgOptionsGroup, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
        gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.verticalAlignment = GridData.BEGINNING;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        jdgVersionCombo.setLayoutData(gd);
        jdgVersionCombo.setItems(ReverseEngConstants.JDG_VERSIONS);
        jdgVersionCombo.select(1);
        jdgVersionCombo.setText(ReverseEngConstants.JDG_VERSIONS[1]);
        GridDataFactory.swtDefaults().grab(false, false).applyTo(jdgVersionCombo);
        jdgVersionCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean is7DOT1 = ((Combo)e.widget).getText().equalsIgnoreCase(ReverseEngConstants.JDG_VERSIONS[1]);

				if( is7DOT1) {
					manager.setJdgVersion(JDG_VERSION.JDG_7_DOT_1);
					cacheNameField.setEnabled(true);
				} else {
					cacheNameField.setEnabled(false);
					manager.setJdgVersion(JDG_VERSION.JDG_6_DOT_6);
				}
				validate();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

        jdgVersionCombo.setVisibleItemCount(2);

    	Label cacheLabel = WidgetFactory.createLabel(jdgOptionsGroup, Messages.MaterializationWizardPage_1_CacheLabel); 
    	cacheLabel.setToolTipText(Messages.MaterializationWizardPage_1_CacheTooltip);
        ((GridData)cacheLabel.getLayoutData()).verticalAlignment = GridData.CENTER;
        
        this.cacheNameField = WidgetFactory.createTextField(jdgOptionsGroup);
        this.cacheNameField.setToolTipText(Messages.MaterializationWizardPage_1_SourceModelNameTooltip);
        
        String cacheName = manager.getJdgCacheName();
        if( ! StringUtilities.isEmpty(cacheName) ) {
        	this.cacheNameField.setText(cacheName);
        }
        this.cacheNameField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ((GridData)this.cacheNameField.getLayoutData()).horizontalSpan = 1;
        ((GridData)this.cacheNameField.getLayoutData()).verticalAlignment = GridData.CENTER;
//        GridDataFactory.swtDefaults().grab(true,  true).applyTo(modelNameField);
        
        this.cacheNameField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if( synchronizing ) return;
				
				String jdgCacheName = cacheNameField.getText();
				 if( ! StringUtilities.isEmpty(modelName) ) {
					
					 manager.setJdgCacheName(jdgCacheName);
				} else {
					jdgCacheName = ""; //$NON-NLS-1$
					manager.setJdgCacheName(jdgCacheName);
				}
				 validate();
			}
		});


		scrolledComposite.sizeScrolledPanel();

		setControl(hostPanel);

		setMessage(Messages.MaterializationWizardPage_1_Message);

		setPageComplete(true);
	}
	
    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     * @throws ModelWorkspaceException 
     */
    void handleBrowse() throws ModelWorkspaceException {
        IContainer folder = WidgetUtil.showFolderSelectionDialog(this.manager.getProject(),
                                                                       new SingleProjectOrFolderFilter(this.manager.getProject()),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null && locationField != null) {
        	locationField.setText(folder.getFullPath().makeRelative().toString());
            this.manager.setTargetLocation(folder);
        }
        
        synchronizeUI();
        
    }

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			synchronizeUI();
		}
	}
	
	void validate() {
		IStatus status = manager.validate(1);
		
		if( status.isOK() ) {
			setErrorMessage(null);
			setMessage(Messages.MaterializationWizardPage_2_ClickFinish_MaterializeOnly);
			setPageComplete(true);
		} else if( status.getSeverity() == IStatus.WARNING) {
			setErrorMessage(null);
			setMessage(status.getMessage(), IStatus.WARNING);
			setPageComplete(true);
		} else {
			setErrorMessage(status.getMessage());
			setPageComplete(false);
		}
	}
	
    void synchronizeUI(){
    	synchronizing = true;
        
    	// Set image for location
    	IContainer location = this.manager.getTargetLocation();
    	if(  location != null && location instanceof IProject ) {
    		locationField.setText(location.getFullPath().makeRelative().toString());
    		locationLabel.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.MODEL_PROJECT));
    	} else {
    		locationField.setText(location.getFullPath().makeRelative().toString());
    		locationLabel.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.OPEN_FOLDER));
    	}
                
        synchronizing = false;
    }
}