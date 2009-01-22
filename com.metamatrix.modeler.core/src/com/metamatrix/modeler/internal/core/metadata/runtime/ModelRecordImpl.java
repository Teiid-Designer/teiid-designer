/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metadata.runtime;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlModelAspect;

/**
 * ModelRecordImpl
 */
public class ModelRecordImpl extends com.metamatrix.metadata.runtime.impl.ModelRecordImpl {

    private static final long serialVersionUID = -8844746659454419088L;
    
	/**
	 * Flags to determine if values have been set.
	 */
	private boolean modelTypeSet;
	private boolean maxSetSizeSet;
	private boolean isVisibleSet;
	private boolean supportsDistinctSet;
	private boolean supportsOrderBySet;
	private boolean supportsOuterJoinSet;
	private boolean supportsWhereAllSet;
	private boolean primaryMetamodelUriSet;
	private boolean supportsJoinSet;

    public ModelRecordImpl(final SqlModelAspect sqlAspect, final EObject eObject) {
		super(new ModelerMetadataRecordDelegate(sqlAspect, eObject));
		setRecordType(IndexConstants.RECORD_TYPE.TABLE);
		this.eObject = eObject;
	}

	private SqlModelAspect getModelAspect() {
		return (SqlModelAspect) ((ModelerMetadataRecordDelegate)this.delegate).getSqlAspect();
	}

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#getPrimaryMetamodelUri()
     */
    @Override
    public String getPrimaryMetamodelUri() {
    	if(eObject != null && !primaryMetamodelUriSet) {
			setPrimaryMetamodelUri(getModelAspect().getPrimaryMetamodelUri((EObject)eObject));
    	}
        return super.getPrimaryMetamodelUri();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#getMaxSetSize()
     */
    @Override
    public int getMaxSetSize() {
		if(eObject != null && !maxSetSizeSet) {
			setMaxSetSize(getModelAspect().getMaxSetSize((EObject)eObject));
		}
        return super.getMaxSetSize();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#isVisible()
     */
    @Override
    public boolean isVisible() {
		if(eObject != null && !isVisibleSet) {
			setVisible(getModelAspect().isVisible((EObject)eObject));
		}
        return super.isVisible();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsDistinct()
     */
    @Override
    public boolean supportsDistinct() {
		if(eObject != null && !supportsDistinctSet) {
			setSupportsDistinct(getModelAspect().supportsDistinct((EObject)eObject));
		}
        return super.supportsDistinct();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsJoin()
     */
    @Override
    public boolean supportsJoin() {
		if(eObject != null && !supportsJoinSet) {
			setSupportsJoin(getModelAspect().supportsJoin((EObject)eObject));
		}
        return super.supportsJoin();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsOrderBy()
     */
    @Override
    public boolean supportsOrderBy() {
		if(eObject != null && !supportsOrderBySet) {
			setSupportsOrderBy(getModelAspect().supportsOrderBy((EObject)eObject));
		}
        return super.supportsOrderBy();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsOuterJoin()
     */
    @Override
    public boolean supportsOuterJoin() {
		if(eObject != null && !supportsOuterJoinSet) {
			setSupportsOuterJoin(getModelAspect().supportsOuterJoin((EObject)eObject));
		}
        return super.supportsOuterJoin();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#supportsWhereAll()
     */
    @Override
    public boolean supportsWhereAll() {
		if(eObject != null && !supportsWhereAllSet) {
			setSupportsWhereAll(getModelAspect().supportsWhereAll((EObject)eObject));
		}
        return super.supportsWhereAll();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ModelRecord#getModelType()
     */
    @Override
    public int getModelType() {
		if(eObject != null && !modelTypeSet) {
			setModelType(getModelAspect().getModelType((EObject)eObject));
		}
        return super.getModelType();
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    @Override
    public void setPrimaryMetamodelUri(String string) {
        super.setPrimaryMetamodelUri(string);
		primaryMetamodelUriSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
		isVisibleSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setMaxSetSize(int i) {
        super.setMaxSetSize(i);
		maxSetSizeSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSupportsDistinct(boolean b) {
        super.setSupportsDistinct(b);
		supportsDistinctSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSupportsJoin(boolean b) {
        super.setSupportsJoin(b);
		supportsJoinSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSupportsOrderBy(boolean b) {
        super.setSupportsOrderBy(b);
		supportsOrderBySet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSupportsOuterJoin(boolean b) {
        super.setSupportsOuterJoin(b);
		supportsOuterJoinSet = true;
    }

    /**
     * @param b
     */
    @Override
    public void setSupportsWhereAll(boolean b) {
        super.setSupportsWhereAll(b);
		supportsWhereAllSet = true;
    }

    /**
     * @param i
     */
    @Override
    public void setModelType(int i) {
        super.setModelType(i);
		modelTypeSet = true;
    }

}
