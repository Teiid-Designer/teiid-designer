/*
 * Copyright © 2000-2005 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer.dbviewer.details;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;


/** 
 * @since 4.3
 */
public class ProcedureDetailContentProvider implements
                                           IStructuredContentProvider {

    TableViewer m_viewer;

    public Object[] getElements(Object input) {
        return ((ProcedureDetailTableModel)input).getElements();
    }

    public void dispose() {
    }

    
    public void inputChanged(Viewer viewer, Object arg1, Object arg2) {
        m_viewer=(TableViewer)viewer;
    }

}
