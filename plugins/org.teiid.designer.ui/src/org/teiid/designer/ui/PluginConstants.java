/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui;

import java.util.Arrays;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.ui.common.PreferenceKeyAndDefaultValue;
import org.teiid.designer.ui.common.UiConstants;


/**
 * This class is intended for use within this plugin only.
 * 
 * @since 8.0
 */
public interface PluginConstants {
    // ============================================================================================================================
    // Constants

    public static final String EMPTY_STRING = ""; //$NON-NLS-1$
    public static final String MODEL_PROJECT_NATURE_ID = ModelerCore.NATURE_ID;
    public static final String XML_EXTENSIONS_PROJECT_NAME = "XMLExtensionsProject"; //$NON-NLS-1$
    public static final List<String> HIDDEN_PROJECTS = Arrays.asList(XML_EXTENSIONS_PROJECT_NAME, ExtensionConstants.BUILTIN_MEDS_PROJECT_NAME);
     
    // ============================================================================================================================
    // Image constants

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
    interface Images extends UiConstants.Images {
        String BLANK_ICON = CVIEW16 + "blank.gif"; //$NON-NLS-1$
        String CLOSE_ICON = CVIEW16 + "close_view.gif"; //$NON-NLS-1$
        String CLOSE20_ICON = FULL + "cview20/close_view.gif"; //$NON-NLS-1$
        String OUTLINE_ICON = CVIEW16 + "outlineView.gif"; //$NON-NLS-1$
        String TABLE_ICON = CVIEW16 + "table.gif"; //$NON-NLS-1$
        String EXTENSIONS_ICON = CVIEW16 + "extensions.gif"; //$NON-NLS-1$
        String ERROR_ICON = OBJ16 + "error_obj.gif"; //$NON-NLS-1$
        String WARNING_ICON = OBJ16 + "warning_obj.gif"; //$NON-NLS-1$
        String ERROR_WARNING_ICON = CVIEW16 + "errorwarning_tab.gif"; //$NON-NLS-1$

        String IMPORT_CONTAINER = OBJ16 + "Imports.gif"; //$NON-NLS-1$
        String IMPORT_STATEMENT = OBJ16 + "Import.gif"; //$NON-NLS-1$

        String ERROR_DECORATOR = OVR16 + "error_co.gif"; //$NON-NLS-1$
        String WARNING_DECORATOR = OVR16 + "warning_co.gif"; //$NON-NLS-1$
        String EXTENSION_DECORATOR = OVR16 + "extension_ovr.gif"; //$NON-NLS-1$
        String PREVIEWABLE_DECORATOR = OVR16 + "run_co.gif"; //$NON-NLS-1$

        String EXPORT_ICON = CTOOL16 + "export_wiz.gif"; //$NON-NLS-1$
        String IMPORT_ICON = CTOOL16 + "import_wiz.gif"; //$NON-NLS-1$
        String IMPORT_DATABASE_ICON = WIZBAN + "importDatabase.gif"; //$NON-NLS-1$
        String EXPORT_DDL_ICON = WIZBAN + "exportDdl.gif"; //$NON-NLS-1$
        String EXPORT_PROJECT_ICON = WIZBAN + "export_project.gif"; //$NON-NLS-1$
        String IMPORT_PROJECT_ICON = WIZBAN + "import_project.gif"; //$NON-NLS-1$
        String CLONE_PROJECT_ICON = CVIEW16 + "clone_project.png"; //$NON-NLS-1$

        String MODEL = OBJ16 + "Model.gif"; //$NON-NLS-1$
        String VIRTUAL_MODEL = OBJ16 + "VirtualModel.gif"; //$NON-NLS-1$
        String OPEN_MODEL = OBJ16 + "OpenModel.gif"; //$NON-NLS-1$
        String OPEN_VIRTUAL_MODEL = OBJ16 + "OpenVirtualModel.gif"; //$NON-NLS-1$
        String LOGICAL_MODEL = OBJ16 + "LogicalModel.gif"; //$NON-NLS-1$
        String OPEN_LOGICAL_MODEL = OBJ16 + "OpenLogicalModel.gif"; //$NON-NLS-1$
        String XSD_MODEL = OBJ16 + "XSDSchema.gif"; //$NON-NLS-1$
        String VIEW_MODEL = OBJ16 + "ViewModel.gif"; //$NON-NLS-1$
        String XML_VIEW_MODEL = OBJ16 + "XmlViewModel.gif"; //$NON-NLS-1$
        String WEB_SERVICE_VIEW_MODEL = OBJ16 + "WebServiceViewModel.gif"; //$NON-NLS-1$
        String EXTENSION_MODEL = OBJ16 + "ExtensionModel.gif"; //$NON-NLS-1$
        String FUNCTION_MODEL = OBJ16 + "FunctionModel.gif"; //$NON-NLS-1$
        String XML_SERVICE_SOURCE_MODEL = OBJ16 + "XmlSourceModel.gif"; //$NON-NLS-1$
        String XML_SERVICE_VIEW_MODEL = OBJ16 + "XmlServiceViewModel.gif"; //$NON-NLS-1$
        String PEOPLE_MODEL = OBJ16 + "PeopleModel.gif"; //$NON-NLS-1$
        String RELATIONSHIP_MODEL = OBJ16 + "RelationshipModel.gif"; //$NON-NLS-1$
        String UML_MODEL = OBJ16 + "UmlModel.gif"; //$NON-NLS-1$
        String XML_MESSAGE_STRUCTURE_MODEL = OBJ16 + "XmlMessageStructureModel.gif"; //$NON-NLS-1$

