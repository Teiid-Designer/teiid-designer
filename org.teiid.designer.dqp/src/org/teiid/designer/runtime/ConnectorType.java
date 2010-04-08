/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.util.Collection;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.core.util.CoreArgCheck;

/**
 *
 */
public class ConnectorType implements Comparable<ConnectorType> {

    private final Collection<PropertyDefinition> propDefs;
    private final String name;
    private final ExecutionAdmin admin;

    /**
     * @param name the type name (never <code>null</code>)
     * @param propDefs the type property definitions (never <code>null</code>)
     * @param admin the execution admin (never <code>null</code>)
     */
    public ConnectorType( String name,
                          Collection<PropertyDefinition> propDefs,
                          ExecutionAdmin admin ) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(propDefs, "propDefs"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$

        this.name = name;
        this.propDefs = propDefs;
        this.admin = admin;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo( ConnectorType type ) {
        CoreArgCheck.isNotNull(type, "type"); //$NON-NLS-1$
        return getName().compareTo(type.getName());
    }

    /**
     * @return the execution admin (never <code>null</code>)
     */
    public ExecutionAdmin getAdmin() {
        return this.admin;
    }

    /**
     * @return the type name (never <code>null</code>)
     * @since 7.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the property definitions (never <code>null</code>);
     * @since 7.0
     */
    public Collection<PropertyDefinition> getPropertyDefinitions() {
        return this.propDefs;
    }

    public PropertyDefinition getPropertyDefinition( String name ) {
        for (PropertyDefinition propDef : this.propDefs) {
            if (propDef.getName().equals(name)) return propDef;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Util.getString("connectorTypeDetailedName", getName(), getAdmin().getServer()); //$NON-NLS-1$
    }

}
