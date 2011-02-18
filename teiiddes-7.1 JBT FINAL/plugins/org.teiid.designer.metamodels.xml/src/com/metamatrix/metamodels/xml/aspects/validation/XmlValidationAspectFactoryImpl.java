/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
