/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.metamodel.aspect.DependencyAspect;
import com.metamatrix.modeler.core.metamodel.aspect.FeatureConstraintAspect;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;


/** 
 * @since 5.0
 */
public class MetamodelAspectManager {
    
    // Mappings between the aspect type and the plugin.xml extension ID for the MetamodelAspectFactory
    private static final Map ASPECT_INTERFACE_TO_EXTENSION_ID = new HashMap(7);
    static {
        ASPECT_INTERFACE_TO_EXTENSION_ID.put(SqlAspect.class,                   ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID);
        ASPECT_INTERFACE_TO_EXTENSION_ID.put(UmlDiagramAspect.class,            ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID);
        ASPECT_INTERFACE_TO_EXTENSION_ID.put(ValidationAspect.class,            ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.ID);
        ASPECT_INTERFACE_TO_EXTENSION_ID.put(DependencyAspect.class,            ModelerCore.EXTENSION_POINT.DEPENDENCY_ASPECT.ID);
        ASPECT_INTERFACE_TO_EXTENSION_ID.put(FeatureConstraintAspect.class,     ModelerCore.EXTENSION_POINT.FEATURE_CONSTRAINT_ASPECT.ID);
        ASPECT_INTERFACE_TO_EXTENSION_ID.put(ImportsAspect.class,               ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID);
        ASPECT_INTERFACE_TO_EXTENSION_ID.put(RelationshipMetamodelAspect.class, ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.ID);
    }
    
    private MetamodelRegistry registry;
    private Map metamodelEntityMap; // keyed on EClass
    private ComposedAdapterFactory composedAdapterFactory;
    
    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /** 
     * @since 5.0
     */
    public MetamodelAspectManager(final MetamodelRegistry theRegistry) {
        ArgCheck.isNotNull(theRegistry);
        this.registry = theRegistry;
        this.metamodelEntityMap = new HashMap();
        
        // Ensure that all the metamodels in the registry are initialized
        initializeRegistry(this.registry);
    }
    
    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================
    
    /**
     * Return the {@link ComposedAdapterFactory} reference
     * that is aware of all loaded metamodels.  If no
     * metamodels are yet loaded then null is returned.
     */
    public AdapterFactory getAdapterFactory() {
        // Create the inital ComposedAdapterFactory instance if it does not yet exist
        if (this.composedAdapterFactory == null) {
            this.composedAdapterFactory  = new MetamodelComposedAdapterFactory(new ResourceItemProviderAdapterFactory());
            
            // Add any additional metamodel AdapterFactory instances to the ComposedAdapterFactory
            final MetamodelDescriptor[] descriptors = getRegistry().getMetamodelDescriptors();
            for (int i = 0; i < descriptors.length; i++) {
                final MetamodelDescriptor d = descriptors[i];
                addAdapterFactories(this.composedAdapterFactory, d);
            }
        }

        return composedAdapterFactory;
    }
    
    /**
     * Return the associated MetamodelAspect for the EClass and aspect type.
     * The type is the key used to look up the appropriate factory and
     * MetamodelAspect.  The type is expected to be one of the following:
     * <p>
     * <li> com.metamatrix.modeler.core.metamodel.aspect.SqlAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.UmlDiagramAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.DependencyAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.FeatureConstraintAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.RelationshipMetamodelAspect
     * </p>
     * @param eClass object whose MetamodelAspect is requested; may not be null
     * @param type the key indicating the type of MetamodelAspect required; may not be null
     * @return associated MetamodelAspect
     */
    public MetamodelAspect getMetamodelAspect(final EClass eClass, final Class type) {
        ArgCheck.isNotNull(eClass);
        ArgCheck.isNotNull(type);
        MetamodelEntity entity = getMetamodelEntity(eClass);
        if (entity != null) {
            String extensionID = (String)ASPECT_INTERFACE_TO_EXTENSION_ID.get(type);
            if (extensionID == null) {
                final String msg = ModelerCore.Util.getString("MetamodelAspectManager.Class_does_not_match_any_metamodel_aspect_ID",type.getName()); //$NON-NLS-1$
                throw new IllegalArgumentException(msg);
            }
            return entity.getMetamodelAspect(extensionID);
        }
        return null;
   }
    
