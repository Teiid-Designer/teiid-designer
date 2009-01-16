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

package com.metamatrix.metamodels.xmlservice.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.xmlservice.XmlServicePackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;


/** 
 * XmlServiceUmlAspectFactoryImpl
 */
public class XmlServiceUmlAspectFactoryImpl implements MetamodelAspectFactory {

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     * @since 4.2
     */
    public MetamodelAspect create(EClassifier classifier,
                                  MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case XmlServicePackage.XML_OPERATION: return new XmlOperationAspect(entity);
            case XmlServicePackage.XML_INPUT: return new XmlInputAspect(entity);
            case XmlServicePackage.XML_OUTPUT: return new XmlOutputAspect(entity);
            case XmlServicePackage.XML_RESULT: return new XmlResultAspect(entity);
            case XmlServicePackage.OPERATION_UPDATE_COUNT: return null;
            default: return null;
        }
    }

}
