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

import java.sql.DatabaseMetaData;

/**
 * @since 4.3
 */
public class ProcedureDetailRow {

    private Object[] el;
    private int type;

    /**
     * @since 4.3
     */
    public ProcedureDetailRow( Object[] obj ) {

        el = new Object[8];
        el[ProcedureDetail.NAME_COLUMN] = obj[3]; // Parameter Name
        el[ProcedureDetail.TYPE_COLUMN] = obj[4]; // Type
        el[ProcedureDetail.DATATYPE_COLUMN] = obj[6]; // Data Type
        el[ProcedureDetail.PRECISION_COLUMN] = obj[7]; // Precision
        el[ProcedureDetail.LENGTH_COLUMN] = obj[8]; // Length
        el[ProcedureDetail.SCALE_COLUMN] = obj[9]; // Scale
        el[ProcedureDetail.NULLABLE_COLUMN] = obj[11]; // Accept Null Value
        el[ProcedureDetail.COMMENTS_COLUMN] = obj[12]; // Comments

        type = ((Integer)el[ProcedureDetail.TYPE_COLUMN]).intValue();

    }

    public Object getValue( int k ) {
        return el[k];
    }

    public String getName() {
        return (String)el[ProcedureDetail.NAME_COLUMN];
    }

    public String getDataType() {
        return (String)el[ProcedureDetail.DATATYPE_COLUMN];
    }

    public boolean isArgumentType() {
        return (type == DatabaseMetaData.procedureColumnIn || type == DatabaseMetaData.procedureColumnInOut);
    }

    public boolean isInType() {
        return (type == DatabaseMetaData.procedureColumnIn);
    }

    public boolean isResultType() {
        return (type == DatabaseMetaData.procedureColumnResult);
    }

}
