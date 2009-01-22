/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlGeneralization;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UmlGeneralizationBass extends AbstractBinaryAssociation {

	/**
	 * @param eObj
	 */
	public UmlGeneralizationBass(EObject eObj) {
		super(eObj, false);
		setRelationshipType(TYPE_UML_GENERALIZATION);
		// we need to set the source and target objects based on UmlGeneralization Aspect
		
		setTargetEObject(((UmlGeneralization)getUmlAspect()).getGeneral(eObj));
		setSourceEObject(((UmlGeneralization)getUmlAspect()).getSpecific(eObj));
	}

	/**
	 * @param eObj
	 * @param sourceObject
	 * @param targetObject
	 */
	public UmlGeneralizationBass(EObject eObj, EObject sourceObject, EObject targetObject) {
		super(eObj, sourceObject, targetObject);
		setRelationshipType(TYPE_UML_GENERALIZATION);
	}

}
