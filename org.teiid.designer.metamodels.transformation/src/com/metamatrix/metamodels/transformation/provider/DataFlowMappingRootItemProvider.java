/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot} object. <!--
 * begin-user-doc --> <!-- end-user-doc -->
 * 
 * @generated
 */
public class DataFlowMappingRootItemProvider extends TransformationMappingRootItemProvider {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public DataFlowMappingRootItemProvider( AdapterFactory adapterFactory ) {
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

            addAllowsOptimizationPropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Allows Optimization feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addAllowsOptimizationPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_DataFlowMappingRoot_allowsOptimization_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_DataFlowMappingRoot_allowsOptimization_feature", "_UI_DataFlowMappingRoot_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 TransformationPackage.eINSTANCE.getDataFlowMappingRoot_AllowsOptimization(),
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
     * @generated NOT
     */
    @Override
    public Collection getChildrenFeatures( Object object ) {
        if (childrenFeatures == null) {
            childrenFeatures = new ArrayList();
            Collection result = super.getChildrenFeatures(object);
            if (childrenFeatures == null) {
                childrenFeatures = new ArrayList();
                childrenFeatures.addAll(result);
            }
            childrenFeatures.add(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes());
        }
        return childrenFeatures;
    }

    /**
     * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
     * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
     * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Collection getChildrenFeaturesGen( Object object ) {
        if (childrenFeatures == null) {
            super.getChildrenFeatures(object);
            childrenFeatures.add(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes());
            childrenFeatures.add(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Links());
        }
        return childrenFeatures;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EStructuralFeature getChildFeature( Object object,
                                                  Object child ) {
        // Check the type of the specified child object and return the proper feature to use for
        // adding (see {@link AddCommand}) it as a child.

        return super.getChildFeature(object, child);
    }

    /**
     * This returns DataFlowMappingRoot.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/DataFlowMappingRoot"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getText( Object object ) {
        DataFlowMappingRoot dataFlowMappingRoot = (DataFlowMappingRoot)object;
        return getString("_UI_DataFlowMappingRoot_type") + " " + dataFlowMappingRoot.isOutputReadOnly(); //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(DataFlowMappingRoot.class)) {
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__NODES:
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT__LINKS:
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

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createDataFlowNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createTargetNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createSourceNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createOperationNodeGroup()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createOperationNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createJoinNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createUnionNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createProjectionNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createFilterNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createGroupingNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createDupRemovalNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createSortNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Nodes(),
                                                     TransformationFactory.eINSTANCE.createSqlNode()));

        newChildDescriptors.add(createChildParameter(TransformationPackage.eINSTANCE.getDataFlowMappingRoot_Links(),
                                                     TransformationFactory.eINSTANCE.createDataFlowLink()));
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
