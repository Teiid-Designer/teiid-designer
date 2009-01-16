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
