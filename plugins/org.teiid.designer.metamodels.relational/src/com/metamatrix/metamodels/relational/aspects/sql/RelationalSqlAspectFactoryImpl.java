/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.sql;

import org.eclipse.emf.ecore.EClassifier;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationalSqlAspectFactoryImpl
 */
public class RelationalSqlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case RelationalPackage.ACCESS_PATTERN: return createAccessPatternAspect(entity);
            case RelationalPackage.CATALOG: return createCatalogAspect(entity);
            case RelationalPackage.COLUMN: return createColumnAspect(entity);
            case RelationalPackage.DIRECTION_KIND: return null;
            case RelationalPackage.FOREIGN_KEY: return createForeignKeyAspect(entity);
            case RelationalPackage.INDEX: return createIndexAspect(entity);
            case RelationalPackage.LOGICAL_RELATIONSHIP: return null;
            case RelationalPackage.LOGICAL_RELATIONSHIP_END: return  null;
            case RelationalPackage.MULTIPLICITY_KIND: return null;
            case RelationalPackage.NULLABLE_TYPE: return null;
            case RelationalPackage.PRIMARY_KEY: return createPrimaryKeyAspect(entity);
            case RelationalPackage.PROCEDURE: return createProcedureAspect(entity);
            case RelationalPackage.PROCEDURE_PARAMETER: return createProcedureParameterAspect(entity);
            case RelationalPackage.PROCEDURE_RESULT: return createProcedureResultAspect(entity);
            case RelationalPackage.RELATIONAL_ENTITY: return null;
            case RelationalPackage.RELATIONSHIP: return null;
            case RelationalPackage.SCHEMA: return createSchemaAspect(entity);
            case RelationalPackage.TABLE: return null;
            case RelationalPackage.BASE_TABLE: return createBaseTableAspect(entity);
            case RelationalPackage.UNIQUE_CONSTRAINT:
            case RelationalPackage.UNIQUE_KEY: return createUniqueKeyAspect(entity);
            case RelationalPackage.VIEW: return createViewAspect(entity);
            case RelationalPackage.PROCEDURE_UPDATE_COUNT: return null;
            default:
                throw new IllegalArgumentException(RelationalPlugin.Util.getString("RelationalSqlAspectFactoryImpl.Invalid_Classifer_ID,_for_creating_SQL_Aspect__1")+classifier); //$NON-NLS-1$
        }
    }
    
//    /**
//     * @return
//     */
//    private MetamodelAspect createSqlDataTypeAspect(MetamodelEntity entity) {
//        return new SqlDataTypeAspect(entity);
//    }
//
    /**
     * @return
     */
    private MetamodelAspect createViewAspect(MetamodelEntity entity) {
        return new ViewAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createForeignKeyAspect(MetamodelEntity entity) {
        return new ForeignKeyAspect(entity);
    }
    
    /**
     * @return
     */
    private MetamodelAspect createAccessPatternAspect(MetamodelEntity entity) {
        return new AccessPatternAspect(entity);
    }    

    /**
     * @return
     */
    private MetamodelAspect createPrimaryKeyAspect(MetamodelEntity entity) {
        return new PrimaryKeyAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createUniqueKeyAspect(MetamodelEntity entity) {
        return new UniqueKeyAspect(entity);
    }    

    /**
     * @return
     */
    private MetamodelAspect createIndexAspect(MetamodelEntity entity) {
        return new IndexAspect(entity);
    }    


    /**
     * @return
     */
    private MetamodelAspect createColumnAspect(MetamodelEntity entity) {
        return new ColumnAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createBaseTableAspect(MetamodelEntity entity) {
        return new BaseTableAspect(entity);
    }
    
    /**
     * @return
     */
    private MetamodelAspect createProcedureAspect(MetamodelEntity entity) {
        return new ProcedureAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createProcedureParameterAspect(MetamodelEntity entity) {
        return new ProcedureParameterAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createProcedureResultAspect(MetamodelEntity entity) {
        return new ProcedureResultAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createCatalogAspect(MetamodelEntity entity) {
        return new CatalogAspect(entity);
    }

    /**
     * @return
     */
    private MetamodelAspect createSchemaAspect(MetamodelEntity entity) {
        return new SchemaAspect(entity);
    }
    
}
