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

package com.metamatrix.modeler.internal.xml.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * XmlSqlAspectFactory
 */
public class XmlSqlAspectFactory implements MetamodelAspectFactory {

    /**
     * Construct an instance of XmlSqlAspectFactory.
     * 
     */
    public XmlSqlAspectFactory() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(final EClassifier classifier, final MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case XmlDocumentPackage.XML_ELEMENT:        return new XmlElementSqlAspect(entity);
            case XmlDocumentPackage.XML_ATTRIBUTE:      return new XmlAttributeSqlAspect(entity);
            case XmlDocumentPackage.XML_ALL:            return new XmlAllSqlAspect(entity);
            case XmlDocumentPackage.XML_CHOICE:         return new XmlChoiceSqlAspect(entity);
            case XmlDocumentPackage.XML_SEQUENCE:       return new XmlSequenceSqlAspect(entity);
            case XmlDocumentPackage.XML_DOCUMENT:       return new XmlDocumentSqlAspect(entity);
            case XmlDocumentPackage.XML_ROOT:           return new XmlElementSqlAspect(entity);
            case XmlDocumentPackage.XML_FRAGMENT_USE:   return new XmlFragmentUseSqlAspect(entity);
            default:
                return null;
        }
    }

}
