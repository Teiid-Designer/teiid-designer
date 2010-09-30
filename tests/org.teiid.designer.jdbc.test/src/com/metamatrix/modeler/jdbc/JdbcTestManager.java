/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.metamatrix.modeler.internal.jdbc.JdbcManagerImpl;
import com.metamatrix.modeler.jdbc.impl.JdbcPackageImpl;

/**
 * JdbcTestManager
 */
public final class JdbcTestManager {

    public static final String ORACLE_DRIVER_NAME = "Oracle JDBC"; //$NON-NLS-1$
    public static final String MM_ORACLE_DRIVER_NAME = "MetaMatrix JDBC for Oracle"; //$NON-NLS-1$

    private final ResourceSet resourceSet;
    private final Object managerLock = new Object();
    private Resource resource;
    private JdbcManagerImpl manager;

    /**
     * Construct an instance of JdbcTestManager.
     */
    public JdbcTestManager() {
        super();
        this.resourceSet = new ResourceSetImpl();
        initialize();
    }

    public void shutdown() {
        if ( this.resource != null ) {
            // Go through and remove all of the resources ...
            final List resources = resourceSet.getResources();
            final Iterator iter = resources.iterator();
            while (iter.hasNext()) {
                final Resource resource = (Resource)iter.next();
                resource.getContents().clear();
            }
            resources.clear();
            this.resource = null;
            this.manager = null;
        }
    }

    /**
     * Return the JdbcManager that is loaded with common {@link JdbcDriver} and {@link JdbcSource} instances
     * needed for unit testing.
     * @return
     */
    public JdbcManager getJdbcManager() {
        if ( manager == null ) {
            synchronized(managerLock) {
                if ( manager == null ) {
                    manager = new JdbcManagerImpl("Test Manager"); //$NON-NLS-1$
                    manager.start();
                }
            }
        }
        return manager;
    }

    protected void initialize() {
        // Initialize the metamodels ...
        EcorePackageImpl.init();
        JdbcPackageImpl.init();

        // Register a resource factory ...
        Resource.Factory.Registry reg = this.resourceSet.getResourceFactoryRegistry();
        if (reg==null) {
            reg = new ResourceFactoryRegistryImpl();
            this.resourceSet.setResourceFactoryRegistry(reg);
        }
        final Map m = reg.getExtensionToFactoryMap();
        m.put("ecore",new XMIResourceFactoryImpl()); //$NON-NLS-1$
        m.put("xmi",new XMIResourceFactoryImpl()); //$NON-NLS-1$
//            if (isExtension) {
//                m=reg.getExtensionToFactoryMap();
//            } else {
//                m=reg.getProtocolToFactoryMap();
//            }
//            m.put(key, f);
    }

    public JdbcDriver getOracleDriver() {
        final JdbcDriver[] drivers = getJdbcManager().findDrivers(ORACLE_DRIVER_NAME);
        return drivers.length != 0 ? drivers[0] : null;
    }

    public JdbcDriver getMetaMatrixOracleDriver() {
        final JdbcDriver[] drivers = getJdbcManager().findDrivers(MM_ORACLE_DRIVER_NAME);
        return drivers.length != 0 ? drivers[0] : null;
    }

    /**
     * Finds and returns the first JdbcSource that has the supplied name.
     * @param name
     * @return
     */
    public JdbcSource getJdbcSource( final String name ) {
        final JdbcSource[] sources = getJdbcManager().findSources(name);
        return sources.length != 0 ? sources[0] : null;
    }
}
