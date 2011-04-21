/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import java.util.Collection;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;

/**
 * TableRecordImpl
 */
public class TableRecordImpl extends com.metamatrix.metadata.runtime.impl.TableRecordImpl {

    private static final long serialVersionUID = 5380577513142295779L;

    /**
	 * Flags to determine if values have been set.
	 */
	private boolean cardinalitySet;
	private boolean tableTypeSet;
	private boolean primaryKeyIDSet;
	private boolean foreignKeyIDsSet;
	private boolean indexIDsSet;
	private boolean uniqueKeyIDsSet;
	private boolean accessPatternIDsSet;
	private boolean isVirtualSet;
	private boolean isMaterializedSet;
	private boolean isSystemSet;
	private boolean supportsUpdateSet;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public TableRecordImpl(final SqlTableAspect sqlAspect, final EObject eObject) {
        super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		setRecordType(IndexConstants.RECORD_TYPE.TABLE);
		this.eObject = eObject;
	}

	private SqlTableAspect getTableAspect() {
		return (SqlTableAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getAccessPatternIDs()
     */
    @Override
    public Collection getAccessPatternIDs() {
    	if(super.eObject != null && !accessPatternIDsSet) {
			Collection accessPatterns = getTableAspect().getAccessPatterns((EObject)this.eObject);
			setAccessPatternIDs(((ModelerMetadataRecordDelegate)this.delegate).getObjectIDs(accessPatterns));
    	}
        return super.getAccessPatternIDs();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getCardinality()
     */
    @Override
    public int getCardinality() {
		if(super.eObject != null && !cardinalitySet) {
			setCardinality(getTableAspect().getCardinality((EObject)this.eObject));
		}
        return super.getCardinality();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getForeignKeyIDs()
     */
    @Override
    public Collection getForeignKeyIDs() {
		if(super.eObject != null && !foreignKeyIDsSet) {
			Collection uniqueKeys = getTableAspect().getForeignKeys((EObject)this.eObject);
			setForeignKeyIDs(((ModelerMetadataRecordDelegate)this.delegate).getObjectIDs(uniqueKeys));
		}
        return super.getForeignKeyIDs();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getIndexIDs()
     */
    @Override
    public Collection getIndexIDs() {
		if(super.eObject != null && !indexIDsSet) {
			Collection indexes = getTableAspect().getIndexes((EObject)this.eObject);
			setIndexIDs(((ModelerMetadataRecordDelegate)this.delegate).getObjectIDs(indexes));
		}
        return super.getIndexIDs();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getPrimaryKeyID()
     */
    @Override
    public Object getPrimaryKeyID() {
		if(super.eObject != null && !primaryKeyIDSet) {
			setPrimaryKeyID(((ModelerMetadataRecordDelegate)this.delegate).getObjectID(getTableAspect().getPrimaryKey((EObject)this.eObject)));
		}
        return super.getPrimaryKeyID();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getUniqueKeyIDs()
     */
    @Override
    public Collection getUniqueKeyIDs() {
		if(super.eObject != null && !uniqueKeyIDsSet) {
			Collection uniqueKeys = getTableAspect().getUniqueKeys((EObject)this.eObject);
			setUniqueKeyIDs(((ModelerMetadataRecordDelegate)this.delegate).getObjectIDs(uniqueKeys));
		}
        return super.getUniqueKeyIDs();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#isVirtual()
     */
    @Override
    public boolean isVirtual() {
		if(super.eObject != null && !isVirtualSet) {
			setVirtual(getTableAspect().isVirtual((EObject)this.eObject));
		}
        return super.isVirtual();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#isMaterialized()
     * @since 4.2
     */
    @Override
    public boolean isMaterialized() {
		if(super.eObject != null && !isMaterializedSet) {
			setMaterialized(getTableAspect().isMaterialized((EObject)this.eObject));
		}
        return super.isMaterialized();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#isSystem()
     */
    @Override
    public boolean isSystem() {
		if(super.eObject != null && !isSystemSet) {
			setSystem(getTableAspect().isSystem((EObject)this.eObject));
		}
        return super.isSystem();
    }

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getTableType()
     */
    @Override
    public int getTableType() {
		if(super.eObject != null && !tableTypeSet) {
			setTableType(getTableAspect().getTableType((EObject)this.eObject));
		}
        return super.getTableType();
    }

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#getMaterializedTableID()
     * @since 4.2
     */
    @Override
    public Object getMaterializedTableID() {
		if(super.eObject != null && !tableTypeSet) {
			setTableType(getTableAspect().getTableType((EObject)this.eObject));
		}
        return super.getMaterializedStageTableID();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.TableRecord#supportsUpdate()
     */
    @Override
    public boolean supportsUpdate() {
		if(super.eObject != null && !supportsUpdateSet) {
			setSupportsUpdate(getTableAspect().supportsUpdate((EObject)this.eObject));
		}
        return super.supportsUpdate();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param i
     */
    @Override
    public void setCardinality(int i) {
        super.setCardinality(i);
		accessPatternIDsSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setTableType(int i) {
        super.setTableType(i);
		tableTypeSet = true;
    }

    /**
     * @param object
     */
    @Override
    public void setPrimaryKeyID(Object keyID) {
        super.setPrimaryKeyID(keyID);
		primaryKeyIDSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSupportsUpdate(boolean b) {
        super.setSupportsUpdate(b);
		supportsUpdateSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setVirtual(boolean b) {
		super.setVirtual(b);
		isVirtualSet = true;
    }

    /**
     * @param isMaterialized The isMaterialized to set.
     * @since 4.2
     */
    @Override
    public void setMaterialized(boolean isMaterialized) {
        super.setMaterialized(isMaterialized);
        this.isMaterializedSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSystem(boolean b) {
        super.setSystem(b);
		isSystemSet = true;
    }

    /**
     * @param collection
     */
    @Override
    public void setAccessPatternIDs(Collection collection) {
        super.setAccessPatternIDs(collection);
		accessPatternIDsSet = true;
    }

    /**
     * @param collection
     */
    @Override
    public void setForeignKeyIDs(Collection collection) {
        super.setForeignKeyIDs(collection);
		foreignKeyIDsSet = true;
    }

    /**
     * @param collection
     */
    @Override
    public void setIndexIDs(Collection collection) {
        super.setIndexIDs(collection);
		indexIDsSet = true;
    }

    /**
     * @param collection
     */
    @Override
    public void setUniqueKeyIDs(Collection collection) {
        super.setUniqueKeyIDs(collection);
		uniqueKeyIDsSet = true;
    }

}
