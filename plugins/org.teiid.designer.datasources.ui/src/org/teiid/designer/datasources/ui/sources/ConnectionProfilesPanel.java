package org.teiid.designer.datasources.ui.sources;

import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.Category;
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
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.UiPlugin;
import org.teiid.designer.datasources.ui.panels.DataSourceItem;
import org.teiid.designer.datasources.ui.wizard.CreateDataSourceDialog;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;

public class ConnectionProfilesPanel extends Composite implements UiConstants {
    private final int GROUP_HEIGHT_160 = 160;
    
    GlobalConnectionManager manager;
	TreeViewer treeViewer;

    private Button newCPButton;
    private Button deleteCPButton;
    private Button editCPButton;
    private Button refreshButton;
    
    private IAction dummyAction;
    private IAction createAction;
    private IAction deleteAction;
    private IAction editAction;
    private IAction generateSourceModelAction;
    private IAction createDataSourceAction;
    private IAction createDataSourceFromProfileAction;
    // Add a Context Menu
    private MenuManager treeMenuManager;
	
    /**
     * DataSourcePanel constructor
     * @param parent the parent composite
     * @param visibleTableRows the number of table rows to show
     * @param teiidImportServer the TeiidServer
     */
    public ConnectionProfilesPanel( Composite parent) {
        super(parent, SWT.NONE);

        this.manager = new GlobalConnectionManager();
        this.treeMenuManager = new MenuManager();

        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(this);

        GridDataFactory.fillDefaults().grab(true,  true).applyTo(this);

        createButtonsPanel(this);

        this.treeViewer = new TreeViewer(this,SWT.NONE | SWT.BORDER | SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;
		this.treeViewer.getControl().setLayoutData(data);
        
        GlobalConnectionTreeProvider provider = new GlobalConnectionTreeProvider(this.manager);
        
        this.treeViewer.setContentProvider(provider);
        
        this.treeViewer.setLabelProvider(provider);

        this.treeViewer.setInput(this.manager);
        this.treeViewer.getControl().setMenu(treeMenuManager.createContextMenu(parent));
        this.treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
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
        
        this.createAction = new Action("Create") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
            		if( manager.serverAvailable() ) {
            			handleCreateSource();
            		}
            	} else {
            		handleCreateProfile();
            	}
            }
		};
		this.createAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.ADD_CONNECTION));
		
        this.editAction = new Action("Edit") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleEditProfile();
            	} else if( isDataSourceSelected() ) {
            		handleEditSource();
            	}
            }
		};
		this.editAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.EDIT_CONNECTION));
		
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
		this.deleteAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.REMOVE_CONNECTION));
		
        this.generateSourceModelAction = new Action("Generate Source Model") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleImportFromProfile();
            	} else {
            		// Launch the Teiid connection importer
            		handleImportFromTeiidSource();
            	}
            }
		};
		this.generateSourceModelAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.GENERATE_SOURCE_MODEL));
		
		this.createDataSourceFromProfileAction =  new Action("Create Data Source") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleCreateDataSourceFromProfile();
            	}
            }
		};
		this.createDataSourceFromProfileAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.CREATE_DATA_SOURCE));
		
		this.createDataSourceAction =  new Action("Create Data Source") { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isDataSourceSelected() ) {
            		handleCreateSource();
            	}
            }
		};
		this.createDataSourceAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.CREATE_DATA_SOURCE));
    }
    
    /**
     * Create the buttons panel containing the new, delete and edit buttons
     * @param parent the parent composite
     */
    private void createButtonsPanel(Composite parent) {
        Composite panel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        panel.setLayout(new GridLayout(1, false));
        GridData groupGD = new GridData();
        groupGD.heightHint=GROUP_HEIGHT_160;
        groupGD.verticalAlignment=GridData.BEGINNING;
        panel.setLayoutData(groupGD);
        
        newCPButton = new Button(panel, SWT.PUSH);
        newCPButton.setImage(UiPlugin.getDefault().getImage(IMAGES.ADD_CONNECTION));
        newCPButton.setToolTipText(Messages.dataSourcePanel_newButtonTooltip);
        newCPButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        newCPButton.setEnabled(true);
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
            		if( manager.serverAvailable() ) {
            			handleCreateSource();
            		}
            	} else {
            		handleCreateProfile();
            	}
            }
            
        });
        
        deleteCPButton = new Button(panel, SWT.PUSH);
        deleteCPButton.setImage(UiPlugin.getDefault().getImage(IMAGES.REMOVE_CONNECTION));
        deleteCPButton.setToolTipText("Delete");//Messages.dataSourcePanel_deleteButtonTooltip);
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
        editCPButton.setImage(UiPlugin.getDefault().getImage(IMAGES.EDIT_CONNECTION));
        editCPButton.setToolTipText("Edit"); //Messages.dataSourcePanel_editButtonTooltip);
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
        
        WidgetFactory.createLabel(panel, "");
        
        refreshButton = new Button(panel, SWT.PUSH);
