/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship.aspects.relationship;

import org.eclipse.emf.ecore.EObject;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.relationship.RelationshipEntity;


/**
 * RelationshipEntityAspect
 */
public abstract class RelationshipEntityAspect extends AbstractRelationshipMetamodelAspect {

    protected RelationshipEntityAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.relationship.metamodel.aspect.relationship.RelationshipMetamodelAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( EObject eObject ) { // NO_UCD
        CoreArgCheck.isInstanceOf(RelationshipEntity.class, eObject);
        RelationshipEntity relationshipEntity = (RelationshipEntity)eObject;
        return relationshipEntity.getName();
    }

}
