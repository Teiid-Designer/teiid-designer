/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.datasources.ui.panels;

/**
 * Interface for DataSourcePropertiesPanel listeners
 */
public interface DataSourcePropertiesPanelListener {
    
    /**
     * notify listeners that the property changed.
     */
    public void propertyChanged( );

}