//        refreshButton.setText(Messages.dataSourcePanel_refreshButtonText);
        refreshButton.setImage(UiPlugin.getDefault().getImage(IMAGES.REFRESH));
        refreshButton.setToolTipText(Messages.dataSourcePanel_refreshButtonTooltip);
        refreshButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        refreshButton.setEnabled(false);
        refreshButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	manager.refreshDataSourceList();
            	treeViewer.refresh(manager);
            }
            
        });
        refreshButton.setEnabled(true);

    }
    
    void setButtonsState() {
    	boolean selection = false;

    	if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
    		if( manager.serverAvailable() ) {
    			newCPButton.setToolTipText("Create Data Source");
    			newCPButton.setToolTipText("Create Data Source");
    		}
    		if( isDataSourceSelected() ) {
    			selection = true;
    		}
    	} else if( isProfileSelected() ) {
    		newCPButton.setToolTipText("Create Connection Profile");
    		newCPButton.setToolTipText("Create Connection Profile");
    		selection = true;
    	} else {
    		newCPButton.setToolTipText("Create Connection Profile");
    		newCPButton.setToolTipText("Create Connection Profile");
    	}
//    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
//    	if( !obj.isEmpty() && 
//    			( obj.getFirstElement() instanceof IConnectionProfile ) || obj.getFirstElement() instanceof DataSourceItem )  {
//    		selection = true;
//    	}
    	newCPButton.setEnabled(true);
    	deleteCPButton.setEnabled(selection);
    	dummyAction.setEnabled(selection);
    	editCPButton.setEnabled(selection);
    	
		treeMenuManager.removeAll();
		
		if( isProfileSelected() ) {
			// can create a profile any time
			treeMenuManager.add(createAction);
			treeMenuManager.add(generateSourceModelAction);
			if( manager.getImportManager().isValidImportServer() ) {
				treeMenuManager.add(createDataSourceFromProfileAction);
			}
		} else if( isDataSourceSelected() ) {
			// can create a profile any time
			treeMenuManager.add(createDataSourceAction);
			treeMenuManager.add(generateSourceModelAction);
//			if( manager.getImportManager().isValidImportServer() ) {
//				treeMenuManager.add(createDataSourceAction);
//			}
		} else if( profileTreeSelected() ) {
			treeMenuManager.add(createAction);
		} else if( manager.serverAvailable() ){
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
		
    	treeViewer.refresh();
    	setButtonsState();
    }
    
    void handleDeleteProfile() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		DeleteAction action = new DeleteAction();
    		action.selectionChanged(dummyAction, obj);
    		
    		action.run();
    	}
    	treeViewer.refresh();
    	setButtonsState();
    }
    
    void handleEditProfile() {
    	EditProfileAction action = new EditProfileAction(this.treeViewer);
    	
    	action.run();
    	
    	treeViewer.refresh();
    	setButtonsState();
    }
    
    class EditProfileAction extends ViewPropertyAction {

		public EditProfileAction(Viewer viewer) {
			super(viewer);

		}

		@Override
		public Object getSelectedObject() {
	    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
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
        CreateDataSourceDialog dialog = new CreateDataSourceDialog(getShell(), manager.getImportManager(), null);
        
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
            this.treeViewer.refresh();

        }
    }
    
    /* 
     * Handler for deleting the dataSource
     */
    private void handleDeleteSource() {
        // Confirm Deletion
        if(MessageDialog.openQuestion(getShell(), Messages.dataSourcePanel_deleteSourceDialogTitle, 
                                      Messages.dataSourcePanel_deleteSourceDialogMsg)) {
            final String dsName = getSelectedDataSourceName();

            IStatus deleteStatus = manager.getDataSourceManager().deleteDataSource(dsName);
            
            // If create failed, show Error Dialog
            if(!deleteStatus.isOK()) {
                ErrorDialog.openError(Display.getCurrent().getActiveShell(),Messages.dataSourcePanel_deleteErrorTitle, deleteStatus.getMessage(), deleteStatus); 
            }

            // Refresh the table and select the just-deployed template
    		manager.refreshDataSourceList( );
            this.treeViewer.refresh();
        }
    }
    
    /* 
     * Handler for editing the dataSource
     */
    private void handleEditSource() {
        String dataSourceName = getSelectedDataSourceName();
        // Show dialog for creating the DataSource
        CreateDataSourceDialog dialog = new CreateDataSourceDialog(getShell(), manager.getImportManager(), dataSourceName);

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

                this.treeViewer.refresh();

            }
        }
    }
    
    private void handleImportFromProfile() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		IConnectionProfile profile = (IConnectionProfile)obj.getFirstElement();
    		if( profile.getCategory().getName().equals(UiConstants.DATABASE_CONNECTIONS) ) {
    			System.out.println(" Profile Category = " + profile.getCategory().getName());
    			Properties props = new Properties();
    			props.setProperty("profileId", profile.getName());
    			ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.JDBC_IMPORT, new StructuredSelection(), new Properties(), true);
    		} else if( profile.getCategory().getName().equals(UiConstants.TEIID_CONNECTIONS)) {
    			
    			
    			if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.REST_WS) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.REST_WS_IMPORT, new StructuredSelection(), new Properties(), true);
    			} else if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.REST_WS) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.REST_WS_IMPORT, new StructuredSelection(), new Properties(), true);
    			} else if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.SALESFORCE) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.SALESFORCE_IMPORT, new StructuredSelection(), new Properties(), true);
    			} else if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.FILE_URL_REMOTE) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.FLAT_FILE_IMPORT, new StructuredSelection(), new Properties(), true);
    			} else {
    				System.out.println("  Profile ID = " + profile.getProviderId());
    			}
    			//ModelerUiViewUtils.launchWizard(profile.getProviderId(), new StructuredSelection(), new Properties(), true);
    		} else if( profile.getCategory().getName().equals(UiConstants.FLAT_FILE_DATA_SOURCE) ) {
    			ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.FLAT_FILE_IMPORT, new StructuredSelection(), new Properties(), true);
    		}
    	}
    }
    
    private void handleCreateDataSourceFromProfile() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		CreateDataSourceAction action = new CreateDataSourceAction((IConnectionProfile)obj.getFirstElement());
    		action.setTeiidServer(ModelerCore.getTeiidServerManager().getDefaultServer());
    		action.run();
    		manager.refreshDataSourceList( );
    		this.treeViewer.refresh();
    	}
    }
    
    private void handleCreateDataSource() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		CreateDataSourceAction action = new CreateDataSourceAction((IConnectionProfile)obj.getFirstElement());
    		action.setTeiidServer(ModelerCore.getTeiidServerManager().getDefaultServer());
    		action.run();
    		manager.refreshDataSourceList( );
    		this.treeViewer.refresh();
    	}
    }
    
    private void handleImportFromTeiidSource() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof DataSourceItem ) {
    		DataSourceItem dsi = ((DataSourceItem) obj.getFirstElement());
    		Properties props = new Properties();
    		props.setProperty("JndiName", dsi.getJndiName());
    		ModelerUiViewUtils.launchWizard(
    				ModelActionConstants.WizardsIDs.TEIID_IMPORT, new StructuredSelection(), props, true);
    	}
    }
    
    /**
     * Get the currently selected DataSource Name
     * @return the selected dataSource name
     */
    private String getSelectedDataSourceName() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof DataSourceItem ) {
	        DataSourceItem selectedDS = (DataSourceItem)obj.getFirstElement();
	        return (selectedDS==null) ? null : selectedDS.getName();
    	}
    	
    	return null;
    }
    
    private boolean profileTreeSelected() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && (obj.getFirstElement() instanceof RootConnectionNode || 
    			obj.getFirstElement() instanceof Category) ) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isProfileSelected() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isDataSourceSelected() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof DataSourceItem ) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isDataSourceTreeSelected() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof RootConnectionNode ) {
    		RootConnectionNode node = (RootConnectionNode)obj.getFirstElement();
    		return node.isDataSource();
    	}
    	
    	return false;
    }
}
