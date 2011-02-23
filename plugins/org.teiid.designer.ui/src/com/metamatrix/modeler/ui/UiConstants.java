/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui;

import java.util.ResourceBundle;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IPageLayout;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;

/**
 * UiConstants
 * 
 * @since 4.0
 */
public interface UiConstants {
    // ============================================================================================================================
    // Constants

    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.ui"; //$NON-NLS-1$

    String PACKAGE_ID = UiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
        protected static final String WIZARDS = PACKAGE_ID + ".wizards."; //$NON-NLS-1$
        protected static final String EXPLORER = "explorer"; //$NON-NLS-1$
        protected static final String SEARCH = PACKAGE_ID + ".search."; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    // ============================================================================================================================
    // Extension constants

    /**
     * Constants related to extensions, including all extension ID's.
     * 
     * @since 4.0
     */
    interface Extensions {
        // Perspectives
        String PERSPECTIVE = "modelerPerspective"; //$NON-NLS-1$
        String OUTPUT_FOLDER = PACKAGE_ID + ".outputFolder"; //$NON-NLS-1$
        String PROPERTY_FOLDER = PACKAGE_ID + ".propertyFolder"; //$NON-NLS-1$
        String TREE_FOLDER = PACKAGE_ID + ".treeFolder"; //$NON-NLS-1$
        String CENTER_LEFT_FOLDER = PACKAGE_ID + ".centerLeftFolder"; //$NON-NLS-1$

        // Wizards
        String NEW_MODEL_WIZARD = "newModel"; //$NON-NLS-1$
        String NEW_PROJECT_WIZARD = "newModelProject"; //$NON-NLS-1$
        String NEW_VDB_WIZARD = "newVdbWizard"; //$NON-NLS-1$
        String NEW_FOLDER_WIZARD = "org.eclipse.ui.wizards.new.folder"; //$NON-NLS-1$

        // Views
        String DATATYPE_HIERARCHY_VIEW = "views.datatypeHierarchyView"; //$NON-NLS-1$
        String DESCRIPTION_VIEW = "views.descriptionView"; //$NON-NLS-1$
        String METAMODELS_VIEW = "views.metamodelsView"; //$NON-NLS-1$
        String ERROR_LOG_VIEW = "views.logView"; //$NON-NLS-1$
        String TAGS_VIEW = "views.tagsView"; //$NON-NLS-1$
        String FAVORITES_VIEW_ID = "views.favoritesView"; //$NON-NLS-1$
        //        String PROBLEM_VIEW_ID         = PC.VIEWS + "problemView"; //$NON-NLS-1$
        String OUTLINE_VIEW = IPageLayout.ID_OUTLINE;
        String PROPERTY_VIEW = IPageLayout.ID_PROP_SHEET;
        String SEARCH_RESULT_VIEW = NewSearchUI.SEARCH_VIEW_ID;
        String SYSTEM_CATALOG_VIEW = "views.systemModelView"; //$NON-NLS-1$
        String ECLIPSE_CHEAT_SHEET_VIEW = "org.eclipse.ui.cheatsheets.views.CheatSheetView";//$NON-NLS-1$

        // Model Editor Pages
        String TABLE_EDITOR = "tableEditorPage"; //$NON-NLS-1$
        String TEXT_EDITOR = "textEditorPage"; //$NON-NLS-1$

        // Views from other plugins
        //        String NAVIGATOR_VIEW = "com.metamatrix.modeler.relationship.ui.navigation.navigationView"; //$NON-NLS-1$

        // Search
        String METADATA_SEARCH_PAGE = "metadataSearchPage"; //$NON-NLS-1$

        /**
         * Constants related to extensions that apply to the model explorer.
         * 
         * @since 4.0
         */
        interface Explorer {
            // Explorer components
            String VIEW = PC.EXPLORER + ".view"; //$NON-NLS-1$
            String DECORATOR = PC.EXPLORER + ".decorator"; //$NON-NLS-1$
            String CONTEXT_MENU = PC.EXPLORER + IModelerActionConstants.ContextMenu.MENU_ID_SUFFIX;
        }

        // DQP UI
        String PREVIEW_DATA_ACTION_ID = "com.metamatrix.modeler.internal.dqp.ui.actions.PreviewTableDataContextAction"; //$NON-NLS-1$
    }

    // ============================================================================================================================
    // Extension Point constants

    /**
     * Constants related to extension points, including all extension point ID's and extension point schema component names.
     * 
     * @since 4.0
     */
    interface ExtensionPoints {

