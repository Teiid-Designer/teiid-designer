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

package com.metamatrix.metamodels.relationship.aspects.validation.rules;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipRole;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.metamodels.relationship.util.RelationshipUtil;
import com.metamatrix.modeler.core.util.ModelVisitorProcessor;
import com.metamatrix.modeler.core.validation.rules.StringNameRule;

/**
 * RelationshipStringNameRule
 */
public class RelationshipStringNameRule extends StringNameRule {

    /**
     * Construct an instance of RelationshipStringNameRule.
     * @param invalidChars
     * @param featureID
     */
    public RelationshipStringNameRule(char[] invalidChars, int featureID) {
        super(invalidChars, featureID);
    }

    /**
     * Construct an instance of RelationshipStringNameRule.
     * @param featureID
     */
    public RelationshipStringNameRule(int featureID) {
        super(featureID);
    }
    
    /**
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#validateCharacters()
     */
    @Override
    protected boolean validateCharacters() {
        return false;
    }

    
    /**
     * This method groups siblings into the following domains, and chooses only those siblings that are in
     * the same domain as the supplied object.
     * <ul>
     *  <li>{@link Relationship} instance</li>
     *  <li>{@link RelationshipType} instance</li>
     *  <li>{@link RelationshipRole} instance</li> 
     * </ul>
     * @see com.metamatrix.modeler.core.validation.rules.StringNameRule#getSiblingsForUniquenessCheck(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public List getSiblingsForUniquenessCheck(final EObject eObject) {
        Object parent = eObject.eContainer();
        if ( parent == null ) {
            parent = eObject.eResource();
        }
        if ( eObject instanceof Relationship ) {
            return RelationshipUtil.findRelationships(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if ( eObject instanceof RelationshipType ) {
            return RelationshipUtil.findRelationshipTypes(parent, ModelVisitorProcessor.DEPTH_ONE);
        } else if ( eObject instanceof RelationshipRole ) {
			return RelationshipUtil.findRelationshipRoles(parent, ModelVisitorProcessor.DEPTH_ONE);
		}

        return super.getSiblingsForUniquenessCheck(eObject);
    }

}
