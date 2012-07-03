/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui.plan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.sqltools.core.DatabaseIdentifier;
import org.eclipse.datatools.sqltools.core.SQLDevToolsConfiguration;
import org.eclipse.datatools.sqltools.core.SQLToolsFacade;
import org.eclipse.datatools.sqltools.core.services.ConnectionService;
import org.eclipse.datatools.sqltools.sql.parser.ParsingResult;
import org.eclipse.datatools.sqltools.sqleditor.ISQLEditorActionConstants;
import org.eclipse.datatools.sqltools.sqleditor.SQLEditor;
import org.eclipse.datatools.sqltools.sqleditor.plan.BaseExplainAction;
import org.eclipse.datatools.sqltools.sqleditor.plan.Images;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IUpdate;
import org.teiid.datatools.connectivity.ui.Activator;
import org.teiid.datatools.connectivity.ui.Messages;
import org.teiid.datatools.views.ExecutionPlanView;

public class TeiidExplainSQLActionDelegate extends BaseExplainAction implements
IEditorActionDelegate, ISelectionChangedListener, IUpdate {

    protected SQLEditor _sqlEditor;

    public TeiidExplainSQLActionDelegate() {
        String title = Messages.getString("TeiidExplainSQLActionDelegate.title"); //$NON-NLS-1$
        String tooltip = Messages.getString("TeiidExplainSQLActionDelegate.tooltip"); //$NON-NLS-1$
        setText(title);
        setToolTipText(tooltip);
        setImageDescriptor(Images.DESC_EXPLAIN_SQL);
        setActionDefinitionId(ISQLEditorActionConstants.EXPLAIN_SQL_ACTION_ID);
    }

    public void setActiveEditor( SQLEditor targetEditor ) {
        _sqlEditor = targetEditor;
        targetEditor.getSelectionProvider().addSelectionChangedListener(this);
        update();
    }

    public void update() {
        String sql = getSQLStatements();
        setEnabled(_sqlEditor != null && (_sqlEditor.isConnected()) && super.canBeEnabled()
                && (sql != null && sql.length() > 0));
    }

    @Override
    public DatabaseIdentifier getDatabaseIdentifier() {
        return _sqlEditor == null ? null : _sqlEditor.getDatabaseIdentifier();
    }

    @Override
    public String getSQLStatements() {
        if (_sqlEditor == null) {
            return null;
        }
        String selectedSQL = _sqlEditor.getSelectedText();
        if (selectedSQL != null && !selectedSQL.isEmpty()) {
            return SQLToolsFacade.getDBHelper(getDatabaseIdentifier()).preprocessSQLScript(selectedSQL);
        }

        return getCurrentStatements();
    }

    private String getCurrentStatements() {
        String selectedText = null;
        IDocument doc = _sqlEditor.getDocumentProvider().getDocument(_sqlEditor.getEditorInput());

        ITextSelection selection = (ITextSelection)_sqlEditor.getSelectionProvider().getSelection();
        int selectionLine = selection.getStartLine();
        try {
            IRegion lineInfo = doc.getLineInformation(selectionLine);
            selectedText = doc.get(lineInfo.getOffset(), lineInfo.getLength());
        } catch (BadLocationException ex) {
            return null;
        }

        return SQLToolsFacade.getDBHelper(getDatabaseIdentifier()).preprocessSQLScript(selectedText);
    }

    /**
     * Sets the focus to the editor after the execution plan is shown
     */
    @Override
    public Runnable getPostRun() {
        Runnable postRun = new Runnable() {
            public void run() {
                _sqlEditor.getEditorSite().getPage().activate(_sqlEditor);
            }
        };
        return postRun;
    }

    /**
     * Returns the variable declarations in the SQL Editor
     */
    @Override
    protected HashMap getVariableDeclarations() {
        ITextSelection _selection = (ITextSelection)_sqlEditor.getSelectionProvider().getSelection();
        int start = 0;
        int length = 0;
        if (_selection == null) {
            _selection = (ITextSelection)_sqlEditor.getSelectionProvider().getSelection();
        }
        // get the offset of the selection
        if (_selection != null && !_selection.isEmpty()) {
            start = _selection.getOffset();
            length = _selection.getLength();
            if (length < 0) {
                length = -length;
                start -= length;
            }
        }
        // when user selects a range
        int offset = length > 0 ? start + 1 : start;

        IDocument document = _sqlEditor.getDocumentProvider().getDocument(_sqlEditor.getEditorInput());
        ParsingResult result = _sqlEditor.getParsingResult();
        HashMap variables = new HashMap();
        if (result != null) {
            variables = result.getVariables(document, offset);
            HashMap sp_params = result.getParameters(document, offset);
            variables.putAll(sp_params);
        }
        return variables;
    }

    /**
     * Updates the action when selection changes
     * 
     * @param event
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        if (event.getSelection() instanceof ITextSelection) {
            update();
        }
    }

    public void setActiveEditor( IAction action,
                                 IEditorPart targetEditor ) {
        setActiveEditor((SQLEditor)targetEditor);
    }

    public void run() {
        if (!isEnabled()) {
            return;
        }
        // Get SQL from the Editor
        String sql = getSQLStatements();

        // Establish Connection to run the plan Query
        establishConnection();

        // Get the Teiid Execution Plan
        String planStr = null;
        try {
            planStr = getExecutionPlan(_conn, sql);
        } catch (SQLException e1) {
            String message = Messages.getString("TeiidExplainSQLActionDelegate.getPlanError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e1);
            Activator.getDefault().getLog().log(status);
            // Show Error Dialog, then exit
            Shell shell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
            MessageDialog.openInformation(shell, Messages.getString("TeiidExplainSQLActionDelegate.getPlanErrorDialog.title"), //$NON-NLS-1$
                                          Messages.getString("TeiidExplainSQLActionDelegate.getPlanErrorDialog.msg")); //$NON-NLS-1$
            handleEnd(_conn);
        }

        // Re-direct to the Teiid Execution Plan View, and update with results
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        IViewPart viewPart = null;
        try {
            if (window != null) {
                viewPart = window.getActivePage().showView(ExecutionPlanView.VIEW_ID);
                if (viewPart instanceof ExecutionPlanView) {
                    String panelDescription = org.teiid.datatools.connectivity.ui.Messages.getString("TeiidExplainSQLActionDelegate.panelDescription"); //$NON-NLS-1$
                    ((ExecutionPlanView)viewPart).updateContents(panelDescription, sql, planStr);
                }
            }
        } catch (PartInitException e) {
            String message = org.teiid.datatools.connectivity.ui.Messages.getString("TeiidExplainSQLActionDelegate.initViewError"); //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message, e);
            Activator.getDefault().getLog().log(status);
        }

        // Closes the connection
        handleEnd(_conn);
    }

    private void establishConnection() {
        if (_conn == null) {
            String profileName = _sqlEditor.getDatabaseIdentifier().getProfileName();
            String dbName = _sqlEditor.getDatabaseIdentifier().getDBname();

            final SQLDevToolsConfiguration config = SQLToolsFacade.getConfigurationByProfileName(profileName);
            final ConnectionService conService = config.getConnectionService();
            _conn = conService.createConnection(profileName, dbName);
        }
    }

    private String getExecutionPlan( Connection sqlConnection,
                                     String sql ) throws SQLException {
        String executionPlan = null;

        if (sql == null || sql.length() == 0) {
            throw new SQLException("An SQL statement is required to retrieve the execution plan");
        }

        Statement stmt = sqlConnection.createStatement();
        stmt.execute("SET SHOWPLAN DEBUG"); //$NON-NLS-1$
        stmt.executeQuery(sql);
        ResultSet planRs = stmt.executeQuery("SHOW PLAN"); //$NON-NLS-1$
        planRs.next();
        executionPlan = planRs.getString("PLAN_XML"); //$NON-NLS-1$
        return executionPlan;
    }

    public void run( IAction action ) {
    }

    public void selectionChanged( IAction action,
                                  ISelection selection ) {
        if (selection instanceof ITextSelection) {
            update();
        }
    }
}
