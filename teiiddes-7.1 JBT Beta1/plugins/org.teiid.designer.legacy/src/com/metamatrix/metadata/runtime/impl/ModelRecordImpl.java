/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import com.metamatrix.core.util.ModelType;
import com.metamatrix.modeler.core.metadata.runtime.ModelRecord;

/**
 * ModelRecordImpl
 */
public class ModelRecordImpl extends AbstractMetadataRecord implements ModelRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private int modelType;
    private int maxSetSize;
    private boolean isVisible;
    private boolean supportsDistinct;
    private boolean supportsJoin;
    private boolean supportsOrderBy;
    private boolean supportsOuterJoin;
    private boolean supportsWhereAll;
    private String primaryMetamodelUri;

    public ModelRecordImpl() {
        this(new MetadataRecordDelegate());
    }

    protected ModelRecordImpl( MetadataRecordDelegate delegate ) {
        this.delegate = delegate;
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#getPrimaryMetamodelUri()
     */
    public String getPrimaryMetamodelUri() {
        return primaryMetamodelUri;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#getMaxSetSize()
     */
    public int getMaxSetSize() {
        return maxSetSize;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#isVisible()
     */
    public boolean isVisible() {
        return isVisible;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsDistinct()
     */
    public boolean supportsDistinct() {
        return supportsDistinct;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsJoin()
     */
    public boolean supportsJoin() {
        return supportsJoin;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsOrderBy()
     */
    public boolean supportsOrderBy() {
        return supportsOrderBy;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsOuterJoin()
     */
    public boolean supportsOuterJoin() {
        return supportsOuterJoin;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsWhereAll()
     */
    public boolean supportsWhereAll() {
        return supportsWhereAll;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#getModelType()
     */
    public int getModelType() {
        return modelType;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#isPhysical()
     */
    public boolean isPhysical() {
        if (getModelType() == ModelType.PHYSICAL) {
            return true;
        }
        return false;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    public void setPrimaryMetamodelUri( String string ) {
        primaryMetamodelUri = string;
    }

    /**
     * @param b
     */
    public void setVisible( boolean b ) {
        isVisible = b;
    }

    /**
     * @param i
     */
    public void setMaxSetSize( int i ) {
        maxSetSize = i;
    }

    /**
     * @param b
     */
    public void setSupportsDistinct( boolean b ) {
        supportsDistinct = b;
    }

    /**
     * @param b
     */
    public void setSupportsJoin( boolean b ) {
        supportsJoin = b;
    }

    /**
     * @param b
     */
    public void setSupportsOrderBy( boolean b ) {
        supportsOrderBy = b;
    }

    /**
     * @param b
     */
    public void setSupportsOuterJoin( boolean b ) {
        supportsOuterJoin = b;
    }

    /**
     * @param b
     */
    public void setSupportsWhereAll( boolean b ) {
        supportsWhereAll = b;
    }

    /**
     * @param i
     */
    public void setModelType( int i ) {
        modelType = i;
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
