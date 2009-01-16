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

package com.metamatrix.metamodels.relationship.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationshipValidationAspectFactoryImpl.java
 */
public class RelationshipValidationAspectFactoryImpl
	implements MetamodelAspectFactory {

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
	 */
	public MetamodelAspect create(EClassifier classifier,	MetamodelEntity entity) {
		switch (classifier.getClassifierID()) {
			case RelationshipPackage.FILE_REFERENCE:
			case RelationshipPackage.ISTATUS:
			case RelationshipPackage.LIST:
			case RelationshipPackage.PLACEHOLDER_REFERENCE:
			case RelationshipPackage.URI_REFERENCE:
			case RelationshipPackage.RELATIONSHIP_ENTITY: return null;
			case RelationshipPackage.RELATIONSHIP: return createRelationshipAspect(entity);
			case RelationshipPackage.RELATIONSHIP_ROLE: return createRelationshipRoleAspect(entity);
			case RelationshipPackage.RELATIONSHIP_TYPE: return createRelationshipTypeAspect(entity);


			default:
				throw new IllegalArgumentException(RelationshipMetamodelPlugin.Util.getString("RelationshipAspectFactoryImpl.Invalid_Classifer_ID,_for_creating_Relationship_Metamodel_Aspect_{0}._1", classifier)); //$NON-NLS-1$
		}
	}

	/**
	 * @return
	 */
	private MetamodelAspect createRelationshipAspect(MetamodelEntity entity) {
		return new RelationshipAspect(entity);
	}

	/**
	 * @return
	 */
	private MetamodelAspect createRelationshipRoleAspect(MetamodelEntity entity) {
		return new RelationshipRoleAspect(entity);
	}

	/**
	 * @return
	 */
	private MetamodelAspect createRelationshipTypeAspect(MetamodelEntity entity) {
		return new RelationshipTypeAspect(entity);
	}

}
