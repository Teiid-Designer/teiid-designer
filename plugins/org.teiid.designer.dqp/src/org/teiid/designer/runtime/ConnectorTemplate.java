/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.util.Properties;

/**
 * The <code>ConnectorTemplate</code> class is an implementation of {@link Connector} that does not communicate with a Teiid
 * server. So it can be used when changes to a connector are not intended to be updated on the server as they occur.
 */
public class ConnectorTemplate extends Connector {

    private final Properties changedProperties;

    /**
     * @param name the name of the new connector (never <code>null</code>)
     * @param type the type of the new connector (never <code>null</code>)
     */
    public ConnectorTemplate( String name,
                              ConnectorType type ) {
        super(new PseudoConnectionFactory(name, type), type);
        this.changedProperties = new Properties();
    }

    /**
     * @param connector the connector whose properties are used to create this template (never <code>null</code>)
     */
    public ConnectorTemplate( Connector connector ) {
        super(new PseudoConnectionFactory(connector), connector.getType());
        this.changedProperties = new Properties();
    }

    /**
     * @return the properties that have changed values since being constructed
     */
    public Properties getChangedProperties() {
        return this.changedProperties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.Connector#getPropertyValue(java.lang.String)
     */
    @Override
    public String getPropertyValue( String name ) {
        if (this.changedProperties.containsKey(name)) {
            return this.changedProperties.getProperty(name);
        }

        return super.getPropertyValue(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.Connector#getProperties()
     */
    @Override
    public Properties getProperties() {
        Properties props = new Properties(super.getProperties());
        props.putAll(this.changedProperties);
        return props;
    }

    /**
     * Modifies the name of the connector.
     * 
     * @param name the new name (may be <code>null</code>)
     */
    public void setName( String name ) {
        ((PseudoConnectionFactory)getConnectionFactory()).setName(name);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.Connector#setProperties(java.util.Properties)
     * @throws UnsupportedOperationException if called
     */
    @Override
    public void setProperties( Properties changedProperties ) throws Exception {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.Connector#setPropertyValue(java.lang.String, java.lang.String)
     */
    @Override
    public void setPropertyValue( String name,
                                  String value ) throws Exception {
        if (isValidPropertyValue(name, value) == null) {
            this.changedProperties.setProperty(name, value);
        } else {
            throw new Exception(Util.getString("invalidPropertyValue", value, name)); //$NON-NLS-1$
        }
    }

}
