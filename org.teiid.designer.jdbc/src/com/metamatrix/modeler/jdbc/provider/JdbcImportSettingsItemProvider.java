/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.provider;


import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPackage;

import com.metamatrix.modeler.jdbc.SourceNames;

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
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link com.metamatrix.modeler.jdbc.JdbcImportSettings} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class JdbcImportSettingsItemProvider
    extends ItemProviderAdapter
    implements
        IEditingDomainItemProvider,
        IStructuredItemContentProvider,
        ITreeItemContentProvider,
        IItemLabelProvider,
        IItemPropertySource {
    /**
     * This constructs an instance from a factory and a notifier.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcImportSettingsItemProvider(AdapterFactory adapterFactory) {
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

            addCreateCatalogsInModelPropertyDescriptor(object);
            addCreateSchemasInModelPropertyDescriptor(object);
            addConvertCaseInModelPropertyDescriptor(object);
            addGenerateSourceNamesInModelPropertyDescriptor(object);
            addIncludedCatalogPathsPropertyDescriptor(object);
            addIncludedSchemaPathsPropertyDescriptor(object);
            addExcludedObjectPathsPropertyDescriptor(object);
            addIncludeForeignKeysPropertyDescriptor(object);
            addIncludeIndexesPropertyDescriptor(object);
            addIncludeProceduresPropertyDescriptor(object);
            addIncludeApproximateIndexesPropertyDescriptor(object);
            addIncludeUniqueIndexesPropertyDescriptor(object);
            addIncludedTableTypesPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Create Catalogs In Model feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addCreateCatalogsInModelPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_createCatalogsInModel_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_createCatalogsInModel_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_CreateCatalogsInModel(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Create Schemas In Model feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addCreateSchemasInModelPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_createSchemasInModel_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_createSchemasInModel_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_CreateSchemasInModel(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Convert Case In Model feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addConvertCaseInModelPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_convertCaseInModel_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_convertCaseInModel_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_ConvertCaseInModel(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Generate Source Names In Model feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addGenerateSourceNamesInModelPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_generateSourceNamesInModel_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_generateSourceNamesInModel_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_GenerateSourceNamesInModel(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Included Catalog Paths feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludedCatalogPathsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includedCatalogPaths_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includedCatalogPaths_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludedCatalogPaths(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Included Schema Paths feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludedSchemaPathsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includedSchemaPaths_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includedSchemaPaths_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludedSchemaPaths(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Excluded Object Paths feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addExcludedObjectPathsPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_excludedObjectPaths_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_excludedObjectPaths_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_ExcludedObjectPaths(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Include Foreign Keys feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludeForeignKeysPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includeForeignKeys_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includeForeignKeys_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludeForeignKeys(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Include Indexes feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludeIndexesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includeIndexes_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includeIndexes_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludeIndexes(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Include Procedures feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludeProceduresPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includeProcedures_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includeProcedures_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludeProcedures(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Include Approximate Indexes feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludeApproximateIndexesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includeApproximateIndexes_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includeApproximateIndexes_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludeApproximateIndexes(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Include Unique Indexes feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludeUniqueIndexesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includeUniqueIndexes_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includeUniqueIndexes_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludeUniqueIndexes(),
                 true,
                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Included Table Types feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addIncludedTableTypesPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcImportSettings_includedTableTypes_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcImportSettings_includedTableTypes_feature", "_UI_JdbcImportSettings_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcImportSettings_IncludedTableTypes(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Collection getChildrenFeatures(Object object) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(JdbcPackage.eINSTANCE.getJdbcImportSettings_Options());
        }
        return childrenFeatures;
    }

    /**
     * This returns JdbcImportSettings.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/JdbcImportSettings"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getText(Object object) {
        return getString("_UI_JdbcImportSettings_type"); //$NON-NLS-1$
    }
    
    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTextGen(Object object) {
        SourceNames labelValue = ((JdbcImportSettings)object).getGenerateSourceNamesInModel();
        String label = labelValue == null ? null : labelValue.toString();
        return label == null || label.length() == 0 ?
            getString("_UI_JdbcImportSettings_type") : //$NON-NLS-1$
            getString("_UI_JdbcImportSettings_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(JdbcImportSettings.class)) {
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES:
            case JdbcPackage.JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case JdbcPackage.JDBC_IMPORT_SETTINGS__OPTIONS:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
                return;
        }
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

        newChildDescriptors.add
            (createChildParameter
                (JdbcPackage.eINSTANCE.getJdbcImportSettings_Options(),
                 JdbcFactory.eINSTANCE.createJdbcImportOptions()));
    }

    /**
     * Return the resource locator for this item provider's resources.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return JdbcEditPlugin.INSTANCE;
    }

}
