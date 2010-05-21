/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata.index;

import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.transformation.metadata.QueryMetadataContext;


/** 
 * Context for vdb and index information needed by MetadataConnectorMetadata.
 * @since 4.3
 */
public class VdbMetadataContext extends QueryMetadataContext {

    private String vdbName;
    private String vdbVersion;

    /** 
     * VdbMetadataContext
     * @param indexSelector The indexSelector to set.
     * @since 4.2
     */
    public VdbMetadataContext() {}

    /** 
     * VdbMetadataContext
     * @param indexSelector The indexSelector to set.
     * @since 4.2
     */
    public VdbMetadataContext(final IndexSelector indexSelector) {
        super(indexSelector);
    }
    
    
    /** 
     * @return Returns the vdbName.
     * @since 4.3
     */
    public String getVdbName() {
        return this.vdbName;
    }

    
    /** 
     * @param vdbName The vdbName to set.
     * @since 4.3
     */
    public void setVdbName(String vdbName) {
        this.vdbName = vdbName;
    }
    
    /** 
     * @return Returns the vdbVersion.
     * @since 4.3
     */
    public String getVdbVersion() {
        return this.vdbVersion;
    }

    
    /** 
     * @param vdbVersion The vdbVersion to set.
     * @since 4.3
     */
    public void setVdbVersion(String vdbVersion) {
        this.vdbVersion = vdbVersion;
    }
}
