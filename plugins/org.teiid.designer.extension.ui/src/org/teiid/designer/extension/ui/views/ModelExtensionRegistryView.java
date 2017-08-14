/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.views;

import static org.teiid.designer.extension.ExtensionConstants.MedOperations.SHOW_IN_REGISTRY;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.CHECK_MARK;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.REGISTERY_MED_UPDATE_ACTION;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.UNREGISTER_MED;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.registry.ExtensionDefinitionsManager;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.registry.RegistryEvent;
import org.teiid.designer.extension.registry.RegistryListener;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.actions.RegistryDeploymentValidator;
import org.teiid.designer.extension.ui.wizards.NewMedWizard;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.table.TableViewerBuilder;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.StatusInfo;
import org.teiid.designer.ui.explorer.ModelExplorerLabelProvider;
import org.teiid.designer.ui.util.ErrorHandler;
import org.teiid.designer.ui.viewsupport.ModelingResourceFilter;


/**
 * The ModelExtension Registry View
 */
public final class ModelExtensionRegistryView extends ViewPart implements ExtensionConstants {

    private IAction cloneMedAction;

    private IAction viewMedAction;

    private IAction findMedReferencesAction;

    private IAction registerMedAction;

    private final ModelExtensionRegistry registry;

    private IAction unregisterMedAction;

    private TableViewerBuilder viewerBuilder;

    private RegistryListener registryListener = new RegistryListener() {

        @Override
        public void process(RegistryEvent event) {
            handleRegistryChanged(event);
        }
    };

