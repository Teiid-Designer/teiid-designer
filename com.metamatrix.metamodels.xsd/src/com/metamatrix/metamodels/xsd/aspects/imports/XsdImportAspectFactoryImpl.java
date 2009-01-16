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

package com.metamatrix.metamodels.xsd.aspects.imports;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.xsd.XSDPackage;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * XsdAspectFactoryImpl
 */
public class XsdImportAspectFactoryImpl implements MetamodelAspectFactory {

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
	 */
	public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
		switch (classifier.getClassifierID()) {
			case XSDPackage.XSD_ANNOTATION: return null;
			case XSDPackage.XSD_ATTRIBUTE_DECLARATION: return null;
			case XSDPackage.XSD_ATTRIBUTE_GROUP_DEFINITION: return null;
			case XSDPackage.XSD_ATTRIBUTE_USE: return null;
			case XSDPackage.XSD_BOUNDED_FACET: return null;
			case XSDPackage.XSD_CARDINALITY_FACET: return null;
			case XSDPackage.XSD_COMPLEX_TYPE_DEFINITION: return null;
			case XSDPackage.XSD_DIAGNOSTIC: return null;
			case XSDPackage.XSD_ELEMENT_DECLARATION: return null;
			case XSDPackage.XSD_ENUMERATION_FACET: return null;
			case XSDPackage.XSD_FRACTION_DIGITS_FACET: return null;
			case XSDPackage.XSD_IDENTITY_CONSTRAINT_DEFINITION: return null;
			case XSDPackage.XSD_IMPORT: return new XsdSchemaDerivativeAspect(entity);
			case XSDPackage.XSD_INCLUDE: return new XsdSchemaDerivativeAspect(entity);
			case XSDPackage.XSD_LENGTH_FACET: return null;
			case XSDPackage.XSD_MAX_EXCLUSIVE_FACET: return null;
			case XSDPackage.XSD_MAX_INCLUSIVE_FACET: return null;
			case XSDPackage.XSD_MAX_LENGTH_FACET: return null;
			case XSDPackage.XSD_MIN_EXCLUSIVE_FACET: return null;
			case XSDPackage.XSD_MIN_INCLUSIVE_FACET: return null;
			case XSDPackage.XSD_MIN_LENGTH_FACET: return null;
			case XSDPackage.XSD_MODEL_GROUP: return null;
			case XSDPackage.XSD_MODEL_GROUP_DEFINITION: return null;
			case XSDPackage.XSD_NOTATION_DECLARATION: return null;
			case XSDPackage.XSD_NUMERIC_FACET: return null;
			case XSDPackage.XSD_ORDERED_FACET: return null;
			case XSDPackage.XSD_PARTICLE: return null;
			case XSDPackage.XSD_PATTERN_FACET: return null;
			case XSDPackage.XSD_REDEFINE: return new XsdSchemaDerivativeAspect(entity);
			case XSDPackage.XSD_SCHEMA: return null;
			case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION: return null;
			case XSDPackage.XSD_TOTAL_DIGITS_FACET: return null;
			case XSDPackage.XSD_WHITE_SPACE_FACET: return null;
			case XSDPackage.XSD_WILDCARD: return null;
			case XSDPackage.XSD_XPATH_DEFINITION: return null;
			default:
				throw new IllegalArgumentException(ModelerCore.Util.getString("XsdImportAspectFactoryImpl.Invalid_ClassiferID,_{0},_for_creating_Import_Aspect_1", classifier)); //$NON-NLS-1$
		}
	}
}
