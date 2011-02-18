/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * FakeIFile
 */
public class FakeIFile extends FakeIResource implements IFile {

    /**
     * Construct an instance of FakeIFile.
     */
    public FakeIFile( final String path ) {
        super(path);
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
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, boolean, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void appendContents( InputStream source,
                                boolean force,
                                boolean keepHistory,
                                IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#appendContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void appendContents( InputStream source,
                                int updateFlags,
                                IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, boolean, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void create( InputStream source,
                        boolean force,
                        IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#create(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void create( InputStream source,
                        int updateFlags,
                        IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#createLink(org.eclipse.core.runtime.IPath, int,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void createLink( IPath localLocation,
                            int updateFlags,
                            IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#delete(boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void delete( boolean force,
                        boolean keepHistory,
                        IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#getContents()
     * @since 4.2
     */
    public InputStream getContents() {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IFile#getContents(boolean)
     * @since 4.2
     */
    public InputStream getContents( boolean force ) {
        return null;
    }

    /**
     * @since 4.2
     */
    public int getEncoding() {
        return 0;
    }

    /**
     * @see org.eclipse.core.resources.IFile#getHistory(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public IFileState[] getHistory( IProgressMonitor monitor ) {
        return null;
    }

    /**
     * @see org.eclipse.core.resources.IFile#move(org.eclipse.core.runtime.IPath, boolean, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void move( IPath destination,
                      boolean force,
                      boolean keepHistory,
                      IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, boolean, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void setContents( InputStream source,
                             boolean force,
                             boolean keepHistory,
                             IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, boolean, boolean,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void setContents( IFileState source,
                             boolean force,
                             boolean keepHistory,
                             IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#setContents(java.io.InputStream, int, org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void setContents( InputStream source,
                             int updateFlags,
                             IProgressMonitor monitor ) {
    }

    /**
     * @see org.eclipse.core.resources.IFile#setContents(org.eclipse.core.resources.IFileState, int,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    public void setContents( IFileState source,
                             int updateFlags,
                             IProgressMonitor monitor ) {
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
    public boolean contains( ISchedulingRule rule ) {
        return false;
    }

    @Override
    public boolean isConflicting( ISchedulingRule rule ) {
        return false;
    }

    public String getCharsetFor( Reader reader ) {
        return null;
    }
}
