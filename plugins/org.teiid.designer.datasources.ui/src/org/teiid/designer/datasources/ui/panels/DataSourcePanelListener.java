/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datasources.ui.panels;

/**
 * Listener interface for DataSourcePanel
 * @since 8.1
 */
public interface DataSourcePanelListener {
    
    /**
     * notify listeners that the selection changed.
     * @param selectedSourceName the selected source
     */
    public void selectionChanged(String selectedSourceName);

}
