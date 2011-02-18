/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.internal.core.search.runtime.SearchRuntimeAdapter;

/**
 * @since 4.2
 */
public class ModelSearchIndexerVisitor implements ModelVisitor {

    final String modelPath;
    final List wordEntries;

    /**
     * @param modelPath
     * @param wordEntries
     * @since 4.2
     */
    public ModelSearchIndexerVisitor( final String modelPath,
                                      final List wordEntries ) {
        super();
        this.modelPath = modelPath;
        this.wordEntries = wordEntries;
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     * @since 4.2
     */
    public boolean visit( final EObject object ) {
        if (object == null) {
            return false;
        }
        this.addIndexWord(object, modelPath, wordEntries);
        return true;
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     * @since 4.2
     */
    public boolean visit( final Resource resource ) {
        if (resource == null) {
            return false;
        }
        return true;
    }

    /**
     * Create a {@link com.metamatrix.internal.core.index.impl.WordEntry} instance representing a EObject within a resource. This
     * resulting WordEntry is of the form: uuid|metamodelURI#EClass|modelPath|
     * 
     * @see com.metamatrix.modeler.internal.core.index.ModelIndexer#addIndexWord(org.eclipse.emf.ecore.EObject, java.lang.String,
     *      java.util.List)
     */
    protected void addIndexWord( final EObject eObject,
                                 final String modelPath,
                                 final List wordEntries ) {
        CoreArgCheck.isNotNull(eObject);
        CoreArgCheck.isNotNull(wordEntries);

        // add all search words for the EObject
        SearchRuntimeAdapter.addObjectSearchWords(eObject, modelPath, wordEntries);
    }

}
