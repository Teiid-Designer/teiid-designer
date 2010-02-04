/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.selector;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.util.ModelContents;

/**
 * ModelContentsModelHelper
 */
public class ModelContentsModelHelper implements ModelHelper {
    
    private final ModelContents contents;

    /**
     * Construct an instance of ModelContentsModelHelper.
     * 
     */
    public ModelContentsModelHelper( final ModelContents contents ) {
        super();
        ArgCheck.isNotNull(contents);
        this.contents = contents;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelHelper#getAnnotation(org.eclipse.emf.ecore.EObject)
     */
    public Annotation getAnnotation( final EObject eObject) {
        return contents.getAnnotation(eObject);
    }

}
