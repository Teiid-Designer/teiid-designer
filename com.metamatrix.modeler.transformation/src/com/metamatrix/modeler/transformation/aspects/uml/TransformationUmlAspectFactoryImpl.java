/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * TransformationUmlAspectFactoryImpl
 */
public class TransformationUmlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case TransformationPackage.MAPPING_CLASS: return createMappingClassAspect();
            case TransformationPackage.MAPPING_CLASS_COLUMN: return createMappingClassColumnAspect();
            case TransformationPackage.STAGING_TABLE: return createStagingTableAspect();
            case TransformationPackage.INPUT_SET: return createInputSetAspect();
            case TransformationPackage.INPUT_PARAMETER: return createInputParameterAspect();
            default:
                return null;
        }
    }

    /**
     * @return
     */
    private MetamodelAspect createMappingClassColumnAspect() {
        return new MappingClassColumnUmlAspect();
    }

    /**
     * @return
     */
    private MetamodelAspect createMappingClassAspect() {
        return new MappingClassUmlAspect();
    }

    /**
     * @return
     */
    private MetamodelAspect createStagingTableAspect() {
        return new StagingTableUmlAspect();
    }

    /**
     * @return
     */
    private MetamodelAspect createInputParameterAspect() {
        return new InputParameterUmlAspect();
    }

    /**
     * @return
     */
    private MetamodelAspect createInputSetAspect() {
        return new InputSetUmlAspect();
    }
}
