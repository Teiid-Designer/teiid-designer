package org.teiid.designer.runtime.connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.teiid.designer.WorkspaceUUIDService;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.vdb.Vdb;


/**
 * @since 8.0
 */
public class ModelConnectionMatcher {

    public Collection<ITeiidDataSource> findTeiidDataSources( Collection<String> names) throws Exception {
        Collection<ITeiidDataSource> dataSources = new ArrayList<ITeiidDataSource>();

        for (String name : names) {
            if (name.equalsIgnoreCase("DefaultDS") || name.equalsIgnoreCase("JmsXA")) { //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }
            TeiidDataSource tds = new TeiidDataSource(name, name, "<unknown>"); //$NON-NLS-1$
            
            if (name.startsWith(Vdb.PREVIEW_PREFIX)) {
                UUID workspaceUuid = WorkspaceUUIDService.getInstance().getUUID();
                if (name.length() > workspaceUuid.toString().length() + 8) {
                    tds.setPreview(true);
                }
            }
            dataSources.add(tds);
        }

        return dataSources;
    }
}
