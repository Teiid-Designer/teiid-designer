/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.mapping.MappingHelper;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Difference Descriptor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.compare.DifferenceDescriptor#getType <em>Type</em>}</li>
 *   <li>{@link org.teiid.designer.compare.DifferenceDescriptor#isSkip <em>Skip</em>}</li>
 *   <li>{@link org.teiid.designer.compare.DifferenceDescriptor#getPropertyDifferences <em>Property Differences</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.compare.ComparePackage#getDifferenceDescriptor()
 * @model
 * @generated
 */
public interface DifferenceDescriptor extends MappingHelper{
    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * The literals are from the enumeration {@link org.teiid.designer.compare.DifferenceType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see org.teiid.designer.compare.DifferenceType
     * @see #setType(DifferenceType)
     * @see org.teiid.designer.compare.ComparePackage#getDifferenceDescriptor_Type()
     * @model
     * @generated
     */
    DifferenceType getType();

    /**
     * Sets the value of the '{@link org.teiid.designer.compare.DifferenceDescriptor#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see org.teiid.designer.compare.DifferenceType
     * @see #getType()
     * @generated
     */
    void setType(DifferenceType value);

    /**
     * Returns the value of the '<em><b>Skip</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Skip</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Skip</em>' attribute.
     * @see #setSkip(boolean)
     * @see org.teiid.designer.compare.ComparePackage#getDifferenceDescriptor_Skip()
     * @model default="false"
     * @generated
     */
    boolean isSkip();

    /**
     * Sets the value of the '{@link org.teiid.designer.compare.DifferenceDescriptor#isSkip <em>Skip</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Skip</em>' attribute.
     * @see #isSkip()
     * @generated
     */
    void setSkip(boolean value);

    /**
     * Returns the value of the '<em><b>Property Differences</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.compare.PropertyDifference}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.compare.PropertyDifference#getDescriptor <em>Descriptor</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Property Differences</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Property Differences</em>' containment reference list.
     * @see org.teiid.designer.compare.ComparePackage#getDifferenceDescriptor_PropertyDifferences()
     * @see org.teiid.designer.compare.PropertyDifference#getDescriptor
     * @model type="org.teiid.designer.compare.PropertyDifference" opposite="descriptor" containment="true"
     * @generated
     */
    EList getPropertyDifferences();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean isDeletion();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean isAddition();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean isChanged();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean isChangedBelow();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    boolean isNoChange();

} // DifferenceDescriptor
