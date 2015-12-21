/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui;

import static org.teiid.designer.extension.ExtensionConstants.MED_EXTENSION;
import static org.teiid.designer.extension.ExtensionConstants.PLUGIN_ID;
import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.teiid.designer.core.builder.AbstractTeiidProjectBuilder;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ModelExtensionAssistantAggregator;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.UiConstants.ExtensionIds;

/**
 * The <code>ModelExtensionDefinitionBuilder</code> is a project builder that creates resource problem markers for Model Extension
 * Definition (MED) files (*.mxd).
 */
public final class ModelExtensionDefinitionBuilder extends AbstractTeiidProjectBuilder {

    private static final boolean VISIT_MODELS = true; // turns visiting off for model files
    private static final String SAX_ERR_PREFIX = "cvc-"; //$NON-NLS-1$
    private static final String MED_VALIDATION_MSG = "MED Validation: "; //$NON-NLS-1$
    private static final String SEE_DETAILS_MSG = " (See log for details)"; //$NON-NLS-1$
    private static final String LEGACY_CLASSNAME_PREFIX = "com.metamatrix"; //$NON-NLS-1$
    public static final String HAS_LEGACY_NAMES = "hasLegacyNames"; //$NON-NLS-1$
    public static final String HAS_OLD_REST_PREFIX = "hasOldRestPrefix"; //$NON-NLS-1$
    public static final String IS_OLD_RELATIONAL_MED = "isOldRelationalMed"; //$NON-NLS-1$
    public static final String IS_OLD_REST_VERSION_MED = "isOldRestVersionMed"; //$NON-NLS-1$

