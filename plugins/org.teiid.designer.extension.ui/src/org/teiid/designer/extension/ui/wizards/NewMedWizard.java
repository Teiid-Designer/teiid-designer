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
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.TempInputStream;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.ui.PluginConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.wizard.AbstractWizard;
import org.teiid.designer.ui.common.wizard.NoOpenProjectsWizardPage;
import org.teiid.designer.ui.viewsupport.ModelerUiViewUtils;


/**
 * NewMedWizard - handles creating of new MEDs
 */
public final class NewMedWizard extends AbstractWizard
 implements INewWizard, CoreStringUtil.Constants {

    private NewMedMainPage newMedMainPage;
    private NewMedDetailsPage newMedDetailsPage;
    private final ModelExtensionDefinition medBeingCopied;

    /**
     * @since 7.6
     */
    public NewMedWizard() {
        this(Messages.newMedWizardTitle, null);
    }

    /**
     * @param wizardTitle the window title (should not be <code>null</code> or empty)
     * @param medBeingCopied the MED being copied (can be <code>null</code>)
     * @since 7.6
     */
    public NewMedWizard(String wizardTitle,
                        ModelExtensionDefinition medBeingCopied) {
        super(UiPlugin.getDefault(), wizardTitle, null);
        this.medBeingCopied = medBeingCopied;
    }

    ModelExtensionDefinition accessMed() {
        return this.medBeingCopied;
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
            	TempInputStream medInputStream = null;
            	
                try {
                    // Get Folder Location and Name of the file to create
                    final IContainer folderLoc = NewMedWizard.this.newMedMainPage.getFolderLocation();
                    final String medName = NewMedWizard.this.newMedMainPage.getMedName();
                    final IFile createdMedFile = folderLoc.getFile(new Path(medName));
                    final ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
                    final ModelExtensionDefinition med = createDefaultMed();

                    final String namespacePrefix = NewMedWizard.this.newMedDetailsPage.getNamespacePrefix();
                    final String namespaceUri = NewMedWizard.this.newMedDetailsPage.getNamespaceUri();
                    final String metamodelUri = NewMedWizard.this.newMedDetailsPage.getMetamodelUri();
                    final int versionInt = NewMedWizard.this.newMedDetailsPage.getVersionInt();
                    final String description = NewMedWizard.this.newMedDetailsPage.getDescription();
                    final Collection<String> supportedModelTypes = NewMedWizard.this.newMedDetailsPage.getSupportedModelTypes();

                    med.setNamespacePrefix(namespacePrefix);
                    med.setNamespaceUri(namespaceUri);
                    med.setMetamodelUri(metamodelUri);
                    med.setVersion(versionInt);
                    med.setDescription(description);
                    for (String modelType : supportedModelTypes) {
                        med.addModelType(modelType);
                    }

                    // if copying an existing MED copy the properties
                    if (accessMed() != null) {
                        final ModelExtensionDefinition medToCopy = accessMed();

                        for (final Entry<String, Collection<ModelExtensionPropertyDefinition>> entry : medToCopy.getPropertyDefinitions().entrySet()) {
                            final String metaclass = entry.getKey();

                            for (final ModelExtensionPropertyDefinition propDefn : entry.getValue()) {
                                ModelExtensionPropertyDefinition copiedProp = (ModelExtensionPropertyDefinition)propDefn.clone();
                                copiedProp.setNamespaceProvider(med);
                                med.addPropertyDefinition(metaclass, copiedProp);
                            }
                        }
                    }

                    medInputStream = medWriter.writeAsStream(med);

                    createdMedFile.create(medInputStream.getRealInputStream(), false, monitor);
                    folderLoc.refreshLocal(IResource.DEPTH_INFINITE, monitor);

                    // open editor - if checkbox is selected
                    if (NewMedWizard.this.newMedMainPage.openInEditorChecked()) {
                        IWorkbenchPage page = UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage();
                        IDE.openEditor(page, createdMedFile);
                    }
                    
                    medInputStream.getRealInputStream().close();
                } catch (final Exception err) {
                    throw new InvocationTargetException(err);
                } finally {
                	medInputStream.deleteTempFile();
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
    public void init(final IWorkbench workbench,
                     final IStructuredSelection selection) {

        IContainer folderLocation = null;
        // Get folder from selection
        if (selection != null && !selection.isEmpty()) {
            folderLocation = ModelUtil.getContainer(selection.getFirstElement());
            // If no container was selected, set to the first open project found. user can re-select if desired.
        } else {
            if (!ModelerUiViewUtils.workspaceHasOpenModelProjects()) {
                IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();

                if (newProject != null) {
                    folderLocation = newProject;
                }
            }
            Collection<IProject> openModelProjects = DotProjectUtils.getOpenModelProjects();
            if( !openModelProjects.isEmpty() ) {
            	folderLocation = openModelProjects.iterator().next();
            }
        }



        if (folderLocation == null || !folderInModelProject(folderLocation)) {
            // Create empty page
            addPage(NoOpenProjectsWizardPage.getStandardPage());
        } else {
            newMedMainPage = new NewMedMainPage(folderLocation);
            newMedDetailsPage = new NewMedDetailsPage(this.medBeingCopied);
            addPage(newMedMainPage);
            addPage(newMedDetailsPage);
        }

    }

    private boolean folderInModelProject(IContainer folderLoc) {
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

    Composite createEmptyPageControl(final Composite parent) {
        return new Composite(parent, SWT.NONE);
    }

}
