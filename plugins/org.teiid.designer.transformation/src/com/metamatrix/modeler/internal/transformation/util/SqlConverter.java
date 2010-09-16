/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.transformation.util;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.query.QueryValidationResult;
import com.metamatrix.modeler.core.validation.ValidationContext;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;

/**
 * SqlConverter
 */
public class SqlConverter {

	/** Delimiter character used when specifying fully qualified entity names */
	public static final char DELIMITER_CHAR = UuidUtil.DELIMITER_CHAR;

    /**
     * convert the specified Sql String to String form
     * @param sqlString the SQL String to convert
     * @param transMappingRoot the transformationMappingRoot for the supplied sql
     * @param cmdType the sql command type
     */
    public static String convertToString(final String sqlString, final EObject transMappingRoot, final int cmdType) {
        return convertToString(sqlString, transMappingRoot, cmdType, false, null);
    }

	/**
	 * convert the specified Sql String to String form
	 * @param sqlString the SQL String to convert
	 * @param transMappingRoot the transformationMappingRoot for the supplied sql
	 * @param cmdType the sql command type
	 * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
	 * or if the whole workspace needs to be searched
	 */
	public static String convertToString(final String sqlString, final EObject transMappingRoot, final int cmdType, final boolean restrictSearch) {
		return convertSql(sqlString, transMappingRoot, cmdType, restrictSearch, null);
	}

    /**
     * convert the specified Sql String to String form
     * @param sqlString the SQL String to convert
     * @param transMappingRoot the transformationMappingRoot for the supplied sql
     * @param cmdType the sql command type
     * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
     * or if the whole workspace needs to be searched
     */
    public static String convertToString(final String sqlString, final EObject transMappingRoot, final int cmdType, 
                                         final boolean restrictSearch, final ValidationContext context) {
        return convertSql(sqlString, transMappingRoot, cmdType, restrictSearch, context);
    }

    /**
     * convert the specified Query using the MappingVisitor
     * @param sqlString the SQL String to convert
     * @param transMappingRoot the transformationMappingRoot for the supplied sql
     * @param convertSymbolsToUUIDs the flag which specifies which way to convert.
     * 'true' converts symbol names to UUIDs, 'false' converts UUIDs to symbol names
	 * @param restrictSearch A boolean indicating if the search needs to be restricted to model imports
	 * or if the whole workspace needs to be searched
     * @param context the ValidationContext to use; may be null
     */
    private static synchronized String convertSql(final String sqlString, final EObject transMappingRoot, 
                                                  final int cmdType,
                                                  final boolean restrictSearch, final ValidationContext context) {
		if(CoreStringUtil.isEmpty(sqlString)) return null;

		CoreArgCheck.isInstanceOf(SqlTransformationMappingRoot.class, transMappingRoot);

        final SqlTransformationMappingRoot sqlTransMappingRoot = (SqlTransformationMappingRoot)transMappingRoot;
        final TransformationValidator validator = new TransformationValidator(sqlTransMappingRoot, context, false, restrictSearch);

        // Attempt to Parse, Resolve and Validate

        QueryValidationResult validationResult = validator.validateSql(sqlString, cmdType, false);

        if(!validationResult.isValidatable()) {
            return null;
        }        


        return sqlString;
    }

}