/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspect;
import org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory;
import org.teiid.designer.core.metamodel.aspect.MetamodelEntity;
import org.teiid.designer.metamodels.xml.XmlDocumentPackage;


/**
 * RelationalValidationAspectFactoryImpl
 */
public class XmlValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @See org.teiid.designer.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, org.teiid.designer.core.metamodel.aspect.MetamodelEntity)
     */
    @Override
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
