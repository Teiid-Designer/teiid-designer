/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.ide.undo.CreateFolderOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.ValidationResult;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;
import com.metamatrix.modeler.internal.core.validation.ValidationProblemImpl;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * @since 4.0
 */
public class NewModelProjectWizard extends BasicNewProjectResourceWizard implements IPropertiesContext, UiConstants {
    private static String SOURCES = getString("sources"); //$NON-NLS-1$
    private static String VIEWS = getString("views"); //$NON-NLS-1$
    private static String SCHEMAS = getString("schemas"); //$NON-NLS-1$
    private static String FUNCTIONS = getString("functions"); //$NON-NLS-1$
    private static String EXTENSIONS =getString("extensions"); //$NON-NLS-1$
    private static String WEB_SERVICES =getString("web_services"); //$NON-NLS-1$
    
	public static final String THIS_CLASS = "NewModelProjectWizard"; //$NON-NLS-1$
	
    private static String getString( String key ) {
        return UiConstants.Util.getString(THIS_CLASS + '.' + key);
    }

    private static String getString( String key,
                                     String parameter ) {
        return UiConstants.Util.getString(THIS_CLASS + '.' + key, parameter);
    }
    
    private Properties designerProperties;
    
    private ProjectTemplateOptionsPage optionsPage;
    
	
    /** 
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     * @since 4.2 - added to address defect 15096
     */
    @Override
    public void addPages() {
        super.addPages();
        IWizardPage mainPage = super.getPage("basicNewProjectPage"); //$NON-NLS-1$
    	mainPage.setTitle(Util.getString("NewModelProjectWizard.title")); //$NON-NLS-1$
    	mainPage.setDescription(Util.getString("NewModelProjectWizard.description")); //$NON-NLS-1$
    	setWindowTitle(Util.getString("NewModelProjectWizard.title")); //$NON-NLS-1$

    	optionsPage = new ProjectTemplateOptionsPage();
		this.addPage(optionsPage);
    }
    //============================================================================================================================
	// Constants
    
	private static final String[] MODEL_NATURES = new String[] {ModelerCore.NATURE_ID}; 
    
    //============================================================================================================================
	// BasicNewProjectResourceWizard Methods
    
    /**
	 * @see org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard#performFinish()
	 * @since 4.0
	 */
	@Override
    public boolean performFinish() {
		// Check the project name validity before proceeding
        IWizardPage mainPage = super.getPage("basicNewProjectPage"); //$NON-NLS-1$
        if(mainPage instanceof WizardNewProjectCreationPage) {
        	String projName = ((WizardNewProjectCreationPage)mainPage).getProjectName();
            final ValidationResultImpl result = new ValidationResultImpl(projName);
            // Prevent usage of this list of chars in project names
            checkInvalidChars(result, projName, UiConstants.NamingAttributes.VALID_PROJECT_CHARS);
            if (result.hasProblems()) {
            	String eMsg = Util.getString("NewModelProjectWizard.namingError.msg") + '\n' + //$NON-NLS-1$
            	              result.getProblems()[0].getMessage();
                String eTitle = Util.getString("NewModelProjectWizard.namingError.title"); //$NON-NLS-1$
                MessageDialog.openWarning( getShell(), eTitle, eMsg );
            	return false;
            }
            // Prevent usage of the list of reserved project names
            checkReservedProjName(result, projName);
            if (result.hasProblems()) {
            	String eMsg = Util.getString("NewModelProjectWizard.namingError.msg") + '\n' + //$NON-NLS-1$
            	              result.getProblems()[0].getMessage();
                String eTitle = Util.getString("NewModelProjectWizard.namingError.title"); //$NON-NLS-1$
                MessageDialog.openWarning( getShell(), eTitle, eMsg );
            	return false;
            }
        }
        super.performFinish();
        final IProject project = getNewProject();
        if (project == null)
            return false;
        try {
            final IProjectDescription desc = project.getDescription();
            desc.setNatureIds(MODEL_NATURES);
            if (ProductCustomizerMgr.getInstance() != null) {
                String productName = ProductCustomizerMgr.getInstance().getProductName();
                if (!CoreStringUtil.isEmpty(productName)) {
                    desc.setComment(productName + ", version " + ModelerCore.ILicense.VERSION); //$NON-NLS-1$
                }
            }
            project.setDescription(desc, null);
            // Defect 11480 - closing and opening the project sets the overlay icon properly
            project.close(null);
            project.open(null);
            
            designerProperties.put(IPropertiesContext.KEY_PROJECT_NAME, project.getName());
            
            createFolders(project);
            
            /*
             *   Code added to ModelWorkspaceViewerFilter to filter out 
             *   non-Model projects will hide a brand-new Model Project because it does not have its
             *   nature at the time the tree is populated and the filter run.  Adding an extra 
             *   tree refresh at the end of the New Model Project process so we get a second chance to
             *   construct the tree AFTER the new project has its nature established.
             */
            refreshModelExplorerResourceNavigatorTree();

            return true;
        } catch (final CoreException err) {
            Util.log(IStatus.ERROR, err, err.getMessage());
            return false;
        }
	}
    
