/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;



/** 
 * Action related identifiers.
 * Here are the valid toolbar paths:
 * <ul>
 *      <li>toolbar.file/</li>
 *      <li>toolbar.file/file.start</li>
 *      <li>toolbar.file/new.start</li>
 *      <li>toolbar.file/new.ext</li>
 *      <li>toolbar.file/save.start</li>
 *      <li>toolbar.file/save.ext</li>
 *      <li>toolbar.file/print.start</li>
 *      <li>toolbar.file/print.ext</li>
 *      <li>toolbar.file/validate.start</li>
 *      <li>toolbar.file/validate.ext</li>
 *      <li>toolbar.file/toolbaradditions</li>
 *      <li>toolbar.file/file.end</li>
 * </ul>
 * Here are the valid main menu paths:
 * <ul>
 *      <li>mainmenu.file/</li>
 *      <li>mainmenu.file/file.start</li>
 *      <li>mainmenu.file/new.start</li>
 *      <li>mainmenu.file/new.ext</li>
 *      <li>mainmenu.file/close.start</li>
 *      <li>mainmenu.file/close.ext</li>
 *      <li>mainmenu.file/save.start</li>
 *      <li>mainmenu.file/save.ext</li>
 *      <li>mainmenu.file/move.start</li>
 *      <li>mainmenu.file/move.ext</li>
 *      <li>mainmenu.file/print.start</li>
 *      <li>mainmenu.file/print.ext</li>
 *      <li>mainmenu.file/import.start</li>
 *      <li>mainmenu.file/import.ext</li>
 *      <li>mainmenu.file/menuadditions</li>
 *      <li>mainmenu.file/file.end</li>
 *      <li>mainmenu.edit/</li>
 *      <li>mainmenu.edit/edit.start</li>
 *      <li>mainmenu.edit/undo.start</li>
 *      <li>mainmenu.edit/undo.ext</li>
 *      <li>mainmenu.edit/cut.start</li>
 *      <li>mainmenu.edit/cut.ext</li>
 *      <li>mainmenu.edit/delete.start</li>
 *      <li>mainmenu.edit/delete.ext</li>
 *      <li>mainmenu.edit/open.start</li>
 *      <li>mainmenu.edit/open.ext</li>
 *      <li>mainmenu.edit/menuadditions</li>
 *      <li>mainmenu.edit/edit.end</li>
 *      <li>mainmenu.search/</li>
 *      <li>mainmenu.search/search.start</li>
 *      <li>mainmenu.search/menuadditions</li>
 *      <li>mainmenu.search/search.end</li>
 *      <li>mainmenu.validate/</li>
 *      <li>mainmenu.validate/validate.start</li>
 *      <li>mainmenu.validate/menuadditions</li>
 *      <li>mainmenu.validate/validate.end</li>
 *      <li>mainmenu.views/</li>
 *      <li>mainmenu.views/views.start</li>
 *      <li>mainmenu.views/menuadditions</li>
 *      <li>mainmenu.views/views.end</li>
 *      <li>mainmenu.help/</li>
 *      <li>mainmenu.help/help.start</li>
 *      <li>mainmenu.help/about.start</li>
 *      <li>mainmenu.help/about.ext</li>
 *      <li>mainmenu.help/menuadditions</li>
 *      <li>mainmenu.help/help.end</li>
 * </ul>
 * @since 4.4
 */
public interface IModelerRcpActionIds {
    
    /**
     * Prefixes that can be used for constructing IDs.
     * @since 4.4
     */
    class Prefixes {
        /**
         * Prefix for action-related IDs.
         * @since 4.4
         */
        public static String ACTION = "action."; //$NON-NLS-1$
        
        /**
         * Prefix for menu-related IDs.
         * @since 4.4
         */
        public static String MENU = "mainmenu."; //$NON-NLS-1$
        
        /**
         * Prefix for status bar-related IDs.
         * @since 4.4
         */
        public static String STATUS_BAR = "statusBar."; //$NON-NLS-1$
        
        /**
         * Prefix for submenu-related IDs.
         * @since 4.4
         */
        public static String SUBMENU = "submenu."; //$NON-NLS-1$
        
        /**
         * Prefix for action-related IDs.
         * @since 4.4
         */
        public static String TOOL_BAR = "toolbar."; //$NON-NLS-1$        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Main Menus
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * File menu ID.
     * @since 4.4
     */
    String ID_FILE_MENU = Prefixes.MENU + "file"; //$NON-NLS-1$
    
    /**
     * Edit menu ID.
     * @since 4.4
     */
    String ID_EDIT_MENU = Prefixes.MENU + "edit"; //$NON-NLS-1$
    
    /**
     * Search menu ID.
     * @since 4.4
     */
    String ID_SEARCH_MENU = Prefixes.MENU + "search"; //$NON-NLS-1$
    