        String MODEL_PROJECT = OBJ16 + "ModelProject.gif"; //$NON-NLS-1$
        String SIMPLE_PROJECT = OBJ16 + "SimpleProject.gif"; //$NON-NLS-1$

        String BUILTIN_DATATYPE = OBJ16 + "BuiltinDatatype.gif"; //$NON-NLS-1$
        
        String TABLE_VDB_SOURCE = OBJ16 + "table-vdb-source.png"; //$NON-NLS-1$
        String PROCEDURE_VDB_SOURCE = OBJ16 + "procedure-vdb-source.png"; //$NON-NLS-1$
        String VDB_SOURCE_MODEL = OBJ16 + "vdb-source-model.png"; //$NON-NLS-1$

        String METAMODEL = OBJ16 + "Metamodel.gif"; //$NON-NLS-1$

        String METADATA_SEARCH = CTOOL16 + "metadata_search.gif"; //$NON-NLS-1$
        String METADATA_FAVORITES = CVIEW16 + "metadata_favorites.gif"; //$NON-NLS-1$
        String CLEAR_ICON = CVIEW16 + "clear.gif"; //$NON-NLS-1$
        String ADD_ICON = CVIEW16 + "add.gif"; //$NON-NLS-1$
        String REMOVE_ICON = CVIEW16 + "remove.gif"; //$NON-NLS-1$
        String FIND_METADATA = CVIEW16 + "find_metadata.gif"; //$NON-NLS-1$
        String ALPHA_SORT_ICON = CVIEW16 + "alphab_sort.gif"; //$NON-NLS-1$
        String REFRESH_ICON = CVIEW16 + "refresh.gif"; //$NON-NLS-1$
        String COLLAPSE_ALL_ICON = CVIEW16 + "collapseall.gif"; //$NON-NLS-1$

        String MODEL_STATISTICS_ICON = CVIEW16 + "statistics.gif"; //$NON-NLS-1$
        String NEW_PUSHDOWN_FUNCTION = OBJ16 + "PushdownFunction.gif"; //$NON-NLS-1$
        String BUILD_MODEL_IMPORTS_ICON = OBJ16 + "Imports.gif"; //$NON-NLS-1$
        String FIND = CVIEW16 + "find.gif"; //$NON-NLS-1$
        String FIND_MODEL_OBJECT = CTOOL16 + "opentype.gif";  //$NON-NLS-1$
        String FIND_REPLACE = CVIEW16 + "find_replace.gif"; //$NON-NLS-1$

        String ENUM_OVERLAY_ICON = OVR16 + "enum_co.gif"; //$NON-NLS-1$

        String CREATE_WEB_SERVICE_ICON = CTOOL16 + "create_web_service.png"; //$NON-NLS-1$

        String CONFIGURE_ICON = CVIEW16 + "configure.png";  //$NON-NLS-1$

        String EDIT_DOCUMENT_ICON = CVIEW16 + "edit-document.png";  //$NON-NLS-1$
        String CLEAR_DOCUMENT_ICON = CVIEW16 + "clear-document.png";  //$NON-NLS-1$
        String EDIT_EXTENSION_PROPERTIES_ICON = CVIEW16 + "edit-extension-properties.png"; //$NON-NLS-1$

        String TEIID_SERVER_DEFAULT_ICON = CVIEW16 + "teiid-server-default.png"; //$NON-NLS-1$
        String TEIID_SERVER_DISCONNECTED_ICON = CVIEW16 + "teiid-server-disconnected.png"; //$NON-NLS-1$
        String JBOSS_SERVER_STOPPED_ICON = CVIEW16 + "jboss-stopped.png"; //$NON-NLS-1$
        String JBOSS_SERVER_STARTED_ICON = CVIEW16 + "jboss-started.png"; //$NON-NLS-1$
        
