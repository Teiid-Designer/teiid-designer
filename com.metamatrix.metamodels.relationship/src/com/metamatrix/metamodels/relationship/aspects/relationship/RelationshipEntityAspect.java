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
