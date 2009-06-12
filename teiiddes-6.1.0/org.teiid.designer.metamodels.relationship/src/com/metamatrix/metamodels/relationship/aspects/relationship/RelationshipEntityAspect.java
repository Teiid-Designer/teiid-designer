/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.aspects.relationship;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relationship.RelationshipEntity;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationshipEntityAspect
 */
public abstract class RelationshipEntityAspect extends AbstractRelationshipMetamodelAspect {

    protected RelationshipEntityAspect( MetamodelEntity entity ) {
        super(entity);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.relationship.metamodel.aspect.relationship.RelationshipMetamodelAspect#getName(org.eclipse.emf.ecore.EObject)
     */
    public String getName( EObject eObject ) {
        ArgCheck.isInstanceOf(RelationshipEntity.class, eObject);
        RelationshipEntity relationshipEntity = (RelationshipEntity)eObject;
        return relationshipEntity.getName();
    }

}
