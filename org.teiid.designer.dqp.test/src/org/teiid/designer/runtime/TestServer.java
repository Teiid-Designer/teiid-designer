/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class TestServer extends Server {

    private Map<String, String> connectorNameConnectorTypeNameMap;
    private Map<String, String> modelNameConectorNameMap;

    public TestServer( String url,
                       String user,
                       String password,
                       boolean persistPassword,
                       EventManager eventManager,
                       Map<String, String> connectorNameConnectorTypeNameMap,
                       Map<String, String> modelNameConectorNameMap ) {
        super(url, user, password, persistPassword, eventManager);
        this.connectorNameConnectorTypeNameMap = connectorNameConnectorTypeNameMap;
        this.modelNameConectorNameMap = modelNameConectorNameMap;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.Server#getAdmin()
     */
    @Override
    public ExecutionAdmin getAdmin() throws Exception {
        if (this.admin == null) {
            this.admin = new TestExecutionAdmin(this, this.eventManager,
                                                new HashMap<String, String>(this.connectorNameConnectorTypeNameMap),
                                                new HashMap<String, String>(this.modelNameConectorNameMap));
            this.connectorNameConnectorTypeNameMap = null;
            this.modelNameConectorNameMap = null;
        }

        return this.admin;
    }

}
