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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.teiid.designer.udf.UdfManager;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.vdb.edit.VdbContextEditor;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;


/**
 * The <code>SqlExplorerConnectionUtils</code> class manages VDB connections when using the 
 * <code>net.sourceforge.sqlexplorer</code> plugin.
 * @since 5.0
 */
public final class SqlExplorerConnectionMgr implements DqpUiConstants,
                                                       IVdbConnectionMgr {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final SessionTreeNode[] NO_CONNECTIONS = new SessionTreeNode[0];
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#closeAllConnections()
     * @since 5.0
     */
    public void closeAllConnections() {
        Object[] connections = getAllConnections();
        
        if ((connections != null) && (connections.length != 0)) {
            for (int i = 0; i < connections.length; ++i) {
                Assertion.isInstanceOf(connections[i], SessionTreeNode.class, connections[i].getClass().getName());
                closeConnectionImpl((SessionTreeNode)connections[i]);
            }
        }
    }

    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#closeConnection(com.metamatrix.vdb.edit.VdbEditingContext)
     * @since 5.0
     */
    public boolean closeConnection(VdbEditingContext theVdbContext) {
        boolean result = true;
        Object connection = getConnection(theVdbContext);
        
        if (connection != null) {
            Assertion.isInstanceOf(connection, SessionTreeNode.class, connection.getClass().getName());
            result = closeConnectionImpl((SessionTreeNode)connection);
        }
        
        return result;
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#closeConnection(com.metamatrix.vdb.edit.VdbContextEditor)
     * @since 5.0
     */
    public boolean closeConnection(VdbContextEditor theVdbContext) {
        boolean result = true;
        Object connection = getConnection(theVdbContext);
        
        if (connection != null) {
            Assertion.isInstanceOf(connection, SessionTreeNode.class, connection.getClass().getName());
            result = closeConnectionImpl((SessionTreeNode)connection);
        }
        
        return result;
    }

    /**
     * Closes the specified VDB connection. 
     * @param theConnection the connection being closed
     * @return <code>true</code> if the connection was closed successfully; <code>false</code> otherwise.
     * @since 5.0
     */
    private boolean closeConnectionImpl(SessionTreeNode theConnection) {
        theConnection.close();
        return isConnectionClosed(theConnection);
    }
    
    /**
     * Obtains all VDB connections.
     * @return the connections (never <code>null</code>)
     * @since 5.0
     */
    private SessionTreeNode[] getAllConnections() {
        SessionTreeNode[] result = null;
        RootSessionTreeNode root = SQLExplorerPlugin.getDefault().stm.getRoot();
        
        if (root != null) {
            Object[] kids = root.getChildren();
            
            if ((kids != null) && (kids.length != 0)) {
                List temp =  Arrays.asList(kids);
                result = (SessionTreeNode[])temp.toArray(new SessionTreeNode[temp.size()]);
            }
        }

        if (result == null) {
            result = NO_CONNECTIONS;
        }
        
        return result;
    }
    
    /**
     * Obtains the connection for the specified <code>VdbEditingContext</code>.
     * @param theVdbContext the context whose connection is being requested
     * @return the connection or <code>null</code> if no connection open
     * @since 5.0
     */
    private SessionTreeNode getConnection(VdbEditingContext theVdbContext) {
        SessionTreeNode result = null;
        SessionTreeNode[] connections = getAllConnections();
        
        if (connections.length != 0) {
            String vdbPath = getVdbPath(theVdbContext);

            for (int i = 0; i < connections.length; ++i) {
                // we ensure alias name is not null when we create alias
                // we also ensure VDB has a name
                if (getConnectionName(connections[i]).equals(vdbPath)) {
                    result = connections[i];
                    break;
                }
            }
        }

        return result;
    }
    private SessionTreeNode getConnection(VdbContextEditor theVdbContext) {
        SessionTreeNode result = null;
        SessionTreeNode[] connections = getAllConnections();
        
        if (connections.length != 0) {
            String vdbPath = getVdbPath(theVdbContext);

            for (int i = 0; i < connections.length; ++i) {
                // we ensure alias name is not null when we create alias
                // we also ensure VDB has a name
                if (getConnectionName(connections[i]).equals(vdbPath)) {
                    result = connections[i];
                    break;
                }
            }
        }

        return result;
    }
    
    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#getConnectionName(java.lang.Object)
     * @since 5.0.1
     */
    public String getConnectionName(Object theConnection) {
        Assertion.isInstanceOf(theConnection, SessionTreeNode.class, theConnection.getClass().getName());
        return ((SessionTreeNode)theConnection).getAlias().getName();
    }
    
    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#getVdbEditingContext(java.lang.Object)
     * @since 5.0.1
     */
    public VdbEditingContext getVdbEditingContext(Object theConnection) {
        Assertion.isInstanceOf(theConnection, SessionTreeNode.class, theConnection.getClass().getName());
        
        VdbEditingContext result = null;
        String connectionName = getConnectionName(theConnection);
        
        // find the associated VDB context for the connection
        try {
            result = VdbEditPlugin.createVdbEditingContext(new Path(connectionName));
        } catch (CoreException theException) {
            UTIL.log(theException);
        }
        
        return result;
    }
    
    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#getVdbContextEditor(java.lang.Object)
     * @since 5.0
     */
    public VdbContextEditor getVdbContextEditor(Object theConnection) {
        Assertion.isInstanceOf(theConnection, SessionTreeNode.class, theConnection.getClass().getName());
        
        VdbContextEditor result = null;
        String connectionName = getConnectionName(theConnection);
        
        // find the associated VDB context for the connection
        try {
            final IPath pathToVdbFile = new Path(connectionName);
            final IPath vdbWorkingPath = new Path(VdbEditPlugin.getVdbWorkingDirectory().getAbsolutePath());
            result = VdbEditPlugin.createSharedWsVdbContextEditor(pathToVdbFile, vdbWorkingPath);
        } catch (CoreException theException) {
            UTIL.log(theException);
        }
        
        return result;
    }

    /**
     * Obtains the time the resource of the VDB context was last saved.
     * @param theVdbContext the context whose last saved time is being requested
     * @return the time
     * @since 5.0
     */
    private long getVdbLastSavedTime(InternalVdbEditingContext theVdbContext) {
        File file = theVdbContext.getPathToVdb().toFile();
        return file.lastModified();
    }
    private long getVdbLastSavedTime(VdbContextEditor theVdbContext) {
        File file = theVdbContext.getVdbFile();
        return file.lastModified();
    }
    
    /**
     * Obtains the OS path to the VDB having the specified context. 
     * @param theVdbContext the context of the VDB whose path is being requested
     * @return the path
     * @throws ClassCastException if the context is not an instanceof {@link InternalVdbEditingContext}
     * @since 5.0.1
     */
    private String getVdbPath(VdbEditingContext theVdbContext) {
        Assertion.isInstanceOf(theVdbContext, InternalVdbEditingContext.class, theVdbContext.getClass().getName());
        return ((InternalVdbEditingContext)theVdbContext).getPathToVdb().toOSString();
    }
    private String getVdbPath(VdbContextEditor theVdbContext) {
        Assertion.isNotNull(theVdbContext);
        return theVdbContext.getVdbFile().getAbsolutePath();
    }
    
    /**
     * Indicates if the specified connection has been closed. 
     * @param theConnection the connection being checked
     * @return <code>true</code> if closed; <code>false</code> otherwise.
     * @since 5.0
     */
    private boolean isConnectionClosed(SessionTreeNode theConnection) {
        return ((theConnection.getConnection() == null) || (theConnection.getConnection().getTimeClosed() != null));
    }
    
    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isConnectionOpen(java.lang.Object)
     * @since 5.0.1
     */
    public boolean isConnectionOpen(Object theConnection) {
        boolean result = false;
        if( ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            VdbContextEditor context = getVdbContextEditor(theConnection);
            
            if (context != null) {
                result = isVdbConnectionOpen(context);
            }
        } else {
            VdbEditingContext context = getVdbEditingContext(theConnection);
            
            if (context != null) {
                result = isVdbConnectionOpen(context);
            }
        }
        
        return result;
    }
    
    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isVdbConnectionOpen(com.metamatrix.vdb.edit.VdbEditingContext)
     * @since 5.0
     */
    public boolean isVdbConnectionOpen(VdbEditingContext theVdbContext) {
        boolean result = false;
        Object conn = getConnection(theVdbContext);
        
        if (conn != null) {
            Assertion.isInstanceOf(conn, SessionTreeNode.class, conn.getClass().getName());
            SessionTreeNode connection = (SessionTreeNode)conn;
            
            result = !isConnectionClosed(connection);
        }
        
        return result;
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isVdbConnectionOpen(com.metamatrix.vdb.edit.VdbContextEditor)
     * @since 5.0
     */
    public boolean isVdbConnectionOpen(VdbContextEditor theVdbContext) {
        boolean result = false;
        Object conn = getConnection(theVdbContext);
        
        if (conn != null) {
            Assertion.isInstanceOf(conn, SessionTreeNode.class, conn.getClass().getName());
            SessionTreeNode connection = (SessionTreeNode)conn;
            
            result = !isConnectionClosed(connection);
        }
        
        return result;
    }
    
    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isConnectionStale(java.lang.Object)
     * @since 5.0.1
     */
    public boolean isConnectionStale(Object theConnection) {
        boolean result = false;
        if( ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            VdbContextEditor context = getVdbContextEditor(theConnection);
            
            if (context != null) {
                result = isVdbConnectionStale(context);
            }
        } else {
            VdbEditingContext context = getVdbEditingContext(theConnection);
            
            if (context != null) {
                result = isVdbConnectionStale(context);
            }
            
        }

        return result;
    }
    
    /** 
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isVdbConnectionStale(com.metamatrix.vdb.edit.VdbEditingContext)
     * @since 5.0.1
     */
    public boolean isVdbConnectionStale(VdbEditingContext theVdbContext) {
        boolean result = false;
        Object temp = getConnection(theVdbContext);
        
        if (temp != null) {
            Assertion.isInstanceOf(temp, SessionTreeNode.class, temp.getClass().getName());
            SessionTreeNode connection = (SessionTreeNode)temp;
            
            if (!isConnectionClosed(connection)) {
                Date connTime = connection.getConnection().getTimeOpened();
                
                if (connTime != null) {
                    Assertion.isInstanceOf(theVdbContext, InternalVdbEditingContext.class, theVdbContext.getClass().getName());
                    long vdbTime = getVdbLastSavedTime((InternalVdbEditingContext)theVdbContext);
                    result = (connTime.getTime() < vdbTime);
                }
            }
        }
        
        return result;
    }

    /**
     * @see com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr#isVdbConnectionStale(com.metamatrix.vdb.edit.VdbContextEditor)
     * @since 5.0
     */
    public boolean isVdbConnectionStale(VdbContextEditor theVdbContext) {
        boolean result = false;
        Object temp = getConnection(theVdbContext);
        
        if (temp != null) {
            Assertion.isInstanceOf(temp, SessionTreeNode.class, temp.getClass().getName());
            SessionTreeNode connection = (SessionTreeNode)temp;
            
            if (!isConnectionClosed(connection)) {
                Date connTime = connection.getConnection().getTimeOpened();
                
                if (connTime != null) {
                    long vdbTime = getVdbLastSavedTime(theVdbContext);
                    result = (connTime.getTime() < vdbTime);
                }
            }
        }
        
        return result;
    }
    
    public boolean isExtensionModuleStale(Object theConnection) {
        boolean result = false;
        Object temp = null;
        if( ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
            VdbContextEditor context = getVdbContextEditor(theConnection);
            
            if (context != null) {
                temp = getConnection(context);
            }
        
        } else {
            VdbEditingContext context = getVdbEditingContext(theConnection);
            
            if (context != null) {
                temp = getConnection(context);
            }
            
        }
        if (temp != null) {
            Assertion.isInstanceOf(temp, SessionTreeNode.class, temp.getClass().getName());
            SessionTreeNode connection = (SessionTreeNode)temp;
            
            if (!isConnectionClosed(connection)) {
                Date connTime = connection.getConnection().getTimeOpened();
                
                if (connTime != null) {
                    IPath connPath = DqpPath.getRuntimeConnectorsPath();
                    String udfPath = UdfManager.INSTANCE.getUdfModelPath().toFile().getAbsolutePath();
                    result = hasStaleFile(new File(connPath.toOSString()), connTime.getTime())
                             || hasStaleFile(new File(udfPath), connTime.getTime());
                }
            }
        }

        return result;
    }
    
    private boolean hasStaleFile(File folder, long timeToCompare) {
    	if (folder.exists()) {
	        File[] files = folder.listFiles();
	        for(int i=0; i< files.length; i++) {
	            if(files[i].lastModified() > timeToCompare) {
	                return true;
	            }
	        }
    	}
        return false;
    }
}