    /**
     * Return the associated MetamodelAspect for the EClass and aspect type identifier.
     * The identifier is the key used to look up the appropriate factory and
     * MetamodelAspect.  The identifier is expected to be one of the following:
     * <p>
     * <li> ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.DEPENDENCY_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.FEATURE_CONSTRAINT_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.ID
     * </p>
     * @param eClass object whose MetamodelAspect is requested; may not be null
     * @param extensionID the key indicating the type of MetamodelAspectFactory to return; may not be null
     * @return the MetamodelAspect
     */
    public MetamodelAspect getMetamodelAspect(final EClass eClass, final String extensionID) {
        ArgCheck.isNotNull(eClass);
        ArgCheck.isNotNull(extensionID);
        ArgCheck.isNotZeroLength(extensionID);
        MetamodelEntity entity = getMetamodelEntity(eClass);
        if (entity != null) {
            return entity.getMetamodelAspect(extensionID);
        }
        return null;
   }
    
    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================
    
    protected void initializeRegistry(final MetamodelRegistry registry) {
        Assertion.isNotNull(registry);
        try {
            final MetamodelDescriptor[] descriptors = getRegistry().getMetamodelDescriptors();
            for (int i = 0; i != descriptors.length; ++i) {
                MetamodelDescriptor d = descriptors[i];
                registry.register(d);
                URI nsUri = registry.getURI(d.getNamespaceURI());
                Assertion.isNotNull(nsUri);
                EPackage ePkg = registry.getEPackage(nsUri);
                Assertion.isNotNull(ePkg);
            }
        } catch (Throwable e) {
            ModelerCore.Util.log(e);
        }
    }
    
    protected void addAdapterFactories(final ComposedAdapterFactory composedFactory, final MetamodelDescriptor descriptor) {
        ArgCheck.isNotNull(composedFactory);
        ArgCheck.isNotNull(descriptor);
        
        AdapterFactory[] factories = descriptor.getAdapterFactories();
        for (int i = 0; i < factories.length; i++) {
            final AdapterFactory factory = factories[i];
            if (factory != null) {
                composedFactory.addAdapterFactory(factory);
            }
        }
    }

    protected MetamodelAspectFactory[] getAspectFactories(final EClass eClass) {
        ArgCheck.isNotNull(eClass);

        // Check that this metamodel is known by the registry
        final URI metamodelURI = getMetamodelURI(eClass);
        checkIsRegisteredMetamodel(metamodelURI);
        
        // Retrieve the aspect factories from the metamodel descriptor
        final MetamodelDescriptor d = getRegistry().getMetamodelDescriptor(metamodelURI);
        return d.getAspectFactories();
    }
    
    protected MetamodelEntity getMetamodelEntity(final EClass eClass) {
        ArgCheck.isNotNull(eClass);
        
        // If this EClass is not contained within one of our suppored metamodels ...
        if (!isSupportedMetamodel(eClass)) {
            return null;
        }

        // Check that this metamodel is known by the registry
        final URI metamodelURI = getMetamodelURI(eClass);
        checkIsRegisteredMetamodel(metamodelURI);
        
        if ( !getMetamodelEntityMap().containsKey(eClass) ) {
            getMetamodelEntityMap().put(eClass, createMetamodelEntity(eClass));
        }
        
        return (MetamodelEntity)getMetamodelEntityMap().get(eClass);
    }
    
    protected MetamodelEntity createMetamodelEntity(final EClass eClass) {
        ArgCheck.isNotNull(eClass);

        final URI metamodelURI = getMetamodelURI(eClass);
        final MetamodelEntityImpl entity = new MetamodelEntityImpl(metamodelURI,eClass);
        
        // Create any MetamodelAspect instances associated with this EClass
        final MetamodelAspectFactory[] factories = getAspectFactories(eClass);
        for (int i = 0; i < factories.length; i++) {
            final MetamodelAspectFactory factory = factories[i];
            try {
                final MetamodelAspect aspect = factory.create(eClass, entity);
                if (aspect != null){
                    entity.addMetamodelAspect(aspect.getID(),aspect);
                }
            } catch (IllegalArgumentException e) {
                // result if no aspect can be created from this entity
            }
        }
        return entity;
    }
    
