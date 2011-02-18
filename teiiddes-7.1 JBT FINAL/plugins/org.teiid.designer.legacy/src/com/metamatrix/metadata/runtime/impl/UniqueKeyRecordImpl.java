/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import java.util.List;
import com.metamatrix.modeler.core.metadata.runtime.UniqueKeyRecord;

/**
 * UniqueKeyRecordImpl
 */
public class UniqueKeyRecordImpl extends ColumnSetRecordImpl implements UniqueKeyRecord {

    /**
     */
    private static final long serialVersionUID = 1L;
    private List foreignKeyIDs;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    public UniqueKeyRecordImpl() {
    	this(new MetadataRecordDelegate());
    }
    
    protected UniqueKeyRecordImpl(MetadataRecordDelegate delegate) {
    	this.delegate = delegate;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /*
     * @see com.metamatrix.modeler.core.metadata.runtime.UniqueKeyRecord#getForeignKeyIDs()
     */
    public List getForeignKeyIDs() {
        return foreignKeyIDs;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param list
     */
    public void setForeignKeyIDs(List list) {
        foreignKeyIDs = list;
    }
}