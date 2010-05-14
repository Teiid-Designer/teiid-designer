/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * A {@link ModelVisitor} implementation that records the number of instances of each {@link EClass primary metamodel EClass} and
 * the number of {@link Resource} instances. For a {@link ModelVisitor} that records the number of all instances of each
 * {@link EClass}, see {@link com.metamatrix.modeler.core.util.ModelStatisticsVisitor}.
 * <p>
 * This class is not thread safe.
 * </p>
 * <p>
 * For usage information, see {@link ModelVisitor}
 * </p>
 */
public class PrimaryMetamodelStatisticsVisitor extends ModelStatisticsVisitor {

    private final Map resourceToPrimaryMetamodelUri;
    private static final String NO_PACKAGE_FOUND_URI = ""; //$NON-NLS-1$

    /**
     * Construct an instance of PrimaryMetamodelModelStatisticsVisitor.
     */
    public PrimaryMetamodelStatisticsVisitor() {
        super();
        this.resourceToPrimaryMetamodelUri = new HashMap();
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelStatisticsVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public boolean visit( final EObject object ) {
        // see if object is instance of a metaclass in the model's primary metamodel
        if (object != null) {
            final Resource resource = object.eResource();
            if (resource != null) {
                String primaryMetamodelUri = (String)this.resourceToPrimaryMetamodelUri.get(resource);
                if (primaryMetamodelUri == null) {
                    if (resource instanceof EmfResource) {
                        final ModelAnnotation modelAnn = ((EmfResource)resource).getModelAnnotation();
                        if (modelAnn != null) {
                            primaryMetamodelUri = modelAnn.getPrimaryMetamodelUri();
                        }
                    }
                    if (primaryMetamodelUri == null) {
                        // Metamodel was not found, so use the constant
                        primaryMetamodelUri = NO_PACKAGE_FOUND_URI;
                    }
                    this.resourceToPrimaryMetamodelUri.put(resource, primaryMetamodelUri);
                }

                // See if the primary metamodel matches the object's metamodel ...
                if (primaryMetamodelUri != NO_PACKAGE_FOUND_URI) {
                    final String metamodelUri = object.eClass().getEPackage().getNsURI();
                    if (!primaryMetamodelUri.equals(metamodelUri)) {
                        return true; // skip this object, but continue down
                    }
                }
            }
        }
        return super.visit(object);
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelStatisticsVisitor#clear()
     */
    @Override
    public void clear() {
        super.clear();
        this.resourceToPrimaryMetamodelUri.clear();
    }

}
