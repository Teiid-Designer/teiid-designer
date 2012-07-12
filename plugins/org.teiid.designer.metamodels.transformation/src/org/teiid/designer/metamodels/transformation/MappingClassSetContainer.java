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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Mapping Class Set Container</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.transformation.MappingClassSetContainer#getMappingClassSets <em>Mapping Class Sets</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getMappingClassSetContainer()
 * @model
 * @generated
 */
public interface MappingClassSetContainer extends EObject {

    /**
     * Returns the value of the '<em><b>Mapping Class Sets</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.transformation.MappingClassSet}. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Mapping Class Sets</em>' containment reference list isn't clear, there really should be more of
     * a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Mapping Class Sets</em>' containment reference list.
     * @see org.teiid.designer.metamodels.transformation.TransformationPackage#getMappingClassSetContainer_MappingClassSets()
     * @model type="org.teiid.designer.metamodels.transformation.MappingClassSet" containment="true"
     * @generated
     */
    EList getMappingClassSets();

} // MappingClassSetContainer
