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