        /** Constants for the DiagramContentProvider extension point */
        interface DiagramContentProvider {
            String DIAGRAM_ID = "diagramContentProvider"; //$NON-NLS-1$
            String DIAGRAM_CLASS = "class"; //$NON-NLS-1$
            String DIAGRAM_CLASSNAME = "name"; //$NON-NLS-1$
            String DIAGRAM_TYPE = "diagramType"; //$NON-NLS-1$
            String DIAGRAM_TYPE_NAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the DiagramLabelProvider extension point */
        interface DiagramLabelProvider {
            String DIAGRAM_ID = "diagramLabelProvider"; //$NON-NLS-1$
            String DIAGRAM_CLASS = "class"; //$NON-NLS-1$
            String DIAGRAM_CLASSNAME = "name"; //$NON-NLS-1$
            String DIAGRAM_TYPE = "diagramType"; //$NON-NLS-1$
            String DIAGRAM_TYPE_NAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the ExtendedModelContentProvider extension point */
        interface ExtendedModelContentProvider {
            String EXTENDED_MODEL_ID = "extendedModelContentProvider"; //$NON-NLS-1$
            String EXTENDED_MODEL_CLASS = "class"; //$NON-NLS-1$
            String EXTENDED_MODEL_CLASSNAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the ExtendedModelLabelProvider extension point */
        interface ExtendedModelLabelProvider {
            String EXTENDED_MODEL_ID = "extendedModelLabelProvider"; //$NON-NLS-1$
            String EXTENDED_MODEL_CLASS = "class"; //$NON-NLS-1$
            String EXTENDED_MODEL_CLASSNAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the exportWizards extension point */
        interface ExportWizards {
            //            String ID = "com.metamatrix.modeler.ui.exportWizards"; //$NON-NLS-1$
            String ID = "org.eclipse.ui.exportWizards"; //$NON-NLS-1$
            // Attributes
            String CLASS = "class"; //$NON-NLS-1$
            String ICON = "icon"; //$NON-NLS-1$
            String NAME = "name"; //$NON-NLS-1$
            String ID_ID = "id"; //$NON-NLS-1$
        }

        /** Constants for the importWizards extension point */
        interface ImportWizards {
            //            String ID = "com.metamatrix.modeler.ui.importWizards"; //$NON-NLS-1$
            String ID = "org.eclipse.ui.importWizards"; //$NON-NLS-1$
            // Attributes
            String CLASS = "class"; //$NON-NLS-1$
            String ICON = "icon"; //$NON-NLS-1$
            String NAME = "name"; //$NON-NLS-1$
            String ID_ID = "id"; //$NON-NLS-1$
        }

        /** Constants for the ModelEditorPage extension point */
        interface ModelEditorPage {
            String ID = "modelEditorPage"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String CONTRIBUTOR_CLASSNAME = "contributorName"; //$NON-NLS-1$
            String TABNAME = "tabName"; //$NON-NLS-1$
            String ORDER = "order"; //$NON-NLS-1$
        }

        /** Constants for the ModelObjectActionContributor extension point */
        interface ModelObjectActionContributor {
            String ID = "modelObjectActionContributor"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the ModelObjectEditorPage extension point */
        interface ModelObjectEditorPage {
            String ID = "modelObjectEditorPage"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the NewAssociationWizard extension point */
        // NewModelObjectWizardManager depends on all constands besides ID being same as NewModelObjectWizard
        interface NewAssociationWizard {
            String ID = "newAssociationWizard"; //$NON-NLS-1$
            String CLASS_ELEMENT = NewModelObjectWizard.CLASS_ELEMENT;
            String CLASSNAME = NewModelObjectWizard.CLASSNAME;
            String DESCRIPTOR_ELEMENT = NewModelObjectWizard.DESCRIPTOR_ELEMENT;
            String DESCRIPTOR_ID = NewModelObjectWizard.DESCRIPTOR_ID;
        }

        /** Constants for the NewModelObjectWizard extension point */
        interface NewModelObjectWizard {
            String ID = "newModelObjectWizard"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String DESCRIPTOR_ELEMENT = "descriptorType"; //$NON-NLS-1$
            String DESCRIPTOR_ID = "name"; //$NON-NLS-1$
        }

        /** Constants for the NewModelWizardContributor extension point */
        interface NewModelWizardContributor {
            String ID = "newModelWizardContributor"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String ICON = "icon"; //$NON-NLS-1$
            String NAME = "name"; //$NON-NLS-1$
            String METAMODEL = "metamodelDescriptorType"; //$NON-NLS-1$
            String IS_VIRTUAL = "isVirtual"; //$NON-NLS-1$
            String IS_PHYSICAL = "isPhysical"; //$NON-NLS-1$
            String ANY = "all"; //$NON-NLS-1$
        }

