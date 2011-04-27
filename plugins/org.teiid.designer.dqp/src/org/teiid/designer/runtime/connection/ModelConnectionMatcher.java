package org.teiid.designer.runtime.connection;

import java.util.ArrayList;
import java.util.Collection;

import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.preview.PreviewManager;

import com.metamatrix.modeler.core.ModelerCore;

public class ModelConnectionMatcher {

    public Collection<TeiidDataSource> findTeiidDataSources( Collection<String> names,
                                                             ExecutionAdmin admin ) throws Exception {
        Collection<TeiidDataSource> dataSources = new ArrayList<TeiidDataSource>();

        for (String name : names) {
            if (name.equalsIgnoreCase("DefaultDS") || name.equalsIgnoreCase("JmsXA")) { //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }
            TeiidDataSource tds = new TeiidDataSource(name, name, "<unknown>", admin); //$NON-NLS-1$
            
            if (name.startsWith(PreviewManager.PREVIEW_PREFIX)) {
                if (name.length() > ModelerCore.workspaceUuid().toString().length() + 8) {
                    tds.setPreview(true);
                }
            }
            dataSources.add(tds);
        }

        return dataSources;
    }
}
