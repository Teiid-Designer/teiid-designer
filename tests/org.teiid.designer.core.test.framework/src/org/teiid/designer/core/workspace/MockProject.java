/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.workspace;

import java.io.File;
import java.net.URI;
import java.util.Map;
import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
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
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentTypeMatcher;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.teiid.designer.core.ModelerCore;

public class MockProject implements IProject {
    private boolean modelNature;

    private final File nativeProjectFile;

    private final MockFileResource projectFile;

    private boolean open;
    
    public MockProject() {
        String tempDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
        this.nativeProjectFile = new File(tempDir + File.separator + ".project");  //$NON-NLS-1$
        this.projectFile = new MockFileResource(nativeProjectFile, this);
        this.projectFile.setAccessible(true);
        setOpen(true);
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#build(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void build( int kind,
                       IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#close(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void close( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void create( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void create( IProjectDescription description,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void delete( boolean deleteContent,
                        boolean force,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getDescription()
     */
    @Override
	public IProjectDescription getDescription() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getFile(java.lang.String)
     */
    @Override
	public IFile getFile( String name ) {
        return projectFile;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getFolder(java.lang.String)
     */
    @Override
	public IFolder getFolder( String name ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getNature(java.lang.String)
     */
    @Override
	public IProjectNature getNature( String natureId ) {

        return null;
    }

    /**
     * @see org.eclipse.core.resources.IProject#getPluginWorkingLocation(org.eclipse.core.runtime.IPluginDescriptor)
     */
    @Override
	public IPath getPluginWorkingLocation( IPluginDescriptor plugin ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getReferencedProjects()
     */
    @Override
	public IProject[] getReferencedProjects() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#getReferencingProjects()
     */
    @Override
	public IProject[] getReferencingProjects() {

        return null;
    }
    
    public void setModelNature(boolean modelNature) {
        this.modelNature = modelNature;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
     */
    @Override
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
    @Override
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
    @Override
	public boolean isOpen() {
        return isAccessible() && open;
    }
    
    public void setOpen(boolean open) {
        this.open = open;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#move(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void move( IProjectDescription description,
                      boolean force,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void open( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void setDescription( IProjectDescription description,
                                int updateFlags,
                                IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IProject#setDescription(org.eclipse.core.resources.IProjectDescription, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void setDescription( IProjectDescription description,
                                IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#exists(org.eclipse.core.runtime.IPath)
     */
    @Override
	public boolean exists( IPath path ) {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findDeletedMembersWithHistory(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public IFile[] findDeletedMembersWithHistory( int depth,
                                                  IProgressMonitor monitor ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath, boolean)
     */
    @Override
	public IResource findMember( IPath path,
                                 boolean includePhantoms ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(org.eclipse.core.runtime.IPath)
     */
    @Override
	public IResource findMember( IPath path ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String, boolean)
     */
    @Override
	public IResource findMember( String name,
                                 boolean includePhantoms ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#findMember(java.lang.String)
     */
    @Override
	public IResource findMember( String name ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#getFile(org.eclipse.core.runtime.IPath)
     */
    @Override
	public IFile getFile( IPath path ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#getFolder(org.eclipse.core.runtime.IPath)
     */
    @Override
	public IFolder getFolder( IPath path ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#members()
     */
    @Override
	public IResource[] members() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#members(boolean)
     */
    @Override
	public IResource[] members( boolean includePhantoms ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IContainer#members(int)
     */
    @Override
	public IResource[] members( int memberFlags ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
	public Object getAdapter( Class adapter ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
     */
    @Override
	public void accept( IResourceProxyVisitor visitor,
                        int memberFlags ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
     */
    @Override
	public void accept( IResourceVisitor visitor,
                        int depth,
                        boolean includePhantoms ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
     */
    @Override
	public void accept( IResourceVisitor visitor,
                        int depth,
                        int memberFlags ) {

    }
    
    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int, int)
     */
    @Override
	@SuppressWarnings("unused")
	public void accept( IResourceProxyVisitor visitor,
                        int depth,
                        int memberFlags ) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
     */
    @Override
	public void accept( IResourceVisitor visitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void clearHistory( IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void copy( IPath destination,
                      boolean force,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void copy( IPath destination,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void copy( IProjectDescription description,
                      boolean force,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void copy( IProjectDescription description,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
     */
    @Override
	public IMarker createMarker( String type ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void delete( boolean force,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void delete( int updateFlags,
                        IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
     */
    @Override
	public void deleteMarkers( String type,
                               boolean includeSubtypes,
                               int depth ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#exists()
     */
    @Override
	public boolean exists() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarker(long)
     */
    @Override
	public IMarker findMarker( long id ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
     */
    @Override
	public IMarker[] findMarkers( String type,
                                  boolean includeSubtypes,
                                  int depth ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getFileExtension()
     */
    @Override
	public String getFileExtension() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getFullPath()
     */
    @Override
	public IPath getFullPath() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getLocation()
     */
    @Override
	public IPath getLocation() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getMarker(long)
     */
    @Override
	public IMarker getMarker( long id ) {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getModificationStamp()
     */
    @Override
	public long getModificationStamp() {

        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getName()
     */
    @Override
	public String getName() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getParent()
     */
    @Override
	public IContainer getParent() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
     */
    @Override
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
    @Override
	public IProject getProject() {
        return this;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
     */
    @Override
	public IPath getProjectRelativePath() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getRawLocation()
     */
    @Override
	public IPath getRawLocation() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
     */
    @Override
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
    @Override
	public int getType() {

        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#getWorkspace()
     */
    @Override
	public IWorkspace getWorkspace() {

        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isAccessible()
     */
    @Override
	public boolean isAccessible() {
        return projectFile.isAccessible();
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isDerived()
     */
    @Override
	public boolean isDerived() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLinked()
     */
    @Override
	public boolean isLinked() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isLocal(int)
     */
    @Override
	public boolean isLocal( int depth ) {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isPhantom()
     */
    @Override
	public boolean isPhantom() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isReadOnly()
     */
    @Override
	public boolean isReadOnly() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isSynchronized(int)
     */
    @Override
	public boolean isSynchronized( int depth ) {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
     */
    @Override
	public boolean isTeamPrivateMember() {

        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void move( IPath destination,
                      boolean force,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void move( IPath destination,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void move( IProjectDescription description,
                      boolean force,
                      boolean keepHistory,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void move( IProjectDescription description,
                      int updateFlags,
                      IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void refreshLocal( int depth,
                              IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setDerived(boolean)
     */
    @Override
	public void setDerived( boolean isDerived ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void setLocal( boolean flag,
                          int depth,
                          IProgressMonitor monitor ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
     */
    @Override
	public void setPersistentProperty( QualifiedName key,
                                       String value ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setReadOnly(boolean)
     */
    @Override
	public void setReadOnly( boolean readOnly ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
     */
    @Override
	public void setSessionProperty( QualifiedName key,
                                    Object value ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
     */
    @Override
	public void setTeamPrivateMember( boolean isTeamPrivate ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void touch( IProgressMonitor monitor ) {

    }

    @Override
	public IPath getWorkingLocation( String id ) {

        return null;
    }

    @Override
	public void open( int updateFlags,
                      IProgressMonitor monitor ) {

    }

    @Override
	public String getDefaultCharset() {

        return null;
    }

    @Override
	public String getDefaultCharset( boolean checkImplicit ) {

        return null;
    }

    @Override
	public void setDefaultCharset( String charset,
                                   IProgressMonitor monitor ) {

    }

    @Override
	public void setDefaultCharset( String charset ) {

    }

    @Override
	public boolean contains( ISchedulingRule rule ) {

        return false;
    }

    @Override
	public boolean isConflicting( ISchedulingRule rule ) {

        return false;
    }

    @Override
	public long getLocalTimeStamp() {

        return 0;
    }

    @Override
	public ResourceAttributes getResourceAttributes() {

        return null;
    }

    @Override
	public void revertModificationStamp( long value ) {

    }

    @Override
	public long setLocalTimeStamp( long value ) {

        return 0;
    }

    @Override
	public void setResourceAttributes( ResourceAttributes attributes ) {

    }

    @Override
	public IContentTypeMatcher getContentTypeMatcher() {

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IProject#create(org.eclipse.core.resources.IProjectDescription, int,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void create( IProjectDescription description,
                        int updateFlags,
                        IProgressMonitor monitor ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#createProxy()
     */
    @Override
	public IResourceProxy createProxy() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#findMaxProblemSeverity(java.lang.String, boolean, int)
     */
    @Override
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
    @Override
	public URI getLocationURI() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#getRawLocationURI()
     */
    @Override
	public URI getRawLocationURI() {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isDerived(int)
     */
    @Override
	public boolean isDerived( int options ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isHidden()
     */
    @Override
	public boolean isHidden() {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#isLinked(int)
     */
    @Override
	public boolean isLinked( int options ) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.resources.IResource#setHidden(boolean)
     */
    @Override
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

	@Override
	public void clearCachedDynamicReferences() {
		// TODO Auto-generated method stub
		
	}
}
