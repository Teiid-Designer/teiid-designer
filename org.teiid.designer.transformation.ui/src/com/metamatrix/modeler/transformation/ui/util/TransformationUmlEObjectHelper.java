/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.diagram.ui.util.RelationalUmlEObjectHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;

/**
 * @author PForhan
 */
public class TransformationUmlEObjectHelper extends RelationalUmlEObjectHelper {

    public static final int MAPPING                         = 100;
    public static final int MAPPING_CLASS                   = 101;
    public static final int SQL_COLUMN                      = 102;
    public static final int SQL_COLUMN_SET                  = 103;
    public static final int SQL_INPUT_PARAMETER             = 104;
    public static final int SQL_INPUT_SET                   = 105;
    public static final int SQL_PROCEDURE                   = 106;
    public static final int SQL_PROCEDURE_PARAMETER         = 107;
    public static final int SQL_PROCEDURE_RESULT_SET        = 108;
    public static final int SQL_TABLE                       = 109;
    public static final int SQL_TRANSFORMATION              = 110;
    public static final int SQL_TRANSFORMATION_MAPPING_ROOT = 111;
    public static final int SQL_VIRTUAL_PROCEDURE           = 112;
    public static final int STAGING_TABLE                   = 113;
    public static final int TRANSFORMATION_MAPPING          = 114;
    public static final int TRANSFORMATION_MAPPING_ROOT     = 115;
    public static final int TRANSFORMATION_OBJECT           = 116;
    public static final int XML_DOCUMENT                    = 117;

    public static int getEObjectType(EObject eObj) {
        int type = RelationalUmlEObjectHelper.getEObjectType(eObj);
        
        if (type != UNKNOWN) {
            return type;
        } // endif

        if (TransformationHelper.isMapping(eObj)) {
            return MAPPING;
        } else if (TransformationHelper.isMappingClass(eObj)) {
            return MAPPING_CLASS;
        } else if (TransformationHelper.isSqlColumn(eObj)) {
            return SQL_COLUMN;
        } else if (TransformationHelper.isSqlInputParameter(eObj)) {
            return SQL_INPUT_PARAMETER;
        } else if (TransformationHelper.isSqlInputSet(eObj)) {
            return SQL_INPUT_SET;
        } else if (TransformationHelper.isSqlProcedure(eObj)) {
            return SQL_PROCEDURE;
        } else if (TransformationHelper.isSqlProcedureParameter(eObj)) {
            return SQL_PROCEDURE_PARAMETER;
        } else if (TransformationHelper.isSqlProcedureResultSet(eObj)) {
            return SQL_PROCEDURE_RESULT_SET;
        } else if (TransformationHelper.isSqlTransformation(eObj)) {
            return SQL_TRANSFORMATION;
        } else if (TransformationHelper.isSqlTransformationMappingRoot(eObj)) {
            return SQL_TRANSFORMATION_MAPPING_ROOT;
        } else if (TransformationHelper.isSqlVirtualProcedure(eObj)) {
            return SQL_VIRTUAL_PROCEDURE;
        } else if (TransformationHelper.isStagingTable(eObj)) {
            return STAGING_TABLE;
        } else if (TransformationHelper.isTransformationMapping(eObj)) {
            return TRANSFORMATION_MAPPING;
        } else if (TransformationHelper.isTransformationMappingRoot(eObj)) {
            return TRANSFORMATION_MAPPING_ROOT;
        } else if (TransformationHelper.isTransformationObject(eObj)) {
            return TRANSFORMATION_OBJECT;
        } else if (TransformationHelper.isXmlDocument(eObj)) {
            return XML_DOCUMENT;
        } else if (TransformationHelper.isSqlTable(eObj)) {
            return SQL_TABLE;
        } else if (TransformationHelper.isSqlColumnSet(eObj)) {
            return SQL_COLUMN_SET;
        } // endif

        // not found, use super's version:
        return UNKNOWN;
    }
}
