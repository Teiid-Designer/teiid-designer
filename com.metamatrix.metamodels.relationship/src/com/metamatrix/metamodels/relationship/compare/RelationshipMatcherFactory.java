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

package com.metamatrix.metamodels.relationship.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.metamatrix.metamodels.relationship.RelationshipPackage;
import com.metamatrix.modeler.core.compare.EObjectMatcher;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;

/**
 * RelationshipMatcherFactory
 */
public class RelationshipMatcherFactory implements EObjectMatcherFactory {

    private EObjectMatcher relationshipMatcher;
    private EObjectMatcher relationshipTypeMatcher;
    private EObjectMatcher relationshipFolderMatcher;
    private EObjectMatcher relationshipRoleMatcher;
    private final List allMatchersList;

    /**
     * Construct an instance of RelationshipMatcherFactory.
     * 
     */
    public RelationshipMatcherFactory() {
        super();
        this.relationshipMatcher = new RelationshipMatcher();
        this.relationshipTypeMatcher = new RelationshipTypeMatcher();
        this.relationshipFolderMatcher = new RelationshipFolderMatcher();
        this.relationshipRoleMatcher = new RelationshipRoleMatcher();
        this.allMatchersList = new ArrayList();
        this.allMatchersList.add(this.relationshipMatcher);
        this.allMatchersList.add(this.relationshipTypeMatcher);
        this.allMatchersList.add(this.relationshipFolderMatcher);
        this.allMatchersList.add(this.relationshipRoleMatcher);
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchersForRoots()
     */
    public List createEObjectMatchersForRoots() {
        return allMatchersList;
    }

    /**
     * @see com.metamatrix.modeler.core.compare.EObjectMatcherFactory#createEObjectMatchers(org.eclipse.emf.ecore.EReference)
     */
    public List createEObjectMatchers(final EReference reference) {
        // Make sure the reference is in the Relational metamodel ...
        final EClass containingClass = reference.getEContainingClass();
        final EPackage metamodel = containingClass.getEPackage();
        if ( !RelationshipPackage.eINSTANCE.equals(metamodel) ) {
            // The feature isn't in the relational metamodel so return nothing ...
            return Collections.EMPTY_LIST;
        }
        
        // See if the feature matches any we care about ...
        final int featureId = reference.getFeatureID();
        final List result = new ArrayList(4);
        switch(featureId) {
            case RelationshipPackage.RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS:
            case RelationshipPackage.RELATIONSHIP__OWNED_RELATIONSHIPS:
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS:
                result.add( this.relationshipMatcher );
                break;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES:
                result.add( this.relationshipTypeMatcher );
                break;
            case RelationshipPackage.RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS:
                result.add( this.relationshipFolderMatcher );
                break;
            case RelationshipPackage.RELATIONSHIP_TYPE__ROLES:
                result.add( this.relationshipRoleMatcher );
                break;
        }
        return result;
    }

}
