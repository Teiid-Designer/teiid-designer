/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xml.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;


/**
 * XmlSqlAspectFactory
 *
 * @since 8.0
 */
public class XmlSqlAspectFactory implements MetamodelAspectFactory {

    /**
     * @see org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, org.teiid.designer.core.metamodel.aspect.MetamodelEntity)
     */
    @Override
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
