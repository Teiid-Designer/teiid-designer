package net.sourceforge.sqlexplorer;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
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

import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeModel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class RetrievingTableDataProgress implements IRunnableWithProgress {

    /**
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void run( IProgressMonitor monitor ) throws InterruptedException {
        monitor.setTaskName(Messages.getString("RetrievingTableDataProgress.Getting_Database_Structure_Data_1")); //$NON-NLS-1$
        monitor.beginTask(Messages.getString("RetrievingTableDataProgress.Getting_Database_Structure_Data_1"), IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        try {
            sessionTreeModel.createSessionTreeNode(conn, alias, monitor, pswd);
        } finally {
            monitor.done();
        }

    }

    SQLConnection conn;
    ISQLAlias alias;
    String pswd;
    SessionTreeModel sessionTreeModel;

    public RetrievingTableDataProgress( SQLConnection conn,
                                        ISQLAlias alias,
                                        SessionTreeModel sessionTreeModel,
                                        String pswd ) {

        this.conn = conn;
        this.alias = alias;
        this.sessionTreeModel = sessionTreeModel;
        this.pswd = pswd;
    }

    String error;

    public boolean isOk() {
        return ((error == null) ? true : false);
    }

    public String getError() {
        return error;
    }

}
