/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.actions;

import static com.metamatrix.modeler.ui.UiConstants.Util;
import static org.teiid.designer.extension.ui.UiConstants.UTIL;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * RegistryDeploymentValidator - has static methods for checking whether a ModelExtensionDefinition can safely be deployed to the
 * extension registry.
 */
public class RegistryDeploymentValidator {

    /**
     * Determine if the supplied IFile has any outstanding problem markers. If markers are found, dialogs are displayed.
     * 
     * @param mxdFile the file containing the extension definition
     * @return <code>true</code> if problems were found
     */
    private static boolean checkProblemMarkers( IFile mxdFile ) {
        IMarker[] markers = null;
        boolean errorOccurred = false;

        try {
            markers = mxdFile.findMarkers(UiConstants.ExtensionIds.PROBLEM_MARKER, false, IResource.DEPTH_INFINITE);
        } catch (CoreException ex) {
            Util.log(ex);
            errorOccurred = true;
        }

        // Notify user if error getting markers
        if (errorOccurred) {
            MessageDialog.openError(getShell(), Messages.checkMedProblemMarkersErrorTitle, Messages.checkMedProblemMarkersErrorMsg);
            return true;
        }

        if (markers.length > 0) {
            MessageDialog.openError(getShell(), Messages.checkMedProblemMarkersHasErrorsTitle,
                                    Messages.checkMedProblemMarkersHasErrorsMsg);
            return true;
        }

        return false;
    }

