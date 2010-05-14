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
package net.sourceforge.sqlexplorer.dbviewer.details;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;

/**
 * @since 4.3
 */
public class ProcedureDetailTableModel {

    private ArrayList paramList = new ArrayList();
    private ArrayList argList = new ArrayList();

    public ProcedureDetailTableModel( ResultSetReader rs ) {

        if (rs == null) return;
        try {
            Object[] obj = null;
            while ((obj = rs.readRow()) != null) {
                ProcedureDetailRow row = new ProcedureDetailRow(obj);
                paramList.add(row);
                if (row.isArgumentType()) {
                    String argName = row.getName() + '_' + row.getDataType();
                    argList.add(argName);
                }
            }
        } catch (java.lang.Exception e) {
            SQLExplorerPlugin.error("Error adding procedure detail row ", e); //$NON-NLS-1$
        }
    }

    public Object[] getElements() {
        return paramList.toArray();
    }

    public Object getValue( Object element,
                            int property ) {
        ProcedureDetailRow e = (ProcedureDetailRow)element;
        return e.getValue(property);
    }

    public List getArgumentNameList() {
        return argList;
    }

}
