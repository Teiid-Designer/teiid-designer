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

package com.metamatrix.modeler.core.metamodel.aspect.relationship;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;

/**
 * RelationshipMetamodelAspect
 */
public interface RelationshipMetamodelAspect extends MetamodelAspect {

	/**
	 * Returns true if the implementation of this RelationshipMetamodelAspect represents
	 * the specified record type.
	 * @param recordType one of {@link com.metamatrix.modeler.core.index.IndexConstants.RECORD_TYPE}
	 * @return
	 */
	boolean isRecordType(char recordType);

	/**
	 * Get the ObjectID of the metamodel entity.
	 * @param eObject The <code>EObject</code> for which object ID is obtained 
	 * @return ObjectID of the metamodel entity.
	 */
	Object getObjectID(EObject eObject);

	/**
	 * Get the ObjectID of the parent metamodel entity.  The parent
	 * entity may be the actual eContainer for the specified EObject or
	 * may represent a logical parent within the model.
	 * @param eObject The <code>EObject</code> for which the parent's object ID is obtained 
	 * @return ObjectID of the parent entity.
	 */
	Object getParentObjectID(EObject eObject);

	/**
	 * Get the relative path within the model including the model name.
	 * @param eObject The <code>EObject</code> for which path is obtained 
	 * @return short name of the table
	 */
	IPath getPath(EObject eObject);

}
