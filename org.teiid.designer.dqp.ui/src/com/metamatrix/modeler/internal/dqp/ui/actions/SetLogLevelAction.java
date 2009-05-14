/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.actions;

import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.dialogs.ListDialog;

import com.metamatrix.admin.api.core.Admin;
import com.metamatrix.admin.api.objects.LogConfiguration;
import com.metamatrix.jdbc.api.Connection;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/**
 * SetLogLevelAction is an action contributed to SQLExplorer's Connections view that
 * can find the log file from the DQP connection and display it to the user.
 * @since 5.0.1
 */
public class SetLogLevelAction implements IViewActionDelegate {

    
    private Shell shell;
    private SessionTreeNode sessionTreeNode;
    
    /** 
     * 
     * @since 5.0.1
     */
    public SetLogLevelAction() {
        super();
    }

    /** 
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     * @since 5.0.1
     */
    public void init(IViewPart view) {
        shell=view.getSite().getShell();
    }

    /** 
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 5.0.1
     */
    public void run(IAction action) {
        
        try {
            // pull the log file out of the connection's DQP properties
            SQLConnection conn = sessionTreeNode.getConnection();
            Admin admin = ((Connection) conn.getConnection()).getAdminAPI();

            LogConfiguration lc = admin.getLogConfiguration();
            int currentLogLevel = lc.getLogLevel();
            LogLevelSelectionDialog dlg = new LogLevelSelectionDialog(shell, currentLogLevel);
            int code = dlg.open();

            if (code == IDialogConstants.OK_ID) {
               int result = dlg.getSelectedLevel();
               lc.setLogLevel(result);
               admin.setLogConfiguration(lc);
            }
            
        } catch (Exception e) {
            DqpUiConstants.UTIL.log(e);
            action.setEnabled(false);
        }

    }

    /** 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     * @since 5.0.1
     */
    public void selectionChanged(IAction action,
                                 ISelection selection) {
        Object obj = SelectionUtilities.getSelectedObject(selection);
        if ( obj instanceof SessionTreeNode ) {
            action.setEnabled(true);
            sessionTreeNode = (SessionTreeNode) obj;
        } else {
            action.setEnabled(false);
        }
        
    }

}


/**
 * A ListDialog to allow the user to select the log level 
 * @since 5.0.1
 */
class LogLevelSelectionDialog extends ListDialog implements IStructuredContentProvider {

    private Object[] levels = new Object[] {DqpUiConstants.UTIL.getStringOrKey("LogLevelSelectionDialog.default"), //$NON-NLS-1$
                                            DqpUiConstants.UTIL.getStringOrKey("LogLevelSelectionDialog.detail"), //$NON-NLS-1$
                                            DqpUiConstants.UTIL.getStringOrKey("LogLevelSelectionDialog.trace")}; //$NON-NLS-1$
    
    public LogLevelSelectionDialog(Shell shell, int level) {
        super(shell);

        setShellStyle(getShellStyle() | SWT.RESIZE);
        setLabelProvider(new LabelProvider());
        setContentProvider(this);
        setInitialSelections(getInitialSelection(level));
        setInput(this.levels);
        setTitle(DqpUiConstants.UTIL.getStringOrKey("LogLevelSelectionDialog.title")); //$NON-NLS-1$
        setMessage(DqpUiConstants.UTIL.getStringOrKey("LogLevelSelectionDialog.msg")); //$NON-NLS-1$
    }
    
    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 5.0.1
     */
    public void dispose() {
    }
    
    /** 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 5.0.1
     */
    public Object[] getElements(Object theInputElement) {
        return this.levels;
    }
    
    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 5.0
     */
    public void inputChanged(Viewer theViewer,
                             Object theOldInput,
                             Object theNewInput) {
    }
    
    public int getSelectedLevel() {
        Object[] selection = super.getResult();
        if (selection.length == 1) {
            if ( selection[0].equals(levels[1]) ) {
                return LogConfiguration.DETAIL;
            }
            if ( selection[0].equals(levels[2]) ) {
                return LogConfiguration.TRACE;
            }
        }
        return LogConfiguration.INFO;
    }
    
    private Object[] getInitialSelection(int logLevel) {
        switch (logLevel) {
            case LogConfiguration.CRITICAL:
            case LogConfiguration.ERROR:
            case LogConfiguration.WARNING:
            case LogConfiguration.INFO:
            default:
                return new Object[] {this.levels[0]};
            case LogConfiguration.DETAIL:
                return new Object[] {this.levels[1]};
            case LogConfiguration.TRACE:
                return new Object[] {this.levels[2]};
        }
    }
    
}
