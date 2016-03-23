/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.transformation.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @since 8.0
 */
@SuppressWarnings("javadoc")
public class Messages extends org.teiid.designer.relational.ui.Messages {
    public static String Browse;
    public static String Edit;
    public static String EditColumnTitle;
    public static String EditParameterTitle;
    public static String EditingParameterInformation;
    public static String EditingColumnInformation;
    public static String Name;
    public static String Path;
    public static String RootPath;
    public static String ColumnInfo;
    public static String ColumnName;
    public static String DefaultValue;
    public static String ForOrdinality;
    public static String GeneratedSQLStatement;
    public static String XMLFile;
    public static String XmlConfigPageTitle;
    public static String XmlConfigPageInitialMessage;
    public static String XmlFileContents;
    public static String RootPathTooltip;
    public static String CreateColumn;
    public static String SetAsRootPath;
    public static String InvalidPathWarning;
    
    public static String createRelationalViewTableActionText;
    public static String createRelationalViewTableTitle;
    public static String createRelationalViewTableExceptionMessage;
    public static String createRelationalViewTableHelpText;
    public static String createFunctionLabel;
    
    public static String userDefinedFunctionLabel;
    public static String sourceFunctionLabel;
    public static String selectProcedureTypeDialogSubTitle;
    public static String selectProcedureTypeDialogTitle;
    public static String createRelationalViewProcedureDescription;
    public static String createRelationalViewUserDefinedFunctionDescription;
    public static String createRelationalSourceFunctionDescription;
    
    public static String createRelationalViewProcedureActionText;
    public static String createRelationalViewProcedureTitle;
    public static String createRelationalViewProcedureExceptionMessage;
    public static String createRelationalViewProcedureHelpText;
    public static String createRelationalProcedureExceptionMessage;
    
    public static String createRelationalViewIndexActionText;
    public static String createRelationalViewIndexTitle;
    public static String createRelationalViewIndexExceptionMessage;
    public static String createRelationalViewIndexHelpText;

    public static String createRelationalViewUserDefinedFunctionTitle;
    public static String createRelationalViewUserDefinedFunctionHelpText;

    public static String sqlLabel;
    public static String transformationSqlLabel;
    public static String sqlDescriptionLabel;
    public static String sqlTemplateLabel;
    public static String sqlGroupLabel;
    
    public static String fkNameLabel;
    public static String autoUpdateLabel;
    public static String nullableLabel;
    public static String uniqueLabel;
    public static String filterConditionLabel;
    public static String referencedColumnsLabel;
    public static String indexLabel;
    public static String tableSelectionTitle;
    public static String selectExistingTableForIndexInitialMessage;
    public static String noTableSelectedMessage;
    
    public static String materializedLabel;
    public static String tableReferenceLabel;
    public static String systemTableLabel;

    public static String globalTempTableLabel;

    public static String nameLabel;
    public static String propertiesLabel;
    public static String addLabel;
    public static String deleteLabel;
    public static String moveUpLabel;
    public static String moveDownLabel;
    public static String includeLabel;
    public static String description;
    public static String modelFileLabel;
    public static String nameInSourceLabel;
    public static String cardinalityLabel;
    public static String supportsUpdateLabel;
    public static String columnNameLabel;
    public static String dataTypeLabel;
    public static String lengthLabel;
    public static String procedureLabel;
    public static String parameterNameLabel;
	public static String isFunctionLabel;
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
	public static String selectSQLTemplateLabel;
	public static String sqlDefinitionLabel;
	
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

    public static String sqlTemplateDialogTitle;
    public static String sqlTemplateDialogTitleMessage;
    public static String sqlTemplateDialogOptionsGroup;
    public static String sqlTemplateDialogSelectLabel;
    public static String sqlTemplateDialogSelectJoinLabel;
    public static String sqlTemplateDialogUnionLabel;
    public static String sqlTemplateDialogFlatFileSrcLabel;
    public static String sqlTemplateDialogXmlFileLocalSrcLabel;
    public static String sqlTemplateDialogXmlFileUrlSrcLabel;
    public static String sqlTemplateDialogSelectObjectTableLabel;
    public static String sqlTemplateDialogSimpleDefaultProcLabel;
    public static String sqlTemplateDialogInsDefaultProcLabel;
    public static String sqlTemplateDialogUpdDefaultProcLabel;
    public static String sqlTemplateDialogDelDefaultProcLabel;
    public static String sqlTemplateDialogSoapCreateProcLabel;
    public static String sqlTemplateDialogSoapExtractProcLabel;
    public static String sqlTemplateDialogRestProcLabel;
    public static String sqlTemplateDialogSqlAreaGroup;
    public static String sqlTemplateDialogSelectTabTitle;
    public static String sqlTemplateDialogProceduresTabTitle;
    public static String sqlTemplateInsertTextOptionsTitle;
    public static String sqlTemplateReplaceAllOptionLabel;
    public static String sqlTemplateInsertAtBeginningOptionLabel;
    public static String sqlTemplateInsertAtCursorOptionLabel;
    public static String sqlTemplateInsertAtEndOptionLabel;
    public static String confirmSqlReplaceDialogTitle;
    public static String confirmSqlReplaceDialogMessage;
    public static String confirmSqlReplaceDialogMessage_2;
    
    public static String DefaultUpdateMessageOK;
    public static String DefaultUpdateMessageAmbigious;
    public static String DefaultUpdateMessageOverride;
    
    // Datatype Reconciler Panel
    public static String datatypeReconciler_helpText;

	public static String datatypeReconciler_convertAllColumnDatatypesLabel;
    public static String datatypeReconciler_convertAllColumnDatatypesTooltip;
    public static String datatypeReconciler_convertAllSqlSymbolsLabel;
    public static String datatypeReconciler_convertAllSqlSymbolsTooltip;
    public static String datatypeReconciler_targetColumnLabel;
    public static String datatypeReconciler_matchedTypeLabel;
    public static String datatypeReconciler_sourceSqlSymbolLabel;
    public static String datatypeReconciler_statusTitle;
    public static String datatypeReconciler_allResolvedMessage;
    public static String datatypeReconciler_someUnresolvedConflicts;
    public static String datatypeReconciler_selectionPanelInfoLabel;
    public static String datatypeReconciler_changeTargetDatatypeTooltip;
    public static String datatypeReconciler_matchedDatatypeTooltip;
    public static String datatypeReconciler_convertSourceDatatypeTooltip;
    
    public static String restOptions;
    public static String enableRestForThisProcedure;
    public static String restMethod;
    public static String restUri;
    public static String restUriTooltip;
    public static String restCharSet;
    public static String restHeaders;
    public static String restHeadersTooltip;
    
    public static String quickFixModelDirtyTitle;
    public static String quickFixModelDirtyMsg;
    public static String getSupportedPrefixesErrorMsg;
    public static String getModelMedErrorMsg;
    public static String saveModelMedErrorMsg;
    public static String saveModelErrorMsg;
    public static String restMedQuickFixLabel;
    
    public static String ExportTeiidDdlModelSelectionPage_modelGroupTitle;
    public static String ExportTeiidDdlModelSelectionPage_fileLabel;
    public static String ExportTeiidDdlModelSelectionPage_title;
    public static String ExportTeiidDdlModelSelectionPage_ddlExportOptions;
    public static String ExportTeiidDdlModelSelectionPage_nameInSourceOption;
    public static String ExportTeiidDdlModelSelectionPage_nativeTypeOption;
    
    static {
        NLS.initializeMessages("org.teiid.designer.transformation.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
