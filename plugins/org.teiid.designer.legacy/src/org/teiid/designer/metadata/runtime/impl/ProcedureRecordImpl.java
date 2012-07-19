/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.impl;

import java.util.List;

import org.teiid.designer.metadata.runtime.MetadataConstants;
import org.teiid.designer.metadata.runtime.ProcedureRecord;

/**
 * ProcedureRecordImpl
 *
 * @since 8.0
 */
public class ProcedureRecordImpl extends AbstractMetadataRecord implements ProcedureRecord {
    
    /**
     */
    private static final long serialVersionUID = 1L;
    private List parameterIDs;
    private boolean isFunction;
    private boolean isVirtual;
    private Object resultSetID;
    private int updateCount;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ProcedureRecordImpl() {
    	this(new MetadataRecordDelegate());
    }
    
    protected ProcedureRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureRecord#getParameterIDs()
     */
    @Override
	public List getParameterIDs() {
        return parameterIDs;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureRecord#isFunction()
     */
    @Override
	public boolean isFunction() {
        return isFunction;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureRecord#isVirtual()
     */
    @Override
	public boolean isVirtual() {
        return this.isVirtual;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureRecord#getResultSetID()
     */
    @Override
	public Object getResultSetID() {
        return resultSetID;
    }

    /*
     * @see org.teiid.designer.metadata.runtime.ProcedureRecord#getType()
     */
    @Override
	public short getType() {
        return this.getProcedureType();
    }
    
    /** 
     * @see org.teiid.designer.metadata.runtime.ProcedureRecord#getUpdateCount()
     * @since 5.5.3
     */
    @Override
	public int getUpdateCount() {
        return this.updateCount;
    }

    /**
     * @param list
     */
    public void setParameterIDs(List list) {
        parameterIDs = list;
    }

    /**
     * @param object
     */
    public void setResultSetID(Object object) {
        resultSetID = object;
    }

    /**
     * @param b
     */
    public void setFunction(boolean b) {
        isFunction = b;
    }

    /**
     * @param b
     */
    public void setVirtual(boolean b) {
        isVirtual = b;
    }
    
    public void setUpdateCount(int count) {
    	this.updateCount = count;
    }

    protected short getProcedureType() {
        if (isFunction()) {
            return MetadataConstants.PROCEDURE_TYPES.FUNCTION;
        }
        if (isVirtual()) {
            return MetadataConstants.PROCEDURE_TYPES.STORED_QUERY;
        }
        return MetadataConstants.PROCEDURE_TYPES.STORED_PROCEDURE;
    }

}