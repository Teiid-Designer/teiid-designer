/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.product;

import com.metamatrix.modeler.ui.actions.IModelerRcpActionIds;
import com.metamatrix.ui.product.IProductContext;
import com.metamatrix.ui.product.IProductContexts;
import com.metamatrix.ui.product.ProductContext;

/**
 * Collection of Modeler product contexts used to determine if features are supported by the current application.
 * 
 * @since 4.4
 */
public interface IModelerProductContexts extends IProductContexts {

    /**
     * A list of categories used in defining the product contexts. Not visible outside the interface.
     * 
     * @since 4.3
     */
    class Categories {
        /**
         * Metadata context category.
         * 
         * @since 4.4
         */
        private static final String METADATA = "metadata."; //$NON-NLS-1$

        /**
         * Menus context category.
         * 
         * @since 4.4
         */
        private static final String MENUS = "menus."; //$NON-NLS-1$

        /**
         * Contributions context category.
         * 
         * @since 4.4
         */
        private static final String CONTRIBUTIONS = "contributions"; //$NON-NLS-1$

        /**
         * Model Resource Explorer View category.
         * 
         * @since 4.4
         */
        private static final String VIEWS = "views."; //$NON-NLS-1$

        /**
         * Preferences category.
         * 
         * @since 5.0
         */
        private static final String PREFERENCES = "preferences."; //$NON-NLS-1$
    }

    /**
     * Collection of {@link IProductContext}s supporting metamodel-related concepts.
     * 
     * @since 4.4
     */
    interface Metamodel {
        /**
         * The Metamodel URI context can be used to determine if a product supports a specific metamodel URI.
         * 
         * @since 4.4
         */
        IProductContext URI = new ProductContext(Categories.METADATA, "metamodeluri"); //$NON-NLS-1$

        /**
         * The Metamodel URI context can be used to determine if a product supports a specific metamodel URI that are visible to
         * the user
         * 
         * @since 5.0
         */
        IProductContext USER_VISIBLE_URI = new ProductContext(Categories.METADATA, "visiblemetamodeluri"); //$NON-NLS-1$
    }

    /**
     * Collection of {@link IProductContext}s supporting license-related concepts.
     * 
     * @since 4.4
     */
    interface Contributions {
        /**
         * The context used to determine if a import wizard capability is supported by a product.
         * 
         * @since 4.3
         * @see com.metamatrix.modeler.ui.UiConstants.ProductInfo.Capabilities.IMPORT
         */
        IProductContext IMPORT = new ProductContext(Categories.CONTRIBUTIONS, "import"); //$NON-NLS-1$

        /**
         * The context used to determine if a import wizard capability is supported by a product.
         * 
         * @since 4.3
         * @see com.metamatrix.modeler.ui.UiConstants.ProductInfo.Capabilities.EXPORT
         */
        IProductContext EXPORT = new ProductContext(Categories.CONTRIBUTIONS, "export"); //$NON-NLS-1$

        /**
         * The context used to determine if a new model wizard contribution is supported by a product.
         * 
         * @since 5.0
         */
        IProductContext NEW_WIZARD = new ProductContext(Categories.CONTRIBUTIONS, "newWizard"); //$NON-NLS-1$
    }

    /**
     * A collection of {@link IProductContext}s specific to the {@link com.metamatrix.modeler.vdbview.ui.views.VdbView}.
     * 
     * @since 4.4
     */
    interface Views {
        /**
         * The context used to determine if the ModelExplorerResourceNavigator should use model project filter.
         * 
         * @since 4.4
         */
        IProductContext ID_MODEL_PROJECT_FILTER = new ProductContext(Categories.VIEWS, "modelProjectFilter"); //$NON-NLS-1$
    }

    /**
     * A collection of {@link IProductContext}s specific to preference pages.
     * 
     * @since 5.0
     */
    interface PreferencePages {
        /**
         * The context used to determine if modeler preference page should include the functionality of importing and exporting
         * preferences.
         * 
         * @since 5.0
         */
        IProductContext ID_IMPORT_EXPORT = new ProductContext(Categories.PREFERENCES, "modelProjectFilter"); //$NON-NLS-1$
    }

    /**
     * Collection of {@link IProductContext}s supporting action-related concepts.
     * 
     * @since 4.4
     */
    interface Actions {
        /**
         * The context used to determine if a main menu is supported by a product. Use the menu identifier as the value being
         * checked.
         * 
         * @see com.metamatrix.modeler.ui.actions.IModelerRcpActionIds
         * @since 4.4
         */
        IProductContext MAIN_MENU = new ProductContext(Categories.MENUS, "mainMenu"); //$NON-NLS-1$

