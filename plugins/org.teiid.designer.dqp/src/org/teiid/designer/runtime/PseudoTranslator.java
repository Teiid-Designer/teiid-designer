/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import java.util.Properties;
import org.teiid.adminapi.Translator;
import com.metamatrix.core.util.CoreArgCheck;

/**
 * The <code>PseudoConnectionFactory</code> class is an implementation of {@link ConnectionFactory} that keeps track of the
 * initial property values and allows its name to be changed.
 */
public class PseudoTranslator implements Translator {

    private static final long serialVersionUID = 1L;

    private final Properties initialProperties; // the initial value of all properties
    private String name;

    /**
     * Used when constructing a new connection factory.
     * 
     * @param name the initial name (never <code>null</code>)
     * @param type the initial type (never <code>null</code>)
     */
    public PseudoTranslator( String name,
                                    ConnectorType type ) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(type, "type"); //$NON-NLS-1$
        this.name = name;
        this.initialProperties = type.getDefaultPropertyValues();
    }

    /**
     * Use when editing an existing connector.
     * 
     * @param connector the existing connector whose initialProperties will be copied
     */
    public PseudoTranslator( Connector connector ) {
        CoreArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
        this.name = connector.getName();
        this.initialProperties = (Properties)connector.getProperties().clone();

        // make sure all properties are present
        Properties defaults = connector.getType().getDefaultPropertyValues();

        for (String name : defaults.stringPropertyNames()) {
            if (!this.initialProperties.containsKey(name)) {
                this.initialProperties.setProperty(name, defaults.getProperty(name));
            }
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getName()
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getProperties()
     */
    @Override
    public Properties getProperties() {
        return this.initialProperties;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.adminapi.AdminObject#getPropertyValue(java.lang.String)
     */
    @Override
    public String getPropertyValue( String name ) {
        return this.initialProperties.getProperty(name);
    }

    /**
     * @param name the new name of the connection factory (can be <code>null</code>)
     */
    public void setName( String name ) {
        this.name = name;
    }

	@Override
	public String getExecutionFactoryClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxResultRows() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getTemplateName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isExceptionOnMaxRows() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isImmutable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isXaCapable() {
		// TODO Auto-generated method stub
		return false;
	}

}
