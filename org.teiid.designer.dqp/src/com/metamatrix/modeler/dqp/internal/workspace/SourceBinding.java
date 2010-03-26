/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * The binding between a model and one or more connectors.
 */
public class SourceBinding {

    private final String name;
    private final String path;
    private final Set<Connector> connectors = new HashSet<Connector>();

    /**
     * @param name the model name (never <code>null</code> or empty)
     * @param path the parent path of the model (never <code>null</code> or empty)
     * @param connector the connector added to the binding (never <code>null</code>)
     */
    public SourceBinding( String name,
                          String path,
                          Connector connector ) {
        this(name, path, Collections.singleton(connector));
        ArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
    }

    /**
     * @param name the model name (never <code>null</code> or empty)
     * @param path the parent path of the model (never <code>null</code> or empty)
     * @param connectors the set of connectors used in the model binding (never <code>null</code> or empty)
     * @throws IllegalArgumentException if connectors are from different servers
     */
    public SourceBinding( String name,
                          String path,
                          Set<Connector> connectors ) {
        ArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(path, "path"); //$NON-NLS-1$
        ArgCheck.isNotEmpty(connectors, "connectors"); //$NON-NLS-1$

        this.name = name;
        this.path = path;

        ExecutionAdmin admin = null;

        // make sure all connectors from same server
        for (Connector connector : connectors) {
            if (connector == null) {
                throw new IllegalArgumentException(Util.getString("connectorCannotBeNullForSourceBinding")); //$NON-NLS-1$
            }
            if (admin == null) {
                admin = connector.getType().getAdmin();
            } else if (admin != connector.getType().getAdmin()) {
                throw new IllegalArgumentException(Util.getString("sourceBindingWithConnectorsFromDifferentServers")); //$NON-NLS-1$
            }

            this.connectors.add(connector);
        }
    }

    /**
     * @param connector the connector being added to the model binding (never <code>null</code>)
     * @return <code>true</code> if the connector was added
     * @throws IllegalArgumentException if connector being added is from a different server
     */
    public boolean addConnector( Connector connector ) {
        ArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$

        // make sure server is the same
        if (this.connectors.iterator().next().getType().getAdmin() != connector.getType().getAdmin()) {
            throw new IllegalArgumentException(Util.getString("sourceBindingWithConnectorsFromDifferentServers")); //$NON-NLS-1$
        }

        return this.connectors.add(connector);
    }

    /**
     * @return an unmodifiable collection of connectors (never <code>null</code> or empty)
     * @since 7.0
     */
    public Set<Connector> getConnectors() {
        return Collections.unmodifiableSet(this.connectors);
    }

    /**
     * @return the model name (never <code>null</code> or empty)
     * @since 7.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the parent path of the model (never <code>null</code> or empty)
     * @since 7.0
     */
    public String getContainerPath() {
        return this.path;
    }

    /**
     * @param connector the connector being removed from the model binding (never <code>null</code>)
     * @throws IllegalArgumentException if trying to remove the last connector in the model binding or if removing a connector
     *         that is not part of this model binding
     */
    public void removeConnector( Connector connector ) {
        ArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$

        // don't allow last connector to be removed
        if ((this.connectors.size() == 1) && this.connectors.contains(connector)) {
            throw new IllegalArgumentException(Util.getString("lastConnectorOfSourceBindingCannotBeRemoved")); //$NON-NLS-1$
        }

        // error if connector not found
        if (!this.connectors.remove(connector)) {
            throw new IllegalArgumentException(Util.getString("connectorToRemoveNotFoundInSourceBinding", connector, this)); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("model: " + this.getName());//$NON-NLS-1$
        sb.append(", path: " + this.getContainerPath());//$NON-NLS-1$
        sb.append(", connectors: ");//$NON-NLS-1$

        int count = this.connectors.size();
        int i = 1;

        for (Connector connector : this.connectors) {
            sb.append(connector.getName());

            if (i < count) {
                sb.append(", "); //$NON-NLS-1$
            }

            ++i;
        }

        return sb.toString();
    }

}
