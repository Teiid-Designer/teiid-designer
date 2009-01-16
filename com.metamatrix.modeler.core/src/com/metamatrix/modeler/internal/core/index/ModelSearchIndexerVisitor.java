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

package com.metamatrix.modeler.internal.core.index;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
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
        ArgCheck.isNotNull(eObject);
        ArgCheck.isNotNull(wordEntries);

        // add all search words for the EObject
        SearchRuntimeAdapter.addObjectSearchWords(eObject, modelPath, wordEntries);
    }

}
