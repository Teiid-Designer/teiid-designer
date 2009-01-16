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

package com.metamatrix.modeler.core.metamodel.aspect.uml;

import java.util.List;

import org.eclipse.emf.ecore.EObject;


/**
 * UmlOperation - An operation is owned by a class and may be invoked in the context 
 * of objects that are instances of that class. It is a typed element and a multiplicity 
 * element.
 */
public interface UmlOperation extends UmlDiagramAspect {
    
    //Show Mask Constants    
    public final static int SIGNATURE_NAME = 1;
    public final static int SIGNATURE_STEROTYPE = 2;
    public final static int SIGNATURE_RETURN = 4;
    public final static int SIGNATURE_PARAMETERS = 8;  
      
    /**
     * Returns the value of the '<em><b>Class </b></em>' container 
     * reference - the class that owns the operation.
     * @param eObject
     * @return the value of the '<em>Class </em>' container reference.
     */
    EObject getOwnerClass(Object eObject);

    /**
     * Returns the value of the '<em><b>Owned Parameter</b></em>' containment 
     * reference list - the parameters to the operation.
     * @param eObject
     * @return the value of the '<em>Owned Parameter</em>' containment 
     * reference list.
     */
    List getOwnedParameters(Object eObject);

    /**
     * Return the list of exceptions that are declared as possible during an 
     * invocation of the operation.
     * @param eObject
     * @return the value of the exceptions 
     */
    List getRaisedExceptions(Object eObject);

    /**
     * Get the return result of the operation, if present.
     * @param eObject
     * @return the type
     */
    Object getType(Object eObject);
    
}
