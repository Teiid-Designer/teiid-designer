/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import com.metamatrix.metamodels.core.Datatype;
import com.metamatrix.modeler.internal.ui.properties.sdt.SimpleDatatypePropertySource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;

/**
 * ModelObjectPropertySourceProvider creates the PropertySheetPage and is the 
 * PropertySourceProvder for metadata model objects.  This implementation is necessary
 * because EObject does not extend IAdaptable and therefore cannot be registered with
 * the IAdapterManager.
 */
public class ModelObjectPropertySourceProvider implements IPropertySourceProvider {

    private static ModelObjectPropertySheetPage propertySheetPage;
    
    /**
     * Construct an instance of ModelObjectPropertySourceProvider.
     */
    public ModelObjectPropertySourceProvider() {
    }

    /**
     * Provide the PropertySource for Model Objects, or any other Adaptable object
     * @see org.eclipse.ui.views.properties.IPropertySourceProvider#getPropertySource(java.lang.Object)
     * @since 4.0
     */
    public IPropertySource getPropertySource(Object object) {
        if ( object == null ) {
            return null;
        }
        
        if ( object instanceof IItemPropertySource ) {
            return new PropertySource(object, (IItemPropertySource)object);
        } else if ( object instanceof EObject ) {
            // make sure the ModelObjectPropertySheetPage has been initialized
            if ( propertySheetPage == null ) {
                getPropertySheetPage();
            }
            
            // see if this object is a simple datatype
            if ( object instanceof XSDSimpleTypeDefinition && !(object instanceof Datatype) ) {
                // make sure it is a global type
                if ( ((EObject)object).eContainer() instanceof XSDSchema ) {
                    IPropertySource emfSource = ModelObjectUtilities.getEmfPropertySourceProvider().getPropertySource(object);
                    return new SimpleDatatypePropertySource((XSDSimpleTypeDefinition) object, emfSource); 
                }
            }
            
            // use EMF's property source provider
            return ModelObjectUtilities.getEmfPropertySourceProvider().getPropertySource(object);
        } else if ( object instanceof IFile && ModelUtilities.isModelFile((IFile) object)) {
            return new ModelPropertySource((IFile) object);
        } else if( object instanceof IExtendedModelObject ) {
            return ((IExtendedModelObject)object).getPropertySource();
        } else {
            // look it up in the Platform's AdapterManager
            IAdapterManager manager = Platform.getAdapterManager();
            return (IPropertySource) manager.getAdapter(object, IPropertySource.class);
        }
    }

    /**
     * Obtain a PropertySheetPage for displaying Model Objects.
     * @since 4.0
     */
    public PropertySheetPage getPropertySheetPage() {
        if (propertySheetPage == null) {
            propertySheetPage = new ModelObjectPropertySheetPage();
            propertySheetPage.setPropertySourceProvider(this);
        }

        return propertySheetPage;
    }

    /**
     * @since 4.0
     */
    public void dispose() {
        // We don't want to dispose of the page directly. We only want to contribute ONE page.
        // This page can be created by the PropertySheet.doCreatePage() method getAdapter(). This will initialize the page.
        //propertySheetPage.dispose();
    }

}
