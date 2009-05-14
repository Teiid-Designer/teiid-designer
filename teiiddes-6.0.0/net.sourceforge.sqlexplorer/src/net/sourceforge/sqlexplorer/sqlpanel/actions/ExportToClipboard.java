package net.sourceforge.sqlexplorer.sqlpanel.actions;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.views.SqlResultsView;
import net.sourceforge.sqlexplorer.sqlpanel.SqlRowElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

public class ExportToClipboard extends Action {

    SqlResultsView compositeTableViewer;
    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getExportToClipBoardIcon());
    Clipboard cb;
    IPreferenceStore store;
    int ii;

    public ExportToClipboard( SqlResultsView compositeTableViewer,
                              IPreferenceStore store,
                              int ii ) {
        this.compositeTableViewer = compositeTableViewer;
        this.setEnabled(true);
        cb = new Clipboard(compositeTableViewer.getSite().getShell().getDisplay());
        this.store = store;
        this.ii = ii;

    }

    @Override
    public String getText() {
        return Messages.getString("ExportToClipboard.Copy_to_Clipboard_1"); //$NON-NLS-1$
    }

    @Override
    public void run() {
        try {
            BusyIndicator.showWhile(compositeTableViewer.getSite().getShell().getDisplay(), new Runnable() {
                public void run() {
                    try {
                        String exp_sep = store.getString(IConstants.CLIP_EXPORT_SEPARATOR);

                        Object[] sqlRowElements = compositeTableViewer.getModel()[ii].getElements();
                        int length = sqlRowElements.length;
                        boolean exp_columns = store.getBoolean(IConstants.CLIP_EXPORT_COLUMNS);
                        StringBuffer buf = new StringBuffer();
                        String separator = System.getProperty("line.separator"); //$NON-NLS-1$
                        if (exp_columns) {
                            String colNames[] = compositeTableViewer.getModel()[ii].getColumns();
                            for (int i = 0; i < colNames.length; i++) {
                                buf.append(colNames[i]);
                                buf.append(exp_sep);
                            }
                            buf.append(separator);
                        }

                        for (int i = 0; i < length; i++) {
                            SqlRowElement row = (SqlRowElement)sqlRowElements[i];
                            int iL = row.getSize();
                            for (int j = 0; j < iL; j++) {
                                buf.append(row.getValue(j));
                                buf.append(exp_sep);
                            }
                            buf.append(separator);
                        }
                        TextTransfer textTransfer = TextTransfer.getInstance();
                        cb.setContents(new Object[] {buf.toString()}, new Transfer[] {textTransfer});

                    } catch (java.lang.Exception e) {
                        SQLExplorerPlugin.error("Error exporting to clipboard ", e); //$NON-NLS-1$
                    }
                }
            });
        } catch (java.lang.Exception e) {
            SQLExplorerPlugin.error("Error exporting to clipboard ", e); //$NON-NLS-1$
        }
    }

    @Override
    public String getToolTipText() {
        return Messages.getString("ExportToClipboard.Copy_To_Clipboard_4"); //$NON-NLS-1$
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