    /**
     * Find Open Editor for the currently selected ModelExtensionDefinition
     * 
     * @param selectedMedFile the mxd file to check
     * @return the currently open editor or <code>null</code> if none open
     */
    private static IEditorPart getOpenEditor( IFile selectedMedFile ) {
        final IWorkbenchWindow window = Activator.getDefault().getCurrentWorkbenchWindow();

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
                            if ((selectedMedFile != null) && selectedMedFile.equals(((IFileEditorInput)input).getFile())) {
                                return editor;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Determine if the supplied File contains a valid ModelExtensionDefinition. 'valid' meaning that the file contents are parsable
     * against the MED schema. If the contents are not valid, a dialog is displayed.
     * 
     * @param mxdContents the file contents of the extension definition
     * @param showInfoDialog flag indicates whether info dialog should be shown to user
     * @return the ModelExtensionDefinition, null if invalid
     */
    private static ModelExtensionDefinition parseMed( InputStream mxdContents,
                                                      boolean showInfoDialog ) {
        ModelExtensionDefinition med = null;
        try {
            med = ExtensionPlugin.getInstance().parse(mxdContents);
        } catch (Exception e) {
            UTIL.log(Messages.medFileParseErrorMsg);
        }

        // Notify user if parse fails
        if (med == null && showInfoDialog) {
            showMedNotValidDialog();
        }

        return med;
    }

    /**
     * Get the ModelExtensionDefinition with the specified NS Prefix from the registry.
     * 
     * @param registry the extension registry
     * @param nsPrefix the ModelExtensionDefinition Namespace Prefix
     * @return the ModelExtensionDefinition, null if not found
     */
    private static ModelExtensionDefinition getRegisteredMedWithNSPrefix( ModelExtensionRegistry registry,
                                                                          String nsPrefix ) {
        return registry.getDefinition(nsPrefix);
    }

    /**
     * Get the ModelExtensionDefinition with the specified NS URI from the registry.
     * 
     * @param registry the extension registry
     * @param nsUri the ModelExtensionDefinition Namespace URI
     * @return the ModelExtensionDefinition, null if not found
     */
    private static ModelExtensionDefinition getRegisteredMedWithNSUri( ModelExtensionRegistry registry,
                                                                       String nsUri ) {
        return registry.getDefinitionWithNSUri(nsUri);
    }

    /**
     * Show the 'MED not parsable' info dialog
     */
    private static void showMedNotValidDialog() {
        MessageDialog.openInformation(getShell(), Messages.registerMedActionInvalidMedTitle,
                                      Messages.registerMedActionInvalidMedMsg);
    }

    /**
     * Show the 'MED with same NS prefix already registered' info dialog
     */
    private static void showMedNSPrefixAlreadyRegisteredDialog() {
        MessageDialog.openInformation(getShell(), Messages.registerMedActionNamespacePrefixRegisteredTitle,
                                      Messages.registerMedActionNamespacePrefixRegisteredMsg);
    }

    /**
     * Show the 'MED with same NS prefix already registered' confirmation dialog. This will ask user if they want to update the
     * registered MED.
     */
    private static boolean showMedNSPrefixAlreadyRegisteredDoUpdateDialog() {
        return MessageDialog.openConfirm(getShell(), Messages.registerMedActionNamespacePrefixRegisteredTitle,
                                         Messages.registerMedActionNamespacePrefixRegisteredDoUpdateMsg);
    }

    /**
     * Show the 'MED with same NS URI already registered' info dialog
     */
    private static void showMedNSUriAlreadyRegisteredDialog() {
        MessageDialog.openInformation(getShell(), Messages.registerMedActionNamespaceUriRegisteredTitle,
                                      Messages.registerMedActionNamespaceUriRegisteredMsg);
    }

    /**
     * Show the 'MED NSUri conflicts with Built-In' info dialog
     */
    private static void showMedNSUriConflictsWBuiltInDialog() {
        MessageDialog.openInformation(getShell(), Messages.registerMedActionNamespaceUriConflictsWBuiltInTitle,
                                      Messages.registerMedActionNamespaceUriConflictsWBuiltInMsg);
    }

    /**
     * Show the 'MED NSPrefix conflicts with Built-In' info dialog
     */
    private static void showMedNSPrefixConflictsWBuiltInDialog() {
        MessageDialog.openInformation(getShell(), Messages.registerMedActionNamespacePrefixConflictsWBuiltInTitle,
                                      Messages.registerMedActionNamespacePrefixConflictsWBuiltInMsg);
    }

    public static void deploy( final IFile medToDeploy ) {
        CoreArgCheck.isNotNull(medToDeploy, "medToDeploy is null"); //$NON-NLS-1$

        // Check whether there is currently an open editor for the selected Med
        IEditorPart editor = getOpenEditor(medToDeploy);

        // If editor is open and dirty, ask user whether to save
        if ((editor != null) && editor.isDirty()) {
            if (!MessageDialog.openQuestion(getShell(), Messages.updateMedInRegistryMedDirtyTitle,
                                            Messages.updateMedInRegistryMedDirtyMsg)) {
                return;
            }

            editor.doSave(new NullProgressMonitor());
        }

        // If the file has any error markers, user is informed to fix them first
        if (RegistryDeploymentValidator.checkProblemMarkers(medToDeploy)) {
            return;
        }

        ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        InputStream fileContents = null;

        try {
            fileContents = medToDeploy.getContents();
        } catch (CoreException e) {
            UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.medFileGetContentsErrorMsg, medToDeploy.getName()));
        }

        if (fileContents != null) {
            // Parse file contents to get the MED. Show info dialog if parse errors.
            ModelExtensionDefinition med = null;

            try {
                ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(ExtensionPlugin.getInstance()
                                                                                                          .getMedSchema());
                med = parser.parse(fileContents, ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());

                if (!parser.getErrors().isEmpty()) {
                    MessageDialog.openError(getShell(), Messages.registerMedActionFailedTitle,
                                            NLS.bind(Messages.medFileParseErrorMsg, medToDeploy.getName()));
                    return;
                }
            } catch (Exception e) {
                UTIL.log(e);
                MessageDialog.openError(getShell(), Messages.registerMedActionFailedTitle, Messages.registerMedActionFailedMsg);
                return;
            }

            // Continue checks on parsable MED
            if (med != null) {
                ModelExtensionDefinition medNSPrefixMatch = RegistryDeploymentValidator.getRegisteredMedWithNSPrefix(registry,
                                                                                                                     med.getNamespacePrefix());
                ModelExtensionDefinition medNSUriMatch = RegistryDeploymentValidator.getRegisteredMedWithNSUri(registry,
                                                                                                               med.getNamespaceUri());

                boolean nsPrefixConflict = false;
                boolean nsUriConflict = false;
                boolean nsPrefixAndUriConflictSameMed = false;
                boolean nsPrefixConflictMedBuiltIn = false;
                boolean nsUriConflictMedBuiltIn = false;

                if (medNSPrefixMatch != null) {
                    nsPrefixConflict = true;
                    nsPrefixConflictMedBuiltIn = medNSPrefixMatch.isBuiltIn();
                }
                if (medNSUriMatch != null) {
                    nsUriConflict = true;
                    nsUriConflictMedBuiltIn = medNSUriMatch.isBuiltIn();
                }
                if (nsPrefixConflict && nsUriConflict && medNSPrefixMatch.equals(medNSUriMatch))
                    nsPrefixAndUriConflictSameMed = true;

                // No conflicts - add it to the registry
                if (!nsPrefixConflict && !nsUriConflict) {
                    // Add the selected Med
                    BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                        @Override
                        public void run() {
                            internalRun(medToDeploy, false);
                        }
                    });
                    // If the NS Prefix conflicts with a Built-in, prompt user to fix
                } else if (nsPrefixConflictMedBuiltIn) {
                    RegistryDeploymentValidator.showMedNSPrefixConflictsWBuiltInDialog();
                    // If the NS URI conflicts with a Built-in, prompt user to fix
                } else if (nsUriConflictMedBuiltIn) {
                    RegistryDeploymentValidator.showMedNSUriConflictsWBuiltInDialog();
                    // If there is (1) just a NS Prefix Conflict or (2) NS Prefix AND URI, but they are same MED, prompt user
                    // whether to update
                } else if (nsPrefixConflict && (!nsUriConflict || (nsUriConflict && nsPrefixAndUriConflictSameMed))) {
                    // Do not re-deploy the same MED
                    if (med.equals(medNSPrefixMatch)) {
                        RegistryDeploymentValidator.showMedNSPrefixAlreadyRegisteredDialog();
                    } else {
                        boolean doUpdate = RegistryDeploymentValidator.showMedNSPrefixAlreadyRegisteredDoUpdateDialog();
                        if (doUpdate) {
                            // Add the selected Med
                            BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
                                @Override
                                public void run() {
                                    internalRun(medToDeploy, true);
                                }
                            });
                        }
                    }
                    // If there is a NS URI Conflict, prompt user to fix it
                } else if (nsUriConflict) {
                    RegistryDeploymentValidator.showMedNSUriAlreadyRegisteredDialog();
                }
            }
        }
    }

    static void internalRun( IFile medToRegister,
                             boolean isUpdate ) {
        boolean wasAdded = true;

        try {
            if (isUpdate) {
                updateExtensionInRegistry(medToRegister);
            } else {
                addExtensionToRegistry(medToRegister);
            }
        } catch (Exception e) {
            wasAdded = false;
            UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.medRegistryAddErrorMsg, medToRegister.getName()));
            MessageDialog.openInformation(getShell(), Messages.registerMedActionFailedTitle, Messages.registerMedActionFailedMsg);
        }

        if (wasAdded) {
            MessageDialog.openInformation(getShell(), Messages.registerMedActionSuccessTitle, Messages.registerMedActionSuccessMsg);
        }
    }

    /**
     * Add a ModelExtensionDefinition to the Extension Registry
     * 
     * @param medFile the file containing the med definition
     * @throws Exception throws exception if the add operation failed
     */
    private static void addExtensionToRegistry( IFile medFile ) throws Exception {
        ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();
        registry.addDefinition(medFile.getContents(), ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());
    }

    /**
     * If a MED with the same NS Prefix is already registered, it will be removed and replaced with the supplied MED
     * 
     * @param medFile the file containing the med definition
     * @throws Exception throws exception if the add operation failed
     */
    private static void updateExtensionInRegistry( IFile medFile ) throws Exception {
        ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();

        // If MED with this prefix is registered, remove it first
        ModelExtensionDefinition med = RegistryDeploymentValidator.parseMed(medFile.getContents(), false);
        if (registry.isNamespacePrefixRegistered(med.getNamespacePrefix())) {
            registry.removeDefinition(med.getNamespacePrefix());
        }

        // Add the supplied MED
        addExtensionToRegistry(medFile);
    }

    private static Shell getShell() {
        return Activator.getDefault().getCurrentWorkbenchWindow().getShell();
    }

}
