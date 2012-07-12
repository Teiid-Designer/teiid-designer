/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relationship;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Placeholder Reference Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.relationship.PlaceholderReferenceContainer#getPlaceholders <em>Placeholders</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.relationship.RelationshipPackage#getPlaceholderReferenceContainer()
 * @model
 * @generated
 */
public interface PlaceholderReferenceContainer extends EObject {

    /**
     * Returns the value of the '<em><b>Placeholders</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.relationship.PlaceholderReference}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer
     * <em>Placeholder Reference Container</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Placeholders</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Placeholders</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relationship.RelationshipPackage#getPlaceholderReferenceContainer_Placeholders()
     * @see org.teiid.designer.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer
     * @model type="org.teiid.designer.metamodels.relationship.PlaceholderReference" opposite="PlaceholderReferenceContainer"
     *        containment="true"
     * @generated
     */
    EList getPlaceholders();

} // PlaceholderReferenceContainer
