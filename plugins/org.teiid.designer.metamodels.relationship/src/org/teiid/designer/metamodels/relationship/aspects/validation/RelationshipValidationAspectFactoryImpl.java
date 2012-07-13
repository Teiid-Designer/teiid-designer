/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.relationship.RelationshipMetamodelPlugin;
import org.teiid.designer.metamodels.relationship.RelationshipPackage;


/**
 * RelationshipValidationAspectFactoryImpl.java
 */
public class RelationshipValidationAspectFactoryImpl
	implements MetamodelAspectFactory {

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, org.teiid.designer.core.metamodel.aspect.MetamodelEntity)
	 */
	@Override
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
