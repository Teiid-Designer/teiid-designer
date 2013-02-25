/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.build;


import static org.teiid.designer.vdb.ui.VdbUiConstants.PLUGIN_ID;
import static org.teiid.designer.vdb.ui.VdbUiConstants.Util;

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
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.Messages;
import org.teiid.designer.vdb.ui.VdbUiConstants;

/**
 *
 */
public class VdbBuilder extends IncrementalProjectBuilder {
	@SuppressWarnings("javadoc")
	public static final String WRONG_PATH = "wrongPath"; //$NON-NLS-1$
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
        IProject project = getProject();
        
        // don't do anything if project is closed or doesn't exist
        if ((project == null) || !project.isAccessible()) {
            return null;
        }

        VdbVisitor visitor = new VdbVisitor();

        getProject().accept(visitor);
        
        // collect the MED files and model files we need to build
        Collection<IFile> vdbFilesToBuild = visitor.getVdbFiles();
        monitor.beginTask(Messages.vdbBuildTaskName, (vdbFilesToBuild.size() + vdbFilesToBuild.size()));

        if (!vdbFilesToBuild.isEmpty()) {
            MultiStatus status = new MultiStatus(PLUGIN_ID, IStatus.ERROR, Messages.vdbFilesBuildProblemMsg, null);

            for (IFile vdbFile : vdbFilesToBuild) {
                monitor.subTask(NLS.bind(Messages.vdbBuildSubTaskName, vdbFile.getName()));

                try {
                    if (vdbFile.exists()) {
                    	vdbFile.deleteMarkers(VdbUiConstants.VdbIds.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE); // clear markers
                        refreshVdbFileMarkers(vdbFile); // create VDB-related problem markers
                    }
                } catch (Exception e) {
                    IStatus vdbStatus = new Status(IStatus.ERROR, PLUGIN_ID, NLS.bind(Messages.vdblFileBuildErrorMsg,
                                                                                        vdbFile.getName()), e);
                    status.add(vdbStatus);
                } finally {
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }

                    monitor.worked(1);
                }
            }

            if (!status.isOK()) {
                Util.log(status);
            }
        }

        // no other projects need also be rebuilt because this project was built
        return null;
	}
	
    /**
     * @param vdbFile the vdb file (cannot be <code>null</code>)
     * @throws Exception if there is a problem obtaining VDB information from the vdb file
     */
    void refreshVdbFileMarkers( IFile vdbFile ) throws Exception {
    	// Check if each model file exists in workspace (WARNING)
    	
    	MultiStatus status = VdbUtil.validateVdbModelsInWorkspace(vdbFile);
    	
    	if( status.getSeverity() > IStatus.INFO ) {
    		for( IStatus iStatus : status.getChildren() )  {
    			switch( iStatus.getSeverity() ) {
    			case IStatus.WARNING: {
    				boolean hasWrongPath = iStatus.getMessage().indexOf("exists in your project") > 0; //$NON-NLS-1$
    				createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, hasWrongPath);
    			} break;
    			case IStatus.ERROR: {
    				createMarker(vdbFile, IMarker.SEVERITY_ERROR, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, false);
    			} break;
    			}
    		}
    	}
    	
    	// Check if each model file location is same as workspace (WARNING)

        //createMarker(vdbFile, IMarker.SEVERITY_WARNING, NLS.bind(Messages.modelMedNotFoundInRegistry, namespacePrefix), VdbUiConstants.VdbIds.PROBLEM_MARKER, false);

        //createMarker(vdbFile, IMarker.SEVERITY_ERROR, Messages.modelMedHasLegacyClassnames, VdbUiConstants.VdbIds.PROBLEM_MARKER, true);
    }
    
    /**
     * @param file the MED or model file who will create the problem marker (precondition: not <code>null</code>)
     * @param severity the marker severity
     * @param message the marker message (precondition: not <code>null</code> or empty)
     * @param markerId the Id for the marker
     * @param hasWrongPath wrong path indicator
     */
    private void createMarker( IFile file,
                               int severity,
                               String message,
                               String markerId,
                               boolean hasWrongPath) {
        // parameters
        assert (file != null) : "file is null"; //$NON-NLS-1$
        assert ((message != null) && !message.isEmpty()) : "message is empty"; //$NON-NLS-1$

        @SuppressWarnings("rawtypes")
		Map<String, Comparable> attributes = new HashMap<String, Comparable>();
        attributes.put(IMarker.SEVERITY, severity);
        attributes.put(IMarker.MESSAGE, message);
        
        // Add attribute if wrong path so Quick Fix can find it
        if( hasWrongPath ) attributes.put(WRONG_PATH, true);

        try {
            MarkerUtilities.createMarker(file, attributes, markerId);
        } catch (CoreException e) {
            Util.log(e);
        }
    }

    /**
     * The <code>MedVisitor</code> gathers MED files that need their problem markers refreshed. A new visitor must be constructed
     * for each build.
     */
    class VdbVisitor implements IResourceVisitor, IResourceDeltaVisitor {

        private Collection<IFile> vdbFiles = new ArrayList<IFile>();

        /**
         * @return the VDB files whose problem markers need to be refreshed (never <code>null</code>)
         */
        public Collection<IFile> getVdbFiles() {
            return this.vdbFiles;
        }


        /**
         * @param resource the resource being checked (never <code>null</code>)
         * @return <code>true</code> if resource is a VDB file
         */
        private boolean isVdbFile( IResource resource ) {
            return ((resource.getType() == IResource.FILE) && VdbConstants.VDB_FILE_EXTENSION.equals(resource.getFileExtension()) && resource.exists());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
         */
        @Override
        public boolean visit( IResource resource ) {
            if (isVdbFile(resource)) {
                this.vdbFiles.add((IFile)resource);
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

            if (isVdbFile(resource)) {
                this.vdbFiles.add((IFile)resource);
            }

            return true; // visit children
        }
    }
}
