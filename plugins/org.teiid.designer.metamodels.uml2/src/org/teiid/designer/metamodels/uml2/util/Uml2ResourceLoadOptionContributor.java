/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.uml2.util;

import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.XMLResource.XMLInfo;
import org.eclipse.emf.ecore.xmi.XMLResource.XMLMap;
import org.eclipse.emf.ecore.xmi.impl.XMLInfoImpl;
import org.eclipse.uml2.uml.UMLPackage;
import org.teiid.designer.core.metamodel.ResourceLoadOptionContributor;


/** 
 * @since 4.3
 */
public class Uml2ResourceLoadOptionContributor implements ResourceLoadOptionContributor {

    /** 
     * @see org.teiid.designer.core.metamodel.ResourceLoadOptionContributor#addMappings(org.eclipse.emf.ecore.xmi.XMLResource.XMLMap)
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
