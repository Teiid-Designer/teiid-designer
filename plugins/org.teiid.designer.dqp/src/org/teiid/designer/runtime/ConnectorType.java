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
import java.util.Collections;
import java.util.Properties;
import net.jcip.annotations.Immutable;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;

/**
 *
 */
@Immutable
public final class ConnectorType implements Comparable<ConnectorType> {

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
        this.propDefs = Collections.unmodifiableCollection(propDefs);
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

        Server thisServer = getAdmin().getServer();
        Server thatServer = type.getAdmin().getServer();

        int c = thisServer.getUrl().compareTo(thatServer.getUrl());

        if (c == 0) {
            c = getName().compareTo(type.getName());
        }

        return c;
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
     * @return the string version of the default value for each property (empty string if no default)
     */
    public Properties getDefaultPropertyValues() {
        Properties defaultValues = new Properties();

        for (PropertyDefinition propDef : getPropertyDefinitions()) {
            String value = (propDef.getDefaultValue() == null) ? StringUtilities.EMPTY_STRING
                                                              : propDef.getDefaultValue().toString();
            defaultValues.setProperty(propDef.getName(), value);
        }

        return defaultValues;
    }

    /**
     * @return an immutable collection of property definitions (never <code>null</code>);
     * @since 7.0
     */
    public Collection<PropertyDefinition> getPropertyDefinitions() {
        return this.propDefs;
    }

    /**
     * @param name the name of the <code>PropertyDefinition</code> being requested (never <code>null</code> or empty)
     * @return the property definition or <code>null</code> if not found
     */
    public PropertyDefinition getPropertyDefinition( String name ) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$

        for (PropertyDefinition propDef : getPropertyDefinitions()) {
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
