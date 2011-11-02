/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.actions;

import static org.teiid.designer.extension.ExtensionConstants.MED_EXTENSION;
import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionText;
import static org.teiid.designer.extension.ui.Messages.updateMedInRegistryActionToolTip;
import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.REGISTERY_MED_UPDATE_ACTION;
import java.io.InputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistantAdapter;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.editors.ModelExtensionDefinitionEditor;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * 
 */
public final class UpdateRegistryModelExtensionDefinitionAction extends SortableSelectionAction {

    private IFile selectedMed;

    public UpdateRegistryModelExtensionDefinitionAction() {
        super(updateMedInRegistryActionText, SWT.FLAT);
        setImageDescriptor(Activator.getDefault().getImageDescriptor(REGISTERY_MED_UPDATE_ACTION));
        setToolTipText(updateMedInRegistryActionToolTip);
    }

    /**
     * Allow single selection of mxd file
     * 
     * @param selection
     * @return
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals(MED_EXTENSION)) {
                    return true;
                }
            }
        }
        return result;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {

        // Check whether there is currently an open editor for the selected Med
        ModelExtensionDefinitionEditor editor = getOpenEditor(this.selectedMed);
        if (editor != null && editor.isDirty()) {
            // If editor is open and dirty, ask user whether to save
            boolean doContinue = MessageDialog.openQuestion(getShell(),
                                                            Messages.updateMedInRegistryMedDirtyTitle,
                                                            Messages.updateMedInRegistryMedDirtyMsg);
            if (!doContinue) {
                return;
            }
            editor.doSave(new NullProgressMonitor());
        }

        // If the file has any error markers, user is informed to fix them first
        if (RegistryDeploymentValidator.checkProblemMarkers(this.selectedMed)) {
            return;
        }

        ModelExtensionRegistry registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);
        InputStream fileContents = null;
        try {
            fileContents = this.selectedMed.getContents();
        } catch (CoreException e) {
            UTIL.log(NLS.bind(Messages.medFileGetContentsErrorMsg, this.selectedMed.getName()));
        }

        if (fileContents != null) {
            boolean isDeployable = RegistryDeploymentValidator.checkMedDeployable(registry, fileContents);
            // If the URI is not registered, go ahead with registration
            if (isDeployable) {
                // Update the registry using the validated MED
                BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

                    @Override
                    public void run() {
                        internalRun();
                    }
                });
            }
        }
    }

    void internalRun() {
        try {
            if (this.selectedMed != null) {
                addExtensionToRegistry(this.selectedMed);
            }
        } catch (Exception e) {
            UTIL.log(NLS.bind(Messages.medRegistryAddErrorMsg, this.selectedMed.getName()));
        }
    }

    @Override
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {

        // Enable for single selection of mxd
        boolean enable = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IFile) {
                String extension = ((IFile)obj).getFileExtension();
                if (extension != null && extension.equals(MED_EXTENSION)) {
                    this.selectedMed = (IFile)obj;
                    enable = true;
                }
            }
        }
        if (!enable) {
            this.selectedMed = null;
        }
        setEnabled(enable);
    }

    /**
     * Add a ModelExtensionDefinition to the Extension Registry
     * 
     * @param medFile the file containing the med definition
     * @throws Exception throws exception if the add operation failed
     */
    public static void addExtensionToRegistry( IFile medFile ) throws Exception {
        ModelExtensionRegistry registry = (Platform.isRunning() ? ExtensionPlugin.getInstance().getRegistry() : null);

        registry.addDefinition(medFile.getContents(), new ModelExtensionAssistantAdapter());
    }

    /*
     * Find Open Editor for the currently selected ModelExtensionDefinition
     * @param selectedMedFile the mxd file to check
     * @return the currently open editor, null if none open.
     */
    private ModelExtensionDefinitionEditor getOpenEditor( IFile selectedMedFile ) {
        final IWorkbenchWindow window = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        ModelExtensionDefinitionEditor medEditor = null;

        if (window != null) {
            final IWorkbenchPage page = window.getActivePage();

            if (page != null) {
                // look through the open editors and see if there is one available for this model file.
                IEditorReference[] editors = page.getEditorReferences();
                for (int i = 0; i < editors.length; ++i) {

                    IEditorPart editor = editors[i].getEditor(false);
                    if (editor != null) {
                        IEditorInput input = editor.getEditorInput();
                        if (input instanceof IFileEditorInput) {
                            if (selectedMedFile != null && selectedMedFile.equals(((IFileEditorInput)input).getFile())) {
                                // found it;
                                if (editor instanceof ModelExtensionDefinitionEditor) {
                                    medEditor = (ModelExtensionDefinitionEditor)editor;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        return medEditor;
    }

    private static Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

}
