/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.editors;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;

/**
 * NavigationMarker Instances of this class contain details necessary to restore a location in the navigation scheme.
 *
 * @since 8.0
 */
public class NavigationMarker implements IMarker {

    private Map mapAttributes;

    /**
     * Construct an instance of NavigationMarker.
     */
    public NavigationMarker() {
        super();
    }

    private Map getAttributeMap() {
        if ( mapAttributes == null ) {
            mapAttributes = new HashMap();
        }
        return mapAttributes;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#delete()
     */
    @Override
	public void delete() {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#exists()
     */
    @Override
	public boolean exists() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String)
     */
    @Override
	public Object getAttribute( String attributeName ) {
        return getAttributeMap().get( attributeName );        
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, int)
     */
    @Override
	public int getAttribute( String attributeName,
                             int defaultValue ) {
               
        Object oValue =  getAttributeMap().get( attributeName );
        if ( oValue != null && oValue instanceof Integer ) {
            return ((Integer)oValue).intValue();
        }
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, java.lang.String)
     */
    @Override
	public String getAttribute( String attributeName,
                                String defaultValue ) {
        Object oValue =  getAttributeMap().get( attributeName );
        if ( oValue != null && oValue instanceof String ) {
            return (String)oValue;
        }
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getAttribute(java.lang.String, boolean)
     */
    @Override
	public boolean getAttribute( String attributeName,
                                 boolean defaultValue ) {
        Object oValue =  getAttributeMap().get( attributeName );
        if ( oValue != null && oValue instanceof Boolean ) {
            return ((Boolean)oValue).booleanValue();
        }
        return defaultValue;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getAttributes()
     */
    @Override
	public Map getAttributes() {
        return getAttributeMap();
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getAttributes(java.lang.String[])
     */
    @Override
	public Object[] getAttributes( String[] attributeNames ) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getCreationTime()
     */
    @Override
	public long getCreationTime() {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getId()
     */
    @Override
	public long getId() {
        return 0;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getResource()
     */
    @Override
	public IResource getResource() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#getType()
     */
    @Override
	public String getType() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#isSubtypeOf(java.lang.String)
     */
    @Override
	public boolean isSubtypeOf( String superType ) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, int)
     */
    @Override
	public void setAttribute( String attributeName,
                              int value ) {
        getAttributeMap().put( attributeName, new Integer( value ) );
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, java.lang.Object)
     */
    @Override
	public void setAttribute( String attributeName,
                              Object value ) {
        getAttributeMap().put( attributeName, value );
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#setAttribute(java.lang.String, boolean)
     */
    @Override
	public void setAttribute( String attributeName,
                              boolean value ) {
        getAttributeMap().put( attributeName, new Boolean( value ) );

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#setAttributes(java.lang.String[], java.lang.Object[])
     */
    @Override
	public void setAttributes( String[] attributeNames,
                               Object[] values ) {

    }

    /* (non-Javadoc)
     * @see org.eclipse.core.resources.IMarker#setAttributes(java.util.Map)
     */
    @Override
	public void setAttributes( Map attributes ) {
        mapAttributes = attributes;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
	public Object getAdapter(Class adapter) {
        return null;
    }

}
