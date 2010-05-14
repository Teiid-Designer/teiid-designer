/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.ui.provider.PropertySource;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import com.metamatrix.metamodels.diagram.PresentationEntity;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.internal.ui.properties.udp.ExtensionPropertySource;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.properties.ITransientPropertyDescriptor;
import com.metamatrix.modeler.ui.viewsupport.StatusBarUpdater;

/**
 * ModelObjectPropertySource is a specialization of PropertySource.
 * The class creates {@link ModelObjectPropertyDescriptor} instances rather than 
 * EMF PropertyDescriptors.  It also provides Metamodel Extension properties
 * along with the core metamodel properties based on the {@link IExtensionPropertiesControl}.
 */
public class ModelObjectPropertySource extends PropertySource {

    public static final String SET = UiConstants.Util.getString("ModelObjectPropertySource.undoSetPrefix") + ' '; //$NON-NLS-1$
    public static final String RESET = UiConstants.Util.getString("ModelObjectPropertySource.undoResetPrefix") + ' '; //$NON-NLS-1$

    // ===================================
    // Instance variables
    private boolean isReadOnlyType = false;
    private boolean isPrimaryMetamodelObject = false;
    private ExtensionPropertySource extensionDelegate;

    /**
     * Collection of transient descriptors.
     * @since 4.3
     */
    private ITransientPropertyDescriptor[] transientDescriptors = new ITransientPropertyDescriptor[0];
    
    /**
     * Map with keys of property identifiers and values of transient descriptors.
     * @since 4.3
     */
    private Map idDescriptorMap;

    // ===================================
    // Constructors

	public ModelObjectPropertySource(Object object, IItemPropertySource itemPropertySource) {
		super(object, itemPropertySource);
        isReadOnlyType = (object instanceof PresentationEntity) 
                        || ( object instanceof Mapping )
                        || ( object instanceof MappingHelper )
                        || ( object instanceof SqlAlias );
                        
        if (object instanceof EObject ) {
            final EObject eObject = (EObject)object;
            final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(eObject,true);
            if ( dtMgr.isSimpleDatatype(eObject) ) {
                try {
                    isReadOnlyType = dtMgr.isBuiltInDatatype(eObject);
                } catch (Exception e) {
                    UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                    isReadOnlyType = false;
                } 
            }
        }
        extensionDelegate = new ExtensionPropertySource((EObject) object);
	}

    // ===================================
    // Methods

    public boolean canDisplayExtensionProperties() {
        return isPrimaryMetamodelObject;
    }

	/* (non-Javadoc)
	 * Overridden createPropertyDescriptor() method.  Return a {@link ModelObjectPropertyDescriptor}.
	 */
  	@Override
    protected IPropertyDescriptor createPropertyDescriptor(
  			IItemPropertyDescriptor itemPropertyDescriptor) {
    	return new ModelObjectPropertyDescriptor(object, itemPropertyDescriptor);
  	}

