/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.teiid.designer.ddl.DdlNodeImporter;
import org.teiid.designer.registry.AbstractExtensionRegistry;

/**
 * Registry for ddl node importer implementations
 */
public class DdlNodeImporterRegistry extends AbstractExtensionRegistry<String, DdlNodeImporter> {

    private static final String EXT_POINT_ID = "org.teiid.designer.ddl.nodeImporter"; //$NON-NLS-1$

    private static final String IMPORTER_ID = "importer"; //$NON-NLS-1$

    private static final String DIALECT_ID = "dialect"; //$NON-NLS-1$
    
    private static DdlNodeImporterRegistry registry;

    /**
     * Get the singleton instance of this registry
     * 
     * @return singleton {@link DdlNodeImporterRegistry}
     * 
     * @throws Exception
     */
    public static DdlNodeImporterRegistry getInstance() throws Exception {
        if (registry == null) {
            registry = new DdlNodeImporterRegistry();
        }

        return registry;
    }

    private DdlNodeImporterRegistry() throws Exception {
        super(EXT_POINT_ID, IMPORTER_ID);
    }

    @Override
    protected void register(IConfigurationElement configurationElement, DdlNodeImporter ddlImporter) {
        String dialect = configurationElement.getAttribute(DIALECT_ID);
        register(dialect.toUpperCase(), ddlImporter);
    }
}
