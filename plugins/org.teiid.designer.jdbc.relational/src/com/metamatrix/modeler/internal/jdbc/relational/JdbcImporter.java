/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.relational;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcManager;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.relational.JdbcRelationalPlugin;
import com.metamatrix.modeler.jdbc.relational.RelationalModelProcessor;

/**
 * <p>
 * </p>
 * 
 * @since 4.0
 */
public final class JdbcImporter implements
                               ModelerJdbcRelationalConstants {

    // ============================================================================================================================
    // Variables

    private ModelResource updatedModel;
    private JdbcSource src;
    private JdbcDatabase db;

    // ============================================================================================================================
    // Property Methods

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public JdbcDatabase getDatabase() {
        return this.db;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public JdbcSource getSource() {
        return this.src;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public ModelResource getUpdatedModel() {
        return this.updatedModel;
    }

    /**
     * <p>
     * </p>
     * 
     * @param database
     *            may be null.
     * @since 4.0
     */
    public void setDatabase(final JdbcDatabase database) {
        this.db = database;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public void setSource(final JdbcSource source) {
        CoreArgCheck.isNotNull(source);
        this.src = source;
    }

    /**
     * @since 4.0
     */
    public void setUpdatedModel(final ModelResource model) throws ModelWorkspaceException {
        this.updatedModel = null;
        if (model != null) {
            for (final Iterator modelIter = model.getAllRootEObjects().iterator(); modelIter.hasNext();) {
                final Object obj = modelIter.next();
                if (obj instanceof JdbcSource) {
                    this.updatedModel = model;
                    try {
                        this.src = (JdbcSource)ModelerCore.getModelEditor().copy((JdbcSource)obj);
                        setUpdatedModelSettings();
                        break;
                    } catch (final Exception err) {
                        throw new ModelWorkspaceException(err);
                    }
                }
            }
        }
    }

    /**
     * @throws JdbcException 
     * @since 5.0
     */
    public void setUpdatedModelSettings() throws JdbcException {
        if (this.db == null) {
            return;
        }
        final JdbcImportSettings settings = this.src.getImportSettings();
        if (settings != null) {
            for (final Iterator objIter = settings.getExcludedObjectPaths().iterator(); objIter.hasNext();) {
                final IPath path = new Path((String)objIter.next());
                final JdbcNode node = findNode(path, this.db);
                if (node != null) {
                    node.setSelected(false);
                }
            }
            final Includes includes = this.db.getIncludes();
            final List types = settings.getIncludedTableTypes();
            includes.setIncludedTableTypes((String[])types.toArray(new String[types.size()]));
            includes.setApproximateIndexes(settings.isIncludeApproximateIndexes());
            includes.setIncludeForeignKeys(settings.isIncludeForeignKeys());
            includes.setIncludeIndexes(settings.isIncludeIndexes());
            includes.setIncludeProcedures(settings.isIncludeProcedures());
            includes.setUniqueIndexesOnly(settings.isIncludeUniqueIndexes());
        }
    }

    // ============================================================================================================================
    // Utility Methods

    /**
     * Connect to the {@link #getSource() source}.
     * 
     * @param password
     *            may be null.
     * @param monitor
     *            the monitor, and may be used to cancel the connection attempt; may be null
     * @since 4.0
     */
    public void connect(final String password,
                        final IProgressMonitor monitor) throws CoreException,
                                                       IOException,
                                                       SQLException {
        // Disconnect any existing connection ...
        disconnect();

        // Create the SQL connection ...
        final JdbcManager mgr = JdbcRelationalUtil.getJdbcManager();
        final Connection connection = mgr.createConnection(this.src, password, monitor);

        // Create the JdbcDatabase instance with this connection ...
        this.db = JdbcPlugin.getJdbcDatabase(this.src, connection);
    }

    /**
     * Connect to the {@link #getSource() source}.
     * 
     * @param password
     *            may be null.
     * @since 4.0
     */
    public void connect(final String password) throws CoreException,
                                              IOException,
                                              SQLException {
        connect(password, null);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public void disconnect() throws SQLException {
        if (this.db != null) {
            final Connection connection = this.db.getConnection();
            if (connection != null) {
                connection.close();
            }
        }
    }

    /**
     * @since 4.2
     */
    public JdbcNode findNode(final IPath path,
                              final JdbcNode parent) throws JdbcException {
        final String seg = path.segment(0);
        final JdbcNode[] children = parent.getChildren();
        for (int ndx = children.length; --ndx >= 0;) {
            final JdbcNode child = children[ndx];
            if (seg.equalsIgnoreCase(child.getName())) {
                if (path.segmentCount() > 1) {
                    return findNode(path.removeFirstSegments(1), child);
                }
                return child;
            }
        }
        return null;
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public IStatus importModel() throws ModelWorkspaceException {
        return importModel(null);
    }

    /**
     * <p>
     * </p>
     * 
     * @since 4.0
     */
    public IStatus importModel(final IProgressMonitor monitor) throws ModelWorkspaceException {
        final RelationalModelProcessor processor = JdbcRelationalPlugin.createRelationalModelProcessor(this.src);

        // apply the import settings to the model ...

        final IStatus status = processor.execute(this.updatedModel, this.db, this.src.getImportSettings(), monitor);
        return status;
    }
}
