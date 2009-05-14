/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.compare;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relationship.RelationshipType;


/**
 * RelationshipTypeMatcher
 */
public class RelationshipTypeMatcher extends AbstractRelationshipEntityMatcher {

    /**
     * Construct an instance of RelationshipTypeMatcher.
     * 
     */
    public RelationshipTypeMatcher() {
        super();
    }

    /**
     * @see com.metamatrix.metamodels.relationship.compare.AbstractRelationshipEntityMatcher#isRelationshipEntity(org.eclipse.emf.ecore.EObject)
     */
    @Override
    protected boolean isRelationshipEntity(final EObject obj) {
        return obj instanceof RelationshipType;
    }
}
