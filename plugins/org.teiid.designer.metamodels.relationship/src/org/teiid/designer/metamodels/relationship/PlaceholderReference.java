/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Placeholder Reference</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer <em>Placeholder
 * Reference Container</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.relationship.RelationshipPackage#getPlaceholderReference()
 * @model abstract="true"
 * @generated
 */
public interface PlaceholderReference extends EObject {

    /**
     * Returns the value of the '<em><b>Placeholder Reference Container</b></em>' container reference. It is bidirectional and its
     * opposite is '{@link org.teiid.designer.metamodels.relationship.PlaceholderReferenceContainer#getPlaceholders
     * <em>Placeholders</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Placeholder Reference Container</em>' container reference isn't clear, there really should be
     * more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Placeholder Reference Container</em>' container reference.
     * @see #setPlaceholderReferenceContainer(PlaceholderReferenceContainer)
     * @see org.teiid.designer.metamodels.relationship.RelationshipPackage#getPlaceholderReference_PlaceholderReferenceContainer()
     * @see org.teiid.designer.metamodels.relationship.PlaceholderReferenceContainer#getPlaceholders
     * @model opposite="placeholders"
     * @generated
     */
    PlaceholderReferenceContainer getPlaceholderReferenceContainer();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer
     * <em>Placeholder Reference Container</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Placeholder Reference Container</em>' container reference.
     * @see #getPlaceholderReferenceContainer()
     * @generated
     */
    void setPlaceholderReferenceContainer( PlaceholderReferenceContainer value );

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @model parameters=""
     * @generated
     */
    String getDisplayableName(); // NO_UCD

} // PlaceholderReference
