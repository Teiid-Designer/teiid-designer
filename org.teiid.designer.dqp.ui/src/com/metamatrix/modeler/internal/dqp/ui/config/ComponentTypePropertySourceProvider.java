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
import org.teiid.designer.runtime.ConnectorType;

/**
 * @since 4.2
 */
public class ComponentTypePropertySourceProvider implements IPropertySourceProvider {

    public IPropertySource getPropertySource( Object object ) {
        if (object instanceof ConnectorType) {
            return new ComponentTypePropertySource((ConnectorType)object);
        }
        return null;
    }

}
