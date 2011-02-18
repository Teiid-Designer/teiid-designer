/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.results.view;

import org.eclipse.datatools.sqltools.result.ui.ExternalResultSetViewerProvider;
import org.eclipse.jface.viewers.TableViewer;

/**
 * 
 */
public class TeiidResultSetViewerProvider extends ExternalResultSetViewerProvider {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.datatools.sqltools.result.ui.ExternalResultSetViewerProvider#getViewer()
     */
    @Override
    public TableViewer getViewer() {
        return super.getViewer();
    }

}