    /* (non-Javadoc)
     * Overridden to intercept propertyId of ExtensionPropertyDescriptor and set the value on the
     * extension object rather than the model object.
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue()
     */    
    @Override
    public void setPropertyValue(Object propertyId, Object value) {
        try {
            if ( this.object != null && this.object instanceof EObject ) {
                
                boolean started = ModelerCore.startTxn(SET + propertyId.toString(), this);
                boolean succeeded = false;
                try {
                    if ( extensionDelegate.isExtensionProperty(propertyId) ) {
                        extensionDelegate.setPropertyValue(propertyId, value);
                        succeeded = true;
                        return;
                    } else if ( propertyId instanceof String ) {
                        
                        //swj: workaround for non-standard handling of XSD enumeration values
                        final IItemPropertyDescriptor itemPropertyDescriptor = super.itemPropertySource.getPropertyDescriptor(this.object, propertyId);
                        final Object genericFeature = itemPropertyDescriptor.getFeature(object);
                        if (genericFeature instanceof EStructuralFeature) {
                            final EStructuralFeature feature = (EStructuralFeature)genericFeature;
                            final EClassifier eType = feature.getEType();
                            // if the feature is many and the type is EEnum, then the falue should be EList
                            if ( feature.isMany() && eType instanceof EEnum && ! ( value instanceof EList ) ) {
                                // non-standard value - set the value directly on the itemPropertyDescriptor
                                itemPropertyDescriptor.setPropertyValue(this.object, value);
                                succeeded = true;
                                return;
                            }
                        }
                        
                        // the user edited a real property
                        final IItemPropertyDescriptor descriptor = this.itemPropertySource.getPropertyDescriptor(this.object, propertyId);
                        if ( descriptor instanceof ItemPropertyDescriptor ) {
	                        boolean success = ModelerCore.getModelEditor().setPropertyValue((EObject)object, value, (ItemPropertyDescriptor) descriptor);
	                        if (success) {
	                            succeeded = true;
	                            return;
	                        }
                        } else if (genericFeature instanceof EStructuralFeature) {
	                        boolean success = ModelerCore.getModelEditor().setPropertyValue((EObject)object, value, genericFeature);
	                        if (success) {
	                            succeeded = true;
	                            return;
	                        } 
	                        descriptor.setPropertyValue(this.object, value);
	                        succeeded = true;
                        } else {
                            descriptor.setPropertyValue(this.object, value);
                            succeeded = true;
                        }
                    }
                } finally {
                    if ( started ) {
                        if ( succeeded ) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                
            }
        
        } catch (Exception e) {
            String objectString = object.toString();
            if ( object instanceof EObject ) {
                StatusBarUpdater.formatEObjectMessage((EObject) object);
            }
            String[] strings = new String[] { e.getClass().getName(), propertyId.toString(), value.toString(), objectString };
            String message =  UiConstants.Util.getString("ModelObjectPropertySource.setPropertyValueError", (Object[])strings); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }
    }
    
    private IPropertyDescriptor[] getAllDescriptors() {

        IPropertyDescriptor[] emfDescriptors = super.getPropertyDescriptors();
        IPropertyDescriptor[] extDescriptors = extensionDelegate.getPropertyDescriptors();
        this.transientDescriptors = getTransientPropertyDescriptors(this.object);
        
        // keep track of the property IDs contributed by the transient descriptors
        if (this.transientDescriptors.length > 0) {
            this.idDescriptorMap = new HashMap();
            
            for (int i = 0; i < this.transientDescriptors.length; ++i) {
                this.idDescriptorMap.put(this.transientDescriptors[i].getId(), this.transientDescriptors[i]);
            }
        }

        // combine all descriptors
        IPropertyDescriptor[] result = new IPropertyDescriptor[emfDescriptors.length + extDescriptors.length + transientDescriptors.length];
        int resultIndex = 0;

        // add EMF descriptors
        if (emfDescriptors.length != 0) {
            for (int i = 0; i < emfDescriptors.length; ++i, ++resultIndex) {
                result[resultIndex] = emfDescriptors[i];
            }
        }
        
        // add extension descriptors
        if (extDescriptors.length != 0) {
            for (int i = 0; i < extDescriptors.length; ++i, ++resultIndex) {
                result[resultIndex] = extDescriptors[i];
            }
        }
        
        // add transient descriptors
        if (transientDescriptors.length != 0) {
            for (int i = 0; i < transientDescriptors.length; ++i, ++resultIndex) {
                result[resultIndex] = transientDescriptors[i];
            }
        }

        return result;
    }
    
    /**
     * Obtains the <code>ITransientPropertyDescriptor</code>s for the specified object.
     * @param theObject the object whose transient property descriptors are being requested
     * @return the transient property descriptors (never <code>null</code>)
     * @since 4.3
     */
    private ITransientPropertyDescriptor[] getTransientPropertyDescriptors(Object theObject) {
        List temp = new ArrayList();
        
        // create transient descriptor for the Object URI property (would be nice if this was an extension pt)
        ITransientPropertyDescriptor id = new ObjectUriPropertyDescriptor();
        
        if (id.supports(theObject)) {
            id.setObject(theObject);
            temp.add(id);
        }
        
        // create transient descriptor for the Namespace property 
        id = new NamespacePropertyDescriptor();
        if (id.supports(theObject)) {
            id.setObject(theObject);
            temp.add(id);
        }
        
        // create transient descriptor for the Enumerated Values property 
        id = new EnumeratedValuesPropertyDescriptor();
        if (id.supports(theObject)) {
            id.setObject(theObject);
            temp.add(id);
        }

        // return appropriate descriptors
        ITransientPropertyDescriptor[] result;
        
        if (!temp.isEmpty()) {
            result = (ITransientPropertyDescriptor[])temp.toArray(new ITransientPropertyDescriptor[temp.size()]);
        } else {
            result = new ITransientPropertyDescriptor[0];
        }
        
        return result;
    }

    /* (non-Javadoc)
     * Overridden to add the metamodel extension properties.
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    @Override
    public IPropertyDescriptor[] getPropertyDescriptors() {
        
        // create an array of descriptors from the primary model object
        IPropertyDescriptor[] descriptors = getAllDescriptors();
        if ( isReadOnlyType ) {
            // wrap each descriptor in a ReadOnlyPropertyDescriptor
            IPropertyDescriptor[] result = new IPropertyDescriptor[descriptors.length];
            for ( int i=0 ; i<descriptors.length ; ++i ) {
                result[i] = new ReadOnlyPropertyDescriptor(descriptors[i], ReadOnlyPropertyDescriptor.READ_ONLY_PROPERTY);
            }
            descriptors = result;
        }
        
        return descriptors;
    }

    /* (non-Javadoc)
     * Overridden to intercept propertyId of ExtensionPropertyDescriptor and get the value from the
     * extension object rather than the model object.
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    @Override
    public Object getPropertyValue(Object propertyId) {
        if ( extensionDelegate.isExtensionProperty(propertyId) ) {
            return extensionDelegate.getPropertyValue(propertyId);
        }
        
        // check if transient descriptors can handle that property
        ITransientPropertyDescriptor descriptor = null;
        if (idDescriptorMap != null) {
            descriptor = (ITransientPropertyDescriptor) this.idDescriptorMap.get(propertyId);
        } // endif
        
        if (descriptor != null) {
            Object propValue = null;
            
            boolean requiredStart = ModelerCore.startTxn(false,false,"Get Property Value",this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                propValue = descriptor.getPropertyValue();
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if(requiredStart) {
                    if(succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
            return propValue;
        }
        
        Object result = null;
        
        // ensure that EMF can provide a property descriptor for this property id
        if ( itemPropertySource.getPropertyDescriptor(object, propertyId) != null ) {
            try {
                result = super.getPropertyValue(propertyId);
            } catch (Exception e) {
                String objectString = object.toString();
                if ( object instanceof EObject ) {
                    StatusBarUpdater.formatEObjectMessage((EObject) object);
                }
                String[] strings = new String[] { e.getClass().getName(), propertyId.toString(), objectString };
                String message =  UiConstants.Util.getString("ModelObjectPropertySource.getPropertyValueError", (Object[])strings); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    @Override
    public void resetPropertyValue(Object propertyId) {
        try {
            if ( this.object != null && this.object instanceof EObject ) {
                
                boolean started = ModelerCore.startTxn(SET + propertyId.toString(), this);
                boolean succeeded = false;
                try {
                    if ( extensionDelegate.isExtensionProperty(propertyId) ) {
                        extensionDelegate.resetPropertyValue(propertyId);
                        succeeded = true;
                        return;
                    } else if ( propertyId instanceof String ) {
                        // the user edited a real property
                        final ItemPropertyDescriptor descriptor = (ItemPropertyDescriptor) this.itemPropertySource.getPropertyDescriptor(this.object, propertyId);
                        descriptor.resetPropertyValue(object);
                        succeeded = true;
                        return;
                    }
                } finally {
                    if ( started ) {
                        if ( succeeded ) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
                
            }
        
        } catch (Exception e) {
            String objectString = object.toString();
            if ( object instanceof EObject ) {
                StatusBarUpdater.formatEObjectMessage((EObject) object);
            }
            String[] strings = new String[] { e.getClass().getName(), propertyId.toString(), objectString };
            String message =  UiConstants.Util.getString("ModelObjectPropertySource.resetPropertyValueError", (Object[])strings); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, e, message);
        }
    }

}
