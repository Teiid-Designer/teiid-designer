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

package com.metamatrix.metamodels.relationship.aspects.relationship;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationshipAspectFactoryImpl
 */
public class RelationshipAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case RelationshipPackage.FILE_REFERENCE: return null;
			case RelationshipPackage.ISTATUS: return null;
			case RelationshipPackage.LIST: return null;
			case RelationshipPackage.PLACEHOLDER_REFERENCE: return null;
			case RelationshipPackage.RELATIONSHIP: return createRelationshipAspect(entity);
			case RelationshipPackage.RELATIONSHIP_ROLE: return createRelationshipRoleAspect(entity);
			case RelationshipPackage.RELATIONSHIP_TYPE: return createRelationshipTypeAspect(entity);
			case RelationshipPackage.RELATIONSHIP_ENTITY: return null;
			case RelationshipPackage.URI_REFERENCE: return null;
            default:
                throw new IllegalArgumentException(RelationshipMetamodelPlugin.Util.getString("RelationshipAspectFactoryImpl.Invalid_Classifer_ID,_for_creating_Relationship_Metamodel_Aspect_{0}._1", classifier)); //$NON-NLS-1$
        }
    }

	/**
	 * @return
	 */
	private MetamodelAspect createRelationshipAspect(MetamodelEntity entity) {
		return new RelationAspect(entity);
	}

	/**
	 * @return
	 */
	private MetamodelAspect createRelationshipRoleAspect(MetamodelEntity entity) {
		return new RelationRoleAspect(entity);
	}

	/**
	 * @return
	 */
	private MetamodelAspect createRelationshipTypeAspect(MetamodelEntity entity) {
		return new RelationTypeAspect(entity);
	}

}
