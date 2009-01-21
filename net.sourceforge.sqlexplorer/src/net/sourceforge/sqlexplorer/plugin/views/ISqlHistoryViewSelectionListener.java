/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package net.sourceforge.sqlexplorer.plugin.views;

import org.eclipse.jface.viewers.ISelectionChangedListener;

/**
 * A selection listener for the {@link net.sourceforge.sqlexplorer.plugin.views.SQLHistoryView}. Registered listeners will get
 * called when a double-click on a history record occurs. The workbench part identified by this listener will be given workbench
 * focus if that part is showing results for the double-clicked history record.
 */
public interface ISqlHistoryViewSelectionListener extends ISelectionChangedListener {

    /**
     * Obtains the identifier for the <code>IWorkbenchPart</code> that will be given focus when a double-click in the
     * {@link net.sourceforge.sqlexplorer.plugin.views.SQLHistoryView}'s table occurs.
     * 
     * @return the workbench part identifier or <code>null</code> if no part should be given focus
     */
    String getWorkbenchPartId();

    /**
     * Indicates if the {@link net.sourceforge.sqlexplorer.plugin.views.SqlHistoryRecord} with the specified identifier is being
     * shown.
     * 
     * @param theId the history record identifier
     * @return <code>true</code>if the record is being shown; <code>false</code> otherwise.
     */
    boolean isShowingSqlHistoryRecord( Object theId );

}
