/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.ICategory;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.db.generic.ui.wizard.NewJDBCFilteredCPWizard;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizard;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizardCategoryFilter;
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
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.PluginDropAdapter;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.datasources.ui.Messages;
import org.teiid.designer.datasources.ui.UiConstants;
import org.teiid.designer.datasources.ui.UiPlugin;
import org.teiid.designer.datasources.ui.panels.DataSourceItem;
import org.teiid.designer.datasources.ui.wizard.CreateDataSourceDialog;
import org.teiid.designer.ui.common.actions.ModelActionConstants;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.viewsupport.IPropertiesContext;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;

/**
 * This panel provides the primary content for the Connections View
 * 
 * Includes a tree view containing both local connection profile nodes and a deployed server node showing available 
 * deployed data sources and resource adapters.
 * 
 * Includes a small default server section which provides edit, start and stop quick buttons
 * 
 * Deployed content available only when server is running (connected)
 * 
 * @author blafond
 *
 */
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
        
        this.createAction = new Action(Messages.Create) { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
            		if( manager.isServerAvailable() ) {
            			handleCreateSource();
            		}
            	} else {
            		handleCreateProfile();
            	}
            }
		};
		this.createAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.ADD_CONNECTION));
		
        this.editAction = new Action(Messages.Edit) { //$NON-NLS-1$
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
		
        this.deleteAction = new Action(Messages.Delete) { //$NON-NLS-1$
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
		
        this.generateSourceModelAction = new Action(Messages.GenerateSourceModel) { //$NON-NLS-1$
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
		
		this.createDataSourceFromProfileAction =  new Action(Messages.CreateDataSource) { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isProfileSelected() ) {
            		handleCreateDataSourceFromProfile();
            	}
            }
		};
		this.createDataSourceFromProfileAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.CREATE_DATA_SOURCE));
		
		this.createDataSourceAction =  new Action(Messages.CreateDataSource) { //$NON-NLS-1$
            @Override
            public void run() {
            	if( isDataSourceSelected() ) {
            		handleCreateSource();
            	} else {
            		handleCreateSource();
            	}
            }
		};
		this.createDataSourceAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(IMAGES.CREATE_DATA_SOURCE));
		
		initDragAndDrop();
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
        panel.setBackground(parent.getBackground());
        
        newCPButton = new Button(panel, SWT.PUSH);
        newCPButton.setImage(UiPlugin.getDefault().getImage(IMAGES.ADD_CONNECTION));
        newCPButton.setToolTipText(Messages.dataSourcePanel_newButtonTooltip);
        newCPButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        newCPButton.setEnabled(true);
        newCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
            		if( manager.isServerAvailable() ) {
            			handleCreateSource();
            		}
            	} else {
            		handleCreateProfile();
            	}
            }
            
        });
        
        deleteCPButton = new Button(panel, SWT.PUSH);
        deleteCPButton.setImage(UiPlugin.getDefault().getImage(IMAGES.REMOVE_CONNECTION));
        deleteCPButton.setToolTipText(Messages.Delete);
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
        editCPButton.setToolTipText(Messages.Edit);
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
        refreshButton.setImage(UiPlugin.getDefault().getImage(IMAGES.REFRESH));
        refreshButton.setToolTipText(Messages.dataSourcePanel_refreshButtonTooltip);
        refreshButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        refreshButton.setEnabled(false);
        refreshButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
            	refresh();
            }
            
        });
        refreshButton.setEnabled(true);

    }
    
    void setButtonsState() {
    	boolean selection = false;

    	if( isDataSourceSelected() || isDataSourceTreeSelected() ) {
    		if( manager.isServerAvailable() ) {
    			newCPButton.setToolTipText(Messages.CreateDataSource);
    		}
    		if( isDataSourceSelected() ) {
    			selection = true;
    		}
    	} else if( isProfileSelected() ) {
    		newCPButton.setToolTipText(Messages.CreateConnectionProfile);
    		selection = true;
    	} else {
    		newCPButton.setToolTipText(Messages.CreateConnectionProfile);
    	}

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
		} else if( profileTreeSelected() ) {
	    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
	    	if( !obj.isEmpty() && (obj.getFirstElement() instanceof RootConnectionNode) ) {
	    		if( ((RootConnectionNode)obj.getFirstElement()).getName().equalsIgnoreCase(Messages.Deployed)) {
	    			treeMenuManager.add(createDataSourceAction);
	    		}
	    	} else {
	    		treeMenuManager.add(createAction);
	    	}
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
    
    public GlobalConnectionManager getManager() {
    	return this.manager;
    }
    
    void handleCreateProfile() {
    	NewCPWizard wiz = null;
    	
    	if( isJdbcProfileSelected() ) {
    		wiz = new NewJDBCFilteredCPWizard();
    	} else if( isODAProfileSelected() ) {
            ArrayList<ViewerFilter> filters = new ArrayList<ViewerFilter>();

            // adds required category filter
            filters.add( new NewCPWizardCategoryFilter(UiConstants.ODA_PROFILE_CATEGORY_ID) );
            ViewerFilter[] viewFilters = (ViewerFilter[]) filters.toArray( new ViewerFilter[ filters.size() ]);
    		wiz = new NewCPWizard(viewFilters, null);
    	} else if( isTeiidProfileSelected() ) {
            ArrayList<ViewerFilter> filters = new ArrayList<ViewerFilter>();

            // adds required category filter
            filters.add( new NewCPWizardCategoryFilter(UiConstants.TEIID_PROFILE_CATEGORY_ID) );
            ViewerFilter[] viewFilters = (ViewerFilter[]) filters.toArray( new ViewerFilter[ filters.size() ]);
    		wiz = new NewCPWizard(viewFilters, null);
    	} else {
    		wiz = new NewFilteredCPWizard();
    	}

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
           refresh();

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
           refresh();
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

               refresh();

            }
        }
    }
    
    private void handleImportFromProfile() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		IConnectionProfile profile = (IConnectionProfile)obj.getFirstElement();
    		if( profile.getCategory().getName().equals(UiConstants.DATABASE_CONNECTIONS) ) {
    			Properties props = new Properties();
    			props.setProperty(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID, profile.getName());

    			ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.JDBC_IMPORT, new StructuredSelection(), props, true);
    		} else if( profile.getCategory().getName().equals(UiConstants.TEIID_CONNECTIONS)) {
    			Properties props = new Properties();
    			props.setProperty(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID, profile.getName());
    			
    			if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.REST_WS) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.REST_WS_IMPORT, new StructuredSelection(), props, true);
    			} else if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.REST_WS) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.REST_WS_IMPORT, new StructuredSelection(), props, true);
    			} else if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.SALESFORCE) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.SALESFORCE_IMPORT, new StructuredSelection(), props, true);
    			} else if( profile.getProviderId().equals(ModelActionConstants.ProfileIDs.FILE_URL_REMOTE) ) {
    				ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.FLAT_FILE_IMPORT, new StructuredSelection(), props, true);
    			} else {
    				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Feature not yet implemented", 
    						"Creating a source model from the profile " + profile.getProviderId() + " has not yet been implemented" + 
    						"\n\nUse corresponding Import > Teiid Designer option for this connection type.");
    				
    			}
    			//ModelerUiViewUtils.launchWizard(profile.getProviderId(), new StructuredSelection(), new Properties(), true);
    		} else if( profile.getCategory().getName().equals(UiConstants.FLAT_FILE_DATA_SOURCE) ) {
    			Properties props = new Properties();
    			props.setProperty(IPropertiesContext.KEY_LAST_CONNECTION_PROFILE_ID, profile.getName());
    			ModelerUiViewUtils.launchWizard(ModelActionConstants.WizardsIDs.FLAT_FILE_IMPORT, new StructuredSelection(), props, true);
    		} else {
     			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Feature not yet implemented", 
     					"Creating a source model from the profile " + profile.getProviderId() + " has not yet been implemented" + 
     					"\n\nUse corresponding Import > Teiid Designer option for this connection type.");
    		}
    	}
    }
    
    private void handleCreateDataSourceFromProfile() {
    	if( !this.manager.isServerAvailable() ) return;
    	
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof IConnectionProfile ) {
    		CreateDataSourceAction action = new CreateDataSourceAction((IConnectionProfile)obj.getFirstElement());
    		action.setTeiidServer(ModelerCore.getTeiidServerManager().getDefaultServer());
    		action.run();
    		refresh();
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
    			obj.getFirstElement() instanceof ICategory) ) {
    		return true;
    	}
    	
    	return false;
    }
    
    private boolean isJdbcProfileSelected() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof ICategory  &&
    		((ICategory)obj.getFirstElement()).getName().equals(UiConstants.DATABASE_CONNECTIONS)) {
    		return true;
    	} else if( isProfileSelected() ) {
    		IConnectionProfile prof = (IConnectionProfile)obj.getFirstElement();
    		if( prof.getCategory().getId().startsWith(UiConstants.RECOVERY_PASSWORD_PROP_NAME)){
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean isODAProfileSelected() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof ICategory) {
    		ICategory cat = ((ICategory)obj.getFirstElement());
    		if( cat.getName().startsWith(UiConstants.ODA_PROFILE_CATEGORY_ID_PREFIX)) {
    			return true;
    		} else if( cat.getParent() != null && cat.getParent().getId().startsWith(UiConstants.ODA_PROFILE_CATEGORY_ID_PREFIX)) {
    			return true;
    		}
    	} else if( isProfileSelected() ) {
    		IConnectionProfile prof = (IConnectionProfile)obj.getFirstElement();
    		if( prof.getCategory().getId().startsWith(UiConstants.ODA_PROFILE_CATEGORY_ID_PREFIX)){
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean isTeiidProfileSelected() {
    	IStructuredSelection obj = (IStructuredSelection)treeViewer.getSelection();
    	if( !obj.isEmpty() && obj.getFirstElement() instanceof ICategory) {
    		ICategory cat = ((ICategory)obj.getFirstElement());
    		if( cat.getId().startsWith(UiConstants.TEIID_PROFILE_CATEGORY_ID_PREFIX)) {
    			return true;
    		} else if( cat.getParent() != null && cat.getParent().getId().startsWith(UiConstants.TEIID_PROFILE_CATEGORY_ID_PREFIX)) {
    			return true;
    		}
    	} else if( !obj.isEmpty() && obj.getFirstElement() instanceof TeiidConnectionFolder) {
    		TeiidConnectionFolder folder = (TeiidConnectionFolder)obj.getFirstElement();
    		ICategory cat = folder.getCategory();
    		if( cat.getId().startsWith(UiConstants.TEIID_PROFILE_CATEGORY_ID_PREFIX)) {
    			return true;
    		} else if( cat.getParent() != null && cat.getParent().getId().startsWith(UiConstants.TEIID_PROFILE_CATEGORY_ID_PREFIX)) {
    			return true;
    		}
    	} else if( isProfileSelected() ) {
    		IConnectionProfile prof = (IConnectionProfile)obj.getFirstElement();
    		if( prof.getCategory().getId().startsWith(UiConstants.TEIID_PROFILE_CATEGORY_ID_PREFIX)){
    			return true;
    		}
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
    
    public void refresh() {
    	if( treeViewer != null && ! treeViewer.getTree().isDisposed() ) {
	    	manager.refreshDataSourceList();
	    	treeViewer.refresh();
	    	this.treeViewer.expandToLevel(2);
    	}
    }
    
    protected void initDragAndDrop() {
        // code copied from superclass. only change is to the drag adapter
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] transfers = new Transfer[] {ConnectionProfileTransfer.getInstance()};

        // drop support
        this.treeViewer.addDragSupport(ops, transfers, new DataSourcesDragAdapter(this.treeViewer));

        // drop support
        PluginDropAdapter adapter = new DataSourcesDropAdapter(this.treeViewer, this);
        adapter.setFeedbackEnabled(true);

        this.treeViewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers, adapter);
    }
}
