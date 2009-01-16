/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.jdbc.metadata.impl;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.internal.jdbc.JdbcUtil;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.data.MetadataRequest;
import com.metamatrix.modeler.jdbc.data.Response;

/**
 * DisabledRequest
 */
public class DisabledRequest extends MetadataRequest {

    /**
     * Construct an instance of DisabledRequest.
     * 
     */
    public DisabledRequest( final MetadataRequest request ) {
        super(request.getName(),request.getTarget(),request.getMethodName(),request.getParameters());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.data.Request#performInvocation(com.metamatrix.modeler.jdbc.data.Response)
     */
    @Override
    protected IStatus performInvocation(final Response results) {
        final String msg = JdbcPlugin.Util.getString("DisabledRequest.RequestNotIncluded"); //$NON-NLS-1$
        return JdbcUtil.createIStatus(IStatus.INFO,msg);
    }

}
