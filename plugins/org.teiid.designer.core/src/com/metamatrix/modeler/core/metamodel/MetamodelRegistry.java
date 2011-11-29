/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel;

import java.util.Collection;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.internal.core.metamodel.MetamodelRootClass;

/** 
 * The MetamodelRegistry manages metamodel information through the EPackage.Registry
 * @since 5.0
 */
public interface MetamodelRegistry{
    
    /**
     * Register the metamodel described by this {@link MetamodelDescriptor}.
     * @param descriptor defines the metamodel to register; may not be null
     * @return the namespace URI used as the identifier for this metamodel in the registry
     */
    URI register(MetamodelDescriptor descriptor);

    /**
     * Unregister the metamodel for this identifier, if it exists, else do nothing.
     * @param nsUri the namespace URI identifier for the metamodel in the registry; may not be null
     */
    void unregister(URI nsUri);

    /**
     * Returns the {@link URI) instance for this namespace URI string, if
     * it exists, else return null. 
     * @param nsUriString the stringified namespace URI; may not be null or empty
     * @return URI instance
     */
    URI getURI(String nsUriString);

    /**
     * Returns the collection of the collection of namespace URI identifiers {@link URI) for all 
     * metamodels known by this registry
     * @return Collection of identifiers
     */
    Collection getURIs();

    /**
     * Returns true if the registry contains a metamodel with this namespace URI
     * identifier string else return false.
     * @param nsUri the namespace URI identifier string for the metamodel in the registry
     */
    boolean containsURI(String nsUriString);

    /**
     * Returns true if the registry contains a metamodel with this namespace URI
     * identifier else return false.
     * @param nsUri the namespace URI identifier for the metamodel in the registry
     */
    boolean containsURI(URI nsUri);

    /**
     * Returns the collection of {@link MetamodelDescriptor} instances for all
     * metamodels known by this registry.
     * @return Collection of MetamodelDescriptor instances
     */
    MetamodelDescriptor[] getMetamodelDescriptors();

    /**
     * Returns the {@link MetamodelDescriptor} for this namespace URI identifier string
     * or null if one does not exist.
     * @param nsUri the identifier string for the metamodel in the registry; may not be null or empty
     * @return MetamodelDescriptor
     */
    MetamodelDescriptor getMetamodelDescriptor(String nsUriString);

    /**
     * Returns the {@link MetamodelDescriptor} for this namespace URI identifier or null if one
     * does not exist.
     * @param nsUri the identifier for the metamodel in the registry; may not be null
     * @return MetamodelDescriptor
     */
    MetamodelDescriptor getMetamodelDescriptor(URI nsUri);

    /**
     * @param nsUri the namespace URI string for the metamodel in the registry (cannot be <code>null</code> or empty)
     * @return the localized metamodel name or <code>null</code>
     */
    String getMetamodelName(String nsUriString);
    
    /**
     * Convenience method to return the {@link EPackage} for the specified namespace 
     * URI identifier or null i fone does not exist. 
     * @param nsUri the namespace URI identifier for the metamodel in the registry; may not be null
     * @return EPackage or null if a metamodel with this URI does not exist
     */
    EPackage getEPackage(URI nsUri);

    /**
     * Convenience method to return the {@link Resource} for the specified namespace 
     * URI identifier or null i fone does not exist. 
     * @param nsUri the namespace URI identifier for the metamodel in the registry; may not be null
     * @return Resource or null if a metamodel with this URI does not exist
     */
    Resource getResource(URI nsUri);
    
    /**
     * Return the associated MetamodelAspect for the EObject and aspect type.
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
     * @param eObject object whose MetamodelAspect is requested; may not be null
     * @param type the key indicating the type of MetamodelAspect required; may not be null
     * @return associated MetamodelAspect
     */
    MetamodelAspect getMetamodelAspect(EObject eObject, Class type);
    
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
    MetamodelAspect getMetamodelAspect(EClass eClass, Class type);

    /**
     * Returns the {@link ComposedAdapterFactory} instance to use when
     * creating adapters for views. The returned reference represents
     * a composition of AdapterFactory instances for all loaded metamodels.
     * If no metamodels are yet loaded, then null is returned.
     * @return AdapterFactory
     */
    AdapterFactory getAdapterFactory();

    /**
     * Return the list of {@link MetamodelRootClass} instances representing
     * the root or top level classes that can be instantiated
     * for this metamodel
     * @param nsUri string
     * @return List
     */
    MetamodelRootClass[] getMetamodelRootClasses(URI nsUri);

    /**
     * Return the list of {@link EClass} instances representing
     * the root or top level classes that can be instantiated
     * for this metamodel
     * @param nsUri string
     * @return List
     */
    EClass[] getRootMetaClasses(URI nsUri);

    /**
     * Return a label for the supplied EClass.
     * @param eClass the metaclass; may not be null
     * @return the displayable label for the EClass
     */
    String getMetaClassLabel(EClass eClass);

    /**
     * Return the string form of the supplied EClass
     * @param eClass string
     * @return String representing the URI of the EClass
     */
    String getMetaClassURI(EClass eClass);

    /**
     * Return the {@link EClass} instance given the meta class URI string.
     * @param metaClassUriString string; may not be null or empty
     * @return EClass
     */
    EClass getMetaClass(String metaClassUriString);
    
    /**
     * Dispose of the metamodel registry and clean up any associated state
     * @since 5.0
     */
    void dispose();
    
}
