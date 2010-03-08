/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;

/**
 * The <code>ReconnectJob</code> attempts to reconnect to the selected {@link Server server(s)}.
 */
public final class ReconnectJob extends Job {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The server being reconnected to.
     */
    private final Server server;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param server the server being connected to (never <code>null</code>)
     */
    public ReconnectJob( Server server ) {
        super(UTIL.getString("reconnectJobTaskName", server.getUrl())); //$NON-NLS-1$
        this.server = server;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#belongsTo(java.lang.Object)
     */
    @Override
    public boolean belongsTo( Object family ) {
        return DqpUiConstants.RECONNECT_SERVER_FAMILY.equals(family);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus run( IProgressMonitor monitor ) {
        IStatus result = null;
        ServerManager serverManager = DqpPlugin.getInstance().getServerRegistry();

        try {
            String taskName = UTIL.getString("reconnectJobTaskName", this.server.getUrl()); //$NON-NLS-1$
            monitor.beginTask(taskName, 1);
            monitor.setTaskName(taskName);
            IStatus status = serverManager.ping(this.server);
            // TODO: Convert status from ping into successful Reconnect message?
            result = status;
            // result = Utils.convert(status);
        } catch (Exception e) {
            String msg = null;

            if (e instanceof InterruptedException) {
                msg = e.getLocalizedMessage();
            } else {
                msg = UTIL.getString("reconnectJobUnexpectedErrorMsg", this.server.getUrl()); //$NON-NLS-1$
            }

            result = new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        } finally {
            monitor.done();
            done(result);
        }

        return result;
    }

}
