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
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;

/**
 * The <code>ModelExtensionDefinitionBuilder</code> is a project builder that creates resource problem markers for Model Extension
 * Definition (MED) files (*.mxd).
 */
public final class ModelExtensionDefinitionBuilder extends IncrementalProjectBuilder {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IProject[] build( int kind,
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

        // collect the MED files we need to build
        Collection<IFile> medFilesToBuild = visitor.getMedFiles();
        monitor.beginTask(Messages.medBuildTaskName, medFilesToBuild.size());

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
                    parser.parse(medFile.getContents(), ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());

                    // create new problem markers
                    createMarkers(medFile, parser.getErrors(), parser.getWarnings(), parser.getInfos());
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

            if (status.getSeverity() == IStatus.ERROR) {
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

        Collection<IFile> medFilesToClean = visitor.getMedFiles();
        monitor.beginTask(Messages.medCleanTaskName, medFilesToClean.size());

        // clean problem markers
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
    }

    /**
     * @param medFile the MED file who will create the problem marker (precondition: not <code>null</code>)
     * @param severity the marker severity
     * @param message the marker message (precondition: not <code>null</code> or empty)
     */
    private void createMarker( IFile medFile,
                               int severity,
                               String message ) {
        // parameters
        assert (medFile != null) : "medFile is null"; //$NON-NLS-1$
        assert ((message != null) && !message.isEmpty()) : "message is empty"; //$NON-NLS-1$

        Map attributes = new HashMap();
        attributes.put(IMarker.SEVERITY, severity);
        attributes.put(IMarker.MESSAGE, message);

        try {
            MarkerUtilities.createMarker(medFile, attributes, UiConstants.ExtensionIds.PROBLEM_MARKER);
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
                                 Collection<String> errors,
                                 Collection<String> warnings,
                                 Collection<String> infos ) throws Exception {
        assert (medFile != null) : "medFile is null"; //$NON-NLS-1$
        assert (errors != null) : "errors is null"; //$NON-NLS-1$
        assert (warnings != null) : "warnings is null"; //$NON-NLS-1$
        assert (infos != null) : "infos is null"; //$NON-NLS-1$

        // create errors
        for (String message : errors) {
            createMarker(medFile, IMarker.SEVERITY_ERROR, message);
        }

        // create warnings
        for (String message : warnings) {
            createMarker(medFile, IMarker.SEVERITY_WARNING, message);
        }

        // create infos
        for (String message : infos) {
            createMarker(medFile, IMarker.SEVERITY_INFO, message);
        }
    }

    /**
     * The <code>MedVisitor</code> gathers MED files that need their problem markers refreshed. A new visitor must be constructed
     * for each build.
     */
    class MedVisitor implements IResourceVisitor, IResourceDeltaVisitor {

        private Collection<IFile> medFiles = new ArrayList<IFile>();

        /**
         * @return the MED files whose problem markers need to be refreshed (never <code>null</code>)
         */
        public Collection<IFile> getMedFiles() {
            return this.medFiles;
        }

        /**
         * @param resource the resource being checked (never <code>null</code>)
         * @return <code>true</code> if resource is a MED file
         */
        private boolean isMedFile( IResource resource ) {
            return ((resource.getType() == IResource.FILE) && MED_EXTENSION.equals(resource.getFileExtension()) && resource.exists());
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
            }

            return true; // visit children
        }
    }

}
