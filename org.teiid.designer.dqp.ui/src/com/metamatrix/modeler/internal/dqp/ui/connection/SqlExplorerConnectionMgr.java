/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.connection;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.sessiontree.model.RootSessionTreeNode;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr;

/**
 * The <code>SqlExplorerConnectionUtils</code> class manages VDB connections when using the
 * <code>net.sourceforge.sqlexplorer</code> plugin.
 * 
 * @since 5.0
 */
public final class SqlExplorerConnectionMgr implements DqpUiConstants, IVdbConnectionMgr {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private static final SessionTreeNode[] NO_CONNECTIONS = new SessionTreeNode[0];

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#closeAllConnections()
     * @since 5.0
     */
    public void closeAllConnections() {
        final Object[] connections = getAllConnections();

        if ((connections != null) && (connections.length != 0)) for (int i = 0; i < connections.length; ++i) {
            CoreArgCheck.isInstanceOf(SessionTreeNode.class, connections[i], connections[i].getClass().getName());
            closeConnectionImpl((SessionTreeNode)connections[i]);
        }
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#closeConnection(com.metamatrix.vdb.edit.VdbEditingContext)
     * @since 5.0
     */
    public boolean closeConnection( final Vdb theVdbContext ) {
        boolean result = true;
        final Object connection = getConnection(theVdbContext);

        if (connection != null) {
            CoreArgCheck.isInstanceOf(SessionTreeNode.class, connection, connection.getClass().getName());
            result = closeConnectionImpl((SessionTreeNode)connection);
        }

        return result;
    }

    /**
     * Closes the specified VDB connection.
     * 
     * @param theConnection the connection being closed
     * @return <code>true</code> if the connection was closed successfully; <code>false</code> otherwise.
     * @since 5.0
     */
    private boolean closeConnectionImpl( final SessionTreeNode theConnection ) {
        theConnection.close();
        return isConnectionClosed(theConnection);
    }

    /**
     * Obtains all VDB connections.
     * 
     * @return the connections (never <code>null</code>)
     * @since 5.0
     */
    private SessionTreeNode[] getAllConnections() {
        SessionTreeNode[] result = null;
        final RootSessionTreeNode root = SQLExplorerPlugin.getDefault().stm.getRoot();

        if (root != null) {
            final Object[] kids = root.getChildren();

            if ((kids != null) && (kids.length != 0)) {
                final List temp = Arrays.asList(kids);
                result = (SessionTreeNode[])temp.toArray(new SessionTreeNode[temp.size()]);
            }
        }

        if (result == null) result = NO_CONNECTIONS;

        return result;
    }

    /**
     * Obtains the connection for the specified <code>Vdb</code>.
     * 
     * @param theVdbContext the context whose connection is being requested
     * @return the connection or <code>null</code> if no connection open
     * @since 5.0
     */
    private SessionTreeNode getConnection( final Vdb theVdbContext ) {
        SessionTreeNode result = null;
        final SessionTreeNode[] connections = getAllConnections();

        if (connections.length != 0) {
            final String vdbPath = getVdbPath(theVdbContext);

            for (int i = 0; i < connections.length; ++i)
                // we ensure alias name is not null when we create alias
                // we also ensure VDB has a name
                if (getConnectionName(connections[i]).equals(vdbPath)) {
                    result = connections[i];
                    break;
                }
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#getConnectionName(java.lang.Object)
     * @since 5.0.1
     */
    public String getConnectionName( final Object theConnection ) {
        CoreArgCheck.isInstanceOf(SessionTreeNode.class, theConnection, theConnection.getClass().getName());
        return ((SessionTreeNode)theConnection).getAlias().getName();
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#getVdbEditingContext(java.lang.Object)
     * @since 5.0.1
     */
    public Vdb getVdb( final Object connection ) {
        CoreArgCheck.isInstanceOf(SessionTreeNode.class, connection, connection.getClass().getName());
        // find the associated VDB context for the connection
        return new Vdb(new Path(getConnectionName(connection)), new NullProgressMonitor());
    }

    /**
     * Obtains the time the resource of the VDB context was last saved.
     * 
     * @param theVdbContext the context whose last saved time is being requested
     * @return the time
     * @since 5.0
     */
    private long getVdbLastSavedTime( final Vdb vdb ) {
        final File file = vdb.getName().toFile();
        return file.lastModified();
    }

    /**
     * Obtains the OS path to the VDB having the specified context.
     * 
     * @param vdb the context of the VDB whose path is being requested
     * @return the path
     * @since 5.0.1
     */
    private String getVdbPath( final Vdb vdb ) {
        return vdb.getName().toOSString();
    }

    private boolean hasStaleFile( final File folder,
                                  final long timeToCompare ) {
        if (folder.exists()) {
            final File[] files = folder.listFiles();
            for (final File file : files)
                if (file.lastModified() > timeToCompare) return true;
        }
        return false;
    }

    /**
     * Indicates if the specified connection has been closed.
     * 
     * @param theConnection the connection being checked
     * @return <code>true</code> if closed; <code>false</code> otherwise.
     * @since 5.0
     */
    private boolean isConnectionClosed( final SessionTreeNode theConnection ) {
        return ((theConnection.getConnection() == null) || (theConnection.getConnection().getTimeClosed() != null));
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isConnectionOpen(java.lang.Object)
     * @since 5.0.1
     */
    public boolean isConnectionOpen( final Object theConnection ) {
        boolean result = false;
        final Vdb vdb = getVdb(theConnection);

        if (vdb != null) result = isVdbConnectionOpen(vdb);

        return result;
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isConnectionStale(java.lang.Object)
     * @since 5.0.1
     */
    public boolean isConnectionStale( final Object theConnection ) {
        boolean result = false;
        final Vdb context = getVdb(theConnection);

        if (context != null) result = isVdbConnectionStale(context);

        return result;
    }

    public boolean isExtensionModuleStale( final Object theConnection ) {
        boolean result = false;
        Object temp = null;
        final Vdb vdb = getVdb(theConnection);

        if (vdb != null) temp = getConnection(vdb);

        if (temp != null) {
            CoreArgCheck.isInstanceOf(SessionTreeNode.class, temp, temp.getClass().getName());
            final SessionTreeNode connection = (SessionTreeNode)temp;

            if (!isConnectionClosed(connection)) {
                final Date connTime = connection.getConnection().getTimeOpened();

                if (connTime != null) {
                    final IPath connPath = DqpPath.getRuntimeConnectorsPath();
                    final String udfPath = UdfManager.INSTANCE.getUdfModelPath().toFile().getAbsolutePath();
                    result = hasStaleFile(new File(connPath.toOSString()), connTime.getTime())
                             || hasStaleFile(new File(udfPath), connTime.getTime());
                }
            }
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isVdbConnectionOpen(com.metamatrix.vdb.edit.VdbEditingContext)
     * @since 5.0
     */
    public boolean isVdbConnectionOpen( final Vdb theVdb ) {
        boolean result = false;
        final Object conn = getConnection(theVdb);

        if (conn != null) {
            CoreArgCheck.isInstanceOf(SessionTreeNode.class, conn, conn.getClass().getName());
            final SessionTreeNode connection = (SessionTreeNode)conn;

            result = !isConnectionClosed(connection);
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isVdbConnectionStale(com.metamatrix.vdb.edit.VdbEditingContext)
     * @since 5.0.1
     */
    public boolean isVdbConnectionStale( final Vdb theVdb ) {
        boolean result = false;
        final Object temp = getConnection(theVdb);

        if (temp != null) {
            CoreArgCheck.isInstanceOf(SessionTreeNode.class, temp, temp.getClass().getName());
            final SessionTreeNode connection = (SessionTreeNode)temp;

            if (!isConnectionClosed(connection)) {
                final Date connTime = connection.getConnection().getTimeOpened();

                if (connTime != null) {
                    CoreArgCheck.isInstanceOf(Vdb.class, theVdb, theVdb.getClass().getName());
                    final long vdbTime = getVdbLastSavedTime(theVdb);
                    result = (connTime.getTime() < vdbTime);
                }
            }
        }

        return result;
    }
}
