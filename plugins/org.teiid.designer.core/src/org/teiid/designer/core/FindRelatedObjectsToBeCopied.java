/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.util.ModelVisitor;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassSet;


/**
 * FindRelatedObjectsToBeCopied
 */
public class FindRelatedObjectsToBeCopied implements ModelVisitor {

    private final Set objectsToBeCopied;
    private final Map modelContentsByResource;

    /**
     * Construct an instance of FindRelatedObjectsToBeCopied.
     */
    public FindRelatedObjectsToBeCopied() {
        super();
        this.objectsToBeCopied = new HashSet();
        this.modelContentsByResource = new HashMap();
    }

    public void addModelContents( final ModelContents contents,
                                  final Resource emfResource ) {
        CoreArgCheck.isNotNull(contents);
        CoreArgCheck.isNotNull(emfResource);
        this.modelContentsByResource.put(emfResource, contents);
    }

    public ModelContents getModelContents( final Resource emfResource ) {
        ModelContents result = (ModelContents)this.modelContentsByResource.get(emfResource);
        if (result == null) {
            if (emfResource instanceof EmfResource) {
                result = ((EmfResource)emfResource).getModelContents();
                if (result != null) {
                    this.addModelContents(result, emfResource);
                }
            }
        }
        return result;
    }

    /**
     * @see org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        if (object instanceof Annotation) {
            return true;
        }

        Resource emfResource = object.eResource();
        if (emfResource != null) {
            final ModelContents modelContents = getModelContents(emfResource);
            if (modelContents != null) {

                // Add the annotation (if there is one) ...
                addAnnotation(object, modelContents);

                // Add any transformations (if there are any) ...
                addTransformations(object, modelContents);

                // Add any mappings (if there are any) ...
                addMappings(object, modelContents);

            }
        }
        return true;
    }

    protected void addMappings( final EObject object,
                                final ModelContents modelContents ) {
        // See if this object has a mapping class set.
        final List mappingClassSets = modelContents.getMappingClassSets(object);
        if (mappingClassSets != null && mappingClassSets.size() != 0) {
            this.objectsToBeCopied.addAll(mappingClassSets);

            // See if there are any annotations and transformations for the mapping classes ..
            final Iterator iter = mappingClassSets.iterator();
            while (iter.hasNext()) {
                final MappingClassSet mcSet = (MappingClassSet)iter.next();
                final List mappingClasses = mcSet.getMappingClasses();
                final Iterator mcIterator = mappingClasses.iterator();
                while (mcIterator.hasNext()) {
                    final MappingClass mc = (MappingClass)mcIterator.next();

                    // Add the annotation (if there is one) ...
                    addAnnotation(mc, modelContents);

                    // Add any transformations (if there are any) ...
                    addTransformations(mc, modelContents);
                }
            }
        }
    }

    protected void addTransformations( final EObject object,
                                       final ModelContents modelContents ) {
        // See if this object has a transformation mapping root.
        // Look for transformations that have the object as an *output*
        // (that is, a transformation that defines the object).
        // Typical SQL transformations have the same target and output,
        // while the target for MappingClass transformations is the mapped location
        // in the XML document, and the output is the MappingClass.
        final List transformations = modelContents.getTransformationsForOutput(object);
        if (transformations != null && transformations.size() != 0) {
            this.objectsToBeCopied.addAll(transformations);
        }
    }

    protected void addAnnotation( final EObject object,
                                  final ModelContents modelContents ) {
        // See if this object has an annotation ...
        final Annotation existingAnnotation = modelContents.getAnnotation(object);
        if (existingAnnotation != null) {
            this.objectsToBeCopied.add(existingAnnotation);
        }
    }

    /**
     * @see org.teiid.designer.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     */
    public boolean visit( final Resource resource ) {
        return resource != null;
    }

    public Collection getAdditionalObjects() {
        return this.objectsToBeCopied;
    }

}
