/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import java.util.Collection;

import org.teiid.designer.metadata.runtime.TableRecord;

/**
 * TableRecordImpl
 */
public class TableRecordImpl extends ColumnSetRecordImpl implements TableRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private int cardinality;
    private int tableType;
    private Object primaryKeyID;
    private Object materializedTableID;
    private Object materializedStageTableID;
    private Collection foreignKeyIDs;
    private Collection indexIDs;
    private Collection uniqueKeyIDs;
    private Collection accessPatternIDs;
    private boolean isVirtual;
    private boolean isSystem;
    private boolean isMaterialized;
    private boolean supportsUpdate;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public TableRecordImpl() {
    	this(new MetadataRecordDelegate());
    }
    
    protected TableRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#getAccessPatternIDs()
     */
    @Override
	public Collection getAccessPatternIDs() {
        return accessPatternIDs;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#getCardinality()
     */
    @Override
	public int getCardinality() {
        return cardinality;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#getForeignKeyIDs()
     */
    @Override
	public Collection getForeignKeyIDs() {
        return foreignKeyIDs;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#getIndexIDs()
     */
    @Override
	public Collection getIndexIDs() {
        return indexIDs;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#getPrimaryKeyID()
     */
    @Override
	public Object getPrimaryKeyID() {
        return primaryKeyID;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#getUniqueKeyIDs()
     */
    @Override
	public Collection getUniqueKeyIDs() {
        return uniqueKeyIDs;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#isVirtual()
     */
    @Override
	public boolean isVirtual() {
        return isVirtual;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.TableRecord#isMaterialized()
     * @since 4.2
     */
    @Override
	public boolean isMaterialized() {
        return isMaterialized;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.TableRecord#isPhysical()
     */
    @Override
	public boolean isPhysical() {
        return !isVirtual();
    }

    /**
     * @see org.teiid.designer.metadata.runtime.TableRecord#isSystem()
     */
    @Override
	public boolean isSystem() {
        return isSystem;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.TableRecord#getTableType()
     */
    @Override
	public int getTableType() {
        return tableType;
    }

    /**
     * @see org.teiid.designer.metadata.runtime.TableRecord#getMaterializedStageTableID()
     * @since 4.2
     */
    @Override
	public Object getMaterializedStageTableID() {
        return this.materializedStageTableID;
    }
    /**
     * @see org.teiid.designer.metadata.runtime.TableRecord#getMaterializedTableID()
     * @since 4.2
     */
    @Override
	public Object getMaterializedTableID() {
        return this.materializedTableID;
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.metadata.runtime.TableRecord#supportsUpdate()
     */
    @Override
	public boolean supportsUpdate() {
        return supportsUpdate;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param i
     */
    public void setCardinality(int i) {
        cardinality = i;
    }

    /**
     * @param i
     */
    public void setTableType(int i) {
        tableType = i;
    }

    /**
     * @param object
     */
    public void setPrimaryKeyID(Object keyID) {
        primaryKeyID = keyID;
    }

    /**
     * @param b
     */
    public void setSupportsUpdate(boolean b) {
        supportsUpdate = b;
    }

    /**
     * @param b
     */
    public void setVirtual(boolean b) {
		isVirtual = b;
    }

    /**
     * @param isMaterialized The isMaterialized to set.
     * @since 4.2
     */
    public void setMaterialized(boolean isMaterialized) {
        this.isMaterialized = isMaterialized;
    }

    /**
     * @param b
     */
    public void setSystem(boolean b) {
        isSystem = b;
    }

    /**
     * @param collection
     */
    public void setAccessPatternIDs(Collection collection) {
        accessPatternIDs = collection;
    }

    /**
     * @param collection
     */
    public void setForeignKeyIDs(Collection collection) {
        foreignKeyIDs = collection;
    }

    /**
     * @param materializedStageTableID The materializedStageTableID to set.
     * @since 4.2
     */
    public void setMaterializedStageTableID(Object materializedStageTableID) {
        this.materializedStageTableID = materializedStageTableID;
    }

    /**
     * @param materializedTableID The materializedTableID to set.
     * @since 4.2
     */
    public void setMaterializedTableID(Object materializedTableID) {
        this.materializedTableID = materializedTableID;
    }

    /**
     * @param collection
     */
    public void setIndexIDs(Collection collection) {
        indexIDs = collection;
    }

    /**
     * @param collection
     */
    public void setUniqueKeyIDs(Collection collection) {
        uniqueKeyIDs = collection;
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