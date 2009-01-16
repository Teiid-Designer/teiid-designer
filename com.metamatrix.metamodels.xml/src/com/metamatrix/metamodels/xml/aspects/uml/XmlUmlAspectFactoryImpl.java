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

package com.metamatrix.metamodels.xml.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.XmlDocumentPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * XmlUmlAspectFactoryImpl
 */
public class XmlUmlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case XmlDocumentPackage.XML_FRAGMENT: return createXmlFragmentAspect(entity);
            case XmlDocumentPackage.XML_DOCUMENT: return createXmlDocumentAspect(entity);
            case XmlDocumentPackage.XML_ELEMENT: return null;
            case XmlDocumentPackage.XML_ATTRIBUTE: return null;
            case XmlDocumentPackage.XML_ROOT: return null;
            case XmlDocumentPackage.XML_COMMENT: return null;
            case XmlDocumentPackage.XML_NAMESPACE: return null;
            case XmlDocumentPackage.XML_SEQUENCE: return null;
            case XmlDocumentPackage.XML_ALL: return null;
            case XmlDocumentPackage.XML_CHOICE: return null;
            case XmlDocumentPackage.PROCESSING_INSTRUCTION: return null;
            case XmlDocumentPackage.XML_FRAGMENT_USE: return null;
            default:
                throw new IllegalArgumentException(XmlDocumentPlugin.Util.getString("XmlUmlAspectFactoryImpl.Invalid_ClassiferID,_,_for_creating_UML_Aspect_1",classifier)); //$NON-NLS-1$
        }
    }

    /**
     * @return
     */
    private MetamodelAspect createXmlDocumentAspect(MetamodelEntity entity) {
        return new XmlDocumentAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createXmlFragmentAspect(MetamodelEntity entity) {
        return new XmlFragmentAspect(entity);
    }

}
