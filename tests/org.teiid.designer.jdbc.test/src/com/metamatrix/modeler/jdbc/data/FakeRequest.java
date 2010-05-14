/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.modeler.jdbc.JdbcPlugin;

/**
 * FakeRequest
 */
public class FakeRequest extends Request {

    private boolean shouldSucceed = true;

    /**
     * Construct an instance of FakeRequest.
     * @param name
     */
    public FakeRequest(String name,Object target) {
        super(name,target);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.jdbc.Request#performInvocation(java.sql.Connection, com.metamatrix.modeler.internal.jdbc.Response)
     */
    @Override
    protected IStatus performInvocation(Response results) {
        return shouldSucceed ? 
               null : 
               new Status(IStatus.WARNING,JdbcPlugin.PLUGIN_ID,0,"invocation of " + getName() + " failed",null); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @return
     */
    public boolean getShouldSucceed() {
        return shouldSucceed;
    }

    /**
     * @param b
     */
    public void setShouldSucceed(boolean b) {
        shouldSucceed = b;
    }

}