    private ModelExtensionAssistantAggregator aggregator = ExtensionPlugin.getInstance().getModelExtensionAssistantAggregator();
    private ModelExtensionRegistry registry = ExtensionPlugin.getInstance().getRegistry();

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IProject[] buildInternal( int kind,
                                Map<String, String> args,
                                IProgressMonitor monitor ) throws CoreException {
        IProject project = getProject();

        // don't do anything if project is closed or doesn't exist
        if ((project == null) || !project.isAccessible()) {
            return null;
        }

        MedVisitor visitor = new MedVisitor();

        if ((IncrementalProjectBuilder.FULL_BUILD == kind) || (getDelta(project) == null)) {
            getProject().accept(visitor); // gather all MEDs in project
        } else {
            IResourceDelta delta = getDelta(project);
            delta.accept(visitor); // gather MEDs that have changed since last build
        }

        // collect the MED files and model files we need to build
        Collection<IFile> medFilesToBuild = visitor.getMedFiles();
        Collection<IFile> modelFilesToBuild = visitor.getModelFiles();
        monitor.beginTask(Messages.medBuildTaskName, (medFilesToBuild.size() + modelFilesToBuild.size()));

        if (!medFilesToBuild.isEmpty()) {
            File medSchema = null; // schema used to validate MED

            try {
                medSchema = ExtensionPlugin.getInstance().getMedSchema();
            } catch (Exception e) {
                IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, Messages.medSchemaNotFoundMsg, e);
                throw new CoreException(status);
            }

            ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(medSchema);
            MultiStatus status = new MultiStatus(PLUGIN_ID, IStatus.ERROR, Messages.medFileParseProblemMsg, null);

            for (IFile medFile : medFilesToBuild) {
                monitor.subTask(NLS.bind(Messages.medBuildSubTaskName, medFile.getName()));

                try {
                    // clear existing markers if not already done by the clean build
                    medFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);

                    // parse to get parse problems
                    ModelExtensionDefinition med = parser.parse(medFile.getContents(), ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());

                    // create new problem markers
                    createMarkers(medFile, med, parser.getErrors(), parser.getWarnings(), parser.getInfos());
                } catch (Exception e) {
                    IStatus parseStatus = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.medFileParseErrorMsg,
                                                                                        medFile.getName()), e);
                    status.add(parseStatus);
                } finally {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.worked(1);
                }
            }

            if (!status.isOK()) {
                UTIL.log(status);
            }
        }

        if (!modelFilesToBuild.isEmpty()) {
            MultiStatus status = new MultiStatus(PLUGIN_ID, IStatus.ERROR, Messages.modelFilesBuildProblemMsg, null);

            for (IFile modelFile : modelFilesToBuild) {
                monitor.subTask(NLS.bind(Messages.modelBuildSubTaskName, modelFile.getName()));

                try {
                    if (modelFile.exists()) {
                        modelFile.deleteMarkers(ExtensionIds.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE); // clear markers
                        refreshModelFileMarkers(modelFile); // create MED-related problem markers
                    }
                } catch (Exception e) {
                    IStatus modelStatus = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.modelFileBuildErrorMsg,
                                                                                        modelFile.getName()), e);
                    status.add(modelStatus);
                } finally {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.worked(1);
                }
            }

            if (!status.isOK()) {
                UTIL.log(status);
            }
        }

        // no other projects need also be rebuilt because this project was built
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#clean(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected void clean( IProgressMonitor monitor ) throws CoreException {
        MedVisitor visitor = new MedVisitor();
        getProject().accept(visitor); // gather all MEDs in project

        try {
            Collection<IFile> medFilesToClean = visitor.getMedFiles();
            Collection<IFile> modelFilesToClean = visitor.getModelFiles();
            monitor.beginTask(Messages.medCleanTaskName, (medFilesToClean.size() + modelFilesToClean.size()));

            // clean all MED problem markers
            for (IFile medFile : medFilesToClean) {
                try {
                    monitor.subTask(NLS.bind(Messages.medCleanSubTaskName, medFile.getName()));
                    medFile.deleteMarkers(null, true, IResource.DEPTH_INFINITE);
                } finally {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.worked(1);
                }
            }

            // clean MED-related problem markers in Teiid Designer models
            for (IFile modelFile : modelFilesToClean) {
                try {
                    monitor.subTask(NLS.bind(Messages.medCleanSubTaskName, modelFile.getName()));
                    modelFile.deleteMarkers(ExtensionIds.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
                } finally {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.worked(1);
                }
            }
        } finally {
            monitor.done();
        }
    }

    /**
     * @param file the MED or model file who will create the problem marker (precondition: not <code>null</code>)
     * @param severity the marker severity
     * @param message the marker message (precondition: not <code>null</code> or empty)
     * @param markerId the Id for the marker
     * @param hasLegacyNames when 'true' adds "hasLegacyNames" attribute to the marker
     */
    private void createMarker( IFile file,
                               int severity,
                               String message,
                               String markerId,
                               boolean hasLegacyNames) {
        // parameters
        assert (file != null) : "file is null"; //$NON-NLS-1$
        assert ((message != null) && !message.isEmpty()) : "message is empty"; //$NON-NLS-1$

        // For severity=ERROR, Re-write the Raw SaxParser exception to something more readable
        if (severity == IMarker.SEVERITY_ERROR) {
            String originalMessage = message;
            if (message.trim().startsWith(SAX_ERR_PREFIX)) {
                int index1 = message.indexOf(':'); // colon following cvc code
                int index2 = message.indexOf('.', index1); // end of first sentence of parser msg
                StringBuffer sb = new StringBuffer(MED_VALIDATION_MSG);
                sb.append(message.substring(index1 + 1, index2 + 1));
                sb.append(SEE_DETAILS_MSG);
                message = sb.toString();
            }
            // log the original message
            UTIL.log(IStatus.ERROR, MED_VALIDATION_MSG + originalMessage);
        }

        Map<String, Comparable> attributes = new HashMap<String, Comparable>();
        attributes.put(IMarker.SEVERITY, severity);
        attributes.put(IMarker.MESSAGE, message);
        if(hasLegacyNames) attributes.put(HAS_LEGACY_NAMES, true);
        if( message.indexOf("\"rest\"") > -1 && message.indexOf("is not registered in workspace") > -1 ) {
        	attributes.put(HAS_OLD_REST_PREFIX, true);
        }
        if( message.indexOf("model extension definition \"relational\" found in model is a different version") > -1  ) {
        	attributes.put(IS_OLD_RELATIONAL_MED, true);
        } else if( message.indexOf("model extension definition \"REST\" found in model is a different version") > -1  ) {
        	attributes.put(IS_OLD_REST_VERSION_MED, true);
        }

        try {
            MarkerUtilities.createMarker(file, attributes, markerId);
        } catch (CoreException e) {
            UTIL.log(e);
        }
    }

    /**
     * @param medFile the MED file whose problem markers are being refreshed (precondition: not <code>null</code>)
     * @param errors the parsing error messages (precondition: not <code>null</code>)
     * @param warnings the parsing warning messages (precondition: not <code>null</code>)
     * @param infos the parsing info messages (precondition: not <code>null</code>)
     * @throws Exception if there is a problem writing the markers to the resource
     */
    private void createMarkers( IFile medFile,
                                ModelExtensionDefinition med,
                                Collection<String> errors,
                                Collection<String> warnings,
                                Collection<String> infos ) throws Exception {
        assert (medFile != null) : "medFile is null"; //$NON-NLS-1$
        assert (errors != null) : "errors is null"; //$NON-NLS-1$
        assert (warnings != null) : "warnings is null"; //$NON-NLS-1$
        assert (infos != null) : "infos is null"; //$NON-NLS-1$

        // create errors
        for (String message : errors) {
            createMarker(medFile, IMarker.SEVERITY_ERROR, message, ExtensionIds.PROBLEM_MARKER, false);
        }

        // create warnings
        for (String message : warnings) {
            createMarker(medFile, IMarker.SEVERITY_WARNING, message ,ExtensionIds.PROBLEM_MARKER, false);
        }

        // create infos
        for (String message : infos) {
            createMarker(medFile, IMarker.SEVERITY_INFO, message, ExtensionIds.PROBLEM_MARKER, false);
        }
        
        // Check for Legacy 'com.metamatrix' classnames
        if(medHasLegacyClassnames(med)) {
            createMarker(medFile, IMarker.SEVERITY_ERROR, Messages.medFileHasLegacyClassnames, ExtensionIds.PROBLEM_MARKER, true);
        }
    }

    private boolean medsAreEqual( ModelExtensionDefinition thisMed,
                                  ModelExtensionDefinition thatMed ) {
        return thisMed.equals(thatMed);
    }
    
    /*
     * Determine if the supplied MED contains property references to the legacy 'com.metamatrix' class names
     * @param theMed the supplied MED
     * @return 'true' if the MED contains properties that reference 'com.metamatrix' class names, 'false' if not.
     */
    private boolean medHasLegacyClassnames( ModelExtensionDefinition theMed ) {
        if (theMed == null)
            return false;

        boolean hasLegacyClassnames = false;
        Map<String, Collection<ModelExtensionPropertyDefinition>> propMap = theMed.getPropertyDefinitions();
        Set<String> mapKeys = propMap.keySet();
        for(String mapKey : mapKeys) {
            if(mapKey.startsWith(LEGACY_CLASSNAME_PREFIX)) {
                hasLegacyClassnames = true;
                break;
            }
        }
        return hasLegacyClassnames;
    }

    /**
     * @param modelFile the model file whose MED-related problem markers are being refreshed (cannot be <code>null</code>)
     * @throws Exception if there is a problem obtaining MED information from the model file
     */
    void refreshModelFileMarkers( IFile modelFile ) throws Exception {
        for (String namespacePrefix : this.aggregator.getSupportedNamespacePrefixes(modelFile)) {
            Object temp = this.registry.getModelExtensionAssistant(namespacePrefix);

            // if there is no assistant than the MED is not registered
            if ((temp == null) || (!(temp instanceof ModelObjectExtensionAssistant))) {
                createMarker(modelFile, IMarker.SEVERITY_WARNING, NLS.bind(Messages.modelMedNotFoundInRegistry, namespacePrefix), ExtensionIds.PROBLEM_MARKER, false);
            } else {
                ModelObjectExtensionAssistant registryAssistant = (ModelObjectExtensionAssistant)temp;
                ModelExtensionDefinition registryMed = registryAssistant.getModelExtensionDefinition();
                ModelObjectExtensionAssistant modelAssistant = ExtensionPlugin.getInstance()
                                                                              .createDefaultModelObjectExtensionAssistant(namespacePrefix);
                ModelExtensionDefinition modelMed = modelAssistant.getModelExtensionDefinition(modelFile);

                // Add marker if Model has properties with the legacy Class names ("com.metamatrix")
                // No further analysis is done until legacy names are corrected.
                if (medHasLegacyClassnames(modelMed)) {
                    createMarker(modelFile, IMarker.SEVERITY_ERROR, Messages.modelMedHasLegacyClassnames, ExtensionIds.PROBLEM_MARKER, true);
                    return;
                }
                
                if (!medsAreEqual(registryMed, modelMed)) {
                    // make sure MED is same version as the registered one
                    createMarker(modelFile, IMarker.SEVERITY_WARNING,
                                 NLS.bind(Messages.modelMedDifferentVersionThanOneFoundInRegistry, namespacePrefix), ExtensionIds.PROBLEM_MARKER, false);
                }
            }
        }
    }

    /**
     * The <code>MedVisitor</code> gathers MED files that need their problem markers refreshed. A new visitor must be constructed
     * for each build.
     */
    class MedVisitor implements IResourceVisitor, IResourceDeltaVisitor {

        private Collection<IFile> medFiles = new ArrayList<IFile>();
        private Collection<IFile> modelFiles = new ArrayList<IFile>();

        /**
         * @return the MED files whose problem markers need to be refreshed (never <code>null</code>)
         */
        public Collection<IFile> getMedFiles() {
            return this.medFiles;
        }

        /**
         * @return the Teiid Designer model files whose MED-related problem markers need to be refreshed (never <code>null</code>)
         */
        public Collection<IFile> getModelFiles() {
            return this.modelFiles;
        }

        /**
         * @param resource the resource being checked (never <code>null</code>)
         * @return <code>true</code> if resource is a MED file
         */
        private boolean isMedFile( IResource resource ) {
            return ((resource.getType() == IResource.FILE) && MED_EXTENSION.equals(resource.getFileExtension()) && resource.exists());
        }

        /**
         * @param resource the resource being checked (never <code>null</code>)
         * @return <code>true</code> if resource is a Teiid Designer model file
         */
        private boolean isModelFile( IResource resource ) {
            return ModelUtil.isModelFile(resource);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
         */
        @Override
        public boolean visit( IResource resource ) {
            if (isMedFile(resource)) {
                this.medFiles.add((IFile)resource);
            } else if (VISIT_MODELS && isModelFile(resource)) {
                this.modelFiles.add((IFile)resource);
            }

            return true; // visit resource members
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        @Override
        public boolean visit( IResourceDelta delta ) {
            IResource resource = delta.getResource();

            if (isMedFile(resource)) {
                this.medFiles.add((IFile)resource);
            } else if (VISIT_MODELS && isModelFile(resource)) {
                this.modelFiles.add((IFile)resource);
            }

            return true; // visit children
        }
    }

}
