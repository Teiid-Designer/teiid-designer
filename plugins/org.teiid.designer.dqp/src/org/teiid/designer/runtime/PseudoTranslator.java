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
    private String type;

    /**
     * Used when constructing a new connection factory.
     * 
     * @param name the initial name (never <code>null</code>)
     * @param type the initial type (never <code>null</code>)
     */
    public PseudoTranslator( String name,
                                    String type, ExecutionAdmin admin) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(type, "type"); //$NON-NLS-1$
        this.name = name;
        this.initialProperties = new Properties();
    }
    
    /**
     * Use when editing an existing translator.
     * 
     * @param translator the existing translator whose initialProperties will be copied
     */
    public PseudoTranslator( TeiidTranslator translator ) {
        CoreArgCheck.isNotNull(translator, "translator"); //$NON-NLS-1$
        this.name = translator.getName();
        this.initialProperties = (Properties)translator.getProperties().clone();

        // make sure all properties are present
        Properties defaults = translator.getDefaultPropertyValues();

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
     * @param name the new name of the translator (can be <code>null</code>)
     */
    public void setName( String name ) {
        this.name = name;
    }

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
    /**
     * @param type the type of the translator (can be <code>null</code>)
     */
    public void setType( String type ) {
        this.type = type;
    }
}
