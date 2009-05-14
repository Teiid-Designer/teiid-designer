/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.factory;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.mapping.choice.IChoiceObject;

/**
 * The <code>IChoiceFactory</code> class defines the methods needed to convert
 * a given object into an instance of IChoiceObject.
 */
public interface IChoiceFactory {

    /**
     * Indicates whether or not this factory can handle the given object.
     * @param eobj the object to be converted
     * @return <code>true</code> if the factory can handle this object.
     */
    boolean supports( EObject eobj );

    /**
     * Generates the appropriate IChoiceObject for this input object
     * @param eobj the object to be converted
     * @return an instance of IChoiceObject based on the input object
     */
    IChoiceObject createChoiceObject( EObject eobj );

}
