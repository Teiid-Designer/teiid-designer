/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata;

import java.util.Collection;
import java.util.Map;
import com.metamatrix.connector.metadata.internal.IObjectSource;

/**
 * Holds two object sources and routes queries to them based on the suffix of the group name.
 */
public class MultiObjectSource implements IObjectSource {
    private IObjectSource primaryObjectSource;
    private IObjectSource secondaryObjectSource;
    private String secondaryGroupNameSuffix;
    
    public MultiObjectSource(IObjectSource primaryObjectSource, String secondaryGroupNameSuffix, IObjectSource objectSource) {
        this.primaryObjectSource = primaryObjectSource;
        this.secondaryGroupNameSuffix = secondaryGroupNameSuffix;
        this.secondaryObjectSource = objectSource;
    }
    
    /* 
     * @see com.metamatrix.connector.metadata.internal.ISimpleObjectSource#getObjects(java.lang.String, java.util.Map)
     */
    public Collection getObjects(String groupName, Map criteria) {
        if (groupName.endsWith(secondaryGroupNameSuffix)) {
            return secondaryObjectSource.getObjects(groupName, criteria);
        }
        return primaryObjectSource.getObjects(groupName, criteria);
    }
}
