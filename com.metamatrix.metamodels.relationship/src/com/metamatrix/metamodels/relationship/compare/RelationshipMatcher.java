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

package com.metamatrix.metamodels.relationship.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;
import com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher;


/**
 * RelationshipMatcher
 */
public class RelationshipMatcher extends AbstractEObjectMatcher implements TwoPhaseEObjectMatcher {

    /**
     * Construct an instance of RelationshipMatcher.
     * 
     */
    public RelationshipMatcher() {
        super();
    }

    protected String getInputKey( final Relationship entity ) {
        return entity.getName();
    }

    protected String getOutputKey( final Relationship entity ) {
        return entity.getName();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(List inputs, List outputs, Map inputsToOutputs, Mapping mapping, MappingFactory factory) {
        addMappings(null,inputs,outputs,inputsToOutputs,mapping,factory);
    }

    /**
     * @see com.metamatrix.modeler.core.compare.TwoPhaseEObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, java.util.Map, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings(EReference reference, List inputs, List outputs, Map inputsToOutputs, Mapping mapping, MappingFactory factory) {
        // Loop over the inputs and accumulate the UUIDs ...
        final Map inputByName = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            if ( obj instanceof Relationship ) {
                final Relationship entity = (Relationship)obj;
                final Object key = this.getInputKey(entity);
                if ( key != null ) {
                    List list = (List) inputByName.get(key);
                    if ( list == null ) {
                        list = new LinkedList();
                        inputByName.put(key,list);
                    }
                    list.add(obj);
                }
            }
        }
        
        // Loop over the outputs and compare the names ...
        final Iterator outputIter = outputs.iterator();
        while (outputIter.hasNext()) {
            final EObject output = (EObject)outputIter.next();
            if ( output instanceof Relationship ) {
                final Relationship outputEntity = (Relationship)output;
                final Object key = this.getOutputKey(outputEntity);
                if ( key != null ) {
                    final List inputEntities = (List) inputByName.get(key);
                    if ( inputEntities != null ) {
                        final Iterator inputIter = inputEntities.iterator();
                        while (inputIter.hasNext()) {
                            final Relationship inputEntity = (Relationship)inputIter.next();
                            final boolean match = isMatch(inputEntity,outputEntity,inputsToOutputs);
                            if ( match ) {
                                inputIter.remove();
                                inputs.remove(inputEntity);
                                outputIter.remove();
                                addMapping(inputEntity,outputEntity,mapping,factory);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param inputEntity
     * @param outputEntity
     * @param inputsToOutputs
     * @return
     */
    protected boolean isMatch(final Relationship inputEntity, final Relationship outputEntity, final Map inputsToOutputs) {
        // The names should already be match, so check the sources and targets ...
        
        // The number of sources have to be the same ...
        final List inputSources = inputEntity.getSources();
        final List outputSources = outputEntity.getSources();
        if ( inputSources.size() != outputSources.size() ) {
            return false;
        }
        
        // The number of sources have to be the same ...
        final List inputTargets = inputEntity.getTargets();
        final List outputTargets = outputEntity.getTargets();
        if ( inputTargets.size() != outputTargets.size() ) {
            return false;
        }
        
        // The sources have to match ...
        final boolean sourcesMatch = isMatch(inputSources,outputSources,inputsToOutputs);
        if ( !sourcesMatch ) {
            return false;
        }
        
        // The targets have to match ...
        final boolean targetsMatch = isMatch(inputSources,outputSources,inputsToOutputs);
        if ( !targetsMatch ) {
            return false;
        }

        return true;
    }
    
    protected boolean isMatch( final List inputParticipants, final List outputParticipants, final Map inputsToOutputs) {
        if ( inputParticipants.size() != outputParticipants.size() ) {
            return false;
        }

        final Iterator inputIter = inputParticipants.iterator();
        final Iterator outputIter = outputParticipants.iterator();
        while (inputIter.hasNext()) {
            final EObject inputParticipant = (EObject)inputIter.next();
            final EObject outputParticipant = (EObject)outputIter.next();
            if ( inputParticipant == null ) {
                if ( outputParticipant == null ) {
                    continue;   // both are null, so okay this far
                }
                return false;   // outputParticipant != null
            }
            if ( outputParticipant == null ) {
                return false;   // inputParticipant != null
            }
            // Otherwise, both non-null
            if ( inputParticipant.equals(outputParticipant) ) {
                continue;       // okay so far
            }
            // Not an exact match, so see if there is a mapping ...
            final EObject mappedInput = (EObject)inputsToOutputs.get(inputParticipant);
            if ( mappedInput != null ) {
                if ( mappedInput.equals(outputParticipant) ) {
                    continue;       // okay so far
                }
            }
            
            //Still no EXACT match compare name and eclass of participants
            final String inputParticipantName = getNameAndEClass(inputParticipant);
            final String outputParticipantName = getNameAndEClass(outputParticipant);
            if(inputParticipantName.equals(outputParticipantName) ) {
                continue;
            }
            // No mapping, so return false
            return false;
        }
        return true;
    }
    
    public String getNameAndEClass(EObject eobj) {
        final StringBuffer result = new StringBuffer();
        if(eobj == null) {
            return result.toString();
        }
        
        EStructuralFeature nameFeature = eobj.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
        if(nameFeature == null) {
            return result.toString();
        }
        
        result.append(eobj.eClass().getName() + "."); //$NON-NLS-1$
        result.append(eobj.eGet(nameFeature) );
        
        return result.toString();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(List inputs, List outputs, Mapping mapping, MappingFactory factory) {
        // Do nothing in phase 1
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings(EReference reference, List inputs, List outputs, Mapping mapping, MappingFactory factory) {
        // Do nothing in phase 1
    }

}
