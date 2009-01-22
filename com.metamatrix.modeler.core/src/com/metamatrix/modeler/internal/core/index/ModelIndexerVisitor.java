/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.util.Collection;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.index.IndexingContext;
import com.metamatrix.modeler.core.util.ModelVisitor;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;

/**
 * @since 4.2
 */
public class ModelIndexerVisitor implements ModelVisitor {

    final String modelPath;
    final Collection wordEntries;
    final IndexingContext context;

    /**
     * @param modelPath
     * @param wordEntries
     * @param context
     * @since 4.2
     */
    public ModelIndexerVisitor( final String modelPath,
                                final Collection wordEntries,
                                final IndexingContext context ) {
        super();
        this.modelPath = modelPath;
        this.wordEntries = wordEntries;
        this.context = context;
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
     * Create the {@link com.metamatrix.internal.core.index.impl.WordEntry} instance(s) to be used as the index file record(s) for
     * this EObject instance. The word entries are added to the list provided by the calling method.
     * 
     * @param EObject
     * @param modelPath path to the model within the workspace
     * @param wordEntries the list to which WordEntry instances are added
     * @return
     */
    protected void addIndexWord( final EObject eObject,
                                 final String modelPath,
                                 final Collection wordEntries ) {
        RuntimeAdapter.addIndexWord(eObject, context, modelPath, wordEntries, false);
    }

}
