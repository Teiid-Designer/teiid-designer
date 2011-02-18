/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

/**
 * ForeignKeyRecord
 */
public interface ForeignKeyRecord extends ColumnSetRecord {
    
    /**
     * Constants for perperties stored on a ForeignKeyRecord 
     * @since 4.3
     */
    public interface ForeignKeyRecordProperties {

        String PRIMARY_KEY_FOR_FK = "primaryKeyForForeignKey";  //$NON-NLS-1$
        
    }
    
    /**
     * Get a primary key identifier in the table
     * @return an identifier for the primary key
     */    
    Object getUniqueKeyID();    

}
