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

package com.metamatrix.modeler.internal.xsd.aspects.uml;

import org.eclipse.xsd.XSDPackage;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;
import com.metamatrix.modeler.xsd.PluginConstants;


/** 
 * @since 5.0.2
 */
public class XsdUmlAspectFactory implements MetamodelAspectFactory,
                                            PluginConstants {
    
    public XsdUmlAspectFactory() {
    }
    
    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     * @since 5.0.2
     */
    public MetamodelAspect create(EClassifier theClassifier,
                                  MetamodelEntity theEntity) {
        switch (theClassifier.getClassifierID()) {
            case XSDPackage.XSD_ANNOTATION: return null;
            case XSDPackage.XSD_ATTRIBUTE_DECLARATION: return null;
            case XSDPackage.XSD_ATTRIBUTE_GROUP_CONTENT: return null;
            case XSDPackage.XSD_ATTRIBUTE_GROUP_DEFINITION: return null;
            case XSDPackage.XSD_ATTRIBUTE_USE: return null;
            case XSDPackage.XSD_BOUNDED_FACET: return null;
            case XSDPackage.XSD_CARDINALITY_FACET: return null;
            case XSDPackage.XSD_COMPLEX_TYPE_CONTENT: return null;
            case XSDPackage.XSD_COMPLEX_TYPE_DEFINITION: return null;
            case XSDPackage.XSD_COMPONENT: return null;
            case XSDPackage.XSD_CONCRETE_COMPONENT: return null;
            case XSDPackage.XSD_CONSTRAINING_FACET: return null;
            case XSDPackage.XSD_DIAGNOSTIC: return null;
            case XSDPackage.XSD_ELEMENT_DECLARATION: return null;
            case XSDPackage.XSD_ENUMERATION_FACET: return new EnumeratedTypeValueAspect(theEntity);
            case XSDPackage.XSD_FACET: return null;
            case XSDPackage.XSD_FEATURE: return null;
            case XSDPackage.XSD_FIXED_FACET: return null;
            case XSDPackage.XSD_FRACTION_DIGITS_FACET: return null;
            case XSDPackage.XSD_FUNDAMENTAL_FACET: return null;
            case XSDPackage.XSD_IDENTITY_CONSTRAINT_DEFINITION: return null;
            case XSDPackage.XSD_IMPORT: return null;
            case XSDPackage.XSD_INCLUDE: return null;
            case XSDPackage.XSD_LENGTH_FACET: return null;
            case XSDPackage.XSD_MAX_EXCLUSIVE_FACET: return null;
            case XSDPackage.XSD_MAX_FACET: return null;
            case XSDPackage.XSD_MAX_INCLUSIVE_FACET: return null;
            case XSDPackage.XSD_MAX_LENGTH_FACET: return null;
            case XSDPackage.XSD_MIN_EXCLUSIVE_FACET: return null;
            case XSDPackage.XSD_MIN_FACET: return null;
            case XSDPackage.XSD_MIN_INCLUSIVE_FACET: return null;
            case XSDPackage.XSD_MIN_LENGTH_FACET: return null;
            case XSDPackage.XSD_MODEL_GROUP: return null;
            case XSDPackage.XSD_MODEL_GROUP_DEFINITION: return null;
            case XSDPackage.XSD_NAMED_COMPONENT: return null;
            case XSDPackage.XSD_NOTATION_DECLARATION: return null;
            case XSDPackage.XSD_NUMERIC_FACET: return null;
            case XSDPackage.XSD_ORDERED_FACET: return null;
            case XSDPackage.XSD_PARTICLE: return null;
            case XSDPackage.XSD_PARTICLE_CONTENT: return null;
            case XSDPackage.XSD_PATTERN_FACET: return null;
            case XSDPackage.XSD_REDEFINABLE_COMPONENT: return null;
            case XSDPackage.XSD_REDEFINE_CONTENT: return null;
            case XSDPackage.XSD_REDEFINE: return null;
            case XSDPackage.XSD_REPEATABLE_FACET: return null;
            case XSDPackage.XSD_SCHEMA: return null;
            case XSDPackage.XSD_SCHEMA_COMPOSITOR: return null;
            case XSDPackage.XSD_SCHEMA_CONTENT: return null;
            case XSDPackage.XSD_SCHEMA_DIRECTIVE: return null;
            case XSDPackage.XSD_SCOPE: return null;
            case XSDPackage.XSD_SIMPLE_TYPE_DEFINITION: return new EnumeratedTypeAspect(theEntity);
            case XSDPackage.XSD_TERM: return null;
            case XSDPackage.XSD_TOTAL_DIGITS_FACET: return null;
            case XSDPackage.XSD_TYPE_DEFINITION: return null;
            case XSDPackage.XSD_WHITE_SPACE_FACET: return null;
            case XSDPackage.XSD_WILDCARD: return null;
            case XSDPackage.XSD_XPATH_DEFINITION: return null;
            case XSDPackage.XSD_ATTRIBUTE_USE_CATEGORY: return null;
            case XSDPackage.XSD_CARDINALITY: return null;
            case XSDPackage.XSD_COMPLEX_FINAL: return null;
            case XSDPackage.XSD_COMPOSITOR: return null;
            case XSDPackage.XSD_CONSTRAINT: return null;
            case XSDPackage.XSD_CONTENT_TYPE_CATEGORY: return null;
            case XSDPackage.XSD_DERIVATION_METHOD: return null;
            case XSDPackage.XSD_DIAGNOSTIC_SEVERITY: return null;
            case XSDPackage.XSD_DISALLOWED_SUBSTITUTIONS: return null;
            case XSDPackage.XSD_FORM: return null;
            case XSDPackage.XSD_IDENTITY_CONSTRAINT_CATEGORY: return null;
            case XSDPackage.XSD_NAMESPACE_CONSTRAINT_CATEGORY: return null;
            case XSDPackage.XSD_ORDERED: return null;
            case XSDPackage.XSD_PROCESS_CONTENTS: return null;
            case XSDPackage.XSD_PROHIBITED_SUBSTITUTIONS: return null;
            case XSDPackage.XSD_SIMPLE_FINAL: return null;
            case XSDPackage.XSD_SUBSTITUTION_GROUP_EXCLUSIONS: return null;
            case XSDPackage.XSD_VARIETY: return null;
            case XSDPackage.XSD_WHITE_SPACE: return null;
            case XSDPackage.XSD_XPATH_VARIETY: return null;
            case XSDPackage.DOM_ATTR: return null;
            case XSDPackage.DOM_DOCUMENT: return null;
            case XSDPackage.DOM_ELEMENT: return null;
            case XSDPackage.DOM_NODE: return null;
            case XSDPackage.VALUE: return null;
            default:
                String msgKey = I18nUtil.getPropertyPrefix(XsdUmlAspectFactory.class) + "invalidClassifierType"; //$NON-NLS-1$
                throw new IllegalArgumentException(Util.getString(msgKey, new Integer(theClassifier.getClassifierID())));
        }
    }

}
