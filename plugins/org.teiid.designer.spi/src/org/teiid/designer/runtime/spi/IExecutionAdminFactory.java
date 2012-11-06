/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

/**
 * Factory for the creation of implementations of {@link IExecutionAdmin}
 */
public interface IExecutionAdminFactory {

    /**
     * Create an {@link IExecutionAdmin} with the given {@link ITeiidServer}
     * 
     * @param teiidServer
     * 
     * @return instance of {@link IExecutionAdmin}
     * 
     * @throws Exception 
     */
    IExecutionAdmin createExecutionAdmin(ITeiidServer teiidServer) throws Exception;
}
