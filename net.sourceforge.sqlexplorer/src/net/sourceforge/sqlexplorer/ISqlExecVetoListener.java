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
package net.sourceforge.sqlexplorer;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * An <code>ISqlExecVetoListener</code> can be registered with the {@link net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin} to
 * potentially stop SQL execution from a SQL Editor.
 * 
 * @since 5.0.1
 */
public interface ISqlExecVetoListener {

    /**
     * Constant suitable to use when indicating SQL execution should continue.
     * 
     * @since 5.0.1
     */
    IStatus CONTINUE_EXECUTION = new Status(IStatus.OK, SQLExplorerPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$

    /**
     * Indicates if the SQL in SQL Editor should be executed. When stopping execution the status message should explain why.
     * 
     * @param theConnection the connection on which the SQL is being executed
     * @return a status with severity not equal to {@link IStatus#ERROR} to continue with execution; otherwise execution will be
     *         stopped.
     * @since 5.0.1
     */
    IStatus continueSqlExecution( Object theConnection );

}
