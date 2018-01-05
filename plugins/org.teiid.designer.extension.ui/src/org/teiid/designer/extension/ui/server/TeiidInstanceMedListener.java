/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.ui.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.convertor.MxdConvertor;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.actions.RegistryDeploymentValidator;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;

/**
 *
 */
public class TeiidInstanceMedListener implements IExecutionConfigurationListener {

    private static TeiidInstanceMedListener instance;

    private ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();

    private IStatus result = null;

    /**
     * @return singleton instance
     */
    public static TeiidInstanceMedListener getInstance() {
        if(instance == null)
            instance = new TeiidInstanceMedListener();

        return instance;
    }

    private TeiidInstanceMedListener() {}

    private void addStatus(final int severity, final String msg, final Exception e) {
        if(result == null) {
            result = new MultiStatus(Activator.PLUGIN_ID, -1, Messages.translatorExtensionConversionResult, null);
        }

        ((MultiStatus) result).add(new Status(severity, Activator.PLUGIN_ID, -1, msg, e) );
    }

    private void internalRun( File medToRegister, boolean isUpdate ) throws Exception {

        if (isUpdate) {
            // If MED with this prefix is registered, remove it first
            FileInputStream inputStream = new FileInputStream(medToRegister);
            try {
                ModelExtensionDefinition med = RegistryDeploymentValidator.parseMed(inputStream, false);
                if (registry.isNamespacePrefixRegistered(med.getNamespacePrefix())) {
                    registry.removeDefinition(med.getNamespacePrefix());
                }
            } finally {
                try {
                    inputStream.close();
                } catch(Exception ex) {
                    // Do nothing
                }
            }
        }

        // Add the supplied MED
        FileInputStream inputStream = new FileInputStream(medToRegister);
        try {
            ModelExtensionDefinition definition = registry.addDefinition(inputStream, ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());
            if (definition != null)
                definition.markAsImported();
        } finally {
            try {
                inputStream.close();
            } catch(Exception ex) {
                // Do nothing
            }
        }
    }

    private void deploy(File fileInput) throws Exception {
        CoreArgCheck.isNotNull(fileInput);

        // Parse file contents to get the MED. Show info dialog if parse errors.
        ModelExtensionDefinition med = null;
        ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(ExtensionPlugin.getInstance()
                                                                                                          .getMedSchema());

        FileInputStream inputStream = new FileInputStream(fileInput);
        try {
            med = parser.parse(inputStream, ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());
        } finally {
            try {
                inputStream.close();
            } catch(Exception ex) {
                // Do nothing
            }
        }

        if (med == null || !parser.getErrors().isEmpty()) {
            String name = med != null ? med.getNamespacePrefix() : Messages.teiidGeneratedMed;
            StringBuffer errorMsg = new StringBuffer(NLS.bind(Messages.medFileParseErrorMsg, name));
            errorMsg.append(StringConstants.NEW_LINE);

            for (String err : parser.getErrors()) {
                errorMsg.append(Messages.parsingError);
                errorMsg.append(err);
                errorMsg.append(StringConstants.NEW_LINE);
            }

            throw new Exception(errorMsg.toString());
        }

        // Continue checks on parsable MED
        ModelExtensionDefinition medNSPrefixMatch = RegistryDeploymentValidator.getRegisteredMedWithNSPrefix(registry,
                                                                                                             med.getNamespacePrefix());
//        ModelExtensionDefinition medNSUriMatch = RegistryDeploymentValidator.getRegisteredMedWithNSUri(registry,
//                                                                                                               med.getNamespaceUri());

        boolean nsPrefixConflict = false;
        boolean nsPrefixConflictMedBuiltIn = false;
        boolean nsUriConflictMedBuiltIn = false;

        if (medNSPrefixMatch != null) {
            nsPrefixConflict = true;
            nsPrefixConflictMedBuiltIn = medNSPrefixMatch.isBuiltIn();
        }


        // No conflicts - add it to the registry
        if (!nsPrefixConflict ) {
            // Add the selected Med
            internalRun(fileInput, false);

            // If the NS Prefix conflicts with a Built-in, then return
        } else if (nsPrefixConflictMedBuiltIn) {
            // FIXME check version of med and teiid instance and register version-specific versions
            return;
            // If the NS URI conflicts with a Built-in, then return
        } else if (nsUriConflictMedBuiltIn) {
            // FIXME check version of med and teiid instance and register version-specific versions
            return;
            // If there is (1) just a NS Prefix Conflict or (2) NS Prefix AND URI, but they are same MED, prompt user
            // whether to update
        } else {
            // Do not re-deploy the same MED
            if (med.equals(medNSPrefixMatch)) {
                // Already registered so return
                return;
            } else {
                // Add the selected Med
                internalRun(fileInput, true);
            }
            // If there is a NS URI Conflict, throw exception to that effect
        }
    }

    /**
     * @param teiidInstance
     *
     */
    private IStatus analyseTranslators(ITeiidServer teiidInstance) {
        CoreArgCheck.isNotNull(teiidInstance, "teiid instance"); //$NON-NLS-1$
        FileOutputStream output = null;
        result = null;

        Collection<ITeiidTranslator> translators = Collections.emptyList();
        try {
            translators = teiidInstance.getTranslators();
        } catch (Exception ex) {
            addStatus(IStatus.ERROR, Messages.failureToReceiveTranslators, ex);
            return result;
        }

        if (translators == null || translators.isEmpty()) {
            // Nothing to do
            return Status.OK_STATUS;
        }

        MxdConvertor convertor = MxdConvertor.getInstance();
        for (ITeiidTranslator translator : translators) {
            try {
                // Create temp file to store convertor's resulting xml
                File outputFile = File.createTempFile(this.getClass().getSimpleName(),
                                                      StringConstants.DOT + StringConstants.XML);
                
                outputFile.deleteOnExit();
                
                // Output stream for convertor
                output = new FileOutputStream(outputFile);
                System.out.println("   TeiidInstanceMedListener.analyseTranslators(" + translator.getName() + ")  coverter.convert()");
                boolean conversion = convertor.convert(translator, output);
                if( !conversion ) System.out.println("          >>>> Success = " + conversion);
                if (!conversion)
                    continue;

                deploy(outputFile);
            } catch (Exception ex) {
            	if(ex.getMessage().contains("did not parse successfully and cannot be added")) {
            		continue;
            	} else {
	                addStatus(IStatus.ERROR,
	                          NLS.bind(Messages.errorOccurredAnalysingTranslators, translator.getName(), ex.getLocalizedMessage()),
	                          ex);
            	}
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException ex) {
                        // Ignore close failures
                    }
                }
            }
        }

        return result != null ? result : Status.OK_STATUS;
    }

    @Override
    public void configurationChanged(final ExecutionConfigurationEvent event) {
        switch(event.getEventType()) {
            case CONNECTED:
                Job job = new Job(Messages.analyseTranslatorsJob) {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        return analyseTranslators(event.getServer());
                    }
                };
                job.setPriority(Job.LONG);
                job.setSystem(true);
                job.setUser(false);
                job.schedule();

                break;
            default:
                // Don't care about other event types
        }
    }
}
