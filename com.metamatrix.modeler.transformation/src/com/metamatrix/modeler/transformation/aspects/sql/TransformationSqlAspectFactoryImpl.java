/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * TransformationSqlAspectFactoryImpl
 */
public class TransformationSqlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case TransformationPackage.MAPPING_CLASS_COLUMN: 
                return new MappingClassColumnSqlAspect(entity);
            case TransformationPackage.MAPPING_CLASS: 
            case TransformationPackage.STAGING_TABLE: 
                return new MappingClassSqlAspect(entity);
            case TransformationPackage.MAPPING_CLASS_SET: 
                return new MappingClassSetSqlAspect(entity);
            case TransformationPackage.SQL_TRANSFORMATION_MAPPING_ROOT: 
                return new SqlTransformationMappingRootSqlAspect(entity);
			case TransformationPackage.TREE_MAPPING_ROOT: 
				return new TreeMappingRootSqlAspect(entity);
            case TransformationPackage.INPUT_PARAMETER: 
                return new InputParameterSqlAspect(entity);
			case TransformationPackage.INPUT_SET: 
				return new InputSetSqlAspect(entity);
            case TransformationPackage.XQUERY_TRANSFORMATION_MAPPING_ROOT: 
                return new XQueryTransformationMappingRootSqlAspect(entity);
            default:
                return null;
        }
    }
}
