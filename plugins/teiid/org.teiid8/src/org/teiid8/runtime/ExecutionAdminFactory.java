/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid8.runtime;

import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory;
import org.teiid.designer.runtime.spi.ITeiidServer;

/**
 *
 */
public class ExecutionAdminFactory implements IExecutionAdminFactory {

    @Override
    public IExecutionAdmin createExecutionAdmin(ITeiidServer teiidServer) throws Exception {
        return new ExecutionAdmin(teiidServer);
    }

}
