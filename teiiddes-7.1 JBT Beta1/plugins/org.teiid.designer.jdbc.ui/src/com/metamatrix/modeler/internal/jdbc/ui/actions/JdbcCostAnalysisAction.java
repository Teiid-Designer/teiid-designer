/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui.actions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.IProgressConstants;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.jdbc.JdbcUtil;
import com.metamatrix.modeler.internal.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import com.metamatrix.modeler.internal.jdbc.ui.ModelerJdbcUiPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.relational.CostAnalyzer;
import com.metamatrix.modeler.jdbc.relational.CostAnalyzerFactory;
import com.metamatrix.modeler.jdbc.relational.impl.ColumnStatistics;
import com.metamatrix.modeler.jdbc.relational.impl.TableStatistics;
import com.metamatrix.modeler.jdbc.ui.ModelerJdbcUiConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class JdbcCostAnalysisAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    private IFile selectedModel;

    public JdbcCostAnalysisAction() {
        super();
        setImageDescriptor(ModelerJdbcUiPlugin.getDefault().getImageDescriptor(ModelerJdbcUiConstants.Images.COST_ANALYSIS));
    }

    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = false;
        try {
            if (!SelectionUtilities.isMultiSelection(selection)) {
                Object obj = SelectionUtilities.getSelectedObject(selection);
                if (obj instanceof IFile && ModelUtilities.isModelFile((IFile)obj)) {
                    ModelResource modelResource = ModelUtil.getModelResource((IFile)obj, false);
                    if (ModelUtilities.hasJdbcSource(modelResource)) {
                        this.selectedModel = (IFile)obj;
                        enable = true;
                    }
                }
            }
        } catch (ModelWorkspaceException err) {
            UiConstants.Util.log(err);
        } finally {
            setEnabled(enable);
        }

    }

    /**
     * We will compute column-level statistics (min-value, max-value, # of null values, # of distinct values) for all columns in
     * tables in the model IFF the model is physical relational with a Jdbc source. We must first prompt the user for the
     * password, as it is not stored with the Jdbc import settings.
     * 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.3
     */
    @Override
    public void run() {
        if (isEnabled()) {
            final Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
            try {
                ModelResource modelResource = ModelUtil.getModelResource(this.selectedModel, false);
                if (modelResource != null) {
                    final Resource resource = modelResource.getEmfResource();
                    if (resource != null) {
                        final JdbcSource source = JdbcUtil.findJdbcSource(resource);
                        if (source != null) {
                            final List emfTables = RelationalUtil.findTables(resource);
                            final Map tblStats = createTableInfos(emfTables);
                            if (tblStats != null && tblStats.size() > 0) {
                                CostAnalysisDialog dialog = new CostAnalysisDialog(
                                                                                   shell,
                                                                                   InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.taskDescription"), //$NON-NLS-1$
                                                                                   InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.passwordPrompt", new Object[] {source.getUrl(), source.getUsername()}), null, null); //$NON-NLS-1$
                                dialog.open();

                                final String successMessage = InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.statisticsUpdatedFinished", emfTables.size()); //$NON-NLS-1$

                                final String password = dialog.getValue();
                                if (password != null) {
                                    final Job job = new Job(
                                                            InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.jobDescription")) { //$NON-NLS-1$
                                        @Override
                                        protected IStatus run( IProgressMonitor monitor ) {
                                            boolean wasSuccessful = false;
                                            try {
                                                monitor.beginTask(InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.taskDescription"), calculateNumberOfWorkIncrements(tblStats.values())); //$NON-NLS-1$

                                                CostAnalyzer costAnalyzer = CostAnalyzerFactory.getCostAnalyzerFactory().getCostAnalyzer(source,
                                                                                                                                         password);
                                                // log output to standard out
                                                // costAnalyzer.setOutputStream(System.out);
                                                costAnalyzer.collectStatistics(tblStats, monitor);

                                                if (!monitor.isCanceled()) {
                                                    populateEmfColumnStatistics(emfTables, tblStats);
                                                }

                                                monitor.done();

                                                if (monitor.isCanceled()) {
                                                    return Status.CANCEL_STATUS;
                                                }
                                                wasSuccessful = true;
                                                return new Status(
                                                                  IStatus.OK,
                                                                  ModelerJdbcUiConstants.PLUGIN_ID,
                                                                  IStatus.OK,
                                                                  InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.statusFinished", emfTables.size()), null); //$NON-NLS-1$
                                            } catch (Exception e) {
                                                InternalModelerJdbcUiPluginConstants.Util.log(e);
                                                return new Status(
                                                                  IStatus.ERROR,
                                                                  ModelerJdbcUiConstants.PLUGIN_ID,
                                                                  IStatus.ERROR,
                                                                  InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.errorMessage"), e); //$NON-NLS-1$
                                            } finally {
                                                if (wasSuccessful) {
                                                    Display.getDefault().asyncExec(new Runnable() {
                                                        public void run() {
                                                            MessageDialog.openInformation(shell,
                                                                                          InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.updateCompleteTitle"),//$NON-NLS-1$ 
                                                                                          successMessage);
                                                        }
                                                    });
                                                }
                                            }
                                        }
                                    };

                                    job.setSystem(false);
                                    job.setUser(true);
                                    job.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
                                    // start as soon as possible
                                    job.schedule();
                                }
                            } else {
                                MessageDialog.openInformation(shell,
                                                              InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.taskDescription"), InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.noValidTablesMessage")); //$NON-NLS-1$ //$NON-NLS-2$
                            }
                        }
                    }
                }
            } catch (Exception e) {
                InternalModelerJdbcUiPluginConstants.Util.log(e);
                final String title = InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.errorTitle"); //$NON-NLS-1$
                final String message = InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.errorMessage"); //$NON-NLS-1$
                MessageDialog.openError(shell, title, message);
            }
        }
    }

    // calculate the number of work units for the progress monitoring
    // of this cost analysis task
    int calculateNumberOfWorkIncrements( Collection tblStats ) {
        // first, add twice the number of columns for two table operations
        // (table cardinality and column attribute population)
        int numWorkInc = tblStats.size() * 2;
        for (Iterator it = tblStats.iterator(); it.hasNext();) {
            TableStatistics tblStat = (TableStatistics)it.next();
            // add the number of columns from each table,
            // as each requires 1-2 database operations
            numWorkInc += tblStat.getColumnStats().size();
        }
        return numWorkInc;
    }

    /**
     * @param emfTables the list of emf tables in this jdbc source physical relational model
     * @param tableInfos the map of value objects containing the newly-computed column statistics
     * @since 4.3
     */
    void populateEmfColumnStatistics( List emfTables,
                                      Map tableInfos ) {
        for (Iterator itTable = emfTables.iterator(); itTable.hasNext();) {
            Table emfTable = (Table)itTable.next();
            if (emfTable.getNameInSource() != null) {
                TableStatistics tableInfo = (TableStatistics)tableInfos.get(unQualifyName(emfTable.getNameInSource()));
                if (tableInfo != null) {
                    emfTable.setCardinality(tableInfo.getCardinality());
                    Map columnInfos = tableInfo.getColumnStats();
                    for (Iterator itColumn = emfTable.getColumns().iterator(); itColumn.hasNext();) {
                        Column emfColumn = (Column)itColumn.next();
                        if (emfColumn.getNameInSource() != null) {
                            ColumnStatistics columnInfo = (ColumnStatistics)columnInfos.get(unQualifyName(emfColumn.getNameInSource()));
                            if (columnInfo != null) {
                                emfColumn.setMinimumValue(columnInfo.getMin());
                                emfColumn.setMaximumValue(columnInfo.getMax());
                                emfColumn.setNullValueCount(columnInfo.getNumNullValues());
                                emfColumn.setDistinctValueCount(columnInfo.getNumDistinctValues());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Takes a list of emf table objects and breaks them down into the CostAnalyzer's TableInfo and ColumnInfo objects. NOTE: The
     * keys used to populate the table and column info maps are the "name in source" field values, not the emf object names of the
     * perspective objects.
     * 
     * @param emfTables
     * @since 4.3
     */
    private Map createTableInfos( List emfTables ) {
        if (emfTables != null) {
            Map tableInfos = new HashMap();
            for (Iterator tblIt = emfTables.iterator(); tblIt.hasNext();) {
                Table emfTable = (Table)tblIt.next();
                if (emfTable.getNameInSource() != null) {
                    Catalog catalog = emfTable.getCatalog();
                    String catalogName = catalog == null || catalog.getNameInSource() == null ? null : unQualifyName(catalog.getNameInSource());
                    Schema schema = emfTable.getSchema();
                    String schemaName = schema == null || schema.getNameInSource() == null ? null : unQualifyName(schema.getNameInSource());
                    String tblName = emfTable.getNameInSource();
                    TableStatistics tableInfo = new TableStatistics(catalogName, schemaName, tblName);
                    Map columnInfos = tableInfo.getColumnStats();
                    for (Iterator colIt = emfTable.getColumns().iterator(); colIt.hasNext();) {
                        Column emfColumn = (Column)colIt.next();
                        if (emfColumn.getNameInSource() != null) {
                            String colName = unQualifyName(emfColumn.getNameInSource());
                            ColumnStatistics columnInfo = new ColumnStatistics(colName);
                            columnInfos.put(colName, columnInfo);
                        }
                    }
                    tableInfos.put(unQualifyName(tblName), tableInfo);
                }
            }
            return tableInfos;
        }
        return null;
    }

    private String unQualifyName( String qualifiedName ) {
        return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1, qualifiedName.length());
    }

    class CostAnalysisDialog extends InputDialog {

        private static final char ECHO_CHAR = '*';

        public CostAnalysisDialog( Shell parentShell,
                                   String dialogTitle,
                                   String dialogMessage,
                                   String initialValue,
                                   IInputValidator validator ) {
            super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
        }

        @Override
        protected Control createDialogArea( Composite parent ) {
            Control control = super.createDialogArea(parent);
            getText().setEchoChar(ECHO_CHAR);
            return control;
        }
    }

    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        try {
            if (!SelectionUtilities.isMultiSelection(selection)) {
                Object obj = SelectionUtilities.getSelectedObject(selection);
                if (obj instanceof IFile && ModelUtilities.isModelFile((IFile)obj)) {
                    ModelResource modelResource = ModelUtil.getModelResource((IFile)obj, false);
                    if (ModelUtilities.hasJdbcSource(modelResource)) {
                        this.selectedModel = (IFile)obj;
                        result = true;
                    }
                }
            }
        } catch (ModelWorkspaceException err) {
            UiConstants.Util.log(err);
        }

        return result;
    }
}
