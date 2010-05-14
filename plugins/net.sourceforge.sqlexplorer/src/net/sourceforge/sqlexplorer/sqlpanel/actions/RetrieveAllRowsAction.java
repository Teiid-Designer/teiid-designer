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
import net.sourceforge.sqlexplorer.plugin.views.SqlResultsView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;

public class RetrieveAllRowsAction extends Action {

    int ii;
    SqlResultsView sqlResultsView;
    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getAllRowsIcon());

    @Override
    public String getText() {
        return Messages.getString("Retrieve_All_Rows_1"); //$NON-NLS-1$
    }

    @Override
    public void run() {
        RetrieveAllProgress sExecP = new RetrieveAllProgress(sqlResultsView.getTableViewer(ii), sqlResultsView.getModel()[ii]);
        ProgressMonitorDialog pg = new ProgressMonitorDialog(null);
        try {
            pg.run(true, true, sExecP);
        } catch (java.lang.InterruptedException e) {
            SQLExplorerPlugin.error("Error Retrieving all rows ", e); //$NON-NLS-1$
        } catch (java.lang.Exception e) {
            SQLExplorerPlugin.error("Error Retrieving all rows ", e); //$NON-NLS-1$
        }

        BusyIndicator.showWhile(sqlResultsView.getSite().getShell().getDisplay(), new Runnable() {
            public void run() {
                sqlResultsView.getTableViewer(ii).getControl().setRedraw(false);
                sqlResultsView.getTableViewer(ii).refresh(false);
                sqlResultsView.getTableViewer(ii).getControl().setRedraw(true);
            }
        });

        sqlResultsView.setMessagePanel2(ii, sqlResultsView.getModel()[ii].getPartial());
        boolean finished = sqlResultsView.getModel()[ii].isFinished();
        sqlResultsView.enableMoreRows(ii, !finished);
    }

    @Override
    public String getToolTipText() {
        return Messages.getString("Retrieve_All_Rows_2"); //$NON-NLS-1$
    }

    @Override
    public ImageDescriptor getHoverImageDescriptor() {
        return img;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return img;
    }

    public RetrieveAllRowsAction( SqlResultsView sqlResultsView,
                                  int ii ) {
        this.sqlResultsView = sqlResultsView;
        this.setEnabled(false);
        this.ii = ii;
    }
}
