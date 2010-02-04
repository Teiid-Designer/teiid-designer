/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import net.sourceforge.sqlexplorer.ISqlExecVetoListener;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr;


/** 
 * @since 4.3
 */
public class StaledExtensionModuleChecker extends StaledVDBChecker {

    /** 
     * @see net.sourceforge.sqlexplorer.ISqlExecVetoListener#continueSqlExecution(java.lang.Object)
     * @since 4.3
     */
    @Override
    public IStatus continueSqlExecution(Object theConnection) {
        IStatus result = ISqlExecVetoListener.CONTINUE_EXECUTION;
        IVdbConnectionMgr connMgr = DqpUiPlugin.getDefault().getVdbConnectionMgr();

        if (connMgr.isExtensionModuleStale(theConnection)){
            result = handleStaleConnection(theConnection, "StaleVdbConnectionDialog.staledExtModule");//$NON-NLS-1$
        }
        
        return result;
    }

}
