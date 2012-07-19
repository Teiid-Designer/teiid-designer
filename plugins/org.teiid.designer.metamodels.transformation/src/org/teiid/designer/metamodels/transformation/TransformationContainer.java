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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.transformation.TransformationContainer#getTransformationMappings <em>Transformation
 * Mappings</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getTransformationContainer()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface TransformationContainer extends EObject {

    /**
     * Returns the value of the '<em><b>Transformation Mappings</b></em>' containment reference list. The list contents are of
     * type {@link org.teiid.designer.metamodels.transformation.TransformationMappingRoot}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Transformation Mappings</em>' containment reference list isn't clear, there really should be
     * more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Transformation Mappings</em>' containment reference list.
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getTransformationContainer_TransformationMappings()
     * @model type="org.teiid.designer.metamodels.transformation.TransformationMappingRoot" containment="true"
     * @generated
     */
    EList getTransformationMappings();

} // TransformationContainer
