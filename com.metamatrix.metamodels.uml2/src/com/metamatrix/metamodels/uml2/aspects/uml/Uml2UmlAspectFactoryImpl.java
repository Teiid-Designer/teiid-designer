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

package com.metamatrix.metamodels.uml2.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.uml2.uml.UMLPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * Uml2UmlAspectFactoryImpl
 */
public class Uml2UmlAspectFactoryImpl implements MetamodelAspectFactory {

    /**
     * Construct an instance of Uml2UmlAspectFactoryImpl.
     *
     */
    public Uml2UmlAspectFactoryImpl() {
        super();
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case UMLPackage.CLASS:                 return new Uml2ClassUmlAspect(entity);
            case UMLPackage.PROPERTY:              return new Uml2PropertyUmlAspect(entity);
            case UMLPackage.PACKAGE:               return new Uml2PackageUmlAspect(entity);
            case UMLPackage.MODEL:                 return new Uml2ModelUmlAspect(entity);
            case UMLPackage.COMMENT:               return new Uml2CommentUmlAspect(entity);
            case UMLPackage.OPERATION:             return new Uml2OperationUmlAspect(entity);
            case UMLPackage.PARAMETER:             return new Uml2PackageUmlAspect(entity);
            case UMLPackage.ENUMERATION:           return new Uml2EnumerationUmlAspect(entity);
            case UMLPackage.ENUMERATION_LITERAL:   return new Uml2EnumerationLiteralUmlAspect(entity);
            case UMLPackage.GENERALIZATION:        return new Uml2GeneralizationUmlAspect(entity);
            case UMLPackage.ASSOCIATION:           return new Uml2AssociationUmlAspect(entity);
            case UMLPackage.ABSTRACTION:           return new Uml2AbstractionUmlAspect(entity);
            case UMLPackage.REALIZATION:           return new Uml2RealizationUmlAspect(entity);
            case UMLPackage.USAGE:                 return new Uml2UsageUmlAspect(entity);
            case UMLPackage.SUBSTITUTION:          return new Uml2SubstitutionUmlAspect(entity);
            case UMLPackage.INTERFACE:             return new Uml2InterfaceUmlAspect(entity);
            default:
				return null;
        }
    }

}
