/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Difference</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.compare.PropertyDifference#getNewValue <em>New Value</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.PropertyDifference#getOldValue <em>Old Value</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.PropertyDifference#isSkip <em>Skip</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.PropertyDifference#getAffectedFeature <em>Affected Feature</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.PropertyDifference#getDescriptor <em>Descriptor</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.modeler.compare.ComparePackage#getPropertyDifference()
 * @model
 * @generated
 */
public interface PropertyDifference extends EObject{
    /**
     * Returns the value of the '<em><b>New Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>New Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>New Value</em>' attribute.
     * @see #setNewValue(Object)
     * @see com.metamatrix.modeler.compare.ComparePackage#getPropertyDifference_NewValue()
     * @model dataType="com.metamatrix.modeler.compare.AnyType"
     * @generated
     */
    Object getNewValue();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.PropertyDifference#getNewValue <em>New Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>New Value</em>' attribute.
     * @see #getNewValue()
     * @generated
     */
    void setNewValue(Object value);

    /**
     * Returns the value of the '<em><b>Old Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Old Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Old Value</em>' attribute.
     * @see #setOldValue(Object)
     * @see com.metamatrix.modeler.compare.ComparePackage#getPropertyDifference_OldValue()
     * @model dataType="com.metamatrix.modeler.compare.AnyType"
     * @generated
     */
    Object getOldValue();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.PropertyDifference#getOldValue <em>Old Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Old Value</em>' attribute.
     * @see #getOldValue()
     * @generated
     */
    void setOldValue(Object value);

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
     * @see com.metamatrix.modeler.compare.ComparePackage#getPropertyDifference_Skip()
     * @model default="false"
     * @generated
     */
    boolean isSkip();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.PropertyDifference#isSkip <em>Skip</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Skip</em>' attribute.
     * @see #isSkip()
     * @generated
     */
    void setSkip(boolean value);

    /**
     * Returns the value of the '<em><b>Affected Feature</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Affected Feature</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Affected Feature</em>' reference.
     * @see #setAffectedFeature(EStructuralFeature)
     * @see com.metamatrix.modeler.compare.ComparePackage#getPropertyDifference_AffectedFeature()
     * @model required="true"
     * @generated
     */
    EStructuralFeature getAffectedFeature();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.PropertyDifference#getAffectedFeature <em>Affected Feature</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Affected Feature</em>' reference.
     * @see #getAffectedFeature()
     * @generated
     */
    void setAffectedFeature(EStructuralFeature value);

    /**
     * Returns the value of the '<em><b>Descriptor</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.compare.DifferenceDescriptor#getPropertyDifferences <em>Property Differences</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Descriptor</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Descriptor</em>' container reference.
     * @see #setDescriptor(DifferenceDescriptor)
     * @see com.metamatrix.modeler.compare.ComparePackage#getPropertyDifference_Descriptor()
     * @see com.metamatrix.modeler.compare.DifferenceDescriptor#getPropertyDifferences
     * @model opposite="propertyDifferences" required="true"
     * @generated
     */
    DifferenceDescriptor getDescriptor();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.compare.PropertyDifference#getDescriptor <em>Descriptor</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Descriptor</em>' container reference.
     * @see #getDescriptor()
     * @generated
     */
    void setDescriptor(DifferenceDescriptor value);

} // PropertyDifference
