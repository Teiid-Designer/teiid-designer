/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.mapping.MappingHelper;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.metamodels.core.CorePackage;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramContainer;
import com.metamatrix.metamodels.diagram.DiagramFactory;
import com.metamatrix.metamodels.transformation.FragmentMappingRoot;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * Applicable containers are defined by the specific Metamodel type. Possible containers include Physical Relational/Data
 * Access/Logical = AnnotationContainer, DiagramContainer Virtual Models = AnnotationContainer, DiagramContainer,
 * TransformationContainer XML Models = AnnotationContainer, DiagramContainer, TransformationContainer, MappingClassSetContainer
 * 
 * @since 4.3
 */
public class ModelResourceContainerFactory {

    private static final String ADD_VALUE_ERROR = "ModelResourceContainerFactory.addValueError"; //$NON-NLS-1$
    private static final String REMOVE_VALUE_ERROR = "ModelResourceContainerFactory.removeValueError"; //$NON-NLS-1$

    private static final String NO_ANNOTATION_CONTAINER_ERROR = "ModelResourceContainerFactory.noAnnotationContainerError"; //$NON-NLS-1$
    private static final String NO_MAPPING_CLASS_SET_CONTAINER_ERROR = "ModelResourceContainerFactory.noMappingClassSetContainerError"; //$NON-NLS-1$

    private static final String SQL_ROOT_TARGET_NOT_VALID_KEY = "ModelResourceContainerFactory.sqlRootTargetInvalid"; //$NON-NLS-1$
    private static final String TREE_ROOT_TARGET_NOT_VALID_KEY = "ModelResourceContainerFactory.treeRootTargetInvalid"; //$NON-NLS-1$
    private static final String FRAGMENT_ROOT_TARGET_NOT_VALID_KEY = "ModelResourceContainerFactory.fragmentRootTargetInvalid"; //$NON-NLS-1$
    private static final String XML_DOCUCUMENT_TARGET_NOT_VALID_KEY = "ModelResourceContainerFactory.xmlDocumentTargetInvalid"; //$NON-NLS-1$

    // Not Supported Warnings.
    private static final String DIAGRAMS_NOT_SUPPORTED_KEY = "ModelResourceContainerFactory.diagramsNotSupportedWarning"; //$NON-NLS-1$

    private static final TransformationFactory transformationFactory = TransformationFactory.eINSTANCE;
    private static final DiagramFactory diagramFactory = DiagramFactory.eINSTANCE;
    private static final CoreFactory coreFactory = CorePackage.eINSTANCE.getCoreFactory();

    // Temporary constant because the xml metamodel isn't exposed/available to Modeler Core
    private static final String XML_URI = "http://www.metamatrix.com/metamodels/XmlDocument"; //$NON-NLS-1$

    private static String getString( String key,
                                     Object value ) {
        return ModelerCore.Util.getString(key, value);
    }

    private static String getString( String key,
                                     Object value,
                                     Object value2 ) {
        return ModelerCore.Util.getString(key, value, value2);
    }

    private static boolean isTransactionable = ModelerCore.getPlugin() != null;

    private static void addValue( final Object owner,
                                  final Object value,
                                  EList feature ) {
        try {
            if (isTransactionable) {
                ModelerCore.getModelEditor().addValue(owner, value, feature);
            } else {
                feature.add(value);
            }
        } catch (ModelerCoreException err) {
            String message = getString(ADD_VALUE_ERROR, value, owner);
            ModelerCore.Util.log(IStatus.ERROR, message);
        }
    }

    private static boolean removeValue( final Object owner,
                                        final Object value,
                                        EList feature ) {
        boolean successful = false;
        try {
            if (isTransactionable) {
                ModelerCore.getModelEditor().removeValue(owner, value, feature);
            } else {
                feature.remove(value);
            }
            successful = true;
        } catch (ModelerCoreException err) {
            String message = getString(REMOVE_VALUE_ERROR, value, owner);
            ModelerCore.Util.log(IStatus.ERROR, message);
        }
        return successful;
    }

