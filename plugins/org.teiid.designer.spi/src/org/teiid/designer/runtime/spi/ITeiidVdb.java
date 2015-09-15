/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import java.util.Collection;
import java.util.List;

/**
 * @since 8.0
 */
public interface ITeiidVdb {

    /**
     * Extension of a vdb file
     */
    static final String VDB_EXTENSION = "vdb"; //$NON-NLS-1$
    
    /**
     * Extension of a vdb file with dot appended
     */
    static final String VDB_DOT_EXTENSION = ".vdb"; //$NON-NLS-1$

    /**
     * Suffix of a dynamic vdb
     */
    static final String DYNAMIC_VDB_SUFFIX = "-vdb.xml"; //$NON-NLS-1$

    /**
     * @return the name
     */
    String getName();

    /**
     * @return deployed name
     */
    String getDeployedName();

    /**
     * @return the version
     */
    int getVersion();

    /**
     * @return <code>true</code> if this is a preview VDB
     */
    boolean isPreviewVdb();
    
    /**
     * @return <code>true</code> if this is a Dynamic VDB
     */
    boolean isDynamicVdb();
    
    /**
     * @return <code>true</code> if this is a DDL-FILE VDB
     */
    boolean isDdlFileVdb();

    /**
     * @return <code>true</code> if this VDB is active
     */
    boolean isActive();

    /**
     * @return <code>true</code> if this VDB is loading
     */
    boolean isLoading();

    /**
     * @return <code>true</code> if this VDB failed
     */
    boolean hasFailed();

    /**
     * @return <code>true</code> if this VDB is removed
     */
    boolean wasRemoved();

    /**
     * @return any validity errors
     */
    List<String> getValidityErrors();

    /**
     * Does the VDB contain any models 
     * 
     * @return <code>true</code> if the vdb has any models 
     */
    boolean hasModels();

    /**
     * Get the names of all the models in this vdb
     * 
     * @return {@link Collection} of model names
     */
    String getManifest();
    
    /**
     * Get the names of all the models in this vdb
     * 
     * @return {@link Collection} of model names
     */
    Collection<String> getModelNames();

    /**
     * @param key
     * 
     * @return value of property or null
     */
    String getPropertyValue(String key);
}
