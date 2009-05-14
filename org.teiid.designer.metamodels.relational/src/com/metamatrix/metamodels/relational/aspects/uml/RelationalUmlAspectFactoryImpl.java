/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.aspects.uml;

import org.eclipse.emf.ecore.EClassifier;

import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelEntity;

/**
 * RelationalUmlAspectFactoryImpl
 */
public class RelationalUmlAspectFactoryImpl implements MetamodelAspectFactory {
    public MetamodelAspect create(EClassifier classifier, MetamodelEntity entity) {
        switch (classifier.getClassifierID()) {
            case RelationalPackage.ACCESS_PATTERN:              return new AccessPatternAspect(entity);
            case RelationalPackage.CATALOG:                     return new CatalogAspect(entity);
            case RelationalPackage.COLUMN:                      return new ColumnAspect(entity);
            case RelationalPackage.DIRECTION_KIND:              return null;    // enumeration
            case RelationalPackage.FOREIGN_KEY:                 return new ForeignKeyAspect(entity);
            case RelationalPackage.INDEX:                       return new IndexAspect(entity);
            case RelationalPackage.LOGICAL_RELATIONSHIP:        return new LogicalRelationshipAspect(entity);
            case RelationalPackage.LOGICAL_RELATIONSHIP_END:    return null;
            case RelationalPackage.MULTIPLICITY_KIND:           return null;    // enumeration
            case RelationalPackage.NULLABLE_TYPE:               return null;    // enumeration
            case RelationalPackage.PRIMARY_KEY:                 return new PrimaryKeyAspect(entity);
            case RelationalPackage.PROCEDURE:                   return new ProcedureAspect(entity);
            case RelationalPackage.PROCEDURE_PARAMETER:         return new ProcedureParameterAspect(entity);
            case RelationalPackage.PROCEDURE_RESULT:            return new ProcedureResultAspect(entity);
            case RelationalPackage.RELATIONAL_ENTITY:           return null;    // abstract class
            case RelationalPackage.RELATIONSHIP:                return null;    // abstract class
            case RelationalPackage.SCHEMA:                      return new SchemaAspect(entity);
            case RelationalPackage.TABLE:                       return null;    // abstract class
            case RelationalPackage.BASE_TABLE:                  return new BaseTableAspect(entity);
            case RelationalPackage.UNIQUE_CONSTRAINT:           return new UniqueConstraintAspect(entity);
            case RelationalPackage.UNIQUE_KEY:                  return null;    // abstract class
            case RelationalPackage.VIEW:                        return new ViewAspect(entity);
            case RelationalPackage.PROCEDURE_UPDATE_COUNT:      return null;    // enumeration
            default:
                throw new IllegalArgumentException(RelationalPlugin.Util.getString("RelationalUmlAspectFactoryImpl.Invalid_Classifer_ID,_for_creating_UML_Aspect_1")+classifier); //$NON-NLS-1$
        }
    }

}
