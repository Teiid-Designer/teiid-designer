/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
