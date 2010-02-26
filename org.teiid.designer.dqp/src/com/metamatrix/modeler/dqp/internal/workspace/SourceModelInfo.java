/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.Server;

/**
 * This class is simpler than BasicModelInfo and is targeted for visibility and use outside of a VDB.
 * 
 * @since 5.0
 */
public class SourceModelInfo implements Serializable {

    private static final long serialVersionUID = -5880868141249826062L;

    private String name;
    private String uuid;
    private String containerPath;

    private final Map<Server, Set<Connector>> connectorByServerMap = new HashMap<Server, Set<Connector>>();

    public SourceModelInfo( String modelName ) {
        this.name = modelName;
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Set<Connector> getConnectors() {
        Set<Connector> allConnectors = new HashSet<Connector>();

        for (Server server : this.connectorByServerMap.keySet()) {
            allConnectors.addAll(this.connectorByServerMap.get(server));
        }

        return allConnectors;
    }

    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @param uuid
     */
    public void setUuid( String uuid ) {
        this.uuid = uuid;
    }

    public void addConnector( Connector connector ) {
        Server server = connector.getType().getAdmin().getServer();

        if (!this.connectorByServerMap.containsKey(server)) {
            this.connectorByServerMap.put(server, new HashSet<Connector>());
        }

        this.connectorByServerMap.get(server).add(connector);
    }

    public void removeConnector( Connector connector ) {
        Server server = connector.getType().getAdmin().getServer();
        Set<Connector> connectors = this.connectorByServerMap.get(server);

        if (connectors != null) {
            connectors.remove(connector);

            if (connectors.isEmpty()) {
                this.connectorByServerMap.remove(server);
            }
        }
    }

    public void setContainerPath( String path ) {
        this.containerPath = path;
    }

    public String getContainerPath() {
        return this.containerPath;
    }

    public String getPath() {
        return this.containerPath;
    }

    @Override
    public String toString() {
        StringBuffer sw = new StringBuffer();

        sw.append("SourceModelInfo: " + this.getName());//$NON-NLS-1$
        sw.append("\n\tFolder: " + this.getContainerPath());//$NON-NLS-1$

        return sw.toString();

    }

}
