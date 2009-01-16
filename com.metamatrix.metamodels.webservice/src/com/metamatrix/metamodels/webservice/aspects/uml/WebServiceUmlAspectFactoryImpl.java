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

package com.metamatrix.metamodels.webservice.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;


/** 
 * WebServiceUmlAspectFactoryImpl
 */
public class WebServiceUmlAspectFactoryImpl implements MetamodelAspectFactory {

    /** 
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     * @since 4.2
     */
    public MetamodelAspect create(EClassifier classifier,
                                  MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case WebServicePackage.INTERFACE: return new InterfaceAspect(entity);
            case WebServicePackage.OPERATION: return new OperationAspect(entity);
            case WebServicePackage.INPUT: return new InputAspect(entity);
            case WebServicePackage.OUTPUT: return new OutputAspect(entity);
            case WebServicePackage.SAMPLE_MESSAGES: return new SampleMessagesAspect(entity);
            default: return null;
        }
    }

}
