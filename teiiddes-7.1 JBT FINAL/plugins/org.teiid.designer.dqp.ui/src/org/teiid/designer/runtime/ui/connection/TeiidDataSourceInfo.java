/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.connection;

import java.util.Properties;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.ExecutionAdmin;

/**
 * 
 */
public class TeiidDataSourceInfo {

    private String displayName;
    private String jndiName;
    private Properties properties;
    private IConnectionInfoProvider connectionInfoProvider;
    private ExecutionAdmin admin;

    public TeiidDataSourceInfo() {

    }

    /**
     * @param displayName
     * @param jndiName
     * @param properties
     * @param connectionInfoProvider
     * @param admin
     */
    public TeiidDataSourceInfo( String displayName,
                                String jndiName,
                                Properties properties,
                                IConnectionInfoProvider connectionInfoProvider,
                                ExecutionAdmin admin ) {
        super();
        this.displayName = displayName;
        this.jndiName = jndiName;
        this.properties = properties;
        this.connectionInfoProvider = connectionInfoProvider;
        this.admin = admin;
    }

    /**
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName Sets displayName to the specified value.
     */
    public void setDisplayName( String displayName ) {
        this.displayName = displayName;
    }

    /**
     * @return jndiName
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * @param jndiName Sets jndiName to the specified value.
     */
    public void setJndiName( String jndiName ) {
        this.jndiName = jndiName;
    }

    /**
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * @param properties Sets properties to the specified value.
     */
    public void setProperties( Properties properties ) {
        this.properties = properties;
    }

    /**
     * @return connectionInfoProvider
     */
    public IConnectionInfoProvider getConnectionInfoProvider() {
        return connectionInfoProvider;
    }

    /**
     * @param connectionInfoProvider Sets connectionInfoProvider to the specified value.
     */
    public void setConnectionInfoProvider( IConnectionInfoProvider connectionInfoProvider ) {
        this.connectionInfoProvider = connectionInfoProvider;
    }

    /**
     * @return admin
     */
    public ExecutionAdmin getAdmin() {
        return admin;
    }

    /**
     * @param admin Sets admin to the specified value.
     */
    public void setAdmin( ExecutionAdmin admin ) {
        this.admin = admin;
    }

}
