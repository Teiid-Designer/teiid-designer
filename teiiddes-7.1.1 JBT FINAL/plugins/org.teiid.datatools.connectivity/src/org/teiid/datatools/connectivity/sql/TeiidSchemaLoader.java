/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.sql;

import org.eclipse.datatools.connectivity.sqm.core.rte.ICatalogObject;
import org.eclipse.datatools.connectivity.sqm.loader.JDBCSchemaLoader;
import org.eclipse.datatools.modelbase.sql.schema.Schema;

public class TeiidSchemaLoader extends JDBCSchemaLoader {

    public TeiidSchemaLoader() {
        super(null);
    }

    public TeiidSchemaLoader( ICatalogObject catalogObject ) {
        super(catalogObject);
    }

    @Override
    protected Schema createSchema() {
        return new TeiidCatalogSchema();
    }

}
