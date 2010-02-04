/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResultsProvider;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.SqlResultsModel;
import com.metamatrix.modeler.internal.ui.actions.workers.ExportTextToFileWorker;

/**
 * @since 5.5.3
 */
public class SaveSqlResultsToFileAction extends Action implements
                                                      DqpUiConstants {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private IResultsProvider resultsProvider;

    private String fileName;

    private boolean success; // indicates if the last run was successful

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @since 5.5.3
     */
    public SaveSqlResultsToFileAction(IResultsProvider resultsProvider) {
        super(UTIL.getString(I18nUtil.getPropertyPrefix(SaveSqlResultsToFileAction.class) + "saveToFileAction"), IAction.AS_PUSH_BUTTON); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(Images.SAVE_TO_FILE_ICON));
        setToolTipText(UTIL.getString(I18nUtil.getPropertyPrefix(SaveSqlResultsToFileAction.class) + "saveToFileAction.tip")); //$NON-NLS-1$
        setEnabled(true);

        this.resultsProvider = resultsProvider;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    private String createHeader(SqlResultsModel model) {
        String prefix = I18nUtil.getPropertyPrefix(SaveSqlResultsToFileAction.class);
        DateFormat formatter = new SimpleDateFormat(UTIL.getString(prefix + "header.datePattern")); //$NON-NLS-1$
        String date = formatter.format(new Date(System.currentTimeMillis()));

        StringBuffer buf = new StringBuffer(model.getSql().length() * 2);
        buf.append(UTIL.getString(prefix + "header.line_1")); //$NON-NLS-1$
        buf.append(UTIL.getString(prefix + "header.line_2") + date + StringUtil.Constants.LINE_FEED_CHAR); //$NON-NLS-1$
        buf.append(UTIL.getString(prefix + "header.line_3") + model.getTotalRowCount() + StringUtil.Constants.LINE_FEED_CHAR); //$NON-NLS-1$
        buf.append(UTIL.getString(prefix + "header.line_4")); //$NON-NLS-1$
        buf.append(model.getSql()).append(StringUtil.Constants.LINE_FEED_CHAR);
        buf.append(UTIL.getString(prefix + "header.line_5")); //$NON-NLS-1$
        buf.append(UTIL.getString(prefix + "header.line_6")); //$NON-NLS-1$

        return buf.toString();
    }

    /**
     * @return the name of the file where the last successful save occurred or <code>null</code>
     * @since 5.5.3
     */
    public String getFileName() {
        return this.fileName;
    }

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 5.5
     */
    @Override
    public void run() {
        this.success = false;
        SqlResultsModel model = (SqlResultsModel)this.resultsProvider.getResults();

        if (model == null) {
            setEnabled(false);
        } else {
            IPreferenceStore store = SQLExplorerPlugin.getDefault().getPreferenceStore();
            String columnDelim = store.getString(IConstants.CLIP_EXPORT_SEPARATOR);
            String lineDelim = System.getProperty("line.separator"); //$NON-NLS-1$
            Object[] rows = model.getRows();
            StringBuffer buf = new StringBuffer();
            boolean showColumnNames = store.getBoolean(IConstants.CLIP_EXPORT_COLUMNS);
            String[] colNames = model.getColumnNames();

            if (showColumnNames) {
                for (int cIndex = 0; cIndex < colNames.length; cIndex++) {
                    if (cIndex != 0) {
                        buf.append(columnDelim);
                    }

                    buf.append(colNames[cIndex]);
                }

                buf.append(lineDelim);
            }

            for (int rowIndex = 0; rowIndex < rows.length; ++rowIndex) {
                Object[] row = (Object[])rows[rowIndex];

                for (int colIndex = 0; colIndex < row.length; ++colIndex) {
                    if (colIndex != 0) {
                        buf.append(columnDelim);
                    }

                    buf.append(row[colIndex]);
                }

                buf.append(lineDelim);
            }

            String prefix = I18nUtil.getPropertyPrefix(SaveSqlResultsToFileAction.class);
            ExportTextToFileWorker expWorker = new ExportTextToFileWorker(UTIL.getString(prefix + "exportFileWorker.title"), //$NON-NLS-1$
                                                                          UTIL.getString(prefix
                                                                                         + "exportFileWorker.defaultFileName"), //$NON-NLS-1$
                                                                          UTIL.getString(prefix
                                                                                         + "exportFileWorker.defaultExtension"), //$NON-NLS-1$
                                                                          createHeader(model), buf.toString());

            this.fileName = expWorker.getFileName();
            this.success = !expWorker.export();
        }
    }

    /**
     * @return <code>true</code> if save was successful
     * @since 5.5.3
     */
    public boolean wasSaveSuccessful() {
        return this.success;
    }
}
