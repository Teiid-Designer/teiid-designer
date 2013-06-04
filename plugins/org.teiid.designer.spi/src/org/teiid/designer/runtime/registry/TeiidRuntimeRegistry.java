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
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.Messages;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.registry.AbstractExtensionRegistry;
import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * @since 8.0
 */
public class TeiidRuntimeRegistry extends AbstractExtensionRegistry<ITeiidServerVersion, IExecutionAdminFactory> {
    
    private static final String EXT_POINT_ID = "org.teiid.designer.spi.teiidRuntimeClient"; //$NON-NLS-1$

    private static final String FACTORY_ID = "runtimeFactory"; //$NON-NLS-1$

    private static final String VERSION_ELEMENT_ID = "version"; //$NON-NLS-1$
    
    private static final String MAJOR_ATTRIBUTE_ID = "major"; //$NON-NLS-1$
    
    private static final String MINOR_ATTRIBUTE_ID = "minor"; //$NON-NLS-1$
    
    private static final String MICRO_ATTRIBUTE_ID = "micro"; //$NON-NLS-1$
    
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
        IConfigurationElement[] versions = configurationElement.getChildren(VERSION_ELEMENT_ID);
        for (IConfigurationElement version : versions) {
            String major = version.getAttribute(MAJOR_ATTRIBUTE_ID);
            String minor = version.getAttribute(MINOR_ATTRIBUTE_ID);
            String micro = version.getAttribute(MICRO_ATTRIBUTE_ID);

            ITeiidServerVersion serverVersion = new TeiidServerVersion(major, minor, micro);
            register(serverVersion, adminFactory);
        }
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
        IExecutionAdminFactory factory = search(teiidServer.getServerVersion());
        if (factory == null)
            throw new Exception("No ExecutionAdmin factory registered for teiid server version " + teiidServer.getServerVersion()); //$NON-NLS-1$
        
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
        
        return factory.getDataTypeManagerService();
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

        return factory.getTeiidDriver();
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
        
        return factory.getQueryService();
    }
    
    /**
     * @param serverVersion
     * @return
     */
    private IExecutionAdminFactory search(ITeiidServerVersion serverVersion) {
        
        IExecutionAdminFactory factory = getRegistered(serverVersion);
        if (factory != null)
            return factory;
        
        for (Map.Entry<ITeiidServerVersion, IExecutionAdminFactory> entry : getRegisteredEntries()) {
            ITeiidServerVersion entryVersion = entry.getKey();
            
            if (serverVersion.compareTo(entryVersion))
                return entry.getValue();
        }
        
        return null;
    }
    
    /**
     * Retrieve all registered server versions
     * 
     * @return unmodifiable collection
     */
    public Collection<ITeiidServerVersion> getRegisteredServerVersions() {
        return getRegisteredKeys();
    }
}
