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
 * ColumnSetRecord
 */
public interface ColumnSetRecord extends MetadataRecord {

    /**
     * Constants for perperties stored on a ColumnSetRecord 
     * @since 4.3
     */
    public interface ColumnSetRecordProperties {

        String ELEMENTS_IN_INDEX = "elementsInIndex";  //$NON-NLS-1$
        String ELEMENTS_IN_KEY = "elementsInKey";  //$NON-NLS-1$
        String ELEMENTS_IN_ACCESS_PTTRN = "elementsInAccPttrn";  //$NON-NLS-1$
    }

    /**
     * Get a list of identifiers for the columns in the record
     * @return a list of identifiers
     */
    List getColumnIDs();

    /**
     * Get a list of identifiers for the columns in the record
     * @return a list of identifiers
     */
    ListEntryRecord[] getColumnIdEntries();

    /**
     * Return true if the record represents a primary key
     * @return
     */
    boolean isPrimaryKey();

    /**
     * Return true if the record represents a index
     * @return
     */
    boolean isIndex();

    /**
     * Return true if the record represents a access pattern
     * @return
     */
    boolean isAccessPattern();

    /**
     * Return true if the record represents a unique key
     * @return
     */
    boolean isUniqueKey();

    /**
     * Return true if the record represents a result set
     * @return
     */
    boolean isResultSet();
    
    /**
     * Return short indicating the type of KEY it is. 
     * @return short
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.KEY_TYPES
     */
    short getType();
}
