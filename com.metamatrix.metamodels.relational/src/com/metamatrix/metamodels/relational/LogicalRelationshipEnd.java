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

package com.metamatrix.metamodels.relational;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Logical Relationship End</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getMultiplicity <em>Multiplicity</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getTable <em>Table</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getRelationship <em>Relationship</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getLogicalRelationshipEnd()
 * @model
 * @generated
 */
public interface LogicalRelationshipEnd extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Multiplicity</b></em>' attribute.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.relational.MultiplicityKind}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Multiplicity</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Multiplicity</em>' attribute.
     * @see com.metamatrix.metamodels.relational.MultiplicityKind
     * @see #setMultiplicity(MultiplicityKind)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getLogicalRelationshipEnd_Multiplicity()
     * @model
     * @generated
     */
    MultiplicityKind getMultiplicity();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getMultiplicity <em>Multiplicity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Multiplicity</em>' attribute.
     * @see com.metamatrix.metamodels.relational.MultiplicityKind
     * @see #getMultiplicity()
     * @generated
     */
    void setMultiplicity(MultiplicityKind value);

    /**
     * Returns the value of the '<em><b>Table</b></em>' reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Table#getLogicalRelationships <em>Logical Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Table</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Table</em>' reference.
     * @see #setTable(Table)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getLogicalRelationshipEnd_Table()
     * @see com.metamatrix.metamodels.relational.Table#getLogicalRelationships
     * @model opposite="logicalRelationships" required="true"
     * @generated
     */
    Table getTable();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getTable <em>Table</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Table</em>' reference.
     * @see #getTable()
     * @generated
     */
    void setTable(Table value);

    /**
     * Returns the value of the '<em><b>Relationship</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.LogicalRelationship#getEnds <em>Ends</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Relationship</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Relationship</em>' container reference.
     * @see #setRelationship(LogicalRelationship)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getLogicalRelationshipEnd_Relationship()
     * @see com.metamatrix.metamodels.relational.LogicalRelationship#getEnds
     * @model opposite="ends" required="true"
     * @generated
     */
    LogicalRelationship getRelationship();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getRelationship <em>Relationship</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Relationship</em>' container reference.
     * @see #getRelationship()
     * @generated
     */
    void setRelationship(LogicalRelationship value);

} // LogicalRelationshipEnd
