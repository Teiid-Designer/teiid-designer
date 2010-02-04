/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;

/**
 * RelationTrackerImpl
 */
public class TransientRelationTrackerImpl implements RelationTracker {
    
    public static final int UNABLE_TO_ADD_RELATIONSHIP_CODE = 20001;

    private Map inputToOutputMap;
    
    private Map outputToInputMap;
    
    /**
     * Construct an instance of RelationTrackerImpl.
     * 
     */
    public TransientRelationTrackerImpl() {
        super();
        this.inputToOutputMap = new HashMap();
        this.outputToInputMap = new HashMap();
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)
     */
    public void recordGeneratedFrom(final EObject input, final EObject output, final List problems) {
        doMapping(input, output);
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject, java.util.List)
     */
    public void recordGeneratedFrom(final EObject input, final List outputs, final List problems) {
        if(outputs.size()>0){
            doMapping(input, (EObject)outputs.get(0));
        }
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#recordGeneratedFrom(java.util.List, java.util.List)
     */
    public void recordGeneratedFrom(final List inputs, final List outputs, final List problems) {
        /*
         * the top two objects in each of the lists will be 'directly' related to one another.
         */
        if (inputs.size() > 0 && outputs.size() > 0) {
            doMapping((EObject)inputs.get(0), (EObject)outputs.get(0));
        }
    }
    
    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#getGeneratedFrom(org.eclipse.emf.ecore.EObject)
     */
    public EObject getGeneratedFrom(EObject output) {
        return (EObject)outputToInputMap.get(output);
    }

    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.processor.RelationTracker#getGeneratedTo(org.eclipse.emf.ecore.EObject)
     */
    public EObject getGeneratedTo(EObject input) {
        return (EObject)inputToOutputMap.get(input);
    }
    
    protected void doMapping(EObject input, EObject output){
        inputToOutputMap.put(input, output);
        outputToInputMap.put(output, input);   
    }

}
