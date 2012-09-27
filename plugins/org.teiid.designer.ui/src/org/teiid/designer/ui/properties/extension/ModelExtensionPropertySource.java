/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.properties.extension;

import static org.teiid.designer.ui.UiConstants.Util;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.ModelExtensionAssistantAggregator;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;


/**
 * 
 *
 * @since 8.0
 */
public class ModelExtensionPropertySource implements IPropertySource {

    // TODO see ModelRowElement uses ExtensionPropertySource should it also use this?

    private final EObject eObject;

    /**
     * @param eObject the object whose property source is being created (cannot be <code>null</code>)
     */
    public ModelExtensionPropertySource( EObject eObject ) {
        CoreArgCheck.isNotNull(eObject, "eObject is null"); //$NON-NLS-1$
        this.eObject = eObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    @Override
    public Object getEditableValue() {
        return this.eObject;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        String metaclassName = this.eObject.getClass().getName();
        Collection<ModelExtensionPropertyDefinition> propDefns = new ArrayList<ModelExtensionPropertyDefinition>();

        try {
            ModelExtensionAssistantAggregator aggregator = ExtensionPlugin.getInstance().getModelExtensionAssistantAggregator();
            propDefns.addAll(aggregator.getPropertyDefinitions(this.eObject));
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.errorCreatingPropertyDescriptors, metaclassName));
        }

        // no extended properties found
        if (propDefns.isEmpty()) {
            return new IPropertyDescriptor[0];
        }

        IPropertyDescriptor[] descriptors = new IPropertyDescriptor[propDefns.size()];
        int i = 0;

        // create descriptors for each property
        for (ModelExtensionPropertyDefinition propDefn : propDefns) {
            descriptors[i++] = new ModelExtensionPropertyDescriptor(this.eObject, propDefn);
        }

        return descriptors;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue( Object id ) {
        if (isExtensionProperty(id)) {
            return ((ModelExtensionPropertyDescriptor)id).getPropertyValue();
        }

        Util.log(IStatus.ERROR, NLS.bind(Messages.unexpectedPropertySourceId, id));
        return null;
    }

    /**
     * @param object the object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the parameter is a <code>ModelExtensionPropertyDescriptor</code>
     */
    public boolean isExtensionProperty( Object object ) {
        CoreArgCheck.isNotNull(object, "object is null"); //$NON-NLS-1$
        return (object instanceof ModelExtensionPropertyDescriptor);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    @Override
    public boolean isPropertySet( Object id ) {
        return (getPropertyValue(id) != null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    @Override
    public void resetPropertyValue( Object id ) {
        setPropertyValue(id, null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    @Override
    public void setPropertyValue( Object id,
                                  Object value ) {
        if (isExtensionProperty(id)) {
            ((ModelExtensionPropertyDescriptor)id).setPropertyValue(value);
        } else {
            Util.log(IStatus.ERROR, NLS.bind(Messages.unexpectedPropertySourceId, id.getClass().getName()));
        }
    }

}
