/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

import com.metamatrix.common.vdb.VDBFile;
import com.metamatrix.modeler.core.index.IndexSelector;

/**
 * This record represents any file in the vdb
 * 
 * @since 4.2
 */
public interface FileRecord extends MetadataRecord, VDBFile {

    /**
     * Constants for names of accessor methods that map to fields stored on the FileRecords. Note the names do not have "get" on
     * them, this is also the nameInsource of the attributes on SystemPhysicalModel.
     * 
     * @since 4.3
     */
    public interface MetadataFieldNames {
        String PATH_IN_VDB_FIELD = "PathInVdb"; //$NON-NLS-1$
    }

    /**
     * Constants for names of accessor methods on the FileRecords. Note the names do have "get" on them, this is also the
     * nameInsource of the parameters on SystemPhysicalModel.
     * 
     * @since 4.3
     */
    public interface MetadataMethodNames {
        String PATH_IN_VDB_FIELD = "getPathInVdb"; //$NON-NLS-1$
    }

    /**
     * Return true if this is a binary file else false.
     * 
     * @return true if this is a binary file else false.
     * @since 4.2
     */
    boolean getBinary();

    /**
     * The strings representing tokens in the file in the vdb
     * 
     * @return An array of strings that are tokens in the file that need to be replaced.
     * @since 4.2
     */
    String[] getTokens();

    /**
     * Set the strings that tokens in the file that need to be replaced.
     * 
     * @param tokens The tokens in the file that need to be replaced
     * @since 4.2
     */
    void setTokens( final String[] tokens );

    /**
     * The strings used to replace tokens in the file in the vdb
     * 
     * @return The token replacement strings
     * @since 4.2
     */
    String[] getTokenReplacements();

    /**
     * Set the strings used to replace a tokens in the file in the vdb
     * 
     * @param replacements The token replacement strings
     * @since 4.2
     */
    void setTokenReplacements( final String[] replacements );

    /**
     * Get the instance of this class back
     * 
     * @return The instance of this calss
     * @since 4.2
     */
    FileRecord getFileRecord();

    /**
     * Check if the record represents a model file record.
     * 
     * @return true if the record is a model or xsd file
     * @since 4.2
     */
    boolean isModelFile();

    /**
     * Check if the record represents a index file record.
     * 
     * @return true if the record is a index file
     * @since 4.2
     */
    boolean isIndexFile();

    /**
     * Return the name of the model this file represents
     * 
     * @return The name of the model for the file
     * @since 4.2
     */
    String getModelName();

    /**
     * Set the indexSelector on this record.
     * 
     * @param selector The indexSelector used to look up file contents
     * @since 4.2
     */
    void setIndexSelector( final IndexSelector selector );
}
