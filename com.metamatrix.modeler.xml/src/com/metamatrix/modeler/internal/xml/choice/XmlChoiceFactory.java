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
