/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Placeholder Reference Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer#getPlaceholders <em>Placeholders</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getPlaceholderReferenceContainer()
 * @model
 * @generated
 */
public interface PlaceholderReferenceContainer extends EObject {

    /**
     * Returns the value of the '<em><b>Placeholders</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.relationship.PlaceholderReference}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer
     * <em>Placeholder Reference Container</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Placeholders</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Placeholders</em>' containment reference list.
     * @see com.metamatrix.metamodels.relationship.RelationshipPackage#getPlaceholderReferenceContainer_Placeholders()
     * @see com.metamatrix.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer
     * @model type="com.metamatrix.metamodels.relationship.PlaceholderReference" opposite="PlaceholderReferenceContainer"
     *        containment="true"
     * @generated
     */
    EList getPlaceholders();

} // PlaceholderReferenceContainer
