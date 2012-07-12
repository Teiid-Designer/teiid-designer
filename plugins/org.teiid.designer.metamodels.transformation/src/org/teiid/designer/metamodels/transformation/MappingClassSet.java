/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.transformation;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Mapping Class Set</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.transformation.MappingClassSet#getMappingClasses <em>Mapping Classes</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.transformation.MappingClassSet#getTarget <em>Target</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.transformation.MappingClassSet#getInputBinding <em>Input Binding</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getMappingClassSet()
 * @model
 * @generated
 */
public interface MappingClassSet extends EObject {

    /**
     * Returns the value of the '<em><b>Mapping Classes</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.transformation.MappingClass}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.transformation.MappingClass#getMappingClassSet <em>Mapping Class Set</em>}'. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Classes</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Mapping Classes</em>' containment reference list.
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getMappingClassSet_MappingClasses()
     * @see org.teiid.designer.metamodels.transformation.MappingClass#getMappingClassSet
     * @model type="org.teiid.designer.metamodels.transformation.MappingClass" opposite="mappingClassSet" containment="true"
     * @generated
     */
    EList getMappingClasses();

    /**
     * Returns the value of the '<em><b>Target</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Target</em>' reference list isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Target</em>' reference.
     * @see #setTarget(EObject)
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getMappingClassSet_Target()
     * @model
     * @generated
     */
    EObject getTarget();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.transformation.MappingClassSet#getTarget <em>Target</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Target</em>' reference.
     * @see #getTarget()
     * @generated
     */
    void setTarget( EObject value );

    /**
     * Returns the value of the '<em><b>Input Binding</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.transformation.InputBinding}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.transformation.InputBinding#getMappingClassSet <em>Mapping Class Set</em>}'. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Binding</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Input Binding</em>' containment reference list.
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getMappingClassSet_InputBinding()
     * @see org.teiid.designer.metamodels.transformation.InputBinding#getMappingClassSet
     * @model type="org.teiid.designer.metamodels.transformation.InputBinding" opposite="mappingClassSet" containment="true"
     * @generated
     */
    EList getInputBinding();

} // MappingClassSet
