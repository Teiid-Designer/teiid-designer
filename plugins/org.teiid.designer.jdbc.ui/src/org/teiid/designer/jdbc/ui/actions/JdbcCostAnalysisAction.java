/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.ui.actions;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.progress.IProgressConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.JdbcUtil;
import org.teiid.designer.jdbc.relational.CostAnalyzer;
import org.teiid.designer.jdbc.relational.CostAnalyzerFactory;
import org.teiid.designer.jdbc.relational.impl.ColumnStatistics;
import org.teiid.designer.jdbc.relational.impl.TableStatistics;
import org.teiid.designer.jdbc.ui.InternalModelerJdbcUiPluginConstants;
import org.teiid.designer.jdbc.ui.ModelerJdbcUiConstants;
import org.teiid.designer.jdbc.ui.ModelerJdbcUiPlugin;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.util.RelationalUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.ISelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * @since 8.0
 */
public class JdbcCostAnalysisAction extends Action implements ISelectionListener, Comparable, ISelectionAction {
    private IFile selectedModel;

    public JdbcCostAnalysisAction() {
        super();
        setImageDescriptor(ModelerJdbcUiPlugin.getDefault().getImageDescriptor(ModelerJdbcUiConstants.Images.COST_ANALYSIS));
    }

    @Override
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
                    // JdbcSource found - check if can do costing update
                    String allowsCostUpdate = ModelUtil.getModelAnnotationPropertyValue(modelResource, IConnectionInfoHelper.JDBCCONNECTION_NAMESPACE+IConnectionInfoHelper.JDBCCONNECTION_ALLOW_COSTUPDATE_KEY);
                    if(allowsCostUpdate!=null && !allowsCostUpdate.isEmpty() && !Boolean.getBoolean(allowsCostUpdate)) {
                        String title = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.costingNotAllowed.title"); //$NON-NLS-1$
                        String message = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.costingNotAllowed.msg"); //$NON-NLS-1$
                        MessageDialog.openInformation(shell, title, message);
                        return;
                    }
                	
                	boolean cancelled = openEditorIfNeeded(modelResource);
                	
                	if( cancelled ) {
                		return;
                	}
                	final Resource resource = modelResource.getEmfResource();
                	
                    executeInTransaction(resource, shell);
                }
            } catch (Exception e) {
                InternalModelerJdbcUiPluginConstants.Util.log(e);
                final String title = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.errorTitle"); //$NON-NLS-1$
                final String message = InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.errorMessage"); //$NON-NLS-1$
                MessageDialog.openError(shell, title, message);
            }
        }
    }
    
    private void executeInTransaction(final Resource resource, final Shell shell) {
        boolean requiredStart = ModelerCore.startTxn(true,true,"Update Cost Statistics",this); //$NON-NLS-1$
        boolean succeeded = false;
        try {
        	internalExecute(resource, shell);
        	
            succeeded = true;
        } finally {
            //if we started the txn, commit it.
            if(requiredStart){
                if(succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
         
    }
    
    private void internalExecute(final Resource resource, final Shell shell) {
    	if (resource != null) {
            final JdbcSource source = JdbcUtil.findJdbcSource(resource);
            if (source != null) {
                final List emfTables = RelationalUtil.findTables(resource);
                final Map tblStats = createTableInfos(emfTables);
                if (tblStats != null && tblStats.size() > 0) {
                    CostAnalysisDialog dialog = new CostAnalysisDialog(
                                                                       shell,
                                                                       InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.taskDescription"), //$NON-NLS-1$
                                                                       InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.passwordPrompt", new Object[] {source.getUrl(), source.getUsername()}), null, null); //$NON-NLS-1$
                    dialog.open();

                    final String password = dialog.getValue();
                    if (password != null) {
                        final Job job = new Job(
                                                InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.jobDescription")) { //$NON-NLS-1$
                            @Override
                            protected IStatus run( IProgressMonitor monitor ) {
                                try {
                                    monitor.beginTask(InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.taskDescription"), calculateNumberOfWorkIncrements(tblStats.values())); //$NON-NLS-1$

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

                                    return new Status(
                                                      IStatus.OK,
                                                      ModelerJdbcUiConstants.PLUGIN_ID,
                                                      IStatus.OK,
                                                      InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.statusFinished", emfTables.size()), null); //$NON-NLS-1$
                                } catch (Exception e) {
                                    InternalModelerJdbcUiPluginConstants.Util.log(e);
                                    return new Status(
                                                      IStatus.ERROR,
                                                      ModelerJdbcUiConstants.PLUGIN_ID,
                                                      IStatus.ERROR,
                                                      InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.errorMessage"), e); //$NON-NLS-1$
                                } finally {
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
                                                  InternalModelerJdbcUiPluginConstants.Util.getString("JdbcCostAnalysisAction.taskDescription"), InternalModelerJdbcUiPluginConstants.Util.getString("CostAnalysisAction.noValidTablesMessage")); //$NON-NLS-1$ //$NON-NLS-2$
                }
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

    @Override
    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    @Override
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
    
    
    /**
     * Should only be called if current object and model are not <code>null</code>.
     * 
     * @since 5.5.3
     */
    private boolean openEditorIfNeeded(ModelResource currentModel) {
    	boolean openEditorCancelled = false;
        // we only need to worry about the readonly status if the file is not currently open,
        // and its underlying IResource is not read only
        
    	if (currentModel!=null && !isEditorOpen(currentModel) && !currentModel.getResource().getResourceAttributes().isReadOnly()) {
            final IFile modelFile = (IFile)currentModel.getResource();
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();

            // may want to change these text strings eventually:
            if (MessageDialog.openQuestion(shell,
                                           ModelEditorManager.OPEN_EDITOR_TITLE,
                                           ModelEditorManager.OPEN_EDITOR_MESSAGE)) {
                // load and activate, not async (to prevent multiple dialogs from coming up):
                // Changed to use method that insures Object editor mode is on
                ModelEditorManager.openInEditMode(modelFile, true, UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);

            } else {
            	openEditorCancelled = true;
            }
        }
        
        return openEditorCancelled;
    }
    
    private boolean isEditorOpen(ModelResource currentModel) {
        if (currentModel != null) {
            IFile modelFile = (IFile)currentModel.getResource();
            return (ModelEditorManager.isOpen(modelFile));
        }

        return false;
    }
}
