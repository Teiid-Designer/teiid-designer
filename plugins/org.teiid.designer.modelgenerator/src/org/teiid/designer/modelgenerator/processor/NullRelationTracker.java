/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.processor;

import java.util.Collections;
import java.util.List;
import org.eclipse.emf.ecore.EObject;

/**
 * NullRelationTracker
 *
 * @since 8.0
 */
public class NullRelationTracker implements RelationTracker {

    public NullRelationTracker() {
    }

    /**
     * @See org.teiid.designer.modelgenerator.RelationTracker#getGeneratedFrom(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getGeneratedFrom(EObject output) {
        return null;
    }

    /**
     * @See org.teiid.designer.modelgenerator.RelationTracker#getGeneratedFromAll(org.eclipse.emf.ecore.EObject)
     */
    public List getGeneratedFromAll(EObject output) {
        return Collections.EMPTY_LIST;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.processor.RelationTracker#getGeneratedTo(org.eclipse.emf.ecore.EObject)
     */
    @Override
	public EObject getGeneratedTo(EObject input) {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.processor.RelationTracker#getGeneratedToAll(org.eclipse.emf.ecore.EObject)
     */
    public List getGeneratedToAll(EObject input) {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.processor.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject, java.util.List)
     */
    @Override
	public void recordGeneratedFrom(EObject input, EObject output, List problems) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.processor.RelationTracker#recordGeneratedFrom(org.eclipse.emf.ecore.EObject, java.util.List, java.util.List)
     */
    @Override
	public void recordGeneratedFrom(EObject input, List outputs, List problems) {

    }

    /* (non-Javadoc)
     * @See org.teiid.designer.modelgenerator.processor.RelationTracker#recordGeneratedFrom(java.util.List, java.util.List, java.util.List)
     */
    @Override
	public void recordGeneratedFrom(List umlInputs, List outputs, List problems) {

    }

}