        /** Constants for the NewChildAction extension point */
        interface NewChildExtension {
            String ID = "newChildAction"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the NewSiblingAction extension point */
        interface NewSiblingExtension {
            String ID = "newSiblingAction"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        /** Constants for the NewChildAction extension point */
        interface DiagramHelperExtension {
            String ID = "diagramHelper"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String DIAGRAM = "diagram"; //$NON-NLS-1$
            String DIAGRAM_TYPE = "diagramType"; //$NON-NLS-1$
        }

        /** Constants for the NewChildAction extension point */
        interface ModelObjectEditHelperExtension {
            String ID = "modelObjectEditHelper"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        interface MetadataPasteSpecialExtension {
            String ID = "metadataPasteSpecial"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "classname"; //$NON-NLS-1$
            String LABEL = "label"; //$NON-NLS-1$
            String DESCRIPTION = "description"; //$NON-NLS-1$
        }

        interface MetadataRefreshExtension {
            String ID = "metadataRefresh"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "classname"; //$NON-NLS-1$
            String LABEL = "label"; //$NON-NLS-1$
            String DESCRIPTION = "description"; //$NON-NLS-1$
        }

        interface PropertyEditorFactoryExtension {
            String ID = "propertyEditorFactory"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        interface ModelerPerspectiveContributorExtension {
            String ID = "modelerPerspectiveContributor"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        interface ModelResourceActionExtension {
            String ID = "modelResourceAction"; //$NON-NLS-1$
            String RESOURCE_ACTION = "resourceAction"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String LABEL = "label"; //$NON-NLS-1$
        }

        interface ModelerSpecialActionExtension {
            String ID = "modelObjectSpecialAction"; //$NON-NLS-1$
            String SPECIAL_ACTION = "specialAction"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String LABEL = "label"; //$NON-NLS-1$
        }

        interface GeneralPreferenceContributor {
            String ID = "generalPreferenceContributor"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class"; //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
            String CATEGORY_ELEMENT = "category"; //$NON-NLS-1$
            String CATEGORY_ID_ATTRIBUTE = "id"; //$NON-NLS-1$
            String CATEGORY_NAME_ATTRIBUTE = "name"; //$NON-NLS-1$
            String PREFERENCE_ELEMENT = "preference"; //$NON-NLS-1$
            String PREFERENCE_ID_ATTRIBUTE = "id"; //$NON-NLS-1$
            String PREFERENCE_CATEGORY_ID_ATTRIBUTE = "categoryId"; //$NON-NLS-1$
            String PREFERENCE_CONTRIBUTOR_CLASS_ATTRIBUTE = "class"; //$NON-NLS-1$
        }
    }

    interface Navigation {
        String TAB_INDEX = "tabIndex"; //$NON-NLS-1$ 
        String SELECTION = "selection"; //$NON-NLS-1$ 
        String CURRENT_INPUT = "currentInput"; //$NON-NLS-1$
        String CURRENT_SELECTION = "currentSelection"; //$NON-NLS-1$
        String MARKER_TYPE = "markerType"; //$NON-NLS-1$
        String NAVIGATION = "navigation"; //$NON-NLS-1$
        String UNKNOWN = "unknown"; //$NON-NLS-1$
        String PAGE_EDITOR = "pageEditor"; //$NON-NLS-1$
        String DELEGATE = "delegate"; //$NON-NLS-1$
        String DELEGATES_MARKER = "delegatesMarker"; //$NON-NLS-1$

    }

    interface ObjectEditor {
        int IGNORE_OPEN_EDITOR = 0;
        int FORCE_CLOSE_EDITOR = 1;
        int FORCE_OPEN_EDITOR = 2;
        int REFRESH_EDITOR_IF_OPEN = 3;
    }

    interface TableEditorAttributes {
        String COLUMN_ORDER = "columnOrder"; //$NON-NLS-1$
    }

    public static final String DESCRIPTION_KEY = Util.getString("ModelObjectTableModel.descriptionColumnName"); //$NON-NLS-1$
    public static final String LOCATION_KEY = Util.getString("ModelObjectTableModel.locationColumnName"); //$NON-NLS-1$        

    interface NamingAttributes {
        // List of invalid characters for project naming
        char[] VALID_PROJECT_CHARS = {'-', '_'};
    }
}
