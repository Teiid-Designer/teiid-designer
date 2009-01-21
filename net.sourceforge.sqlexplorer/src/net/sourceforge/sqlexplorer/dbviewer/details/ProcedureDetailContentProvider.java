/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package net.sourceforge.sqlexplorer.dbviewer.details;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

/**
 * @since 4.3
 */
public class ProcedureDetailContentProvider implements IStructuredContentProvider {

    TableViewer m_viewer;

    public Object[] getElements( Object input ) {
        return ((ProcedureDetailTableModel)input).getElements();
    }

    public void dispose() {
    }

    public void inputChanged( Viewer viewer,
                              Object arg1,
                              Object arg2 ) {
        m_viewer = (TableViewer)viewer;
    }

}