        /**
         * The context used to determine if a submenu is supported by a product. Use the menu identifier as the value being
         * checked.
         * 
         * @see com.metamatrix.modeler.ui.actions.IModelerRcpActionIds
         * @since 4.4
         */
        IProductContext SUB_MENU = new ProductContext(Categories.MENUS, "subMenu"); //$NON-NLS-1$

        /**
         * The context used to determine if an action is supported by a product. Use the action dentifier as the value being
         * checked.
         * 
         * @see com.metamatrix.modeler.ui.actions.IModelerRcpActionIds
         * @since 4.4
         */
        IProductContext ACTION = new ProductContext(Categories.MENUS, "action"); //$NON-NLS-1$

        /**
         * The context used to determine if an action is supported by a product. Use the action dentifier as the value being
         * checked. Use the full class name for the value (i.e. com.metamatrix.modeler.internal.ui.ShowModelStatisticsAction)
         * 
         * @see com.metamatrix.modeler.ui.actions.IModelerRcpActionIds
         * @since 4.4
         */
        IProductContext MODEL_RESOURCE_ACTION_GROUP = new ProductContext(Categories.MENUS, "modelResourceActionGroup"); //$NON-NLS-1$

        /**
         * The context used to determine if an action is supported by a product. Use the action dentifier as the value being
         * checked. Use the full class name for the value (i.e. com.metamatrix.modeler.internal.ui.GenerateSqlRelationshipsAction)
         * 
         * @see com.metamatrix.modeler.ui.actions.IModelerRcpActionIds
         * @since 4.4
         */
        IProductContext MODEL_OBJECT_SPECIAL_ACTION_GROUP = new ProductContext(Categories.MENUS, "modelObjectSpecialActionGroup"); //$NON-NLS-1$

    }

    /**
     * A collection of values used when checking the {@link IModelerProductContexts.Actions#ACTION} product context.
     * 
     * @since 4.4
     */
    interface ActionValues {
        /**
         * About action ID.
         * 
         * @since 4.4
         */
        String ID_ABOUT_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "about"; //$NON-NLS-1$

        /**
         * Clone action ID.
         * 
         * @since 4.4
         */
        String ID_CLONE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "clone"; //$NON-NLS-1$

        /**
         * CopyFullName action ID.
         * 
         * @since 4.4
         */
        String ID_COPY_FULL_NAME_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "copyFullName"; //$NON-NLS-1$

        /**
         * CopyFullName action ID.
         * 
         * @since 4.4
         */
        String ID_COPY_NAME_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "copyName"; //$NON-NLS-1$

        /**
         * Close action ID.
         * 
         * @since 4.4
         */
        String ID_CLOSE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "close"; //$NON-NLS-1$

        /**
         * Close All editors action ID.
         * 
         * @since 4.4
         */
        String ID_CLOSEALL_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "closeAll"; //$NON-NLS-1$

        /**
         * Copy action ID.
         * 
         * @since 4.4
         */
        String ID_COPY_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "copy"; //$NON-NLS-1$

        /**
         * Cut action ID.
         * 
         * @since 4.4
         */
        String ID_CUT_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "cut"; //$NON-NLS-1$

        /**
         * Delete action ID.
         * 
         * @since 4.4
         */
        String ID_DELETE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "delete"; //$NON-NLS-1$

        /**
         * Dynamic help action ID.
         * 
         * @since 4.4
         */
        String ID_DYNAMIC_HELP_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "dynamicHelp"; //$NON-NLS-1$

        /**
         * Edit action ID.
         * 
         * @since 4.4
         */
        String ID_EDIT_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "edit"; //$NON-NLS-1$

        /**
         * Exit action ID.
         * 
         * @since 4.4
         */
        String ID_EXIT_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "exit"; //$NON-NLS-1$

        /**
         * Export action ID.
         * 
         * @since 4.4
         */
        String ID_EXPORT_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "export"; //$NON-NLS-1$

        /**
         * The File menu MRU.
         * 
         * @since 5.0.1
         */
        String ID_FILE_MRU_MENU = IModelerRcpActionIds.Prefixes.SUBMENU + "fileMru"; //$NON-NLS-1$;

        /**
         * Help contents action ID.
         * 
         * @since 4.4
         */
        String ID_HELP_CONTENTS_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "helpContents"; //$NON-NLS-1$

        /**
         * Help search action ID.
         * 
         * @since 4.4
         */
        String ID_HELP_SEARCH_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "helpSearch"; //$NON-NLS-1$

        /**
         * Import action ID.
         * 
         * @since 4.4
         */
        String ID_IMPORT_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "import"; //$NON-NLS-1$

        /**
         * Lock Tool Bar action ID.
         * 
         * @since 4.4
         */
        String ID_LOCK_TOOL_BAR_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "lockToolBar"; //$NON-NLS-1$

