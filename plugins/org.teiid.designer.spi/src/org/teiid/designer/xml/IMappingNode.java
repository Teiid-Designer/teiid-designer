/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.xml;






/**
 *
 */
public interface IMappingNode {
    
    /** The default build in type */
    String DEFAULT_BUILT_IN_TYPE = ""; //$NON-NLS-1$
    
    /** The default minimum bound of the cardinality of a node. */
    Integer DEFAULT_CARDINALITY_MINIMUM_BOUND = new Integer(1);
    
    /** The default maximum bound of the cardinality of a node. */
    Integer DEFAULT_CARDINALITY_MAXIMUM_BOUND = new Integer(1);
    
    /** The default value for recursion limit */
    Integer DEFAULT_RECURSION_LIMIT = new Integer(10);
    
    /** The default value for is nillable */
    Boolean DEFAULT_IS_NILLABLE = Boolean.FALSE;
    
    /** The default value for exception on recursion limit */
    Boolean DEFAULT_EXCEPTION_ON_RECURSION_LIMIT = Boolean.FALSE;
    
    /**
     * PRESERVE -No normalization is done, the value is not changed.
     * REPLACE - All occurrences of tab, line feed and carriage return are replaced with space
     * COLLAPSE - After the processing implied by replace, contiguous sequences of space are 
     * collapsed to a single space, and leading and trailing spaces are removed.
     */
    String NORMALIZE_TEXT_PRESERVE = "preserve"; //$NON-NLS-1$
    String NORMALIZE_TEXT_REPLACE  = "replace"; //$NON-NLS-1$
    String NORMALIZE_TEXT_COLLAPSE = "collapse"; //$NON-NLS-1$
    
    /** The default value for normalize text */
    String DEFAULT_NORMALIZE_TEXT = NORMALIZE_TEXT_PRESERVE;
    
    
    
    /**
     * @param node
     * 
     * @return added node
     */
    IMappingNode addChild(IMappingNode node);

    /**
     * @param excludeFromDocument
     */
    void setExclude(boolean excludeFromDocument);

    /**
     * @param source
     */
    void setSource(String source);

    /**
     * @param stagingTable
     */
    void addStagingTable(String stagingTable);

}
