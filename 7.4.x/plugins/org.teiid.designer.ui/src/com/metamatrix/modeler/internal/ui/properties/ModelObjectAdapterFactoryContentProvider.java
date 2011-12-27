/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
