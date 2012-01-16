/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.metamodel.MetamodelRootClassDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;

/**
 * The MetaBase Toolkit Registry represents a single naming/directory service through which metamodels can be registered and
 * discovered. Each MetamodelRegistry includes a reference to a {@link ResourceSet} used when materializing metamodels as model
 * entities. The MetamodelRegistry/ResourceSet pair represents a single meta-level.
 */
public class MetamodelRegistryImpl implements MetamodelRegistry {

    private static final Comparator METAMODEL_ROOT_CLASS_NAME_COMPARATOR = new MetamodelRootClassNameComparator();
    private static final EClass[] EMPTY_ECLASS_ARRAY = new EClass[0];
    private static final MetamodelRootClass[] EMPTY_METAMODEL_ROOT_CLASS_ARRAY = new MetamodelRootClass[0];

    private final Map descriptorByUriMap;
    private final Map uriByStringMap;
    private final Set uris;
    private MetamodelAspectManager aspectMgr;

    /**
     * @since 5.0
     */
    public MetamodelRegistryImpl() {
        this.descriptorByUriMap = new HashMap();
        this.uriByStringMap = new HashMap();
        this.uris = new HashSet();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#containsURI(java.lang.String)
     * @since 5.0
     */
    @Override
    public boolean containsURI( final String nsUriString ) {
        if (CoreStringUtil.isEmpty(nsUriString)) {
            return false;
        }
        final URI nsUri = getURI(nsUriString);
        if (nsUri == null) {
            return false;
        }
        return containsURI(nsUri);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#containsURI(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    @Override
    public boolean containsURI( final URI nsUri ) {
        if (nsUri == null) {
            return false;
        }

        // Check if the nsUri is the primary namespace URI ...
        if (this.uris.contains(nsUri)) {
            return true;
        }

        // Check if the nsUri is an alternate namespace URI
        if (this.descriptorByUriMap.keySet().contains(nsUri)) {
            return true;
        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getMetamodelDescriptor(java.lang.String)
     * @since 5.0
     */
    @Override
    public MetamodelDescriptor getMetamodelDescriptor( final String nsUriString ) {
        CoreArgCheck.isNotNull(nsUriString);
        CoreArgCheck.isNotZeroLength(nsUriString);

        final URI nsUri = getURI(nsUriString);
        if (nsUri == null) {
            return null;
        }
        return getMetamodelDescriptor(nsUri);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getMetamodelDescriptor(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    @Override
    public MetamodelDescriptor getMetamodelDescriptor( final URI nsUri ) {
        CoreArgCheck.isNotNull(nsUri);
        return (MetamodelDescriptor)this.descriptorByUriMap.get(nsUri);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getMetamodelDescriptors()
     * @since 5.0
     */
    @Override
    public MetamodelDescriptor[] getMetamodelDescriptors() {
        Collection values = new HashSet(this.descriptorByUriMap.values());
        return (MetamodelDescriptor[])values.toArray(new MetamodelDescriptor[values.size()]);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getMetamodelDisplayName(java.lang.String)
     */
    @Override
    public String getMetamodelName( String nsUriString ) {
        MetamodelDescriptor descriptor = getMetamodelDescriptor(nsUriString);

        // no descriptor found
        if (descriptor == null) {
            return null;
        }

        String name = descriptor.getDisplayName();

        if (CoreStringUtil.isEmpty(name)) {
            return descriptor.getName();
        }

        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getModelTypeName(java.lang.String)
     */
    @Override
    public String getModelTypeName( String modelType ) {
        CoreArgCheck.isNotEmpty(modelType, "modelType is empty"); //$NON-NLS-1$

        for (MetamodelDescriptor descriptor : getMetamodelDescriptors()) {
            for (ModelType allowedModelType : descriptor.getAllowableModelTypes()) {
                if (allowedModelType.getLiteral().equals(modelType)) {
                    return allowedModelType.getDisplayName();
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getModelTypes(java.lang.String)
     */
    @Override
    public Set<String> getModelTypes( String nsUriString ) {
        MetamodelDescriptor descriptor = getMetamodelDescriptor(nsUriString);

        // no descriptor found
        if (descriptor == null) {
            return Collections.emptySet();
        }

        ModelType[] modelTypes = descriptor.getAllowableModelTypes();

        if (modelTypes.length != 0) {
            Set<String> result = new HashSet<String>(modelTypes.length);

            for (ModelType modelType : modelTypes) {
                result.add(modelType.getLiteral());
            }

            return result;
        }

        // should never get here
        return Collections.emptySet();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getEPackage(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    @Override
    public EPackage getEPackage( final URI nsUri ) {
        CoreArgCheck.isNotNull(nsUri);
        MetamodelDescriptor descriptor = (MetamodelDescriptor)this.descriptorByUriMap.get(nsUri);
        if (descriptor != null) {
            return descriptor.getEPackage();
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getResource(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    @Override
    public Resource getResource( final URI nsUri ) {
        CoreArgCheck.isNotNull(nsUri);
        MetamodelDescriptor descriptor = (MetamodelDescriptor)this.descriptorByUriMap.get(nsUri);
        if (descriptor != null) {
            EPackage ePkg = descriptor.getEPackage();
            if (ePkg != null) {
                return ePkg.eResource();
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getURI(java.lang.String)
     * @since 5.0
     */
    @Override
    public URI getURI( final String nsUriString ) {
        CoreArgCheck.isNotNull(nsUriString);
        CoreArgCheck.isNotZeroLength(nsUriString);
        return (URI)this.uriByStringMap.get(nsUriString);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getURIs()
     * @since 5.0
     */
    @Override
    public Collection getURIs() {
        return this.uris;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#register(com.metamatrix.modeler.core.MetamodelDescriptor)
     * @since 5.0
     */
    @Override
    public URI register( final MetamodelDescriptor descriptor ) {
        CoreArgCheck.isNotNull(descriptor);
        addDescriptorMappings(descriptor);
        return getURI(descriptor.getNamespaceURI());
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#unregister(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    @Override
    public void unregister( final URI nsUri ) {
        CoreArgCheck.isNotNull(nsUri);
        MetamodelDescriptor descriptor = getMetamodelDescriptor(nsUri);
        if (descriptor != null) {
            removeDescriptorMappings(descriptor);
        }
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#dispose()
     * @since 5.0
     */
    @Override
    public void dispose() {
        MetamodelDescriptor[] descriptors = getMetamodelDescriptors();
        for (int i = 0; i != descriptors.length; ++i) {
            MetamodelDescriptor d = descriptors[i];
            if (d instanceof MetamodelDescriptorImpl) {
                ((MetamodelDescriptorImpl)d).dispose();
            }
        }
        this.descriptorByUriMap.clear();
        this.uriByStringMap.clear();
        this.uris.clear();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getAdapterFactory()
     * @since 5.0
     */
    @Override
    public AdapterFactory getAdapterFactory() {
        if (this.aspectMgr == null) {
            this.aspectMgr = new MetamodelAspectManager(this);
        }
        return this.aspectMgr.getAdapterFactory();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getMetamodelAspect(org.eclipse.emf.ecore.EObject,
     *      java.lang.Class)
     * @since 5.0
     */
    @Override
    public MetamodelAspect getMetamodelAspect( final EObject eObject,
                                               final Class type ) {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isNotNull(type);
        return getMetamodelAspect(getEClass(eObject), type);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelRegistry#getMetamodelAspect(org.eclipse.emf.ecore.EClass,
     *      java.lang.Class)
     * @since 5.0
     */
    @Override
    public MetamodelAspect getMetamodelAspect( final EClass eClass,
                                               final Class type ) {
        CoreArgCheck.isNotNull(eClass);
        CoreArgCheck.isNotNull(type);
        if (this.aspectMgr == null) {
            this.aspectMgr = new MetamodelAspectManager(this);
        }
        return this.aspectMgr.getMetamodelAspect(eClass, type);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getMetaClassLabel(org.eclipse.emf.ecore.EClass)
     * @since 5.0
     */
    @Override
    public String getMetaClassLabel( final EClass eClass ) {
        CoreArgCheck.isNotNull(eClass);

        final AdapterFactory factory = getAdapterFactory();
        final EFactory objectFactory = eClass.getEPackage().getEFactoryInstance();
        final EObject instance = objectFactory.create(eClass);

        final IItemLabelProvider provider = (IItemLabelProvider)factory.adapt(instance, IItemLabelProvider.class);
        if (provider != null && provider instanceof ItemProviderAdapter) {
            final ItemProviderAdapter adapter = (ItemProviderAdapter)provider;
            try {
                final String name = adapter.getString("_UI_" + eClass.getName() + "_type"); //$NON-NLS-1$//$NON-NLS-2$
                return name;
            } catch (MissingResourceException e) {
                // do nothing ...
            }
        }
        return eClass.getName();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getMetaClassURI(org.eclipse.emf.ecore.EClass)
     * @since 5.0
     */
    @Override
    public String getMetaClassURI( final EClass eClass ) {
        CoreArgCheck.isNotNull(eClass);
        return EcoreUtil.getURI(eClass).toString();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getMetaClass(java.lang.String)
     * @since 5.0
     */
    @Override
    public EClass getMetaClass( final String metaClassUriString ) {
        CoreArgCheck.isNotNull(metaClassUriString);
        CoreArgCheck.isNotZeroLength(metaClassUriString);
        URI metaClassUri = URI.createURI(metaClassUriString);
        URI nsUri = metaClassUri.trimFragment();
        if (nsUri != null) {
            EPackage ePackage = getEPackage(nsUri);
            if (ePackage != null) {
                return (EClass)ePackage.eResource().getEObject(metaClassUri.fragment());
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getRootMetaClasses(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    @Override
    public EClass[] getRootMetaClasses( final URI nsUri ) {
        CoreArgCheck.isNotNull(nsUri);
        MetamodelDescriptor d = getMetamodelDescriptor(nsUri);
        if (d != null) {
            final List metamodelRootClasses = getRootMetaClasses(d);
            final List result = new ArrayList(metamodelRootClasses.size());
            for (Iterator iter = metamodelRootClasses.iterator(); iter.hasNext();) {
                final MetamodelRootClass mrc = (MetamodelRootClass)iter.next();
                result.add(mrc.getEClass());
            }
            return (EClass[])result.toArray(new EClass[result.size()]);
        }
        return EMPTY_ECLASS_ARRAY;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelRegistry#getMetamodelRootClasses(org.eclipse.emf.common.util.URI)
     * @since 5.0
     */
    @Override
    public MetamodelRootClass[] getMetamodelRootClasses( URI nsUri ) {
        CoreArgCheck.isNotNull(nsUri);
        MetamodelDescriptor d = getMetamodelDescriptor(nsUri);
        if (d != null) {
            final List result = getRootMetaClasses(d);
            return (MetamodelRootClass[])result.toArray(new MetamodelRootClass[result.size()]);
        }
        return EMPTY_METAMODEL_ROOT_CLASS_ARRAY;
    }

    protected URI createUri( final String uriString ) {
        CoreArgCheck.isNotNull(uriString);
        CoreArgCheck.isNotZeroLength(uriString);

        // Return the existing URI instance if it exists
        if (this.uriByStringMap.containsKey(uriString)) {
            return (URI)this.uriByStringMap.get(uriString);
        }

        // Create a new URI instance
        URI uri = null;
        if ((new File(uriString)).exists()) {
            uri = URI.createFileURI(uriString);
        } else {
            uri = URI.createURI(uriString);
        }

        return uri;
    }

    protected EClass getEClass( final EObject eObject ) {
        CoreArgCheck.isNotNull(eObject);
        if (!(eObject instanceof XClass) && eObject instanceof EClass) {
            return (EClass)eObject;
        }
        return eObject.eClass();
    }

    protected void addDescriptorMappings( final MetamodelDescriptor descriptor ) {
        CoreArgCheck.isNotNull(descriptor);

        // Populate the maps with the primary namespace URI information
        String nsUriString = descriptor.getNamespaceURI();
        URI nsUri = createUri(nsUriString);
        this.uriByStringMap.put(nsUriString, nsUri);
        this.descriptorByUriMap.put(nsUri, descriptor);

        // Only add the primary namespace URI to the set of URIs for the registry
        this.uris.add(nsUri);

        // If there are alternate namespace URIs for this metamodel then register them also
        String[] alternateUris = descriptor.getAlternateNamespaceURIs();
        if (alternateUris != null && alternateUris.length > 0) {
            for (int i = 0; i != alternateUris.length; ++i) {
                nsUriString = alternateUris[i];
                nsUri = createUri(nsUriString);
                this.uriByStringMap.put(nsUriString, nsUri);
                this.descriptorByUriMap.put(nsUri, descriptor);
            }
        }
    }

    protected void removeDescriptorMappings( final MetamodelDescriptor descriptor ) {
        CoreArgCheck.isNotNull(descriptor);

        String nsUriString = descriptor.getNamespaceURI();
        URI nsUri = createUri(nsUriString);
        this.uris.remove(nsUri);
        this.uriByStringMap.remove(nsUriString);
        this.descriptorByUriMap.remove(nsUri);

        // If there are alternate namespace URIs for this metamodel then register them also
        String[] alternateUris = descriptor.getAlternateNamespaceURIs();
        if (alternateUris != null && alternateUris.length > 0) {
            for (int i = 0; i != alternateUris.length; ++i) {
                nsUriString = alternateUris[i];
                nsUri = createUri(nsUriString);
                this.uriByStringMap.remove(nsUriString);
                this.descriptorByUriMap.remove(nsUri);
            }
        }
    }

    protected List getRootMetaClasses( final MetamodelDescriptor descriptor ) {
        CoreArgCheck.isNotNull(descriptor);

        final URI nsUri = getURI(descriptor.getNamespaceURI());
        final MetamodelRootClassDescriptor[] rootClasses = descriptor.getRootClassDescriptors();

        // If the allowable root classes are defined in the MetamodelDescriptor ...
        if (rootClasses != null && rootClasses.length > 0) {

            // Create a list of all EClass instances in the metamodel
            final List allEClasses = new ArrayList();
            final EPackage ePackage = getEPackage(nsUri);
            for (Iterator i = ePackage.getEClassifiers().iterator(); i.hasNext();) {
                final EClassifier eClassifier = (EClassifier)i.next();
                if (eClassifier instanceof EClass) {
                    final EClass eClass = (EClass)eClassifier;
                    if (!allEClasses.contains(eClass) && !eClass.isAbstract() && !eClass.isInterface()) {
                        allEClasses.add(eClass);
                    }
                }

            }

            // Iterate through the list of all EClass in the metamodel and create
            // a list containing only those instances that match the EClass defined
            // in the descriptor or implement the interface defined in the descriptor
            final Map result = new HashMap(rootClasses.length);

            for (Iterator i = allEClasses.iterator(); i.hasNext();) {
                final EClass eClass = (EClass)i.next();

                // Check the list of EClass instances in the descriptor to see
                // if the metamodel EClass instance matches the descriptors
                // class or implements the descriptors class interface
                for (int j = 0; j < rootClasses.length; j++) {
                    final Class eClassClass = eClass.getClass();
                    final Class eClassInstanceClass = eClass.getInstanceClass();

                    final Class rootClass = rootClasses[j].getExtensionClass();
                    final int maxOccurs = rootClasses[j].getMaxOccurs();

                    // Add to the result if the Class instance is the same
                    if (eClassClass.equals(rootClass) && !result.containsKey(eClass)) {
                        result.put(eClass, new MetamodelRootClass(eClass, maxOccurs));
                        break;
                    }
                    // Add to the result if the referenced Class instance is the same
                    else if (eClassInstanceClass != null && eClassInstanceClass.equals(rootClass) && !result.containsKey(eClass)) {
                        result.put(eClass, new MetamodelRootClass(eClass, maxOccurs));
                        break;
                    }
                }
            }
            List listResult = new ArrayList(result.values());

            // Sort the list of EClasses before returning it
            Collections.sort(listResult, METAMODEL_ROOT_CLASS_NAME_COMPARATOR);
            return listResult;
        }
        return Collections.EMPTY_LIST;
    }

    static class MetamodelRootClassNameComparator implements Comparator {

        @Override
        public int compare( Object obj1,
                            Object obj2 ) {
            if (obj1 == null && obj2 == null) {
                return 0;
            } else if (obj1 == null && obj2 != null) {
                return -1;
            } else if (obj1 != null && obj2 == null) {
                return 1;
            }
            MetamodelRootClass rootClass1 = (MetamodelRootClass)obj1;
            MetamodelRootClass rootClass2 = (MetamodelRootClass)obj2;
            EClass eClass1 = rootClass1.getEClass();
            EClass eClass2 = rootClass2.getEClass();
            String value1 = eClass1.getName();
            String value2 = eClass2.getName();
            return value1.compareToIgnoreCase(value2);
        }
    }

}
