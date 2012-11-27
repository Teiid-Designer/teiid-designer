/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.komodo.ui;


/**
 * UiConstants
 * 
 * @since 1.0
 */
public interface UiConstants {
    // ============================================================================================================================
    // Constants

    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.komodo.ui"; //$NON-NLS-1$

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
    
    // ============================================================================================================================
    // Extension constants

    /**
     * Constants related to extensions, including all extension ID's.
     * 
     * @since 4.0
     */
    interface Extensions {
        // Perspectives
        String PERSPECTIVE = "komodoPerspective"; //$NON-NLS-1$
        String OUTPUT_FOLDER = PACKAGE_ID + ".outputFolder"; //$NON-NLS-1$
        String PROPERTY_FOLDER = PACKAGE_ID + ".propertyFolder"; //$NON-NLS-1$
        String TREE_FOLDER = PACKAGE_ID + ".treeFolder"; //$NON-NLS-1$
        String CENTER_LEFT_FOLDER = PACKAGE_ID + ".centerLeftFolder"; //$NON-NLS-1$
        String TOP_RIGHT_FOLDER = PACKAGE_ID + ".topRightFolder"; //$NON-NLS-1$

        // Wizards
        String NEW_VDB_WIZARD = "newVdbWizard"; //$NON-NLS-1$
        String NEW_MED_WIZARD = "newMedWizard"; //$NON-NLS-1$
        String NEW_FOLDER_WIZARD = "org.eclipse.ui.wizards.new.folder"; //$NON-NLS-1$

        // Views
        String DATATYPE_HIERARCHY_VIEW = "views.datatypeHierarchyView"; //$NON-NLS-1$
        String DESCRIPTION_VIEW = "views.descriptionView"; //$NON-NLS-1$
        String ERROR_LOG_VIEW = "org.eclipse.pde.runtime.LogView"; //"views.logView"; //$NON-NLS-1$
        String TAGS_VIEW = "views.tagsView"; //$NON-NLS-1$
        String FAVORITES_VIEW_ID = "views.favoritesView"; //$NON-NLS-1$
        String SYSTEM_CATALOG_VIEW = "views.systemModelView"; //$NON-NLS-1$
        String SQL_RESERVED_WORDS_VIEW = "views.sqlReservedWordsView"; //$NON-NLS-1$
        String ECLIPSE_CHEAT_SHEET_VIEW = "org.eclipse.ui.cheatsheets.views.CheatSheetView";//$NON-NLS-1$

        // DQP UI
        String PREVIEW_DATA_ACTION_ID = "org.teiid.designer.runtime.ui.preview.PreviewTableDataContextAction"; //$NON-NLS-1$
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

        /** Constants for the exportWizards extension point */
        interface ExportWizards {
            //            String ID = "org.teiid.designer.ui.exportWizards"; //$NON-NLS-1$
            String ID = "org.eclipse.ui.exportWizards"; //$NON-NLS-1$
            // Attributes
            String CLASS = "class"; //$NON-NLS-1$
            String ICON = "icon"; //$NON-NLS-1$
            String NAME = "name"; //$NON-NLS-1$
            String ID_ID = "id"; //$NON-NLS-1$
        }

        /** Constants for the importWizards extension point */
        interface ImportWizards {
            //            String ID = "org.teiid.designer.ui.importWizards"; //$NON-NLS-1$
            String ID = "org.eclipse.ui.importWizards"; //$NON-NLS-1$
            // Attributes
            String CLASS = "class"; //$NON-NLS-1$
            String ICON = "icon"; //$NON-NLS-1$
            String NAME = "name"; //$NON-NLS-1$
            String ID_ID = "id"; //$NON-NLS-1$
        }

        /** Constants for the NewChildAction extension point */
        interface DiagramHelperExtension {
            String ID = "diagramHelper"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String DIAGRAM = "diagram"; //$NON-NLS-1$
            String DIAGRAM_TYPE = "diagramType"; //$NON-NLS-1$
        }

        interface PropertyEditorFactoryExtension {
            String ID = "propertyEditorFactory"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
        }

        interface KomodoPerspectiveContributorExtension {
            String ID = "komodoPerspectiveContributor"; //$NON-NLS-1$
            String CLASS = "class"; //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
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

}
