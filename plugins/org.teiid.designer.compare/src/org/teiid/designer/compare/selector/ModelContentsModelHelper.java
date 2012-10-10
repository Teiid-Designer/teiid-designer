/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.selector;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.metamodels.core.Annotation;


/**
 * ModelContentsModelHelper
 *
 * @since 8.0
 */
public class ModelContentsModelHelper implements ModelHelper {
    
    private final ModelContents contents;

    /**
     * Construct an instance of ModelContentsModelHelper.
     * 
     */
    public ModelContentsModelHelper( final ModelContents contents ) {
        super();
        CoreArgCheck.isNotNull(contents);
        this.contents = contents;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelHelper#getAnnotation(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public Annotation getAnnotation( final EObject eObject) {
        return contents.getAnnotation(eObject);
    }

}
