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
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.provider.MappingHelperItemProvider;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.transformation.SqlTransformation} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class SqlTransformationItemProvider extends MappingHelperItemProvider {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SqlTransformationItemProvider( AdapterFactory adapterFactory ) {
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

            addSelectSqlPropertyDescriptor(object);
            addInsertSqlPropertyDescriptor(object);
            addUpdateSqlPropertyDescriptor(object);
            addDeleteSqlPropertyDescriptor(object);
            addInsertAllowedPropertyDescriptor(object);
            addUpdateAllowedPropertyDescriptor(object);
            addDeleteAllowedPropertyDescriptor(object);
            addOutputLockedPropertyDescriptor(object);
            addInsertSqlDefaultPropertyDescriptor(object);
            addUpdateSqlDefaultPropertyDescriptor(object);
            addDeleteSqlDefaultPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Select Sql feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSelectSqlPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_selectSql_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_selectSql_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_SelectSql(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Insert Sql feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addInsertSqlPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_insertSql_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_insertSql_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_InsertSql(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Update Sql feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUpdateSqlPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_updateSql_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_updateSql_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_UpdateSql(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Delete Sql feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDeleteSqlPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_deleteSql_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_deleteSql_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_DeleteSql(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Insert Allowed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addInsertAllowedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_insertAllowed_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_insertAllowed_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_InsertAllowed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Update Allowed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUpdateAllowedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_updateAllowed_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_updateAllowed_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_UpdateAllowed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Delete Allowed feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDeleteAllowedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_deleteAllowed_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_deleteAllowed_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_DeleteAllowed(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Output Locked feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addOutputLockedPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_outputLocked_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_outputLocked_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_OutputLocked(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Insert Sql Default feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addInsertSqlDefaultPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_insertSqlDefault_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_insertSqlDefault_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_InsertSqlDefault(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Update Sql Default feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addUpdateSqlDefaultPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_updateSqlDefault_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_updateSqlDefault_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_UpdateSqlDefault(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Delete Sql Default feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDeleteSqlDefaultPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_SqlTransformation_deleteSqlDefault_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_SqlTransformation_deleteSqlDefault_feature", "_UI_SqlTransformation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getSqlTransformation_DeleteSqlDefault(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Collection getChildrenFeatures( Object object ) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(TransformationPackage.eINSTANCE.getSqlTransformation_Aliases());
        }
        return childrenFeatures;
    }

    /**
     * This returns SqlTransformation.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/SqlTransformation"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     */
    @Override
    public String getText( Object object ) {
        return getString("_UI_SqlTransformation_type"); //$NON-NLS-1$
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

        switch (notification.getFeatureID(SqlTransformation.class)) {
            case TransformationPackage.SQL_TRANSFORMATION__SELECT_SQL:
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL:
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL:
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL:
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_ALLOWED:
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_ALLOWED:
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_ALLOWED:
            case TransformationPackage.SQL_TRANSFORMATION__OUTPUT_LOCKED:
            case TransformationPackage.SQL_TRANSFORMATION__INSERT_SQL_DEFAULT:
            case TransformationPackage.SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT:
            case TransformationPackage.SQL_TRANSFORMATION__DELETE_SQL_DEFAULT:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case TransformationPackage.SQL_TRANSFORMATION__ALIASES:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
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

        newChildDescriptors.add(createChildParameter(MappingPackage.eINSTANCE.getMappingHelper_Nested(),
                                                     TransformationFactory.eINSTANCE.createSqlTransformation()));

        newChildDescriptors.add(createChildParameter(MappingPackage.eINSTANCE.getMappingHelper_Nested(),
                                                     TransformationFactory.eINSTANCE.createXQueryTransformation()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getSqlTransformation_Aliases(),
                                                     TransformationFactory.eINSTANCE.createSqlAlias()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return TransformationEditPlugin.INSTANCE;
    }

}
