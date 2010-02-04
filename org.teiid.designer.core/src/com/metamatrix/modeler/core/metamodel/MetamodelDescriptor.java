/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel;

import java.util.List;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EPackage;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelInitializer;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;

/**
 * Descriptor 
 */
public interface MetamodelDescriptor {
    
    /**
     * Return the identifier of the metamodel extension
     * @return the identifier, or null if no extension ID exists.
     */
    String getExtensionID();
    
    /**
     * Return the name of the metamodel
     * @return String or null if no name exists.
     */
    String getName();
    
    /**
     * Return the display name of the metamodel
     * @return String or null if no display name exists.
     */
    String getDisplayName();
    
    /**
     * Return the address of the metamodel in the form
     * of a URI.
     * @return URI or null if no URI exists.
     */
    String getNamespaceURI();
    
    /**
     * Return the list of alternate addresses of the metamodel in the form
     * of URIs.
     * @return List of alternate URIs that exist (if any).
     */
    String[] getAlternateNamespaceURIs();    
    
    /**
     * Return the namespace prefix associated with this metamodel
     * @return String or null if no prefix exists.
     */
    String getNamespacePrefix();

    /**
     * Return true if this descriptor is for a primary (non-participatory only) metamodel
     * @return true if this descriptor is for a primary (non-participatory only) metamodel
     */
    boolean isPrimary();
    
    /**
     * Return true if this metamodel supports extension
     * @return true if this metamodel supports extension
     */
    boolean supportsExtension();
    
    /**
     * Return true if this metamodel supports diagramming
     * @return true if this metamodel supports diagramming
     */
    boolean supportsDiagrams();
    
    /**
     * Returns true if this metamodel can be used to create a
     * new model instances.
     * @return
     */
    boolean supportsNewModel();
    
    /** 
     * Return the file extension to be used when creating a  
     * {@link org.eclipse.emf.ecore.resource.Resource} for storing
     * an new instance of this metamodel.  Note - the file extension
     * string <b>will</b> be prefixed by a period (e.g. ".xmi").
     * 
     * @return the file extension
     */
    String getFileExtension();
    
    /**
     * Returns an instance of the loaded class for this extension using the plug-in 
     * class loader specified when the ExtensionDescriptor was constructed. 
     * The successful loading of the class will <b>always activate</b> the 
     * corresponding plug-in.  The <b>same</b> instance will returned for all calls
     * to this method.
     * @return an instance of the loaded class or null if the class 
     * could not be loaded or an instance could not be created.
     */
    EPackage getEPackage();
    
    /** 
     * Return an array of ModelType instances representing the set
     * of permissible types for instances of this metamodel. 
     * @return
     */
    ModelType[] getAllowableModelTypes();
    
    /**
     * Returns the names of available ModelInitializers that may be used to initialize an empty
     * model when this metamodel is the primary metamodel.
     * @return the ordered list of String names; may be empty, but never null
     * @see #getModelInitializer(String)
     * @see #getModelInitializerDescription(String)(String)
     */
    List getModelInitializerNames();
    
    /**
     * Return the description for the ModelInitializer with the supplied name.
     * @return the description; null if there is no initializer with that name
     * @see #getModelInitializerNames()
     * @see #getModelInitializer(String)
     */
    String getModelInitializerDescription( String name );
    
    /**
     * Return the ModelInitializer with the supplied name.  The ModelInitializer may be used to initialize 
     * an empty model when this metamodel is the primary metamodel.
     * @return the ModelInitializer; null if there is no initializer with that name
     * @see #getModelInitializerNames()
     * @see #getModelInitializerDescription(String)(String)
     */
    ModelInitializer getModelInitializer( String name );
    
    /**
     * Return an array of MetamodelRootClassDescriptor instances representing the
     * allowable EClass that can be used as roots to a model of this metamodel type
     * @return
     * @since 5.0
     */
    MetamodelRootClassDescriptor[] getRootClassDescriptors();
    
    /**
     * Return an array of AdapterFactory instances associated with this metamodel.
     * @return
     * @since 5.0
     */
    AdapterFactory[] getAdapterFactories();
    
    /**
     * Return an array of MetamodelAspectFactory instances associated with this metamodel.
     * @return
     * @since 5.0
     */
    MetamodelAspectFactory[] getAspectFactories();
    
    /**
     * Return the MetamodelAspectFactory for the specified aspect type.  If no 
     * MetamodelAspectFactory exists for this type then null is returned.  The 
     * type is expected to be one of the following:
     * <p>
     * <li> com.metamatrix.modeler.core.metamodel.aspect.SqlAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.UmlDiagramAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.DependencyAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.FeatureConstraintAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect
     * <li> com.metamatrix.modeler.core.metamodel.aspect.RelationshipMetamodelAspect
     * </p>
     * @param type the key indicating the type of MetamodelAspectFactory to return; may not be null
     * @return the MetamodelAspectFactory
     */
    MetamodelAspectFactory getAspectFactory(Class type);
    
    /**
     * Return the MetamodelAspectFactory for the specified aspect type identifier.  If no 
     * MetamodelAspectFactory exists for this identifier then null is returned.  The identifier
     * is expected to be one of the following:
     * <p>
     * <li> ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.DEPENDENCY_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.FEATURE_CONSTRAINT_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID
     * <li> ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.ID
     * </p>
     * @param extensionID the key indicating the type of MetamodelAspectFactory to return; may not be null
     * @return the MetamodelAspectFactory
     */
    MetamodelAspectFactory getAspectFactory(String extensionID);
}
