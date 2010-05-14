/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
