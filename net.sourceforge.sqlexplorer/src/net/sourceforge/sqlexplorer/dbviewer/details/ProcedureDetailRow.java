/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
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
