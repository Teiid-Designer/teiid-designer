/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.registry;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.Messages;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.registry.AbstractExtensionRegistry;
import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory.SupportLevel;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * @since 8.0
 */
public class TeiidRuntimeRegistry extends AbstractExtensionRegistry<IExecutionAdminFactory, IExecutionAdminFactory> {
    
    private static final String EXT_POINT_ID = "org.teiid.designer.spi.teiidRuntimeClient"; //$NON-NLS-1$

    private static final String FACTORY_ID = "runtimeFactory"; //$NON-NLS-1$

    private static TeiidRuntimeRegistry registry;

    /**
     * Get the singleton instance of this registry
     * 
     * @return singleton {@link TeiidRuntimeRegistry}
     * 
     * @throws Exception
     */
    public static TeiidRuntimeRegistry getInstance() throws Exception {
        if (registry == null) {
            registry = new TeiidRuntimeRegistry();
        }

        return registry;
    }

    private TeiidRuntimeRegistry() throws Exception {
        super(EXT_POINT_ID, FACTORY_ID);
    }

    @Override
    protected void register(IConfigurationElement configurationElement, IExecutionAdminFactory adminFactory) {
        register(adminFactory, adminFactory);
    }

    /**
     * Get an {@link IExecutionAdminFactory} applicable for the given server version
     *
     * @param teiidServerVersion
     *
     * @return instance of {@link IExecutionAdminFactory}
     * @throws Exception
     */
    public IExecutionAdminFactory getExecutionAdminFactory(ITeiidServerVersion teiidServerVersion) throws Exception {
        IExecutionAdminFactory factory = search(teiidServerVersion);
        if (factory == null)
            throw new Exception(NLS.bind(Messages.NoExecutionAdminFactory, teiidServerVersion));

        return factory;
    }

    /**
     * Get an {@link IExecutionAdmin} applicable for the given server
     * 
     * @param teiidServer
     * 
     * @return instance of {@link IExecutionAdmin}
     * @throws Exception 
     */
    public IExecutionAdmin getExecutionAdmin(ITeiidServer teiidServer) throws Exception {
        IExecutionAdminFactory factory = getExecutionAdminFactory(teiidServer.getServerVersion());
        return factory.createExecutionAdmin(teiidServer);
    }

    /**
     * Get the teiid data type manager service
     * 
     * @param teiidServerVersion
     * 
     * @return instance of {@link IDataTypeManagerService}
     * @throws Exception 
     */
    public IDataTypeManagerService getDataTypeManagerService(ITeiidServerVersion teiidServerVersion) throws Exception {
        IExecutionAdminFactory factory = search(teiidServerVersion);
        if (factory == null)
            throw new Exception(NLS.bind(Messages.NoExecutionAdminFactory, teiidServerVersion));
        
        return factory.getDataTypeManagerService(teiidServerVersion);
    }

    /**
     * Get the Teiid Driver for the given server version
     *
     * @param teiidServerVersion
     *
     * @return the Teiid Driver
     * @throws Exception
     */
    public Driver getTeiidDriver(ITeiidServerVersion teiidServerVersion) throws Exception {
        IExecutionAdminFactory factory = search(teiidServerVersion);
        if (factory == null)
            throw new Exception(NLS.bind(Messages.NoExecutionAdminFactory, teiidServerVersion));

        return factory.getTeiidDriver(teiidServerVersion);
    }

    /**
     * Get the teiid sql syntax service
     * 
     * @param teiidServerVersion
     * 
     * @return instance of {@link IQueryService}
     * @throws Exception 
     */
    public IQueryService getQueryService(ITeiidServerVersion teiidServerVersion) throws Exception {
        IExecutionAdminFactory factory = search(teiidServerVersion);
        if (factory == null)
            throw new Exception(NLS.bind(Messages.NoExecutionAdminFactory, teiidServerVersion));
        
        return factory.getQueryService(teiidServerVersion);
    }
    
    /**
     * @param serverVersion
     * @return
     */
    private IExecutionAdminFactory search(ITeiidServerVersion serverVersion) {
        /*
         * First try and find a factory that fully supports the given teiid version
         */
        for (Map.Entry<IExecutionAdminFactory, IExecutionAdminFactory> entry : getRegisteredEntries()) {
            IExecutionAdminFactory factory = entry.getValue();
            if (SupportLevel.FULL_SUPPORT.equals(factory.supports(serverVersion)))
                return factory;
        }

        /*
         * Cannot find one with full support so try finding a factory that works
         * with the given teiid version but is not fully tested
         */
        for (Map.Entry<IExecutionAdminFactory, IExecutionAdminFactory> entry : getRegisteredEntries()) {
            IExecutionAdminFactory factory = entry.getValue();
            if (SupportLevel.WORKS.equals(factory.supports(serverVersion)))
                return factory;
        }
        
        return null;
    }
    
    /**
     * Retrieve all registered server versions
     * 
     * @return unmodifiable collection
     */
    public Collection<ITeiidServerVersion> getSupportedVersions() {
        List<ITeiidServerVersion> versions = new ArrayList<ITeiidServerVersion>();
        for (TeiidServerVersion.Version version : TeiidServerVersion.Version.values()) {
            Version teiidDefault = TeiidServerVersion.Version.TEIID_DEFAULT;
            if (teiidDefault.equals(version))
                continue; // don't need the default

            if (version.get().isGreaterThan(teiidDefault.get()))
                continue; // anything greater than default is not supported

            versions.add(version.get());
        }

        return versions;
    }
}
