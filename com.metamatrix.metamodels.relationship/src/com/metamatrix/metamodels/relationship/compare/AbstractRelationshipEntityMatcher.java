/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.compare;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingFactory;

import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.modeler.core.compare.AbstractEObjectMatcher;

/**
 * AbstractRelationshipEntityMatcher
 */
public abstract class AbstractRelationshipEntityMatcher extends AbstractEObjectMatcher {

    /**
     * Construct an instance of AbstractRelationshipEntityMatcher.
     * 
     */
    public AbstractRelationshipEntityMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappingsForRoots(java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappingsForRoots(final List inputs, final List outputs, 
                                    final Mapping mapping, final MappingFactory factory) {
        addMappings(null,inputs,outputs,mapping,factory);
    }

    protected String getInputKey( final RelationshipEntity entity ) {
        return entity.getName();
    }

    protected String getOutputKey( final RelationshipEntity entity ) {
        return entity.getName();
    }
    
    protected abstract boolean isRelationshipEntity( final EObject obj );

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcher#addMappings(org.eclipse.emf.ecore.EReference, java.util.List, java.util.List, org.eclipse.emf.mapping.Mapping, org.eclipse.emf.mapping.MappingFactory)
     */
    public void addMappings( final EReference reference, final List inputs, final List outputs,
                             final Mapping mapping, final MappingFactory factory) {
        // Loop over the inputs and accumulate the UUIDs ...
        final Map inputByName = new HashMap();
        final Iterator iter = inputs.iterator();
        while (iter.hasNext()) {
            final EObject obj = (EObject)iter.next();
            if ( isRelationshipEntity(obj) ) {
                final RelationshipEntity entity = (RelationshipEntity)obj;
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
            if ( isRelationshipEntity(output) ) {
                final RelationshipEntity outputEntity = (RelationshipEntity)output;
                final Object key = this.getOutputKey(outputEntity);
                if ( key != null ) {
                    final List inputEntities = (List) inputByName.get(key);
                    if ( inputEntities != null ) {
                        final int outputClassifierId = output.eClass().getClassifierID();
                        final Iterator inputIter = inputEntities.iterator();
                        while (inputIter.hasNext()) {
                            final RelationshipEntity inputEntity = (RelationshipEntity)inputIter.next();
                            if ( inputEntity.eClass().getClassifierID() == outputClassifierId ) {
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

}
