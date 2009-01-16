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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeManager;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.relational.Column} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class ColumnItemProvider extends RelationalEntityItemProvider {
    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ColumnItemProvider( AdapterFactory adapterFactory ) {
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

            addNativeTypePropertyDescriptor(object);
            addLengthPropertyDescriptor(object);
            addFixedLengthPropertyDescriptor(object);
            addPrecisionPropertyDescriptor(object);
            addScalePropertyDescriptor(object);
            addNullablePropertyDescriptor(object);
            addAutoIncrementedPropertyDescriptor(object);
            addDefaultValuePropertyDescriptor(object);
            addMinimumValuePropertyDescriptor(object);
            addMaximumValuePropertyDescriptor(object);
            addFormatPropertyDescriptor(object);
            addCharacterSetNamePropertyDescriptor(object);
            addCollationNamePropertyDescriptor(object);
            addSelectablePropertyDescriptor(object);
            addUpdateablePropertyDescriptor(object);
            addCaseSensitivePropertyDescriptor(object);
            addSearchabilityPropertyDescriptor(object);
            addCurrencyPropertyDescriptor(object);
            addRadixPropertyDescriptor(object);
            addSignedPropertyDescriptor(object);
            addDistinctValueCountPropertyDescriptor(object);
            addNullValueCountPropertyDescriptor(object);
            addUniqueKeysPropertyDescriptor(object);
            addIndexesPropertyDescriptor(object);
            addForeignKeysPropertyDescriptor(object);
            addAccessPatternsPropertyDescriptor(object);
            addTypePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Native Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addNativeTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_nativeType_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_NativeType"), //$NON-NLS-1$ 
                                                                 RelationalPackage.eINSTANCE.getColumn_NativeType(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_nativeType_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_nativeType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_nativeType_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_NativeType(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
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
                                                                 getString("_UI_Column_type_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Type"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Type(),
                                                                 true,
                                                                 null,
                                                                 getString("_UI_Column_type_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_type_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_type_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Type(),
                                                                 true,
                                                                 null,
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
                                                                 getString("_UI_Column_nullable_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Nullable"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Nullable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_nullable_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_nullable_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_nullable_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Nullable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Auto Incremented feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addAutoIncrementedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_autoIncremented_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_AutoIncremented"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_AutoIncremented(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_Column_autoIncremented_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Auto Incremented feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addAutoIncrementedPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_autoIncremented_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_autoIncremented_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_AutoIncremented(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
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
                                                                 getString("_UI_Column_defaultValue_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_DefaultValue"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_DefaultValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_defaultValue_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_defaultValue_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_defaultValue_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_DefaultValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Minimum Value feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addMinimumValuePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_minimumValue_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_MinimumValue"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_MinimumValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_minimumValue_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Minimum Value feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addMinimumValuePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_minimumValue_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_minimumValue_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_MinimumValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Maximum Value feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addMaximumValuePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_maximumValue_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_MaximumValue"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_MaximumValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_maximumValue_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Maximum Value feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addMaximumValuePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_maximumValue_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_maximumValue_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_MaximumValue(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Format feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addFormatPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_format_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Format"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Format(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_format_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Format feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addFormatPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_format_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_format_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Format(),
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
                                                                 getString("_UI_Column_length_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Length"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Length(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 getString("_UI_Column_length_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_length_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_length_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Length(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Fixed Length feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addFixedLengthPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_fixedLength_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_FixedLength"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_FixedLength(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_Column_fixedLength_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Fixed Length feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addFixedLengthPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_fixedLength_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_fixedLength_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_FixedLength(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
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
                                                                 getString("_UI_Column_scale_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Scale"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Scale(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 getString("_UI_Column_scale_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_scale_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_scale_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Scale(),
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
                                                                 getString("_UI_Column_precision_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Precision"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Precision(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 getString("_UI_Column_precision_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_precision_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_precision_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Precision(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Character Set Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addCharacterSetNamePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_characterSetName_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_CharacterSetName"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_CharacterSetName(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_characterSetName_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Character Set Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addCharacterSetNamePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_characterSetName_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_characterSetName_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_CharacterSetName(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Collation Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addCollationNamePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_collationName_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_CollationName"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_CollationName(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_collationName_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Collation Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addCollationNamePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_collationName_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_collationName_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_CollationName(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Selectable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSelectablePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_selectable_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Selectable"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Selectable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_Column_selectable_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Selectable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSelectablePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_selectable_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_selectable_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Selectable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Updateable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addUpdateablePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_updateable_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Updateable"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Updateable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_Column_updateable_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Updateable feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUpdateablePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_updateable_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_updateable_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Updateable(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Case Sensitive feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addCaseSensitivePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_caseSensitive_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_CaseSensitive"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_CaseSensitive(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_Column_caseSensitive_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Case Sensitive feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addCaseSensitivePropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_caseSensitive_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_caseSensitive_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_CaseSensitive(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Searchability feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSearchabilityPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_searchability_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Searchability"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Searchability(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_Column_searchability_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Searchability feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSearchabilityPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_searchability_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_searchability_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Searchability(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Currency feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addCurrencyPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_currency_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Currency"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Currency(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_Column_currency_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Currency feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addCurrencyPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_currency_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_currency_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Currency(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
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
                                                                 getString("_UI_Column_radix_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Radix"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Radix(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 getString("_UI_Column_radix_feature_category"), //$NON-NLS-1$
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
                                                                 getString("_UI_Column_radix_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_radix_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Radix(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Signed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSignedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_signed_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("ColumnItemProvider._UI_PropertyDescriptor_description_Signed"), //$NON-NLS-1$
                                                                 RelationalPackage.eINSTANCE.getColumn_Signed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_Column_signed_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Signed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSignedPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_signed_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_signed_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Signed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Distinct Value Count feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDistinctValueCountPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_distinctValueCount_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_distinctValueCount_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_DistinctValueCount(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Null Value Count feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNullValueCountPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_nullValueCount_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_nullValueCount_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_NullValueCount(),
                                                                 true,
                                                                 ItemPropertyDescriptor.INTEGRAL_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Unique Keys feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addUniqueKeysPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_Column_uniqueKeys_feature"), //$NON-NLS-1$
                                                                             getString("ColumnItemProvider._UI_PropertyDescriptor_description_UniqueKeys"), //$NON-NLS-1$
                                                                             RelationalPackage.eINSTANCE.getColumn_UniqueKeys(),
                                                                             true, null,
                                                                             getString("_UI_Column_uniqueKeys_feature_category"), //$NON-NLS-1$
                                                                             null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((Column)o).getUniqueKeys();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                final Column column = (Column)object;
                final ColumnSet owner = column.getOwner();
                if (owner instanceof BaseTable) {
                    final List result = new ArrayList();
                    final BaseTable table = (BaseTable)owner;
                    final PrimaryKey pk = table.getPrimaryKey();
                    if (pk != null) {
                        result.add(pk);
                    }
                    final List constraints = table.getUniqueConstraints();
                    if (constraints.size() != 0) {
                        result.addAll(constraints);
                    }
                    return result;
                }
                // Other cases are ProcedureResults and Views (neither of which have unique keys)
                return new ArrayList();
            }
        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Unique Keys feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUniqueKeysPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_uniqueKeys_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_uniqueKeys_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_UniqueKeys(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Indexes feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addIndexesPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_Column_indexes_feature"), //$NON-NLS-1$
                                                                             getString("ColumnItemProvider._UI_PropertyDescriptor_description_Indexes"), //$NON-NLS-1$
                                                                             RelationalPackage.eINSTANCE.getColumn_Indexes(),
                                                                             true, null,
                                                                             getString("_UI_Column_indexes_feature_category"), //$NON-NLS-1$
                                                                             null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((Column)o).getIndexes();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                // Find all of the indexes in this model ...
                final Column column = (Column)object;
                final Resource model = column.eResource();
                final List results = RelationalUtil.findIndexes(model);
                return results;
            }
        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Indexes feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addIndexesPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_indexes_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_indexes_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_Indexes(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Foreign Keys feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addForeignKeysPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_Column_foreignKeys_feature"), //$NON-NLS-1$
                                                                             getString("ColumnItemProvider._UI_PropertyDescriptor_description_ForeignKeys"), //$NON-NLS-1$
                                                                             RelationalPackage.eINSTANCE.getColumn_ForeignKeys(),
                                                                             true,
                                                                             null,
                                                                             getString("_UI_Column_foreignKeys_feature_category"), //$NON-NLS-1$
                                                                             null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((Column)o).getForeignKeys();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                final Column column = (Column)object;
                final ColumnSet owner = column.getOwner();
                if (owner instanceof BaseTable) {
                    final BaseTable table = (BaseTable)owner;
                    return table.getForeignKeys();
                }
                // Other cases are ProcedureResults and Views (neither of which have foreign keys)
                return new ArrayList();
            }
        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Foreign Keys feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addForeignKeysPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_foreignKeys_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_foreignKeys_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_ForeignKeys(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Access Patterns feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addAccessPatternsPropertyDescriptor( Object object ) {
        // Start customized code
        final ItemPropertyDescriptor descriptor = new ItemPropertyDescriptor(
                                                                             ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                             getResourceLocator(),
                                                                             getString("_UI_Column_accessPatterns_feature"), //$NON-NLS-1$
                                                                             getString("ColumnItemProvider._UI_PropertyDescriptor_description_AccessPatterns"), //$NON-NLS-1$
                                                                             RelationalPackage.eINSTANCE.getColumn_AccessPatterns(),
                                                                             true,
                                                                             null,
                                                                             getString("_UI_Column_accessPatterns_feature_category"), //$NON-NLS-1$
                                                                             null) {
            @Override
            public Object getPropertyValue( Object o ) {
                return ((Column)o).getAccessPatterns();
            }

            @Override
            public Collection getChoiceOfValues( Object object ) {
                final Column column = (Column)object;
                final ColumnSet owner = column.getOwner();
                if (owner instanceof Table) {
                    final Table table = (Table)owner;
                    return table.getAccessPatterns();
                }
                // Other case is ProcedureResults (does not have access patterns)
                return new ArrayList();
            }
        };
        itemPropertyDescriptors.add(descriptor);
        // End customized code
    }

    /**
     * This adds a property descriptor for the Access Patterns feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addAccessPatternsPropertyDescriptorGen( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_Column_accessPatterns_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_Column_accessPatterns_feature", "_UI_Column_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 RelationalPackage.eINSTANCE.getColumn_AccessPatterns(),
                                                                 true,
                                                                 null,
                                                                 null,
                                                                 null));
    }

    /**
     * This returns Column.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/Column"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String getText( Object object ) {
        // Start customized code
        final Column column = (Column)object;
        String label = column.getName();
        if (label == null || label.length() == 0) {
            label = getString("_UI_Column_type"); //$NON-NLS-1$
        }
        // Add the datatype ...
        final EObject dt = column.getType();
        final DatatypeManager dtMgr = ModelerCore.getDatatypeManager(column, true);
        final String dtName = dtMgr.getName(dt);
        if (dt != null && dtName != null && dtName.trim().length() != 0) {
            label = label + " : " + dtName; //$NON-NLS-1$
        }
        final int length = column.getLength();
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
        String label = ((Column)object).getName();
        return label == null || label.length() == 0 ? getString("_UI_Column_type") : //$NON-NLS-1$
        getString("_UI_Column_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(Column.class)) {
            case RelationalPackage.COLUMN__NATIVE_TYPE:
            case RelationalPackage.COLUMN__LENGTH:
            case RelationalPackage.COLUMN__FIXED_LENGTH:
            case RelationalPackage.COLUMN__PRECISION:
            case RelationalPackage.COLUMN__SCALE:
            case RelationalPackage.COLUMN__NULLABLE:
            case RelationalPackage.COLUMN__AUTO_INCREMENTED:
            case RelationalPackage.COLUMN__DEFAULT_VALUE:
            case RelationalPackage.COLUMN__MINIMUM_VALUE:
            case RelationalPackage.COLUMN__MAXIMUM_VALUE:
            case RelationalPackage.COLUMN__FORMAT:
            case RelationalPackage.COLUMN__CHARACTER_SET_NAME:
            case RelationalPackage.COLUMN__COLLATION_NAME:
            case RelationalPackage.COLUMN__SELECTABLE:
            case RelationalPackage.COLUMN__UPDATEABLE:
            case RelationalPackage.COLUMN__CASE_SENSITIVE:
            case RelationalPackage.COLUMN__SEARCHABILITY:
            case RelationalPackage.COLUMN__CURRENCY:
            case RelationalPackage.COLUMN__RADIX:
            case RelationalPackage.COLUMN__SIGNED:
            case RelationalPackage.COLUMN__DISTINCT_VALUE_COUNT:
            case RelationalPackage.COLUMN__NULL_VALUE_COUNT:
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
