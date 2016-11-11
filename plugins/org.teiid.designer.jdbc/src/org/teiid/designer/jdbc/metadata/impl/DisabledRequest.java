/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import org.eclipse.core.runtime.IStatus;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.JdbcUtil;
import org.teiid.designer.jdbc.data.MetadataRequest;
import org.teiid.designer.jdbc.data.MethodRequest;
import org.teiid.designer.jdbc.data.Response;

/**
 * DisabledRequest
 *
 * @since 8.0
 */
public class DisabledRequest extends MetadataRequest {

    /**
     * Construct an instance of DisabledRequest.
     * 
     */
    public DisabledRequest( final MethodRequest request ) {
        super(request.getName(),request.getTarget(),request.getMethodName(),request.getParameters());
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Request#performInvocation(org.teiid.designer.jdbc.data.Response)
     */
    @Override
    protected IStatus performInvocation(final Response results) {
        final String msg = JdbcPlugin.Util.getString("DisabledRequest.RequestNotIncluded"); //$NON-NLS-1$
        return JdbcUtil.createIStatus(IStatus.INFO,msg);
    }

}
