/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dataservices.ui.editor;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ui.actions.DeleteAction;
import org.eclipse.datatools.connectivity.ui.actions.ViewPropertyAction;
import org.eclipse.datatools.connectivity.ui.wizards.NewFilteredCPWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.EditorPart;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.panels.DataSourceItem;
import org.teiid.designer.datasources.ui.sources.ConnectionProfilesPanel;
import org.teiid.designer.datasources.ui.sources.CreateDataSourceAction;
import org.teiid.designer.datasources.ui.sources.DataSourcesPanel;
import org.teiid.designer.datasources.ui.sources.GlobalConnectionManager;
import org.teiid.designer.datasources.ui.sources.GlobalConnectionTreeProvider;
import org.teiid.designer.datasources.ui.sources.RootConnectionNode;
import org.teiid.designer.datasources.ui.wizard.CreateDataSourceDialog;
import org.teiid.designer.ui.common.util.LayoutDebugger;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.explorer.ModelExplorerContentProvider;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;

public class DataServiceProjectEditor extends EditorPart {

    private Composite control;
	protected ManagedForm managedForm;
	
    GlobalConnectionManager manager;
	TreeViewer connectionsTreeViewer;
	TreeViewer projectTreeViewer;

    private Button newCPButton;
    private Button deleteCPButton;
    private Button editCPButton;
    
    private IAction dummyAction;
    private IAction createAction;
    private IAction deleteAction;
    private IAction editAction;
    private IAction generateSourceModelAction;
    private IAction createDataSourceAction;
    
    // Add a Context Menu
    private MenuManager treeMenuManager;
    
	public DataServiceProjectEditor() {
        this.manager = new GlobalConnectionManager();
        this.treeMenuManager = new MenuManager();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		
		if( !(input instanceof DSProjectEditorInput) ) {
			throw new PartInitException("File " + input.getName() + " is not a project");
		}
		DSProjectEditorInput dsInput = (DSProjectEditorInput)input;
		
        setSite(site);
        setInput(dsInput);
        setPartName(dsInput.getProject().getName());
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
        control = new Composite(parent, SWT.NONE);
        FillLayout layout = new FillLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        control.setLayout(layout);
        
        createPrimaryForm(control);
        
//        LayoutDebugger.debugLayout(control);
	}

	@Override
	public void setFocus() {
        if (control != null) {
            control.setFocus();
        }
	}
	
	private void createPrimaryForm(Composite parent ) {
		managedForm = new ManagedForm(parent);
//		setManagedForm(managedForm);
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		toolkit.decorateFormHeading(form.getForm());
		form.setText("Data Services Project Editor");
//		form.setImage(ImageResource.getImage(ImageResource.IMG_SERVER));
		form.getBody().setLayout(new GridLayout());
		
		Composite columnComp = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		//layout.marginHeight = 10;
		//layout.marginWidth = 10;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		columnComp.setLayout(layout);
		columnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		// left column
		Composite leftColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		leftColumnComp.setLayout(layout);
		leftColumnComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); //GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		createLeftSection(leftColumnComp, toolkit);
		
		//insertSections(leftColumnComp, "org.eclipse.wst.server.editor.overview.left");
		
		// right column
		Composite rightColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		rightColumnComp.setLayout(layout);
		rightColumnComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); //GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		createRightSection(rightColumnComp, toolkit);
		
		form.reflow(true);
	}
	
	protected void createLeftSection(Composite leftColumnComp, FormToolkit toolkit) {
		Section section = toolkit.createSection(leftColumnComp, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText("Connections");
		section.setDescription("Configured connection profiles and deployed data sources");
		section.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_FILL));
		GridDataFactory.swtDefaults().grab(true,  true).applyTo(section);
		
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_FILL));
		GridDataFactory.swtDefaults().grab(true,  true).applyTo(composite);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		addProfilesPanel(toolkit, composite);

	}
	
	private void addProfilesPanel(FormToolkit toolkit, Composite composite) {

        createButtonsPanel(composite);

        this.connectionsTreeViewer = new TreeViewer(composite,SWT.NONE | SWT.BORDER | SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_BOTH);
		this.connectionsTreeViewer.getControl().setLayoutData(data);
		GridDataFactory.swtDefaults().grab(true,  true).hint(400, 300).applyTo(connectionsTreeViewer.getTree());
        
        GlobalConnectionTreeProvider provider = new GlobalConnectionTreeProvider(this.manager);
        
        this.connectionsTreeViewer.setContentProvider(provider);
        
        this.connectionsTreeViewer.setLabelProvider(provider);

        this.connectionsTreeViewer.setInput(this.manager);
        
        this.connectionsTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setButtonsState();
			}
		});
