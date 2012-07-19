/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

/**
 * EcoreEStringToStringMapEntryMatcher
 *
 * @since 8.0
 */
public class EcoreEStringToStringMapEntryMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of EcoreEStringToStringMapEntryMatcher.
     * 
     */
    public EcoreEStringToStringMapEntryMatcher() {
        super();
    }
    
    /**
     * @see org.teiid.designer.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    @Override
	public void addMappingsForRoots(final List inputs, final List outputs,
                                    final Mapping mapping, final MappingFactory factory) {
    }
    
    /**
     * @see org.teiid.designer.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    @Override
	public void addMappings( final EReference reference, final List inputs, final List outputs, 
                             final Mapping mapping, final MappingFactory factory) {
        final Map mapEntryByKey = new HashMap();

        // Loop over the inputs and find any of the above objects ...
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl)iter.next();
            final String key = entry.getTypedKey();
            mapEntryByKey.put(key,entry);
        }
        
        // Exit quickly ...
        if ( mapEntryByKey.isEmpty() ) {
            return;
        }
        
        // Loop over the outputs and find matches for any of the above objects ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EStringToStringMapEntryImpl entry = (EStringToStringMapEntryImpl)outputIter.next();
            final String key = entry.getTypedKey();
            final EStringToStringMapEntryImpl inputEntry = (EStringToStringMapEntryImpl)mapEntryByKey.get(key);
            if ( inputEntry != null ) {
                inputs.remove(inputEntry);
                outputIter.remove();
                addMapping(inputEntry,entry,mapping,factory);
            }
        }
    }
    
}
