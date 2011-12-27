/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;

/**
 * The RelationalModelProcessor is used to create or update a model so that it reflects
 * a {@link JdbcDatabase JDBC data source}.
 */
public interface RelationalModelProcessor {

    /**
     * Change the supplied relational model to reflect the supplied JDBC database.
     * This method should be used only when used <i>outside</i> of the Eclipse workspace
     * environment.
     * @param modelResource the model that is to be changed; this can be a new resource
     * that is empty, or it may be an existing model that is to be altered to reflect
     * the JDBC source; may not be null
     * @param jdbcDatabase the JDBC database node; may not be null
     * @param settings the import settings; may not be null
     * @param monitor the progress monitor; may be null
     * @return the status containing the result of the import process
     */
    IStatus execute( Resource modelResource, JdbcDatabase jdbcDatabase,
                     JdbcImportSettings settings, IProgressMonitor monitor );

    /**
     * Change the supplied relational model to reflect the supplied JDBC database.
     * This method should be used only when used <i>outside</i> of the Eclipse workspace
     * environment.
     * @param modelResource the model that is to be changed; this can be a new resource
     * that is empty, or it may be an existing model that is to be altered to reflect
     * the JDBC source; may not be null
     * @param container The model's container.
     * @param jdbcDatabase the JDBC database node; may not be null
     * @param settings the import settings; may not be null
     * @param monitor the progress monitor; may be null
     * @return the status containing the result of the import process
     */
    IStatus execute( Resource modelResource, Container container, JdbcDatabase jdbcDatabase,
                     JdbcImportSettings settings, IProgressMonitor monitor );

    /**
     * Change the supplied relational model to reflect the supplied JDBC database.
     * This method should be used when used <i>within</i> the Eclipse workspace environment.
     * @param modelResource the model that is to be changed; this can be a new resource
     * that is empty, or it may be an existing model that is to be altered to reflect
     * the JDBC source; may not be null
     * @param jdbcDatabase the JDBC database node; may not be null
     * @param settings the import settings; may not be null
     * @param monitor the progress monitor; may be null
     * @return the status containing the result of the import process
     * @throws ModelWorkspaceException if there is a problem accessing the contents of the model resource
     */
    IStatus execute( ModelResource modelResource, JdbcDatabase jdbcDatabase,
                     JdbcImportSettings settings, IProgressMonitor monitor ) throws ModelWorkspaceException;
    
    /**
     * Sets whether objects added to a model are copied or moved from the {@link JdbcDatabase JDBC data source model}.
     * 
     * @param moveRatherThanCopyAdds
     * @since 4.3
     */
    void setMoveRatherThanCopyAdds(boolean moveRatherThanCopyAdds);
    
    /**
     * Sets a boolean parameter that can be used by the processor to log performance timing values when debugging 
     * @param logTiming
     * @since 4.3
     */
    void setDebugLogTiming(boolean logTiming);
    
    /**
     *  Gets a boolean parameter that can be used by the processor to log performance timing values when debugging 
     * 
     * @since 4.3
     */
    boolean getDebugLogTiming();

    /**
     * Set whether to include incomplete foreign keys
     * 
     * @param includeIncompleteFKs
     */
    void setIncludeIncompleteFKs( boolean includeIncompleteFKs );

    /**
     * Gets the boolean flag for inclusion of incomplete FKs
     * 
     * @return 'true' if including incomplete FKs, 'false' if not.
     */
    boolean getIncludeIncompleteFKs();

}
