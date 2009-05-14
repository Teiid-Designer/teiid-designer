/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.config;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConfigurationObjectEditor;


/** 
 * @since 4.2
 */
public class ComponentTypePropertySourceProvider implements IPropertySourceProvider {

    private ConfigurationObjectEditor editor;
    
    /** 
     * 
     * @since 4.2
     */
    public ComponentTypePropertySourceProvider(ConfigurationObjectEditor editor) {
        this.editor = editor;
    }
    
    public IPropertySource getPropertySource(Object object) {
        if ( object instanceof ComponentType ) {
            return new ComponentTypePropertySource((ComponentType) object, editor);
        }
        return null;
    }

}
