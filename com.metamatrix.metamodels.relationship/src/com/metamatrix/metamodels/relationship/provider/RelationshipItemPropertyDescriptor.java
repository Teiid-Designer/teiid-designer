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

package com.metamatrix.metamodels.relationship.provider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;

import com.metamatrix.metamodels.relationship.Relationship;
import com.metamatrix.metamodels.relationship.RelationshipRole;

/**
 * RelationshipItemPropertyDescriptor
 */
public class RelationshipItemPropertyDescriptor extends ItemPropertyDescriptor {

    private boolean sourceRole;

//    /**
//     * Construct an instance of RelationshipItemPropertyDescriptor.
//     * @param adapterFactory
//     * @param displayName
//     * @param description
//     * @param feature
//     */
//    public RelationshipItemPropertyDescriptor(
//        AdapterFactory adapterFactory,
//        String displayName,
//        String description,
//        EStructuralFeature feature) {
//        super(adapterFactory, displayName, description, feature);
//    }
//
//    /**
//     * Construct an instance of RelationshipItemPropertyDescriptor.
//     * @param adapterFactory
//     * @param displayName
//     * @param description
//     * @param feature
//     * @param isSettable
//     */
//    public RelationshipItemPropertyDescriptor(
//        AdapterFactory adapterFactory,
//        String displayName,
//        String description,
//        EStructuralFeature feature,
//        boolean isSettable) {
//        super(adapterFactory, displayName, description, feature, isSettable);
//    }
//
//    /**
//     * Construct an instance of RelationshipItemPropertyDescriptor.
//     * @param adapterFactory
//     * @param displayName
//     * @param description
//     * @param feature
//     * @param isSettable
//     * @param category
//     */
//    public RelationshipItemPropertyDescriptor(
//        AdapterFactory adapterFactory,
//        String displayName,
//        String description,
//        EStructuralFeature feature,
//        boolean isSettable,
//        String category) {
//        super(adapterFactory, displayName, description, feature, isSettable, category);
//    }
//
//    /**
//     * Construct an instance of RelationshipItemPropertyDescriptor.
//     * @param adapterFactory
//     * @param displayName
//     * @param description
//     * @param parentReferences
//     */
//    public RelationshipItemPropertyDescriptor(
//        AdapterFactory adapterFactory,
//        String displayName,
//        String description,
//        EReference[] parentReferences) {
//        super(adapterFactory, displayName, description, parentReferences);
//    }
//
//    /**
//     * Construct an instance of RelationshipItemPropertyDescriptor.
//     * @param adapterFactory
//     * @param displayName
//     * @param description
//     * @param parentReferences
//     * @param isSettable
//     */
//    public RelationshipItemPropertyDescriptor(
//        AdapterFactory adapterFactory,
//        String displayName,
//        String description,
//        EReference[] parentReferences,
//        boolean isSettable) {
//        super(adapterFactory, displayName, description, parentReferences, isSettable);
//    }
//
    /**
     * Construct an instance of RelationshipItemPropertyDescriptor.
     * @param adapterFactory
     * @param displayName
     * @param description
     * @param feature
     * @param isSettable
     * @param staticImage
     */
    public RelationshipItemPropertyDescriptor(
        AdapterFactory adapterFactory,
        String displayName,
        String description,
        EStructuralFeature feature,
        boolean isSettable,
        Object staticImage,
        boolean sourceRole) {
        super(adapterFactory, displayName, description, feature, isSettable, staticImage);
        this.sourceRole = sourceRole;
    }

//    /**
//     * Construct an instance of RelationshipItemPropertyDescriptor.
//     * @param adapterFactory
//     * @param displayName
//     * @param description
//     * @param feature
//     * @param isSettable
//     * @param staticImage
//     * @param category
//     */
//    public RelationshipItemPropertyDescriptor(
//        AdapterFactory adapterFactory,
//        String displayName,
//        String description,
//        EStructuralFeature feature,
//        boolean isSettable,
//        Object staticImage,
//        String category) {
//        super(adapterFactory, displayName, description, feature, isSettable, staticImage, category);
//    }
    
    /**
     * @see org.eclipse.emf.edit.provider.ItemPropertyDescriptor#getDisplayName(java.lang.Object)
     */
    @Override
    public String getDisplayName(Object object) {
        if ( object instanceof Relationship ) {
            final Relationship relationship = (Relationship)object;
            final RelationshipRole role = this.sourceRole ? 
                                          relationship.getSourceRole() :
                                          relationship.getTargetRole();
            final RelationshipRole otherRole = this.sourceRole ? 
                                          relationship.getTargetRole() :
                                          relationship.getSourceRole();
            final String roleName = role != null ? role.getName() : null;
            final String otherRoleName = otherRole != null ? otherRole.getName() : null;
            if ( roleName != null && roleName.trim().length() != 0 ) {
                // Check whether the role name is the same; can't have two properties that are the same
                if ( !roleName.equals(otherRoleName) ) {
                    return roleName;
                }
            }
        }
        return super.getDisplayName(object);
    }


}
