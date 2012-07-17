/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * FakeResource
 */
public class FakeResource implements Resource {
    @Override
	public ResourceSet getResourceSet() {
        return null;
    }

    @Override
	public URI getURI() {
        return null;
    }

    @Override
	public void setURI( URI uri ) {
    }

    @Override
	public EList getContents() {
        return null;
    }

    @Override
	public TreeIterator getAllContents() {
        return null;
    }

    @Override
	public String getURIFragment( EObject eObject ) {
        return null;
    }

    @Override
	public EObject getEObject( String uriFragment ) {
        return null;
    }

    @Override
	public boolean isTrackingModification() {
        return false;
    }

    @Override
	public void setTrackingModification( boolean isTrackingModification ) {
    }

    @Override
	public boolean isModified() {
        return false;
    }

    @Override
	public void setModified( boolean isModified ) {
    }

    @Override
	public boolean isLoaded() {
        return false;
    }

    @Override
	public void unload() {
    }

    @Override
	public EList getErrors() {
        return null;
    }

    @Override
	public EList getWarnings() {
        return null;
    }

    @Override
	public EList eAdapters() {
        return null;
    }

    @Override
	public boolean eDeliver() {
        return false;
    }

    @Override
	public void eSetDeliver( boolean deliver ) {
    }

    @Override
	public void eNotify( Notification notification ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#delete(java.util.Map)
     */
    @Override
	public void delete( Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#getTimeStamp()
     */
    @Override
	public long getTimeStamp() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#load(java.util.Map)
     */
    @Override
	public void load( Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#load(java.io.InputStream, java.util.Map)
     */
    @Override
	public void load( InputStream inputStream,
                      Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#save(java.util.Map)
     */
    @Override
	public void save( Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#save(java.io.OutputStream, java.util.Map)
     */
    @Override
	public void save( OutputStream outputStream,
                      Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#setTimeStamp(long)
     */
    @Override
	public void setTimeStamp( long timeStamp ) {
    }
}
