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
     * @See org.teiid.designer.core.metadata.runtime.ProcedureRecord#getParameterIDs()
     */
    public List getParameterIDs() {
        return parameterIDs;
    }

    /*
     * @See org.teiid.designer.core.metadata.runtime.ProcedureRecord#isFunction()
     */
    public boolean isFunction() {
        return isFunction;
    }

    /*
     * @See org.teiid.designer.core.metadata.runtime.ProcedureRecord#isVirtual()
     */
    public boolean isVirtual() {
        return this.isVirtual;
    }

    /*
     * @See org.teiid.designer.core.metadata.runtime.ProcedureRecord#getResultSetID()
     */
    public Object getResultSetID() {
        return resultSetID;
    }

    /*
     * @See org.teiid.designer.core.metadata.runtime.ProcedureRecord#getType()
     */
    public short getType() {
        return this.getProcedureType();
    }
    
    /** 
     * @see org.teiid.designer.metadata.runtime.ProcedureRecord#getUpdateCount()
     * @since 5.5.3
     */
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