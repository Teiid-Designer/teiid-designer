/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import java.io.File;
import java.util.Properties;

/**
 * The <code>ModelExtensionAssistantAdapter</code> class is a complete <code>ModelExtensionAssistant</code> implementation. All
 * abstract methods are implemented to have no effect or return <code>null</code>.
 */
public class ModelExtensionAssistantAdapter extends ModelExtensionAssistant {

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getOverriddenValue(java.lang.Object, java.lang.String)
     */
    @Override
    public String getOverriddenValue( Object modelObject,
                                      String propId ) throws Exception {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getModelExtensionDefinition(java.lang.Object)
     */
    @Override
    public ModelExtensionDefinition getModelExtensionDefinition( Object modelObject ) throws Exception {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getOverriddenValues(java.lang.Object)
     */
    @Override
    public Properties getOverriddenValues( Object modelObject ) throws Exception {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getPropertyValue(java.lang.Object, java.lang.String)
     */
    @Override
    public String getPropertyValue( Object modelObject,
                                    String propId ) throws Exception {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getPropertyValues(java.lang.Object)
     */
    @Override
    public Properties getPropertyValues( Object modelObject ) throws Exception {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#hasExtensionProperties(java.io.File)
     */
    @Override
    public boolean hasExtensionProperties( File file ) throws Exception {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#hasExtensionProperties(java.lang.Object)
     */
    @Override
    public boolean hasExtensionProperties( Object modelObject ) throws Exception {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#isModelExtensionDefinitionRelated(java.lang.Object)
     */
    @Override
    public boolean isModelExtensionDefinitionRelated( Object modelObject ) throws Exception {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeModelExtensionDefinition(java.lang.Object)
     */
    @Override
    public void removeModelExtensionDefinition( Object modelObject ) throws Exception {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#removeProperty(java.lang.Object, java.lang.String)
     */
    @Override
    public void removeProperty( Object modelObject,
                                String propId ) throws Exception {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#saveModelExtensionDefinition(java.lang.Object)
     */
    @Override
    public void saveModelExtensionDefinition( Object modelObject ) throws Exception {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#setPropertyValue(java.lang.Object, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setPropertyValue( Object modelObject,
                                  String propId,
                                  String newValue ) throws Exception {
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#supportsMyNamespace(java.lang.Object)
     */
    @Override
    public boolean supportsMyNamespace( Object modelObject ) throws Exception {
        return false;
    }

}
