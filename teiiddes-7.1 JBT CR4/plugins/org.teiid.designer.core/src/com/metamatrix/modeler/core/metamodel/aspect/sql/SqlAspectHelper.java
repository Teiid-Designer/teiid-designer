/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.metamodel.aspect.AspectManager;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspect;


/** 
 * @since 4.3
 */
public class SqlAspectHelper {
    
    /**
     * Get the SqlAspect given an EObject
     * @param eObject the EObject
     * @return the SqlAspect for the supplied EObject
     */
    public static SqlAspect getSqlAspect(final EObject eObject) {
        return AspectManager.getSqlAspect(eObject);
    }
    
    /**
     * Determine if the supplied EObject has a SqlColumnAspect
     * @param eObject the EObject
     * @return 'true' if has SqlColumnAspect, 'false' if not.
     */
    public static boolean isColumn(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlColumnAspect )
            return true;
            
        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlTableAspect
     * @param eObject the EObject
     * @return 'true' if has SqlTableAspect, 'false' if not.
     */
    public static boolean isTable(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlTableAspect )
            return true;
            
        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlTableAspect
     * and is a XML document 
     * @param eObject the EObject
     * @return 'true' if has SqlTableAspect and XML doc, 'false' if not.
     */
    public static boolean isXmlDocument(EObject eObject ) {
        if(isValidTreeTransformationTarget(eObject)) {
            return true;
        }
        return false;
    }
    
    /**
     * Determine if the supplied EObject has a SqlTableAspect
     * and is a XML document 
     * @param eObject the EObject
     * @return 'true' if has SqlTableAspect and XML doc, 'false' if not.
     */
    public static boolean isXmlFragment(EObject eObject ) {
        if(isValidTreeTransformationTarget(eObject)) {
            return true;
        }
        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlTableAspect
     * @param eObject the EObject
     * @return 'true' if has SqlTableAspect, 'false' if not.
     */
    public static boolean isUpdatableGroup(EObject eObject ) {
        MetamodelAspect sqlAspect = getSqlAspect(eObject);
        if(sqlAspect != null) {
            if ( sqlAspect instanceof SqlTableAspect ) {
                SqlTableAspect tableAspect = (SqlTableAspect) sqlAspect;
                return tableAspect.supportsUpdate(eObject);
            }
            if ( sqlAspect instanceof SqlProcedureAspect ) {
                // Virtual procedures allow only select SQL, not insert, update or delete SQL
                return false;
            }
        }
        return false;
    }

    /**
     * Determine if the supplied EObject is a valid Sql transformation target
     * @param eObject the EObject
     * @return 'true' if valid target, 'false' if not.
     */
    public static boolean isValidSqlTransformationTarget(EObject eObject ) {
        boolean isValid = false;
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null ) {
            if(aspect instanceof SqlTableAspect ) {
                isValid = ((SqlTableAspect)aspect).isMappable(eObject,SqlTableAspect.MAPPINGS.SQL_TRANSFORM);
            } else if(aspect instanceof SqlProcedureAspect) {
                isValid = ((SqlProcedureAspect)aspect).isMappable(eObject,SqlProcedureAspect.MAPPINGS.SQL_TRANSFORM);
            }
        }
            
        return isValid;
    }

    /**
     * Determine if the supplied EObject is a valid tree transformation target
     * @param eObject the EObject
     * @return 'true' if valid target, 'false' if not.
     */
    public static boolean isValidTreeTransformationTarget(EObject eObject ) {
        boolean isValid = false;
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null ) {
            if(aspect instanceof SqlTableAspect ) {
                isValid = ((SqlTableAspect)aspect).isMappable(eObject,SqlTableAspect.MAPPINGS.TREE_TRANSFORM);
            }
        }
            
        return isValid;
    }

    /**
     * Determine if the supplied EObject is a valid transformation target
     * @param eObject the EObject
     * @return 'true' if valid target, 'false' if not.
     */
    public static boolean isValidTransformationTarget(EObject eObject ) {
        boolean isValid = isValidSqlTransformationTarget(eObject);
        if(!isValid) {
            isValid = isValidTreeTransformationTarget(eObject);
        }
        return isValid;
    }

    /**
     * Determine if the supplied EObject has a SqlColumnSetAspect
     * @param eObject the EObject
     * @return 'true' if has SqlColumnSetAspect, 'false' if not.
     */
    public static boolean isColumnSet(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlColumnSetAspect )
            return true;

        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlForeignKeyAspect
     * @param eObject the EObject
     * @return 'true' if has SqlForeignKeyAspect, 'false' if not.
     */
    public static boolean isForeignKey(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlForeignKeyAspect )
            return true;

        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlTransformationAspect
     * @param eObject the EObject
     * @return 'true' if has SqlTransformationAspect, 'false' if not.
     */
    public static boolean isTransformation(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlTransformationAspect )
            return true;

        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlDatatypeAspect
     * @param eObject the EObject
     * @return 'true' if has SqlDatatypeAspect, 'false' if not.
     */
    public static boolean isDatatype(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlDatatypeAspect )
            return true;

        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlAnnotationAspect
     * @param eObject the EObject
     * @return 'true' if has SqlAnnotationAspect, 'false' if not.
     */
    public static boolean isAnnotation(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlAnnotationAspect )
            return true;

        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlVdbAspect
     * @param eObject the EObject
     * @return 'true' if has SqlVdbAspect, 'false' if not.
     */
    public static boolean isVdb(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlVdbAspect )
            return true;

        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlProcedureAspect
     * @param eObject the EObject
     * @return 'true' if has SqlProcedureAspect, 'false' if not.
     */
    public static boolean isProcedure(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlProcedureAspect )
            return true;

        return false;
    }
    
    /**
     * Determine if the supplied EObject has a SqlProcedureParameterAspect
     * @param eObject the EObject
     * @return 'true' if has SqlProcedureParameterAspect, 'false' if not.
     */
    public static boolean isProcedureParameter(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlProcedureParameterAspect )
            return true;

        return false;
    }
    
    /**
     * Determine if the supplied EObject has a SqlTableAspect
     * and the aspect has a resultType() == ResultSet
     * @param eObject the EObject
     * @return 'true' if has SqlTableAspect and is correct result set record type, 'false' if not.
     */
    public static boolean isProcedureResultSet(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlResultSetAspect ) {
            if( ((SqlResultSetAspect)aspect).isRecordType(IndexConstants.RECORD_TYPE.RESULT_SET) )
                return true;
        }
        return false;
    }

    /**
     * Determine if the supplied EObject has a SqlModelAspect
     * @param eObject the EObject
     * @return 'true' if has SqlModelAspect, 'false' if not.
     */
    public static boolean isModel(EObject eObject ) {
        MetamodelAspect aspect = getSqlAspect(eObject);
        if( aspect != null && aspect instanceof SqlModelAspect )
            return true;

        return false;
    }
}
