/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.JdbcImportSettings;
import org.teiid.designer.jdbc.JdbcManager;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.metadata.Includes;
import org.teiid.designer.jdbc.metadata.JdbcDatabase;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.relational.util.JdbcModelProcessorManager;
import org.teiid.designer.jdbc.relational.util.JdbcRelationalUtil;


/**
 * <p>
 * </p>
 * 
 * @since 8.0
 */
public final class JdbcImporter implements ModelerJdbcRelationalConstants {

    private ModelResource updatedModel;
    private JdbcSource src;
    private JdbcSource updateSrc;
    private JdbcDatabase db;
    private boolean isVdbSourceModel;
    private String vdbSourceModelName;
    private boolean reachedObjectsPage;
	private String schemaFilter;
	private String tableFilter;
	private String storedProcFilter;
	
    /**
     * The unique jbossJndiName
     * 
     */
	private String jbossJndiName;
	
	private boolean autoCreateDataSource = true;

	/**
     * @since 4.0
     */
    public JdbcDatabase getDatabase() {
        return this.db;
    }

    /**
     * @since 4.0
     */
    public JdbcSource getSource() {
        return this.src;
    }

    /**
     * @since 4.0
     */
    public ModelResource getUpdatedModel() {
        return this.updatedModel;
    }

    /**
     * @param database may be null.
     * @since 4.0
     */
    public void setDatabase( final JdbcDatabase database ) {
        this.db = database;
        setReachedObjectsPage(false);
    }

    /**
     * @since 4.0
     */
    public void setSource( final JdbcSource source ) {
        CoreArgCheck.isNotNull(source);
        this.src = source;
    }

    /**
     * @since 4.0
     */
    public void setUpdatedModel( final ModelResource model ) throws ModelWorkspaceException {
        this.updatedModel = null;
        this.updateSrc = null;
        // Non-null model supplied. Transfer the import settings
        if (model != null) {
            for (final Iterator modelIter = model.getAllRootEObjects().iterator(); modelIter.hasNext();) {
                final Object obj = modelIter.next();
                if (obj instanceof JdbcSource) {
                    this.updatedModel = model;
                    try {
                        this.updateSrc = (JdbcSource)ModelerCore.getModelEditor().copy((JdbcSource)obj);
                        setUpdatedModelSettings();
                        break;
                    } catch (final Exception err) {
                        throw new ModelWorkspaceException(err);
                    }
                }
            }
            // null model supplied. Reset import settings back to original
        } else {
            try {
                setUpdatedModelSettings();
            } catch (final Exception err) {
                throw new ModelWorkspaceException(err);
            }
        }
        if( this.jbossJndiName == null ) {
        	String modelName = this.updatedModel.getItemName();
        	this.jbossJndiName = modelName + "_DS";
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
        JdbcImportSettings settings = null;
        if (this.updateSrc != null) {
            settings = this.updateSrc.getImportSettings();
        } else if (this.src != null) {
            settings = this.src.getImportSettings();
        }

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

    /**
     * Connect to the {@link #getSource() source}.
     * 
     * @param password may be null.
     * @param monitor the monitor, and may be used to cancel the connection attempt; may be null
     * @since 4.0
     */
    public void connect( final String password,
                         final IProgressMonitor monitor ) throws CoreException, SQLException {
        // Disconnect any existing connection ...
        disconnect();

        // Create the SQL connection ...
        final JdbcManager mgr = JdbcRelationalUtil.getJdbcManager();
        JdbcSource jdbcSrc = (this.updateSrc != null) ? this.updateSrc : this.src;
        final Connection connection = mgr.createConnection(jdbcSrc, password, monitor);

        // Create the JdbcDatabase instance with this connection ...
        this.db = JdbcPlugin.getJdbcDatabase(jdbcSrc, connection);
    }

    /**
     * Connect to the {@link #getSource() source}.
     * 
     * @param password may be null.
     * @since 4.0
     */
    public void connect( final String password ) throws CoreException, SQLException {
        connect(password, null);
    }

    /**
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
    public JdbcNode findNode( final IPath path,
                              final JdbcNode parent ) throws JdbcException {
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
     * @since 4.0
     */
    public IStatus importModel() throws ModelWorkspaceException {
        return importModel(null);
    }

    /**
     * @since 4.0
     */
    public IStatus importModel( final IProgressMonitor monitor ) throws ModelWorkspaceException {
        final RelationalModelProcessor processor = JdbcModelProcessorManager.createRelationalModelProcessor();

        // apply the import settings to the model ...

        final IStatus status = processor.execute(this.updatedModel, this.db, this.updateSrc.getImportSettings(), monitor);
        return status;
    }
    
    /**
	 * @return the isVdbSourceModel
	 */
	public boolean isVdbSourceModel() {
		return this.isVdbSourceModel;
	}

	/**
	 * @param isVdbSourceModel the isVdbSourceModel to set
	 */
	public void setIsVdbSourceModel(boolean isVdbSourceModel) {
		this.isVdbSourceModel = isVdbSourceModel;
	}
	
	/**
	 * @return the vdb source model name. may be null
	 */
	public String getVdbSourceModelName() {
		return vdbSourceModelName;
	}
	
	/**
	 * @param name the vdb source model name

	 */
	public void setVdbSourceModelName(String name) {
		this.vdbSourceModelName = name;
	}

	/**
	 * @return the reachedObjectsPage
	 */
	public boolean isReachedObjectsPage() {
		return this.reachedObjectsPage;
	}

	/**
	 * @param reachedObjectsPage the reachedObjectsPage to set
	 */
	public void setReachedObjectsPage(boolean reachedObjectsPage) {
		this.reachedObjectsPage = reachedObjectsPage;
	}
	
	/**
	 * @param schemaFilter the schema filter text from the CP
	 */
    public void setSchemaFilter(String schemaFilter) {
    	this.schemaFilter = schemaFilter;
    }

	/**
	 * @param tableFilter the table filter text from the CP
	 */
    public void setTableFilter(String tableFilter) {
    	this.tableFilter = tableFilter;
    }
    
	/**
	 * @param storedProcFilter the stored proc filter text from the CP
	 */
    public void setStoredProcFilter(String storedProcFilter) {
    	this.storedProcFilter = storedProcFilter;
    }

	/**
	 * Get the current schema filter text
	 * @return filter text
	 */
    public String getSchemaFilter( ) {
    	return this.schemaFilter;
    }

	/**
	 * Get the current table filter text
	 * @return filter text
	 */
    public String getTableFilter( ) {
    	return this.tableFilter;
    }
    
	/**
	 * Get the current stored proc filter text
	 * @return filter text
	 */
    public String getStoredProcFilter( ) {
    	return this.storedProcFilter;
    }
    
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public String getJBossJndiName() {
        return this.jbossJndiName;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setJBossJndiNameName(String jndiName) {
		this.jbossJndiName = jndiName;
	}
	
	/**
	 * 
	 * @return sourceModelName the source relational model name
	 */
	public boolean doCreateDataSource() {
        return this.autoCreateDataSource;
	}
	
	/**
	 * 
	 * @param sourceModelName (never <code>null</code> or empty).
	 */
	public void setCreateDataSource(boolean value) {
		this.autoCreateDataSource = value;
	}
	
	
}
