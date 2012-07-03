/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
            case WebServicePackage.OPERATION_UPDATE_COUNT: return null; // enumeration
            default: return null;
        }
    }

}
