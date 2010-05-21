/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import com.metamatrix.modeler.core.metadata.runtime.AnnotationRecord;

/**
 * AnnotationRecordImpl
 */
public class AnnotationRecordImpl extends AbstractMetadataRecord implements AnnotationRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String description;

    public AnnotationRecordImpl() {
    	this(new MetadataRecordDelegate());
    }
    
    protected AnnotationRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.AnnotationRecord#getDescription()
     */
    public String getDescription() {
        return this.description;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(100);
        sb.append(getClass().getSimpleName());
        sb.append(" name="); //$NON-NLS-1$
        sb.append(getName());
        sb.append(", nameInSource="); //$NON-NLS-1$
        sb.append(getNameInSource());
        sb.append(", uuid="); //$NON-NLS-1$
        sb.append(getUUID());
        sb.append(", pathInModel="); //$NON-NLS-1$
        sb.append(getPath());
        return sb.toString();
    }

    /**
     * @param string
     */
    public void setDescription(final String string) {
        this.description = string;
    }

}