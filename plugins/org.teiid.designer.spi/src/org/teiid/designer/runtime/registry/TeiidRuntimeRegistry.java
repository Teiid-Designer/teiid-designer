/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.registry;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.teiid.designer.DesignerSPIPlugin;
import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.sql.IQueryService;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * @since 8.0
 */
public class TeiidRuntimeRegistry {
    
    private static final String EXT_POINT_ID = "org.teiid.designer.spi.teiidRuntimeClient"; //$NON-NLS-1$

    private static final String FACTORY_ID = "runtimeFactory"; //$NON-NLS-1$
    
    private static final String CLASS_ATTRIBUTE_ID = "class"; //$NON-NLS-1$
    
    private static final String VERSION_ELEMENT_ID = "version"; //$NON-NLS-1$
    
    private static final String MAJOR_ATTRIBUTE_ID = "major"; //$NON-NLS-1$
    
    private static final String MINOR_ATTRIBUTE_ID = "minor"; //$NON-NLS-1$
    
    private static final String MICRO_ATTRIBUTE_ID = "micro"; //$NON-NLS-1$
    
    private static TeiidRuntimeRegistry registry;
    
    private Map<ITeiidServerVersion, IExecutionAdminFactory> factories = new HashMap<ITeiidServerVersion, IExecutionAdminFactory>();
    
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
            registry.load();
        }
        
        return registry;
    }
    
    private void load() throws Exception {
        IExtensionRegistry extRegistry = Platform.getExtensionRegistry();
        IConfigurationElement[] extensions = extRegistry.getConfigurationElementsFor(EXT_POINT_ID);
        for (IConfigurationElement element : extensions) {
            if (! FACTORY_ID.equals(element.getName()))
                continue;
            
            IExecutionAdminFactory factory = (IExecutionAdminFactory) element.createExecutableExtension(CLASS_ATTRIBUTE_ID);
            
            IConfigurationElement[] versions = element.getChildren(VERSION_ELEMENT_ID);
            for (IConfigurationElement version : versions) {
                String major = version.getAttribute(MAJOR_ATTRIBUTE_ID);
                String minor = version.getAttribute(MINOR_ATTRIBUTE_ID);
                String micro = version.getAttribute(MICRO_ATTRIBUTE_ID);
                
                ITeiidServerVersion serverVersion = new TeiidServerVersion(major, minor, micro);
                factories.put(serverVersion, factory);
            }
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
            throw new Exception(DesignerSPIPlugin.Util.getString(
                                                                 getClass().getSimpleName() + "NoExecutionAdminFactory", teiidServerVersion)); //$NON-NLS-1$
        
        return factory.getDataTypeManagerService();
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
            throw new Exception(DesignerSPIPlugin.Util.getString(
                                                                 getClass().getSimpleName() + "NoExecutionAdminFactory", teiidServerVersion)); //$NON-NLS-1$
        
        return factory.getQueryService();
    }
    
    /**
     * @param serverVersion
     * @return
     */
    private IExecutionAdminFactory search(ITeiidServerVersion serverVersion) {
        final String WILDCARD = ITeiidServerVersion.WILDCARD;
        
        IExecutionAdminFactory factory = factories.get(serverVersion);
        if (factory != null)
            return factory;
        
        for (Map.Entry<ITeiidServerVersion, IExecutionAdminFactory> entry : factories.entrySet()) {
            ITeiidServerVersion entryVersion = entry.getKey();
            
            if (! serverVersion.getMajor().equals(entryVersion.getMajor()))
                continue;
            
            String serverMinor = serverVersion.getMinor();
            String entryMinor = entryVersion.getMinor();
            
            if (! serverMinor.equals(entryMinor) && ! serverMinor.equals(WILDCARD) && ! entryMinor.equals(WILDCARD))
                continue;
            
            String serverMicro = serverVersion.getMicro();
            String entryMicro = entryVersion.getMicro();
            
            if (! serverMicro.equals(entryMicro) && ! serverMicro.equals(WILDCARD) && ! entryMicro.equals(WILDCARD))
                continue;
            
            /*
             *  Either server version or entry version contain sufficient wildcards
             *  to be considered a match
             */
            return entry.getValue();
        }
        
        return null;
    }
}
