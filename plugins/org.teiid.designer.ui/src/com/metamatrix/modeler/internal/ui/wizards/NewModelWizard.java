/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.framework.Bundle;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelInitializer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.transaction.UnitOfWorkImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.actions.DeleteResourceAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelInitializerSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor;
import com.metamatrix.modeler.ui.wizards.INewModelWizardContributor2;
import com.metamatrix.modeler.ui.wizards.NewModelWizardInput;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * This is a sample new wizard. Its role is to create a new file resource in the provided container. If the container resource (a
 * folder or a project) is selected in the workspace when the wizard is opened, it will accept it as the target container. The
 * wizard creates one file with the extension "xml". If a sample multi-page editor (also available as a template) is registered
 * for the same extension, it will be able to open it.
 */
public class NewModelWizard extends AbstractWizard
    implements INewWizard, UiConstants, UiConstants.ExtensionPoints.NewModelWizardContributor {

    private static ArrayList contributorList;

    protected NewModelWizardMetamodelPage metamodelSelectionPage;
    protected ISelection selection;
    INewModelWizardContributor wizardPageContributor;
    private IWizardPage[] wizardPageArray;
    private boolean exceptionOccurred = false;

    private NewModelWizardInput defaultNewModelInput;
    private Properties designerProperties;

    /** key = wizardPageContributor title, value = IWizardPage[] */
    private HashMap contributorPageMap = new HashMap();

    private IWizardPage createHiddenProjPage = null;

    // Defect 22359 - improve new model performance
    // added cached new resource so we can override the rollback and delete the resource if needed.
    private ModelResource newModelResource = null;

    /**
     * Constructor for NewModelWizard.
     */
    public NewModelWizard() {
        super(UiPlugin.getDefault(), Util.getString("NewModelWizard.title"), null); //$NON-NLS-1$
        setNeedsProgressMonitor(true);
        setForcePreviousAndNextButtons(true);
        this.newModelResource = null;
    }

    /**
     * Constructor for NewModelWizard.
     */
    public NewModelWizard( NewModelWizardInput newModelInput,
                           Properties properties ) {
        this();
        this.defaultNewModelInput = newModelInput;
        this.newModelResource = null;
        this.designerProperties = properties;
    }

    /**
     * Adding the page to the wizard.
     */
    @Override
    public void addPages() {
        if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()
            && (ProductCustomizerMgr.getInstance().getProductCharacteristics().getHiddenProject(false) == null)) {
            this.createHiddenProjPage = ProductCustomizerMgr.getInstance().getProductCharacteristics().getCreateHiddenProjectWizardPage();

            if (this.createHiddenProjPage != null) {
                addPage(this.createHiddenProjPage);
            }
        }

        metamodelSelectionPage = new NewModelWizardMetamodelPage(selection, designerProperties);
        NewModelWizardInput someModelInput = defaultNewModelInput;

        if (someModelInput == null || someModelInput.isEmpty()) {
            // Let's ask some framework if they want to fine-tune the initial state of the wizard based on selection.
            Object someObj = ProductCustomizerMgr.getInstance().getProductCharacteristics().getNewModelInput(selection);
            if (someObj instanceof NewModelWizardInput) {
                someModelInput = (NewModelWizardInput)someObj;
            }
        }

        if (someModelInput != null && !someModelInput.isEmpty()) {
            metamodelSelectionPage.setNewModelInput(someModelInput);
        }
        addPage(metamodelSelectionPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as
     * execution context.
     */
    @Override
    public boolean finish() {
        newModelResource = null;
        // defect 16340 - if there is no selected builder, be sure to exclude from finish processing:
        if (metamodelSelectionPage.getSelectedBuilder() == null) {
            wizardPageContributor = null;
        } // endif

        final String containerName = metamodelSelectionPage.getContainerName();
        final String fileName = metamodelSelectionPage.getFileName();
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            public void execute( IProgressMonitor theMonitor ) throws InvocationTargetException {
                try {
                    doFinish(containerName, fileName, theMonitor);
                } catch (CoreException e) {
                    throw new InvocationTargetException(e);
                } finally {
                    theMonitor.done();
                }
            }
        };

        final boolean startedTxn = ModelerCore.startTxn(false, false, NewModelWizard.this.getWindowTitle(), NewModelWizard.this);
        // Defect 22359 - improve new model performance
        // utilize the overrideRollback txn capability so the UOW doesn't cache commands
        boolean overrideRollback = false;
        if (startedTxn) {
            overrideRollback = true;
            ((UnitOfWorkImpl)ModelerCore.getCurrentUoW()).setOverrideRollback(overrideRollback);
        }
        boolean success = false;
        try {
            getContainer().run(false, true, op);
            success = true;
        } catch (InterruptedException e) {
            success = false;
            return false;
        } catch (InvocationTargetException e) {
            success = false;
            Throwable realException = e.getTargetException();
            String msg = realException.getMessage();
            if( msg == null || msg.length() == 0 ) {
            	msg = Util.getString("NewModelWizard.Error_1.msg", realException.getClass().getName().toString()); //$NON-NLS-1$
            }
            MessageDialog.openError(getShell(), Util.getString("NewModelWizard.Error_1.title"), msg); //$NON-NLS-1$
            return false;
        } finally {
            if (startedTxn) {
                if (success) {
                    ModelerCore.commitTxn();
                } else {
                    // Defect 22359 - improve new model performance
                    // utilize the overrideRollback txn capability so the UOW doesn't cache commands
                    // this wizard performs the real rollback (i.e. delete new resource)
                    if (overrideRollback) {
                        if (newModelResource != null) {
                            DeleteResourceAction action = new DeleteResourceAction();
                            action.selectionChanged(null, new StructuredSelection(newModelResource));
                            action.run();
                        }
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        newModelResource = null;
        return true;
    }

    void setExceptionOccurred( boolean flag ) {
        this.exceptionOccurred = flag;
    }

    /**
     * The worker method. It will find the container, create the file if missing or just replace its contents, and open the editor
     * on the newly created file.
     */
    void doFinish( final String containerName,
                   final String fileName,
                   final IProgressMonitor monitor ) throws CoreException {
        // create a sample file
        monitor.beginTask(Util.getString("NewModelWizard.Creating__2") + fileName, 2); //$NON-NLS-1$
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException(Util.getString("NewModelWizard.Container___3") + containerName + Util.getString("NewModelWizard.__does_not_exist._4")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        IContainer container = (IContainer)resource;
        final IFile file = container.getFile(new Path(fileName));
        setExceptionOccurred(false);
        final ModelResource modelResource = ModelerCore.create(file);
        if (modelResource != null) {
            this.newModelResource = modelResource;
            if (wizardPageContributor != null) {
                // This method will assumes the contributor may create a modelAnnotation (i.e. via structural copy, etc...)
                // And we want to set the URI/ModelType after the work.
                processThroughContributor(modelResource, file, monitor);
            } else {
                final ModelAnnotation annotation = modelResource.getModelAnnotation();
                if (annotation != null) {

                    Display.getDefault().syncExec(new Runnable() {
                        public void run() {
                            MetamodelDescriptor descriptor = metamodelSelectionPage.getMetamodelDescriptor();
                            String uri = descriptor.getNamespaceURI();
                            annotation.setPrimaryMetamodelUri(uri);
                            annotation.setModelType(getModelType());

                            final List initializerNames = descriptor.getModelInitializerNames();
                            final int numInitializers = initializerNames.size();
                            String initializerName = null;
                            if (numInitializers == 1) {
                                // Only one, so choose it ...
                                initializerName = (String)initializerNames.get(0);
                            } else if (numInitializers > 1) {
                                // More than one, so give choice to users ...
                                ModelInitializerSelectionDialog dialog = new ModelInitializerSelectionDialog(getShell(),
                                                                                                             descriptor);
                                dialog.setInitialSelection();
                                dialog.open();
                                if (dialog.getReturnCode() == Window.OK && dialog.getResult() != null
                                    && dialog.getResult().length > 0) {
                                    initializerName = (String)dialog.getResult()[0];
                                } else {
                                    // can't cancel, so just use the first one
                                    initializerName = (String)initializerNames.get(0);
                                }
                            }

                            // Release memory
                            System.gc();
                            Thread.yield();

                            if (initializerName != null) {
                                // Run the initializer ...
                                final ModelInitializer initializer = descriptor.getModelInitializer(initializerName);
                                if (initializer != null) {
                                    try {
                                        final IStatus status = initializer.execute(modelResource.getEmfResource());
                                        System.out.println(status);
                                    } catch (Exception e) {
                                        setExceptionOccurred(true);
                                        UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                                    }
                                }
                            }

                            // Release memory
                            System.gc();
                            Thread.yield();

                            // Force save after setting these properties.
                            try {
                                ModelUtilities.saveModelResource(modelResource, monitor, true, this);
                            } catch (Exception e) {
                                setExceptionOccurred(true);
                                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                            }
                            monitor.worked(1);
                            monitor.setTaskName(Util.getString("NewModelWizard.Opening_file_for_editing..._5")); //$NON-NLS-1$

                            ModelEditorManager.openInEditMode(file, true, UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);

                            monitor.worked(1);
                        }
                    });
                }
            }

        }

        // Update Designer Properties if it was provided
        if (this.designerProperties != null && modelResource != null && ModelUtil.isModelFile(modelResource.getEmfResource())) {
            if (ModelUtil.isVirtual(modelResource.getEmfResource())) {
                String viewModelName = ((IFile)modelResource.getCorrespondingResource()).getName();
                DesignerPropertiesUtil.setViewModelName(this.designerProperties, viewModelName);
            } else if (ModelUtil.isPhysical(modelResource.getEmfResource())) {
                String sourceModelName = ((IFile)modelResource.getCorrespondingResource()).getName();
                DesignerPropertiesUtil.setSourceModelName(this.designerProperties, sourceModelName);
            }
        }


        monitor.worked(1);
        // Only open the new file for editing if we know we were successful in creating it.
        if (this.exceptionOccurred) {
            setExceptionOccurred(false);
        }
        monitor.worked(1);
    }

    private void processThroughContributor( final ModelResource modelResource,
                                            final IFile file,
                                            final IProgressMonitor monitor ) throws CoreException {
        final ModelAnnotation annotation = modelResource.getModelAnnotation();
        if (annotation != null) {

            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    MetamodelDescriptor descriptor = metamodelSelectionPage.getMetamodelDescriptor();
                    String uri = descriptor.getNamespaceURI();
                    annotation.setPrimaryMetamodelUri(uri);
                    annotation.setModelType(getModelType());

                    try {
                        wizardPageContributor.doFinish(modelResource, monitor);
                    } catch (Exception e) {
                        setExceptionOccurred(true);
                        // bwpTODO-- display error dialog?
                        Util.log(e);
                    }

                    ModelAnnotation originalModelAnotation = null;
                    try {
                        // Make sure all containers exist
                        // Set the ModelType first to make sure appropriate containers are created or NOT
                        modelResource.getModelAnnotation().setModelType(getModelType());
                        ModelerCore.getModelEditor().getAllContainers(modelResource.getEmfResource());
                        List existingImports = new ArrayList(annotation.getModelImports());
                        // Delete the temp annotation
                        modelResource.getEmfResource().getContents().remove(annotation);
                        // Get the real one (from the original model)
                        originalModelAnotation = modelResource.getModelAnnotation();
                        if (!existingImports.isEmpty()) {
                            if (originalModelAnotation.getModelImports().isEmpty()) {
                                try {
                                    ModelerCore.getModelEditor().addValue(originalModelAnotation,
                                                                          existingImports,
                                                                          originalModelAnotation.getModelImports());
                                } catch (Exception e) {
                                    setExceptionOccurred(true);
                                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                                }
                            } else {
                                // We need to check for duplicate imports
                                List originalImports = originalModelAnotation.getModelImports();
                                for (Iterator iter = existingImports.iterator(); iter.hasNext();) {
                                    ModelImport newImport = (ModelImport)iter.next();
                                    boolean importExists = false;
                                    for (Iterator iter2 = originalImports.iterator(); iter2.hasNext();) {
                                        ModelImport nextImport = (ModelImport)iter2.next();
                                        if (nextImport.getModelLocation().equalsIgnoreCase(newImport.getModelLocation())) {
                                            importExists = true;
                                            break;
                                        }
                                    }
                                    if (!importExists) {
                                        try {
                                            ModelerCore.getModelEditor().addValue(originalModelAnotation,
                                                                                  newImport,
                                                                                  originalModelAnotation.getModelImports());
                                        } catch (Exception e) {
                                            setExceptionOccurred(true);
                                            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                                        }
                                    }
                                }
                            }
                        }
                    } catch (ModelWorkspaceException theException) {
                        setExceptionOccurred(true);
                        ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
                    }
                    // Now let's complete the work.
                    // reset the URI and ModelType to desired
                    // Then organize imports & save
                    if (originalModelAnotation != null) {
                        originalModelAnotation.setPrimaryMetamodelUri(uri);
                        originalModelAnotation.setModelType(getModelType());

                        // Force save after setting these properties.
                        try {
                            ModelUtilities.saveModelResource(modelResource, monitor, true, this);
                        } catch (Exception e) {
                            setExceptionOccurred(true);
                            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                        }
                        monitor.worked(1);
                        monitor.setTaskName(Util.getString("NewModelWizard.Opening_file_for_editing..._5")); //$NON-NLS-1$
                        // Changed to use method that insures Object editor mode is on
                        ModelEditorManager.openInEditMode(file, true, UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
                        monitor.worked(1);
                    }

                }
            });
        }

    }

    private void throwCoreException( String message ) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, "com.metamatrix.modeler.ui", IStatus.OK, message, null); //$NON-NLS-1$
        throw new CoreException(status);
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        this.selection = selection;

    	if( !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		this.selection = new StructuredSelection(newProject);
        	}
        }
    }

    public ModelType getModelType() {
        return metamodelSelectionPage.getSelectedModelType();
    }

    /**
     * Get metamodel type. No constants available since this is metadata-driven. Returns a String indicating the metamodel type.
     * 
     * @return the metamodel type
     */
    public String getMetamodelType() {
        String metamodelType = metamodelSelectionPage.getMetamodelType();
        return metamodelType;
    }

    public static List getModelBuilders( MetamodelDescriptor descriptor,
                                         boolean isVirtual ) {
        ArrayList result = new ArrayList(getContributorList().size());
        // filter the list down to only those that can use the specified descriptor & isVirtual value
        for (Iterator iter = getContributorList().iterator(); iter.hasNext();) {
            NewModelWizardDescriptor desc = (NewModelWizardDescriptor)iter.next();
            if (desc.canBuild(descriptor, isVirtual)) {
                result.add(desc);
            }
        }
        return result;
    }

    static List getContributorList() {
        if (contributorList == null) {
            contributorList = new ArrayList();

            // get the NewModelWizardContributor extension point from the plugin class
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, ID);
            // get the all extensions to the NewModelWizardContributor extension point
            for (IExtension extension : extensionPoint.getExtensions()) {
                String title = extension.getLabel();
                IConfigurationElement[] elements = extension.getConfigurationElements();
                Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
                for (int j = 0; j < elements.length; ++j) {
                    if (elements[j].getName().equals(CLASS)) {
                        String classname = elements[j].getAttribute(NAME);
                        NewModelWizardDescriptor desc = new NewModelWizardDescriptor(ID, classname, bundle, title, elements);
                        contributorList.add(desc);
                        break;
                    }
                }
            }
        }

        return contributorList;
    }

    /**
     * The <code>Wizard</code> implementation of this <code>IWizard</code> method creates all the pages controls using
     * <code>IDialogPage.createControl</code>. Subclasses should reimplement this method if they want to delay creating one or
     * more of the pages lazily. The framework ensures that the contents of a page will be created before attempting to show it.
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        // this.pageContainer = pageContainer;
        super.createPageControls(pageContainer, false);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        if (wizardPageContributor != null) wizardPageContributor.currentPageChanged(getContainer().getCurrentPage());

        if (page == this.createHiddenProjPage) {
            return this.metamodelSelectionPage;
        }

        if (page == metamodelSelectionPage) {

            updatePageQueue(false);
            if (wizardPageArray != null) {
                return wizardPageArray[0];
            }
            return null;

        }
        for (int i = 0; i < wizardPageArray.length; ++i) {
            if (wizardPageArray[i] == page) {
                if (i + 1 < wizardPageArray.length) {
                    if (this.wizardPageContributor instanceof INewModelWizardContributor2) {
                        return ((INewModelWizardContributor2)this.wizardPageContributor).getNextPage(page);
                    }

                    return wizardPageArray[i + 1];
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.metamodelSelectionPage) {
            result = currentPage.isPageComplete();
        } else if (currentPage == this.createHiddenProjPage) {
            result = false;
        } else {
            boolean lastPage = (currentPage == wizardPageArray[wizardPageArray.length - 1]);

            if (!lastPage && (this.wizardPageContributor != null)) {
                result = this.wizardPageContributor.canFinishEarly(currentPage);
            } else {
                result = lastPage && currentPage.isPageComplete();
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPageCount()
     */
    @Override
    public int getPageCount() {
        if (wizardPageArray != null) {
            return wizardPageArray.length + 1;
        }
        return 1;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage( IWizardPage page ) {

        if (wizardPageArray == null || page == this.metamodelSelectionPage) {
            return null;
        }
        if (page == wizardPageArray[0]) {
            return this.metamodelSelectionPage;
        }
        for (int i = 1; i < wizardPageArray.length; ++i) {
            if (page == wizardPageArray[i]) {
                if (this.wizardPageContributor instanceof INewModelWizardContributor2) {
                    return ((INewModelWizardContributor2)this.wizardPageContributor).getPreviousPage(page);
                }

                return wizardPageArray[i - 1];
            }
        }
        return null;
    }

    /**
     * Looks at the metamodel selection page's contributor table to keep wizardPageArray current with the state of the selection.
     * 
     * @param inputChanged
     */
    private void updatePageQueue( boolean inputChanged ) {
        IResource container = metamodelSelectionPage.getTargetContainer();
        IPath targetFilePath = metamodelSelectionPage.getFilePath();
        boolean isVirtual = metamodelSelectionPage.isVirtualSelected();
        MetamodelDescriptor metamodelDescriptor = metamodelSelectionPage.getMetamodelDescriptor();
        NewModelWizardDescriptor descriptor = metamodelSelectionPage.getSelectedBuilder();
        if (descriptor == null) {
            wizardPageArray = null;
            wizardPageContributor = null;
        } else {
            boolean addWizardPages = false;
            // see if we have already built this contributor
            INewModelWizardContributor contributor = (INewModelWizardContributor)contributorPageMap.get(descriptor.getTitle());
            if (contributor == null) {
                // instantiate it and create it's pages
                wizardPageContributor = (INewModelWizardContributor)descriptor.getExtensionClassInstance();
                contributorPageMap.put(descriptor.getTitle(), wizardPageContributor);
                wizardPageContributor.createWizardPages(selection, container, targetFilePath, metamodelDescriptor, isVirtual);
                wizardPageArray = wizardPageContributor.getWizardPages();
                addWizardPages = true;
            } else {
                // determine if this is the current wizardPageContributor
                if (contributor != wizardPageContributor) {
                    // then the pages need to be re-obtained
                    contributor.inputChanged(selection, container, metamodelDescriptor, isVirtual);
                    wizardPageArray = contributor.getWizardPages();
                    addWizardPages = true;
                    wizardPageContributor = contributor;
                } else {
                    // then the current wizardPageArray is fine unless the inputs changed
                    if (inputChanged) {
                        wizardPageContributor.inputChanged(selection, container, metamodelDescriptor, isVirtual);
                        wizardPageArray = wizardPageContributor.getWizardPages();
                        addWizardPages = true;
                    }
                }
            }

            if (addWizardPages) {
                for (int i = 0; i < wizardPageArray.length; ++i) {
                    super.addPage(wizardPageArray[i]);
                }
            }

        }
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performCancel()
     * @since 4.2
     */
    @Override
    public boolean performCancel() {
        // tell the contributor the wizard has been cancelled
        if (this.wizardPageContributor != null) {
            this.wizardPageContributor.doCancel();
        }

        return super.performCancel();
    }

}
