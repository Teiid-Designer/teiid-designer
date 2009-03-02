/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc;

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
    public ResourceSet getResourceSet() {
        return null;
    }

    public URI getURI() {
        return null;
    }

    public void setURI( URI uri ) {
    }

    public EList getContents() {
        return null;
    }

    public TreeIterator getAllContents() {
        return null;
    }

    public String getURIFragment( EObject eObject ) {
        return null;
    }

    public EObject getEObject( String uriFragment ) {
        return null;
    }

    public boolean isTrackingModification() {
        return false;
    }

    public void setTrackingModification( boolean isTrackingModification ) {
    }

    public boolean isModified() {
        return false;
    }

    public void setModified( boolean isModified ) {
    }

    public boolean isLoaded() {
        return false;
    }

    public void unload() {
    }

    public EList getErrors() {
        return null;
    }

    public EList getWarnings() {
        return null;
    }

    public EList eAdapters() {
        return null;
    }

    public boolean eDeliver() {
        return false;
    }

    public void eSetDeliver( boolean deliver ) {
    }

    public void eNotify( Notification notification ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#delete(java.util.Map)
     */
    public void delete( Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#getTimeStamp()
     */
    public long getTimeStamp() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#load(java.util.Map)
     */
    public void load( Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#load(java.io.InputStream, java.util.Map)
     */
    public void load( InputStream inputStream,
                      Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#save(java.util.Map)
     */
    public void save( Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#save(java.io.OutputStream, java.util.Map)
     */
    public void save( OutputStream outputStream,
                      Map<?, ?> options ) {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.emf.ecore.resource.Resource#setTimeStamp(long)
     */
    public void setTimeStamp( long timeStamp ) {
    }
}
