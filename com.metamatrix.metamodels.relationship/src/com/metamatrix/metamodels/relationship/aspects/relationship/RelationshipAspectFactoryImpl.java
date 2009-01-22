/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
