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

package com.metamatrix.modeler.modelgenerator.processor;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/**
 * NullRelationTracker
 */
public class NullRelationTracker implements RelationTracker {

    public NullRelationTracker() {
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#getGeneratedFrom(org.eclipse.emf.ecore.EObject)
     */
    public EObject getGeneratedFrom(EObject output) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.modelgenerator.RelationTracker#getGeneratedFromAll(org.eclipse.emf.ecore.EObject)
     */
    public List getGeneratedFromAll(EObject output) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.processor.RelationTracker#getGeneratedTo(org.eclipse.emf.ecore.EObject)
     */
    public EObject getGeneratedTo(EObject input) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.processor.RelationTracker#getGeneratedToAll(org.eclipse.emf.ecore.EObject)
     */
    public List getGeneratedToAll(EObject input) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.processor.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject, java.util.List)
     */
    public void recordGeneratedFrom(EObject input, EObject output, List problems) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.processor.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject, java.util.List, java.util.List)
     */
    public void recordGeneratedFrom(EObject input, List outputs, List problems) {

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.modelgenerator.processor.RelationTracker#recordGeneratedFrom(java.util.List, java.util.List, java.util.List)
     */
    public void recordGeneratedFrom(List umlInputs, List outputs, List problems) {

    }

}
