/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.properties.udp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor.PropertyValueWrapper;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;

public class ExtensionPropertySource implements IPropertySource {

    public static final AdapterFactoryContentProvider extensionPropertySourceProvider = ModelUtilities.getEmfAdapterFactoryContentProvider();

    EObject object;
    private EObject extension;
    private boolean isPrimaryMetamodelObject;
    private IItemPropertySource itemSource;
    private IPropertySource modelAnnotationExtensionSource;

    /**
     * Construct an instance of ExtensionPropertySource.
     */
    public ExtensionPropertySource( EObject object ) {
        this.object = object;
        isPrimaryMetamodelObject = ModelObjectUtilities.isPrimaryMetamodelObject(object);
        if (isPrimaryMetamodelObject) {
            try {
                this.extension = ModelerCore.getModelEditor().getExtension(object);
            } catch (ModelerCoreException e) {
                UiConstants.Util.log(e);
            }
        } else if (object instanceof ModelAnnotation) {
            this.extension = object;
        }

        if (this.extension != null) {
            this.itemSource = (IItemPropertySource)extensionPropertySourceProvider.getAdapterFactory().adapt(this.extension,
                                                                                                             IItemPropertySource.class);
        }
    }

    public boolean isExtensionProperty( Object id ) {
        return id instanceof ExtensionPropertyDescriptor;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return object;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        IPropertyDescriptor[] result = new IPropertyDescriptor[0];
        if (extension != null) {
            // Validate extension
            final List features = extension.eClass().getEStructuralFeatures();
            if (features.isEmpty()) {
                return result;
            }
            for (final Iterator iter = features.iterator(); iter.hasNext();) {
                final EStructuralFeature feature = (EStructuralFeature)iter.next();
                if (feature.getEType() == null) {
                    return result;
                }
            }

            // get the property descriptors off the extension object
            IPropertySource propSource = ExtensionPropertyDescriptor.extensionPropertySourceProvider.getPropertySource(extension);
            IPropertyDescriptor[] propArray = propSource.getPropertyDescriptors();

            // every extension property must get wrapped so we can find it again for get/setValue calls
            result = new IPropertyDescriptor[propArray.length];
            for (int i = 0; i < result.length; ++i) {
                result[i] = new ExtensionPropertyDescriptor(extension, object, propArray[i]);
            }

            // special case: if the extension is a ModelAnnotation, then we must check for extensions to ModelAnnotation
            if (extension instanceof ModelAnnotation) {
                try {
                    EObject modelExtension = ModelerCore.getModelEditor().getExtension(extension);
                    if (modelExtension != null) {
                        // get a property source
                        modelAnnotationExtensionSource = ExtensionPropertyDescriptor.extensionPropertySourceProvider.getPropertySource(modelExtension);
                        // get the descriptors from it
                        IPropertyDescriptor[] extensions = modelAnnotationExtensionSource.getPropertyDescriptors();
                        if (extensions != null && extensions.length > 0) {

                            // wrap all the extended properties in ExtensionPropertyDescriptors
                            IPropertyDescriptor[] myExtensions = new IPropertyDescriptor[extensions.length];
                            for (int i = 0; i < extensions.length; ++i) {
                                myExtensions[i] = new ModelAnnotationExtensionDescriptor(modelExtension, extensions[i]);
                            }

                            // make a new IPropertyDescriptor array to return
                            List resultSoFar = new ArrayList(Arrays.asList(result));
                            resultSoFar.addAll(Arrays.asList(myExtensions));
                            result = new IPropertyDescriptor[resultSoFar.size()];
                            for (int i = 0; i < resultSoFar.size(); ++i) {
                                result[i] = (IPropertyDescriptor)resultSoFar.get(i);
                            }
                        }
                    }
                } catch (ModelerCoreException e) {
                    UiConstants.Util.log(e);
                }
            }
        }

        return result;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue( Object id ) {
        if (id instanceof ModelAnnotationExtensionDescriptor) {
            // the user edited an Extension property
            final ExtensionPropertyDescriptor extensionDescriptor = (ExtensionPropertyDescriptor)id;
            return modelAnnotationExtensionSource.getPropertyValue(extensionDescriptor.getDelegateId());
        }
        if (id instanceof ExtensionPropertyDescriptor && itemSource != null) {
            // the user edited an Extension property
            final ExtensionPropertyDescriptor extensionDescriptor = (ExtensionPropertyDescriptor)id;
            Object value = itemSource.getPropertyDescriptor(this.extension, extensionDescriptor.getDelegateId()).getPropertyValue(this.extension);
            if (value instanceof PropertyValueWrapper) {
                PropertyValueWrapper wrapper = (PropertyValueWrapper)value;
                Object wrappedValue = wrapper.getEditableValue(wrapper);
                if (wrappedValue instanceof ModelType) {
                    value = ((ModelType)wrappedValue).getDisplayName();
                }
            }
            return value;
        }
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet( Object id ) {
        return getPropertyValue(id) != null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue( Object id ) {
        setPropertyValue(id, null);
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
     */
    public void setPropertyValue( Object id,
                                  Object value ) {
        if (id instanceof ModelAnnotationExtensionDescriptor) {
            // the user edited an Extension property
            final ModelAnnotationExtensionDescriptor extensionDescriptor = (ModelAnnotationExtensionDescriptor)id;
            boolean started = ModelerCore.startTxn(ModelObjectPropertySource.SET + extensionDescriptor.getDisplayName(), this);
            boolean succeeded = false;
            try {
                modelAnnotationExtensionSource.setPropertyValue(extensionDescriptor.getDelegateId(), value);
                succeeded = true;
            } finally {
                if (started) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        } else if (id instanceof ExtensionPropertyDescriptor) {
            // the user edited an Extension property
            boolean doIt = false;
            Object currentValue = getPropertyValue(id);

            if (currentValue instanceof ItemPropertyDescriptor.PropertyValueWrapper) {
                currentValue = ((ItemPropertyDescriptor.PropertyValueWrapper)currentValue).getEditableValue(id);
            }

            if (currentValue == null) {
                doIt = (value != null);
            } else if (value == null) {
                doIt = true;
            } else {
                doIt = !currentValue.equals(value);
            }

            if (doIt) {
                ExtensionPropertyDescriptor extensionDescriptor = (ExtensionPropertyDescriptor)id;
                IItemPropertyDescriptor propDescriptor = itemSource.getPropertyDescriptor(this.extension,
                                                                                          extensionDescriptor.getDelegateId());

                boolean started = ModelerCore.startTxn(ModelObjectPropertySource.SET + extensionDescriptor.getDisplayName(), this);
                boolean succeeded = false;
                try {
                    EStructuralFeature sf = (EStructuralFeature)propDescriptor.getFeature(this.extension);
                    Object newValue = value;
                    final EClassifier eClass = sf.getEType();
                    if (eClass instanceof EDataType) {
                        final EDataType dt = (EDataType)eClass;
                        final EPackage ePackage = dt.getEPackage();
                        final EFactory fac = ePackage.getEFactoryInstance();
                        if (value instanceof String) {
                            newValue = fac.createFromString(dt, (String)value);
                        }
                    }
                    propDescriptor.setPropertyValue(this.extension, newValue);
                    succeeded = true;
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }
        }
    }

    /**
     * Determine if the supplied data value can be converted to the appropriate type for the ExtensionDescriptor.
     * 
     * @param extensionDescriptor the supplied extension descriptor
     * @param value the supplied value
     * @return 'true' if the value can be converted successfully, 'false' if not.
     */
    public boolean canConvertToCorrectDatatype( ExtensionPropertyDescriptor extensionDescriptor,
                                                Object value ) {
        boolean canConvert = false;
        IItemPropertyDescriptor propDescriptor = itemSource.getPropertyDescriptor(this.extension,
                                                                                  extensionDescriptor.getDelegateId());

        try {
            EStructuralFeature sf = (EStructuralFeature)propDescriptor.getFeature(this.extension);
            final EDataType dt = (EDataType)sf.getEType();
            final EPackage ePackage = dt.getEPackage();
            final EFactory fac = ePackage.getEFactoryInstance();
            Object newValue = value;
            if (value instanceof String) {
                newValue = fac.createFromString(dt, (String)value);
            }
            if (newValue != null) {
                canConvert = true;
            }
        } catch (Exception e) {
            canConvert = false;
        }

        return canConvert;
    }

    public class ModelAnnotationExtensionDescriptor extends ExtensionPropertyDescriptor {
        public ModelAnnotationExtensionDescriptor( EObject extensionObject,
                                                   IPropertyDescriptor delegate ) {
            super(extensionObject, object, delegate);
        }
    }
}
