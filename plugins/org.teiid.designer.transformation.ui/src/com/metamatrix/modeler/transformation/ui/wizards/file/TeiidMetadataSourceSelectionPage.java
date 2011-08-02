/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.wizards.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.teiid.designer.datatools.ui.actions.EditConnectionProfileAction;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;

import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.ui.internal.InternalUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.util.WizardUtil;
import com.metamatrix.ui.internal.viewsupport.FileSystemLabelProvider;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * Dialog for users to enter, edit and manage the Teiid Metadata file source information.
 */
public class TeiidMetadataSourceSelectionPage extends AbstractWizardPage
	implements IChangeNotifier, UiConstants, InternalUiConstants.Widgets, CoreStringUtil.Constants {

	// ===========================================================================================================================
	// Constants
	
	private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(TeiidMetadataSourceSelectionPage.class);
	
	private static final String TITLE = getString("title"); //$NON-NLS-1$
	private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
	
	private static final int PROFILE_COLUMN_COUNT = 3;
	
	private static final String SOURCE_LABEL = getString("sourceLabel"); //$NON-NLS-1$
	private static final String NEW_BUTTON = "New..."; //Util.getString("Widgets.newLabel"); //$NON-NLS-1$
	private static final String EDIT_BUTTON = "Edit..."; //Util.getString("Widgets.editLabel"); //$NON-NLS-1$
	
	private static final String INVALID_PAGE_MESSAGE = getString("invalidPageMessage"); //$NON-NLS-1$
	private static final String HOME = "HOME"; //$NON-NLS-1$
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	
	private static final int COLUMN_FILE_NAME = 0;
	private static final int COLUMN_VIEW_TABLE_NAME = 1;
	private static final int COLUMN_STATUS = 2;
	
	private static final String ODA_FLAT_FILE_ID = "org.eclipse.datatools.connectivity.oda.flatfile"; //$NON-NLS-1$
	
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }
	
	private ILabelProvider srcLabelProvider;
	private Combo srcCombo;
	private Label dataFileFolder;
	private Button editCPButton, configureButton;
	private CheckboxTableViewer fileViewer;
	
	private Action configureDataFileAction;
	private Action editViewTableNameAction;
	
	private ProfileManager profileManager = ProfileManager.getInstance();
	private Collection<IConnectionProfile> connectionProfiles;
    
    private TeiidMetadataImportInfo info;

	/**
	 * @since 4.0
	 */
	public TeiidMetadataSourceSelectionPage(TeiidMetadataImportInfo info) {
		this(null, info);
	}

	/**
	 * @since 4.0
	 */
	public TeiidMetadataSourceSelectionPage(Object selection, TeiidMetadataImportInfo info) {
		super(TeiidMetadataSourceSelectionPage.class.getSimpleName(), TITLE);
		// Set page incomplete initially
		this.info = info;
		setPageComplete(false);
		setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.IMPORT_TEIID_METADATA));
	}

	@Override
	public void createControl(Composite parent) {
        // Create page
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        
        mainPanel.setLayout(new GridLayout());
        mainPanel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
        mainPanel.setSize(mainPanel.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        setControl(mainPanel);
        // Add widgets to page
        
        // ---------------------------------------------------------------------------
        // ----------- Connection Profile SOURCE Panel ---------------------------------
        // ---------------------------------------------------------------------------
        Group profileGroup = WidgetFactory.createGroup(mainPanel, SOURCE_LABEL, SWT.NONE, 2);
        profileGroup.setLayout(new GridLayout(PROFILE_COLUMN_COUNT, false));
        profileGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
 
        refreshConnectionProfiles();

        this.srcLabelProvider = new LabelProvider() {

            @Override
            public String getText( final Object source ) {
                return ((IConnectionProfile)source).getName();
            }
        };
        this.srcCombo = WidgetFactory.createCombo(profileGroup,
                                                  SWT.READ_ONLY,
                                                  GridData.FILL_HORIZONTAL,
                                                  (ArrayList)this.connectionProfiles,
                                                  null, //this.src,
                                                  this.srcLabelProvider,
                                                  true);
        this.srcCombo.addModifyListener(new ModifyListener() {

            public void modifyText( final ModifyEvent event ) {
                sourceModified();
                fileViewer.refresh();
            }
        });
        this.srcCombo.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				sourceModified();
                fileViewer.refresh();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

        this.srcCombo.setVisibleItemCount(10);

        WidgetFactory.createButton(profileGroup, NEW_BUTTON).addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                createNewConnectionProfile();
            }
        });
        
        editCPButton = WidgetFactory.createButton(profileGroup, EDIT_BUTTON);
        editCPButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                editConnectionProfile();
            }
        });
        
        
        createFolderContentsListGroup(mainPanel);
        
        setMessage(INITIAL_MESSAGE);
	}
	
    /**
     * Method to create List box control group for displaying current zip file project list.
     
     * 
     * @param parent
     * @since 4.2
     */
    private void createFolderContentsListGroup( Composite parent ) {
    	Group folderContentsGroup = WidgetFactory.createGroup(parent, getString("folderContentsGroup"), SWT.FILL, 3, 2); //$NON-NLS-1$
    	GridData gd = new GridData(GridData.FILL_BOTH);
    	gd.heightHint = 250;
    	folderContentsGroup.setLayoutData(gd);
        
        dataFileFolder = new Label(folderContentsGroup, SWT.NONE);
        dataFileFolder.setText(getString("dataFileFolderText")); //$NON-NLS-1$
        GridData gdFF = new GridData();
        gdFF.horizontalSpan = 2;
        dataFileFolder.setLayoutData(gdFF); //new GridData(GridData.FILL_HORIZONTAL));
        
        createFileTableViewer(folderContentsGroup);
        
        configureButton = WidgetFactory.createButton(folderContentsGroup, SWT.PUSH);
        configureButton.setText(getString("configureButton")); //$NON-NLS-1$
        configureButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent event ) {
                configureDataFile();
            }
        });
        configureButton.setEnabled(false);
        configureButton.setToolTipText(getString("configureButtonTooltip")); //$NON-NLS-1$
    }
    
    private void createFileTableViewer(Composite parent) {
    	Table table = new Table(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK );
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.fileViewer = new CheckboxTableViewer(table);
        GridData gd = new GridData(GridData.FILL_BOTH);
     	gd.heightHint = 180;
     	gd.horizontalSpan = 2;
     	this.fileViewer.getControl().setLayoutData(gd);
     	this.fileViewer.setContentProvider(new DataFolderContentProvider());
     	this.fileViewer.setLabelProvider(new FileSystemLabelProvider());
     	this.fileViewer.addSelectionChangedListener( new ISelectionChangedListener() {
 			
 		@Override
 		public void selectionChanged(SelectionChangedEvent event) {
 				ISelection selection = event.getSelection();
 				if( selection != null ) {
 					configureButton.setEnabled(true);
 				} else {
 					configureButton.setEnabled(false);
 				}
 				
 			}
 		});
         
        this.fileViewer.addCheckStateListener(new ICheckStateListener() {
 			
 			@Override
 			public void checkStateChanged(CheckStateChangedEvent event) {
 				Object element = event.getElement();
 				if( element instanceof File ) {
 					info.setDoProcess((File)element, event.getChecked());
 				}
 				validatePage();
 			}
 		});
        
     // create columns
        TableViewerColumn column = new TableViewerColumn(this.fileViewer, SWT.LEFT);
        column.getColumn().setText(getString("dataFileNameColumn")); //$NON-NLS-1$
        column.setLabelProvider(new DataFileContentColumnLabelProvider(COLUMN_FILE_NAME));
        column.getColumn().pack();

        column = new TableViewerColumn(this.fileViewer, SWT.LEFT);
        column.getColumn().setText(getString("viewFileNameColumn")); //$NON-NLS-1$
        column.setLabelProvider(new DataFileContentColumnLabelProvider(COLUMN_VIEW_TABLE_NAME));
//        column.setEditingSupport(new DataFileContentColumnLabelProvider(this.propertiesViewer, this.vdb.getFile()));
        column.getColumn().pack();
        
        column = new TableViewerColumn(this.fileViewer, SWT.LEFT);
        column.getColumn().setText(getString("statusColumn")); //$NON-NLS-1$
        column.setLabelProvider(new DataFileContentColumnLabelProvider(COLUMN_STATUS));
//        column.setEditingSupport(new DataFileContentColumnLabelProvider(this.propertiesViewer, this.vdb.getFile()));
        column.getColumn().pack();
        
        // Add a Context Menu
        final MenuManager dataRolesMenuManager = new MenuManager();
        this.fileViewer.getControl().setMenu(dataRolesMenuManager.createContextMenu(parent));
        this.fileViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( final SelectionChangedEvent event ) {
                dataRolesMenuManager.removeAll();
                IStructuredSelection sel = (IStructuredSelection)fileViewer.getSelection();
                if (sel.size() == 1) {
                    configureButton.setEnabled(true);
                    dataRolesMenuManager.add(configureDataFileAction);
                    dataRolesMenuManager.add(editViewTableNameAction);
                }

            }
        });
        
        this.configureDataFileAction = new Action("Configure") { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                configureDataFile();
            }
        };

        this.configureDataFileAction.setEnabled(true);
        this.editViewTableNameAction = new Action(getString("editViewTableNameAction")) { //$NON-NLS-1$
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                editViewTableName();
            }
        };

        this.editViewTableNameAction.setEnabled(true);
    }

	@Override
	public void addChangeListener(IChangeListener theListener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void removeChangeListener(IChangeListener theListener) {
		// TODO Auto-generated method stub
	}
	
    void sourceModified() {
        if( this.srcCombo.getSelectionIndex() > -1 ) {
        	String cpName = this.srcCombo.getItem(this.srcCombo.getSelectionIndex());
        	for( IConnectionProfile profile : this.connectionProfiles ) {
        		if( profile.getName().equalsIgnoreCase(cpName) ) {
        			setConnectionProfile(profile);
        			Properties props = profile.getBaseProperties();
        			String home = (String)props.get(HOME);
        			if( home != null ) {
        				this.dataFileFolder.setText(Util.getString(I18N_PREFIX + "sourceLocation", home)); //$NON-NLS-1$
        				this.dataFileFolder.pack(true);
        			}
        			clearFileListViewer();
        			loadFileListViewer();
        			break;
        		}
        	}
        } else {
        	setConnectionProfile(null);
        }
        
        validatePage();
        
        this.editCPButton.setEnabled(getConnectionProfile() != null);
    }
    
    private void setConnectionProfile(IConnectionProfile profile) {
    	this.info.setConnectionProfile(profile);
    }
    
    private IConnectionProfile getConnectionProfile() {
    	return this.info.getConnectionProfile();
    }
    
    private void clearFileListViewer() {
//        org.eclipse.swt.widgets.List contents = fileViewer.getTree().getItems();
        fileViewer.remove(fileViewer.getTable().getItems());
    }

    private void loadFileListViewer( ) {
    	if( getConnectionProfile() != null ) {
	        File folder = getFolderForConnectionProfile();
	        if( folder != null && folder.exists() && folder.isDirectory() ) {
	        	fileViewer.setInput(folder);
	        	TableItem[] items = fileViewer.getTable().getItems();
	        	for( TableItem item : items) {
	        		Object data = item.getData();
	        		if( data != null && data instanceof File ) {
	        			TeiidMetadataFileInfo fileInfo = new TeiidMetadataFileInfo((File)data);
	        			this.info.addFileInfo(fileInfo);
	        			this.info.validate();
	        		}
	        	}
	        	for( TableColumn column : this.fileViewer.getTable().getColumns() ) {
	        		column.pack();
	        		column.setWidth(column.getWidth() + 4);
	        	}
	        }
    	}
    }
    
    private File getFolderForConnectionProfile() {
    	if( getConnectionProfile() != null ) {
    		Properties props = getConnectionProfile().getBaseProperties();
			String home = (String)props.get(HOME);
			if( home != null ) {
				return new File(home);
			}
    	}
    	
    	return null;
    }
    
    void profileChanged() {
    	sourceModified();
    }
    
    void createNewConnectionProfile() {
    	INewWizard wiz = (INewWizard)new NewTeiidFilteredCPWizard(ODA_FLAT_FILE_ID);

		WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), (Wizard)wiz);
		wizardDialog.setBlockOnOpen(true);

		CPListener listener = new CPListener();
		ProfileManager.getInstance().addProfileListener(listener);
		if( wizardDialog.open() == Window.OK ) {

			refreshConnectionProfiles();
			
			resetCPComboItems();
			setConnectionProfile(listener.getChangedProfile());
			
			selectProfile(listener.getChangedProfile());

		}
		ProfileManager.getInstance().removeProfileListener(listener);
    }
    
    void selectProfile(IConnectionProfile profile) {
    	int index = 0;
    	for( String item : this.srcCombo.getItems()) {
    		if( item != null && item.equalsIgnoreCase(profile.getName())) {
    			this.srcCombo.select(index);
    			sourceModified();
    			break;
    		}
    		index++;
    	}
    }
    
    void resetCPComboItems() {
    	if( this.srcCombo != null ) {
            WidgetUtil.setComboItems(this.srcCombo, (ArrayList)this.connectionProfiles, this.srcLabelProvider, true);
    	}
    }
	
    void editConnectionProfile() {
    	if( getConnectionProfile() != null) {
    		IConnectionProfile currentProfile = getConnectionProfile();
    		EditConnectionProfileAction action = new EditConnectionProfileAction(getShell(), currentProfile);
    		
    		CPListener listener = new CPListener();
            ProfileManager.getInstance().addProfileListener(listener);
            
    		action.run();
    		
    		// Update the Combo Box
    		if( action.wasFinished() )   {
            	setConnectionProfile(listener.getChangedProfile());
                this.refreshConnectionProfiles();
                WidgetUtil.setComboItems(this.srcCombo, (ArrayList)this.connectionProfiles, this.srcLabelProvider, true);

                WidgetUtil.setComboText(this.srcCombo, getConnectionProfile(), this.srcLabelProvider);
                
                selectConnectionProfile(currentProfile.getName());

            	ProfileManager.getInstance().removeProfileListener(listener);
	    		
	    		sourceModified();
    		} else {
    			// Remove the listener if the dialog is canceled
    			ProfileManager.getInstance().removeProfileListener(listener);
    		}
    	}
    }
    
    void configureDataFile() {
    	// Get selection
    	IStructuredSelection selectedFile = (IStructuredSelection)this.fileViewer.getSelection();
    	if( selectedFile.getFirstElement() != null && selectedFile.getFirstElement() instanceof File ) {
    		TeiidMetadataFileInfo fileInfo = this.info.getFileInfo((File)selectedFile.getFirstElement());
    		
    		// Make a copy of the fileInfo;
    		
    		TeiidMetadataFileInfo tempFileInfo = new TeiidMetadataFileInfo(fileInfo);
    		TeiidDataFileAnalyzerDialog dialog = new TeiidDataFileAnalyzerDialog(this.getShell(), tempFileInfo);
    		
    		dialog.open();
    		
    		if( dialog.getReturnCode() == Dialog.OK ) {
    			if( dialog.infoChanged() ) {
    				fileInfo.inject(tempFileInfo);
    			}
    			info.validate();
    			this.fileViewer.refresh();
    			validatePage();
    		}
    		
    		
    	}
    }
	
    void editViewTableName() {
    	IStructuredSelection selectedFile = (IStructuredSelection)this.fileViewer.getSelection();
    	if( selectedFile.getFirstElement() != null && selectedFile.getFirstElement() instanceof File ) {
    		TeiidMetadataFileInfo fileInfo = this.info.getFileInfo((File)selectedFile.getFirstElement());
    		
    		EditViewTableNameDialog dialog = new EditViewTableNameDialog(this.getShell(), fileInfo);
    		
    		dialog.open();
    		
    		if( dialog.getReturnCode() == Dialog.OK ) {
    			this.fileViewer.refresh();
    			validatePage();
    		}
    	}
    }
    
    void selectConnectionProfile(String name) {
    	if( name == null ) {
    		return;
    	}
    	
    	int cpIndex = -1;
    	int i = 0;
    	for( String item : srcCombo.getItems()) { 
    		if( item != null && item.length() > 0 ) {
    			if( item.toUpperCase().equalsIgnoreCase(name.toUpperCase())) {
    				cpIndex = i;
    				break;
    			}
    		}
    		i++;
    	}
    	if( cpIndex > -1 ) {
    		srcCombo.select(cpIndex);
    	}
    }
	
	private void refreshConnectionProfiles() {
        this.connectionProfiles = new ArrayList<IConnectionProfile>();
        
        final IConnectionProfile[] tempProfiles = profileManager.getProfilesByCategory(ODA_FLAT_FILE_ID);
        for (final IConnectionProfile profile : tempProfiles) {
            connectionProfiles.add(profile);
        }
	}
	
    private boolean validatePage() {
        // Check for at least ONE open non-hidden Model Project
        boolean validProj = false;
        for (IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            try {
                boolean result = proj.isOpen() && !proj.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)
                                 && proj.hasNature(ModelerCore.NATURE_ID);
                if (result) {
                    validProj = true;
                    break;
                }
            } catch (CoreException e) {
                UiConstants.Util.log(e);
            }
        }

        if (!validProj) {
            WizardUtil.setPageComplete(this, getString("noOpenProjectsMessage"), ERROR); //$NON-NLS-1$
        } else if (this.srcCombo.getText().length() == 0) {
            WizardUtil.setPageComplete(this, INVALID_PAGE_MESSAGE, ERROR);
        } else if (info.getStatus().getSeverity() > IStatus.WARNING) {
        	WizardUtil.setPageComplete(this, info.getStatus().getMessage(), ERROR);
        } else {
            WizardUtil.setPageComplete(this);
        }
        fireStateChanged();

        return validProj;
    }
	
    void fireStateChanged() {
//      Object[] listeners = this.notifier.getListeners();
//
//      for (Object listener : listeners) {
//          ((IChangeListener)listener).stateChanged(this);
//      }
  }

  public class CPListener implements IProfileListener {

      IConnectionProfile latestProfile;

      @Override
      public void profileAdded( IConnectionProfile profile ) {
          latestProfile = profile;
          fireStateChanged();
      }

      @Override
      public void profileChanged( IConnectionProfile profile ) {
      	latestProfile = profile;
      }

      @Override
      public void profileDeleted( IConnectionProfile profile ) {
          // nothing
      }

      public IConnectionProfile getChangedProfile() {
          return latestProfile;
      }
  }
  
  class DataFolderContentProvider implements ITreeContentProvider {

	    ///////////////////////////////////////////////////////////////////////////////////////////////
	    // CONSTANTS
	    ///////////////////////////////////////////////////////////////////////////////////////////////

	    private Object[] NO_CHILDREN = new Object[0];
	    
	    ///////////////////////////////////////////////////////////////////////////////////////////////
	    // METHODS
	    ///////////////////////////////////////////////////////////////////////////////////////////////

	    /** 
	     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	     * @since 4.2
	     */
	    public void dispose() {
	    }

	    /** 
	     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	     * @since 4.2
	     */
	    public Object[] getChildren(Object theParent) {
	        Object[] result = null;
	        
	        if (theParent instanceof File) {
	           result = ((File)theParent).listFiles();
	        }
	        
	        return ((result == null) ? NO_CHILDREN : result);
	    }

	    /** 
	     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	     * @since 4.2
	     */
	    public Object[] getElements(Object theInput) {
	    	if( theInput instanceof File ) {
	    		return ((File)theInput).listFiles();
	    	}
	    	
	        return File.listRoots();
	    }

	    /** 
	     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	     * @since 4.2
	     */
	    public Object getParent(Object theElement) {
	        return ((theElement instanceof File) ? ((File)theElement).getParentFile() : null);
	    }

	    /** 
	     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	     * @since 4.2
	     */
	    public boolean hasChildren(Object theElement) {
	        Object[] kids = getChildren(theElement);
	        return ((kids != null) && (kids.length > 0));
	    }

	    /** 
	     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	     * @since 4.2
	     */
	    public void inputChanged(Viewer theViewer,
	                             Object theOldInput,
	                             Object theNewInput) {
	    }
	}
  
	class DataFileContentColumnLabelProvider extends ColumnLabelProvider {

		private final int columnNumber;

		public DataFileContentColumnLabelProvider(int columnNumber) {
			this.columnNumber = columnNumber;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element) {
			// Element should be a "File"
			switch (this.columnNumber) {
				case COLUMN_FILE_NAME: {
					return ((File) element).getName();
				}
				case COLUMN_VIEW_TABLE_NAME: {
					TeiidMetadataFileInfo fileInfo = info.getFileInfo((File) element);
					if (fileInfo != null && fileInfo.getViewTableName() != null) {
						return fileInfo.getViewTableName();
					}
					return EMPTY_STRING;
				}
				case COLUMN_STATUS: {
					TeiidMetadataFileInfo fileInfo = info.getFileInfo((File) element);
					if (fileInfo != null && fileInfo.getViewTableName() != null) {
						return fileInfo.getStatus().getMessage();
					}
				}
			}
			return EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
		 */
		@Override
		public String getToolTipText(Object element) {
			switch (this.columnNumber) {
			case COLUMN_FILE_NAME: {
				return getString("fileNameColumnTooltip"); //$NON-NLS-1$
			}
			case COLUMN_VIEW_TABLE_NAME: {
				return getString("viewNameColumnTooltip"); //$NON-NLS-1$
			}
			case COLUMN_STATUS: {
				return getString("statusColumnTooltip"); //$NON-NLS-1$
			}
		}
		return EMPTY_STRING;
		}

		@Override
		public Image getImage(Object element) {
			if( this.columnNumber == COLUMN_STATUS ) {
				TeiidMetadataFileInfo fileInfo = info.getFileInfo((File) element);
				if (fileInfo != null) {
					if( fileInfo.getStatus().getSeverity() == IStatus.OK){
						return null;
					} else if(fileInfo.getStatus().getSeverity() == IStatus.WARNING ) {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.WARNING_ICON);
					} else if(fileInfo.getStatus().getSeverity() == IStatus.ERROR ) {
						return UiPlugin.getDefault().getImage(PluginConstants.Images.ERROR_ICON);
					}
					return null;
				}
			}
			return null;
		}
		
		
	}
	
	class EditViewTableNameDialog extends TitleAreaDialog {
		private final String TITLE = getString("EditViewTableNameDialog.title"); //$NON-NLS-1$

	    //=============================================================
	    // Instance variables
	    //=============================================================
	    private TeiidMetadataFileInfo fileInfo;
	    private Text tableNameText;
	    
	    private StringNameValidator validator;
	        
	    //=============================================================
	    // Constructors
	    //=============================================================
	    /**
	     * AliasEntryDialog constructor.
	     * 
	     * @param parent   parent of this dialog
	     * @param transObj the transformation EObject
	     * @param title    dialog display title
	     */
	    public EditViewTableNameDialog(Shell parent, TeiidMetadataFileInfo fileInfo) {
	        super(parent);
	        this.fileInfo = fileInfo;
	        this.validator = new RelationalStringNameValidator(true, true);
	    }
	    
	    @Override
	    protected void configureShell( Shell shell ) {
	        super.configureShell(shell);
	        shell.setText(TITLE);
	    }
	    
	    /* (non-Javadoc)
	    * @see org.eclipse.jface.window.Window#setShellStyle(int)
	    */
	    @Override
	    protected void setShellStyle( int newShellStyle ) {
	        super.setShellStyle(newShellStyle | SWT.RESIZE | SWT.MAX);

	    }
	        
	    //=============================================================
	    // Instance methods
	    //=============================================================

	    @Override
	    protected Control createDialogArea(Composite parent) {
	        Composite composite = (Composite)super.createDialogArea(parent);
	        //------------------------------        
	        // Set layout for the Composite
	        //------------------------------        
	        GridLayout gridLayout = new GridLayout();
	        composite.setLayout(gridLayout);
	        gridLayout.numColumns = 1;
	        GridData gridData = new GridData(GridData.FILL_BOTH);
	        gridData.grabExcessHorizontalSpace = true;
//	        gridData.grabExcessVerticalSpace = true;
	        gridData.widthHint = 500;
	        composite.setLayoutData(gridData);
	        
	        Group nameGroup = WidgetFactory.createGroup(composite, getString("EditViewTableNameDialog.viewTableName"), SWT.NONE, 1, 1 ); //$NON-NLS-1$
	    	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//	    	gd.heightHint = 140;
	    	nameGroup.setLayoutData(gd);
	        
	        tableNameText = WidgetFactory.createTextField(nameGroup);
//	        tableNameText.setTextLimit(50);
	        tableNameText.setText(this.fileInfo.getViewTableName());
	        tableNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	        
	        tableNameText.addModifyListener( new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					String error = validator.checkValidName(tableNameText.getText());
					if( error == null || error.length() == 0 ) {
						setMessage(getString("EditViewTableNameDialog.clickOK")); //$NON-NLS-1$
						setErrorMessage(null);
						setOkEnabled(true);
						return;
					}
					
					setErrorMessage(Util.getString("EditViewTableNameDialog.errorMessage" , error)); //$NON-NLS-1$
					setOkEnabled(false);
				}
			});
	        
	        setMessage(getString("EditViewTableNameDialog.initialMessage")); //$NON-NLS-1$
	        
	        return composite;
	    }
	    
	    @Override
	    public void create() {
	        super.create();
	        setOkEnabled(false);
	    }
	    @Override
	    protected void okPressed() {
	        fileInfo.setViewTableName(tableNameText.getText());
	        super.okPressed();
	    }
	    
	    public void setOkEnabled(boolean enabled) {
	        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	    }
	}
}
