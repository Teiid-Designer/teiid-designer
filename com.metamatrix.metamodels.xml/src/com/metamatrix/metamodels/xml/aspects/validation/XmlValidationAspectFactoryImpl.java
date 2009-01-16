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

package com.metamatrix.metamodels.xml.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationalValidationAspectFactoryImpl
 */
public class XmlValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
            switch (classifier.getClassifierID()) {
                case XmlDocumentPackage.XML_NAMESPACE: return new XmlNamespaceAspect(entity);
				case XmlDocumentPackage.XML_DOCUMENT:  return new XmlDocumentAspect(entity);
				case XmlDocumentPackage.XML_ATTRIBUTE:
				case XmlDocumentPackage.XML_ELEMENT:
				case XmlDocumentPackage.XML_BASE_ELEMENT:
				case XmlDocumentPackage.XML_ROOT:				    
				case XmlDocumentPackage.XML_DOCUMENT_NODE:  return new XmlDocumentNodeAspect(entity);
				case XmlDocumentPackage.XML_CHOICE:
				case XmlDocumentPackage.XML_ALL:
				case XmlDocumentPackage.XML_SEQUENCE: return new XmlContainerNodeAspect(entity);				    
				default:
					return null;
            }
    }
}