//        LayoutDebugger.debugLayout(this);
        
        this.dummyAction = new Action(null) {
            @Override
            public void run() {
               // DO NOTHING
            }
        };
        

        this.connectionsTreeViewer.getControl().setMenu(treeMenuManager.createContextMenu(this.connectionsTreeViewer.getTree()));
        
        this.createAction = new Action("New...") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleCreateProfile();
            	} else if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
            		if( manager.isServerAvailable() ) {
            			handleCreateSource();
            		}
            	} else {
            		handleCreateProfile();
            	}
            }
		};
		
        this.editAction = new Action("Edit...") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleEditProfile();
            	} else if( isDataSourceSelected() ) {
            		handleEditSource();
            	}
            }
		};
		
        this.deleteAction = new Action("Delete") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleDeleteProfile();
            	} else if( isDataSourceSelected() ) {
            		handleDeleteSource();
            	}
            }
		};
		
        this.generateSourceModelAction = new Action("Generate Source Model") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleImportFromProfile();
            	}
            }
		};
		
		this.createDataSourceAction =  new Action("Create Data Source") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleCreateDataSource();
            	}
            }
		};
	}
	
    /**
     * Create the buttons panel containing the new, delete and edit buttons
     * @param parent the parent composite
     */
    private void createButtonsPanel(Composite parent) {
        Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        panel.setLayout(new GridLayout(1, false));
        GridData groupGD = new GridData();
        groupGD.heightHint= 160; //GROUP_HEIGHT_160;
        groupGD.verticalAlignment=GridData.BEGINNING;
        panel.setLayoutData(groupGD);
        
        newCPButton = new Button(panel, SWT.PUSH);
        newCPButton.setText(Messages.dataSourcePanel_newButtonText);
        newCPButton.setToolTipText(Messages.dataSourcePanel_newButtonTooltip);
        newCPButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        newCPButton.setEnabled(true);
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	if( isProfileSelected() ) {
            		handleCreateProfile();
            	} else if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
            		if( manager.isServerAvailable() ) {
            			handleCreateSource();
            		} else {
            			handleCreateProfile();
            		}
            	} else {
        			handleCreateProfile();
        		}
            }
            
        });
        
        deleteCPButton = new Button(panel, SWT.PUSH);
        deleteCPButton.setText(Messages.dataSourcePanel_deleteButtonText);
        deleteCPButton.setToolTipText(Messages.dataSourcePanel_deleteButtonTooltip);
        deleteCPButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        deleteCPButton.setEnabled(false);
        deleteCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	if( isProfileSelected() ) {
            		handleDeleteProfile();
            	} else if( isDataSourceSelected() ) {
            		handleDeleteSource();
            	}
            }
            
        });
        deleteCPButton.setEnabled(false);
        
        editCPButton = new Button(panel, SWT.PUSH);
        editCPButton.setText(Messages.dataSourcePanel_editButtonText);
        editCPButton.setToolTipText(Messages.dataSourcePanel_editButtonTooltip);
        editCPButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        editCPButton.setEnabled(false);
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	if( isProfileSelected() ) {
            		handleEditProfile();
            	} else if( isDataSourceSelected() ) {
            		handleEditSource();
            	}
            }
            
        });
        
        editCPButton.setEnabled(false);

    }
	
	protected void createRightSection(Composite leftColumnComp, FormToolkit toolkit) {
		Section section = toolkit.createSection(leftColumnComp, ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText("Project Contents");
//		section.setDescription(Messages.serverEditorOverviewGeneralDescription);
		section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); //GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_FILL));
		
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); //GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_FILL));
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		
		addProjectExplorerPanel(toolkit, composite);
	}
	

	protected Label createLabel(FormToolkit toolkit, Composite parent, String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}
	
	// =========================================================
    
    void setButtonsState() {
    	boolean selection = false;

    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && 
    			( obj.getFirstElement() instanceof IConnectionProfile ) || obj.getFirstElement() instanceof DataSourceItem )  {
    		selection = true;
    	}
    	deleteCPButton.setEnabled(selection);
    	dummyAction.setEnabled(selection);
    	editCPButton.setEnabled(selection);
    	
		treeMenuManager.removeAll();
		
		if( isProfileSelected() || !isDataSourceTreeSelected() ) {
			// can create a profile any time
			treeMenuManager.add(createAction);
			treeMenuManager.add(generateSourceModelAction);
			if( manager.getImportManager().isValidImportServer() ) {
				treeMenuManager.add(createDataSourceAction);
			}
		} else if( manager.isServerAvailable() ){
			// can't create a DS without a running server
			treeMenuManager.add(createAction);
		}
		treeMenuManager.add(new Separator());
		if( selection ) {
			treeMenuManager.add(editAction);
			treeMenuManager.add(deleteAction);
		}
    }
    
    void handleCreateProfile() {
    	NewFilteredCPWizard wiz = new NewFilteredCPWizard();

		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
		//wizardDialog.setBlockOnOpen(true);
		wizardDialog.open();
		
    	connectionsTreeViewer.refresh();
    	setButtonsState();
    }
    
    void handleDeleteProfile() {
    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		DeleteAction action = new DeleteAction();
    		action.selectionChanged(dummyAction, obj);
    		
    		action.run();
    	}
    	connectionsTreeViewer.refresh();
    	setButtonsState();
    }
    
    void handleEditProfile() {
    	EditProfileAction action = new EditProfileAction(this.connectionsTreeViewer);
    	
    	action.run();
    	
    	connectionsTreeViewer.refresh();
    	setButtonsState();
    }
    
    class EditProfileAction extends ViewPropertyAction {

		public EditProfileAction(Viewer viewer) {
			super(viewer);

		}

		@Override
		public Object getSelectedObject() {
	    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
	    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
	    		return obj.getFirstElement();
	    	}
	    	
	    	return null;
		}
    	
    }
	
    /*
     * Handler for creating a new Data Source
     */
    private void handleCreateSource() {
        // Show dialog for creating the DataSource
        CreateDataSourceDialog dialog = new CreateDataSourceDialog(this.getEditorSite().getShell(), manager.getImportManager(), null);
        
        dialog.open();
        
        
        // If Dialog was OKd, create the DataSource
        if (dialog.getReturnCode() == Window.OK) {
            final String dsName = dialog.getDataSourceName();
            final String dsDriver = dialog.getDataSourceDriverName();
            final Properties dsProperties = dialog.getDataSourceProperties();
            IStatus createStatus = manager.getDataSourceManager().createDataSource(dsName, dsDriver, dsProperties);
            
            // If create failed, show Error Dialog
            if(!createStatus.isOK()) {
                ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_createErrorTitle, createStatus.getMessage(), createStatus); 
            }
            
            // Refresh the table and select the just-deployed template
            this.connectionsTreeViewer.refresh();

        }
    }
    
    /* 
     * Handler for deleting the dataSource
     */
    private void handleDeleteSource() {
        // Confirm Deletion
        if(MessageDialog.openQuestion(this.getEditorSite().getShell(), Messages.dataSourcePanel_deleteSourceDialogTitle, 
                                      Messages.dataSourcePanel_deleteSourceDialogMsg)) {
            final String dsName = getSelectedDataSourceName();

            IStatus deleteStatus = manager.getDataSourceManager().deleteDataSource(dsName);
            
            // If create failed, show Error Dialog
            if(!deleteStatus.isOK()) {
                ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_deleteErrorTitle, deleteStatus.getMessage(), deleteStatus); 
            }

            // Refresh the table and select the just-deployed template
    		manager.refreshDataSourceList( );
            this.connectionsTreeViewer.refresh();
        }
    }
    
    /* 
     * Handler for editing the dataSource
     */
    private void handleEditSource() {
        String dataSourceName = getSelectedDataSourceName();
        // Show dialog for creating the DataSource
        CreateDataSourceDialog dialog = new CreateDataSourceDialog(this.getEditorSite().getShell(), manager.getImportManager(), dataSourceName);

        dialog.open();
        
        // If Dialog was OKd, update the DataSource
        if (dialog.getReturnCode() == Window.OK) {
            // No need to update if no properties changed
            if(dialog.hasPropertyChanges()) {
                final String sourceName = dialog.getDataSourceName();
                final String dsDriver = dialog.getDataSourceDriverName();
                final Properties dsProps = dialog.getDataSourceProperties();

                IStatus deleteCreateStatus = manager.getDataSourceManager().deleteAndCreateDataSource(sourceName,sourceName,dsDriver,dsProps);

                // If create failed, show Error Dialog
                if(!deleteCreateStatus.isOK()) {
                    ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_editErrorTitle, deleteCreateStatus.getMessage(), deleteCreateStatus); 
                }

                // Refresh the table and select the just-deployed template

                this.connectionsTreeViewer.refresh();

            }
        }
    }
    
    private void handleImportFromProfile() {
    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		IConnectionProfile profile = (IConnectionProfile)obj.getFirstElement();
    		if( profile.getCategory().getName().equals(UiConstants.DATABASE_CONNECTIONS) ) {
    			Properties props = new Properties();
    			props.setProperty("profileId", profile.getName());
    			ModelerUiViewUtils.launchWizard("jdbcImportWizard", new StructuredSelection(), new Properties(), true);
    		} else if(profile.getCategory().getName().equals("Flat File Data Source") ) {
    			Properties props = new Properties();
    			props.setProperty("profileId", profile.getName());
    			ModelerUiViewUtils.launchWizard("teiidMetadataImportWizard", new StructuredSelection(), new Properties(), true);
    		}
    	}
    }
    
    private void handleCreateDataSource() {
    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		CreateDataSourceAction action = new CreateDataSourceAction((IConnectionProfile)obj.getFirstElement());
    		action.setTeiidServer(ModelerCore.getTeiidServerManager().getDefaultServer());
    		action.run();
    		manager.refreshDataSourceList( );
    		this.connectionsTreeViewer.refresh();
    	}
    }
    
    /**
     * Get the currently selected DataSource Name
     * @return the selected dataSource name
     */
    private String getSelectedDataSourceName() {
    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof DataSourceItem ) {
	        DataSourceItem selectedDS = (DataSourceItem)obj.getFirstElement();
	        return (selectedDS==null) ? null : selectedDS.getName();
    	}
    	
    	return null;
    }
    
    private boolean isProfileSelected() {
    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isDataSourceSelected() {
    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof DataSourceItem ) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isDataSourceTreeSelected() {
    	IStructuredSelection obj = (IStructuredSelection)connectionsTreeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof RootConnectionNode ) {
    		RootConnectionNode node = (RootConnectionNode)obj.getFirstElement();
    		return node.isDataSource();
    	}
    	
    	return false;
    }
    
    private void addProjectExplorerPanel(FormToolkit toolkit, Composite composite) {
        this.projectTreeViewer = new TreeViewer(composite,SWT.NONE | SWT.BORDER | SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_BOTH);
		this.projectTreeViewer.getControl().setLayoutData(data);
		GridDataFactory.swtDefaults().grab(true,  true).hint(400, 300).applyTo(connectionsTreeViewer.getTree());
        
        ITreeContentProvider provider = new SingleProjectContentsContentProvider(((DSProjectEditorInput)getEditorInput()).getProject());
        
        this.projectTreeViewer.setContentProvider(provider);
        
        this.projectTreeViewer.setLabelProvider(new ModelExplorerLabelProvider());
        
        this.projectTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// TODO:
			}
		});
        
        this.projectTreeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot());
    }
    
    /**
    *
    */
   class SingleProjectContentsContentProvider  extends ModelExplorerContentProvider {
   	IProject project;
   	
   	
       /* (non-Javadoc)
        * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
        */
       /**
   	 * @param project
   	 */
   	public SingleProjectContentsContentProvider(IProject project) {
   		super();
   		this.project = project;
   	}


   	@Override
       public Object[] getElements( Object inputElement ) {
       	for( Object element : getChildren(inputElement)) {
       		if( element instanceof IProject && ((IProject)element == this.project) ) {
       			return getChildren(element);
       		}
       	}
           return new Object[0];
       }
   }

}
