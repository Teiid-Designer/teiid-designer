/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.choice;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.xml.XmlChoice;
import com.metamatrix.modeler.mapping.choice.IChoiceObject;
import com.metamatrix.modeler.mapping.factory.IChoiceFactory;

/**
 * XmlChoiceFactory  
 */
public class XmlChoiceFactory implements IChoiceFactory {

    XmlChoiceObject xcChoice;

    /**
     * Construct an instance of XmlChoiceFactory.
     * 
     */
    public XmlChoiceFactory() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IChoiceFactory#supports(org.eclipse.emf.ecore.EObject)
     */
    public boolean supports(EObject eobj) {
//        System.out.println("[XmlChoiceFactory.supports]"); //$NON-NLS-1$        return false;
        return ( eobj instanceof XmlChoice );         
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.mapping.factory.IChoiceFactory#createChoiceObject(org.eclipse.emf.ecore.EObject)
     */
    public IChoiceObject createChoiceObject(EObject eobj) {
//        System.out.println("[XmlChoiceFactory.createChoiceObject]"); //$NON-NLS-1$        return false;
        if ( eobj instanceof XmlChoice ) {        
             return new XmlChoiceObject( (XmlChoice)eobj );
        }
        return null;
    }

}
