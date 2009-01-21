/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
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
