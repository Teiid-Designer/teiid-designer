/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
