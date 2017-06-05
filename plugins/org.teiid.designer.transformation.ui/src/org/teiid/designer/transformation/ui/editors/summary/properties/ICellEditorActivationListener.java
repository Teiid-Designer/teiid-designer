/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

/* Copied from org.eclipse.ui.views.* packages
 * 
 * The Properties View source was restrictive to use in a dockable View's page object
 * 
 * PropertySheetViewer was tightly connected to our extended ModelObjectPropertySheetPage and it's
 * functionality met our needs to embed this viewer in an Editor page.
 * 
 * So copied over this class and minimum number of associated classes to utilize this viewer in our
 * editor.
 * 
 * see:  org.eclipse.ui.views.properties.ICellEditorActivationListener.java
 */

package org.teiid.designer.transformation.ui.editors.summary.properties;

import org.eclipse.jface.viewers.CellEditor;

public interface ICellEditorActivationListener {
    /**
     * Notifies that the cell editor has been activated
     *
     * @param cellEditor the cell editor which has been activated
     */
    public void cellEditorActivated(CellEditor cellEditor);

    /**
     * Notifies that the cell editor has been deactivated
     *
     * @param cellEditor the cell editor which has been deactivated
     */
    public void cellEditorDeactivated(CellEditor cellEditor);
}

