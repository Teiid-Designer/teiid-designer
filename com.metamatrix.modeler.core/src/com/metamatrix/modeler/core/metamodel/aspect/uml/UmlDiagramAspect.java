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

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;

/**
 * UmlDiagramAspect
 */
public interface UmlDiagramAspect extends MetamodelAspect {
    //Visibility Constants
    public final static int VISIBILITY_PUBLIC       = 100;
    public final static int VISIBILITY_PROTECTED    = 101;
    public final static int VISIBILITY_PRIVATE      = 102;
    public final static int VISIBILITY_DEFAULT      = 103;

    /**
     * Get the Image for the give eObject
     * @param eObject
     * @return the Image for the eobject
     */
    Object getImage(Object eObject);

    /**
     * Return the Visibility constant for the given eObject
     * @param eObject
     * @return the visibility int constant
     */
    int getVisibility(Object eObject);

    /**
     * Return the Sterotype string for the given eObject
     * @param eObject
     * @return the Sterotype string
     */
    String getStereotype(Object eObject);

    /**
     * Return the Signature string for the given eObject
     * @param eObject
     * @param showMask the mask for which attributes constitue the signature
     * @return the Signature string using the mask
     */
    String getSignature(Object eObject, int showMask);

    /**
     * Return the editable portion of the signature string for the given eObject
     * @param eObject
     * @return the editable portion of the signature string 
     */
    String getEditableSignature(Object eObject);

    /**
     * Set the Signature string for the given eObject
     * @param eObject
     * @param newSignature
     * @return an IStatus object with the results of the set operation
     */
    IStatus setSignature(Object eObject, String newSignature);
}
