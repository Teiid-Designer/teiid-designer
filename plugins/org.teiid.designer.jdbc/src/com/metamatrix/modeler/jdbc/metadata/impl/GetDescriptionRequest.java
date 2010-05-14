/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.data.MethodRequest;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * GetDescriptionRequest
 */
public class GetDescriptionRequest extends MethodRequest {
    
    public static final String NAME = JdbcPlugin.Util.getString("GetDescriptionRequestName"); //$NON-NLS-1$
    
    /**
     * Construct an instance of GetDescriptionRequest.
     * 
     */
    public GetDescriptionRequest( final JdbcNode node, final String methodName ) {
        super(NAME,node,methodName,new Object[]{});
    }

}
