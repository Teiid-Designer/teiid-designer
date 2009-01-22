/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.actions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.transformation.util.SqlConstants;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelIdentifier;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * @since 5.0
 */
public class ImportTransformationSqlFromTextAction extends SortableSelectionAction implements UiConstants {
    private static final String IMPORT_PROBLEM_KEY = "ImportTransformationSqlFromTextAction.importProb"; //$NON-NLS-1$
    private static final String IMPORT_SQL_DIALOG_TITLE_KEY = "ImportTransformationSqlFromTextAction.importSqlDialog.title"; //$NON-NLS-1$
    private static final String IMPORT_SQL_PROBLEM_DIALOG_TITLE_KEY = "ImportTransformationSqlFromTextAction.importSqlProblemDialog.title"; //$NON-NLS-1$
    private static final String UNUSED_SQL_DIALOG_TITLE_KEY = "ImportTransformationSqlFromTextAction.unusedSqlDialogTitle"; //$NON-NLS-1$
    private static final String UNUSED_SQL_DIALOG_MESSAGE_KEY = "ImportTransformationSqlFromTextAction.unusedSqlDialogMessage"; //$NON-NLS-1$
    private static final String MONITOR_MAIN_TASK_NAME_KEY = "ImportTransformationSqlFromTextAction.monitorMainTaskName"; //$NON-NLS-1$
    private static final String MONITOR_TASK_SETTING_SQL_KEY = "ImportTransformationSqlFromTextAction.monitorTaskSettingSql";//$NON-NLS-1$
    private static final String MONITOR_TASK_RECONCILING_KEY = "ImportTransformationSqlFromTextAction.monitorTaskReconciling";//$NON-NLS-1$
    private static final String MONITOR_TASK_VALIDATING_SQL_KEY = "ImportTransformationSqlFromTextAction.monitorTaskValidatingSql";//$NON-NLS-1$
    static final String MONITOR_TASK_SAVING_MODEL = UiConstants.Util.getString("ImportTransformationSqlFromTextAction.monitorTaskSavingModel");//$NON-NLS-1$

    private static final String DELIMETER = "\\|";//$NON-NLS-1$
    private boolean isTestingMode = false;
    private Collection anyLeftOverRows;

