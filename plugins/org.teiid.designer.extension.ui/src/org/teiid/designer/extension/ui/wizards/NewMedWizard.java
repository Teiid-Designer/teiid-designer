/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * NewMedWizard - handles creating of new MEDs
 */
public final class NewMedWizard extends AbstractWizard
 implements INewWizard, CoreStringUtil.Constants {

    IFile createdMedFile; // the file that was saved

    private NewMedMainPage newMedMainPage;
    private NewMedDetailsPage newMedDetailsPage;
    private ModelExtensionDefinition initialMed;

    /**
     * @since 7.6
     */
    public NewMedWizard() {
        this(Messages.newMedWizardTitle, null);
    }

    /**
     * @since 7.6
     */
    public NewMedWizard( ModelExtensionDefinition med ) {
        this(Messages.newMedWizardTitle, med);
    }

    /**
     * @since 7.6
     */
    public NewMedWizard( String wizardTitle,
                         ModelExtensionDefinition med ) {
        super(UiPlugin.getDefault(), wizardTitle, null);
        this.initialMed = med;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 7.6
     */
    @Override
    public boolean finish() {
        // create MED resource
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            /**
             * {@inheritDoc}
             *
             * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
             */
            @Override
            @SuppressWarnings("unchecked")
			public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                try {
                    // Get Folder Location and Name of the file to create
                    IContainer folderLoc = NewMedWizard.this.newMedMainPage.getFolderLocation();
                    String medName = NewMedWizard.this.newMedMainPage.getMedName();

                    // Get File
                    createdMedFile = folderLoc.getFile(new Path(medName));

                    ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
                    InputStream medInputStream = null;

                    // if no Med was supplied, Create a default Med and set options from second page.
                    if (NewMedWizard.this.initialMed == null) {
                        ModelExtensionDefinition med = createDefaultMed();

                        String namespacePrefix = NewMedWizard.this.newMedDetailsPage.getNamespacePrefix();
                        String namespaceUri = NewMedWizard.this.newMedDetailsPage.getNamespaceUri();
                        String metamodelUri = NewMedWizard.this.newMedDetailsPage.getMetamodelUri();
                        int versionInt = NewMedWizard.this.newMedDetailsPage.getVersionInt();
                        String description = NewMedWizard.this.newMedDetailsPage.getDescription();
                        Collection<String> supportedModelTypes = NewMedWizard.this.newMedDetailsPage.getSupportedModelTypes();

                        med.setNamespacePrefix(namespacePrefix);
                        med.setNamespaceUri(namespaceUri);
                        med.setMetamodelUri(metamodelUri);
                        med.setVersion(versionInt);
                        med.setDescription(description);
                        for (String modelType : supportedModelTypes) {
                            med.addModelType(modelType);
                        }
                        medInputStream = medWriter.writeAsStream(med);
                        // If MED was supplied, use it - setting the modifiable values
                    } else {
                        String namespacePrefix = NewMedWizard.this.newMedDetailsPage.getNamespacePrefix();
                        String namespaceUri = NewMedWizard.this.newMedDetailsPage.getNamespaceUri();
                        String description = NewMedWizard.this.newMedDetailsPage.getDescription();
                        NewMedWizard.this.initialMed.setNamespacePrefix(namespacePrefix);
                        NewMedWizard.this.initialMed.setNamespaceUri(namespaceUri);
                        NewMedWizard.this.initialMed.setDescription(description);
                        medInputStream = medWriter.writeAsStream(NewMedWizard.this.initialMed);
                    }

                    createdMedFile.create(medInputStream, false, monitor);
                    folderLoc.refreshLocal(IResource.DEPTH_INFINITE, monitor);

                    // open editor - if checkbox is selected
                    if (NewMedWizard.this.newMedMainPage.openInEditorChecked()) {
                        IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
                        IDE.openEditor(page, createdMedFile);
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
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
            WidgetUtil.showError(Messages.newMedWizardCreateFileErrorMsg);
            return false;
        }
    }

    /**
     * @return a default ModelExtensionDefinition (never <code>null</code>)
     */
    ModelExtensionDefinition createDefaultMed() {
        ModelExtensionAssistant assistant = ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant();
        ModelExtensionDefinition newMed = new ModelExtensionDefinition(assistant);
        return newMed;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     * @since 7.6
     */
    @Override
    public void init( final IWorkbench workbench,
                      final IStructuredSelection selection ) {

        IContainer folderLocation = null;
        // Get folder from selection
        if (selection != null && !selection.isEmpty()) {
            folderLocation = ModelUtil.getContainer(selection.getFirstElement());
            // If no container was selected, set to the first open project found. user can re-select if desired.
        } else {
            folderLocation = getWorkspaceOpenProject();
        }
        

    	if( !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		folderLocation = newProject;
        	}
        }

        if (folderLocation != null && !folderInModelProject(folderLocation)) {
            // Create empty page
            WizardPage page = new WizardPage(NewMedWizard.class.getSimpleName(), null, null) {
                public void createControl( final Composite parent ) {
                    setControl(createEmptyPageControl(parent));
                }
            };
            page.setMessage(Messages.newMedWizardNotModelProjMsg, IMessageProvider.ERROR);
            page.setPageComplete(false);
            addPage(page);
        } else {
            newMedMainPage = createNewMedMainPage(folderLocation);
            newMedDetailsPage = createNewMedDetailsPage();
            addPage(newMedMainPage);
            addPage(newMedDetailsPage);
        }

    }

    protected NewMedMainPage createNewMedMainPage( final IContainer folderLocation ) {
        return new NewMedMainPage(folderLocation);
    }

    protected NewMedDetailsPage createNewMedDetailsPage() {
        return new NewMedDetailsPage(this.initialMed);
    }

    /*
     * Get first open / non-hidden project from the workspace
     */
    private IProject getWorkspaceOpenProject() {
        IProject openProj = null;

        for (IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
            try {
                boolean result = proj.isOpen() && !proj.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)
                                 && proj.hasNature(ModelerCore.NATURE_ID);
                if (result) {
                    openProj = proj;
                    break;
                }
            } catch (CoreException e) {
                UiConstants.Util.log(e);
            }
        }
        return openProj;
    }

    public IFile getCreatedMedFile() {
        return this.createdMedFile;
    }

    private boolean folderInModelProject( IContainer folderLoc ) {
        boolean result = false;

        if (folderLoc != null) {
            IProject project = folderLoc.getProject();
            try {
                if (project != null && project.getNature(PluginConstants.MODEL_PROJECT_NATURE_ID) != null) {
                    result = true;
                }
            } catch (CoreException ex) {
                ModelerCore.Util.log(IStatus.ERROR, ex, ex.getMessage());
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * @since 7.6
     */
    @Override
    public boolean canFinish() {
        return super.canFinish();
    }

    Composite createEmptyPageControl( final Composite parent ) {
        return new Composite(parent, SWT.NONE);
    }

}