    protected URI getMetamodelURI(final EClass eClass) {
        ArgCheck.isNotNull(eClass);
        return URI.createURI(eClass.getEPackage().getNsURI());
    }
    
    protected boolean isSupportedMetamodel(final EClass eClass) {
        ArgCheck.isNotNull(eClass);
        final Resource resource = eClass.eResource();
        if (resource != null) {
            final URI metamodelURI = resource.getURI();
            if (metamodelURI != null && getRegistry().containsURI(metamodelURI)) {
                return true;
            }
        }
        return false;
    }

    protected void checkIsRegisteredMetamodel(final URI metamodelURI) {
        if (!getRegistry().containsURI(metamodelURI)) {
            final String msg = ModelerCore.Util.getString("MetamodelAspectManager.Metamodel_does_not_exist_in_registry",metamodelURI); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }
    
    protected MetamodelRegistry getRegistry() {
        return this.registry;
    }
    
    protected Map getMetamodelEntityMap() {
        return this.metamodelEntityMap;
    }

    // ==================================================================================
    //                        I N N E R   C L A S S
    // ==================================================================================
    
    protected static class MetamodelComposedAdapterFactory extends ComposedAdapterFactory {
        private static final String UML_ADAPTER_FACTORY_CLASS_NAME = "com.metamatrix.metamodels.uml2.provider.Uml2ItemProviderAdapterFactory"; //$NON-NLS-1$

        private EcoreItemProviderAdapterFactory ecoreAdapter;
        
        public MetamodelComposedAdapterFactory( final AdapterFactory delegateFactory ) {
            super(delegateFactory);
        }
        
        @Override
        public AdapterFactory getFactoryForTypes(Collection types) {
            AdapterFactory result = super.getFactoryForTypes(types);
            if ( result != null ) {
                return result;
            }
            return super.getFactoryForTypes(types);
        }
        
        /**
         * @see org.eclipse.emf.edit.provider.ComposedAdapterFactory#adapt(org.eclipse.emf.common.notify.Notifier, java.lang.Object)
         */
        @Override
        public Adapter adapt(Notifier target, Object type) {
            Adapter adapter = super.adapt(target, type);
            if (adapter == null && ecoreAdapter != null ) {
                // See if the ECore adapter works ...
                adapter = ecoreAdapter.adapt(target,type);
            }
            return adapter;
        }

        /** 
         * @see org.eclipse.emf.edit.provider.ComposedAdapterFactory#addAdapterFactory(org.eclipse.emf.common.notify.AdapterFactory)
         */
        @Override
        public void addAdapterFactory(AdapterFactory adapterFactory) {
            if (adapterFactory == null) {
                return;
            }
            AdapterFactory ecoreAdapterFactory = null;
            AdapterFactory umlAdapterFactory   = null;
            for (Iterator i = this.adapterFactories.iterator(); i.hasNext();) {
                final AdapterFactory af = (AdapterFactory)i.next();
                // If an AdapterFactory already exists then return
                if (af != null && af.getClass().equals(adapterFactory.getClass())) {
                    return;
                }
                // If we've found the Uml adapter factory, save its reference
                if (UML_ADAPTER_FACTORY_CLASS_NAME.equals(af.getClass().getName())) {
                    umlAdapterFactory = af;
                }
                // If we've found the ECore adapter factory, save its reference
                else if (af.getClass().equals(EcoreItemProviderAdapterFactory.class)) {
                    ecoreAdapterFactory = af;
                }
            }
            
            super.addAdapterFactory(adapterFactory);
            
            // If we found the Uml adapter factory, remove it then add it back in
            if ( umlAdapterFactory != null ) {
                this.adapterFactories.remove(umlAdapterFactory);
                super.addAdapterFactory(umlAdapterFactory);
            }
            // If we found the ECore adapter factory, remove it then add it back in - always at the end (because it can adapt anything)
            if ( ecoreAdapterFactory != null ) {
                this.adapterFactories.remove(ecoreAdapterFactory);
                super.addAdapterFactory(ecoreAdapterFactory);
                ecoreAdapter = (EcoreItemProviderAdapterFactory)ecoreAdapterFactory;
            }
        }

    }

}
