/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
