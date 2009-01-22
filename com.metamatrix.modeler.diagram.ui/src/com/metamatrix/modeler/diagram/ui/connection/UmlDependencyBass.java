/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.connection;

import org.eclipse.emf.ecore.EObject;

/**
 * @author BLaFond
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class UmlDependencyBass extends AbstractBinaryAssociation {


	/**
	 * @param eObj
	 * @param sourceObject
	 * @param targetObject
	 */
	public UmlDependencyBass(EObject eObj, EObject sourceObject, EObject targetObject) {
		super(eObj, sourceObject, targetObject);
		setRelationshipType(TYPE_UML_DEPENDENCY);
	}

}