        /**
         * Move action ID.
         * 
         * @since 4.4
         */
        String ID_MOVE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "move"; //$NON-NLS-1$

        /**
         * New wizard drop down action ID.
         * 
         * @since 4.4
         */
        String ID_NEW_WIZARD_DROP_DOWN_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "newWizardDropDown"; //$NON-NLS-1$

        /**
         * Open action ID.
         * 
         * @since 4.4
         */
        String ID_OPEN_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "open"; //$NON-NLS-1$

        /**
         * Find action ID.
         * 
         * @since 5.0.1
         */
        String ID_FIND_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "find"; //$NON-NLS-1$

        /**
         * Open preferences ID.
         * 
         * @since 4.4
         */
        String ID_OPEN_PREFERENCES_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "openPreferences"; //$NON-NLS-1$

        /**
         * Paste action ID.
         * 
         * @since 4.4
         */
        String ID_PASTE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "paste"; //$NON-NLS-1$

        /**
         * Paste action ID.
         * 
         * @since 4.4
         */
        String ID_PASTE_SPECIAL_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "pasteSpecial"; //$NON-NLS-1$

        /**
         * Print action ID.
         * 
         * @since 4.4
         */
        String ID_PRINT_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "print"; //$NON-NLS-1$

        /**
         * Properties action ID.
         * 
         * @since 4.4
         */
        String ID_PROPERTIES_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "properties"; //$NON-NLS-1$

        /**
         * Redo action ID.
         * 
         * @since 4.4
         */
        String ID_REDO_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "redo"; //$NON-NLS-1$

        /**
         * Redo Refactor action ID.
         * 
         * @since 4.4
         */
        String ID_REFACTOR_REDO_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "refactorRedo"; //$NON-NLS-1$

        /**
         * Undo Refactor action ID.
         * 
         * @since 4.4
         */
        String ID_REFACTOR_UNDO_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "refactorUndo"; //$NON-NLS-1$

        /**
         * Refresh action ID.
         * 
         * @since 4.4
         */
        String ID_REFRESH_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "refresh"; //$NON-NLS-1$

        /**
         * Rename action ID.
         * 
         * @since 4.4
         */
        String ID_RENAME_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "rename"; //$NON-NLS-1$

        /**
         * Rename Resource action ID.
         * 
         * @since 4.4
         */
        String ID_RENAME_RESOURCE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "renameResource"; //$NON-NLS-1$

        /**
         * Reset Views action ID.
         * 
         * @since 4.4
         */
        String ID_RESET_VIEWS_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "resetViews"; //$NON-NLS-1$

        /**
         * Save action ID.
         * 
         * @since 4.4
         */
        String ID_SAVE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "save"; //$NON-NLS-1$

        /**
         * Save All Editors action ID.
         * 
         * @since 4.4
         */
        String ID_SAVEALL_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "saveAll"; //$NON-NLS-1$

        /**
         * Save As action ID.
         * 
         * @since 4.4
         */
        String ID_SAVEAS_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "saveAs"; //$NON-NLS-1$

        /**
         * Software updates action ID.
         * 
         * @since 5.0
         */
        String ID_SOFTWARE_UPDATES_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "softwareUpdates"; //$NON-NLS-1$

        /**
         * Software updates action ID.
         * 
         * @since 5.0
         */
        String ID_DIAGNOSTICS_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "diagnostics"; //$NON-NLS-1$

        /**
         * Set Datatype action ID.
         * 
         * @since 4.4
         */
        String ID_SET_DATATYPE_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "setDatatype"; //$NON-NLS-1$

        /**
         * Toggles auto build action ID. Works with build preference.
         * 
         * @since 4.4
         */
        String ID_TOGGLE_AUTO_BUILD_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "toggleAutoBuild"; //$NON-NLS-1$

        /**
         * Undo action ID.
         * 
         * @since 4.4
         */
        String ID_UNDO_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "undo"; //$NON-NLS-1$

        /**
         * Update Model Imports action ID.
         * 
         * @since 4.4
         */
        String ID_UPDATE_MODEL_IMPORTS_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "updateModelImports"; //$NON-NLS-1$

        /**
         * Validate All (clean build) action ID.
         * 
         * @since 4.4
         */
        String ID_VALIDATE_ALL_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "ValidateAll"; //$NON-NLS-1$

        /**
         * Validate Changes (incremental build) action ID.
         * 
         * @since 4.4
         */
        String ID_VALIDATE_CHANGES_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "validateChanges"; //$NON-NLS-1$

        /**
         * Welcome action ID.
         * 
         * @since 4.4
         */
        String ID_WELCOME_ACTION = IModelerRcpActionIds.Prefixes.ACTION + "welcome"; //$NON-NLS-1$
    }
}
