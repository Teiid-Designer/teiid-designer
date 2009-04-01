/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlProcedureAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlTableAspect;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants.Extensions;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants.Preferences;
import com.metamatrix.modeler.dqp.ui.workspace.WorkspaceExecutor;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.views.PreviewDataView;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.AccessPatternColumnsDialog;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.ParameterInputDialog;
import com.metamatrix.modeler.internal.dqp.ui.workspace.dialogs.PrunePreviewResultsDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.workspace.WorkspaceExecutionUtil;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.webservice.util.WebServiceUtil;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.0
 */
public class PreviewTableDataContextAction extends SortableSelectionAction {

    /**
     * @since 5.0
     */
    public PreviewTableDataContextAction() {
        super();
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.PREVIEW_DATA_ICON));
        setWiredForSelection(true);
        setToolTipText(DqpUiConstants.UTIL.getString("PreviewTableDataContextAction.tooltip")); //$NON-NLS-1$
    }

    /**
     * Valid selections include Relational Tables, Procedures or Relational Models. The roots instance variable will populated
     * with all Tables and Procedures contained within the current selection.
     * 
     * @return
     * @since 4.1
     */
    @Override
    protected boolean isValidSelection( ISelection selection ) {
        boolean isValid = true;
        if (SelectionUtilities.isEmptySelection(selection)) {
            isValid = false;
        }

        if (isValid && SelectionUtilities.isSingleSelection(selection)) {
            final EObject eObj = SelectionUtilities.getSelectedEObject(selection);

            if (eObj != null) {
                boolean executable = ModelObjectUtilities.isExecutable(eObj);
                boolean hasBinding = hasModelBinding(eObj);
                isValid = (executable && hasBinding);
            } else {
                isValid = false;
            }

        } else {
            isValid = false;
        }

        return isValid;
    }

    private boolean hasModelBinding( EObject obj ) {
        String[] names;
        try {
            names = ModelObjectUtilities.getDependentPhysicalModelNames(obj);
        } catch (ModelWorkspaceException e) {
            String msg = DqpUiConstants.UTIL.getString("PreviewTableDataContextAction.errorGettingDependentPhysicalSources", obj); //$NON-NLS-1$
            DqpUiConstants.UTIL.log(IStatus.ERROR, e, msg);
            return false;
        }

        WorkspaceExecutor executor = WorkspaceExecutor.getInstance();

        boolean bound = (names.length != 0);
        for (String name : names) {
            if (!executor.modelHasConnectorBinding(name)) {
                bound = false;
            }
        }
        return bound;
    }

    /**
     * @see org.eclipse.jface.action.IAction#run()
     * @since 5.0
     */
    @Override
    public void run() {
        internalRun();
    }

    /**
     *
     */
    @Override
    public boolean isApplicable( ISelection selection ) {
        return isValidSelection(selection);
    }

    
     private boolean isShowingMaxPreviews( EObject eObject ) {
        PreviewDataView view = getPreviewDataView();

        if (view != null) {
            int diff = getCurrentResultsLimit() - view.getResultCount();

            if (diff == 0) {
                // see if results are currently displayed than it is OK since old results will
                // get replaced with the new results
                return !view.isShowingResult(eObject);
            }

            if (diff < 0) {
                return true;
            }
        }

        return false;
    }

    private PreviewDataView getPreviewDataView() {
        return (PreviewDataView)UiUtil.getViewPart(DqpUiConstants.Extensions.PREVIEW_DATA_VIEW);
    }

    private int displayPruneResultsDialog( EObject object ) {
        // decrease the limit by one if a preview for this object is already being displayed
        int limit = getCurrentResultsLimit();

        if (getPreviewDataView().isShowingResult(object)) {
            --limit;
        }

        PrunePreviewResultsDialog dialog = new PrunePreviewResultsDialog(getPreviewDataView(), getCurrentResultsLimit(), object);
        dialog.create();
        return dialog.open();
    }

    /**
     * Open the launch configuration dialog, passing in the current workbench selection.
     */
    private void internalRun() {
        String sql = null;
        List<String> paramValues = null;
        final Shell shell = getShell();
        final EObject selected = SelectionUtilities.getSelectedEObject(getSelection());
        
         // if showing the max number of previews don't continue unless user closes the appropriate number of previews
        if (isShowingMaxPreviews(selected)) {
            if (displayPruneResultsDialog(selected) != Window.OK) {
                return;
            }
        }

        List accessPatternsColumns = null;
        if (SqlAspectHelper.isTable(selected)) {
            SqlTableAspect tableAspect = (SqlTableAspect)SqlAspectHelper.getSqlAspect(selected);
            Collection accessPatterns = tableAspect.getAccessPatterns(selected);

            if (accessPatterns != null && !accessPatterns.isEmpty()) {
                // first need to type the collection since dialog requires typed collection
                List<EObject> patterns = new ArrayList<EObject>(accessPatterns.size());

                for (Object pattern : accessPatterns) {
                    patterns.add((EObject)pattern);
                }

                AccessPatternColumnsDialog dialog = new AccessPatternColumnsDialog(shell, patterns);

                if (dialog.open() == Window.OK) {
                    accessPatternsColumns = dialog.getColumns();
                    paramValues = dialog.getColumnValues();
                } else {
                    return;
                }
            } else {
                paramValues = Collections.emptyList();
            }
        }

        if (selected instanceof Operation) {
            List<EObject> inputElements = WebServiceUtil.getInputElements((Operation)selected, false);

            if (!inputElements.isEmpty()) {
                ParameterInputDialog dialog = getInputDialog(inputElements);
                dialog.open();

                if (dialog.getReturnCode() == Window.OK) {
                    paramValues = dialog.getParameterValues();
                    sql = WebServiceUtil.getSql((Operation)selected, paramValues);
                    paramValues = Collections.emptyList(); // no need to pass these to the executor
                } else {
                    return;
                }
            } else {
                paramValues = Collections.emptyList();
                sql = WebServiceUtil.getSql((Operation)selected, paramValues);
            }
        } else if (SqlAspectHelper.isProcedure(selected)) {
            SqlProcedureAspect procAspect = (SqlProcedureAspect)SqlAspectHelper.getSqlAspect(selected);
            List<EObject> params = procAspect.getParameters(selected);
            if (params != null && !params.isEmpty()) {
                ParameterInputDialog dialog = getInputDialog(params);
                dialog.open();
                if (dialog.getReturnCode() == Window.OK) {
                    paramValues = dialog.getParameterValues();
                } else {
                    return;
                }
            } else {
                paramValues = Collections.emptyList();
            }
        }

        assert (paramValues != null);

        final int rowLimit = getCurrentRowLimit();
        final Object[] paramValuesAsArray = paramValues.toArray();

        String displaySQL = sql;
        if (sql == null) {
            sql = ModelObjectUtilities.getSQL(selected, paramValuesAsArray, accessPatternsColumns);
            displaySQL = getDisplaySQL(sql, paramValues);
        }

        if (sql != null) {
            class QueryExecutor implements IRunnableWithProgress {
                private final QueryMetadataInterface qmi;
                private final String sql;
                private final String displaySql;

                public QueryExecutor( EObject previewObject,
                                      String sql,
                                      String displaySql ) {
                    this.qmi = WorkspaceExecutionUtil.getMetadata(previewObject);
                    this.sql = sql;
                    this.displaySql = displaySql;
                }

                public void run( IProgressMonitor monitor ) throws InvocationTargetException {
                    try {
                        WorkspaceExecutor.getInstance().executeSQL(this.qmi,
                                                                   this.sql,
                                                                   paramValuesAsArray,
                                                                   this.displaySql,
                                                                   rowLimit,
                                                                   monitor);
                    } catch (SQLException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            }

            ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell) {
                @Override
                protected void cancelPressed() {
                    super.cancelPressed();

                    try {
                        WorkspaceExecutor.getInstance().cancel();
                    } catch (SQLException e) {
                        DqpUiConstants.UTIL.log(e);
                    }
                }
            };

            QueryExecutor op = new QueryExecutor(selected, sql, displaySQL);

            try {
                dialog.run(true, true, op);

                if (!dialog.getProgressMonitor().isCanceled()) {
                    showResults(WorkspaceExecutor.getInstance().getResults());
                }
            } catch (InvocationTargetException e) {
                if (!dialog.getProgressMonitor().isCanceled()) {
                    String msg = e.getTargetException().getMessage();
                    DqpUiConstants.UTIL.log(new Status(IStatus.ERROR, DqpUiConstants.PLUGIN_ID, IStatus.OK, msg, e.getTargetException()));
                    MessageDialog.openError(shell, DqpUiConstants.UTIL.getString("PreviewTableDataAction.error_in_execution"), msg); //$NON-NLS-1$
                }
            } catch (InterruptedException e) {
                // dialog was canceled
            } finally {
                dialog.getProgressMonitor().done();
            }
        } else {
            DqpUiConstants.UTIL.log(new Status(IStatus.WARNING, DqpUiConstants.PLUGIN_ID, IStatus.OK,
                                            "failed to produce valid SQL to execute", null)); //$NON-NLS-1$
        }
    }

    private ParameterInputDialog getInputDialog( List<EObject> params ) {
        ParameterInputDialog dialog = new ParameterInputDialog(getShell(), params);
        return dialog;
    }

    private String getDisplaySQL( String sql,
                                  List<String> paramValues ) {
        if (paramValues != null && !paramValues.isEmpty()) {
            for (String value : paramValues) {
                // skip over null values as those don't have a ? to replace
                if (value != null) {
                    sql = sql.replaceFirst("\\?", "'" + value + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
            }
        }
        return sql;
    }

    private int getCurrentRowLimit() {
        IPreferenceStore prefStore = DqpUiPlugin.getDefault().getPreferenceStore();
        int rowLimit = 10;
        // initialize
        rowLimit = prefStore.getInt(Preferences.ID_PREVIEW_ROW_LIMIT);

        if (rowLimit < 1) {
            rowLimit = prefStore.getDefaultInt(Preferences.ID_PREVIEW_ROW_LIMIT);
        }

        return rowLimit;
    }

    
     private int getCurrentResultsLimit() {
        IPreferenceStore prefStore = DqpUiPlugin.getDefault().getPreferenceStore();
        int resultsLimit = prefStore.getInt(Preferences.ID_PREVIEW_RESULTS_LIMIT);

        if (resultsLimit < 1) {
            resultsLimit = prefStore.getDefaultInt(Preferences.ID_PREVIEW_RESULTS_LIMIT);
        }

        return resultsLimit;
    }

    /**
     * Show the specified results in the results view.
     * 
     * @param theResults the results being displayed
     * @since 5.5.3
     */
    private void showResults( final IResults theResults ) {
        // let the UI display the results
        final EObject selected = SelectionUtilities.getSelectedEObject(getSelection());

        final IWorkbenchWindow iww = DqpUiPlugin.getDefault().getCurrentWorkbenchWindow();
        iww.getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    IWorkbenchPage page = iww.getActivePage();
                    PreviewDataView view = (PreviewDataView)page.showView(Extensions.PREVIEW_DATA_VIEW);
                    view.addResults(theResults, selected);
                } catch (Exception theException) {
                    DqpUiConstants.UTIL.log(IStatus.ERROR, theException.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * This method was created to allow the transformation.ui plugin, and TransformationObjectEditorPage to get it's own instance
     * of this action so it can allow preview of the specific edited virtual table or procedure. This allows the original action
     * to remain workspace selection driven. Override abstract method
     * 
     * @see com.metamatrix.modeler.ui.actions.SortableSelectionAction#getClone()
     * @since 5.0
     */
    @Override
    public SortableSelectionAction getClone() {
        return new PreviewTableDataContextAction();
    }

    private Shell getShell() {
        return Display.getCurrent().getActiveShell();
    }
}
