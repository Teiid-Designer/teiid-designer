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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.teiid.designer.core.builder.AbstractTeiidProjectBuilder;
import org.teiid.designer.vdb.VdbConstants;
import org.teiid.designer.vdb.VdbUtil;
import org.teiid.designer.vdb.ui.Messages;
import org.teiid.designer.vdb.ui.VdbUiConstants;

/**
 *
 */
public class VdbBuilder extends AbstractTeiidProjectBuilder {
	@SuppressWarnings("javadoc")
	public static final String WRONG_PATH = "wrongPath"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String OUT_OF_SYNC = "outOfSync"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String NAME_CHANGED = "nameChanged"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String MISSING_UUID = "missingUuid"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String MISSING_MODEL = "missingModel"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String TOO_MANY_SOURCES = "tooManySources"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String DIFFERENT_VALIDATION_VERSION = "differentValidationVersion"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String MISSING_TRANSLATOR_TYPE = "missingTranslatorType"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String MISSING_JNDI_NAME = "missingJndiName"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String MODEL_WITH_ERRORS = "modelWithErrors"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String DUPLICATE_MODEL_NAMES = "duplicateModelNames"; //$NON-NLS-1$
	@SuppressWarnings("javadoc")
	public static final String SINGLE_AUTHENTICATION_TYPE_IGNORED = "singleAuthenticationTypeIgnored"; //$NON-NLS-1$
	
    private enum MarkerType {
    	DEFAULT,
    	WRONG_PATH, 
    	OUT_OF_SYNC, 
    	NAME_CHANGED, 
    	MISSING_UUID,
    	MISSING_MODEL,
    	TOO_MANY_SOURCES,
    	DIFFERENT_VALIDATION_VERSION,
    	MISSING_TRANSLATOR_TYPE,
    	MISSING_JNDI_NAME,
    	MODEL_WITH_ERRORS,
    	DUPLICATE_MODEL_NAMES,
    	SINGLE_AUTHENTICATION_TYPE_IGNORED;
    }
    
    /**
public enum Currency {
        PENNY(1), NICKLE(5), DIME(10), QUARTER(25);
        private int value;

        private Currency(int value) {
                this.value = value;
        }
};  

Read more: http://javarevisited.blogspot.com/2011/08/enum-in-java-example-tutorial.html#ixzz2MJdMYTAM

    	*/

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int, java.util.Map,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
	@Override
	protected IProject[] buildInternal(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
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

        if (vdbFilesToBuild.isEmpty())
            return null;

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
    				if( iStatus.getMessage().indexOf("exists in your project") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.WRONG_PATH);
    				}
    				if( iStatus.getMessage().indexOf("is not synchronized") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.OUT_OF_SYNC);
    				}
    				if( iStatus.getMessage().indexOf("has different name than model") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.NAME_CHANGED);
    				}
    				if( iStatus.getMessage().indexOf("is missing its ID") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.MISSING_UUID);
    				}
    				if( iStatus.getMessage().indexOf("does not exist") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.MISSING_MODEL);
    				}
    				if( iStatus.getMessage().indexOf("multiple sources defined") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.TOO_MANY_SOURCES);
    				}
    				if( iStatus.getMessage().indexOf("runtime validation version") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.DIFFERENT_VALIDATION_VERSION);
    				}
    				if( iStatus.getMessage().indexOf("no JNDI name defined") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.MISSING_JNDI_NAME);
    				}
    				if( iStatus.getMessage().indexOf("single authentication type") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_WARNING, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.SINGLE_AUTHENTICATION_TYPE_IGNORED);
    				}
    				
    			} break;
    			case IStatus.ERROR: {
    				if( iStatus.getMessage().indexOf("no translator type defined") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_ERROR, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.MISSING_TRANSLATOR_TYPE);
    				} else if( iStatus.getMessage().indexOf("and will not be ACTIVE") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_ERROR, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.MODEL_WITH_ERRORS);
    				} else if(iStatus.getMessage().indexOf("VDB cannot contain models") > 0 ) { //$NON-NLS-1$
    					createMarker(vdbFile, IMarker.SEVERITY_ERROR, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.DUPLICATE_MODEL_NAMES);
    				} else {
    					createMarker(vdbFile, IMarker.SEVERITY_ERROR, iStatus.getMessage(), VdbUiConstants.VdbIds.PROBLEM_MARKER, MarkerType.DEFAULT);
    				}
    			} break;
    			}
    		}
    	}
    }
    
    /**
     * @param file the MED or model file who will create the problem marker (precondition: not <code>null</code>)
     * @param severity the marker severity
     * @param message the marker message (precondition: not <code>null</code> or empty)
     * @param markerId the Id for the marker
     * @param markerType
     */
    private void createMarker( IFile file,
                               int severity,
                               String message,
                               String markerId,
                               MarkerType markerType) {
        // parameters
        assert (file != null) : "file is null"; //$NON-NLS-1$
        assert ((message != null) && !message.isEmpty()) : "message is empty"; //$NON-NLS-1$

        @SuppressWarnings("rawtypes")
		Map<String, Comparable> attributes = new HashMap<String, Comparable>();
        attributes.put(IMarker.SEVERITY, severity);
        attributes.put(IMarker.MESSAGE, message);
        
		if (markerType == MarkerType.WRONG_PATH) {
			attributes.put(WRONG_PATH, true);
		} else if (markerType == MarkerType.OUT_OF_SYNC) {
			attributes.put(OUT_OF_SYNC, true);
		} else if (markerType == MarkerType.NAME_CHANGED) {
			attributes.put(NAME_CHANGED, true);
		} else if (markerType == MarkerType.MISSING_UUID) {
			attributes.put(MISSING_UUID, true);
		} else if (markerType == MarkerType.MISSING_MODEL) {
			attributes.put(MISSING_MODEL, true);
		} else if (markerType == MarkerType.DIFFERENT_VALIDATION_VERSION) {
			attributes.put(DIFFERENT_VALIDATION_VERSION, true);
		} else if (markerType == MarkerType.DUPLICATE_MODEL_NAMES) {
		    attributes.put(DUPLICATE_MODEL_NAMES, true);
		}
		
		attributes.put(IMarker.LOCATION, file.getName());
        
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
