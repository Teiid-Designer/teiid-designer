package org.teiid.designer.runtime;

public interface TeiidServerRegistryConstants {
    /**
     * The attribute used to persist a server's custom label. May not exist if server is not using a custom label.
     */
    String CUSTOM_LABEL_ATTR = "customLabel"; //$NON-NLS-1$
    
    /**
     * The attribute indicating if the server is currently the preview server.
     */
    String DEFAULT_ATTR = "default"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's login password.
     */
    String PASSWORD_ATTR = "password"; //$NON-NLS-1$

    /**
     * The file name used when persisting the server registry.
     */
    String REGISTRY_FILE = "serverRegistry.xml"; //$NON-NLS-1$

    /**
     * The tag used when persisting a server.
     */
    String SERVER_TAG = "server"; //$NON-NLS-1$

    /**
     * The server collection tag used when persisting the server registry.
     */
    String SERVERS_TAG = "servers"; //$NON-NLS-1$
    
    /**
     * The tag used when persisting admin connection info.
     */
    String ADMIN_TAG = "admin"; //$NON-NLS-1$
    
    /**
     * The tag used when persisting jdbc connection info.
     */
    String JDBC_TAG = "jdbc"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's login user.
     */
    String USER_ATTR = "user"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's host value.
     */
    String HOST_ATTR = "host"; //$NON-NLS-1$

    String PARENT_SERVER_ID = "parentServerId"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's version value.
     */
    String SERVER_VERSION = "version"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's port value.
     */
    String PORT_ATTR = "port"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's secure value.
     */
    String SECURE_ATTR = "secure"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's jdbc host value.
     */
    String JDBC_HOST_ATTR = "jdbchost"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's jdbc port value.
     */
    String JDBC_PORT_ATTR = "jdbcport"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's login user.
     */
    String JDBC_USER_ATTR = "jdbcuser"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's login password.
     */
    String JDBC_PASSWORD_ATTR = "jdbcpassword"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's secure value.
     */
    String JDBC_SECURE_ATTR = "jdbcsecure"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's jdbc port override value.
     */
    String JDBC_PORT_OVERRIDE_ATTR = "jdbcportoverride"; //$NON-NLS-1$
}
