/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.choice;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.mapping.choice.IChoiceObject;
import org.teiid.designer.mapping.factory.IChoiceFactory;
import org.teiid.designer.metamodels.xml.XmlChoice;


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
     * @See org.teiid.designer.mapping.factory.IChoiceFactory#supports(org.eclipse.emf.ecore.EObject)
     */
    public boolean supports(EObject eobj) {
//        System.out.println("[XmlChoiceFactory.supports]"); //$NON-NLS-1$        return false;
        return ( eobj instanceof XmlChoice );         
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.mapping.factory.IChoiceFactory#createChoiceObject(org.eclipse.emf.ecore.EObject)
     */
    public IChoiceObject createChoiceObject(EObject eobj) {
//        System.out.println("[XmlChoiceFactory.createChoiceObject]"); //$NON-NLS-1$        return false;
        if ( eobj instanceof XmlChoice ) {        
             return new XmlChoiceObject( (XmlChoice)eobj );
        }
        return null;
    }

}
