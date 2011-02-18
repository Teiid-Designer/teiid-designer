/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.impl;

import com.metamatrix.modeler.core.metadata.runtime.ListEntryRecord;

/**
 * ListEntryRecordImpl
 */
public class ListEntryRecordImpl implements ListEntryRecord {
    
    private String uuid;
    private int position;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================
    
    public ListEntryRecordImpl(final String uuid, final int position) {
        this.uuid     = uuid;
        this.position = position;
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.metadata.runtime.ListEntryRecord#getPosition()
     */
    public int getPosition() {
        return this.position;
    }

    /** 
     * @see com.metamatrix.modeler.core.metadata.runtime.ListEntryRecord#getUUID()
     */
    public String getUUID() {
        return this.uuid;
    }

}
