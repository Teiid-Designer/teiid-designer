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

package com.metamatrix.metamodels.xml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Buildable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlBuildable#getBuildState <em>Build State</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlBuildable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface XmlBuildable extends EObject {
    /**
     * Returns the value of the '<em><b>Build State</b></em>' attribute.
     * The default value is <code>"COMPLETE"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.xml.BuildStatus}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Build State</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Build State</em>' attribute.
     * @see com.metamatrix.metamodels.xml.BuildStatus
     * @see #setBuildState(BuildStatus)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlBuildable_BuildState()
     * @model default="COMPLETE"
     * @generated
     */
    BuildStatus getBuildState();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlBuildable#getBuildState <em>Build State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Build State</em>' attribute.
     * @see com.metamatrix.metamodels.xml.BuildStatus
     * @see #getBuildState()
     * @generated
     */
    void setBuildState(BuildStatus value);

} // XmlBuildable
