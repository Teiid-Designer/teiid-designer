/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import java.sql.DatabaseMetaData;

/**
 * MetadataRequest
 */
public class MetadataRequest extends MethodRequest {

    /**
     * Construct an instance of MetadataRequest.
     * @param target
     * @param name
     * @param methodName
     * @param params
     */
    public MetadataRequest(String name, final Object metadata, String methodName, Object[] params) {
        super(name, metadata, methodName, params);
    }
    
    protected DatabaseMetaData getDatabaseMetaData() {
        return (DatabaseMetaData)getTarget();
    }

}
