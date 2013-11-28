/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb.ui.wizards;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.validation.rules.StringNameValidator;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.InternalUiConstants;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.text.StyledTextEditor;
import org.teiid.designer.ui.common.util.WidgetFactory;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.util.WizardUtil;
import org.teiid.designer.ui.common.viewsupport.ListContentProvider;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;
import org.teiid.designer.ui.viewsupport.IPropertiesContext;
import org.teiid.designer.ui.viewsupport.ModelLabelProvider;
import org.teiid.designer.ui.viewsupport.ModelProjectSelectionStatusValidator;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;
import org.teiid.designer.ui.viewsupport.SingleProjectFilter;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.ui.VdbUiConstants;
import org.teiid.designer.vdb.ui.editor.VdbEditor;


/**
 * @since 8.0
 */
public final class NewVdbWizard extends AbstractWizard
    implements IPropertiesContext, INewWizard, InternalUiConstants.Widgets, CoreStringUtil.Constants, UiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(NewVdbWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String PAGE_TITLE = getString("pageTitle"); //$NON-NLS-1$
    private static final String VDB_NAME_ERROR = getString("vdbNameError"); //$NON-NLS-1$

    private static final int COLUMN_COUNT = 3;

    private static final String NAME_LABEL = getString("nameLabel"); //$NON-NLS-1$
    private static final String FOLDER_LABEL = getString("folderLabel"); //$NON-NLS-1$

    private static final String INITIAL_MESSAGE = getString("initialMessage"); //$NON-NLS-1$
    private static final String CREATE_FILE_ERROR_MESSAGE = getString("createFileErrorMessage"); //$NON-NLS-1$

    private static final String NOT_MODEL_PROJECT_MSG = getString("notModelProjectMessage"); //$NON-NLS-1$
    private static final String SELECT_FOLDER_MESSAGE = getString("selectFolderMessage"); //$NON-NLS-1$
    
    static final String ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE = VdbUiConstants.Util.getString("addFileDialogInvalidSelectionMessage"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_NON_MODEL_SELECTED_MESSAGE = VdbUiConstants.Util.getString("addFileDialogNonModelSelectedMessage"); //$NON-NLS-1$
    static final String ADD_FILE_DIALOG_VDB_SOURCE_MODEL_SELECTED_MESSAGE = VdbUiConstants.Util.getString("addFileDialogVdbSourceModelSelectedMessage");  //$NON-NLS-1$
    
    private static final StringNameValidator nameValidator = new StringNameValidator(StringNameValidator.DEFAULT_MINIMUM_LENGTH,
                                                                                     StringNameValidator.DEFAULT_MAXIMUM_LENGTH);

    static String getString( final String id ) {
        return VdbUiConstants.Util.getString(I18N_PREFIX + id);
    }
    
    String name;
    IContainer folder;

    private WizardPage mainPage;
    Text nameText, folderText;
    TableViewer modelsViewer;
    StyledTextEditor descriptionTextEditor;
    Button btnBrowse;
    Button addModelsButton;

	Button removeModelsButton;
    private ISelectionStatusValidator projectValidator = new ModelProjectSelectionStatusValidator();
    final ModelLabelProvider modelLabelProvider = new ModelLabelProvider();

    IStructuredSelection initialSelection;
    
    List<IResource> modelsForVdb;
    
    Properties designerProperties;
    private boolean openProjectExists = true;
    private IProject newProject;

    
    final ISelectionStatusValidator validator = new ISelectionStatusValidator() {
        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.dialogs.ISelectionStatusValidator#validate(java.lang.Object[])
         */
        @Override
        public IStatus validate( final Object[] selection ) {
            for (int ndx = selection.length; --ndx >= 0;) {
            	Object obj = selection[ndx];
                if (obj instanceof IContainer) {
                	return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0, ADD_FILE_DIALOG_INVALID_SELECTION_MESSAGE, null);
                } else if( obj instanceof IFile ) {
                	IFile file = (IFile)obj;
                
                	if ( !ModelUtilities.isModelFile(file) && !ModelUtil.isXsdFile(file) ) {
                		return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0, ADD_FILE_DIALOG_NON_MODEL_SELECTED_MESSAGE, null); 
                	}
                	if( ModelUtilities.isVdbSourceModel(file) ) {
                		return new Status(IStatus.ERROR, VdbUiConstants.PLUGIN_ID, 0, ADD_FILE_DIALOG_VDB_SOURCE_MODEL_SELECTED_MESSAGE, null);
                	}
                }
            }
            
            return new Status(IStatus.OK, VdbUiConstants.PLUGIN_ID, 0, EMPTY_STRING, null);
        }
    };

    /**
     * @since 4.0
     */
    public NewVdbWizard() {
        super(UiPlugin.getDefault(), TITLE, null);
        this.modelsForVdb = new ArrayList<IResource>();
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        // append VDB file extension if needed
        if (!name.endsWith(ModelerCore.VDB_FILE_EXTENSION)) {
            name += ModelerCore.VDB_FILE_EXTENSION;
        }
        
        if( designerProperties != null ) {
            DesignerPropertiesUtil.setVdbName(designerProperties, name);
        }

        // create VDB resource
        final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    final IFile vdbFile = NewVdbWizard.this.folder.getFile(new Path(NewVdbWizard.this.name));
                    vdbFile.create(new ByteArrayInputStream(new byte[0]), false, monitor);
                    Vdb newVdb = new Vdb(vdbFile, false, monitor);
            		String desc = descriptionTextEditor.getText();
            		if( desc != null && desc.length() > 0 ) {
            			newVdb.setDescription(desc);
            		}
                    newVdb.save(monitor);
                    NewVdbWizard.this.folder.refreshLocal(IResource.DEPTH_INFINITE, monitor);


                    // open editor
                    IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
                    IDE.openEditor(page, vdbFile);
                    
                    //Thread.sleep(200);

                    if (modelsForVdb != null && !modelsForVdb.isEmpty()) {
                		VdbEditor editor = getVdbEditor(vdbFile);
                		
                		if( editor != null ) {
                    		List<IFile> models = new ArrayList<IFile>();
                    		
                    		for( IResource nextModel : modelsForVdb) {
                    			models.add((IFile)nextModel);
                    		}
                    		
                    		editor.addModels(models);
                    		editor.doSave(new NullProgressMonitor());
                		}
                    }
                    
                } catch (final Exception err) {
                    throw new InvocationTargetException(err);
                } finally {
                    monitor.done();
                }
            }
        };
        try {
            new ProgressMonitorDialog(getShell()).run(false, true, op);
            return true;
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                err = ((InvocationTargetException)err).getTargetException();
            }
            VdbUiConstants.Util.log(err);
            WidgetUtil.showError(CREATE_FILE_ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 4.0
     */
    @Override
	public void init( final IWorkbench workbench,
                      final IStructuredSelection originalSelection ) {

    	IStructuredSelection selection = originalSelection;
    	openProjectExists = ModelerUiViewUtils.workspaceHasOpenModelProjects();
        if( !openProjectExists ) {
        	newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		selection = new StructuredSelection(newProject);
        		openProjectExists = true;
        	} else {
        		openProjectExists = false;
        	}
        }
        
        if (isAllModelsSelected(selection)) {
            initialSelection = new StructuredSelection(selection.toArray());
        }
        if (selection != null && !selection.isEmpty()) {
            this.folder = ModelUtil.getContainer(selection.getFirstElement());
        }

        if (folder != null && !folderInModelProject()) {
            // Create empty page
            this.mainPage = new WizardPage(NewVdbWizard.class.getSimpleName(), PAGE_TITLE, null) {
                @Override
				public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            this.mainPage.setMessage(NOT_MODEL_PROJECT_MSG, IMessageProvider.ERROR);
        } else {

            // Create and add page
            this.mainPage = new WizardPage(NewVdbWizard.class.getSimpleName(), PAGE_TITLE, null) {
                @Override
				public void createControl( final Composite parent ) {
                    setControl(createPageControl(parent));
                }
            };
            this.mainPage.setMessage(INITIAL_MESSAGE);

            // If current selection not null, set folder to selection if a folder, or to containing folder if not
            if (this.folder != null) {
                if (!projectValidator.validate(new Object[] {this.folder}).isOK()) {
                    this.folder = null;
                }
            } else { // folder == null
                this.mainPage.setMessage(SELECT_FOLDER_MESSAGE, IMessageProvider.ERROR);
            }
        }

        this.mainPage.setPageComplete(false);
        addPage(mainPage);
    }

    private boolean folderInModelProject() {
        boolean result = false;

        if (this.folder != null) {
            IProject project = this.folder.getProject();
            try {
                if (project != null && project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) != null) {
                    result = true;
                }
            } catch (CoreException ex) {
                VdbUiConstants.Util.log(ex);
            }
        }

        return result;
    }

    /**
     * Indicates if all selected objects are {@link IResource}s.
     * 
     * @param theSelection the selection being checked
     * @return <code>true</code> if all selected objects are <code>EObject</code>; <code>false</code> otherwise.
     */
    boolean isAllModelsSelected( ISelection theSelection ) {
    	if( (theSelection == null) ||  theSelection.isEmpty()) {
    		return true;
    	}
        boolean result = (theSelection instanceof IStructuredSelection) && SelectionUtilities.isAllIResourceObjects(theSelection);

        if (result) {
            @SuppressWarnings("rawtypes")
			List selectedObjects = SelectionUtilities.getSelectedObjects(theSelection);
            for (@SuppressWarnings("rawtypes")
			Iterator iter = selectedObjects.iterator(); iter.hasNext();) {
                IResource res = (IResource)iter.next();
                if (!ModelUtilities.isModelFile(res)) {
                    result = false;
                    break;
                }
            }

        }

        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 4.0
     */
    @Override
    public boolean canFinish() {
        // defect 16154 -- Finish can be enabled even if errors on page.
        // check the page's isComplete status (in super) -- just follow its advice.
        return super.canFinish();
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }

    /**
     * @param parent 
     * @return composite the page
     * @since 4.0
     */
	@SuppressWarnings({ "unused", "unchecked" })
	Composite createPageControl( final Composite parent ) {
        // Create page
        final Composite mainPanel = new Composite(parent, SWT.NONE);
        mainPanel.setLayout(new GridLayout(COLUMN_COUNT, false));
        // Add widgets to page
        WidgetFactory.createLabel(mainPanel, FOLDER_LABEL);
        final String name = (this.folder == null ? null : this.folder.getFullPath().makeRelative().toString());
        this.folderText = WidgetFactory.createTextField(mainPanel, GridData.FILL_HORIZONTAL, 1, name, SWT.READ_ONLY);
        this.folderText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( final ModifyEvent event ) {
                folderModified();
            }
        });
        btnBrowse = WidgetFactory.createButton(mainPanel, BROWSE_BUTTON);
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                browseButtonSelected();
            }
        });
        WidgetFactory.createLabel(mainPanel, NAME_LABEL);
        this.nameText = WidgetFactory.createTextField(mainPanel, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.nameText.addModifyListener(new ModifyListener() {
            @Override
			public void modifyText( final ModifyEvent event ) {
                nameModified();
            }
        });

        // set focus to browse button if no folder selected. otherwise set focus to text field
        if (folder == null) {
            btnBrowse.setFocus();
        } else {
            nameText.setFocus();
        }
        
        DESCRIPTION_GROUP: {
            final Group descGroup = WidgetFactory.createGroup(mainPanel, getString("description"), GridData.FILL_HORIZONTAL, 3); //$NON-NLS-1$
            descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData descGridData = new GridData(GridData.FILL_BOTH);
            descGridData.horizontalSpan = 1;
            descGridData.heightHint = 50;
            descGridData.minimumHeight = 30;
            descGridData.grabExcessVerticalSpace = true;
            descriptionTextEditor.setLayoutData(descGridData);
            descriptionTextEditor.setText(""); //$NON-NLS-1$
        }
        
        MODELS_GROUP : {
	        Group group = WidgetFactory.createGroup(mainPanel, getString("selectedModelsGroupTitle"), GridData.FILL_BOTH, COLUMN_COUNT, 2); //$NON-NLS-1$
	    	GridData gd = new GridData(GridData.FILL_BOTH);
	    	gd.heightHint = 200;
	    	gd.horizontalSpan = 3;
	    	group.setLayoutData(gd);
	    	
	    	Composite leftToolbarPanel = new Composite(group, SWT.NONE);
	    	leftToolbarPanel.setLayout(new GridLayout());
		  	GridData ltpGD = new GridData(GridData.FILL_VERTICAL);
		  	ltpGD.heightHint=120;
		  	leftToolbarPanel.setLayoutData(ltpGD);
	    	
	    	addModelsButton = new Button(leftToolbarPanel, SWT.PUSH);
	    	addModelsButton.setText(getString("add")); //$NON-NLS-1$
	    	addModelsButton.setToolTipText(getString("addModelsTooltip")); //$NON-NLS-1$
	    	addModelsButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    	addModelsButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleAddModelsSelected();
					if( !modelsViewer.getSelection().isEmpty() ) {
						removeModelsButton.setEnabled(true);
					}
				}
	    		
			});
	    	
	    	removeModelsButton = new Button(leftToolbarPanel, SWT.PUSH);
	    	removeModelsButton.setText(getString("remove")); //$NON-NLS-1$
	    	removeModelsButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    	removeModelsButton.setEnabled(false);
	    	removeModelsButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					Collection<IResource> models = new ArrayList<IResource>();
					
					IStructuredSelection selection = (IStructuredSelection)modelsViewer.getSelection();
					for( Object obj : selection.toArray()) {
						if( obj instanceof IResource ) {
							models.add((IResource)obj);
						}
					}
					removeModels(models);
					removeModelsButton.setEnabled(false);
				}
	    		
			});
	    	
	    	
	        this.modelsViewer = new TableViewer(group);
	        GridData gdv = new GridData(GridData.FILL_BOTH);
	        //gdv.horizontalSpan = COLUMN_COUNT;
	        modelsViewer.getControl().setLayoutData(gdv);
	        modelsViewer.setContentProvider(new ListContentProvider());
	        modelsViewer.setLabelProvider(new ModelExplorerLabelProvider());
            // Add Models from properties if available
            if (this.designerProperties != null && !this.designerProperties.isEmpty()) {
                IFile sourceMdl = DesignerPropertiesUtil.getSourceModel(this.designerProperties);
                IFile viewMdl = DesignerPropertiesUtil.getViewModel(this.designerProperties);
                if (sourceMdl != null) this.modelsForVdb.add(sourceMdl);
                if (viewMdl != null) this.modelsForVdb.add(viewMdl);
            } else {
                this.modelsForVdb.addAll(SelectionUtilities.getSelectedIResourceObjects(initialSelection));
            }
	        modelsViewer.setInput(this.modelsForVdb);
	        modelsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					removeModelsButton.setEnabled(!event.getSelection().isEmpty());
				}
			});
        }
        
        updateForProperties();
        return mainPanel;
    }
	
	void handleAddModelsSelected() {
		final ViewerFilter filter = new ViewerFilter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public boolean select( final Viewer viewer,
                                   final Object parent,
                                   final Object element ) {
                if (element instanceof IContainer) 
                	return true;
                
                final IFile file = (IFile)element;
                
                if (ModelUtilities.isModelFile(file) || ModelUtil.isXsdFile(file)) { 
                	return true;
                }

                return false;
            }
        };
        ModelingResourceFilter wsFilter = new ModelingResourceFilter(filter);
        wsFilter.setShowHiddenProjects(true);
        final Object[] models = WidgetUtil.showWorkspaceObjectSelectionDialog(
        		getString("selectModelsTitle"), //$NON-NLS-1$
                getString("selectModelsMessage"), //$NON-NLS-1$
                true, null, wsFilter, validator, modelLabelProvider);
        
        addModels(models);
        
        
	}
	
	void addModels(Object[] models) {
		for( Object model : models) {
			if( !modelsForVdb.contains(model) ) {
				modelsForVdb.add((IResource)model);
			}
		}
		
		this.modelsViewer.refresh();
	}
	
	void removeModels(Collection<IResource> models) {
		for( IResource model : models) {
			modelsForVdb.remove(model);
		}
		this.modelsViewer.refresh();
	}

    /**
     * @since 4.0
     */
    void browseButtonSelected() {
    	ModelingResourceFilter resFilter = new ModelingResourceFilter();
    	resFilter.addFilter(new SingleProjectFilter(this.designerProperties));
        this.folder = WidgetUtil.showFolderSelectionDialog(this.folder, resFilter, projectValidator);

        if (folder != null) {
            this.folderText.setText(folder.getFullPath().makeRelative().toString());

            if (CoreStringUtil.isEmpty(nameText.getText())) {
                nameText.setFocus();
            }
        }

        validatePage();
    }

    /**
     * @since 4.0
     */
    void folderModified() {
        validatePage();
    }

    /**
     * @since 4.0
     */
    void nameModified() {
        validatePage();
    }

    /**
     * @since 4.0
     */
    private void validatePage() {
        final IContainer folder;
        try {
            folder = WizardUtil.validateFileAndFolder(this.nameText,
                                                      this.folderText,
                                                      this.mainPage,
                                                      ModelerCore.VDB_FILE_EXTENSION,
                                                      false);
            if (this.mainPage.getMessageType() == IMessageProvider.ERROR) {
                // WizardUtil.validateFileAndFolder can set error message and message type so no need to do further
                // validation if an error was already found (JBEDSP-588)
                return;
            }

            IStatus status = projectValidator.validate(new Object[] {folder});
            String proposedName = this.nameText.getText();

            if (!status.isOK()) {
                // only update the message if the vFolder is non-null;
                // if WizardUtil returned null, it already set the status
                // this corrects the case where the wrong message shows for
                // a bad filename.
                if (folder != null) {
                    this.mainPage.setErrorMessage(status.getMessage());
                    this.mainPage.setPageComplete(false);
                } // endif
            } else if (!nameValidator.isValidName(proposedName)) {
                this.mainPage.setErrorMessage(VDB_NAME_ERROR);
                this.mainPage.setPageComplete(false);
            } else if (ModelUtilities.vdbNameReservedValidation(proposedName) != null) {
                this.mainPage.setErrorMessage(ModelUtilities.vdbNameReservedValidation(proposedName));
                this.mainPage.setPageComplete(false);
            } else {
                this.mainPage.setErrorMessage(null);
                this.mainPage.setPageComplete(true);
            }

            if (this.mainPage.isPageComplete()) {
                this.name = proposedName;
                this.folder = folder;
            }
        } catch (final CoreException err) {
            VdbUiConstants.Util.log(err);
            WizardUtil.setPageComplete(this.mainPage, err.getLocalizedMessage(), IMessageProvider.ERROR);
        }
    }
    
	/**
	 * Finds the visible VDB Editor for the supplied VDB
	 * 
	 * If an editor is NOT open for this vdb, then null is returned.
	 * 
	 * @param vdb
	 * @return the VdbEditor
	 */
	public VdbEditor getVdbEditor(final IFile vdb) {
		final IWorkbenchWindow window = UiPlugin.getDefault()
				.getCurrentWorkbenchWindow();

		if (window != null) {
			final IWorkbenchPage page = window.getActivePage();

			if (page != null) {
				VdbEditor editor = findEditorPart(page, vdb);
				if( editor != null ) {
					return editor;
				}
			}
		}
		return null;
	}

	private VdbEditor findEditorPart(final IWorkbenchPage page, IFile vdbFile) {
		// look through the open editors and see if there is one available for
		// this model file.
		final IEditorReference[] editors = page.getEditorReferences();
		for (int i = 0; i < editors.length; ++i) {

			final IEditorPart editor = editors[i].getEditor(false);
			if (editor instanceof VdbEditor) {
				final VdbEditor vdbEditor = (VdbEditor) editor;
				final IPath editorVdbPath = vdbEditor.getVdb().getName();
				if (vdbFile.getFullPath().equals(editorVdbPath)) 
					return vdbEditor;

			}
		}

		return null;
	}

	/**
	 * 
	 */
	@Override
	public void setProperties(Properties properties) {
		this.designerProperties = properties;
	}
	
	private void updateForProperties() {
		if( this.designerProperties != null ) {
	    	if( this.folder == null ) {
	            // Get Project from Properties - if it exists.
	            IProject project = DesignerPropertiesUtil.getProject(this.designerProperties);
	            if (project != null) {
	                this.folder = project;
	                this.folderText.setText(this.folder.getFullPath().makeRelative().toString());
	                if (CoreStringUtil.isEmpty(nameText.getText())) {
	                    nameText.setFocus();
	                }
	            }
	    	}
	    	
	    	// Check the properties for view or source model names
	    	IFile viewModel = DesignerPropertiesUtil.getViewModel(this.designerProperties);
	    	if( viewModel != null ) {
	    		// Add to VDB
	    		addModels(new IFile[] {viewModel});
	    	}
	    	
	    	IFile sourceModel = DesignerPropertiesUtil.getSourceModel(this.designerProperties);
	    	if( sourceModel != null ) {
	    		// Add to VDB
	    		addModels(new IFile[] {sourceModel});
	    	}
		}
		
    	if( this.designerProperties != null && !this.openProjectExists) {
			DesignerPropertiesUtil.setProjectStatus(this.designerProperties, IPropertiesContext.NO_OPEN_PROJECT);
		}
	}

}
