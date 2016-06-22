/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.jdg;

import org.eclipse.core.resources.IContainer;
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
import org.teiid.designer.transformation.ui.UiConstants;
import org.teiid.designer.transformation.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.widget.DefaultScrolledComposite;
import org.teiid.designer.ui.common.widget.Label;
import org.teiid.designer.ui.common.wizard.AbstractWizardPage;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;

public class PojoWizardPage_2 extends AbstractWizardPage implements UiConstants, ReverseEngConstants {
   
    private boolean synchronizing;
    
	Text packageField;
	Text classNameField;
	Text moduleZipFileNameField;
	
//	Button saveToFileSystemCB;
//	Text fileLocationField;
//	Button fileLocationBrowseButton;
	
	Button saveToWorkspaceCB;
	Text workspaceLocationField;
	Button workspaceLocationBrowseButton;
    
    private MaterializedModelManager manager;

	public PojoWizardPage_2(MaterializedModelManager generator) {
		super(PojoWizardPage_2.class.getSimpleName(), Messages.PojoWizardPage_2_Title);
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
		
		/* This page needs to dislpay and control
		 * 
		 * 1) The selected view (Label + Text)
		 * 2) Materialized Source model (Label + Text)
		 * 3) 
		 */
		
		{ // ==========  POJO Package and Class name info ========================
			// Add widgets to page
	    	Group theGroup = WidgetFactory.createGroup(mainPanel, Messages.PojoWizardPage_2_PojoDefinition, SWT.NONE, 1, 2);
	    	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    	((GridLayout)theGroup.getLayout()).marginLeft = 10;
	    	((GridLayout)theGroup.getLayout()).marginRight = 10;
	        
	    	WidgetFactory.createLabel(theGroup, Messages.PojoWizardPage_2_PackageName); 
	        
	        this.packageField = WidgetFactory.createTextField(theGroup);
	        this.packageField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        ((GridData)this.packageField.getLayoutData()).horizontalSpan = 1;
	
	        String name = manager.getPojoPackageName();
	        if( ! StringUtilities.isEmpty(name) ) {
	        	this.packageField.setText(name);
	        }
	        
	        this.packageField.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					if( synchronizing ) return;
					
					String name = packageField.getText();
					 if( ! StringUtilities.isEmpty(name) ) {
						 manager.setPojoPackageName(name);
					} else {
						name = ""; //$NON-NLS-1$
						manager.setPojoPackageName(name);
					}
					validate();
				}
			});
	
	        // Instruction label.
	        Label classNameLabel = new Label(theGroup, SWT.NULL);
	        classNameLabel.setText(Messages.PojoWizardPage_2_ClassName); //$NON-NLS-1$
	        
	        classNameField = new Text(theGroup, SWT.BORDER | SWT.SINGLE);
	        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	        classNameField.setLayoutData(gd);
	        
	        String className = manager.getPojoClassName();
	        if( ! StringUtilities.isEmpty(className) ) {
	        	this.classNameField.setText(className);
	        }
	        
	        classNameField.addModifyListener(new ModifyListener() {
	            @Override
				public void modifyText( ModifyEvent e ) {
					if( synchronizing ) return;
					
					String name = classNameField.getText();
					 if( ! StringUtilities.isEmpty(name) ) {
						 manager.setPojoClassName(name);
					} else {
						name = ""; //$NON-NLS-1$
						manager.setPojoClassName(name);
					}
					 
	                validate();
	            }
	        }); 
	        classNameField.setEditable(true);
	        
	        // ================== ANNOTATION TYPE COMBO BOX ====================
			
	        Label annotationTypeLabel = new Label(theGroup, SWT.NONE);
	        annotationTypeLabel.setText(Messages.PojoWizardPage_2_AnnotationType);
	        annotationTypeLabel.setToolTipText(Messages.PojoWizardPage_2_AnnotationTypeTooltip);
	        gd = new GridData();
	        annotationTypeLabel.setLayoutData(gd);
	
	        Combo annotationTypeCombo = WidgetFactory.createCombo(theGroup, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
	        gd = new GridData();
	        gd.horizontalAlignment = GridData.FILL;
	        gd.verticalAlignment = GridData.BEGINNING;
	        gd.grabExcessHorizontalSpace = true;
	        annotationTypeCombo.setLayoutData(gd);
	        annotationTypeCombo.setItems(new String[] { MaterializedModelManager.NONE,
	        											MaterializedModelManager.PROTOBUF, 
	        											MaterializedModelManager.HIBERNATE });
	        annotationTypeCombo.select(1);
	        annotationTypeCombo.setText(MaterializedModelManager.PROTOBUF);
	        annotationTypeCombo.setToolTipText(Messages.PojoWizardPage_2_AnnotationTypeTooltip);
	        GridDataFactory.swtDefaults().grab(false, false).applyTo(annotationTypeCombo);
	        annotationTypeCombo.addSelectionListener(new SelectionListener() {
	
				@Override
				public void widgetSelected(SelectionEvent e) {
					manager.setAnnotationType(((Combo)e.widget).getText());
				}
	
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
	
	        annotationTypeCombo.setVisibleItemCount(2);
	        
	        // Instruction label.
	        Label moduleZipFileNameLabel = new Label(theGroup, SWT.NULL);
	        moduleZipFileNameLabel.setText(Messages.PojoWizardPage_2_ModuleZipFileName); //$NON-NLS-1$
	        
	        this.moduleZipFileNameField = new Text(theGroup, SWT.BORDER | SWT.SINGLE);
	        gd = new GridData(GridData.FILL_HORIZONTAL);
	        this.moduleZipFileNameField.setLayoutData(gd);
	        
	        name = manager.getModuleZipFileName();
	        if( ! StringUtilities.isEmpty(name) ) {
	        	this.moduleZipFileNameField.setText(name);
	        }
	        
	        moduleZipFileNameField.addModifyListener(new ModifyListener() {
	            @Override
				public void modifyText( ModifyEvent e ) {
					if( synchronizing ) return;
					
					String name = moduleZipFileNameField.getText();
					 if( ! StringUtilities.isEmpty(name) ) {
						 manager.setModuleZipFileName(name);
					} else {
						name = ""; //$NON-NLS-1$
						manager.setModuleZipFileName(name);
					}
	                validate();
	            }
	        });
	        moduleZipFileNameField.setEditable(true);
        }
		
		{
	    	Group theGroup = WidgetFactory.createGroup(mainPanel, Messages.PojoWizardPage_2_OutputOptions, SWT.NONE, 1, 3);
	    	theGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//	        saveToWorkspaceCB = new Button(theGroup, SWT.RADIO);
//	        saveToWorkspaceCB.setFont(theGroup.getFont());
//	        saveToWorkspaceCB.setSelection(true);
////	        saveToWorkspaceCB.addListener(SWT.Selection, this);
//	        saveToWorkspaceCB.setText("Save to Workspace");
//	        GridDataFactory.swtDefaults().span(3,  1).applyTo(saveToWorkspaceCB);
//	        saveToWorkspaceCB.addSelectionListener(new SelectionListener() {
//				
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					fileLocationField.setEnabled(saveToFileSystemCB.getSelection());
//					fileLocationBrowseButton.setEnabled(saveToFileSystemCB.getSelection());
//					workspaceLocationField.setEnabled(saveToWorkspaceCB.getSelection());
//					workspaceLocationBrowseButton.setEnabled(saveToWorkspaceCB.getSelection());
//				}
//				
//				@Override
//				public void widgetDefaultSelected(SelectionEvent e) {
//				}
//			});
	        
	        Label workspaceLocationLabel = new Label(theGroup, SWT.NULL);
	        workspaceLocationLabel.setText(Messages.PojoWizardPage_2_ProjectLocation);
	        workspaceLocationLabel.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.FOLDER));
	        workspaceLocationLabel.setToolTipText(Messages.PojoWizardPage_2_ProjectLocationTooltip);
	        
	        workspaceLocationField = new Text(theGroup, SWT.BORDER | SWT.SINGLE);
	        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	        gd.verticalAlignment = GridData.CENTER;
	        workspaceLocationField.setLayoutData(gd);
	        if( manager.getPojoWorkspaceFolder() != null ) {
	        	workspaceLocationField.setText(manager.getPojoWorkspaceFolder().getName());
	        }
	        workspaceLocationField.addModifyListener(new ModifyListener() {
	            @Override
				public void modifyText( ModifyEvent e ) {
	                validate();
	            }
	        });
	        
	        workspaceLocationField.setToolTipText(Messages.PojoWizardPage_2_ProjectLocationTooltip);
	        workspaceLocationField.setEditable(false);

	        workspaceLocationBrowseButton = new Button(theGroup, SWT.PUSH);
	        GridData buttonGridData = new GridData();
	        workspaceLocationBrowseButton.setLayoutData(buttonGridData);
	        workspaceLocationBrowseButton.setText("..."); //$NON-NLS-1$
	        workspaceLocationBrowseButton.setToolTipText(Messages.PojoWizardPage_2_ProjectLocationTooltip);
	        workspaceLocationBrowseButton.addSelectionListener(new SelectionAdapter() {
	            @Override
	            public void widgetSelected( SelectionEvent e ) {
					try {
						handleWorkspaceBrowse();
					} catch (ModelWorkspaceException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
	        });
	        
	        // Build Location
	        // FILE SYSTEM OR WORKSPACE???
	        // Add File System checkbox
//	        saveToFileSystemCB = new Button(theGroup, SWT.RADIO);
//	        saveToFileSystemCB.setFont(theGroup.getFont());
//	        saveToFileSystemCB.setSelection(false);
////	        saveToFileSystemCB.addListener(SWT.Selection, this);
//	        saveToFileSystemCB.setText("Save to File System");
//	        GridDataFactory.swtDefaults().span(3,  1).applyTo(saveToFileSystemCB);
//	        saveToFileSystemCB.addSelectionListener(new SelectionListener() {
//				"Project Location"
//				@Override
//				public void widgetSelected(SelectionEvent e) {
//					fileLocationField.setEnabled(saveToFileSystemCB.getSelection());
//					fileLocationBrowseButton.setEnabled(saveToFileSystemCB.getSelection());
//					workspaceLocationField.setEnabled(saveToWorkspaceCB.getSelection());
//					workspaceLocationBrowseButton.setEnabled(saveToWorkspaceCB.getSelection());
//				}
//				
//				@Override
//				public void widgetDefaultSelected(SelectionEvent e) {
//				}
//			});
	        
//	        Label fileLocationLabel = new Label(theGroup, SWT.NULL);
//	        fileLocationLabel.setText("Project or Folder");
//	        
//	        fileLocationField = new Text(theGroup, SWT.BORDER | SWT.SINGLE);
//	        gd = new GridData(GridData.FILL_HORIZONTAL);
//	        gd.verticalAlignment = GridData.CENTER;
//	        fileLocationField.setLayoutData(gd);
////	        if( manager.getPojoWorkspaceFolder() != null ) {
////	        	fileLocationField.setText(manager.getPojoWorkspaceFolder().getName());
////	        }
//	        fileLocationField.addModifyListener(new ModifyListener() {
//	            @Override
//				public void modifyText( ModifyEvent e ) {
//	                manager.validate();
//	            }
//	        });
//	        
//	        fileLocationField.setEditable(false);
//
//	        fileLocationBrowseButton = new Button(theGroup, SWT.PUSH);
//	        buttonGridData = new GridData();
//	        fileLocationBrowseButton.setLayoutData(buttonGridData);
//	        fileLocationBrowseButton.setText("..."); //$NON-NLS-1$
//	        fileLocationBrowseButton.addSelectionListener(new SelectionAdapter() {
//	            @Override
//	            public void widgetSelected( SelectionEvent e ) {
//					handleFileSystemBrowse();
//	            }
//	        });
//
//			fileLocationField.setEnabled(saveToFileSystemCB.getSelection());
//			fileLocationBrowseButton.setEnabled(saveToFileSystemCB.getSelection());
//			workspaceLocationField.setEnabled(saveToWorkspaceCB.getSelection());
//			workspaceLocationBrowseButton.setEnabled(saveToWorkspaceCB.getSelection());
		}

        
        // POJO java file class name
        

		scrolledComposite.sizeScrolledPanel();

		setControl(hostPanel);

		setMessage(Messages.PojoWizardPage_2_Message);

		setPageComplete(true);
	}
	
	void handleFileSystemBrowse() {
//        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
//        dlg.setFilterExtensions(new String[] {"*.*"}); //$NON-NLS-1$ 
//        dlg.setText("Select File System Folder");
//        
//        String fileStr = dlg.open();
//        
//        if( !StringUtilities.isEmpty(fileStr)) {
//        	fileLocationField.setText(fileStr);
//        	this.manager.setPojoFileSystemFolder(new File(fileStr));
//        }
	}
	
    /**
     * Uses the standard container selection dialog to choose the new value for the container field.
     * @throws ModelWorkspaceException 
     */
    void handleWorkspaceBrowse() throws ModelWorkspaceException {
        IContainer folder = WidgetUtil.showFolderSelectionDialog((IContainer)this.manager.getMaterializedViewModel().getCorrespondingResource().getProject(),
                                                                       new ModelingResourceFilter(),
                                                                       new ModelProjectSelectionStatusValidator());

        if (folder != null ) {
        	workspaceLocationField.setText(folder.getFullPath().makeRelative().toString());
            this.manager.setPojoWorkspaceFolder(folder);
        }
    }


	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (visible) {
			synchronizeUI();
		}
	}
	
	void validate() {
		IStatus status = manager.validate(2);
		
		if( status.isOK() ) {
			setErrorMessage(null);
			setMessage(Messages.PojoWizardPage_2_ClickFinish);
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
        
    	// TODO:
                
        synchronizing = false;
    }
}