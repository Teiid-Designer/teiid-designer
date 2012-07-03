/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    public static String Browse;
    public static String Edit;
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
    
    public static String createRelationalViewActionText;
    public static String createRelationalViewTitle;
    public static String createRelationalViewInitialMessage;
    public static String createRelationalViewExceptionMessage;
    public static String createRelationalViewHelpText;
    
    public static String createRelationalViewProcedureActionText;
    public static String createRelationalViewProcedureTitle;
    public static String createRelationalViewProcedureInitialMessage;
    public static String createRelationalViewProcedureExceptionMessage;
    public static String createRelationalViewProcedureHelpText;

    public static String sqlLabel;
    public static String sqlDescriptionLabel;
    public static String sqlTemplateLabel;
    public static String sqlGroupLabel;

    public static String nameLabel;
    public static String propertiesLabel;
    public static String addLabel;
    public static String deleteLabel;
    public static String moveUpLabel;
    public static String moveDownLabel;

    public static String description;
    public static String modelFileLabel;
    public static String nameInSourceLabel;
    public static String cardinalityLabel;
    public static String supportsUpdateLabel;
    public static String columnsLabel;
    public static String columnNameLabel;
    public static String dataTypeLabel;
    public static String lengthLabel;
    public static String parametersLabel;
    public static String parameterNameLabel;
    public static String cardinalityErrorTitle;
    public static String cardinalityMustBeAnInteger;
    public static String validationOkCreateObject;

    public static String sqlTemplateDialogTitle;
    public static String sqlTemplateDialogTitleMessage;
    public static String sqlTemplateDialogOptionsGroup;
    public static String sqlTemplateDialogSelectLabel;
    public static String sqlTemplateDialogSelectJoinLabel;
    public static String sqlTemplateDialogUnionLabel;
    public static String sqlTemplateDialogFlatFileSrcLabel;
    public static String sqlTemplateDialogXmlFileLocalSrcLabel;
    public static String sqlTemplateDialogXmlFileUrlSrcLabel;
    public static String sqlTemplateDialogInsDefaultProcLabel;
    public static String sqlTemplateDialogUpdDefaultProcLabel;
    public static String sqlTemplateDialogDelDefaultProcLabel;
    public static String sqlTemplateDialogSoapCreateProcLabel;
    public static String sqlTemplateDialogSoapExtractProcLabel;
    public static String sqlTemplateDialogRestProcLabel;
    public static String sqlTemplateDialogSqlAreaGroup;
    public static String sqlTemplateDialogSelectTabTitle;
    public static String sqlTemplateDialogProceduresTabTitle;
    
    public static String DefaultUpdateMessageOK;
    public static String DefaultUpdateMessageAmbigious;
    public static String DefaultUpdateMessageOverride;
    
    
    static {
        NLS.initializeMessages("com.metamatrix.modeler.transformation.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
