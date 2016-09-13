/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.relational.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @since 8.0
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {
	
    public static String fkNameLabel;
    public static String nativeQueryLabel;
    public static String sqlLabel;
    public static String autoUpdateLabel;
    public static String nullableLabel;
    public static String uniqueLabel;
    public static String filterConditionLabel;
    public static String referencedColumnsLabel;
    public static String indexesLabel;
    public static String indexLabel;
    public static String tableSelectionTitle;
    public static String selectExistingTableForIndexInitialMessage;
    public static String noTableSelectedMessage;

    public static String baseTableActionText;
    public static String createRelationalTableTitle;
    public static String createRelationalTableExceptionMessage;
    public static String createRelationalTableHelpText;
    

    public static String viewActionText;
    public static String createRelationalViewTitle;
    public static String createRelationalViewExceptionMessage;
    public static String createRelationalViewHelpText;
    
    public static String procedureLabel;
    public static String sourceFunctionLabel;
    public static String nativeQueryProcedureLabel;
    public static String userDefinedFunctionLabel;
    public static String selectProcedureTypeDialogSubTitle;
    public static String selectProcedureTypeDialogTitle;
    public static String createRelationalProcedureDescription;
    public static String createRelationalSourceFunctionDescription;
    public static String createRelationalNativeQueryProcedureDescription;
    public static String createRelationalUserDefinedFunctionDescription;
    public static String createRelationalProcedureActionText;
    public static String createRelationalProcedureTitle;
    public static String createRelationalProcedureExceptionMessage;
    public static String createRelationalProcedureHelpText;

    public static String createRelationalSourceFunctionTitle;
    public static String createRelationalUserDefinedFunctionTitle;
    public static String createRelationalSourceFunctionHelpText;
    public static String createRelationalUserDefinedFunctionHelpText;
    public static String createRelationalNativeQueryProcedureTitle;
    public static String createRelationalNativeQueryProcedureHelpText;
    
    public static String createRelationalIndexActionText;
    public static String createRelationalIndexTitle;
    public static String createRelationalIndexExceptionMessage;
    public static String createRelationalIndexHelpText;
    public static String browseModelToSelectTableForIndexTooltipText;

    public static String nativeQueryHelpText;
    public static String nativeQueryNotSupportedForViews;
    public static String foreignKeysNotSupportedForViews;
    public static String primaryKeysNotSupportedForViews;
    public static String uniqueConstraintsNotSupportedForViews;
    public static String indexesNotSupportedForViews;

    public static String modelFileLabel;

    public static String nameInSourceLabel;
    public static String cardinalityLabel;
    public static String materializedLabel;
    public static String tableReferenceLabel;
    public static String supportsUpdateLabel;
    public static String systemTableLabel;
    public static String foreignKeysLabel;
    public static String primaryKeyLabel;
    public static String columnsLabel;
    public static String uniqueConstraintLabel;
    public static String uniqueConstraintsLabel;
    public static String columnNameLabel;
    public static String parameterNameLabel;
    public static String dataTypeLabel;
    public static String lengthLabel;
	public static String isFunctionLabel;
	public static String parametersLabel;
	public static String directionLabel;
	public static String updateCountLabel;
	public static String nonPreparedLabel;
	public static String deterministicLabel;
	public static String returnsNullOnNullLabel;
	public static String variableArgumentsLabel;
	public static String aggregateLabel;
	public static String allowsDistinctLabel;
	public static String allowsOrderByLabel;
	public static String analyticLabel;
	public static String decomposableLabel;
	public static String usesDistinctRowsLabel;
	public static String functionPropertiesLabel;
	public static String aggregatePropertiesLabel;
	public static String resultSetLabel;
	public static String javaClassLabel;
	public static String javaMethodLabel;
	public static String udfJarPathLabel;
	public static String functionCategoryLabel;
	public static String allowJoinLabel;
	public static String allowJoinTooltip;

	public static String validationOkCreateObject;
	public static String selectColumnsTitle;
	public static String selectColumnsSubTitle;
	public static String selectColumnsMessage;
	public static String createForeignKeyTitle;
	public static String editForeignKeyTitle;
	public static String foreignKeyMultiplicity;
	public static String uniqueKeyMultiplicity;
	public static String selectPrimaryKeyOrUniqueConstraint;
	public static String selectColumnReferencesToFK;
	public static String newForeignKeyMessage;
	public static String cardinalityErrorTitle;
	public static String cardinalityMustBeAnInteger;
	public static String unsupportedObjectType;
	public static String selectColumnReferencesForIndex;
	public static String newIndexMessage;
	public static String createIndexTitle;
	public static String editIndexTitle;
	public static String includeResultSetTooltip;
	public static String createUniqueConstraintTitle;
	public static String editUniqueConstraintTitle;

	public static String upgradeRelationalExtensionsLabel;
    public static String quickFixMedFileDirtyTitle;
    public static String quickFixMedFileDirtyMsg;
    public static String quickFixModelDirtyTitle;
    public static String quickFixModelDirtyMsg;
    public static String medFileParseErrorMsg;
    public static String getSupportedPrefixesErrorMsg;
    public static String getModelMedErrorMsg;
    public static String saveModelMedErrorMsg;
    public static String saveModelErrorMsg;
    
    public static String Edit;
    public static String EditColumnTitle;
    public static String EditingColumnInformation;
    public static String Name;
    public static String ColumnLengthError;
    public static String ColumnWidthError;
    public static String ClickOkToAcceptChanges;
    public static String EditParameterTitle;
    public static String EditingParameterInformation;
    
    static {
        NLS.initializeMessages("org.teiid.designer.relational.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
