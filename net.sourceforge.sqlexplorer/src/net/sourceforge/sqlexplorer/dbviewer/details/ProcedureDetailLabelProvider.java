/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package net.sourceforge.sqlexplorer.dbviewer.details;

import java.sql.DatabaseMetaData;
import net.sourceforge.sqlexplorer.Messages;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @since 4.3
 */
public class ProcedureDetailLabelProvider extends LabelProvider implements ITableLabelProvider {

    private static final String IN = Messages.getString("ProcedureDetailLabelProvider.inType"); //$NON-NLS-1$
    private static final String OUT = Messages.getString("ProcedureDetailLabelProvider.outType"); //$NON-NLS-1$
    private static final String INOUT = Messages.getString("ProcedureDetailLabelProvider.inoutType"); //$NON-NLS-1$
    private static final String RETURN = Messages.getString("ProcedureDetailLabelProvider.returnType"); //$NON-NLS-1$
    private static final String RESULT = Messages.getString("ProcedureDetailLabelProvider.resultType"); //$NON-NLS-1$
    private static final String TYPE_UNKNOWN = Messages.getString("ProcedureDetailLabelProvider.unknownType"); //$NON-NLS-1$
    private static final String NULLS = Messages.getString("ProcedureDetailLabelProvider.nullAllowed"); //$NON-NLS-1$
    private static final String NO_NULLS = Messages.getString("ProcedureDetailLabelProvider.noNullAllowed"); //$NON-NLS-1$
    private static final String NULLS_UNKNOWN = Messages.getString("ProcedureDetailLabelProvider.nullAllowedUnknown"); //$NON-NLS-1$

    ProcedureDetailTableModel model;

    public ProcedureDetailLabelProvider( ProcedureDetailTableModel model ) {
        this.model = model;
    }

    /**
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(Object, int)
     */
    public Image getColumnImage( Object arg0,
                                 int arg1 ) {
        return null;
    }

    public String getColumnText( Object element,
                                 int columnIndex ) {
        Object obj = model.getValue(element, columnIndex);
        if (obj != null) switch (columnIndex) {

            case ProcedureDetail.TYPE_COLUMN:

                int typeIndex = DatabaseMetaData.procedureColumnUnknown;
                if (obj instanceof String) {
                    typeIndex = new Integer((String)obj).intValue();
                } else if (obj instanceof Integer) {
                    typeIndex = ((Integer)obj).intValue();
                }

                switch (typeIndex) {
                    case DatabaseMetaData.procedureColumnIn:
                        return IN;
                    case DatabaseMetaData.procedureColumnInOut:
                        return INOUT;
                    case DatabaseMetaData.procedureColumnOut:
                        return OUT;
                    case DatabaseMetaData.procedureColumnResult:
                        return RESULT;
                    case DatabaseMetaData.procedureColumnReturn:
                        return RETURN;
                    default:
                        return TYPE_UNKNOWN;
                }

            case ProcedureDetail.NULLABLE_COLUMN:

                int nullIndex = DatabaseMetaData.procedureNullableUnknown;
                if (obj instanceof String) {
                    nullIndex = new Integer((String)obj).intValue();
                } else if (obj instanceof Integer) {
                    nullIndex = ((Integer)obj).intValue();
                }

                switch (nullIndex) {
                    case DatabaseMetaData.procedureNullable:
                        return NULLS;
                    case DatabaseMetaData.procedureNoNulls:
                        return NO_NULLS;
                    default:
                        return NULLS_UNKNOWN;
                }

            default:
                return obj.toString();
        }

        return ""; //$NON-NLS-1$
    }

}
