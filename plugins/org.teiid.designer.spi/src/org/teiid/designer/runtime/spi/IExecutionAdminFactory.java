/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import java.sql.Driver;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * Factory for the creation of implementations of {@link IExecutionAdmin}
 */
public interface IExecutionAdminFactory {

    /**
     * The support level to return from the
     * {@link IExecutionAdminFactory#supports(ITeiidServerVersion)}
     * support method
     */
    enum SupportLevel {
        /**
         * Full support and tested
         */
        FULL_SUPPORT,

        /**
         * Unsupported but workable
         */
        WORKS,

        /**
         * No support and fails to work
         */
        NO_SUPPORT;
    }

    /**
     * @param version
     *
     * @return this teiid runtime client support the given teiid version
     */
    SupportLevel supports(ITeiidServerVersion version);
 
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

    /**
     * Get the teiid data type manager service
     * @param teiidVersion
     *
     * @return instance of {@link IDataTypeManagerService}
     */
    IDataTypeManagerService getDataTypeManagerService(ITeiidServerVersion teiidVersion);

    /**
     * Get the {@link Driver} for the Teiid Instance
     * @param teiidVersion
     *
     * @return the driver
     */
    Driver getTeiidDriver(ITeiidServerVersion teiidVersion);

    /**
     * Get the query service
     * @param teiidVersion
     * 
     * @return instance of {@link IQueryService}
     */
    IQueryService getQueryService(ITeiidServerVersion teiidVersion);

    /**
     * Get the location of this class' parent plugin
     *
     * @return OS specific path to the plugin location
     */
    String getRuntimePluginPath();
}
