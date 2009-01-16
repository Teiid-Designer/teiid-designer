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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Container Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlContainerHolder#getContainers <em>Containers</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerHolder()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface XmlContainerHolder extends EObject{
    /**
     * Returns the value of the '<em><b>Containers</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.xml.XmlContainerNode}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Containers</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Containers</em>' containment reference list.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerHolder_Containers()
     * @see com.metamatrix.metamodels.xml.XmlContainerNode#getParent
     * @model type="com.metamatrix.metamodels.xml.XmlContainerNode" opposite="parent" containment="true"
     * @generated
     */
    EList getContainers();

} // XmlContainerHolder
