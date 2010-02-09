/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.extension.provider;

import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.provider.EClassItemProvider;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.util.CoreUtil;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.core.extension.XClass} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class XClassItemProvider extends EClassItemProvider {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public XClassItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List getPropertyDescriptorsGen( Object object ) { // NO_UCD
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addExtendedClassPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addExtendedClassPropertyDescriptor(object);

            CoreUtil.removePropertyDescriptor(this.itemPropertyDescriptors, new EStructuralFeature[] {
                EcorePackage.eINSTANCE.getEClass_Interface(), EcorePackage.eINSTANCE.getEClass_Abstract(),
                EcorePackage.eINSTANCE.getEClass_Interface(), EcorePackage.eINSTANCE.getEClass_ESuperTypes(),
                EcorePackage.eINSTANCE.getEClassifier_DefaultValue(), EcorePackage.eINSTANCE.getEClassifier_InstanceClass(),
                EcorePackage.eINSTANCE.getEClassifier_InstanceClassName()});
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Extended Class feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addExtendedClassPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_XClass_extendedClass_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_XClass_extendedClass_feature", "_UI_XClass_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 ExtensionPackage.Literals.XCLASS__EXTENDED_CLASS,
                                                                 true,
                                                                 false,
                                                                 false,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns XClass.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return overlayImage(object, getResourceLocator().getImage("full/obj16/XClass")); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        final XClass xclass = (XClass)object;
        String label = xclass.getName();
        if (label == null || label.length() == 0) {
            label = getString("_UI_XClass_type"); //$NON-NLS-1$
        }
        final EClass extendedEClass = xclass.getExtendedClass();
        if (extendedEClass != null) {
            final EPackage epkg = extendedEClass.getEPackage();
            label = label + " -> " + (epkg != null ? epkg.getName() : "") + "::" + extendedEClass.getName(); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
        return label;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) { // NO_UCD
        String label = ((XClass)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_XClass_type") : //$NON-NLS-1$
        getString("_UI_XClass_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating a viewer
     * notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void notifyChanged( Notification notification ) {
        updateChildren(notification);
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that
     * can be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    protected void collectNewChildDescriptors( Collection newChildDescriptors,
                                               Object object ) {
        // super.collectNewChildDescriptors(newChildDescriptors, object);

        // defect 18246 - when adding EAttributes or EReferences to a EClass, you must use
        // the EStructuralFeatures list
        newChildDescriptors.add(createChildParameter(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES,
                                                     ExtensionFactory.eINSTANCE.createXAttribute()));
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created under this
     * object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void collectNewChildDescriptorsGen( Collection newChildDescriptors, // NO_UCD
                                                  Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add(createChildParameter(EcorePackage.Literals.ECLASS__ESTRUCTURAL_FEATURES,
                                                     ExtensionFactory.eINSTANCE.createXAttribute()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return ExtensionEditPlugin.INSTANCE;
    }

}
