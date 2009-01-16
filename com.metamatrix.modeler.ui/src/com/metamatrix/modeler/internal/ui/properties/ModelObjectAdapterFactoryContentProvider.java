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

package com.metamatrix.modeler.internal.ui.properties;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * ModelObjectAdapterFactoryContentProvider is a specialization of {@link AdapterFactoryContentProvider}
 * whose sole purpose is to provide a {@link ModelObjectPropertySource} wrapper around EMF's
 * PropertySource.  The wrapper also is responsible for hooking up the IExtensionPropertiesController
 * to the ModelObjectPropertySource so that metamodel extension properties can be displayed.
 */
public class ModelObjectAdapterFactoryContentProvider extends AdapterFactoryContentProvider {

	/**
	 * Construct an instance of ModelObjectAdapterFactoryContentProvider.
	 * @param adapterFactory the AdapterFactory for providing the superclass default behavior. 
	 */
    public ModelObjectAdapterFactoryContentProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}
	
	/**
	 * Overridden createPropertySource() method.  Return a {@link ModelObjectPropertySource}.
	 */
	@Override
    protected IPropertySource createPropertySource(Object object, 
            IItemPropertySource itemPropertySource) {
                
        ModelObjectPropertySource source = new ModelObjectPropertySource(object, itemPropertySource);
        return source;  
  	}

    /* (non-Javadoc)
     * Overridden so that EMF does not hear notifications
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    @Override
    public void notifyChanged(Notification notification) {
    }

}
