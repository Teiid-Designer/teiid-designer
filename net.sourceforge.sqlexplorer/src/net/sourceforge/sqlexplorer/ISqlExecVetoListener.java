/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package net.sourceforge.sqlexplorer;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * An <code>ISqlExecVetoListener</code> can be registered with the {@link net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin} to
 * potentially stop SQL execution from a SQL Editor.
 * 
 * @since 5.0.1
 */
public interface ISqlExecVetoListener {

    /**
     * Constant suitable to use when indicating SQL execution should continue.
     * 
     * @since 5.0.1
     */
    IStatus CONTINUE_EXECUTION = new Status(IStatus.OK, SQLExplorerPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$

    /**
     * Indicates if the SQL in SQL Editor should be executed. When stopping execution the status message should explain why.
     * 
     * @param theConnection the connection on which the SQL is being executed
     * @return a status with severity not equal to {@link IStatus#ERROR} to continue with execution; otherwise execution will be
     *         stopped.
     * @since 5.0.1
     */
    IStatus continueSqlExecution( Object theConnection );

}
