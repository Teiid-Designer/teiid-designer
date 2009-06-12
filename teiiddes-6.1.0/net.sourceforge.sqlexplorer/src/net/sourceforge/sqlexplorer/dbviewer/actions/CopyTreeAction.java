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
package net.sourceforge.sqlexplorer.dbviewer.actions;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.SqlexplorerImages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

public class CopyTreeAction extends Action {
    Clipboard cb;
    private ImageDescriptor img = ImageDescriptor.createFromURL(SqlexplorerImages.getExportToClipBoardIcon());
    Tree tb;

    /**
	 * 
	 */
    public CopyTreeAction( Tree tb ) {
        this.tb = tb;
        cb = new Clipboard(tb.getDisplay());

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#getImageDescriptor()
     */
    @Override
    public ImageDescriptor getImageDescriptor() {
        return img;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#getText()
     */
    @Override
    public String getText() {

        return Messages.getString("CopyTableAction.Copy_to_Clipboard_1"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        try {
            final IPreferenceStore store = SQLExplorerPlugin.getDefault().getPreferenceStore();
            BusyIndicator.showWhile(tb.getDisplay(), new Runnable() {
                public void run() {
                    try {
                        String exp_sep = store.getString(IConstants.CLIP_EXPORT_SEPARATOR);
                        // int length=tb.getColumnCount();
                        // Object[] sqlRowElements=(Object[]) compositeTableViewer.getModel().getElements();
                        // int length=sqlRowElements.length;
                        boolean exp_columns = store.getBoolean(IConstants.CLIP_EXPORT_COLUMNS);
                        StringBuffer buf = new StringBuffer();
                        String separator = System.getProperty("line.separator"); //$NON-NLS-1$
                        TreeColumn colNames[] = tb.getColumns();
                        if (exp_columns) {

                            for (int i = 0; i < colNames.length; i++) {
                                buf.append(colNames[i].getText());
                                buf.append(exp_sep);
                            }
                            buf.append(separator);
                        }
                        int length = tb.getItemCount();
                        for (int i = 0; i < length; i++) {
                            TreeItem row = tb.getItem(i);

                            for (int j = 0; j < colNames.length; j++) {
                                buf.append(row.getText(j));
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

}
