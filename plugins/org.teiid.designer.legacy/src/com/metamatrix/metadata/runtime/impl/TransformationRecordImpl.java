/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import java.util.List;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metadata.runtime.TransformationRecord;

/**
 * TransformationRecordImpl
 */
public class TransformationRecordImpl extends AbstractMetadataRecord implements TransformationRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private String transformation;
    private Object transformedObjectID;
    private String transformationType;
    private List bindings;
    private List schemaPaths;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of TransformationRecordImpl.
     *
     */
    public TransformationRecordImpl() {
    	this(new MetadataRecordDelegate());
    }
    
    protected TransformationRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getTransformation()
     */
    public String getTransformation() {
        return transformation;
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getBindings()
     */
    public List getBindings() {
        return this.bindings;
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getSchemaPaths()
     */
    public List getSchemaPaths() {
        return schemaPaths;
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getTransformationType()
     */
    public String getTransformationType() {
        return transformationType;
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getTransformedObjectID()
     */
    public Object getTransformedObjectID() {
        return transformedObjectID;
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TransformationRecord#getType()
     */
    public String getType() {
        return this.getTransformTypeForRecordType(this.getRecordType());
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    public void setTransformation(final String string) {
        transformation = string;
    }

    /**
     * @param object
     */
    public void setTransformedObjectID(final Object object) {
        transformedObjectID = object;
    }

    /**
     * @param string
     */
    public void setTransformationType(String string) {
        transformationType = string;
    }

    /**
     * @param collection
     */
    public void setBindings(List bindings) {
        this.bindings = bindings;
    }

    /**
     * @param collection
     */
    public void setSchemaPaths(List collection) {
        schemaPaths = collection;
    }

    protected String getTransformTypeForRecordType(final char recordType) {
        switch (recordType) {
            case IndexConstants.RECORD_TYPE.SELECT_TRANSFORM: return Types.SELECT;
            case IndexConstants.RECORD_TYPE.INSERT_TRANSFORM: return Types.INSERT;
            case IndexConstants.RECORD_TYPE.UPDATE_TRANSFORM: return Types.UPDATE;
            case IndexConstants.RECORD_TYPE.DELETE_TRANSFORM: return Types.DELETE;
            case IndexConstants.RECORD_TYPE.PROC_TRANSFORM: return Types.PROCEDURE;
            case IndexConstants.RECORD_TYPE.MAPPING_TRANSFORM: return Types.MAPPING;
            default:
                throw new IllegalArgumentException("Invalid record type, for key " + recordType); //$NON-NLS-1$
        }
    }

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

}