    /**
     * This method provides a way to initialize and create all containers possible within an EMF resource
     * 
     * @param resource
     * @return
     * @since 4.3
     */
    public static Collection getAllContainers( final Resource resource,
                                               final boolean forceCreate ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource)) return Collections.EMPTY_LIST;

        EmfResource emfResource = (EmfResource)resource;
        // add null check? and change to emfResource
        List allContainers = new ArrayList();

        // Note: ModelContents keeps a reference for each container. In order to keep them in sync,
        // we need to call the getContainer method for each...
        ModelContents contents = ModelerCore.getModelEditor().getModelContents(emfResource);

        if (supportsAnnotations(emfResource)) {
            allContainers.add(getAnnotationContainer(resource, forceCreate));
            contents.getAnnotationContainer(forceCreate);
        }

        if (supportsDiagrams(emfResource)) {
            allContainers.add(getDiagramContainer(resource, forceCreate));
            contents.getDiagramContainer(forceCreate);
        }

        if (supportsTransformations(emfResource)) {
            allContainers.add(getTransformationContainer(resource, forceCreate));
            contents.getTransformationContainer(forceCreate);
        }

        if (supportsMappingClassSets(emfResource)) {
            allContainers.add(getMappingClassSetContainer(resource, forceCreate));
            contents.getMappingClassSetContainer(forceCreate);
        }
        if (allContainers.isEmpty()) return Collections.EMPTY_LIST;

        return allContainers;
    }

    // ================================================================
    // MAPPING CLASS SET CONTAINER METHODS
    // ================================================================

    /**
     * Utility method to create the MappingClassSetContainer for this resource
     * 
     * @param resource
     * @return the MappingClassSetContainer for this resource
     * @since 4.3
     */
    public static MappingClassSetContainer createMappingClassSetContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsMappingClassSets(resource)) return null;

        MappingClassSetContainer container = getMappingClassSetContainer(resource);

        if (container == null) {
            EmfResource emfResource = (EmfResource)resource;
            container = transformationFactory.createMappingClassSetContainer();
            addValue(emfResource, container, emfResource.getContents());
        }

        return container;
    }

    /**
     * Utility method to return the MappingClassSetContainer for this resource. If the resource supports this container type and
     * it exists, the container is returned. If the resource supports this container type and it doesn't exist and forceCreate ==
     * TRUE then, a new container is created and added to the resource.
     * 
     * @param resource
     * @param forceCreate - boolean to create container if none exists
     * @return the MappingClassSetContainer for this resource
     */
    public static MappingClassSetContainer getMappingClassSetContainer( final Resource resource,
                                                                        final boolean forceCreate ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsMappingClassSets(resource)) return null;

        MappingClassSetContainer container = getMappingClassSetContainer(resource);

        // If there is no container, then create one ...
        if (container == null && forceCreate) {
            container = createMappingClassSetContainer(resource);
        }

        return container;
    }

    /**
     * Utility method to return the MappingClassSetContainer for an existing list of rootEObjects
     * 
     * @return the MappingClassSetContainer for this resource
     */
    public static MappingClassSetContainer getMappingClassSetContainer( final List rootEObjects ) {
        ArgCheck.isNotNull(rootEObjects);

        if (rootEObjects.isEmpty()) return null;

        // Make a copy of the root objects (to prevent concurrent mod exception)
        final List rootObjs = new ArrayList(rootEObjects);

        // Look under the roots for the MappingClassSetContainer
        final Iterator iter = rootObjs.iterator();
        while (iter.hasNext()) {
            final EObject rootObj = (EObject)iter.next();
            if (rootObj instanceof MappingClassSetContainer) {
                return (MappingClassSetContainer)rootObj;
            }
        }

        return null;
    }

    /**
     * Utility method to return an existing MappingClassSetContainer for this resource
     * 
     * @return the MappingClassSetContainer for this resource
     */
    public static MappingClassSetContainer getMappingClassSetContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource)) return null;

        return getMappingClassSetContainer(((EmfResource)resource).getContents());
    }

    // ================================================================
    // TRANSFORMATION CONTAINER METHODS
    // ================================================================

    /**
     * Utility method to create the TransformationContainer for this resource
     * 
     * @param resource
     * @return the TransformationContainer for this resource
     * @since 4.3
     */
    public static TransformationContainer createTransformationContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsTransformations(resource)) return null;

        TransformationContainer container = getTransformationContainer(resource);

        if (container == null) {
            EmfResource emfResource = (EmfResource)resource;
            container = transformationFactory.createTransformationContainer();
            addValue(emfResource, container, emfResource.getContents());
        }

        return container;
    }

    /**
     * Utility method to return the TransformationContainer for this resource If the resource supports this container type and it
     * exists, the container is returned. If the resource supports this container type and it doesn't exist and forceCreate ==
     * TRUE then, a new container is created and added to the resource.
     * 
     * @param resource
     * @param forceCreate - boolean to create container if none exists
     * @return the TransformationContainer for this resource
     */
    public static TransformationContainer getTransformationContainer( final Resource resource,
                                                                      final boolean forceCreate ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsTransformations(resource)) return null;

        TransformationContainer container = getTransformationContainer(resource);

        // If there is no container, then create one ...
        if (container == null && forceCreate) {
            container = createTransformationContainer(resource);
        }

        return container;
    }

    /**
     * Utility method to return the TransformationContainer for an existing list of rootEObjects
     * 
     * @return the TransformationContainer for this resource
     */
    public static TransformationContainer getTransformationContainer( final List rootEObjects ) {
        ArgCheck.isNotNull(rootEObjects);

        if (rootEObjects.isEmpty()) return null;

        // Make a copy of the root objects (to prevent concurrent mod exception)
        final List rootObjs = new ArrayList(rootEObjects);

        // Look under the roots for the TransformationContainer
        final Iterator iter = rootObjs.iterator();
        while (iter.hasNext()) {
            final EObject rootObj = (EObject)iter.next();
            if (rootObj instanceof TransformationContainer) {
                return (TransformationContainer)rootObj;
            }
        }

        return null;
    }

    /**
     * Utility method to return an existing TransformationContainer for this resource
     * 
     * @return the TransformationContainer for this resource
     */
    public static TransformationContainer getTransformationContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource)) return null;

        return getTransformationContainer(((EmfResource)resource).getContents());
    }

    // ================================================================
    // DIAGRAM CONTAINER METHODS
    // ================================================================

    /**
     * Utility method to create the DiagramContainer for this resource
     * 
     * @param resource
     * @return the DiagramContainer for this resource
     * @since 4.3
     */
    public static DiagramContainer createDiagramContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsDiagrams(resource)) return null;

        DiagramContainer container = getDiagramContainer(resource);

        if (container == null) {
            EmfResource emfResource = (EmfResource)resource;
            container = diagramFactory.createDiagramContainer();
            addValue(emfResource, container, emfResource.getContents());
        }

        return container;
    }

    /**
     * Utility method to return the DiagramContainer for this resource If the resource supports this container type and it exists,
     * the container is returned. If the resource supports this container type and it doesn't exist and forceCreate == TRUE then,
     * a new container is created and added to the resource.
     * 
     * @param resource
     * @param forceCreate - boolean to create container if none exists
     * @return the DiagramContainer for this resource
     */
    public static DiagramContainer getDiagramContainer( final Resource resource,
                                                        final boolean forceCreate ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsDiagrams(resource)) return null;

        DiagramContainer container = getDiagramContainer(resource);

        if (container == null && forceCreate) {
            container = createDiagramContainer(resource);
        }

        return container;
    }

    /**
     * Utility method to return the DiagramContainer for an existing list of rootEObjects
     * 
     * @return the DiagramContainer for this resource
     */
    public static DiagramContainer getDiagramContainer( final List rootEObjects ) {
        ArgCheck.isNotNull(rootEObjects);

        if (rootEObjects.isEmpty()) return null;

        // Make a copy of the root objects (to prevent concurrent mod exception)
        final List rootObjs = new ArrayList(rootEObjects);

        // Look under the roots for the DiagramContainer
        final Iterator iter = rootObjs.iterator();
        while (iter.hasNext()) {
            final EObject rootObj = (EObject)iter.next();
            if (rootObj instanceof DiagramContainer) {
                return (DiagramContainer)rootObj;
            }
        }

        return null;
    }

    /**
     * Utility method to return an existing DiagramContainer for this resource
     * 
     * @return the DiagramContainer for this resource
     */
    public static DiagramContainer getDiagramContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource)) return null;

        return getDiagramContainer(((EmfResource)resource).getContents());
    }

    // ================================================================
    // ANNOTATION CONTAINER METHODS
    // ================================================================

    /**
     * Utility method to create the AnnotationContainer for this resource
     * 
     * @param resource
     * @return the AnnotationContainer for this resource
     * @since 4.3
     */
    public static AnnotationContainer createAnnotationContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsAnnotations(resource)) return null;

        AnnotationContainer container = getAnnotationContainer(resource);

        if (container == null) {
            EmfResource emfResource = (EmfResource)resource;
            container = coreFactory.createAnnotationContainer();
            addValue(emfResource, container, emfResource.getContents());
        }

        return container;
    }

    /**
     * Utility method to return the AnnotationContainer for this resource If the resource supports this container type and it
     * exists, the container is returned. If the resource supports this container type and it doesn't exist and forceCreate ==
     * TRUE then, a new container is created and added to the resource.
     * 
     * @param resource
     * @param forceCreate - boolean to create container if none exists
     * @return the AnnotationContainer for this resource
     */
    public static AnnotationContainer getAnnotationContainer( final Resource resource,
                                                              final boolean forceCreate ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource) || !supportsAnnotations(resource)) return null;

        AnnotationContainer container = getAnnotationContainer(resource);

        // If there is no container, then create one ...
        if (container == null && forceCreate) {
            container = createAnnotationContainer(resource);
        }

        return container;
    }

    /**
     * Utility method to return the AnnotationContainer for an existing list of rootEObjects
     * 
     * @return the AnnotationContainer for this resource
     */
    public static AnnotationContainer getAnnotationContainer( final List rootEObjects ) {
        ArgCheck.isNotNull(rootEObjects);

        if (rootEObjects.isEmpty()) return null;

        // Make a copy of the root objects (to prevent concurrent mod exception)
        final List rootObjs = new ArrayList(rootEObjects);

        // Look under the roots for the AnnotationContainer
        final Iterator iter = rootObjs.iterator();
        while (iter.hasNext()) {
            final EObject rootObj = (EObject)iter.next();
            if (rootObj instanceof AnnotationContainer) {
                return (AnnotationContainer)rootObj;
            }
        }

        return null;
    }

    /**
     * Utility method to return an existing AnnotationContainer for this resource
     * 
     * @return the AnnotationContainer for this resource
     */
    public static AnnotationContainer getAnnotationContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);

        if (!(resource instanceof EmfResource)) return null;

        return getAnnotationContainer(((EmfResource)resource).getContents());
    }

    // ================================================================
    // SUPPORTS METAMODEL METHODS
    // ================================================================

    public static boolean supportsDiagrams( final Resource resource ) {
        if (resource == null || !(resource instanceof EmfResource)) return false;

        EmfResource emfResource = (EmfResource)resource;
        // add null check? and change to emfResource
        boolean supportsDiagrams = false;

        // Now let's look at model type and URI info to determine which containers to find/create/return

        // Check ModelAnnotation
        if (emfResource.getModelAnnotation() != null) {
            // Check URI
            String stringURI = emfResource.getModelAnnotation().getPrimaryMetamodelUri();
            if (!StringUtil.isEmpty(stringURI)) {
                MetamodelDescriptor descriptor = ModelerCore.getMetamodelRegistry().getMetamodelDescriptor(stringURI);

                if (descriptor != null) {
                    supportsDiagrams = descriptor.supportsDiagrams();
                }
            }
        }

        return supportsDiagrams;
    }

    public static boolean supportsTransformations( final Resource resource ) {
        if (resource == null || !(resource instanceof EmfResource)) return false;

        EmfResource emfResource = (EmfResource)resource;
        // add null check? and change to emfResource
        boolean supportsTransformations = false;

        // Now let's look at model type and URI info to determine which containers to find/create/return

        // Check ModelAnnotation
        if (emfResource.getModelAnnotation() != null) {
            ModelType type = emfResource.getModelAnnotation().getModelType();
            if (type.equals(ModelType.VIRTUAL_LITERAL)) {
                supportsTransformations = true;
            } else if (type.equals(ModelType.LOGICAL_LITERAL)) {
                String stringURI = emfResource.getModelAnnotation().getPrimaryMetamodelUri();
                if (XML_URI.equals(stringURI)) {
                    supportsTransformations = true;
                }
            }
        }

        return supportsTransformations;
    }

    public static boolean supportsMappingClassSets( final Resource resource ) {
        if (resource == null || !(resource instanceof EmfResource)) return false;

        EmfResource emfResource = (EmfResource)resource;
        // add null check? and change to emfResource
        boolean supportsMappingClassSets = false;

        // Now let's look at model type and URI info to determine which containers to find/create/return

        // Check ModelAnnotation
        if (emfResource.getModelAnnotation() != null) {
            ModelType type = emfResource.getModelAnnotation().getModelType();
            String stringURI = emfResource.getModelAnnotation().getPrimaryMetamodelUri();
            if (type.equals(ModelType.VIRTUAL_LITERAL)) {
                // Check URI
                if (XML_URI.equals(stringURI)) {
                    supportsMappingClassSets = true;
                }
            } else if (type.equals(ModelType.LOGICAL_LITERAL)) {
                if (XML_URI.equals(stringURI)) {
                    supportsMappingClassSets = true;
                }
            }
        }

        return supportsMappingClassSets;
    }

    public static boolean supportsAnnotations( final Resource resource ) {
        if (resource == null || !(resource instanceof EmfResource)) return false;

        EmfResource emfResource = (EmfResource)resource;
        // add null check? and change to emfResource
        boolean supportsAnnotations = false;

        // Now let's look at model type and URI info to determine which containers to find/create/return

        // Check ModelAnnotation
        if (emfResource.getModelAnnotation() != null) {
            ModelType type = emfResource.getModelAnnotation().getModelType();
            if (type.equals(ModelType.VIRTUAL_LITERAL)) {
                supportsAnnotations = true;
            } else if (type.equals(ModelType.PHYSICAL_LITERAL)) {
                supportsAnnotations = true;
            } else if (type.equals(ModelType.FUNCTION_LITERAL)) {
                supportsAnnotations = true;
            } else if (type.equals(ModelType.LOGICAL_LITERAL)) {
                supportsAnnotations = true;
            } else if (type.equals(ModelType.EXTENSION_LITERAL)) {
                supportsAnnotations = true;
            }
        }

        return supportsAnnotations;
    }

    // -----------------------------------------------------------
    // Annotation Methods
    // -----------------------------------------------------------

    /**
     * This method assumes the user requests a new annotation only The user is reponsible for setting the target of the annotation
     * and adding the annotation to the annotation container correctly
     * 
     * @return new Annotation
     */
    public static Annotation createNewAnnotation() {
        return CoreFactory.eINSTANCE.createAnnotation();
    }

    /**
     * This method assumes the user requests a new annotation for a target object and the user is responsible for adding the
     * annotation to the annotation container
     * 
     * @param target
     * @return new Annotation
     * @since 4.3
     */
    public static Annotation createNewAnnotation( final EObject target ) {
        ArgCheck.isNotNull(target);

        Annotation newAnnotation = createNewAnnotation();
        newAnnotation.setAnnotatedObject(target);

        return newAnnotation;
    }

    /**
     * This method assumes the user requests a new annotation for a target object and the user supplies the annotation container
     * 
     * @param target reference the annotation
     * @param container
     * @return new Annotation
     * @since 4.3
     */
    public static Annotation createNewAnnotation( final EObject target,
                                                  final AnnotationContainer container ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(container);

        Annotation newAnnotation = createNewAnnotation(target);
        // newAnnotation.setAnnotationContainer(container);
        // Now that we have the root, have to add the value correctly
        // If we don't have a resource, we can't add to the container
        addValue(container, newAnnotation, container.getAnnotations());

        return newAnnotation;
    }

    /**
     * This method assumes the user requests a new annotation for a target object and that the new object exists within an
     * existing resource and that the new annotation should be fully instantiated and added to the annotation container
     * 
     * @param target reference for annotation
     * @param resource - an EmfResource
     * @return new Annotation
     * @since 4.3
     */
    public static Annotation createNewAnnotation( final EObject target,
                                                  final Resource resource ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        Annotation annotation = null;

        if (supportsAnnotations(resource)) {
            AnnotationContainer container = ModelResourceContainerFactory.getAnnotationContainer(resource, true);
            if (container != null) {
                annotation = createNewAnnotation(target, container);
            } else {
                // We shouldn't get here. If ModelResource exists, then we should have created a container successfully
                String message = getString(NO_ANNOTATION_CONTAINER_ERROR, resource, target);
                ModelerCore.Util.log(IStatus.ERROR, message);
            }
        }

        return annotation;
    }

    /**
     * Convience method to delete an annotation from a resource
     * 
     * @param annotation
     * @param resource - an EmfResource
     * @return success - true if removed, false if not
     * @since 4.3
     */
    public static boolean deleteAnnotation( final Annotation annotation ) {
        ArgCheck.isNotNull(annotation);

        AnnotationContainer ac = (AnnotationContainer)annotation.eContainer();
        boolean successful = false;
        if (ac != null) {
            successful = removeValue(ac, annotation, ac.getAnnotations());
        }

        return successful;
    }

    // -----------------------------------------------------------
    // Diagram Methods
    // -----------------------------------------------------------

    /**
     * This method assumes the user requests a new diagram only The user is reponsible for setting the target of the diagram and
     * adding the diagram to the diagram container correctly
     * 
     * @return Diagram
     */
    public static Diagram createNewDiagram() {
        return diagramFactory.createDiagram();
    }

    /**
     * This method assumes the user requests a new diagram for a target object and the user is responsible for adding the
     * annotation to the diagram container
     * 
     * @param target reference for the diagram
     * @return Diagram
     * @since 4.3
     */
    public static Diagram createNewDiagram( final EObject target ) {
        ArgCheck.isNotNull(target);
        Diagram newDiagram = createNewDiagram();
        newDiagram.setTarget(target);
        return newDiagram;
    }

    /**
     * This method assumes the user requests a new diagram for a target object and the user supplies the diagram container
     * 
     * @param target reference for the diagram
     * @param container
     * @return Diagram
     * @since 4.3
     */
    public static Diagram createNewDiagram( final EObject target,
                                            final DiagramContainer container ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(container);

        Diagram newDiagram = createNewDiagram(target);
        // newDiagram.setDiagramContainer(container);
        // If we don't have a resource, we can't add to the container
        addValue(container, newDiagram, container.getDiagram());

        return newDiagram;
    }

    /**
     * This method assumes the user requests a new annotation for a target object and that the new object exists within an
     * existing resource and that the new annotation should be fully instantiated and added to the annotation container
     * 
     * @param target reference for the diagram
     * @param resource - an EmfResource
     * @return Diagram
     * @since 4.3
     */
    public static Diagram createNewDiagram( final EObject target,
                                            final Resource resource ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        if (supportsDiagrams(resource)) {
            return createNewDiagram(target, getDiagramContainer(resource, true));
        }

        // We need to log something here to indicate that this method was called with the intention of creating a diagram
        // for an eObject in a model that does not support Diagrams.
        String message = getString(DIAGRAMS_NOT_SUPPORTED_KEY, resource);
        ModelerCore.Util.log(IStatus.WARNING, message);

        return null;
    }

    /**
     * Method used to create a new diagram, add it to the
     * 
     * @param target reference for the diagram
     * @param resource
     * @param persistent
     * @return Diagram
     * @since 4.3
     */
    public static Diagram createNewDiagram( final EObject target,
                                            final Resource resource,
                                            final boolean persistent ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        if (supportsDiagrams(resource)) {
            if (persistent) {
                return createNewDiagram(target, resource);
            }
            // Create new diagram and only add it to the transient diagrams in ModelContents
            ModelContents contents = ModelerCore.getModelEditor().getModelContents(target);
            Diagram newDiagram = createNewDiagram(target);
            contents.addTransientDiagram(newDiagram);

            return newDiagram;
        }

        // We need to log something here to indicate that this method was called with the intention of creating a diagram
        // for an eObject in a model that does not support Diagrams.
        String message = getString(DIAGRAMS_NOT_SUPPORTED_KEY, resource);
        ModelerCore.Util.log(IStatus.WARNING, message);

        return null;
    }

    /**
     * @param diagram
     * @param resource
     * @return booolean success value
     * @since 4.3
     */
    public static boolean deleteDiagram( final Diagram diagram ) {

        ArgCheck.isNotNull(diagram);

        DiagramContainer dc = (DiagramContainer)diagram.eContainer();
        boolean successful = false;
        if (dc != null) {
            successful = removeValue(dc, diagram, dc.getDiagram());
        }

        return successful;
    }

    /**
     * This method allows users to change the persistent state of a diagram. It will not create a new diagram, but move the
     * diagram between the DiagramContainer and the ModelContents.transientDiagram list.
     * 
     * @param resource
     * @param diagram
     * @param persistent
     * @throws ModelWorkspaceException
     * @since 4.3
     */
    public static void setDiagramPersistence( final Diagram diagram,
                                              final Resource resource,
                                              final boolean persistent ) {
        ArgCheck.isNotNull(diagram);
        ArgCheck.isNotNull(resource);

        if (persistent == isPersistent(diagram)) {
            return;
        }

        // And add to the resource
        final DiagramContainer container = ModelResourceContainerFactory.getDiagramContainer(resource, true);

        if (persistent) {
            // Either to the actual persistent DiagramContainer. Note that this
            // is done by setting the container on the diagram rather than adding
            // the diagram to the container's getDiagram() list - this is because
            // doing it this way ensures that the resource is notified as
            // having unsaved changes

            if (container != null && !container.getDiagram().contains(diagram)) {
                // Remove from transient diagram list
                ModelContents contents = ModelerCore.getModelEditor().getModelContents(container);
                contents.removeTransientDiagram(diagram);
                // add to persistent list
                diagram.setDiagramContainer(container);
            }
            // Now we should remove from transient diagrams?

        } else {
            if (container != null && container.getDiagram().contains(diagram)) {
                // Remove from persistent state.
                removeValue(container, diagram, container.getDiagram());
                // Add to transient state
                ModelContents contents = ModelerCore.getModelEditor().getModelContents(container);
                contents.addTransientDiagram(diagram);
            }
        }
    }

    /**
     * Simple method that returns the persistence state of a diagram. Namely... if it has an eContainer or not
     * 
     * @param diagram
     * @return is persistent diagram
     * @since 4.3
     */
    public static boolean isPersistent( final Diagram diagram ) {
        ArgCheck.isNotNull(diagram);

        return diagram.eContainer() != null;
    }

    // -----------------------------------------------------------
    // Transformation Methods
    // -----------------------------------------------------------

    /**
     * Method to delete existing transformation from the container within a model resource
     * 
     * @param transformation - TransformationMappingRoot
     * @param resource - Resource
     * @return success - true if deleted, false if not
     * @since 4.3
     */
    public static boolean deleteTransformation( final TransformationMappingRoot transformation ) {
        ArgCheck.isNotNull(transformation);

        TransformationContainer tc = (TransformationContainer)transformation.eContainer();
        boolean successful = false;
        if (tc != null) {
            successful = removeValue(tc, transformation, tc.getTransformationMappings());
        }

        return successful;
    }

    // -----------------------------------------------------------------------------
    // SQL Transformation Mapping Methods
    // -----------------------------------------------------------------------------

    /**
     * This method assumes the user requests a new SqlTransformationMappingRoot only The user is reponsible for setting the target
     * of the SqlTransformationMappingRoot and adding the SqlTransformationMappingRoot to the transformation container correctly
     * 
     * @return Diagram
     */
    public static SqlTransformationMappingRoot createNewSqlTransformationMappingRoot() {
        return transformationFactory.createSqlTransformationMappingRoot();
    }

    /**
     * This method is designed to be used by ALL modeler code to create new sql transformation roots. It will start and use a
     * transaction, if needed and assumes that the target object (i.e. table) exists in a ModelResource (i.e. target.eResource !=
     * null)
     * 
     * @param target
     * @param isSignificant
     * @param txnSource
     * @return
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static SqlTransformationMappingRoot createNewSqlTransformationMappingRoot( final EObject target,
                                                                                      final boolean isSignificant,
                                                                                      final Object txnSource ) {
        ArgCheck.isNotNull(target);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }

        SqlTransformationMappingRoot transMappingRoot = null;

        // Need to wrap these in transaction
        Object source = txnSource;
        // If source is null, make the source the virtual target table, since this new root is being created/carried
        // along for the ride.
        if (source == null) source = target;

        boolean requiresStart = ModelerCore.startTxn(isSignificant, true, "Create Sql Transformation Mapping Root", source); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Now, let's create the t-root
            transMappingRoot = createNewSqlTransformationMappingRoot(target, target.eResource());
            if (transMappingRoot != null) succeeded = true;
        } finally {
            if (requiresStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        return transMappingRoot;
    }

    /**
     * This method allows creating a new SqlTransformationMappingRoot for a specified target. The new root is NOT added to
     * container
     * 
     * @param target reference for new SqlTransformationMappingRoot
     * @return SqlTransformationMappingRoot
     * @since 4.3
     */
    public static SqlTransformationMappingRoot createNewSqlTransformationMappingRoot( final EObject target ) {
        ArgCheck.isNotNull(target);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        SqlTransformationMappingRoot transMappingRoot = null;

        // Let's wrap this in a transaction

        // Now, let's create the t-root
        transMappingRoot = createNewSqlTransformationMappingRoot();
        addValue(transMappingRoot, target, transMappingRoot.getOutputs());
        transMappingRoot.setTarget(target);

        return transMappingRoot;
    }

    /**
     * This method allows creating a new SqlTransformationMappingRoot for a specified target and transformation container.
     * 
     * @param target reference for new SqlTransformationMappingRoot
     * @param container - the TransformationContainer
     * @return SqlTransformationMappingRoot
     * @since 4.3
     */
    public static SqlTransformationMappingRoot createNewSqlTransformationMappingRoot( final EObject target,
                                                                                      final TransformationContainer container ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(container);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        SqlTransformationMappingRoot transMappingRoot = createNewSqlTransformationMappingRoot(target);

        addValue(container, transMappingRoot, container.getTransformationMappings());

        return transMappingRoot;
    }

    /**
     * This is the all-inclusive create new SqlTransformationMappingRoot method. It create a new root, sets the target object
     * reference, adds it to the transformation container.
     * 
     * @param target
     * @param resource - EmfResource
     * @return
     * @since 4.3
     */
    public static SqlTransformationMappingRoot createNewSqlTransformationMappingRoot( final EObject target,
                                                                                      final Resource resource ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        return createNewSqlTransformationMappingRoot(target, getTransformationContainer(resource, true));
    }

    public static boolean isSqlTransformationMappingRoot( Object target ) {
        if (target instanceof SqlTransformationMappingRoot) return true;

        return false;
    }

    // -----------------------------------------------------------------------------
    // XQuery Transformation Mapping Methods
    // -----------------------------------------------------------------------------

    /**
     * This method assumes the user requests a new XQueryTransformationMappingRoot only The user is reponsible for setting the
     * target of the XQueryTransformationMappingRoot and adding the XQueryTransformationMappingRoot to the transformation
     * container correctly
     * 
     * @return
     */
    public static XQueryTransformationMappingRoot createNewXQueryTransformationMappingRoot() {
        return transformationFactory.createXQueryTransformationMappingRoot();
    }

    /**
     * This method is designed to be used by ALL modeler code to create new XQuery transformation roots. It will start and use a
     * transaction, if needed and assumes that the target object (i.e. table) exists in a ModelResource (i.e. target.eResource !=
     * null)
     * 
     * @param target
     * @param isSignificant
     * @param txnSource
     * @return
     * @throws ModelerCoreException
     * @since 5.0.1
     */
    public static XQueryTransformationMappingRoot createNewXQueryTransformationMappingRoot( final EObject target,
                                                                                            final boolean isSignificant,
                                                                                            final Object txnSource ) {
        ArgCheck.isNotNull(target);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }

        XQueryTransformationMappingRoot transMappingRoot = null;

        // Need to wrap these in transaction
        Object source = txnSource;
        // If source is null, make the source the virtual target table, since this new root is being created/carried
        // along for the ride.
        if (source == null) source = target;

        boolean requiresStart = ModelerCore.startTxn(isSignificant, true, "Create XQuery Transformation Mapping Root", source); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Now, let's create the t-root
            transMappingRoot = createNewXQueryTransformationMappingRoot(target, target.eResource());
            if (transMappingRoot != null) succeeded = true;
        } finally {
            if (requiresStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        return transMappingRoot;
    }

    /**
     * This method allows creating a new XQueryTransformationMappingRoot for a specified target. The new root is NOT added to
     * container
     * 
     * @param target reference for new XQueryTransformationMappingRoot
     * @return XQueryTransformationMappingRoot
     * @since 5.0.1
     */
    public static XQueryTransformationMappingRoot createNewXQueryTransformationMappingRoot( final EObject target ) {
        ArgCheck.isNotNull(target);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        XQueryTransformationMappingRoot transMappingRoot = null;

        // Let's wrap this in a transaction

        // Now, let's create the t-root
        transMappingRoot = createNewXQueryTransformationMappingRoot();
        addValue(transMappingRoot, target, transMappingRoot.getOutputs());
        transMappingRoot.setTarget(target);

        return transMappingRoot;
    }

    /**
     * This method allows creating a new XQueryTransformationMappingRoot for a specified target and transformation container.
     * 
     * @param target reference for new XQueryTransformationMappingRoot
     * @param container - the TransformationContainer
     * @return XQueryTransformationMappingRoot
     * @since 5.0.1
     */
    public static XQueryTransformationMappingRoot createNewXQueryTransformationMappingRoot( final EObject target,
                                                                                            final TransformationContainer container ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(container);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        XQueryTransformationMappingRoot transMappingRoot = createNewXQueryTransformationMappingRoot(target);

        addValue(container, transMappingRoot, container.getTransformationMappings());

        return transMappingRoot;
    }

    /**
     * This is the all-inclusive create new XQueryTransformationMappingRoot method. It create a new root, sets the target object
     * reference, adds it to the transformation container.
     * 
     * @param target
     * @param resource - EmfResource
     * @return
     * @since 5.0.1
     */
    public static XQueryTransformationMappingRoot createNewXQueryTransformationMappingRoot( final EObject target,
                                                                                            final Resource resource ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        if (!SqlAspectHelper.isValidSqlTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(SQL_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        return createNewXQueryTransformationMappingRoot(target, getTransformationContainer(resource, true));
    }

    public static boolean isXQueryTransformationMappingRoot( Object target ) {
        if (target instanceof XQueryTransformationMappingRoot) return true;

        return false;
    }

    /**
     * Get the MappingHelper from a SqlTransformationMappingRoot. If one doesn't exist, it is created. This method assumes it will
     * be used within a larger transaction
     * 
     * @param transMappingRoot the transformation mapping root
     * @return the mapping helper
     */
    public static MappingHelper addMappingHelper( Object transMappingRoot ) {
        ArgCheck.isNotNull(transMappingRoot);

        MappingHelper helper = null;
        if (isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get the Mapping Helper from the MappingRoot
            SqlTransformationMappingRoot sqlTransMappingRoot = (SqlTransformationMappingRoot)transMappingRoot;
            helper = sqlTransMappingRoot.getHelper();

            // If helper is null, create one
            if (helper == null) {
                // Create Primary SqlTransformation
                helper = transformationFactory.createSqlTransformation();
                sqlTransMappingRoot.setHelper(helper);
                // Create Nested SqlTransformation for User SQL
                createNestedUserSqlTransformation(helper);
            }
        }
        if (isXQueryTransformationMappingRoot(transMappingRoot)) {
            // Get the Mapping Helper from the MappingRoot
            XQueryTransformationMappingRoot xqueryTransMappingRoot = (XQueryTransformationMappingRoot)transMappingRoot;
            helper = xqueryTransMappingRoot.getHelper();

            // If helper is null, create one
            if (helper == null) {
                // Create Primary XQueryTransformation
                helper = transformationFactory.createXQueryTransformation();
                xqueryTransMappingRoot.setHelper(helper);
            }
        }
        return helper;
    }

    /**
     * This method is designed to be used by ALL modeler code to create/add a MappingHelper to a transformation mapping root.
     * 
     * @param transMappingRoot
     * @param isSignificant
     * @param txnSource
     * @return helper the MappingHelper
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static MappingHelper addMappingHelper( final Object transMappingRoot,
                                                  final boolean isSignificant,
                                                  final Object txnSource ) {
        ArgCheck.isNotNull(transMappingRoot);

        MappingHelper helper = null;

        // Let's wrap this in a transaction

        if (transMappingRoot != null && isSqlTransformationMappingRoot(transMappingRoot)) {
            // Get the Mapping Helper from the MappingRoot
            SqlTransformationMappingRoot sqlTransMappingRoot = (SqlTransformationMappingRoot)transMappingRoot;
            helper = sqlTransMappingRoot.getHelper();

            // If helper is null, create one
            if (helper == null) {
                // Need to wrap these in transaction
                Object source = txnSource;
                // If source is null, make the source the t-root, since this new helper is being created/carried
                // along for the ride.
                if (source == null) source = transMappingRoot;

                boolean requiresStart = ModelerCore.startTxn(false, true, "Create Transformation Mapping Helper", source); //$NON-NLS-1$
                boolean succeeded = false;
                try {
                    addMappingHelper(transMappingRoot);
                    succeeded = true;
                } finally {
                    if (requiresStart) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }
            }

        }

        return helper;
    }

    /**
     * Create a Nested SqlTransformation within the primary SqlTransformation. Purpose of the nested SqlTransformation object is
     * to maintain "user" Sql Strings (non-uuid versions)
     * 
     * @param sqlTransformation the primary SqlTransformation mapping helper
     * @return the created SqlTransformation
     */
    public static SqlTransformation createNestedUserSqlTransformation( MappingHelper sqlTransformation ) {
        ArgCheck.isNotNull(sqlTransformation);

        // Create Nested SqlTransformation for User SQL
        SqlTransformation userSqlTrans = transformationFactory.createSqlTransformation();
        userSqlTrans.setNestedIn(sqlTransformation);

        return userSqlTrans;
    }

    // -----------------------------------------------------------------------------
    // Tree Mapping Root Methods
    // -----------------------------------------------------------------------------

    /**
     * This method assumes the user requests a new SqlTransformationMappingRoot only The user is reponsible for setting the target
     * of the SqlTransformationMappingRoot and adding the SqlTransformationMappingRoot to the transformation container correctly
     * 
     * @return Diagram
     */
    public static TreeMappingRoot createNewTreeMappingRoot() {
        return transformationFactory.createTreeMappingRoot();
    }

    /**
     * This method is designed to be used by ALL modeler code to create new tree mapping roots. It will start and use a
     * transaction, if needed and assumes that the target object exists in a ModelResource (i.e. target.eResource != null)
     * 
     * @param target
     * @param isSignificant
     * @param txnSource
     * @return
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static TreeMappingRoot createNewTreeMappingRoot( final EObject target,
                                                            final boolean isSignificant,
                                                            final Object txnSource ) {
        ArgCheck.isNotNull(target);

        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(TREE_ROOT_TARGET_NOT_VALID_KEY, target));
        }

        TreeMappingRoot treeMappingRoot = null;

        // Need to wrap these in transaction
        Object source = txnSource;
        // If source is null, make the source the virtual target table, since this new root is being created/carried
        // along for the ride.
        if (source == null) source = target;

        boolean requiresStart = ModelerCore.startTxn(false, true, "Create Tree Mapping Root", source); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Now, let's create the t-root
            treeMappingRoot = createNewTreeMappingRoot(target, target.eResource());
            if (treeMappingRoot != null) succeeded = true;
        } finally {
            if (requiresStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        return treeMappingRoot;
    }

    /**
     * This method allows creating a new TreeMappingRoot for a specified target. The new root is NOT added to container
     * 
     * @param target
     * @return
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static TreeMappingRoot createNewTreeMappingRoot( final EObject target ) {
        ArgCheck.isNotNull(target);
        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(TREE_ROOT_TARGET_NOT_VALID_KEY, target));
        }

        // Create the root
        TreeMappingRoot treeMappingRoot = createNewTreeMappingRoot();
        // Set target
        treeMappingRoot.setTarget(target);

        return treeMappingRoot;
    }

    /**
     * This method allows creating a new TreeMappingRoot for a specified target and transformation container.
     * 
     * @param target reference for new TreeMappingRoot
     * @param container - the TransformationContainer
     * @return TreeMappingRoot
     * @since 4.3
     */
    public static TreeMappingRoot createNewTreeMappingRoot( final EObject target,
                                                            final TransformationContainer container ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(container);

        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(TREE_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        // Now, let's create the t-root
        TreeMappingRoot transMappingRoot = createNewTreeMappingRoot(target);

        addValue(container, transMappingRoot, container.getTransformationMappings());

        return transMappingRoot;
    }

    /**
     * This is the all-inclusive create new TreeMappingRoot method. It create a new root, sets the target object reference, adds
     * it to the transformation container.
     * 
     * @param target
     * @param resource - EmfResource
     * @return
     * @since 4.3
     */
    public static TreeMappingRoot createNewTreeMappingRoot( final EObject target,
                                                            final Resource resource ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(TREE_ROOT_TARGET_NOT_VALID_KEY, target));
        }

        return createNewTreeMappingRoot(target, getTransformationContainer(resource, true));
    }

    public static boolean isTreeMappingRoot( Object target ) {
        if (target instanceof TreeMappingRoot) return true;

        return false;
    }

    // -----------------------------------------------------------------------------
    // Tree Mapping Root Methods
    // -----------------------------------------------------------------------------

    /**
     * This method assumes the user requests a new SqlTransformationMappingRoot only The user is reponsible for setting the target
     * of the SqlTransformationMappingRoot and adding the SqlTransformationMappingRoot to the transformation container correctly
     * 
     * @return Diagram
     */
    public static FragmentMappingRoot createNewFragmentMappingRoot() {
        return transformationFactory.createFragmentMappingRoot();
    }

    /**
     * This method is designed to be used by ALL modeler code to create new tree mapping roots.
     * 
     * @param target
     * @param isSignificant
     * @param txnSource
     * @return
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static FragmentMappingRoot createNewFragmentMappingRoot( final EObject target,
                                                                    final boolean isSignificant,
                                                                    final Object txnSource ) {
        ArgCheck.isNotNull(target);
        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(FRAGMENT_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        FragmentMappingRoot fragmentMappingRoot = null;

        // Need to wrap these in transaction
        Object source = txnSource;
        // If source is null, make the source the virtual target table, since this new root is being created/carried
        // along for the ride.
        if (source == null) source = target;

        boolean requiresStart = ModelerCore.startTxn(false, true, "Create Fragment Mapping Root", source); //$NON-NLS-1$
        boolean succeeded = false;
        try {
            // Now, let's create the t-root
            fragmentMappingRoot = createNewFragmentMappingRoot(target, target.eResource());
            if (fragmentMappingRoot != null) succeeded = true;
        } finally {
            if (requiresStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        return fragmentMappingRoot;
    }

    /**
     * This method allows creating a new FragmentMappingRoot for a specified target. The new root is NOT added to container
     * 
     * @param target
     * @return
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static FragmentMappingRoot createNewFragmentMappingRoot( final EObject target ) {
        ArgCheck.isNotNull(target);
        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(FRAGMENT_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        // create the root
        FragmentMappingRoot fragmentMappingRoot = transformationFactory.createFragmentMappingRoot();
        // Set the target
        fragmentMappingRoot.setTarget(target);

        return fragmentMappingRoot;
    }

    /**
     * This method allows creating a new FragmentMappingRoot for a specified target and transformation container.
     * 
     * @param target reference for new FragmentMappingRoot
     * @param container - the TransformationContainer
     * @return FragmentMappingRoot
     */

    public static FragmentMappingRoot createNewFragmentMappingRoot( final EObject target,
                                                                    final TransformationContainer container ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(container);

        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(FRAGMENT_ROOT_TARGET_NOT_VALID_KEY, target));
        }
        // Now, let's create the t-root
        FragmentMappingRoot fragmentMappingRoot = createNewFragmentMappingRoot(target);

        addValue(container, fragmentMappingRoot, container.getTransformationMappings());

        return fragmentMappingRoot;
    }

    /**
     * This is the all-inclusive create new FragmentMappingRoot method. It create a new root, sets the target object reference,
     * adds it to the transformation container.
     * 
     * @param target
     * @param resource - EmfResource
     * @return
     * @since 4.3
     */
    public static FragmentMappingRoot createNewFragmentMappingRoot( final EObject target,
                                                                    final Resource resource ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        if (!SqlAspectHelper.isValidTreeTransformationTarget(target)) {
            throw new IllegalArgumentException(getString(FRAGMENT_ROOT_TARGET_NOT_VALID_KEY, target));
        }

        return createNewFragmentMappingRoot(target, getTransformationContainer(resource, true));
    }

    public static boolean isFragmentMappingRoot( Object target ) {
        if (target instanceof FragmentMappingRoot) return true;

        return false;
    }

    /**
     * This method assumes the user requests a new MappingClassSet only The user is reponsible for setting the target of the
     * MappingClassSet and adding the MappingClassSet to the MappingClassSet container correctly
     * 
     * @return new Annotation
     */
    public static MappingClassSet createNewMappingClassSet() {
        return TransformationFactory.eINSTANCE.createMappingClassSet();
    }

    /**
     * This method assumes the user requests a new MappingClassSet for a target object and the user is responsible for adding the
     * MappingClassSet to the MappingClassSet container
     * 
     * @param target
     * @return new Annotation
     * @since 4.3
     */
    public static MappingClassSet createNewMappingClassSet( final EObject target ) {
        ArgCheck.isNotNull(target);

        if (!SqlAspectHelper.isXmlDocument(target)) {
            throw new IllegalArgumentException(getString(XML_DOCUCUMENT_TARGET_NOT_VALID_KEY, target));
        }

        MappingClassSet newMappingClassSet = createNewMappingClassSet();
        newMappingClassSet.setTarget(target);

        return newMappingClassSet;
    }

    /**
     * This method assumes the user requests a new MappingClassSet for a target object and the user supplies the MappingClassSet
     * container
     * 
     * @param target reference the MappingClassSet
     * @param container the MappingClassSetContainer
     * @return new Annotation
     * @since 4.3
     */
    public static MappingClassSet createNewMappingClassSet( final EObject target,
                                                            MappingClassSetContainer container ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(container);

        if (!SqlAspectHelper.isXmlDocument(target)) {
            throw new IllegalArgumentException(getString(XML_DOCUCUMENT_TARGET_NOT_VALID_KEY, target));
        }

        MappingClassSet mappingClassSet = createNewMappingClassSet(target);
        mappingClassSet.setTarget(target);

        addValue(container, mappingClassSet, container.getMappingClassSets());

        return mappingClassSet;
    }

    /**
     * This method assumes the user requests a new MappingClassSet for a target object and that the new object exists within an
     * existing resource and that the new MappingClassSet should be fully instantiated and added to the MappingClassSet container
     * 
     * @param target reference for MappingClassSet
     * @param modelResource
     * @return new MappingClassSet
     * @since 4.3
     */
    public static MappingClassSet createNewMappingClassSet( final EObject target,
                                                            final Resource resource ) {
        ArgCheck.isNotNull(target);
        ArgCheck.isNotNull(resource);

        MappingClassSet mappingClassSet = null;

        if (supportsAnnotations(resource)) {
            MappingClassSetContainer container = ModelResourceContainerFactory.getMappingClassSetContainer(resource, true);
            if (container != null) {
                mappingClassSet = createNewMappingClassSet(target, container);
            } else {
                // We shouldn't get here. If Resource exists, then we should be creating a container correctly
                String message = getString(NO_MAPPING_CLASS_SET_CONTAINER_ERROR, resource, target);
                ModelerCore.Util.log(IStatus.ERROR, message);
            }
        }

        return mappingClassSet;
    }

    public static boolean deleteMappingClassSet( final MappingClassSet mappingClassSet ) {
        ArgCheck.isNotNull(mappingClassSet);

        MappingClassSetContainer mcsc = (MappingClassSetContainer)mappingClassSet.eContainer();
        boolean successful = false;
        if (mcsc != null) {
            successful = removeValue(mcsc, mappingClassSet, mcsc.getMappingClassSets());
        }

        return successful;
    }

    /**
     * This method is designed to be used by ALL modeler code to create new mapping class set.
     * 
     * @param target
     * @return
     * @throws ModelerCoreException
     * @since 4.3
     */
    public static MappingClassSet getMappingClassSet( final EObject target,
                                                      boolean forceCreate ) {
        ArgCheck.isNotNull(target);

        if (!SqlAspectHelper.isXmlDocument(target)) {
            throw new IllegalArgumentException(getString(XML_DOCUCUMENT_TARGET_NOT_VALID_KEY, target));
        }

        MappingClassSet mappingClassSet = null;

        // Let's wrap this in a transaction

        // Need to do some work here
        Resource resource = target.eResource();
        if (resource != null) {
            MappingClassSetContainer container = getMappingClassSetContainer(resource, true);

            List setList = getMappingClassSets(resource, target);

            if (setList == null || setList.isEmpty()) {
                if (forceCreate) {
                    mappingClassSet = createNewMappingClassSet(target, container);
                }
            } else {
                mappingClassSet = (MappingClassSet)setList.get(0);
            }
        }

        return mappingClassSet;
    }

    /**
     * Get the {@link MappingClassSet} objects associated with the supplied target model object. MappingClassSets are created
     * using the {@link #createNewMappingClassSet(EObject)} method.
     * 
     * @param target the target object; may be null, meaning find all {@link MappingClassSet} instances that have no target
     * @return the {@link MappingClassSet} instances that are associated with the target object; never null, but possibly empty
     */
    public static List getMappingClassSets( final Resource resource,
                                            final EObject target ) {
        MappingClassSetContainer container = getMappingClassSetContainer(resource, true);
        if (container != null) {
            final List result = new ArrayList();
            final Iterator iter = new ArrayList(container.eContents()).iterator();
            while (iter.hasNext()) {
                final MappingClassSet mcSet = (MappingClassSet)iter.next();
                if (mcSet.getTarget() != null && mcSet.getTarget().equals(target)) {
                    result.add(mcSet);
                }
            }
            return result;

        }

        return Collections.EMPTY_LIST;
    }

}
