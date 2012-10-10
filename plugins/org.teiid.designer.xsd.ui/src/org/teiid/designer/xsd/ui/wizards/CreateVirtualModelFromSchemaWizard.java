/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.ui.wizards;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.teiid.core.designer.PluginUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;
import org.teiid.designer.ui.viewsupport.RelationalObjectBuilder;
import org.teiid.designer.xsd.ui.ModelerXsdUiConstants;
import org.teiid.designer.xsd.ui.ModelerXsdUiPlugin;


/**
 * @since 8.0
 */
public class CreateVirtualModelFromSchemaWizard extends AbstractWizard {
    public static boolean HEADLESS = false; // Flag to set Wizard to run in headless mode for testing

    private static final String TITLE = ModelerXsdUiConstants.Util.getString("CreateVirtualModelFromSchemaWizard.title"); //$NON-NLS-1$

    // ============================================================================================================================

    private final PluginUtil Util = ModelerXsdUiConstants.Util;

    // The page for driving the user options.
    protected GlobalEntitiesPage complexSchemaTypesPage;

    private MultiStatus status;

    // The current workspace selection
    protected ISelection selection;

    private IWizardPage[] wizardPageArray;
    private Resource selectedResource;
    public ModelResource selectedModelResource;

    /**
     * Constructor for NewModelWizard.
     */
    public CreateVirtualModelFromSchemaWizard() {
        super(ModelerXsdUiPlugin.getDefault(), TITLE, null);
        setNeedsProgressMonitor(false);
    }

    // ************************** Wizard Methods **************************

    /**
     * Adding the page to the wizard.
     */
    @Override
    public void addPages() {
        complexSchemaTypesPage = new GlobalEntitiesPage(this.selectedResource);
        addPage(complexSchemaTypesPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as
     * execution context.
     */
    @Override
    public boolean finish() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
			public void run( final IProgressMonitor monitor ) {
                // Get the options and execute the build.
                doFinish(monitor);
            }
        };

        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false,
                                                        false,
                                                        CreateVirtualModelFromSchemaWizard.this.getWindowTitle(),
                                                        CreateVirtualModelFromSchemaWizard.this);
        try {
            new ProgressMonitorDialog(getShell()).run(false, false, op);
        } catch (Throwable err) {
            Util.log(IStatus.ERROR, err, err.getMessage());
        } finally {
            // This operation is NOT undoable or significant... ALWAYS commit to ensure
            // Nothing is left hanging.
            if (startedTxn) {
                ModelerCore.commitTxn();
            }
        }

        return true;
    }

    /**
     * The worker method. It will find the container, create the file(s) - Made this method public to allow for headless testing.
     * 
     * @param IPRogressMonitor - The progress monitor for this operation.
     */

    public void doFinish( final IProgressMonitor monitor ) {
        if (complexSchemaTypesPage == null) {
            final String msg = Util.getString("CreateVirtualModelFromSchemaWizard.noInit"); //$NON-NLS-1$
            addStatus(IStatus.ERROR, msg, null);
            return;
        }
        // Let's create the Virtual Model given the path and file name

        IPath modelPath = complexSchemaTypesPage.getFilePath();
        IProject targetProject = complexSchemaTypesPage.getTargetProject();
        Collection typesList = complexSchemaTypesPage.getTypesToConvert();

        if (modelPath != null && targetProject != null && targetProject.exists() && !typesList.isEmpty()) {
            ModelResource newModel = constructVirtualModel(targetProject, modelPath);
            if (newModel != null) {
                try {
                    final GenerateVirtualFromXsdHelper helper = new GenerateVirtualFromXsdHelper(getStatus(), newModel.getEmfResource(), typesList);
            		helper.doBuild(monitor);
            	
            		ModelUtilities.saveModelResource(newModel, monitor, true, this);
            		ModelEditorManager.activate(newModel, true);
            	} catch (Exception err) {
                    addStatus(IStatus.ERROR, err.getMessage(), err);
                }
            }
        }
    }

    public MultiStatus getStatus() {
        if (status == null) {
            status = new MultiStatus(ModelerXsdUiConstants.PLUGIN_ID, 0,
                                     Util.getString("CreateVirtualModelFromSchemaWizard.status"), null); //$NON-NLS-1$
        }

        return status;
    }

    private void addStatus( final int severity,
                            final String message,
                            final Throwable ex ) {
        final Status sts = new Status(severity, ModelerXsdUiConstants.PLUGIN_ID, 0, message, ex);
        getStatus().add(sts);
    }

    /**
     * Create a Virtual Model with the supplied name, in the desired project
     * 
     * @param targetProj the project resource under which to create the model
     * @param modelName the model name to create
     * @return the newly-created ModelResource
     */
    private ModelResource constructVirtualModel( IProject targetProject,
                                                 IPath modelPath ) {
        // REmove the first project segment to make relative
        IPath relativeModelPath = modelPath.removeFirstSegments(1);
        final IFile modelFile = targetProject.getFile(relativeModelPath);
        // Create the file
        final ModelResource resrc = ModelerCore.create(modelFile);

        // Set the metamodel and model type and Save
        try {
            final RelationalObjectBuilder builder = new RelationalObjectBuilder(resrc.getEmfResource());
            resrc.getModelAnnotation().setPrimaryMetamodelUri(builder.getRelationalPackageURI());
            resrc.getModelAnnotation().setModelType(ModelType.VIRTUAL_LITERAL);
            resrc.save(null, true);
        } catch (ModelWorkspaceException mwe) {
            addStatus(IStatus.ERROR, mwe.getMessage(), mwe);
        }
        return resrc;
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        this.selection = selection;
        if (SelectionUtilities.isSingleSelection(selection)) {
            final Object sel = SelectionUtilities.getSelectedObject(selection);
            if (sel instanceof IFile) {
                final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile)sel);
                this.selectedModelResource = modelResource;
                try {
                    this.selectedResource = modelResource.getEmfResource();
                } catch (ModelWorkspaceException err) {
                    Util.log(err);
                }
            } else if (sel instanceof Resource) {
                // This is for headless testing...
                this.selectedResource = (Resource)sel;
            }
        }

        if (this.selectedResource == null) {
            // DO NOTHING
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        if (page == complexSchemaTypesPage) {
            return null;
        }

        for (int i = 0; i < wizardPageArray.length; ++i) {
            if (wizardPageArray[i] == page) {
                if (i + 1 < wizardPageArray.length) {
                    return wizardPageArray[i + 1];
                }
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * This Wizard can finish if the Options page is complete.
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.complexSchemaTypesPage) {
            result = currentPage.isPageComplete();
        } else {
            boolean lastPage = (currentPage == wizardPageArray[wizardPageArray.length - 1]);
            result = lastPage && currentPage.isPageComplete();
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

        if (wizardPageArray == null || page == this.complexSchemaTypesPage) {
            return null;
        }
        if (page == wizardPageArray[0]) {
            return this.complexSchemaTypesPage;
        }
        for (int i = 1; i < wizardPageArray.length; ++i) {
            if (page == wizardPageArray[i]) {
                return wizardPageArray[i - 1];
            }
        }
        return null;
    }

}
