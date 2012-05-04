/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.connection;

import java.util.Properties;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * Provides Teiid Data Source specific capabilites for a Data Source Type.
 */
public interface IConnectionInfoProvider extends IConnectionInfoHelper {
    /**
     * Extracts the information needed for a Teiid runtime connection from the <code>IConnectionProfile</code> and inserts it into
     * the <code>ModelResource</code>
     * 
     * @param modelResource
     * @param connectionProfile
     */
    void setConnectionInfo( ModelResource modelResource,
                            IConnectionProfile connectionProfile ) throws ModelWorkspaceException;

    /**
     * Accessor for the Teiid Data Source password property key. Null is there is no password for this DataSource Type.
     * 
     * @return
     */
    public abstract String getPasswordPropertyKey();
    
    /**
     * Accessor for the Teiid Data Source password property key. Null is there is no password for this DataSource Type.
     * 
     * @return
     */
    public abstract String getDataSourcePasswordPropertyKey();
    
    public abstract boolean requiresPassword(IConnectionProfile connectionProfile);

    /**
     * Accessor for the Teiid Data Source Type.
     * 
     * @return the Teiid Data Source Type
     */
    String getDataSourceType();

    /**
     * Extracts the Teiid-related properties required for Data Source creation from Connection Profile
     * 
     * @param connectionProfile
     * @return the list of properties
     */
    Properties getTeiidRelatedProperties( IConnectionProfile connectionProfile );
}
