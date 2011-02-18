/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

import java.util.List;

/**
 * UniqueKeyRecord
 */
public interface UniqueKeyRecord extends ColumnSetRecord {

    /**
     * Get a foreign key identifiers reference by the uniqekey
     * @return an identifiers for the foreign keys
     */    
    List getForeignKeyIDs();
}
