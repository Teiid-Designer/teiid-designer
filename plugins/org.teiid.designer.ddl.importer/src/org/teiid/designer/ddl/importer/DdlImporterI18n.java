/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ddl.importer;

import static org.teiid.designer.ddl.importer.DdlImporterPlugin.i18n;

/**
 * i18n constants
 */
public interface DdlImporterI18n {
    String CREATING_CHANGE_REPORT_MSG = i18n("creatingChangeReportMsg"); //$NON-NLS-1$
    String CREATING_MODEL_MSG = i18n("creatingModelMsg"); //$NON-NLS-1$
    String DDL_FILE_NOT_FOUND_MSG = i18n("ddlFileNotFoundMsg"); //$NON-NLS-1$
    String ENTITY_NOT_FOUND_MSG = i18n("entityNotFoundMsg"); //$NON-NLS-1$
    String INVALID_MODEL_FOLDER_MSG = i18n("invalidModelFolderMsg"); //$NON-NLS-1$
    String INVALID_MODEL_NAME_MSG = i18n("invalidModelNameMsg"); //$NON-NLS-1$
    String MODEL = i18n("model"); //$NON-NLS-1$
    String MODEL_FOLDER_IN_NON_MODEL_PROJECT_MSG = i18n("modelFolderInNonModelProjectMsg"); //$NON-NLS-1$
    String MODEL_FOLDER_IS_FILE_MSG = i18n("modelFolderIsFileMsg"); //$NON-NLS-1$
    String MODEL_NAME_IS_FOLDER_MSG = i18n("modelNameIsFolderMsg"); //$NON-NLS-1$
    String MODEL_NAME_IS_NON_MODEL_FILE_MSG = i18n("modelNameIsNonModelFileMsg"); //$NON-NLS-1$
    String MODEL_NAME_IS_NON_RELATIONAL_MODEL_MSG = i18n("modelNameIsNonRelationalModelMsg"); //$NON-NLS-1$
    String PARSING_DDL_MSG = i18n("parsingDdlMsg"); //$NON-NLS-1$
    String SAVING_MODEL_MSG = i18n("savingModelMsg"); //$NON-NLS-1$
    String FK_TABLE_REF_NOT_FOUND_MSG = i18n("fkTableRefNotFoundMsg"); //$NON-NLS-1$
    String FAILURE_IMPORT_MSG = i18n("importFailureMsg"); //$NON-NLS-1$
}
