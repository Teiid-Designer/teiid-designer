/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import java.util.Properties;

import org.teiid.designer.vdb.connections.ConnectionFinder;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * 
 */
public class VdbSouceConnectionFinder implements ConnectionFinder {

    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     * @see org.teiid.designer.vdb.connections.ConnectionFinder#findConnectionName(java.lang.String, java.util.Properties)
     */
    @Override
    public String findConnectionName( String modelName,
                                             Properties properties ) throws Exception {
    	CoreArgCheck.isNotNull(modelName, "modelName");
    	CoreArgCheck.isNotEmpty(properties, "properties");
        ModelConnectionMapper mapper = new ModelConnectionMapper(modelName, properties);

        return mapper.findConnectionFactoryName();
    }
}
