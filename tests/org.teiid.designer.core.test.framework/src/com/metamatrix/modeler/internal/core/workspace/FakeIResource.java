/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.net.URI;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import com.metamatrix.core.util.CoreArgCheck;

/**
 * @since 4.2
 */
public class FakeIResource implements IResource {

    protected final IPath path;
    protected final IPath workspaceRootPath;
    protected boolean doesExist = true;

    /**
     * Construct an instance of FakeIResource.
     */
    public FakeIResource( final String pathInWorkspace ) {
        this(pathInWorkspace, null);
    }

    /**
     * Construct an instance of FakeIResource.
     */
    public FakeIResource( final String pathInWorkspace,
                          final String workspaceRootPath ) {
        CoreArgCheck.isNotZeroLength(pathInWorkspace);
        this.path = new Path(pathInWorkspace);
        if (workspaceRootPath != null && workspaceRootPath.length() > 0) {
            this.workspaceRootPath = new Path(workspaceRootPath);
            // Assert that the pathInWorkspace starts with the workspaceRootPath
            if (this.workspaceRootPath.matchingFirstSegments(this.path) != this.workspaceRootPath.segmentCount()) {
                throw new IllegalArgumentException("The pathInWorkspace must start with the specified workspaceRootPath"); //$NON-NLS-1$
            }
        } else {
            this.workspaceRootPath = null;
        }
    }

    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int)
     * @since 4.2
     */
    public void accept( IResourceProxyVisitor visitor,
                        int memberFlags ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor)
     * @since 4.2
     */
    public void accept( IResourceVisitor visitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, boolean)
     * @since 4.2
     */
    public void accept( IResourceVisitor visitor,
                        int depth,
                        boolean includePhantoms ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceVisitor, int, int)
     * @since 4.2
     */
    public void accept( IResourceVisitor visitor,
                        int depth,
                        int memberFlags ) {
    }
    
    /**
     * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor, int, int)
     * @since 4.2
     */
    public void accept( IResourceProxyVisitor visitor,
                        int depth,
                        int memberFlags ) {
    }
    
    /**
     * @see org.eclipse.core.resources.IResource#clearHistory(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void clearHistory( IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void copy( IPath destination,
                      boolean force,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.runtime.IPath, int,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void copy( IPath destination,
                      int updateFlags,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void copy( IProjectDescription description,
                      boolean force,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#copy(org.eclipse.core.resources.IProjectDescription, int,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void copy( IProjectDescription description,
                      int updateFlags,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#createMarker(java.lang.String)
     * @since 4.2
     */
    public IMarker createMarker( String type ) {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void delete( boolean force,
                        IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#delete(int, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void delete( int updateFlags,
                        IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#deleteMarkers(java.lang.String, boolean, int)
     * @since 4.2
     */
    public void deleteMarkers( String type,
                               boolean includeSubtypes,
                               int depth ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#exists()
     * @since 4.2
     */
    public boolean exists() {
        return doesExist;
    }

    public void setExists( boolean exists ) {
        this.doesExist = exists;
    }

    /**
     * @see org.eclipse.core.resources.IResource#findMarker(long)
     * @since 4.2
     */
    public IMarker findMarker( long id ) {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#findMarkers(java.lang.String, boolean, int)
     * @since 4.2
     */
    public IMarker[] findMarkers( String type,
                                  boolean includeSubtypes,
                                  int depth ) {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getFileExtension()
     * @since 4.2
     */
    public String getFileExtension() {
        return this.path.getFileExtension();
    }

    /**
     * @see org.eclipse.core.resources.IResource#getFullPath()
     * @since 4.2
     */
    public IPath getFullPath() {
        return this.path;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getLocation()
     * @since 4.2
     */
    public IPath getLocation() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getMarker(long)
     * @since 4.2
     */
    public IMarker getMarker( long id ) {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getModificationStamp()
     * @since 4.2
     */
    public long getModificationStamp() {
        return 0;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getName()
     * @since 4.2
     */
    public String getName() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getParent()
     * @since 4.2
     */
    public IContainer getParent() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getPersistentProperty(org.eclipse.core.runtime.QualifiedName)
     * @since 4.2
     */
    public String getPersistentProperty( QualifiedName key ) {
        return null;
    }

    @Override
    public Map getPersistentProperties() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getProject()
     * @since 4.2
     */
    public IProject getProject() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getProjectRelativePath()
     * @since 4.2
     */
    public IPath getProjectRelativePath() {
        if (this.workspaceRootPath != null) {
            return this.path.removeFirstSegments(this.workspaceRootPath.segmentCount());
        }
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getRawLocation()
     * @since 4.2
     */
    public IPath getRawLocation() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName)
     * @since 4.2
     */
    public Object getSessionProperty( QualifiedName key ) {
        return null;
    }

    @Override
    public Map getSessionProperties() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getType()
     * @since 4.2
     */
    public int getType() {
        return 0;
    }

    /**
     * @see org.eclipse.core.resources.IResource#getWorkspace()
     * @since 4.2
     */
    public IWorkspace getWorkspace() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IResource#isAccessible()
     * @since 4.2
     */
    public boolean isAccessible() {
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResource#isDerived()
     * @since 4.2
     */
    public boolean isDerived() {
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResource#isLocal(int)
     * @since 4.2
     */
    public boolean isLocal( int depth ) {
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResource#isLinked()
     * @since 4.2
     */
    public boolean isLinked() {
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResource#isPhantom()
     * @since 4.2
     */
    public boolean isPhantom() {
        return false;
    }

    /**
     * @since 4.2
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResource#isSynchronized(int)
     * @since 4.2
     */
    public boolean isSynchronized( int depth ) {
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResource#isTeamPrivateMember()
     * @since 4.2
     */
    public boolean isTeamPrivateMember() {
        return false;
    }

    /**
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void move( IPath destination,
                      boolean force,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.runtime.IPath, int,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void move( IPath destination,
                      int updateFlags,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, boolean, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void move( IProjectDescription description,
                      boolean force,
                      boolean keepHistory,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#move(org.eclipse.core.resources.IProjectDescription, int,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void move( IProjectDescription description,
                      int updateFlags,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#refreshLocal(int, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void refreshLocal( int depth,
                              IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#setDerived(boolean)
     * @since 4.2
     */
    public void setDerived( boolean isDerived ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#setLocal(boolean, int, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void setLocal( boolean flag,
                          int depth,
                          IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#setPersistentProperty(org.eclipse.core.runtime.QualifiedName, java.lang.String)
     * @since 4.2
     */
    public void setPersistentProperty( QualifiedName key,
                                       String value ) {
    }

    /**
     * @since 4.2
     */
    public void setReadOnly( boolean readOnly ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#setSessionProperty(org.eclipse.core.runtime.QualifiedName, java.lang.Object)
     * @since 4.2
     */
    public void setSessionProperty( QualifiedName key,
                                    Object value ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#setTeamPrivateMember(boolean)
     * @since 4.2
     */
    public void setTeamPrivateMember( boolean isTeamPrivate ) {
    }

    /**
     * @see org.eclipse.core.resources.IResource#touch(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void touch( IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @since 4.2
     */
    public Object getAdapter( Class adapter ) {
        return null;
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
