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

package net.sourceforge.sqlexplorer.sqlpanel.actions;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.sqlexplorer.sqlpanel.SqlExecProgress;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;

public class ExecSQLAction extends Action {

    SQLEditor txtComp;
    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getExecSQLIcon());
    private int maxRows;
    // private SessionTreeNode sessionTreeNode;
    // private boolean reRun=false;
    // SessionTreeNode node;
    SessionTreeNode preferredNode;

    public ExecSQLAction( SQLEditor txtComp,
                          int maxRows ) {

        this.txtComp = txtComp;
        this.maxRows = maxRows;
    }

    public ExecSQLAction( SQLEditor txtComp,
                          int maxRows,
                          SessionTreeNode node_ ) {

        this.txtComp = txtComp;
        this.maxRows = maxRows;
        this.preferredNode = node_;
    }

    @Override
    public String getText() {
        return Messages.getString("Execute_SQL_2"); //$NON-NLS-1$
    }

    @Override
    public String getToolTipText() {
        return Messages.getString("Execute_SQL_3"); //$NON-NLS-1$
    }

    @Override
    public void run() {
        SessionTreeNode runNode = null;
        if (preferredNode == null) runNode = txtComp.getSessionTreeNode();
        else runNode = preferredNode;
        if (runNode == null) return;
        // System.out.println("Executing on "+runNode);
        final SqlExecProgress sExecP = new SqlExecProgress(txtComp.getSQLToBeExecuted(), txtComp, maxRows, runNode);

        ProgressMonitorDialog pg = new ProgressMonitorDialog(txtComp.getSite().getShell());
        Throwable exception = null;
        try {
            pg.run(true, true, sExecP);
        } catch (Throwable e) {
            exception = e;
            SQLExplorerPlugin.error("Error executing the SQL statement ", e); //$NON-NLS-1$
        }
        if (exception == null) {
            exception = sExecP.getException();
        }
        if (exception != null) {
            if (isOutOfMemoryError(exception)) {
                txtComp.getSite().getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        MessageDialog.openError(txtComp.getSite().getShell(),
                                                Messages.getString("SqlExecProgress.OutOfMemoryTitle"),
                                                Messages.getString("SqlExecProgress.OutOfMemoryMessage"));
                    }
                });
            } else {
                txtComp.getSite().getShell().getDisplay().asyncExec(new Runnable() {
                    public void run() {
                        MessageDialog.openError(txtComp.getSite().getShell(),
                                                Messages.getString("Error..._2"), sExecP.getException().getMessage());//$NON-NLS-1$

                    }
                });
            }
        }
    }

    @Override
    public ImageDescriptor getHoverImageDescriptor() {
        return img;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return img;
    }

    private boolean isOutOfMemoryError( Throwable e ) {
        if (e == null) {
            return false;
        }
        if (e.toString().indexOf("OutOfMemoryError") != -1) {//$NON-NLS-1$
            return true;
        }
        return isOutOfMemoryError(e.getCause());
    }
}
