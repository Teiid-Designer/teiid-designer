/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.relationship;

import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.metamodel.aspect.AbstractMetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.core.metamodel.aspect.relationship.RelationshipMetamodelAspect;


/**
 * AbstractRelationshipMetamodelAspect.java
 */
public abstract class AbstractRelationshipMetamodelAspect extends AbstractMetamodelAspect implements RelationshipMetamodelAspect {

	public static final String ASPECT_ID = ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.ID;

	protected AbstractRelationshipMetamodelAspect(MetamodelEntity entity) {
		super.setMetamodelEntity(entity);
		super.setID(ASPECT_ID);
	}
}
