/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;
import com.metamatrix.metamodels.core.ModelImport;

/**
 * CoreModelImportMatcher
 */
public class CoreModelImportMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of CoreAnnotationMatcher.
     * 
     */
    public CoreModelImportMatcher() {
        super();
    }
    
    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(final List inputs, final List outputs,
                                    final Mapping mapping, final MappingFactory factory) {
    }
    
    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings( final EReference reference, final List inputs, final List outputs, 
                             final Mapping mapping, final MappingFactory factory) {
        final Map inputModelImportsByUuid = new HashMap();

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final ModelImport modelImport = (ModelImport)iter.next();
            final String importedUuid = modelImport.getUuid();
            inputModelImportsByUuid.put(importedUuid,modelImport);
        }
        
        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final ModelImport modelImport = (ModelImport)outputIter.next();
            final String importedUuid = modelImport.getUuid();
            final ModelImport inputModelImport = (ModelImport)inputModelImportsByUuid.get(importedUuid);
            if ( inputModelImport != null ) {
                outputIter.remove();
                inputs.remove(inputModelImport);
                addMapping(inputModelImport,modelImport,mapping,factory);
            }
        }
    }
    
}
