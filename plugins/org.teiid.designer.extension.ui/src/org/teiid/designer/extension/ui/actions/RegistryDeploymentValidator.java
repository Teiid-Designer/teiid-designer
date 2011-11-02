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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionAssistantAdapter;
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
     * Determine if the supplied File contains a valid and deployable ModelExtensionDefinition. 'valid' meaning that the file
     * contents are parsable against the MED schema. 'deployable' meaning that the med namespace prefix and URI are not already
     * registered. If the contents are not deployable, the appropriate info dialogs are displayed.
     * 
     * @param mxdFile the file containing the extension definition
     * @return the ModelExtensionDefinition, null if invalid
     */
    public static boolean checkMedDeployable( ModelExtensionRegistry registry,
                                              InputStream mxdContents ) {
        boolean isDeployable = false;
        ModelExtensionDefinition med = checkMedValid(mxdContents);
        if (med != null) {
            boolean isNamespacePrefixRegistered = checkMedNamespacePrefixRegistered(registry, med);
            if (!isNamespacePrefixRegistered) {
                isDeployable = !checkMedNamespacePrefixRegistered(registry, med);
            }
        }
        return isDeployable;
    }

    /**
     * Determine if the supplied File contains a valid ModelExtensionDefinition. 'valid' meaning that the file contents are
     * parsable against the MED schema. If the contents are not valid, a dialog is displayed.
     * 
     * @param mxdFile the file containing the extension definition
     * @return the ModelExtensionDefinition, null if invalid
     */
    public static ModelExtensionDefinition checkMedValid( InputStream mxdContents ) {
        ModelExtensionDefinition med = null;
        try {
            ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(
                                                                                       ExtensionPlugin.getInstance().getMedSchema());
            med = parser.parse(mxdContents, new ModelExtensionAssistantAdapter());
        } catch (Exception e) {
            UTIL.log(Messages.medFileParseErrorMsg);
        }

        // Notify user if parse fails
        if (med == null) {
            MessageDialog.openInformation(getShell(),
                                          Messages.registerMedActionInvalidMedTitle,
                                          Messages.registerMedActionInvalidMedMsg);
        }

        return med;
    }

    /**
     * Check the ModelExtensionDefinition namespace prefix to see if an extension with the same prefix has already been deployed
     * to the registry. If there is a conflict, a dialog is displayed.
     * 
     * @param registry the extension registry
     * @param med the ModelExtensionDefinition
     * @return 'true' if a matching namespacePrefix is already registered, 'false' if not
     */
    public static boolean checkMedNamespacePrefixRegistered( ModelExtensionRegistry registry,
                                                             ModelExtensionDefinition med ) {
        boolean namespacePrefixRegistered = registry.isNamespacePrefixRegistered(med.getNamespacePrefix());

        // Notify user if namespace already registered
        if (namespacePrefixRegistered) {
            MessageDialog.openInformation(getShell(),
                                          Messages.registerMedActionNamespacePrefixRegisteredTitle,
                                          Messages.registerMedActionNamespacePrefixRegisteredMsg);
        }
        return namespacePrefixRegistered;
    }

    /**
     * Check the ModelExtensionDefinition namespace URI to see if an extension with the same URI has already been deployed to the
     * registry. If there is a conflict, a dialog is displayed.
     * 
     * @param registry the extension registry
     * @param med the ModelExtensionDefinition
     * @return 'true' if a matching namespaceUri is already registered, 'false' if not
     */
    public static boolean checkMedNamespaceUriRegistered( ModelExtensionRegistry registry,
                                                          ModelExtensionDefinition med ) {
        boolean namespaceUriRegistered = registry.isNamespaceUriRegistered(med.getNamespaceUri());

        // Notify user if namespace URI already registered
        if (namespaceUriRegistered) {
            MessageDialog.openInformation(getShell(),
                                          Messages.registerMedActionNamespaceUriRegisteredTitle,
                                          Messages.registerMedActionNamespaceUriRegisteredMsg);
        }
        return namespaceUriRegistered;
    }

    private static Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

}
