/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import com.metamatrix.modeler.core.metadata.runtime.PropertyRecord;

/**
 * PropertyRecordImpl
 */
public class PropertyRecordImpl extends AbstractMetadataRecord implements PropertyRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    // its true for old vdbs(version < RuntimeAdapter.ANNOTATION_TAGS_INDEX_VERSION), since they represent only extentions
    private boolean isExtention = true;

    public PropertyRecordImpl() {
    	this(new MetadataRecordDelegate());
    }

    protected PropertyRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }
    
    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.PropertyRecord#getPropertyName()
     */
    public String getPropertyName() {
        return this.name;
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.PropertyRecord#getPropertyValue()
     */
    public String getPropertyValue() {
        return this.value;
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.PropertyRecord#isExtention()
     * @since 4.2
     */
    public boolean isExtension() {
        return this.isExtention;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(getClass().getSimpleName());
        sb.append(", uuid="); //$NON-NLS-1$
        sb.append(getUUID());
        sb.append(" propName="); //$NON-NLS-1$
        sb.append(getPropertyName());
        sb.append(" propValue="); //$NON-NLS-1$
        sb.append(getPropertyValue());
        sb.append(", pathInModel="); //$NON-NLS-1$
        sb.append(getPath());
        return sb.toString();
    }

    /**
     * @param list
     */
    public void setPropertyName(final String name) {
        this.name = name;
    }

    /**
     * @param list
     */
    public void setPropertyValue(final String value) {
        this.value = value;
    }

    /**
     * @param isExtention The isExtention to set.
     * @since 4.2
     */
    public void setExtension(boolean isExtention) {
        this.isExtention = isExtention;
    }
}