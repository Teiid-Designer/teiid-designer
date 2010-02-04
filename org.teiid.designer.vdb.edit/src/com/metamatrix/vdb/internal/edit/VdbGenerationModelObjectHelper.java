/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.internal.edit;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.vdb.edit.VdbGenerationContext;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.Severity;


/**
 * @since 4.2
 */
public class VdbGenerationModelObjectHelper implements VdbGenerationContext.ModelObjectHelper {

    // Map ObjectID to ProblemMarker[]
    final Map problemsByObjectId;

    public VdbGenerationModelObjectHelper(final Map problemsByObjectId) {
        ArgCheck.isNotNull(problemsByObjectId);
        this.problemsByObjectId = problemsByObjectId;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#getUuid(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getUuid(EObject objectInModel) {
        ArgCheck.isNotNull(objectInModel);
        return ModelerCore.getObjectIdString(objectInModel);
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#getDescription(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public String getDescription(EObject objectInModel) {
        ArgCheck.isNotNull(objectInModel);
        final Annotation existingAnnotation = getExistingAnnotation(objectInModel);
        if ( existingAnnotation != null ) {
            return existingAnnotation.getDescription();
        }
        return null;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#hasErrors(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean hasErrors(EObject objectInModel) {
        if (objectInModel != null && this.problemsByObjectId.containsKey(ModelerCore.getObjectId(objectInModel))) {
			Collection problems = (Collection)this.problemsByObjectId.get(ModelerCore.getObjectId(objectInModel));
            for (final Iterator iter = problems.iterator(); iter.hasNext();) {
                final ProblemMarker marker = (ProblemMarker)iter.next();
                if (marker.getSeverity() == Severity.ERROR_LITERAL) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#hasWarnings(org.eclipse.emf.ecore.EObject)
     * @since 4.3
     */
    public boolean hasWarnings(EObject objectInModel) {
        if (objectInModel != null && this.problemsByObjectId.containsKey(ModelerCore.getObjectId(objectInModel))) {
			Collection problems = (Collection)this.problemsByObjectId.get(ModelerCore.getObjectId(objectInModel));
            for (final Iterator iter = problems.iterator(); iter.hasNext();) {
                final ProblemMarker marker = (ProblemMarker)iter.next();
                if (marker.getSeverity() == Severity.WARNING_LITERAL) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.vdb.edit.VdbGenerationContext.ModelObjectHelper#getProperties(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public Properties getProperties(EObject objectInModel) {
        ArgCheck.isNotNull(objectInModel);
        final Properties properties = new Properties();
        final Annotation existingAnnotation = getExistingAnnotation(objectInModel);
        if ( existingAnnotation != null ) {
            final EMap props = existingAnnotation.getTags();
            final Iterator iter = props.iterator();
            while (iter.hasNext()) {
                final Map.Entry entry = (Map.Entry)iter.next();
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                properties.put(key, value);
            }
        }
        return properties;
    }

    protected Annotation getExistingAnnotation(EObject objectInModel) {
        final Resource resource = objectInModel.eResource();
        if ( resource instanceof EmfResource ) {
            final EmfResource emfResource = (EmfResource) resource;
            final ModelContents contents = emfResource.getModelContents();
            if ( contents != null ) {
                return contents.getAnnotation(objectInModel);
            }
        }

        // Don't know what type of model it is, so return null ...
        return null;
    }

}
