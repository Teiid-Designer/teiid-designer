/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.search.runtime;

import java.util.Properties;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.search.runtime.AnnotatedObjectRecord;

/**
 * AnnotatedObjectRecordImpl
 * recordType|objectID|name|fullname|uri|tags|description|modelPath|metaclassURI|
 */
public class AnnotatedObjectRecordImpl extends ResourceObjectRecordImpl implements AnnotatedObjectRecord {
    
    private String description;
    private Properties properties;
    
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.SearchRecord#getRecordType()
     * @since 4.2
     */
    @Override
    public char getRecordType() {
        return IndexConstants.SEARCH_RECORD_TYPE.ANNOTATION;
    }
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.AnnotatedObjectRecord#getDescription()
     * @since 4.2
     */
    public String getDescription() {
        return this.description;
    }
    /** 
     * @see com.metamatrix.modeler.core.search.runtime.AnnotatedObjectRecord#getProperties()
     * @since 4.2
     */
    public Properties getProperties() {
        return this.properties;
    }
    
    /** 
     * @param description The description to set.
     * @since 4.2
     */
    public void setDescription(final String description) {
        this.description = description;
    }
    
    /** 
     * @param properties The properties to set.
     * @since 4.2
     */
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
}
