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

package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr;

import net.sourceforge.sqlexplorer.ISqlExecVetoListener;


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