    /**
     * @since 5.0
     */
    public ImportTransformationSqlFromTextAction() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#isValidSelection(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isValidSelection( ISelection selection ) {
        // Enable for single/multiple Virtual Tables
        return virtualModelSelected(selection);
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        ISelection cachedSelection = getSelection();
        if (cachedSelection != null && !cachedSelection.isEmpty()) {
            Object selectedObj = SelectionUtilities.getSelectedObject(cachedSelection);
            if (selectedObj != null && selectedObj instanceof IFile) {
                ModelResource modelResource = null;
                try {
                    modelResource = ModelUtilities.getModelResource(((IFile)selectedObj), false);
                    if (modelResource != null) {
                        String fileName = askUserForInputFilename();
                        if (fileName != null) {
                            importSqlFromFile(fileName, modelResource);
                        }
                    }
                } catch (ModelWorkspaceException e) {
                    UiConstants.Util.log(e);
                }
            }

        }
        selectionChanged(null, new StructuredSelection());
    }

    /**
     * @see com.metamatrix.modeler.ui.actions.ISelectionAction#isApplicable(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return virtualModelSelected(selection);
    }

    private boolean virtualModelSelected( ISelection theSelection ) {
        boolean result = false;
        List allObjs = SelectionUtilities.getSelectedObjects(theSelection);
        if (!allObjs.isEmpty() && allObjs.size() == 1) {
            Iterator iter = allObjs.iterator();
            result = true;
            Object nextObj = null;
            while (iter.hasNext() && result) {
                nextObj = iter.next();

                if (nextObj instanceof IFile) {
                    result = ModelIdentifier.isRelationalViewModel((IFile)nextObj);
                } else {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * returns a collection of SqlRow objects retrieved from a Sql Text file
     * 
     * @param fileString
     * @return
     * @since 5.0
     */
    public Collection getSqlRowsFromFile( String fileString ) {
        // PERFORM ARG CHECK
        // Look for NULL or EMPTY strings
        ArgCheck.isNotNull(fileString);
        ArgCheck.isNotEmpty(fileString);

        Collection sqlRows = new ArrayList();
        FileReader fileReader = null;
        BufferedReader bufferReader = null;
        try {
            fileReader = new FileReader(fileString);
            bufferReader = new BufferedReader(fileReader);
            String str;
            while ((str = bufferReader.readLine()) != null) {
                SqlRow newRow = createSqlRow(str);
                if (newRow != null) {
                    sqlRows.add(newRow);
                }
            }
        } catch (Exception e) {
            String msg = UiConstants.Util.getString(IMPORT_PROBLEM_KEY);
            UiConstants.Util.log(IStatus.ERROR, e, msg);
            String dialogMessage = msg + "\n" + e.getMessage(); //$NON-NLS-1$
            if (!isTestingMode) {
                displayError(getShell(), UiConstants.Util.getString(IMPORT_SQL_PROBLEM_DIALOG_TITLE_KEY), dialogMessage);
            }
        } finally {
            // Clean up readers & buffers
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (java.io.IOException e) {
                UiConstants.Util.log(IStatus.ERROR, e, UiConstants.Util.getString(IMPORT_PROBLEM_KEY));
            }
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
            } catch (java.io.IOException e) {
                UiConstants.Util.log(IStatus.ERROR, e, UiConstants.Util.getString(IMPORT_PROBLEM_KEY));
            }
        }

        return sqlRows;
    }

    /**
     * Import SQL text from file and set any SQL applicable to the virtual tables within the supplied ModelResource
     * 
     * @param fileString
     * @param modelResource
     * @since 5.0
     */
    public Collection importSqlFromFile( String fileString,
                                         ModelResource modelResource ) {
        Collection unusedSqlRows = Collections.EMPTY_LIST;
        // PERFORM ARG CHECK
        // Look for NULL or EMPTY strings
        ArgCheck.isNotNull(fileString);
        ArgCheck.isNotNull(modelResource);
        ArgCheck.isNotEmpty(fileString);

        Collection sqlRows = getSqlRowsFromFile(fileString);

        if (!sqlRows.isEmpty()) {
            unusedSqlRows = processRows(modelResource, sqlRows);
        }

        return unusedSqlRows;
    }

    private String askUserForInputFilename() {
        FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String[] {"*.txt", "*.*"}); //$NON-NLS-1$ //$NON-NLS-2$ 
        dlg.setText(UiConstants.Util.getString(IMPORT_SQL_DIALOG_TITLE_KEY));

        return dlg.open();
    }

    private Collection processRows( final ModelResource modelResource,
                                    final Collection sqlRows ) {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                monitor.beginTask(UiConstants.Util.getString(MONITOR_MAIN_TASK_NAME_KEY, modelResource.getItemName()),
                                  10 * sqlRows.size() + 10);
                try {
                    setTransformationSql(modelResource, sqlRows, monitor);
                } catch (ModelWorkspaceException theException) {
                    UiConstants.Util.log(theException);
                }
                monitor.subTask(MONITOR_TASK_SAVING_MODEL);
                monitor.worked(5);
                try {
                    ModelUtilities.saveModelResource(modelResource, monitor, false, this);
                } catch (Exception e) {
                    throw new InvocationTargetException(e);
                }
                monitor.worked(5);
            }
        };

        try {
            final ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(false, true, op);
            if (dlg.getProgressMonitor().isCanceled()) {
                // DO NOTHING
            }
        } catch (final InterruptedException ignored) {

        } catch (final Exception err) {
            Throwable t = err;

            if (err instanceof InvocationTargetException) {
                t = err.getCause();
            }

            WidgetUtil.showError(t);
        }

        Collection returnedRows = Collections.EMPTY_LIST;
        if (anyLeftOverRows != null) {
            returnedRows = new ArrayList(anyLeftOverRows);
            anyLeftOverRows = null;
        }
        return returnedRows;

    }

    private Shell getShell() {
        return UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    /**
     * Opens an error dialog to display the given message.
     * 
     * @param message the error message to show
     */
    private void displayError( final Shell shell,
                               final String dialogTitle,
                               final String message ) {
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                MessageDialog.openError(shell, dialogTitle, message);
            }
        });
    }

    private void notifyUserOfLeftovers( final List leftOverTables,
                                        final List leftOverRows ) {
        if (!isTestingMode && !leftOverTables.isEmpty()) {
            final Shell shell = getShell();
            shell.getDisplay().syncExec(new Runnable() {
                public void run() {
                    boolean result = ListMessageDialog.openQuestion(shell,
                                                                    UiConstants.Util.getString(UNUSED_SQL_DIALOG_TITLE_KEY),
                                                                    null,
                                                                    UiConstants.Util.getString(UNUSED_SQL_DIALOG_MESSAGE_KEY),
                                                                    leftOverTables,
                                                                    null);
                    if (result) {
                        // we need to create virtual tables

                    }
                }
            });
        }
    }

    private SqlRow createSqlRow( String rowString ) {
        String[] parsedString = rowString.split(DELIMETER);
        return new SqlRow(getSqlTypeFromString(parsedString[1]), parsedString[0], parsedString[2]);
    }

    boolean setTransformationSql( ModelResource modelResource,
                                  Collection sqlRows,
                                  IProgressMonitor monitor ) throws ModelWorkspaceException {
        boolean sqlChanged = false;
        Collection leftOverTables = new HashSet();
        Collection leftOverRows = new HashSet();
        int iRow = 0;
        String tableName = null;
        for (Iterator iter = sqlRows.iterator(); iter.hasNext();) {
            SqlRow nextRow = (SqlRow)iter.next();
            EObject existingTable = null;
            IPath tablePath = new Path(nextRow.getPath());
            tableName = tablePath.lastSegment();
            existingTable = ModelerCore.getModelEditor().findObjectByPath(modelResource.getEmfResource(), tablePath);
            if (existingTable != null) {
                SqlTransformationMappingRoot root = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(existingTable);

                if (root != null) {
                    monitor.subTask(UiConstants.Util.getString(MONITOR_TASK_SETTING_SQL_KEY, nextRow.getPath()));
                    QueryValidator validator = new TransformationValidator(root);
                    boolean changed = false;

                    switch (nextRow.getType()) {
                        case QueryValidator.SELECT_TRNS: {
                            changed = TransformationHelper.setSqlString(root,
                                                                        nextRow.getSql(),
                                                                        QueryValidator.SELECT_TRNS,
                                                                        false,
                                                                        this);
                            monitor.subTask(UiConstants.Util.getString(MONITOR_TASK_RECONCILING_KEY, tableName));
                            monitor.worked(3);
                            TransformationMappingHelper.reconcileMappingsOnSqlChange(root, this);
                            monitor.subTask(UiConstants.Util.getString(MONITOR_TASK_VALIDATING_SQL_KEY, tableName));
                            monitor.worked(3);
                            validator.validateSql(nextRow.getSql(), QueryValidator.SELECT_TRNS, false, true);
                            monitor.worked(4);
                        }
                            break;
                        case QueryValidator.INSERT_TRNS: {
                            changed = TransformationHelper.setSqlString(root,
                                                                        nextRow.getSql(),
                                                                        QueryValidator.INSERT_TRNS,
                                                                        false,
                                                                        this);
                            monitor.subTask(UiConstants.Util.getString(MONITOR_TASK_VALIDATING_SQL_KEY, tableName));
                            monitor.worked(5);
                            validator.validateSql(nextRow.getSql(), QueryValidator.INSERT_TRNS, false, true);
                            monitor.worked(5);
                        }
                            break;
                        case QueryValidator.UPDATE_TRNS: {
                            changed = TransformationHelper.setSqlString(root,
                                                                        nextRow.getSql(),
                                                                        QueryValidator.UPDATE_TRNS,
                                                                        false,
                                                                        this);
                            monitor.subTask(UiConstants.Util.getString(MONITOR_TASK_VALIDATING_SQL_KEY, tableName));
                            monitor.worked(5);
                            validator.validateSql(nextRow.getSql(), QueryValidator.UPDATE_TRNS, false, true);
                            monitor.worked(5);
                        }
                            break;
                        case QueryValidator.DELETE_TRNS: {
                            changed = TransformationHelper.setSqlString(root,
                                                                        nextRow.getSql(),
                                                                        QueryValidator.DELETE_TRNS,
                                                                        false,
                                                                        this);
                            monitor.worked(5);
                            monitor.subTask(UiConstants.Util.getString(MONITOR_TASK_VALIDATING_SQL_KEY, tableName));
                            validator.validateSql(nextRow.getSql(), QueryValidator.DELETE_TRNS, false, true);
                            monitor.worked(5);
                        }
                            break;

                        default: {
                        }
                            break;
                    }
                    if (changed) {
                        sqlChanged = true;
                    }
                }
            } else {
                if (!leftOverTables.contains(nextRow.getPath())) {
                    leftOverTables.add(nextRow.getPath());
                    leftOverRows.add(nextRow);
                }

            }
            iRow++;
        }
        anyLeftOverRows = leftOverRows;
        notifyUserOfLeftovers(new ArrayList(leftOverTables), new ArrayList(leftOverRows));

        return sqlChanged;
    }

    private int getSqlTypeFromString( String typeString ) {
        if (typeString.equalsIgnoreCase(SqlConstants.SQL_TYPE_SELECT_STRING)) {
            return QueryValidator.SELECT_TRNS;
        }
        if (typeString.equalsIgnoreCase(SqlConstants.SQL_TYPE_INSERT_STRING)) {
            return QueryValidator.INSERT_TRNS;
        }
        if (typeString.equalsIgnoreCase(SqlConstants.SQL_TYPE_UPDATE_STRING)) {
            return QueryValidator.UPDATE_TRNS;
        }
        if (typeString.equalsIgnoreCase(SqlConstants.SQL_TYPE_DELETE_STRING)) {
            return QueryValidator.DELETE_TRNS;
        }

        return QueryValidator.UNKNOWN_TRNS;
    }

    /**
     * @since 5.0
     */
    class SqlRow {
        private int type;
        private String path;
        private String sql;

        SqlRow( int sqlType,
                String relativePathAndName,
                String userSqlString ) {
            super();
            this.type = sqlType;
            this.path = relativePathAndName;
            this.sql = userSqlString;
        }

        public String getPath() {
            return this.path;
        }

        public String getSql() {
            return this.sql;
        }

        public int getType() {
            return this.type;
        }
    }

    public boolean isTestingMode() {
        return this.isTestingMode;
    }

    public void setIsTestingMode( boolean theIsTestingMode ) {
        this.isTestingMode = theIsTestingMode;
    }

}
