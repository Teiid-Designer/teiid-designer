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
 * A representation of the model object '<em><b>Procedure Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.ProcedureResult#getProcedure <em>Procedure</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedureResult()
 * @model
 * @generated
 */
public interface ProcedureResult extends ColumnSet{
    /**
     * Returns the value of the '<em><b>Procedure</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Procedure#getResult <em>Result</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Procedure</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Procedure</em>' container reference.
     * @see #setProcedure(Procedure)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedureResult_Procedure()
     * @see com.metamatrix.metamodels.relational.Procedure#getResult
     * @model opposite="result"
     * @generated
     */
    Procedure getProcedure();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.ProcedureResult#getProcedure <em>Procedure</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Procedure</em>' container reference.
     * @see #getProcedure()
     * @generated
     */
    void setProcedure(Procedure value);

} // ProcedureResult
