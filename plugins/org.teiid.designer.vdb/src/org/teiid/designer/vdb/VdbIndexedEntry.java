/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.container.ResourceFinder;
import org.teiid.designer.core.index.Index;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.ModelElement;
import org.teiid.designer.vdb.manifest.ProblemElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.manifest.Severity;

/**
 *
 */
public abstract class VdbIndexedEntry extends VdbEntry {

    /**
     * 
     */
    public class Problem {

        private final int severity;
        private final String message;
        private final String location;

        Problem( final IMarker marker ) {
            this.severity = marker.getAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
            this.message = marker.getAttribute(IMarker.MESSAGE, null);
            this.location = marker.getAttribute(IMarker.LOCATION, null);
        }

        Problem( final ProblemElement problem ) {
            this.severity = problem.getSeverity() == Severity.ERROR ? IMarker.SEVERITY_ERROR : IMarker.SEVERITY_WARNING;
            this.message = problem.getMessage();
            this.location = problem.getLocation();
        }

        /**
         * @return location
         */
        public String getLocation() {
            return location;
        }

        /**
         * @return message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return severity
         */
        public int getSeverity() {
            return severity;
        }
    }

    protected static final String INDEX_FOLDER = "runtime-inf/";

    private final String indexName;

    private final Set<Problem> problems = new HashSet<Problem>();

    /**
     * @param vdb
     * @param element
     * @throws Exception
     */
    public VdbIndexedEntry(Vdb vdb, EntryElement element) throws Exception {
        super(vdb, element);

        String indexName = null;
        for (final PropertyElement property : element.getProperties()) {
            final String name = property.getName();
            if (ModelElement.INDEX_NAME.equals(name))
                indexName = property.getValue();
        }

        this.indexName = indexName;
    }

    /**
     * @param vdb
     * @param name
     * @throws Exception
     */
    public VdbIndexedEntry(Vdb vdb, IPath name) throws Exception {
        super(vdb, name);
        indexName = IndexUtil.getRuntimeIndexFileName(findFileInWorkspace());
    }

    /**
     * Clean the entry
     */
    protected void clean() {
        // Clear problems
        problems.clear();

        getIndexFile().delete();
    }

    protected ResourceFinder getFinder() throws Exception {
        return ModelerCore.getModelContainer().getResourceFinder();
    }

    protected Resource findModel() throws Exception {
        IResource resource = ModelerCore.getWorkspace().getRoot().findMember(getName());
    
        // model not found in workspace
        if (resource == null) {
            return null;
        }
    
        Resource emfResource = getFinder().findByURI(URI.createFileURI(resource.getLocation().toString()), false);
    
        // as a last resort force loading the resource
        if (emfResource == null) {
            emfResource = ModelerCore.getModelContainer().getResource(URI.createFileURI(resource.getLocation().toString()), true);
        }
    
        return emfResource;
    }

    protected File getIndexFile() {
        return new File(getVdb().getFolder(), INDEX_FOLDER + indexName);
    }

    /**
     * @return indexName
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * @return the immutable set of problems associated with this model entry
     */
    public final Set<Problem> getProblems() {
        return Collections.unmodifiableSet(problems);
    }

    /**
     * @param problem
     */
    protected void addProblem(Problem problem) {
        problems.add(problem);
    }

    /**
     * @throws Exception
     */
    protected void synchronizeIndex() throws Exception {
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile == null)
            return;

        // Clear problems
        problems.clear();
        getIndexFile().delete();
        
        // Build model if necessary
        // Get Index File and check time/date to see if we need to rebuild or not
        IPath indexPath = new Path(IndexUtil.INDEX_PATH + indexName); //
        File indexFile = indexPath.toFile();
        long indexDate = -1;
        if (indexFile.exists()) {
            indexDate = indexFile.lastModified();
        }
        if (workspaceFile.getLocalTimeStamp() > indexDate) {
            // Note that this will index and validate the model in the workspace
            getVdb().getBuilder().buildResources(new NullProgressMonitor(),
                                                 Collections.singleton(workspaceFile),
                                                 ModelerCore.getModelContainer(),
                                                 false);
        }

        // Copy snapshot of workspace file index to VDB folder
        // TODO: If index name of workspace file can change (?), we have to delete the old index and update our index name
        final Index index = IndexUtil.getIndexFile(indexName, IndexUtil.INDEX_PATH + indexName, getName().lastSegment());
        FileUtils.copy(index.getIndexFile(), getIndexFile().getParentFile(), true);

        problems.clear();
        // Synchronize model problems
        for (final IMarker marker : workspaceFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
            Object attr = marker.getAttribute(IMarker.SEVERITY);
            if (attr == null) {
                continue;
            }
            // Asserting attr is an Integer...
            final int severity = ((Integer)attr).intValue();
            if (severity == IMarker.SEVERITY_ERROR || severity == IMarker.SEVERITY_WARNING) {
                problems.add(new Problem(marker));
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.vdb.VdbEntry#save(java.util.zip.ZipOutputStream)
     */
    @Override
    public void save( final ZipOutputStream out ) throws Exception {
        super.save(out);
        // Save model index
        save(out, new ZipEntry(INDEX_FOLDER + getIndexName()), getIndexFile());

        if (!getVdb().isPreview()) {
            // Convert problems for this model entry to markers on the VDB file
            final IFile vdbFile = getVdb().getFile();
            for (final Problem problem : getProblems()) {
                final IMarker marker = vdbFile.createMarker(IMarker.PROBLEM);
                marker.setAttribute(IMarker.SEVERITY, problem.getSeverity());
                marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
                marker.setAttribute(IMarker.LOCATION, getName().toString() + '/' + problem.getLocation());
            }
        }
    }
}