    private void createFolders(IProject project) {
    	if( !optionsPage.wasVisible() ) {
    		return;
    	}
    	
        if( optionsPage.createSources) {
    		createFolder(project, optionsPage.sourcesStr);
    	}
    	if( optionsPage.createViews ) {
    		createFolder(project, optionsPage.viewsStr);
    	}
    	if( optionsPage.createSchema ) {
    		createFolder(project, optionsPage.schemaStr);
    	}
    	if( optionsPage.createWebServices ) {
    		createFolder(project, optionsPage.webServicesStr);
    	}
    	if( optionsPage.createFunctions ) {
    		createFolder(project, optionsPage.functionsStr);
    	}
    	if( optionsPage.createExtensions ) {
    		createFolder(project, optionsPage.extensionsStr);
    	}
    }
    

    private void refreshModelExplorerResourceNavigatorTree() {
        // activate the Model Explorer view (must do this last)optionGroupLabel
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                try {
                    ModelExplorerResourceNavigator view = 
                        (ModelExplorerResourceNavigator) UiUtil.getWorkbenchPage().showView(Extensions.Explorer.VIEW);
                    view.getTreeViewer().refresh( true );
                } catch (PartInitException err) {
                    Util.log(IStatus.ERROR, err, err.getMessage());
                }
            }
        });

    }

    private void checkInvalidChars(final ValidationResult result,final String stringToValidate, final char[] validChars) {
        CoreArgCheck.isNotNull(stringToValidate);                
        CoreArgCheck.isNotNull(result);
        final StringNameValidator validator = new StringNameValidator(validChars);
        final String reasonInvalid = validator.checkValidName(stringToValidate);
        if ( reasonInvalid != null ) {
            // create validation problem and addit to the resuly
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reasonInvalid);
            result.addProblem(problem);
        }
    }
    
    private void checkReservedProjName(final ValidationResult result,final String name) {
        CoreArgCheck.isNotNull(name);                
        CoreArgCheck.isNotNull(result);
        if(ModelerCore.isReservedProjectName(name)) {
            String reservedProjMsg = Util.getString("NewModelProjectWizard.reservedProjNameError",name); //$NON-NLS-1$

            // create validation problem and addit to the resuly
            ValidationProblem problem  = new ValidationProblemImpl(0, IStatus.ERROR, reservedProjMsg);
            result.addProblem(problem);
        }
    }
    
    private void createFolder(IProject project, String name) {
		final IPath containerPath = project.getFullPath();
		IPath newFolderPath = containerPath.append(name);
		final IFolder newFolderHandle = ResourcesPlugin.getWorkspace().getRoot().getFolder(newFolderPath);
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				AbstractOperation op;
				op = new CreateFolderOperation(
					newFolderHandle, null, false, null,
					getString("errorCreatingNewFolderTitle")); //$NON-NLS-1$
				try {
					// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
					// directly execute the operation so that the undo state is
					// not preserved.  Making this undoable can result in accidental
					// folder (and file) deletions.
					op.execute(monitor, WorkspaceUndoUtil
						.getUIInfoAdapter(getShell()));
				} catch (final ExecutionException e) {
					getShell().getDisplay().syncExec(
							new Runnable() {
								public void run() {
									if (e.getCause() instanceof CoreException) {
										ErrorDialog
												.openError(getShell(), // Was Utilities.getFocusShell()
														getString("internalErrorCreatingNewFolderTitle"), //$NON-NLS-1$
														null, // no special message
														((CoreException) e
																.getCause())
																.getStatus());
									} else {
										UiConstants.Util.log(IStatus.ERROR, e, e.getCause().getMessage());
										MessageDialog
												.openError(getShell(),
														getString("internalErrorCreatingNewFolderTitle"), //$NON-NLS-1$
														UiConstants.Util.getString("internalErrorMsg", //$NON-NLS-1$
																		e
																				.getCause()
																				.getMessage()));
									}
								}
							});
				}
			}
		};

		try {
			new ProgressMonitorDialog(getShell()).run(true, true, op);
		} catch (InterruptedException e) {
			return;
		} catch (InvocationTargetException e) {
			// ExecutionExceptions are handled above, but unexpected runtime
			// exceptions and errors may still occur.
			UiConstants.Util.log(IStatus.ERROR, e, e.getTargetException().getMessage()); 
			MessageDialog
					.open(MessageDialog.ERROR,getShell(),
							getString("internalErrorCreatingNewFolderTitle"), //$NON-NLS-1$
							getString("internalErrorMsg", //$NON-NLS-1$,
											e.getTargetException().getMessage()), SWT.SHEET);
		}

    }
    
    @Override
    public void setProperties(Properties props) {
    	this.designerProperties = props;
    }
    
    class ProjectTemplateOptionsPage extends AbstractWizardPage {
        private String NO_FOLDERS_MESSAGE = getString("noFoldersMessage"); //$NON-NLS-1$
        private String CREATE_FOLDER = getString("createFolderOperationLabel"); //$NON-NLS-1$
        
		public boolean createSources = true;
		public boolean createViews = true;
		public boolean createSchema = true;
		public boolean createWebServices = true;
		public boolean createFunctions = false;
		public boolean createExtensions = false;
        
        private Button sourcesCB;
        private Button viewsCB;
        private Button schemaCB;
        private Button webServicesCB;
        private Button functionsCB;
        private Button extensionsCB;
        
        private Text sourcesText;
        private Text viewsText;
        private Text schemaText;
        private Text webServicesText;
        private Text functionsText;
        private Text extensionsText;
        
        public String sourcesStr = SOURCES;
        public String viewsStr = VIEWS;
        public String schemaStr = SCHEMAS;
        public String webServicesStr = WEB_SERVICES;
        public String functionsStr = FUNCTIONS;
        public String extensionsStr = EXTENSIONS;
        
        private String projectName;
        
        private boolean wasVisible = false;
        
        public ProjectTemplateOptionsPage() {
            super(ProjectTemplateOptionsPage.class.getSimpleName(), getString("optionsTitle"));//$NON-NLS-1$
            this.setDescription(getString("initialMessage")); //$NON-NLS-1$
        }

        public void createControl( Composite theParent ) {
            final int COLUMNS = 1;
            Composite mainPanel = WidgetFactory.createPanel(theParent, SWT.NO_SCROLL, GridData.FILL_BOTH);
            mainPanel.setLayout(new GridLayout(COLUMNS, false));
            setControl(mainPanel);
            createOptionsPanel(mainPanel);

            setPageStatus();
        }

        @Override
        public boolean canFlipToNextPage() {
            return false;
        }

        void setPageStatus() {
            setMessage(null, IStatus.OK);
        }

        private void createOptionsPanel( Composite theParent ) {
        	GridData gd = new GridData(GridData.FILL_BOTH);
            theParent.setLayoutData(gd);

            Group folderOptionsGroup = WidgetFactory.createGroup(theParent, getString("optionGroupLabel"), GridData.FILL_HORIZONTAL, 1, 2); //$NON-NLS-1$
            
            // ============ SOURCES FOLDER ====================================================================
            sourcesCB = WidgetFactory.createCheckBox(folderOptionsGroup);
            sourcesCB.setText(CREATE_FOLDER);
            sourcesCB.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    createSources = sourcesCB.getSelection();
                    sourcesText.setEnabled(sourcesCB.getSelection());
                    validate();
                }
            });
            sourcesCB.setSelection(createSources);
            
            sourcesText = createTextField(folderOptionsGroup, SOURCES, getString("createFolderCheckBoxLabel", SOURCES )); //$NON-NLS-1$
            sourcesText.addModifyListener( new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    sourcesStr = sourcesText.getText();
                    validate();
                }
            });
            
            // ============ VIEWS FOLDER ====================================================================
            viewsCB = WidgetFactory.createCheckBox(folderOptionsGroup);
            viewsCB.setText(CREATE_FOLDER);
            viewsCB.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    createViews = viewsCB.getSelection();
                    viewsText.setEnabled(viewsCB.getSelection());
                    validate();
                }
            });
            viewsCB.setSelection(createViews);
            
            viewsText = createTextField(folderOptionsGroup, VIEWS, getString("createFolderCheckBoxLabel", VIEWS )); //$NON-NLS-1$
            viewsText.addModifyListener( new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                	
                    viewsStr = viewsText.getText();
                    validate();
                }
            });
            
            // ============ SCHEMAS FOLDER ====================================================================
            schemaCB = WidgetFactory.createCheckBox(folderOptionsGroup);
            schemaCB.setText(CREATE_FOLDER);
            schemaCB.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    createSchema = schemaCB.getSelection();
                    schemaText.setEnabled(schemaCB.getSelection());
                    validate();
                }
            });
            schemaCB.setSelection(createSchema);
            
            schemaText = createTextField(folderOptionsGroup, SCHEMAS, getString("createFolderCheckBoxLabel", SCHEMAS )); //$NON-NLS-1$
            schemaText.addModifyListener( new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    schemaStr = schemaText.getText();
                    validate();
                }
            });
            
            // ============ WEB SERVICES FOLDER ====================================================================
            webServicesCB = WidgetFactory.createCheckBox(folderOptionsGroup);
            webServicesCB.setText(CREATE_FOLDER);
            webServicesCB.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    createWebServices = webServicesCB.getSelection();
                    webServicesText.setEnabled(webServicesCB.getSelection());
                    validate();
                }
            });
            webServicesCB.setSelection(createWebServices);
            
            webServicesText = createTextField(folderOptionsGroup, WEB_SERVICES, getString("createFolderCheckBoxLabel", WEB_SERVICES )); //$NON-NLS-1$
            webServicesText.addModifyListener( new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    webServicesStr = webServicesText.getText();
                    validate();
                }
            });
            
            // ============ FUNCTIONS FOLDER ====================================================================
            functionsCB = WidgetFactory.createCheckBox(folderOptionsGroup);
            functionsCB.setText(CREATE_FOLDER);
            functionsCB.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    createFunctions = functionsCB.getSelection();
                    functionsText.setEnabled(functionsCB.getSelection());
                    validate();
                }
            });
            functionsCB.setSelection(createFunctions);
            
            functionsText = createTextField(folderOptionsGroup, FUNCTIONS, getString("createFolderCheckBoxLabel", FUNCTIONS )); //$NON-NLS-1$
            functionsText.addModifyListener( new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    functionsStr = functionsText.getText();
                    validate();
                }
            });
            
            // ============ EXTENSIONS FOLDER ====================================================================
            extensionsCB = WidgetFactory.createCheckBox(folderOptionsGroup);
            extensionsCB.setText(CREATE_FOLDER);
            extensionsCB.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(final SelectionEvent event) {
                    createExtensions = extensionsCB.getSelection();
                    extensionsText.setEnabled(extensionsCB.getSelection());
                    validate();
                }
            });
            extensionsCB.setSelection(createExtensions);
            
            extensionsText = createTextField(folderOptionsGroup, EXTENSIONS, getString("createFolderCheckBoxLabel", EXTENSIONS )); //$NON-NLS-1$
            extensionsText.addModifyListener( new ModifyListener() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    extensionsStr = extensionsText.getText();
                    validate();
                }
            });
            
        }
        
        private Text createTextField(Composite parent, String text, String tooltip) {
        	Text newText = new Text(parent, SWT.BORDER);
        	newText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        	newText.setText(text);
        	newText.setToolTipText(tooltip);
            return newText;
        }
        
        public boolean wasVisible() {
        	return wasVisible;
        }
        
        @Override
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			wasVisible = true;
		}

		private void validate() {

			if( !(createSchema ||
				createFunctions ||
				createExtensions ||
				createViews ||
				createSources ||
				createWebServices) ) {
				// NO POSSIBLE FOLDERS TO CREATE
				setPageStatus(true, NO_FOLDERS_MESSAGE, null);
				return;
			}
			
			// check for duplicate names
			if( createExtensions ) {
				if( extensionsStr.length() == 0 ) {
					setPageStatus(false, null, getString("zeroLengthFolderNameError")); //$NON-NLS-1$
					return;
				}
				
				if( checkDuplicateName(createSources, extensionsStr, sourcesStr)) return;
				if( checkDuplicateName(createViews, extensionsStr, viewsStr)) return;
				if( checkDuplicateName(createSchema, extensionsStr, schemaStr)) return;
				if( checkDuplicateName(createWebServices, extensionsStr, webServicesStr)) return;
				if( checkDuplicateName(createFunctions, extensionsStr, functionsStr)) return;
			}
			
			if( createFunctions ) {
				if( functionsStr.length() == 0 ) {
					setPageStatus(false, null, getString("zeroLengthFolderNameError")); //$NON-NLS-1$
					return;
				}
				
				if( checkDuplicateName(createSources, functionsStr, sourcesStr)) return;
				if( checkDuplicateName(createViews, functionsStr, viewsStr)) return;
				if( checkDuplicateName(createSchema, functionsStr, schemaStr)) return;
				if( checkDuplicateName(createWebServices, functionsStr, webServicesStr)) return;
				if( checkDuplicateName(createExtensions, functionsStr, extensionsStr)) return;
			}
			if( createSources ) {
				if( sourcesStr.length() == 0 ) {
					setPageStatus(false, null, getString("zeroLengthFolderNameError")); //$NON-NLS-1$
					return;
				}
				
				if( checkDuplicateName(createViews, sourcesStr, viewsStr)) return;
				if( checkDuplicateName(createSchema, sourcesStr, schemaStr)) return;
				if( checkDuplicateName(createWebServices, sourcesStr, webServicesStr)) return;
				if( checkDuplicateName(createFunctions, sourcesStr, functionsStr)) return;
				if( checkDuplicateName(createExtensions, sourcesStr, extensionsStr)) return;
			}
			if( createViews ) {
				if( viewsStr.length() == 0 ) {
					setPageStatus(false, null, getString("zeroLengthFolderNameError")); //$NON-NLS-1$
					return;
				}
				
				if( checkDuplicateName(createSources, viewsStr, sourcesStr)) return;
				if( checkDuplicateName(createSchema, viewsStr, schemaStr)) return;
				if( checkDuplicateName(createWebServices, viewsStr, webServicesStr)) return;
				if( checkDuplicateName(createFunctions, viewsStr, functionsStr)) return;
				if( checkDuplicateName(createExtensions, viewsStr, extensionsStr)) return;
			}
			if( createSchema ) {
				if( extensionsStr.length() == 0 ) {
					setPageStatus(false, null, getString("zeroLengthFolderNameError")); //$NON-NLS-1$
					return;
				}
				
				if( checkDuplicateName(createSources, schemaStr, sourcesStr)) return;
				if( checkDuplicateName(createViews, schemaStr, viewsStr)) return;
				if( checkDuplicateName(createWebServices, schemaStr, webServicesStr)) return;
				if( checkDuplicateName(createFunctions, schemaStr, functionsStr)) return;
				if( checkDuplicateName(createExtensions, schemaStr, extensionsStr)) return;
			}
			if( createWebServices ) {
				if( webServicesStr.length() == 0 ) {
					setPageStatus(false, null, getString("zeroLengthFolderNameError")); //$NON-NLS-1$
					return;
				}
				
				if( checkDuplicateName(createSources, webServicesStr, sourcesStr)) return;
				if( checkDuplicateName(createViews, webServicesStr, viewsStr)) return;
				if( checkDuplicateName(createSchema, webServicesStr, schemaStr)) return;
				if( checkDuplicateName(createFunctions, webServicesStr, functionsStr)) return;
				if( checkDuplicateName(createExtensions, webServicesStr, extensionsStr)) return;
			}

			setPageStatus(true, getString("initialMessage", projectName), null);  //$NON-NLS-1$
		}
		
		private void setPageStatus(boolean complete, String message, String errorMessage) {
			setErrorMessage(errorMessage);
			setMessage(message);
			setPageComplete(complete);
		}
		
		private boolean checkDuplicateName(boolean doCheck, String str1, String str2) {
			if( doCheck && str1.equalsIgnoreCase(str2) ){
				this.setErrorMessage(getString("duplicateFolderNameError", functionsStr)); //$NON-NLS-1$
				setPageComplete(false);
				return true;
			}
			return false;
		}
    }
}
