/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.metamodels.transformation.util.TransformationUtil;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.transformation.InputBinding} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class InputBindingItemProvider
    extends ItemProviderAdapter
    implements
        IEditingDomainItemProvider,
        IStructuredItemContentProvider,
        ITreeItemContentProvider,
        IItemLabelProvider,
        IItemPropertySource {
            
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InputBindingItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public List getPropertyDescriptors(Object object) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addInputParameterPropertyDescriptor(object);
            addMappingClassColumnPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Input Parameter feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addInputParameterPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_InputBinding_inputParameter_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_InputBinding_inputParameter_feature", "_UI_InputBinding_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 TransformationPackage.eINSTANCE.getInputBinding_InputParameter(),
                 true,
                 null,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Mapping Class Column feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addMappingClassColumnPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_InputBinding_mappingClassColumn_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_InputBinding_mappingClassColumn_feature", "_UI_InputBinding_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 TransformationPackage.eINSTANCE.getInputBinding_MappingClassColumn(),
                 true,
                 null,
                 null,
                 null));
    }

    /**
     * This returns InputBinding.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/InputBinding"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getText(Object object) {
        final InputBinding binding = (InputBinding)object;
        String inputStr = null;
        String valueStr = null;
        if ( binding.getInputParameter() != null && binding.getInputParameter().getName() != null && 
             binding.getInputParameter().getName().trim().length() != 0 ) {
            inputStr = binding.getInputParameter().getName();
        }
        if ( binding.getMappingClassColumn() != null && binding.getMappingClassColumn().getName() != null && 
             binding.getMappingClassColumn().getName().trim().length() != 0 ) {
            valueStr = binding.getMappingClassColumn().getName();
        }
        if ( valueStr == null && inputStr == null ) {
            return getString("_UI_InputBinding_type"); //$NON-NLS-1$
        }
        if ( inputStr == null ) {
            inputStr = getString("_UI_InputBinding_unknown_parameter"); //$NON-NLS-1$
        }
        if ( valueStr == null ) {
            valueStr = getString("_UI_InputBinding_unknown_expression"); //$NON-NLS-1$
        }
        if ( binding.getMappingClassColumn() != null && binding.getMappingClassColumn().getType() != null ) {
            valueStr = valueStr + ":" + TransformationUtil.getName(binding.getMappingClassColumn().getType()); //$NON-NLS-1$
        }
        return inputStr + " = " + valueStr; //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTextGen(Object object) {
        return getString("_UI_InputBinding_type"); //$NON-NLS-1$
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached
     * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void notifyChanged(Notification notification) {
        updateChildren(notification);
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s
     * describing all of the children that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors(Collection newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return TransformationEditPlugin.INSTANCE;
    }

}
