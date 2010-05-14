/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.provider;


import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcPackage;
import com.metamatrix.modeler.jdbc.JdbcSource;

/**
 * This is the item provider adapter for a {@link com.metamatrix.modeler.jdbc.JdbcSource} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class JdbcSourceItemProvider
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
    public JdbcSourceItemProvider(AdapterFactory adapterFactory) {
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

            addJdbcDriverPropertyDescriptor(object);
            addNamePropertyDescriptor(object);
            addDriverNamePropertyDescriptor(object);
            addDriverClassPropertyDescriptor(object);
            addUsernamePropertyDescriptor(object);
            addUrlPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    protected void addNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
        (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_JdbcSource_name_feature"), //$NON-NLS-1$
             getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_name_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
             JdbcPackage.eINSTANCE.getJdbcSource_Name(),
             true,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             //Customize code start
             getString("JdbcSource_source_category"), //$NON-NLS-1$
//           Customize code end
             null));
        
//        itemPropertyDescriptors.add
//            (new ItemPropertyDescriptor
//                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
//                 getString("_UI_JdbcSource_name_feature"), //$NON-NLS-1$
//                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_name_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                 JdbcPackage.eINSTANCE.getJdbcSource_Name(),
//                 true,
//                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
//                 getString("JdbcSource_source_category"))); //$NON-NLS-1$
    }
    
    /**
     * This adds a property descriptor for the Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addNamePropertyDescriptorGen(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcSource_name_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_name_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcSource_Name(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Driver Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    protected void addDriverNamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
        (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_JdbcSource_driverName_feature"), //$NON-NLS-1$
             getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_driverName_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
             JdbcPackage.eINSTANCE.getJdbcSource_DriverName(),
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,             
//           Customize code start
             getString("JdbcSource_driver_category"), //$NON-NLS-1$
//           Customize code end
             null));
        
//        itemPropertyDescriptors.add
//            (new ItemPropertyDescriptor
//                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
//                 getString("_UI_JdbcSource_driverName_feature"), //$NON-NLS-1$
//                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_driverName_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                 JdbcPackage.eINSTANCE.getJdbcSource_DriverName(),
//                 false,
//                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
//                 getString("JdbcSource_driver_category"))); //$NON-NLS-1$
    }
    
    /**
     * This adds a property descriptor for the Driver Name feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addDriverNamePropertyDescriptorGen(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcSource_driverName_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_driverName_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcSource_DriverName(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Driver Class feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    protected void addDriverClassPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
        (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_JdbcSource_driverClass_feature"), //$NON-NLS-1$
             getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_driverClass_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
             JdbcPackage.eINSTANCE.getJdbcSource_DriverClass(),
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
//           Customize code start
             getString("JdbcSource_driver_category"), //$NON-NLS-1$
//           Customize code end,
             null));
        
//        itemPropertyDescriptors.add
//            (new ItemPropertyDescriptor
//                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
//                 getString("_UI_JdbcSource_driverClass_feature"), //$NON-NLS-1$
//                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_driverClass_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                 JdbcPackage.eINSTANCE.getJdbcSource_DriverClass(),
//                 false,
//                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
//                 getString("JdbcSource_driver_category"))); //$NON-NLS-1$
    }
    
    /**
     * This adds a property descriptor for the Driver Class feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addDriverClassPropertyDescriptorGen(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcSource_driverClass_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_driverClass_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcSource_DriverClass(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Username feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    protected void addUsernamePropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
        (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_JdbcSource_username_feature"), //$NON-NLS-1$
             getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_username_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
             JdbcPackage.eINSTANCE.getJdbcSource_Username(),
             true,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             //customize code
             getString("JdbcSource_source_category"),//$NON-NLS-1$
             //end customization
             null));
        
//        itemPropertyDescriptors.add
//            (new ItemPropertyDescriptor
//                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
//                 getString("_UI_JdbcSource_username_feature"), //$NON-NLS-1$
//                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_username_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                 JdbcPackage.eINSTANCE.getJdbcSource_Username(),
//                 true,
//                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
//                 getString("JdbcSource_source_category"))); //$NON-NLS-1$
    }

    /**
     * This adds a property descriptor for the Username feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addUsernamePropertyDescriptorGen(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcSource_username_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_username_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcSource_Username(),
                 true,
                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                 null,
                 null));
    }

    /**
     * This adds a property descriptor for the Url feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    protected void addUrlPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
        (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_JdbcSource_url_feature"), //$NON-NLS-1$
             getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_url_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
             JdbcPackage.eINSTANCE.getJdbcSource_Url(),
             true,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             //custom code
             getString("JdbcSource_source_category"), //$NON-NLS-1$
             //end code
             null));
        
//        itemPropertyDescriptors.add
//            (new ItemPropertyDescriptor
//                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
//                 getString("_UI_JdbcSource_url_feature"), //$NON-NLS-1$
//                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_url_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                 JdbcPackage.eINSTANCE.getJdbcSource_Url(),
//                 true,
//                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
//                 getString("JdbcSource_source_category"))); //$NON-NLS-1$
    }
    
    /**
     * This adds a property descriptor for the Url feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addUrlPropertyDescriptorGen(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcSource_url_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_url_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcSource_Url(),
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
            childrenFeatures.add(JdbcPackage.eINSTANCE.getJdbcSource_Properties());
            childrenFeatures.add(JdbcPackage.eINSTANCE.getJdbcSource_ImportSettings());
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature(Object object, Object child) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This adds a property descriptor for the Jdbc Driver feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    protected void addJdbcDriverPropertyDescriptor(Object object) {
        itemPropertyDescriptors.add
        (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_JdbcSource_jdbcDriver_feature"), //$NON-NLS-1$
             getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_jdbcDriver_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
             JdbcPackage.eINSTANCE.getJdbcSource_JdbcDriver(),
             false,
             null,
             //custom code
             getString("JdbcSource_driver_category"), //$NON-NLS-1$
             //end custom
             null));
        
//        itemPropertyDescriptors.add
//            (new ItemPropertyDescriptor
//                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
//                 getString("_UI_JdbcSource_jdbcDriver_feature"), //$NON-NLS-1$
//                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_jdbcDriver_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//                 JdbcPackage.eINSTANCE.getJdbcSource_JdbcDriver(),
//                 false,
//                 getString("JdbcSource_driver_category"))); //$NON-NLS-1$
    }

    /**
     * This adds a property descriptor for the Jdbc Driver feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void addJdbcDriverPropertyDescriptorGen(Object object) {
        itemPropertyDescriptors.add
            (createItemPropertyDescriptor
                (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                 getResourceLocator(),
                 getString("_UI_JdbcSource_jdbcDriver_feature"), //$NON-NLS-1$
                 getString("_UI_PropertyDescriptor_description", "_UI_JdbcSource_jdbcDriver_feature", "_UI_JdbcSource_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                 JdbcPackage.eINSTANCE.getJdbcSource_JdbcDriver(),
                 true,
                 null,
                 null,
                 null));
    }

    /**
     * This returns JdbcSource.gif.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object getImage(Object object) {
        return getResourceLocator().getImage("full/obj16/JdbcSource"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    @Override
    public String getText(Object object) {
        return "Data Source"; //$NON-NLS-1$
//      final JdbcSource source = (JdbcSource)object;
//      String label = source.getName();
//      // Append the URL ...
//      final String url = source.getUrl();
//      if ( url != null && url.trim().length() != 0 ) {
//          if ( label == null || label.trim().length() == 0 ) {
//              label = source.getUrl();
//          }
//      }
//      if ( label == null || label.trim().length() == 0 ) {
//          label = getString("_UI_JdbcSource_type"); //$NON-NLS-1$
//      }
//      return label;
    }
    
    /**
     * This returns the label text for the adapted class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTextGen(Object object) {
        String label = ((JdbcSource)object).getName();
        return label == null || label.length() == 0 ?
            getString("_UI_JdbcSource_type") : //$NON-NLS-1$
            getString("_UI_JdbcSource_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(JdbcSource.class)) {
            case JdbcPackage.JDBC_SOURCE__NAME:
            case JdbcPackage.JDBC_SOURCE__DRIVER_NAME:
            case JdbcPackage.JDBC_SOURCE__DRIVER_CLASS:
            case JdbcPackage.JDBC_SOURCE__USERNAME:
            case JdbcPackage.JDBC_SOURCE__URL:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case JdbcPackage.JDBC_SOURCE__PROPERTIES:
            case JdbcPackage.JDBC_SOURCE__IMPORT_SETTINGS:
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
     * @generated NOT
     */
    @Override
    protected void collectNewChildDescriptors(Collection newChildDescriptors, Object object)
    {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add
            (createChildParameter
                (JdbcPackage.eINSTANCE.getJdbcSource_Properties(),
                 JdbcFactory.eINSTANCE.createJdbcSourceProperty()));

//        newChildDescriptors.add
//            (createChildParameter
//                (JdbcPackage.eINSTANCE.getJdbcSource_ImportSettings(),
//                 JdbcFactory.eINSTANCE.createJdbcImportSettings()));
    }
    
    /**
     * This adds to the collection of {@link org.eclipse.emf.edit.command.CommandParameter}s
     * describing all of the children that can be created under this object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected void collectNewChildDescriptorsGen(Collection newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);

        newChildDescriptors.add
            (createChildParameter
                (JdbcPackage.eINSTANCE.getJdbcSource_Properties(),
                 JdbcFactory.eINSTANCE.createJdbcSourceProperty()));

        newChildDescriptors.add
            (createChildParameter
                (JdbcPackage.eINSTANCE.getJdbcSource_ImportSettings(),
                 JdbcFactory.eINSTANCE.createJdbcImportSettings()));
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
