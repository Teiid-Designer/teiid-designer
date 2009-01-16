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