    /**
     * Constructor
     */
    public ModelExtensionRegistryView() {
        this.registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);
        if(this.registry != null)
            this.registry.addListener(registryListener);
    }

    @Override
    public void dispose() {
        if (this.registry != null)
            this.registry.removeListener(registryListener);

        super.dispose();
    }

    private void configureColumn( TableViewerColumn viewerColumn,
                                  int columnIndex,
                                  String headerText,
                                  String headerToolTip,
                                  boolean resizable ) {

        TableColumn column = viewerColumn.getColumn();
        column.setText(headerText);
        column.setToolTipText(headerToolTip);
        column.setMoveable(false);
        column.setResizable(resizable);
    }

    private void configureMenu( IMenuManager menuMgr ) {
        menuMgr.add(this.findMedReferencesAction);
    }

    private void configureToolBar( IToolBarManager toolBarMgr ) {
        toolBarMgr.add(this.registerMedAction);
        toolBarMgr.add(this.unregisterMedAction);
        toolBarMgr.add(this.cloneMedAction);
        toolBarMgr.add(this.viewMedAction);
        toolBarMgr.update(true);
    }

    private void createActions() {
        this.cloneMedAction = new Action(Messages.cloneMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleCloneMed();
            }
        };
        this.cloneMedAction.setToolTipText(Messages.cloneMedActionToolTip);
        this.cloneMedAction.setEnabled(false);
        this.cloneMedAction.setImageDescriptor(PlatformUI.getWorkbench()
                                                         .getSharedImages()
                                                         .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

        this.viewMedAction = new Action(Messages.viewMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleViewMed();
            }
        };
        this.viewMedAction.setToolTipText(Messages.viewMedActionToolTip);
        this.viewMedAction.setEnabled(false);
        this.viewMedAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(org.teiid.designer.extension.ui.UiConstants.ImageIds.VIEW_MED_ACTION));
        
        this.findMedReferencesAction = new Action(Messages.findMedReferencesActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleFindMedReferences();
            }
        };
        this.findMedReferencesAction.setToolTipText(Messages.findMedReferencesActionToolTip);
        this.findMedReferencesAction.setEnabled(false);

        try {
            this.findMedReferencesAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(org.teiid.designer.extension.ui.UiConstants.ImageIds.SEARCH));
        } catch (Exception e) {
            ErrorHandler.toExceptionDialog(e);
        }

        this.registerMedAction = new Action(Messages.registerMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleRegisterMed();
            }
        };
        this.registerMedAction.setToolTipText(Messages.registerMedActionToolTip);
        this.registerMedAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(REGISTERY_MED_UPDATE_ACTION));

        this.unregisterMedAction = new Action(Messages.unregisterMedActionText, SWT.BORDER) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#run()
             */
            @Override
            public void run() {
                handleUnregisterMed();
            }
        };
        this.unregisterMedAction.setToolTipText(Messages.unregisterMedActionToolTip);
        this.unregisterMedAction.setEnabled(false);
        this.unregisterMedAction.setImageDescriptor(Activator.getDefault().getImageDescriptor(UNREGISTER_MED));
    }

    private void createColumns() {
        // NOTE: create in the order in ColumnIndexes
        TableViewerColumn column = viewerBuilder.createColumn(SWT.CENTER, 5, 25, true);
        configureColumn(column, ColumnIndexes.BUILT_IN, Messages.builtInColumnText, Messages.builtInColumnToolTip, false);

        column = viewerBuilder.createColumn(SWT.CENTER, 5, 25, true);
        configureColumn(column, ColumnIndexes.IMPORTED, Messages.importedColumnText, Messages.importedColumnToolTip, false);

        column = viewerBuilder.createColumn(SWT.LEFT, 14, 30, true);
        configureColumn(column, ColumnIndexes.NAMESPACE_PREFIX, Messages.namespacePrefixColumnText,
                        Messages.namespacePrefixColumnToolTip, true);

        column = viewerBuilder.createColumn(SWT.LEFT, 14, 30, true);
        configureColumn(column, ColumnIndexes.NAMESPACE_URI, Messages.namespaceUriColumnText, Messages.namespaceUriColumnToolTip,
                        true);

        column = viewerBuilder.createColumn(SWT.LEFT, 14, 30, true);
        configureColumn(column, ColumnIndexes.METAMODEL_URI, Messages.extendedMetamodelUriColumnText,
                        Messages.metamodelUriColumnToolTip, true);

        column = viewerBuilder.createColumn(SWT.LEFT, 14, 30, true);
        configureColumn(column, ColumnIndexes.MODEL_TYPES, Messages.modelTypeColumnText, Messages.modelTypesColumnToolTip, true);

        column = viewerBuilder.createColumn(SWT.RIGHT, 5, 10, true);
        configureColumn(column, ColumnIndexes.VERSION, Messages.versionColumnText, Messages.versionColumnToolTip, true);

        column = viewerBuilder.createColumn(SWT.LEFT, 22, 40, true);
        configureColumn(column, ColumnIndexes.DESCRIPTION, Messages.descriptionColumnText, Messages.descriptionColumnToolTip, true);
    }

    private MenuManager createContextMenu() {
        MenuManager mgr = new MenuManager();
        mgr.add(this.registerMedAction);
        mgr.add(this.unregisterMedAction);
        mgr.add(this.cloneMedAction);
        mgr.add(this.viewMedAction);

        return mgr;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        Composite pnlMain = new Composite(parent, SWT.BORDER);
        pnlMain.setLayout(new GridLayout());
        pnlMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.viewerBuilder = new TableViewerBuilder(pnlMain, (SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION));
        ColumnViewerToolTipSupport.enableFor(this.viewerBuilder.getTableViewer());

        // create columns
        createColumns();

        this.viewerBuilder.setComparator(new ViewerComparator() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public int compare( Viewer viewer,
                                Object med1,
                                Object med2 ) {
                assert med1 instanceof ModelExtensionDefinition;
                assert med1 instanceof ModelExtensionDefinition;

                return super.compare(viewer, ((ModelExtensionDefinition)med1).getNamespacePrefix(),
                                     ((ModelExtensionDefinition)med2).getNamespacePrefix());
            }
        });

        this.viewerBuilder.setLabelProvider(new MedLabelProvider());
        this.viewerBuilder.setContentProvider(new IStructuredContentProvider() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                Collection<ModelExtensionDefinition> definitions = new ArrayList<ModelExtensionDefinition>();

                for (ModelExtensionDefinition med : getModelExtensionDefinitions()) {
                    if (med.getModelExtensionAssistant().supportsMedOperation(SHOW_IN_REGISTRY, null)) {
                        definitions.add(med);
                    }
                }

                return definitions.toArray();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {
                // nothing to do
            }
        });

        this.viewerBuilder.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleMedSelected();
            }
        });

        // populate the view
        this.viewerBuilder.setInput(this);

        createActions();

        MenuManager mgr = createContextMenu();
        Menu menu = mgr.createContextMenu(this.viewerBuilder.getControl());
        this.viewerBuilder.getControl().setMenu(menu);

        IActionBars actionBars = getViewSite().getActionBars();
        configureMenu(actionBars.getMenuManager());
        configureToolBar(actionBars.getToolBarManager());

        registerGlobalActions(getViewSite().getActionBars());
    }

    Collection<ModelExtensionDefinition> getModelExtensionDefinitions() {
        if (this.registry != null) {
            return this.registry.getAllDefinitions();
        }

        return Collections.emptyList();
    }

    private ModelExtensionDefinition getSelectedMed() {
        IStructuredSelection selection = (IStructuredSelection)this.viewerBuilder.getSelection();

        if (selection.isEmpty()) {
            return null;
        }

        // selection must be one MED
        assert (selection.size() == 1) : "selection size is not zero or one"; //$NON-NLS-1$
        assert (selection.getFirstElement() instanceof ModelExtensionDefinition) : "selected object is not a MED"; //$NON-NLS-1$

        return (ModelExtensionDefinition)selection.getFirstElement();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return NLS.bind(Messages.registryViewToolTip, this.registry.getAllDefinitions().size());
    }

    void handleCloneMed() {
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Clone MED action should not be enabled if there is no selection"; //$NON-NLS-1$

        NewMedWizard wizard = new NewMedWizard(Messages.copyMedWizardTitle, selectedMed);
        wizard.init(UiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(), null);

        // Open wizard dialog
        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
        wizardDialog.setBlockOnOpen(true);
        wizardDialog.open();
    }

    /*
     * Handler for View Built-In MEDS
     */
    void handleViewMed() {
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Clone MED action should not be enabled if there is no selection"; //$NON-NLS-1$

        IFile medFile = null;

        try {
            // If the selected MED is built in, look in hidden BuiltIn MEDs folder first.  If not in the hidden folder,
            // then copy it there
            if (selectedMed.isBuiltIn()) {
                medFile = copyMedToBuiltInProject(EMPTY_STRING, selectedMed);
            } else if (selectedMed.isImported()) {
                // Must be a MED imported from a teiid instance translator

                // Save the definition to the imported med filesystem directory
                String definitionsPath = ExtensionPlugin.getInstance().getUserDefinitionsPath();
                ExtensionDefinitionsManager manager = new ExtensionDefinitionsManager(definitionsPath);
                File defnFile = manager.saveDefinition(new ModelExtensionDefinitionWriter(), selectedMed);
                if (defnFile != null)
                    // Since the file is outside of the workspace, create a link to it in the
                    // built-in project so that it can be opened in the editor
                    medFile = linkMedToBuiltInProject(TEIID_IMPORT_DIRECTORY, defnFile);
            } else {
                // Lookup MED in the workspace
                medFile = findWorkspaceMedMatch(selectedMed.getNamespacePrefix());
            }

            // Open the MED file in the editor
            if (medFile != null) {
                IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
                IDE.openEditor(page, medFile);
            } else {
                MessageDialog.openError(getShell(),
                                        Messages.openMedViewFailedTitle,
                                        NLS.bind(Messages.openMedCouldNotViewFile, selectedMed.getNamespacePrefix()));
            }

        } catch (Exception ex) {
            ErrorHandler.toExceptionDialog(ex);
        }

    }

    /*
     * Try to find MED in the workspace.  It is returned if found, otherwise returns null
     * @param nsPrefix the namespace prefix of the med
     * @return the medFile if found
     */
    private IFile findWorkspaceMedMatch(String nsPrefix) {
    	IFile medFileMatch = null;

        Collection<IProject> projects = DotProjectUtils.getOpenModelProjects();
        for(IProject project : projects) {
        	Collection<IFile> projFiles = DotProjectUtils.getAllProjectResources(project);    
        	for(IFile projFile : projFiles) {
				String fileExt = projFile.getFileExtension();
				if(fileExt!=null && MED_EXTENSION.equalsIgnoreCase(fileExt)) {
					if(medFileMatches( projFile , nsPrefix )) {
						medFileMatch = projFile;
						break;
					}
				}
        	}
        	if(medFileMatch!=null) break;
        }
        
    	return medFileMatch;
    }
    
    /*
     * Determine if the supplied medFile has the same Namespace Prefix as the supplied namespace
     * @param medFile the med IFile
     * @param nsPrefix the namespace prefix to match
     * @return 'true' if matches, 'false' if not.
     */
    private boolean medFileMatches(IFile medFile, String nsPrefix) {

    	InputStream fileContents = null;
        try {
            fileContents = medFile.getContents();
        } catch (CoreException e) {
            ErrorHandler.toExceptionDialog(NLS.bind(Messages.medFileGetContentsErrorMsg, medFile.getName()), e);
            return false;
        }

        if (fileContents == null)
            return false;

        // Parse file contents to get the MED. Show info dialog if parse errors.
        ModelExtensionDefinition med = null;

        try {
            ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(
                                                                                       ExtensionPlugin.getInstance().getMedSchema());
            med = parser.parse(fileContents, ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());

            if (!parser.getErrors().isEmpty()) {
                MessageDialog.openError(getShell(),
                                        Messages.registerMedActionFailedTitle,
                                        NLS.bind(Messages.medFileParseErrorMsg, medFile.getName()));
                return false;
            }
        } catch (Exception e) {
            ErrorHandler.toExceptionDialog(Messages.registerMedActionFailedMsg, e);
            return false;
        } finally {
        	try {
				fileContents.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

        // Continue checks on parsable MED
        if (med == null)
            return false;

        String medFileNsPrefix = med.getNamespacePrefix();
        if (medFileNsPrefix == null)
            return false;

        if (! medFileNsPrefix.equals(nsPrefix)) {
            return false;
        }

        return true;
    }

    /**
     * Create directory structure defined by given path
     *
     * @param mxdFilePath
     * @param project
     * @throws CoreException
     */
    private void createDirectories(IPath filePath, IProject project) throws CoreException {
        /*
         * Allows files in sub-directories to be copied into project
         * by constructing the path of directories first
         */
        if (filePath.segmentCount() == 1)
            return;

        // Path has directories that would need to be created
        StringBuilder directory = new StringBuilder();
        NullProgressMonitor monitor = new NullProgressMonitor();
        for (String segment : filePath.segments()) {
            if (filePath.lastSegment().equals(segment))
                break;

            directory.append(File.separator).append(segment);

            Path folderPath = new Path(directory.toString());
            IFolder folder = project.getFolder(folderPath);
            if (folder == null || folder.exists())
                continue;

            folder.create(false, true, monitor);
        }
    }

    private IFile getFileFromBuiltInProject(IPath filePath) throws Exception {
        final IProject builtInProject = ModelerCore.getWorkspace().getRoot().getProject(ExtensionConstants.BUILTIN_MEDS_PROJECT_NAME);
        if (builtInProject == null)
            return null;

        NullProgressMonitor monitor = new NullProgressMonitor();
        builtInProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);

        // If project does not exist, create it.
        if(!builtInProject.exists())
            builtInProject.create(monitor);

        // Open the project
        builtInProject.open(monitor);

        return builtInProject.getFile(filePath);
    }

    private IFile linkMedToBuiltInProject(String subDirectory, File mxdFile) throws Exception {
        if(mxdFile == null || ! mxdFile.exists())
            return null;

        StringBuilder mxdPathBuilder = new StringBuilder(subDirectory);
        mxdPathBuilder.append(File.separator).append(mxdFile.getName());

        IPath mxdLinkPath = new Path(mxdPathBuilder.toString());
        IFile mxdLink = getFileFromBuiltInProject(mxdLinkPath);
        IProject project = mxdLink.getProject();
        NullProgressMonitor monitor = new NullProgressMonitor();

        // If link exists then delete it since it may be stale and out-of-date
        if (mxdLink.exists()) {
            mxdLink.delete(true, monitor);
        }

        // Create link
        createDirectories(mxdLinkPath, project);
        mxdLink.createLink(new Path(mxdFile.getAbsolutePath()), IResource.NONE, null);

        // Set the link to read-only to avoid editing these imported files
        ResourceAttributes attributes = mxdLink.getResourceAttributes();
        attributes.setReadOnly(true);
        mxdLink.setResourceAttributes(attributes);

        // Refresh project
        project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

        return mxdLink;
    }

    /**
     * Copy the given med to the built-in project, creating (if necessary)
     * and adding it to the given sub-directory.
     *
     * @param subDirectory
     * @param medDefinition
     * @return the created {@link IFile} handle to the med
     */
    private IFile copyMedToBuiltInProject(String subDirectory, ModelExtensionDefinition medDefinition) throws Exception {
        String medName = medDefinition.getNamespacePrefix().toLowerCase();

        StringBuilder mxdPathBuilder = new StringBuilder(subDirectory);
        mxdPathBuilder.append(File.separator).append(medName).append(DOT_MED_EXTENSION);

        IPath mxdFilePath = new Path(mxdPathBuilder.toString());
        IFile mxdFile = getFileFromBuiltInProject(mxdFilePath);
        IProject project = mxdFile.getProject();

        // If failed to get a handle or mxd already exists then return
        if (mxdFile.exists())
            return mxdFile;

        InputStream medInputStream = null;
        try {
            createDirectories(mxdFilePath, project);

            // Create the MED
            final ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
            medInputStream = medWriter.writeAsStream(medDefinition);

            NullProgressMonitor monitor = new NullProgressMonitor();
            // Create the resource and set it to read-only
            mxdFile.create(medInputStream, false, monitor);
            ResourceAttributes attributes = mxdFile.getResourceAttributes();
            attributes.setReadOnly(true);
            mxdFile.setResourceAttributes(attributes);

            // Refresh project
            project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

            return mxdFile;
        } finally {
            if (medInputStream != null) {
                try {
                    medInputStream.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

    void handleFindMedReferences() {
        // TODO implement handleFindMedReferences
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Find MED references action should not be enabled if there is no selection"; //$NON-NLS-1$
    }

    void handleMedSelected() {
        ModelExtensionDefinition selectedMed = getSelectedMed();

        // No Selection, disable
        if (selectedMed == null) {
            this.viewMedAction.setEnabled(false);
            this.cloneMedAction.setEnabled(false);
            this.findMedReferencesAction.setEnabled(false);
            this.unregisterMedAction.setEnabled(false);
            // MED Selected, enabled depending on builtIn for some
        } else {
       		this.viewMedAction.setEnabled(true);
            this.cloneMedAction.setEnabled(true);
            // this.findMedReferencesAction.setEnabled(true); // TODO uncomment this when action is implemented
            if (selectedMed.isBuiltIn()) {
                this.unregisterMedAction.setEnabled(false);
            } else {
                this.unregisterMedAction.setEnabled(true);
            }
        }
    }

    /*
     * Select a MED from the workspace and add it to the Registry
     */
    void handleRegisterMed() {
        // ---------------------------------
        // Select mxd from workspace
        // ---------------------------------
        IFile mxdFile = selectWorkspaceMed();

        if (mxdFile != null) {
            RegistryDeploymentValidator.deploy(mxdFile);
        }
    }

    private IFile selectWorkspaceMed() {
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
                if (!isMedFile(file))
                    return false;
                return true;
            }
        };
        ModelingResourceFilter wsFilter = new ModelingResourceFilter(filter);
        wsFilter.setShowHiddenProjects(false);
        wsFilter.addFilter(new ModelProjectFilter());
        final Object[] models = WidgetUtil.showWorkspaceObjectSelectionDialog(Messages.selectMedDialogTitle,
                                                                              Messages.selectMedDialogMsg, false, null, wsFilter,
                                                                              medSelectionValidator,
                                                                              new ModelExplorerLabelProvider());
        // Return selected mxd. If nothing selected, return null
        if (models.length > 0 && models[0] instanceof IFile)
            return (IFile)models[0];
        return null;
    }

    /** Validator that makes sure the selection containes all WSDL files. */
    private ISelectionStatusValidator medSelectionValidator = new ISelectionStatusValidator() {
        @Override
        public IStatus validate( Object[] theSelection ) {
            IStatus result = null;
            boolean valid = true;

            if ((theSelection != null) && (theSelection.length > 0)) {
                for (int i = 0; i < theSelection.length; i++) {
                    if ((!(theSelection[i] instanceof IFile)) || !isMedFile((IFile)theSelection[i])) {
                        valid = false;
                        break;
                    }
                }
            } else {
                valid = false;
            }

            if (valid) {
                result = new StatusInfo(ExtensionConstants.PLUGIN_ID);
            } else {
                result = new StatusInfo(ExtensionConstants.PLUGIN_ID, IStatus.ERROR, Messages.selectMedDialogNotMedSelectionMsg);
            }

            return result;
        }
    };

    /**
     * Return true if the IResource represents a mxd file.
     * 
     * @param resource The file that may be a mxd file
     * @return true if it is a mxd
     */
    public boolean isMedFile( final IResource resource ) {
        // Check that the resource has the correct lower-case extension
        if (MED_EXTENSION.equals(resource.getFileExtension()))
            return true;
        return false;
    }

    void handleRegistryChanged( final RegistryEvent event ) {
        final ModelExtensionDefinition med = event.getDefinition();

        // Ensure this is called in the UI thread
        UiUtil.runInSwtThread(new Runnable() {
            @Override
            public void run() {
                if (event.isAdd()) {
                    viewerBuilder.add(med);
                } else if (event.isChange()) {
                    viewerBuilder.getTableViewer().refresh(med);
                } else if (event.isRemove()) {
                    viewerBuilder.getTableViewer().remove(med);
                }
            }
        }, true);
    }

    void handleUnregisterMed() {
        ModelExtensionDefinition selectedMed = getSelectedMed();
        assert (selectedMed != null) : "Unregister MED action should not be enabled if there is no selection"; //$NON-NLS-1$

        // Confirm that user really wants to unregister
        boolean continueRemove = MessageDialog.openConfirm(getShell(), Messages.unregisterMedConfirmTitle,
                                                           Messages.unregisterMedConfirmMsg);
        if (continueRemove) {
            this.registry.removeDefinition(selectedMed.getNamespacePrefix());
        }
    }

    private void registerGlobalActions( IActionBars actionBars ) {
        actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), this.cloneMedAction);
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), this.unregisterMedAction);

        // properties and select all should always be disabled
        IAction unsupportedAction = new Action() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.action.Action#setEnabled(boolean)
             */
            @Override
            public void setEnabled( boolean theEnabled ) {
                super.setEnabled(false);
            }
        };
        unsupportedAction.setEnabled(false);

        actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), unsupportedAction);
        actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), unsupportedAction);

        // save these changes
        actionBars.updateActionBars();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if ((this.viewerBuilder != null) && !this.viewerBuilder.getControl().isDisposed()) {
            this.viewerBuilder.getControl().setFocus();
        }
    }

    interface ColumnIndexes {
        int BUILT_IN = 0;
        int IMPORTED = 1;
        int NAMESPACE_PREFIX = 2;
        int NAMESPACE_URI = 3;
        int METAMODEL_URI = 4;
        int MODEL_TYPES = 5;
        int VERSION = 6;
        int DESCRIPTION = 7;
    }

    class MedLabelProvider extends StyledCellLabelProvider {

        private Image getImage( int columnIndex, ModelExtensionDefinition element ) {
            switch (columnIndex) {
                case ColumnIndexes.BUILT_IN:
                    if (element.isBuiltIn())
                        return Activator.getDefault().getImage(CHECK_MARK);
                    break;
                case ColumnIndexes.IMPORTED:
                    if (element.isImported())
                        return Activator.getDefault().getImage(CHECK_MARK);
                    break;
            }

            return null;
        }

        private String getText( int columnIndex, ModelExtensionDefinition element ) {
            switch (columnIndex) {
                case ColumnIndexes.BUILT_IN:
                	if (element.isBuiltIn())
                		return "X";
                	return CoreStringUtil.Constants.EMPTY_STRING;
                case ColumnIndexes.IMPORTED:
                	if (element.isImported())
                		return "X";
                    return CoreStringUtil.Constants.EMPTY_STRING;

                case ColumnIndexes.NAMESPACE_PREFIX:
                    return element.getNamespacePrefix();

                case ColumnIndexes.NAMESPACE_URI:
                    return element.getNamespaceUri();

                case ColumnIndexes.METAMODEL_URI:
                    String metamodelUri =  element.getMetamodelUri();
                    return Activator.getDefault().getMetamodelName(metamodelUri);

                case ColumnIndexes.MODEL_TYPES:
                    Collection<String> modelTypes =  element.getSupportedModelTypes();

                    if (modelTypes.isEmpty()) {
                        return Messages.allModelTypesAreSupported;
                    }

                    StringBuilder text = new StringBuilder();
                    boolean firstTime = true;

                    for (String modelType : modelTypes) {
                        if (firstTime) {
                            firstTime = false;
                        } else {
                            text.append(SPACE).append(COMMA);
                        }

                        text.append(Activator.getDefault().getModelTypeName(modelType));
                    }

                    return text.toString();

                case ColumnIndexes.VERSION:
                    return Integer.toString( element.getVersion());

                case ColumnIndexes.DESCRIPTION:
                    return  element.getDescription();

                default:
                    // shouldn't happen
                    assert false : "Unknown column index of " + columnIndex; //$NON-NLS-1$
                    return null;
            }
        }

        @Override
        public void update(ViewerCell cell) {
            assert cell.getElement() instanceof ModelExtensionDefinition;

            int columnIndex = cell.getColumnIndex();
            ModelExtensionDefinition element = (ModelExtensionDefinition) cell.getElement();

            String text = getText(columnIndex, element);
            Image img = getImage(columnIndex, element);

            if (text != null)
                cell.setText(text);

            if (img != null)
                cell.setImage(img);

            super.update(cell);
        }

        /**
         * Override the paint method to ensure that the
         * check boxes in the built-in and imported columns
         * are centred.
         */
        @Override
        protected void paint(Event event, Object element) {
            TableItem tableItem = (TableItem) event.item;
            Image img = tableItem.getImage(event.index);

            if (img == null) {
                super.paint(event, element);
                return;
            }

            Rectangle bounds = tableItem.getBounds(event.index);
            Rectangle imgBounds = img.getBounds();
            bounds.width /= 2;
            bounds.width -= imgBounds.width / 2;
            bounds.height /= 2;
            bounds.height -= imgBounds.height / 2;

            int x = bounds.width > 0 ? bounds.x + bounds.width : bounds.x;
            int y = bounds.height > 0 ? bounds.y + bounds.height : bounds.y;

            event.gc.drawImage(img, x, y);
        }
    }

    private static Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
    
    class ModelProjectFilter extends ViewerFilter {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        @Override
        public boolean select( Viewer viewer,
                               Object parentElement,
                               Object element ) {
            if (element instanceof IProject) {
                boolean result = false;

                try {
                    result = ((IProject)element).isOpen() && !((IProject)element).hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)
                    && ((IProject)element).hasNature(ModelerCore.NATURE_ID);
                } catch (CoreException e) {
                    ErrorHandler.toExceptionDialog(e);
                }

                return result;
            }

            return true;
        }
    }

}