    /**
     * Validate menu ID.
     * @since 4.4
     */
    String ID_VALIDATE_MENU = Prefixes.MENU + "validate"; //$NON-NLS-1$
    
    /**
     * Views menu ID.
     * @since 4.4
     */
    String ID_VIEWS_MENU = Prefixes.MENU + "views"; //$NON-NLS-1$
    
    /**
     * Help menu ID.
     * @since 4.4
     */
    String ID_HELP_MENU = Prefixes.MENU + "help"; //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Submenus
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * The Edit -> Insert Association menu ID.
     * @since 4.4
     */
    String ID_INSERT_ASSOCIATION_MENU = Prefixes.SUBMENU + "insertAssociation"; //$NON-NLS-1$

    /**
     * The Edit -> Insert Child menu ID.
     * @since 4.4
     */
    String ID_INSERT_CHILD_MENU = Prefixes.SUBMENU + "insertChild"; //$NON-NLS-1$

    /**
     * The Edit -> Insert Sibling menu ID.
     * @since 4.4
     */
    String ID_INSERT_SIBLING_MENU = Prefixes.SUBMENU + "insertSibling"; //$NON-NLS-1$
    
    /**
     * The Edit -> Modeling menu ID.
     * @since 4.4
     */
    String ID_MODELING_MENU = Prefixes.SUBMENU + "modeling"; //$NON-NLS-1$

    /**
     * The File -> New menu ID.
     * @since 4.4
     */
    String ID_NEW_MENU = Prefixes.SUBMENU + "new"; //$NON-NLS-1$
    
    /**
     * The Views -> Open View menu ID.
     * @since 4.4
     */
    String ID_OPEN_VIEW_MENU = Prefixes.SUBMENU + "openView"; //$NON-NLS-1$
    
    /**
     * The File -> Refactor menu ID.
     * @since 4.4
     */
    String ID_REFACTOR_MENU = Prefixes.SUBMENU + "refactor"; //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Menu and Toolbar
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Marker ID for adding about-related actions in the help menu.
     * @since 4.4
     */
    String ID_ABOUT_GROUP_EXT = "about.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of about-related actions in the help menu.
     * @since 4.4
     */
    String ID_ABOUT_GROUP_START = "about.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding close-related actions in the file menu.
     * @since 4.4
     */
    String ID_CLOSE_GROUP_EXT = "close.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of close-related actions in the file menu.
     * @since 4.4
     */
    String ID_CLOSE_GROUP_START = "close.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding cut-related actions in the edit menu.
     * @since 4.4
     */
    String ID_CUT_GROUP_EXT = "cut.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of the the cut-related actions in the edit menu.
     * @since 4.4
     */
    String ID_CUT_GROUP_START = "cut.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding delete-related actions in the edit menu.
     * @since 4.4
     */
    String ID_DELETE_GROUP_EXT = "delete.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of the delete-related actions in the edit menu.
     * @since 4.4
     */
    String ID_DELETE_GROUP_START = "delete.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for end of the edit menu. <strong>Nothing should be inserted after this marker.</strong>
     * @since 4.4
     */
    String ID_EDIT_END = "edit.end"; //$NON-NLS-1$
    
    /**
     * Marker ID for start of the edit menu. <strong>Nothing should be inserted before this marker.</strong>
     * @since 4.4
     */
    String ID_EDIT_START = "edit.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for end of the file menu or file toolbar. <strong>Nothing should be inserted after this marker.</strong>
     * @since 4.4
     */
    String ID_FILE_END = "file.end"; //$NON-NLS-1$
    
    /**
     * Marker ID for start of the file menu or file toolbar. <strong>Nothing should be inserted before this marker.</strong>
     * @since 4.4
     */
    String ID_FILE_START = "file.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for end of the help menu or help toolbar. <strong>Nothing should be inserted after this marker.</strong>
     * @since 4.4
     */
    String ID_HELP_END = "help.end"; //$NON-NLS-1$
    
    /**
     * Marker ID for start of the help menu or help toolbar. <strong>Nothing should be inserted before this marker.</strong>
     * @since 4.4
     */
    String ID_HELP_START = "help.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding import-related actions in the file menu.
     * @since 4.4
     */
    String ID_IMPORT_GROUP_EXT = "import.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of import-related actions in the file menu.
     * @since 4.4
     */
    String ID_IMPORT_GROUP_START = "import.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for end of the insert menus found in the edit menu. <strong>Nothing should be inserted after this marker.</strong>
     * @since 4.4
     */
    String ID_INSERT_END = "insert.end"; //$NON-NLS-1$

