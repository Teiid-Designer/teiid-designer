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

package com.metamatrix.metamodels.relational.provider;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relational.ProcedureParameter} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class ProcedureParameterItemProvider extends RelationalEntityItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ProcedureParameterItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addDirectionPropertyDescriptor(object);
            addDefaultValuePropertyDescriptor(object);
            addNativeTypePropertyDescriptor(object);
            addLengthPropertyDescriptor(object);
            addPrecisionPropertyDescriptor(object);
            addScalePropertyDescriptor(object);
            addNullablePropertyDescriptor(object);
            addRadixPropertyDescriptor(object);
            addTypePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Direction feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addDirectionPropertyDescriptor( Object object ) {
        ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                       ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                       getResourceLocator(),
                                                                       getString("_UI_ProcedureParameter_direction_feature"), //$NON-NLS-1$
                                                                       getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_direction_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                       RelationalPackage.eINSTANCE.getProcedureParameter_Direction(),
                                                                       true,
                                                                       ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                       // Start customized code
                                                                       getString("_UI_ProcedureParameter_direction_feature_category"), //$NON-NLS-1$
                                                                       // End customized code
                                                                       null) {
            @Override
            public Collection getChoiceOfValues( Object object ) {
                if (isVirtual(object)) {
                    Collection choiceValues = new HashSet();
                    choiceValues.add(DirectionKind.IN_LITERAL);
                    return choiceValues;
                }
                return super.getChoiceOfValues(object);
            }
        };

        itemPropertyDescriptors.add(descriptor);
    }

    /**
     * Return whether the resource containing this object represents a virtual model. If we cannot determine if the resource is a
     * virtual model then false is returned
     * 
     * @generated NOT
     */
    protected boolean isVirtual( Object object ) {
        if (object instanceof ProcedureParameter) {
            try {
                ModelAnnotation ma = ModelerCore.getModelEditor().getModelAnnotation((EObject)object);
                return (ma != null && ma.getModelType().getValue() == ModelType.VIRTUAL);
            } catch (Exception e) {
                RelationalPlugin.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }
        return false;
    }

    /**
     * This adds a property descriptor for the Direction feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDirectionPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_direction_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_direction_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Direction(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Default Value feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addDefaultValuePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_defaultValue_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_defaultValue_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_DefaultValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_defaultValue_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Default Value feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDefaultValuePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_defaultValue_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_defaultValue_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_DefaultValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Native Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addNativeTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_nativeType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_nativeType_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_NativeType(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_nativeType_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Native Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNativeTypePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_nativeType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_nativeType_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_NativeType(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Length feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addLengthPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_length_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_length_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Length(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_length_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Length feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addLengthPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_length_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_length_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Length(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Precision feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addPrecisionPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_precision_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_precision_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Precision(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_precision_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Precision feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addPrecisionPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_precision_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_precision_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Precision(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Scale feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addScalePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_scale_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_scale_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Scale(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_scale_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Scale feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addScalePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_scale_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_scale_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Scale(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Nullable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addNullablePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_nullable_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_nullable_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Nullable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_nullable_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Nullable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNullablePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_nullable_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_nullable_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Nullable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Radix feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addRadixPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_radix_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_radix_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Radix(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_radix_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Radix feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addRadixPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_radix_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_radix_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Radix(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_type_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_type_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Type(),
                                                                 true,
                                                                 null,
                                                                 // Start customized code
                                                                 getString("_UI_ProcedureParameter_type_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addTypePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ProcedureParameter_type_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ProcedureParameter_type_feature", "_UI_ProcedureParameter_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getProcedureParameter_Type(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns ProcedureParameter.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public Object getImage( Object object ) {
        // Start customized code
        final ProcedureParameter param = (ProcedureParameter)object;
        final DirectionKind direction = param.getDirection();
        if (direction != null) {
            if (direction == DirectionKind.IN_LITERAL) {
                return getResourceLocator().getImage("full/obj16/Parameter_in"); //$NON-NLS-1$
            }
            if (direction == DirectionKind.OUT_LITERAL) {
                return getResourceLocator().getImage("full/obj16/Parameter_out"); //$NON-NLS-1$
            }
            if (direction == DirectionKind.INOUT_LITERAL) {
                return getResourceLocator().getImage("full/obj16/Parameter_inout"); //$NON-NLS-1$
            }
            if (direction == DirectionKind.RETURN_LITERAL) {
                return getResourceLocator().getImage("full/obj16/Parameter_return"); //$NON-NLS-1$
            }
            if (direction == DirectionKind.UNKNOWN_LITERAL) {
                return getResourceLocator().getImage("full/obj16/Parameter_unknown"); //$NON-NLS-1$
            }
        }
        // End customized code
        return getResourceLocator().getImage("full/obj16/ProcedureParameter"); //$NON-NLS-1$
    }

    /**
     * This returns ProcedureParameter.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Object getImageGen( Object object ) {
        return getResourceLocator().getImage("full/obj16/ProcedureParameter"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        // Start customized code
        final ProcedureParameter param = (ProcedureParameter)object;
        String label = param.getName();
        if (label == null || label.length() == 0) {
            label = getString("_UI_ProcedureParameter_type"); //$NON-NLS-1$
        }
        // Add the datatype ...
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(param, true);
        final EObject dt = param.getType();
        final String dtName = dtMgr.getName(dt);
        if (dt != null && dtName != null && dtName.trim().length() != 0) {
            label = label + " : " + dtName; //$NON-NLS-1$
        }
        final int length = param.getLength();
        if (dt != null && length != 0 && dtMgr.isCharacter(dt)) {
            label = label + "(" + length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return label;
        // End customized code
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getTextGen( Object object ) {
        String label = ((ProcedureParameter)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_ProcedureParameter_type") : //$NON-NLS-1$
        getString("_UI_ProcedureParameter_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(ProcedureParameter.class)) {
            case RelationalPackage.PROCEDURE_PARAMETER__DIRECTION:
            case RelationalPackage.PROCEDURE_PARAMETER__DEFAULT_VALUE:
            case RelationalPackage.PROCEDURE_PARAMETER__NATIVE_TYPE:
            case RelationalPackage.PROCEDURE_PARAMETER__LENGTH:
            case RelationalPackage.PROCEDURE_PARAMETER__PRECISION:
            case RelationalPackage.PROCEDURE_PARAMETER__SCALE:
            case RelationalPackage.PROCEDURE_PARAMETER__NULLABLE:
            case RelationalPackage.PROCEDURE_PARAMETER__RADIX:
            case RelationalPackage.PROCEDURE_PARAMETER__TYPE:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s describing all of the children that
     * can be created under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors( Collection newChildDescriptors,
                                               Object object ) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return RelationalEditPlugin.INSTANCE;
    }

}