        String NEW_WIZARD_ICON = OBJ16 + "new-wizard.png"; //$NON-NLS-1$
        String NEW_MODEL_PROJECT_ICON = OBJ16 + "new-model-project.png"; //$NON-NLS-1$
        String NEW_SOURCE_MODEL_ICON = OBJ16 + "new-source-model.png"; //$NON-NLS-1$
        String NEW_VIEW_MODEL_ICON = OBJ16 + "new-view-model.png"; //$NON-NLS-1$
        String NEW_WEB_SERVICE_MODEL_ICON = OBJ16 + "new-web-service-model.png"; //$NON-NLS-1$
        String NEW_XML_DOCUMENT_MODEL_ICON = OBJ16 + "new-xml-document-model.png"; //$NON-NLS-1$
        String NEW_VDB_ICON = OBJ16 + "newvdb_wiz.gif"; //$NON-NLS-1$
        
        String RESTORE_DEFAULT_VALUE = CTOOL16 + "restore-default-value.png"; //$NON-NLS-1$
    }

    // ======================================================================================================
    // Preference store keys for preferences

    interface Prefs {
        interface General {
            String LOCK_TARGET_VIRTUAL_ATTRIBUTES_BY_DEFAULT = "lockTargetVirtualAttributesByDefault"; //$NON-NLS-1$
            String SHOW_MODEL_CONTENTS_IN_MODEL_EXPLORER = "showModelContentsInModelExplorer"; //$NON-NLS-1$
            String SORT_MODEL_CONTENTS = "sortModelContents"; //$NON-NLS-1$
            String AUTO_OPEN_EDITOR_IF_NEEDED = "autoOpenEditorIfNeeded"; //$NON-NLS-1$
            String AUTO_UPDATE_IMPORTS_ON_SAVE = "autoUpdateImportsOnSave"; //$NON-NLS-1$
            String LOGICAL_AS_RELATIONAL = "logicalAsRelational"; //$NON-NLS-1$
            String AUTO_OPEN_PERSPECTIVE_WHEN_MODEL_EDITOR_OPENED = "autoOpenPerspectiveWhenModelEditorOpened"; //$NON-NLS-1$
            String SHOW_NON_MODELS_IN_MODEL_EXPLORER = "showNonModelsInModelExplorer"; //$NON-NLS-1$
            String SHOW_IMPORTS_IN_MODEL_EXPLORER = "showImportsInModelExplorer"; //$NON-NLS-1$
            String AUTO_WILL_TOGGLE_WITH_CHILDREN = "autoWillToggleChildren"; //$NON-NLS-1$
            PreferenceKeyAndDefaultValue[] PREFERENCES = new PreferenceKeyAndDefaultValue[] {
                new PreferenceKeyAndDefaultValue(LOCK_TARGET_VIRTUAL_ATTRIBUTES_BY_DEFAULT, new Boolean(false)),
                new PreferenceKeyAndDefaultValue(SHOW_MODEL_CONTENTS_IN_MODEL_EXPLORER, new Boolean(true)),
                new PreferenceKeyAndDefaultValue(SORT_MODEL_CONTENTS, new Boolean(false)),
                new PreferenceKeyAndDefaultValue(AUTO_OPEN_EDITOR_IF_NEEDED, new Boolean(false)),
                new PreferenceKeyAndDefaultValue(AUTO_OPEN_PERSPECTIVE_WHEN_MODEL_EDITOR_OPENED, MessageDialogWithToggle.PROMPT),
                new PreferenceKeyAndDefaultValue(AUTO_UPDATE_IMPORTS_ON_SAVE, new Boolean(true)),
                new PreferenceKeyAndDefaultValue(SHOW_NON_MODELS_IN_MODEL_EXPLORER, new Boolean(true)),
                new PreferenceKeyAndDefaultValue(SHOW_IMPORTS_IN_MODEL_EXPLORER, new Boolean(true)),
                new PreferenceKeyAndDefaultValue(ITeiidServerManager.DEFAULT_TEIID_SERVER_VERSION_ID, TeiidServerVersion.deriveUltimateDefaultServerVersion()),
                new PreferenceKeyAndDefaultValue(AUTO_WILL_TOGGLE_WITH_CHILDREN, new Boolean(false)),
                };
        }
    }

    interface Transactions {
        boolean SIGNIFICANT = true;
        boolean INSIGNIFICANT = false;
        boolean NOT_UNDOABLE = false;
        boolean UNDOABLE = true;
    }
}
