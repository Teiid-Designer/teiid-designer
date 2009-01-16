/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.modelgenerator.util;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.metamodels.core.CoreFactory;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.util.ModelResourceContainerFactory;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.modelgenerator.ModelGeneratorPlugin;

/**
 * AnnotationHelperImpl
 */
public class AnnotationHelperImpl implements AnnotationHelper {

    private final ModelSelector selector;

    private ModelContents contents;

    /**
     * Construct an instance of AnnotationHelperImpl.
     *
     */
    public AnnotationHelperImpl(final ModelSelector selector) {
        if (selector == null) {
            throw new IllegalArgumentException(ModelGeneratorPlugin.Util.getString("AnnotationHelperImpl.The_ModelSelector_instance_used_to_try_to_instantiate_a_AnnotationHelperImpl_was_Null._1")); //$NON-NLS-1$
        }

        this.selector = selector;

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.util.AnnotationHelper#createAnnotation(org.eclipse.emf.ecore.EObject, java.lang.String)
     */
    public Annotation createAnnotation(final EObject targetObject, final String objectDescription)
        throws AnnotationHelperException {
        Annotation annotation = ModelResourceContainerFactory.createNewAnnotation(targetObject);
        if( annotation == null ) {
            // Let's just create one?
            annotation = CoreFactory.eINSTANCE.createAnnotation();
        }
        AnnotationContainer ac = getModelContents().getAnnotationContainer(false);
        if( ac != null )
            annotation.setAnnotationContainer(ac);

        annotation.setDescription(objectDescription);
        return annotation;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.util.AnnotationHelper#setAnnotation(org.eclipse.emf.ecore.EObject, java.lang.String, java.lang.Object)
     */
    public void setAnnotation(final EObject targetObject, final String key, final Object value)
        throws AnnotationHelperException {
        ModelContents modelContents = getModelContents();

        Annotation annotation = modelContents.getAnnotation(targetObject);
        // If no annotation exists, create a new one
        if(annotation==null) {
            annotation = ModelResourceContainerFactory.createNewAnnotation(targetObject,
			                                                               ModelResourceContainerFactory.getAnnotationContainer(modelContents.getResource(),
			                                                                                                                    true));
            if(annotation==null)
                annotation = createAnnotation(targetObject, null);
        }
        // Set the Key-Value pair on the annotation Tags
        if(annotation!=null) {
            EMap annotationMap = annotation.getTags();
            annotationMap.put(key, value);
        }
    }

    private ModelContents getModelContents() throws AnnotationHelperException {
        if (contents == null) {

            try {
                contents = selector.getModelContents();
            } catch (ModelWorkspaceException e) {
                throw new AnnotationHelperException(e);
            }

        }
        return contents;
    }

}
