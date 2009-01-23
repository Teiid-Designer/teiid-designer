/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
