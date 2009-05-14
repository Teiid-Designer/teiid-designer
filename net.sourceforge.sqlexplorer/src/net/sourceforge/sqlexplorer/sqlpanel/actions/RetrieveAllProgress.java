package net.sourceforge.sqlexplorer.sqlpanel.actions;

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

import java.lang.reflect.InvocationTargetException;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.sqlpanel.SqlTableModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;

public class RetrieveAllProgress implements IRunnableWithProgress {

    public void run( final IProgressMonitor monitor ) throws InterruptedException {
        while (!sqlTableModel.isFinished()) {
            if (monitor.isCanceled()) {
                throw new InterruptedException();
            }
            try {
                if (sqlTableModel.tenMoreRows() == false) break;// The sql connection is closed
                tableViewer.getTable().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        // tableViewer.refresh(false);
                        monitor.setTaskName(Messages.getString("Retrieved_Rows___1") + sqlTableModel.getSize()); //$NON-NLS-1$
                    }
                });

            } catch (Throwable e) {
                new InvocationTargetException(e);
            }
        }
    }

    public RetrieveAllProgress( TableViewer tableViewer,
                                SqlTableModel sqlTableModel ) {
        this.sqlTableModel = sqlTableModel;
        this.tableViewer = tableViewer;
    }

    SqlTableModel sqlTableModel;
    TableViewer tableViewer;

}
