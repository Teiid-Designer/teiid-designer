/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.dqp.ui.actions;

import java.io.File;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import com.metamatrix.jdbc.api.Connection;
import com.metamatrix.modeler.dqp.execution.VdbExecutionFileManager;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.logview.OpenLogDialog;


/**
 * ShowDqpLogAction is an action contributed to SQLExplorer's Connections view that
 * can find the log file from the DQP connection and display it to the user.
 * @since 5.0.1
 */
public class ShowDqpLogAction implements IViewActionDelegate {

    
    private Shell shell;
    private IPath logFilePath;
    
    /** 
     * 
     * @since 5.0.1
     */
    public ShowDqpLogAction() {
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
        if (logFilePath != null) {
            // open the log file path in the default system program
            if ( ! Program.launch(logFilePath.toOSString()) ) {
                Program p = Program.findProgram(VdbExecutionFileManager.LOG_EXT);
                if (p != null) {
                    p.execute(logFilePath.toOSString());
                } else {
                    // open the log file path in a dialog
                    OpenLogDialog openDialog = new OpenLogDialog(shell, logFilePath.toFile());
                    openDialog.create();
                    openDialog.open();
                }                  
            }

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
            try {
                // pull the log file out of the connection's DQP properties
                SQLConnection conn = ((SessionTreeNode) obj).getConnection();
                if ( conn != null && conn.getConnection() != null ) {
                    String logPath = ((Connection) conn.getConnection()).getAdminAPI().getSystem().getPropertyValue(VdbExecutionFileManager.DQP_LOG_PROP);
                    File logFile = new File(logPath);
                    
                    if (logFile.exists()) {
                        logFilePath = new Path(logFile.getCanonicalPath());
                    }
                    else {
                        String name = logPath.substring(0, logPath.lastIndexOf(VdbExecutionFileManager.LOG_EXT))
                                      + VdbExecutionFileManager.DQP_LOG_EXT;
                        logFile = DqpPath.getVdbExecutionPath().append(name).toFile();
                        logFilePath = new Path(name);
                    }
                } else {
                    action.setEnabled(false);
                }
            } catch (Exception e) {
                DqpUiConstants.UTIL.log(e);
                action.setEnabled(false);
            }
        } else {
            action.setEnabled(false);
        }
        
    }

}
