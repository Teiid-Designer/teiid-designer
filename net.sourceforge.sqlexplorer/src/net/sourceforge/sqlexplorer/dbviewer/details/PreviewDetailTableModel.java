package net.sourceforge.sqlexplorer.dbviewer.details;

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

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;

class PreviewDetailTableModel {
    ArrayList list = new ArrayList();

    public PreviewDetailTableModel( ResultSetReader reader,
                                    ResultSetMetaData metaData,
                                    IDetailLogDisplay detailLog,
                                    int maxRows ) {
        int count = 0;

        try {

            count = metaData.getColumnCount();

            int i = 0;
            Object[] obj = null;
            while ((obj = reader.readRow()) != null) {
                list.add(new PreviewDetailRow(obj, count));
                i++;
                if (i >= maxRows) break;
            }

        } catch (java.lang.Exception e) {
            SQLExplorerPlugin.error("Error getting preview data ", e); //$NON-NLS-1$
            detailLog.setMessage(e.getMessage());
        }
    }

    public Object[] getElements() {
        return list.toArray();
    }

    public Object getValue( Object element,
                            int property ) {
        PreviewDetailRow e = (PreviewDetailRow)element;
        return e.getValue(property);
    }

    public Object[] getChildren() {
        return list.toArray();
    }

}
