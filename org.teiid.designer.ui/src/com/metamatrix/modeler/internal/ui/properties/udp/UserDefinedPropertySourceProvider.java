/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties.udp;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;

/**
 * UserDefinedPropertySourceProvider
 */
public class UserDefinedPropertySourceProvider implements IPropertySourceProvider {

    /**
     * Construct an instance of UserDefinedPropertySourceProvider.
     * 
     */
    public UserDefinedPropertySourceProvider() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
     */
    public IPropertySource getPropertySource(Object object) {
        if ( object instanceof EObject ) {
            return new UserDefinedPropertySource((EObject) object);
        }
        return null;
    }

}
