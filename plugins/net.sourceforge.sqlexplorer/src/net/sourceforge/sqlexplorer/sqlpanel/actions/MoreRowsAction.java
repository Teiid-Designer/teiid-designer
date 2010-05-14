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

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.SqlResultsView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;

public class MoreRowsAction extends Action {

    SqlResultsView sqlResultsView;
    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getMoreRowsIcon());
    int ii;

    public MoreRowsAction( SqlResultsView sqlResultsView,
                           int ii ) {
        this.sqlResultsView = sqlResultsView;
        this.setEnabled(false);
        this.ii = ii;
    }

    @Override
    public String getText() {
        return Messages.getString("Retrieve_more_rows_1"); //$NON-NLS-1$
    }

    @Override
    public void run() {
        try {
            BusyIndicator.showWhile(sqlResultsView.getSite().getShell().getDisplay(), new Runnable() {
                public void run() {
                    try {
                        sqlResultsView.getModel()[ii].moreRows();
                        boolean finished = sqlResultsView.getModel()[ii].isFinished();
                        sqlResultsView.setMessagePanel2(ii, sqlResultsView.getModel()[ii].getPartial());
                        sqlResultsView.getTableViewer(ii).refresh(false);
                        sqlResultsView.enableMoreRows(ii, !finished);
                    } catch (java.lang.Exception e) {
                        SQLExplorerPlugin.error("Error calling MoreRows Action ", e); //$NON-NLS-1$
                    }
                }
            });
        } catch (java.lang.Exception e) {
            SQLExplorerPlugin.error("Error calling MoreRows Action ", e); //$NON-NLS-1$
        }
    }

    @Override
    public String getToolTipText() {
        return Messages.getString("Retrieve_more_rows_2"); //$NON-NLS-1$
    }

    @Override
    public ImageDescriptor getHoverImageDescriptor() {
        return img;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return img;
    }
}
