/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import com.metamatrix.modeler.core.metadata.runtime.ForeignKeyRecord;

/**
 * ForeignKeyRecordImpl
 */
public class ForeignKeyRecordImpl extends ColumnSetRecordImpl implements ForeignKeyRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private Object uniqueKeyID;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public ForeignKeyRecordImpl() {
    	this(new MetadataRecordDelegate());
    }
    
    protected ForeignKeyRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metadata.runtime.ForeignKeyRecord#getPrimaryKeyID()
     */
    public Object getUniqueKeyID() {
        return uniqueKeyID;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param object
     */
    public void setUniqueKeyID(Object keyID) {
        uniqueKeyID = keyID;
    }    
}