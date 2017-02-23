package org.teiid.designer.transformation.ui.editors.summary;

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