    /**
     * Marker ID for start of the insert menus found in the edit menu. <strong>Nothing should be inserted before this marker.</strong>
     * @since 4.4
     */
    String ID_INSERT_START = "insert.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for menu additions.
     * @since 4.4
     */
    String ID_MENU_ADDITIONS = "menuadditions"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding move-related actions in the file menu.
     * @since 4.4
     */
    String ID_MOVE_GROUP_EXT = "move.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of move-related actions in the file menu.
     * @since 4.4
     */
    String ID_MOVE_GROUP_START = "move.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding new-related actions to the main menu and toolbar.
     * @since 4.4
     */
    String ID_NEW_GROUP_EXT = "new.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of new-related actions in the main menu and toolbar.
     * @since 4.4
     */
    String ID_NEW_GROUP_START = "new.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding open-related actions.
     * @since 4.4
     */
    String ID_OPEN_GROUP_EXT = "open.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of open-related actions in the edit menu.
     * @since 4.4
     */
    String ID_OPEN_GROUP_START = "open.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding print-related actions to the main menu and toolbar.
     * @since 4.4
     */
    String ID_PRINT_GROUP_EXT = "print.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of print-related actions in the file menu and toolbar.
     * @since 4.4
     */
    String ID_PRINT_GROUP_START = "print.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding properties-related actions in the file menu.
     * @since 4.4
     */
    String ID_PROPERTIES_GROUP_EXT = "properties.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of properties-related actions in the file menu.
     * @since 4.4
     */
    String ID_PROPERTIES_GROUP_START = "properties.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding save-related actions in the main menu and toolbar.
     * @since 4.4
     */
    String ID_SAVE_GROUP_EXT = "save.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of save-related actions in the main menu and toolbar.
     * @since 4.4
     */
    String ID_SAVE_GROUP_START = "save.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for end of the search menu or search toolbar. <strong>Nothing should be inserted after this marker.</strong>
     * @since 4.4
     */
    String ID_SEARCH_END = "search.end"; //$NON-NLS-1$
    
    /**
     * Marker ID for start of the search menu or search toolbar. <strong>Nothing should be inserted before this marker.</strong>
     * @since 4.4
     */
    String ID_SEARCH_START = "search.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for toolbar action  additions.
     * @since 4.4
     */
    String ID_TOOL_BAR_ADDITIONS = "toolbaradditions"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding undo-related actions in the edit menu.
     * @since 4.4
     */
    String ID_UNDO_GROUP_EXT = "undo.ext"; //$NON-NLS-1$

    /**
     * Marker ID for the start of the undo-related actions in the edit menu.
     * @since 4.4
     */
    String ID_UNDO_GROUP_START = "undo.start"; //$NON-NLS-1$

    /**
     * Marker ID for end of the validate menu. <strong>Nothing should be inserted after this marker.</strong>
     * @since 4.4
     */
    String ID_VALIDATE_END = "validate.end"; //$NON-NLS-1$
    
    /**
     * Marker ID for adding validation-related actions to the toolbar.
     * @since 4.4
     */
    String ID_VALIDATE_GROUP_EXT = "validateGroup.ext"; //$NON-NLS-1$
    
    /**
     * Marker ID for the start of the validation-related actions in the toolbar.
     * @since 4.4
     */
    String ID_VALIDATE_GROUP_START = "validateGroup.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for start of the validate menu. <strong>Nothing should be inserted before this marker.</strong>
     * @since 4.4
     */
    String ID_VALIDATE_START = "validate.start"; //$NON-NLS-1$
    
    /**
     * Marker ID for end of the views menu. <strong>Nothing should be inserted after this marker.</strong>
     * @since 4.4
     */
    String ID_VIEWS_END = "views.end"; //$NON-NLS-1$
    
    /**
     * Marker ID for start of the views menu. <strong>Nothing should be inserted before this marker.</strong>
     * @since 4.4
     */
    String ID_VIEWS_START = "views.start"; //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // STATUS BAR
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * ID for status bar label.
     * @since 4.4
     */
    String ID_STATUS_BAR_LABEL = Prefixes.STATUS_BAR + "label"; //$NON-NLS-1$
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // TOOL BAR
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * ID for the toolbar context menu.
     * @since 4.4
     */
    String ID_TOOL_BAR_CONTEXT_MENU = Prefixes.TOOL_BAR + "contextMenu"; //$NON-NLS-1$
    
    /**
     * ID for the file toolbar.
     * @since 4.4
     */
    String ID_FILE_TOOL_BAR = Prefixes.TOOL_BAR + "file"; //$NON-NLS-1$
    
    /**
     * ID for the help toolbar.
     * @since 4.4
     */
    String ID_HELP_TOOL_BAR = Prefixes.TOOL_BAR + "help"; //$NON-NLS-1$
    
    /**
     * ID for the help toolbar.
     * @since 4.4
     */
    String ID_SEARCH_TOOL_BAR = Prefixes.TOOL_BAR + "search"; //$NON-NLS-1$
    
}
