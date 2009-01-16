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

package com.metamatrix.metamodels.uml2.util;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLResource.XMLInfo;
import org.eclipse.emf.ecore.xmi.XMLResource.XMLMap;
import org.eclipse.emf.ecore.xmi.impl.XMLInfoImpl;

import org.eclipse.uml2.uml.UMLPackage;

import com.metamatrix.modeler.core.metamodel.ResourceLoadOptionContributor;


/** 
 * @since 4.3
 */
public class Uml2ResourceLoadOptionContributor implements ResourceLoadOptionContributor {

    /** 
     * @see com.metamatrix.modeler.core.metamodel.ResourceLoadOptionContributor#addMappings(org.eclipse.emf.ecore.xmi.XMLResource.XMLMap)
     * @since 4.3
     */
    public void addMappings(XMLMap xmlMap) {
        if (xmlMap != null) {
            XMLResource.XMLInfo info = new XMLInfoImpl();
            info.setXMLRepresentation(XMLInfo.ELEMENT);
            info.setName("literal"); //$NON-NLS-1$
            xmlMap.add(UMLPackage.eINSTANCE.getEnumeration_OwnedLiteral(), info);
        }
    }

}
