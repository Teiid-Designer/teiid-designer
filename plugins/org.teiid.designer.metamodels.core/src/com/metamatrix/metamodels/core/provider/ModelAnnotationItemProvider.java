/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.core.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.custom.impl.XsdModelAnnotationImpl;

/**
 * This is the item provider adapter for a {@link com.metamatrix.metamodels.core.ModelAnnotation} object. <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class ModelAnnotationItemProvider extends ItemProviderAdapter
    implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider,
    IItemPropertySource {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ModelAnnotationItemProvider( AdapterFactory adapterFactory ) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public List getPropertyDescriptors( Object object ) {
        // Start customized code
        // mmDefect_12555 - Repopulate the list of property descriptors every call
        if (itemPropertyDescriptors != null) {
            this.itemPropertyDescriptors.clear();
            this.itemPropertyDescriptors = null;
        }
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            // mmDefect_12555 - Specialize the visible properties for XSD resources
            boolean isXsd = (object instanceof XsdModelAnnotationImpl);

            // addDescriptionPropertyDescriptor(object);
            if (!isXsd) {
                addNameInSourcePropertyDescriptor(object);
            }
            addPrimaryMetamodelUriPropertyDescriptor(object);
            addModelTypePropertyDescriptor(object);
            // addVisiblePropertyDescriptor(object); // hidden, since settable in 4.2 using VDB Editor
            // End customized code
            addNamespaceUriPropertyDescriptor(object);
            addExtensionPackagePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List getPropertyDescriptorsGen( Object object ) { // NO_UCD
        if (itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            addDescriptionPropertyDescriptor(object);
            addNameInSourcePropertyDescriptor(object);
            addPrimaryMetamodelUriPropertyDescriptor(object);
            addModelTypePropertyDescriptor(object);
            addNamespaceUriPropertyDescriptor(object);
            addProducerNamePropertyDescriptor(object);
            addProducerVersionPropertyDescriptor(object);
            addExtensionPackagePropertyDescriptor(object);
        }
        return itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Description feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addDescriptionPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_description_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("_UI_ModelAnnotation_description_feature_description"), //$NON-NLS-1$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_Description(),
                                                                 false,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_ModelAnnotation_description_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Description feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addDescriptionPropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_description_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_description_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_Description(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Name In Source feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addNameInSourcePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_nameInSource_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("_UI_ModelAnnotation_nameInSource_feature_description"), //$NON-NLS-1$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_NameInSource(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_ModelAnnotation_nameInSource_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Name In Source feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNameInSourcePropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_nameInSource_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_nameInSource_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_NameInSource(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Primary Metamodel Uri feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addPrimaryMetamodelUriPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_primaryMetamodelUri_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("_UI_ModelAnnotation_primaryMetamodelUri_feature_description"), //$NON-NLS-1$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_PrimaryMetamodelUri(),
                                                                 false,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_ModelAnnotation_primaryMetamodelUri_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Primary Metamodel Uri feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addPrimaryMetamodelUriPropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_primaryMetamodelUri_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_primaryMetamodelUri_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_PrimaryMetamodelUri(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Model Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addModelTypePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_modelType_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("_UI_ModelAnnotation_modelType_feature_description"), //$NON-NLS-1$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_ModelType(),
                                                                 false,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_ModelAnnotation_modelType_feature_category"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Model Type feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addModelTypePropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_modelType_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_modelType_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_ModelType(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Max Set Size feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addMaxSetSizePropertyDescriptor( Object object ) {
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Max Set Size feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addMaxSetSizePropertyDescriptorGen( Object object ) { // NO_UCD
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Visible feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addVisiblePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_visible_feature"), //$NON-NLS-1$
                                                                 // Start customized code
                                                                 getString("_UI_ModelAnnotation_visible_feature_description"), //$NON-NLS-1$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_Visible(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 getString("_UI_ModelAnnotation_visible_feature_category"), //$NON-NLS-1$
                                                                 // End customized code
                                                                 null));
    }

    /**
     * This adds a property descrip//            addMaxSetSizePropertyDescriptor(object);
//            addVisiblePropertyDescriptor(object);
//            addSupportsDistinctPropertyDescriptor(object);
//            addSupportsJoinPropertyDescriptor(object);
//            addSupportsOrderByPropertyDescriptor(object);
//            addSupportsOuterJoinPropertyDescriptor(object);
//            addSupportsWhereAllPropertyDescriptor(object);tor for the Visible feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addVisiblePropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_visible_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_visible_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_Visible(),
                                                                 true,
                                                                 ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Supports Distinct feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSupportsDistinctPropertyDescriptor( Object object ) {
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Distinct feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSupportsDistinctPropertyDescriptorGen( Object object ) { // NO_UCD
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Join feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSupportsJoinPropertyDescriptor( Object object ) {
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Join feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSupportsJoinPropertyDescriptorGen( Object object ) { // NO_UCD
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Order By feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSupportsOrderByPropertyDescriptor( Object object ) {
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Order By feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSupportsOrderByPropertyDescriptorGen( Object object ) { // NO_UCD
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Outer Join feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSupportsOuterJoinPropertyDescriptor( Object object ) {
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Outer Join feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSupportsOuterJoinPropertyDescriptorGen( Object object ) { // NO_UCD
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Where All feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addSupportsWhereAllPropertyDescriptor( Object object ) {
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Supports Where All feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addSupportsWhereAllPropertyDescriptorGen( Object object ) { // NO_UCD
    	// Property no longer supported
    }

    /**
     * This adds a property descriptor for the Namespace Uri feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addNamespaceUriPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_namespaceUri_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_namespaceUri_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_NamespaceUri(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 getString("_UI_ModelAnnotation_namespaceUri_feature_category"), //$NON-NLS-1$
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Producer Name feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addProducerNamePropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_ProducerName_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_ProducerName_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_ProducerName(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Producer Version feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addProducerVersionPropertyDescriptor( Object object ) {
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_ProducerVersion_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_ProducerVersion_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_ProducerVersion(),
                                                                 true,
                                                                 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
                                                                 null,
                                                                 null));
    }

    /**
     * This adds a property descriptor for the Extension Package feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected void addExtensionPackagePropertyDescriptor( Object object ) {
        // Start customized code
        itemPropertyDescriptors.add(new ItemPropertyDescriptor(
                                                               ((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                               getResourceLocator(),
                                                               getString("_UI_ModelAnnotation_extensionPackage_feature"), //$NON-NLS-1$
                                                               getString("_UI_ModelAnnotation_extensionPackage_feature_description"), //$NON-NLS-1$
                                                               CorePackage.eINSTANCE.getModelAnnotation_ExtensionPackage(),
                                                               true,
                                                               null,
                                                               getString("_UI_ModelAnnotation_extensionPackage_feature_category"), //$NON-NLS-1$
                                                               null) {
            /**
             * @see org.eclipse.emf.edit.provider.ItemPropertyDescriptor#getChoiceOfValues(java.lang.Object)
             */
            @Override
            public Collection getChoiceOfValues( Object object ) {
                // mmDefect_12555 - Get the list of extension package choices
                // Collection results = super.getChoiceOfValues(object);
                Collection results = getReachableXPackages(object);
                if (!results.contains(null)) {
                    final List newResults = new ArrayList(results.size() + 1);
                    newResults.add(null);
                    newResults.addAll(results);
                    results = newResults;
                }
                return results;
            }

            // mmDefect_12555 - Collect all the extension package instances encountered in the workspace.
            // This specialization is required since a XsdModelAnnotationImpl is not owned by a
            // resource. Calls to super.getChoiceOfValues(object) return an empty collection since
            // the logic does not have a resource or resource set to search over.
            private Collection getReachableXPackages( Object object ) {
                Collection result = new HashSet();
                if (object instanceof XsdModelAnnotationImpl) {
                    EClassifier type = CorePackage.eINSTANCE.getModelAnnotation_ExtensionPackage().getEType();
                    Collection visited = new HashSet();
                    Resource resource = ((XsdModelAnnotationImpl)object).getResource();
                    if (resource != null) {
                        ResourceSet resourceSet = resource.getResourceSet();
                        if (resourceSet != null) {
                            for (TreeIterator i = resourceSet.getAllContents(); i.hasNext();) {
                                Object child = i.next();
                                if (child instanceof EObject) {
                                    collectReachableObjectsOfType(visited, result, (EObject)child, type);
                                    i.prune();
                                }
                            }
                        } else {
                            for (Iterator i = resource.getContents().iterator(); i.hasNext();) {
                                collectReachableObjectsOfType(visited, result, (EObject)i.next(), type);
                            }
                        }
                    } else {
                        collectReachableObjectsOfType(visited, result, EcoreUtil.getRootContainer((EObject)object), type);
                    }
                    return result;
                } else if (object instanceof ModelAnnotation) {
                    EClassifier type = CorePackage.eINSTANCE.getModelAnnotation_ExtensionPackage().getEType();
                    return getReachableObjectsOfType((EObject)object, type);
                }
                return result;
            }
        });
        // End customized code
    }

    /**
     * This adds a property descriptor for the Extension Package feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected void addExtensionPackagePropertyDescriptorGen( Object object ) { // NO_UCD
        itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
                                                                 getResourceLocator(),
                                                                 getString("_UI_ModelAnnotation_extensionPackage_feature"), //$NON-NLS-1$
                                                                 getString("_UI_PropertyDescriptor_description", "_UI_ModelAnnotation_extensionPackage_feature", "_UI_ModelAnnotation_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                                                 CorePackage.eINSTANCE.getModelAnnotation_ExtensionPackage(),
                                                                 true,
                                                                 null,
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
            childrenFeatures.add(CorePackage.eINSTANCE.getModelAnnotation_Tags());
            childrenFeatures.add(CorePackage.eINSTANCE.getModelAnnotation_ModelImports());
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
     * This returns ModelAnnotation.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object getImage( Object object ) {
        return getResourceLocator().getImage("full/obj16/ModelAnnotation"); //$NON-NLS-1$
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String getText( Object object ) {
        String label = ((ModelAnnotation)object).getNameInSource();
        return label == null || label.length() == 0 ? getString("_UI_ModelAnnotation_type") : //$NON-NLS-1$
        getString("_UI_ModelAnnotation_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
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

        switch (notification.getFeatureID(ModelAnnotation.class)) {
            case CorePackage.MODEL_ANNOTATION__DESCRIPTION:
            case CorePackage.MODEL_ANNOTATION__NAME_IN_SOURCE:
            case CorePackage.MODEL_ANNOTATION__PRIMARY_METAMODEL_URI:
            case CorePackage.MODEL_ANNOTATION__MODEL_TYPE:
            case CorePackage.MODEL_ANNOTATION__MAX_SET_SIZE:
            case CorePackage.MODEL_ANNOTATION__VISIBLE:
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_DISTINCT:
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_JOIN:
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_ORDER_BY:
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN:
            case CorePackage.MODEL_ANNOTATION__SUPPORTS_WHERE_ALL:
            case CorePackage.MODEL_ANNOTATION__NAMESPACE_URI:
            case CorePackage.MODEL_ANNOTATION__PRODUCER_NAME:
            case CorePackage.MODEL_ANNOTATION__PRODUCER_VERSION:
                fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
            case CorePackage.MODEL_ANNOTATION__TAGS:
            case CorePackage.MODEL_ANNOTATION__MODEL_IMPORTS:
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

        newChildDescriptors.add(createChildParameter(CorePackage.eINSTANCE.getModelAnnotation_Tags(),
                                                     EcoreFactory.eINSTANCE.create(EcorePackage.eINSTANCE.getEStringToStringMapEntry())));

        newChildDescriptors.add(createChildParameter(CorePackage.eINSTANCE.getModelAnnotation_ModelImports(),
                                                     CoreFactory.eINSTANCE.createModelImport()));
    }

    /**
     * Return the resource locator for this item provider's resources. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ResourceLocator getResourceLocator() {
        return CoreEditPlugin.INSTANCE;
    }

    /**
     * Return whether the resource containing this object represents a virtual model. If we cannot determine if the resource is a
     * virtual model then false is returned
     * 
     * @generated NOT
     */
    protected boolean isVirtual( Object object ) {
        if (object instanceof ModelAnnotation) {
            ModelAnnotation annot = (ModelAnnotation)object;
            if (annot.getModelType() == ModelType.VIRTUAL_LITERAL) {
                return true;
            }
        }
        return false;
    }

}
