/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.validation;

import org.eclipse.emf.ecore.EClassifier;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * TransformationValidationAspectFactoryImpl
 */
public class TransformationValidationAspectFactoryImpl implements MetamodelAspectFactory {

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory#create(org.eclipse.emf.ecore.EClassifier, com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity)
     */
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case TransformationPackage.STAGING_TABLE:            
            case TransformationPackage.MAPPING_CLASS: return new MappingClassAspect(entity);
            case TransformationPackage.MAPPING_CLASS_COLUMN: return new MappingClassColumnAspect(entity);
			case TransformationPackage.INPUT_SET: return new InputSetAspect(entity);
			case TransformationPackage.INPUT_BINDING: return new InputBindingAspect(entity);
			case TransformationPackage.INPUT_PARAMETER: return new InputParameterAspect(entity);
            case TransformationPackage.TRANSFORMATION_MAPPING: return new TransformationMappingAspect(entity);
            case TransformationPackage.TREE_MAPPING_ROOT: return new TreeMappingRootAspect(entity);
			case TransformationPackage.SQL_ALIAS: return new SqlAliasAspect(entity);
			case TransformationPackage.SQL_TRANSFORMATION: return new SqlTransformationAspect(entity);
            case TransformationPackage.SQL_TRANSFORMATION_MAPPING_ROOT: return new SqlTransformationMappingRootAspect(entity);
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT:
			case TransformationPackage.FRAGMENT_MAPPING_ROOT:
            case TransformationPackage.TRANSFORMATION_CONTAINER:
            case TransformationPackage.MAPPING_CLASS_OBJECT:
            case TransformationPackage.MAPPING_CLASS_SET:
            case TransformationPackage.MAPPING_CLASS_SET_CONTAINER: return null;            
            default:
                return null;
        }
    }
}
