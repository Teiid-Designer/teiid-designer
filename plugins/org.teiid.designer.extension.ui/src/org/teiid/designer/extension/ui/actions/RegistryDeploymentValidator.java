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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;

/**
 * RegistryDeploymentValidator - has static methods for checking whether a ModelExtensionDefinition can safely be deployed to the
 * extension registry.
 */
public class RegistryDeploymentValidator {

    /**
     * Determine if the supplied IFile has any outstanding problem markers. If markers are found, dialogs are displayed.
     * 
     * @param mxdFile the IFile containing the extension definition
     * @return 'true' if problems were found, 'false' if not.
     */
    public static boolean checkProblemMarkers( IFile mxdFile ) {
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
            MessageDialog.openWarning(getShell(),
                                      Messages.checkMedProblemMarkersErrorTitle,
                                      Messages.checkMedProblemMarkersErrorMsg);
            return true;
        }

        if (markers.length > 0) {
            MessageDialog.openInformation(getShell(),
                                          Messages.checkMedProblemMarkersHasErrorsTitle,
                                          Messages.checkMedProblemMarkersHasErrorsMsg);
            return true;
        }

        return false;
    }

    /**
     * Determine if the supplied File contains a valid ModelExtensionDefinition. 'valid' meaning that the file contents are
     * parsable against the MED schema. If the contents are not valid, a dialog is displayed.
     * 
     * @param mxdContents the file contents of the extension definition
     * @param showInfoDialog flag indicates whether info dialog should be shown to user
     * @return the ModelExtensionDefinition, null if invalid
     */
    public static ModelExtensionDefinition parseMed( InputStream mxdContents,
                                                   boolean showInfoDialog ) {
        ModelExtensionDefinition med = null;
        try {
            ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(
                                                                                       ExtensionPlugin.getInstance().getMedSchema());
            med = parser.parse(mxdContents, ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());
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
     * Determine if the supplied InputStream contains a valid and deployable ModelExtensionDefinition. 'valid' meaning that the
     * file contents are parsable against the MED schema. 'deployable' meaning that the med namespace prefix and URI are not
     * already registered. Info dialogs can be displayed, based on the showInfoDialog setting
     * 
     * @param registry the extension registry
     * @param mxdContents the file contents of the extension definition
     * @param showInfoDialog flag indicates whether info dialog should be shown to user
     * @return 'true' if the MED is deployable, 'false' if not.
     */
    public static boolean isMedDeployable( ModelExtensionRegistry registry,
                                           InputStream mxdContents,
                                           boolean showInfoDialog ) {
        boolean isDeployable = false;
        ModelExtensionDefinition med = parseMed(mxdContents, showInfoDialog);
        if (med != null) {
            boolean isNamespacePrefixRegistered = isMedNamespacePrefixRegistered(registry,
                                                                                 med.getNamespacePrefix(),
                                                                                 showInfoDialog);
            if (!isNamespacePrefixRegistered) {
                isDeployable = !isMedNamespaceUriRegistered(registry, med.getNamespaceUri(), showInfoDialog);
            }
        }
        return isDeployable;
    }

    /**
     * Determine if the supplied InputStream contains a valid/parsable Model Extension Definition. Validity is determined whether
     * it can be parsed successfully using the MED schema.
     * 
     * @param mxdContents the file contents of the extension definition
     * @param showInfoDialog flag indicates whether info dialog should be shown to user
     * @return 'true' if a matching namespacePrefix is already registered, 'false' if not
     */
    public static boolean isMedParsable( InputStream mxdContents,
                                         boolean showInfoDialog ) {
        ModelExtensionDefinition med = parseMed(mxdContents, showInfoDialog);
        if (med != null) {
            return true;
        }
        return false;
    }

    /**
     * Check the ModelExtensionDefinition namespace prefix to see if an extension with the same prefix has already been deployed
     * to the registry. If there is a conflict, a dialog is displayed.
     * 
     * @param registry the extension registry
     * @param nsPrefix the ModelExtensionDefinition Namespace Prefix
     * @param showInfoDialog flag indicates whether info dialog should be shown to user
     * @return 'true' if a matching namespacePrefix is already registered, 'false' if not
     */
    public static boolean isMedNamespacePrefixRegistered( ModelExtensionRegistry registry,
                                                          String nsPrefix,
                                                          boolean showInfoDialog ) {
        boolean namespacePrefixRegistered = registry.isNamespacePrefixRegistered(nsPrefix);

        // Notify user if namespace already registered
        if (namespacePrefixRegistered && showInfoDialog) {
            showMedNSPrefixAlreadyRegisteredDialog();
        }
        return namespacePrefixRegistered;
    }

    /**
     * Check the ModelExtensionDefinition namespace URI to see if an extension with the same URI has already been deployed to the
     * registry. If there is a conflict, a dialog is displayed.
     * 
     * @param registry the extension registry
     * @param nsUri the ModelExtensionDefinition Namespace URI
     * @param showInfoDialog flag indicates whether info dialog should be shown to user
     * @return 'true' if a matching namespaceUri is already registered, 'false' if not
     */
    public static boolean isMedNamespaceUriRegistered( ModelExtensionRegistry registry,
                                                       String nsUri,
                                                       boolean showInfoDialog ) {
        boolean namespaceUriRegistered = registry.isNamespaceUriRegistered(nsUri);

        // Notify user if namespace URI already registered
        if (namespaceUriRegistered && showInfoDialog) {
            showMedNSUriAlreadyRegisteredDialog();
        }
        return namespaceUriRegistered;
    }

    /**
     * Get the ModelExtensionDefinition with the specified NS Prefix from the registry.
     * 
     * @param registry the extension registry
     * @param nsPrefix the ModelExtensionDefinition Namespace Prefix
     * @return the ModelExtensionDefinition, null if not found
     */
    public static ModelExtensionDefinition getRegisteredMedWithNSPrefix( ModelExtensionRegistry registry,
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
    public static ModelExtensionDefinition getRegisteredMedWithNSUri( ModelExtensionRegistry registry,
                                                                      String nsUri ) {
        return registry.getDefinitionWithNSUri(nsUri);
    }

    /**
     * Show the 'MED not parsable' info dialog
     */
    public static void showMedNotValidDialog() {
        MessageDialog.openInformation(getShell(),
                                      Messages.registerMedActionInvalidMedTitle,
                                      Messages.registerMedActionInvalidMedMsg);
    }

    /**
     * Show the 'MED with same NS prefix already registered' info dialog
     */
    public static void showMedNSPrefixAlreadyRegisteredDialog() {
        MessageDialog.openInformation(getShell(),
                                      Messages.registerMedActionNamespacePrefixRegisteredTitle,
                                      Messages.registerMedActionNamespacePrefixRegisteredMsg);
    }

    /**
     * Show the 'MED with same NS prefix already registered' confirmation dialog. This will ask user if they want to update the
     * registered MED.
     */
    public static boolean showMedNSPrefixAlreadyRegisteredDoUpdateDialog() {
        return MessageDialog.openConfirm(getShell(),
                                         Messages.registerMedActionNamespacePrefixRegisteredTitle,
                                         Messages.registerMedActionNamespacePrefixRegisteredDoUpdateMsg);
    }

    /**
     * Show the 'MED with same NS URI already registered' info dialog
     */
    public static void showMedNSUriAlreadyRegisteredDialog() {
        MessageDialog.openInformation(getShell(),
                                      Messages.registerMedActionNamespaceUriRegisteredTitle,
                                      Messages.registerMedActionNamespaceUriRegisteredMsg);
    }

    /**
     * Show the 'MED NSUri conflicts with Built-In' info dialog
     */
    public static void showMedNSUriConflictsWBuiltInDialog() {
        MessageDialog.openInformation(getShell(),
                                      Messages.registerMedActionNamespaceUriConflictsWBuiltInTitle,
                                      Messages.registerMedActionNamespaceUriConflictsWBuiltInMsg);
    }

    /**
     * Show the 'MED NSPrefix conflicts with Built-In' info dialog
     */
    public static void showMedNSPrefixConflictsWBuiltInDialog() {
        MessageDialog.openInformation(getShell(),
                                      Messages.registerMedActionNamespacePrefixConflictsWBuiltInTitle,
                                      Messages.registerMedActionNamespacePrefixConflictsWBuiltInMsg);
    }

    public static boolean doDeployment( ModelExtensionRegistry registry,
                                        IFile mxdFile ) {
        boolean wasAdded = false;
        InputStream fileContents = null;
        try {
            fileContents = mxdFile.getContents();
        } catch (CoreException e) {
            UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.medFileGetContentsErrorMsg, mxdFile.getName()));
            return wasAdded;
        }

        // Parse file contents to get the MED. Show info dialog if not parsable.
        ModelExtensionDefinition med = RegistryDeploymentValidator.parseMed(fileContents, true);

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

            if (nsPrefixConflict && nsUriConflict && medNSPrefixMatch.equals(medNSUriMatch)) nsPrefixAndUriConflictSameMed = true;

            // No conflicts - add it to the registry
            if (!nsPrefixConflict && !nsUriConflict) {
                // Add the Extension Definition to the registry
                try {
                    UpdateRegistryModelExtensionDefinitionAction.addExtensionToRegistry(mxdFile);
                    wasAdded = true;
                } catch (Exception e) {
                    UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.medRegistryAddErrorMsg, mxdFile.getName()));
                    MessageDialog.openInformation(getShell(),
                                                  Messages.registerMedActionFailedTitle,
                                                  Messages.registerMedActionFailedMsg);
                }
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
                        // Add the Extension Definition to the registry
                        try {
                            UpdateRegistryModelExtensionDefinitionAction.updateExtensionInRegistry(mxdFile);
                            wasAdded = true;
                        } catch (Exception e) {
                            UTIL.log(IStatus.ERROR, e, NLS.bind(Messages.medRegistryAddErrorMsg, mxdFile.getName()));
                            MessageDialog.openInformation(getShell(),
                                                          Messages.registerMedActionFailedTitle,
                                                          Messages.registerMedActionFailedMsg);
                        }
                    }
                }
                // If there is a NS URI Conflict, prompt user to fix it
            } else if (nsUriConflict) {
                RegistryDeploymentValidator.showMedNSUriAlreadyRegisteredDialog();
            }
        }
        return wasAdded;
    }

    private static Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

}
