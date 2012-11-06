/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.Collection;
import org.teiid.designer.runtime.ITeiidDataSource;
import org.teiid.designer.runtime.ui.DqpUiConstants;

/**
 * @since 8.0
 */
public class DataSourcesFolder extends AbstractTeiidFolder<ITeiidDataSource> {

    private static final String DATA_SOURCES_FOLDER_NAME = DqpUiConstants.UTIL.getString(DataSourcesFolder.class.getSimpleName() + ".label"); //$NON-NLS-1$
    
    /**
     * Create new instance
     * 
     * @param parentNode
     * @param values
     */
    public DataSourcesFolder(TeiidServerContainerNode parentNode, Collection<ITeiidDataSource> values ) {
        super(parentNode, values);
    }

    @Override
    public String getName() {
        return DATA_SOURCES_FOLDER_NAME;
    }
}
