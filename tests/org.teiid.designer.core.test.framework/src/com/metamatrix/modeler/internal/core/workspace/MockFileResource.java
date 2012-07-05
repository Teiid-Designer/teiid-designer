/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Map;
import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.teiid.core.util.FileUtils;
import com.metamatrix.modeler.core.ModelerCore;

@SuppressWarnings( "deprecation" )
public class MockFileResource implements IFile {

    private File file;
    // private boolean accessible;
    private boolean modelNature;

    public MockFileResource( File file ) {
        this.file = file;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
     */
    public void accept( IResourceProxyVisitor visitor,
                        int memberFlags ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
     */
    public void accept( IResourceVisitor visitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
     */
    public void accept( IResourceVisitor visitor,
                        int depth,
                        boolean includePhantoms ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
     */
    public void accept( IResourceVisitor visitor,
                        int depth,
                        int memberFlags ) {

    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int, int)
     */
    public void accept( IResourceProxyVisitor visitor,
                        int depth,
                        int memberFlags ) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void clearHistory( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy( IPath destination,
                      boolean force,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy( IPath destination,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy( IProjectDescription description,
                      boolean force,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void copy( IProjectDescription description,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
     */
    public IMarker createMarker( String type ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete( boolean force,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete( int updateFlags,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
     */
    public void deleteMarkers( String type,
                               boolean includeSubtypes,
                               int depth ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#exists()
     */
    public boolean exists() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarker(long)
     */
    public IMarker findMarker( long id ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
     */
    public IMarker[] findMarkers( String type,
                                  boolean includeSubtypes,
                                  int depth ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getFileExtension()
     */
    public String getFileExtension() {
        if (this.file != null) {
            return FileUtils.getExtension(this.file);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getFullPath()
     */
    public IPath getFullPath() {
        if (this.file != null) {
            return new Path(this.file.getAbsolutePath());
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocation()
     */
    public IPath getLocation() {

        return new Path(this.file.getAbsolutePath());
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getMarker(long)
     */
    public IMarker getMarker( long id ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getModificationStamp()
     */
    public long getModificationStamp() {

        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getName()
     */
    public String getName() {
        return this.file.getName();
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getParent()
     */
    public IContainer getParent() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
     */
    public String getPersistentProperty( QualifiedName key ) {

        return null;
    }

    @Override
    public Map getPersistentProperties() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProject()
     */
    public IProject getProject() {
        return new MockProject(this.modelNature);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
     */
    public IPath getProjectRelativePath() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getRawLocation()
     */
    public IPath getRawLocation() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
     */
    public Object getSessionProperty( QualifiedName key ) {

        return null;
    }

    @Override
    public Map getSessionProperties() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getType()
     */
    public int getType() {
        return IResource.FILE;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getWorkspace()
     */
    public IWorkspace getWorkspace() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isAccessible()
     */
    public boolean isAccessible() {

        return false;
    }

    public void setAccessible( boolean accessible ) {
        // this.accessible = accessible;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isDerived()
     */
    public boolean isDerived() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLocal(int)
     */
    public boolean isLocal( int depth ) {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLinked()
     */
    public boolean isLinked() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isPhantom()
     */
    public boolean isPhantom() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isReadOnly()
     */
    public boolean isReadOnly() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isSynchronized(int)
     */
    public boolean isSynchronized( int depth ) {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
     */
    public boolean isTeamPrivateMember() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move( IPath destination,
                      boolean force,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move( IPath destination,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move( IProjectDescription description,
                      boolean force,
                      boolean keepHistory,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move( IProjectDescription description,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void refreshLocal( int depth,
                              IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setDerived(boolean)
     */
    public void setDerived( boolean isDerived ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setLocal( boolean flag,
                          int depth,
                          IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
     */
    public void setPersistentProperty( QualifiedName key,
                                       String value ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setReadOnly(boolean)
     */
    public void setReadOnly( boolean readOnly ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
     */
    public void setSessionProperty( QualifiedName key,
                                    Object value ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
     */
    public void setTeamPrivateMember( boolean isTeamPrivate ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void touch( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void appendContents( InputStream source,
                                boolean force,
                                boolean keepHistory,
                                IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void appendContents( InputStream source,
                                int updateFlags,
                                IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create( InputStream source,
                        boolean force,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void create( InputStream source,
                        int updateFlags,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#createLink(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createLink( IPath localLocation,
                            int updateFlags,
                            IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void delete( boolean force,
                        boolean keepHistory,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IStorage#getContents()
     */
    public InputStream getContents() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getContents(boolean)
     */
    public InputStream getContents( boolean force ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getEncoding()
     */
    public int getEncoding() {

        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#getHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IFileState[] getHistory( IProgressMonitor monitor ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#move(org.eclipse.core.runtime.IPath, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void move( IPath destination,
                      boolean force,
                      boolean keepHistory,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents( IFileState source,
                             boolean force,
                             boolean keepHistory,
                             IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents( IFileState source,
                             int updateFlags,
                             IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents( InputStream source,
                             boolean force,
                             boolean keepHistory,
                             IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void setContents( InputStream source,
                             int updateFlags,
                             IProgressMonitor monitor ) {

    }

    public void setModelNature( boolean modelNature ) {
        this.modelNature = modelNature;
    }

    private static class MockProject implements IProject {
        private boolean modelNature;

        public MockProject( boolean modelNature ) {
            this.modelNature = modelNature;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#build(int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void build( int kind,
                           IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#close(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void close( IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void create( IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void create( IProjectDescription description,
                            IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void delete( boolean deleteContent,
                            boolean force,
                            IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#getDescription()
         */
        public IProjectDescription getDescription() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#getFile(java.lang.String)
         */
        public IFile getFile( String name ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#getFolder(java.lang.String)
         */
        public IFolder getFolder( String name ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#getNature(java.lang.String)
         */
        public IProjectNature getNature( String natureId ) {

            return null;
        }

        /**
         * @see org.eclipse.core.resources.IProject#getPluginWorkingLocation(org.eclipse.core.runtime.IPluginDescriptor)
         */
        public IPath getPluginWorkingLocation( IPluginDescriptor plugin ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#getReferencedProjects()
         */
        public IProject[] getReferencedProjects() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#getReferencingProjects()
         */
        public IProject[] getReferencingProjects() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
         */
        public boolean hasNature( String natureId ) {
            boolean result = false;

            if (this.modelNature && natureId.equals(ModelerCore.NATURE_ID)) {
                result = true;
            }

            return result;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#isNatureEnabled(java.lang.String)
         */
        public boolean isNatureEnabled( String natureId ) {
            boolean result = false;

            if (this.modelNature && natureId.equals(ModelerCore.NATURE_ID)) {
                result = true;
            }

            return result;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#isOpen()
         */
        public boolean isOpen() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#move(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void move( IProjectDescription description,
                          boolean force,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#open(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void open( IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void setDescription( IProjectDescription description,
                                    int updateFlags,
                                    IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void setDescription( IProjectDescription description,
                                    IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#exists(org.eclipse.core.runtime.IPath)
         */
        public boolean exists( IPath path ) {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#findDeletedMembersWithHistory(int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public IFile[] findDeletedMembersWithHistory( int depth,
                                                      IProgressMonitor monitor ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath, boolean)
         */
        public IResource findMember( IPath path,
                                     boolean includePhantoms ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath)
         */
        public IResource findMember( IPath path ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String, boolean)
         */
        public IResource findMember( String name,
                                     boolean includePhantoms ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String)
         */
        public IResource findMember( String name ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#getFile(org.eclipse.core.runtime.IPath)
         */
        public IFile getFile( IPath path ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#getFolder(org.eclipse.core.runtime.IPath)
         */
        public IFolder getFolder( IPath path ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#members()
         */
        public IResource[] members() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#members(boolean)
         */
        public IResource[] members( boolean includePhantoms ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IContainer#members(int)
         */
        public IResource[] members( int memberFlags ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
         */
        public Object getAdapter( Class adapter ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
         */
        public void accept( IResourceProxyVisitor visitor,
                            int memberFlags ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
         */
        public void accept( IResourceVisitor visitor,
                            int depth,
                            boolean includePhantoms ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
         */
        public void accept( IResourceVisitor visitor,
                            int depth,
                            int memberFlags ) {

        }
        
        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int, int)
         */
        @SuppressWarnings("unused")
		public void accept( IResourceProxyVisitor visitor,
                            int depth,
                            int memberFlags ) {
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
         */
        public void accept( IResourceVisitor visitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void clearHistory( IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void copy( IPath destination,
                          boolean force,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void copy( IPath destination,
                          int updateFlags,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void copy( IProjectDescription description,
                          boolean force,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void copy( IProjectDescription description,
                          int updateFlags,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
         */
        public IMarker createMarker( String type ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void delete( boolean force,
                            IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void delete( int updateFlags,
                            IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
         */
        public void deleteMarkers( String type,
                                   boolean includeSubtypes,
                                   int depth ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#exists()
         */
        public boolean exists() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#findMarker(long)
         */
        public IMarker findMarker( long id ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
         */
        public IMarker[] findMarkers( String type,
                                      boolean includeSubtypes,
                                      int depth ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getFileExtension()
         */
        public String getFileExtension() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getFullPath()
         */
        public IPath getFullPath() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getLocation()
         */
        public IPath getLocation() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getMarker(long)
         */
        public IMarker getMarker( long id ) {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getModificationStamp()
         */
        public long getModificationStamp() {

            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getName()
         */
        public String getName() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getParent()
         */
        public IContainer getParent() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
         */
        public String getPersistentProperty( QualifiedName key ) {

            return null;
        }

        @Override
        public Map getPersistentProperties() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getProject()
         */
        public IProject getProject() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
         */
        public IPath getProjectRelativePath() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getRawLocation()
         */
        public IPath getRawLocation() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
         */
        public Object getSessionProperty( QualifiedName key ) {

            return null;
        }

        @Override
        public Map getSessionProperties() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getType()
         */
        public int getType() {

            return 0;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#getWorkspace()
         */
        public IWorkspace getWorkspace() {

            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isAccessible()
         */
        public boolean isAccessible() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isDerived()
         */
        public boolean isDerived() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isLinked()
         */
        public boolean isLinked() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isLocal(int)
         */
        public boolean isLocal( int depth ) {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isPhantom()
         */
        public boolean isPhantom() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isReadOnly()
         */
        public boolean isReadOnly() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isSynchronized(int)
         */
        public boolean isSynchronized( int depth ) {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
         */
        public boolean isTeamPrivateMember() {

            return false;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void move( IPath destination,
                          boolean force,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void move( IPath destination,
                          int updateFlags,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void move( IProjectDescription description,
                          boolean force,
                          boolean keepHistory,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void move( IProjectDescription description,
                          int updateFlags,
                          IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void refreshLocal( int depth,
                                  IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#setDerived(boolean)
         */
        public void setDerived( boolean isDerived ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
         */
        public void setLocal( boolean flag,
                              int depth,
                              IProgressMonitor monitor ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
         */
        public void setPersistentProperty( QualifiedName key,
                                           String value ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#setReadOnly(boolean)
         */
        public void setReadOnly( boolean readOnly ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
         */
        public void setSessionProperty( QualifiedName key,
                                        Object value ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
         */
        public void setTeamPrivateMember( boolean isTeamPrivate ) {

        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void touch( IProgressMonitor monitor ) {

        }

        public IPath getWorkingLocation( String id ) {

            return null;
        }

        public void open( int updateFlags,
                          IProgressMonitor monitor ) {

        }

        public String getDefaultCharset() {

            return null;
        }

        public String getDefaultCharset( boolean checkImplicit ) {

            return null;
        }

        public void setDefaultCharset( String charset,
                                       IProgressMonitor monitor ) {

        }

        public void setDefaultCharset( String charset ) {

        }

        public boolean contains( ISchedulingRule rule ) {

            return false;
        }

        public boolean isConflicting( ISchedulingRule rule ) {

            return false;
        }

        public long getLocalTimeStamp() {

            return 0;
        }

        public ResourceAttributes getResourceAttributes() {

            return null;
        }

        public void revertModificationStamp( long value ) {

        }

        public long setLocalTimeStamp( long value ) {

            return 0;
        }

        public void setResourceAttributes( ResourceAttributes attributes ) {

        }

        public IContentTypeMatcher getContentTypeMatcher() {

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, int,
         *      org.eclipse.core.runtime.IProgressMonitor)
         */
        public void create( IProjectDescription description,
                            int updateFlags,
                            IProgressMonitor monitor ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#createProxy()
         */
        public IResourceProxy createProxy() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#findMaxProblemSeverity(java.lang.String, boolean, int)
         */
        public int findMaxProblemSeverity( String type,
                                           boolean includeSubtypes,
                                           int depth ) {
            return 0;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#getLocationURI()
         */
        public URI getLocationURI() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#getRawLocationURI()
         */
        public URI getRawLocationURI() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#isDerived(int)
         */
        public boolean isDerived( int options ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#isHidden()
         */
        public boolean isHidden() {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#isLinked(int)
         */
        public boolean isLinked( int options ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#setHidden(boolean)
         */
        public void setHidden( boolean isHidden ) {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#isHidden(int)
         */
        @Override
        public boolean isHidden( int options ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#isTeamPrivateMember(int)
         */
        @Override
        public boolean isTeamPrivateMember( int options ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IContainer#createFilter(int, org.eclipse.core.resources.FileInfoMatcherDescription,
         *      int, org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public IResourceFilterDescription createFilter( int arg0,
                                                        FileInfoMatcherDescription arg1,
                                                        int arg2,
                                                        IProgressMonitor arg3 ) throws CoreException {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IContainer#getFilters()
         */
        @Override
        public IResourceFilterDescription[] getFilters() throws CoreException {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#getPathVariableManager()
         */
        @Override
        public IPathVariableManager getPathVariableManager() {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#isVirtual()
         */
        @Override
        public boolean isVirtual() {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IResource#setDerived(boolean, org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public void setDerived( boolean arg0,
                                IProgressMonitor arg1 ) throws CoreException {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IProject#loadSnapshot(int, java.net.URI, org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public void loadSnapshot( int arg0,
                                  URI arg1,
                                  IProgressMonitor arg2 ) throws CoreException {
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.core.resources.IProject#saveSnapshot(int, java.net.URI, org.eclipse.core.runtime.IProgressMonitor)
         */
        @Override
        public void saveSnapshot( int arg0,
                                  URI arg1,
                                  IProgressMonitor arg2 ) throws CoreException {
        }

		@Override
		public void build(IBuildConfiguration config, int kind,
				IProgressMonitor monitor) throws CoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void build(int kind, String builderName,
				Map<String, String> args, IProgressMonitor monitor)
				throws CoreException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public IBuildConfiguration getActiveBuildConfig() throws CoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IBuildConfiguration getBuildConfig(String configName)
				throws CoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IBuildConfiguration[] getBuildConfigs() throws CoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IBuildConfiguration[] getReferencedBuildConfigs(
				String configName, boolean includeMissing) throws CoreException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasBuildConfig(String configName) throws CoreException {
			// TODO Auto-generated method stub
			return false;
		}
    }

    public String getCharset() {

        return null;
    }

    public String getCharset( boolean checkImplicit ) {

        return null;
    }

    public IContentDescription getContentDescription() {

        return null;
    }

    public void setCharset( String newCharset,
                            IProgressMonitor monitor ) {

    }

    public void setCharset( String newCharset ) {

    }

    public long getLocalTimeStamp() {

        return 0;
    }

    public ResourceAttributes getResourceAttributes() {

        return null;
    }

    public void revertModificationStamp( long value ) {

    }

    public long setLocalTimeStamp( long value ) {

        return 0;
    }

    public void setResourceAttributes( ResourceAttributes attributes ) {

    }

    public boolean contains( ISchedulingRule rule ) {

        return false;
    }

    public boolean isConflicting( ISchedulingRule rule ) {

        return false;
    }

    public String getCharsetFor( Reader reader ) {

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IFile#createLink(java.net.URI, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void createLink( URI location,
                            int updateFlags,
                            IProgressMonitor monitor ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#createProxy()
     */
    public IResourceProxy createProxy() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#findMaxProblemSeverity(java.lang.String, boolean, int)
     */
    public int findMaxProblemSeverity( String type,
                                       boolean includeSubtypes,
                                       int depth ) {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#getLocationURI()
     */
    public URI getLocationURI() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#getRawLocationURI()
     */
    public URI getRawLocationURI() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isDerived(int)
     */
    public boolean isDerived( int options ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isLinked(int)
     */
    public boolean isLinked( int options ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#setHidden(boolean)
     */
    public void setHidden( boolean isHidden ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isHidden(int)
     */
    @Override
    public boolean isHidden( int options ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember(int)
     */
    @Override
    public boolean isTeamPrivateMember( int options ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#getPathVariableManager()
     */
    @Override
    public IPathVariableManager getPathVariableManager() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isVirtual()
     */
    @Override
    public boolean isVirtual() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#setDerived(boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void setDerived( boolean arg0,
                            IProgressMonitor arg1 ) throws CoreException {
    }

}
