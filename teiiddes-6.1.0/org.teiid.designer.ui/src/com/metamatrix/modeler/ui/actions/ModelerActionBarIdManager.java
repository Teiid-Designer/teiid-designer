/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import org.eclipse.ui.IWorkbenchActionConstants;

import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.product.IProductContexts.Product;


/**
 * Menus, menu items, toolbar items and groups, and menu marker IDs can change from product to product. This class keeps
 * track and returns the correct ID.
 * @since 4.4
 */
public class ModelerActionBarIdManager implements IModelerRcpActionIds {
    /**
     * Moved from IModelerActionConstants. These are the menu marker IDs for the
     * IDE application.
     * @since 4.4
     */
    private interface EditMenu {
        /** Name of insert child menu. */
        String INSERT_CHILD_MENU = "insertChildMenu"; //$NON-NLS-1$
        
        /** Name of insert sibling menu. */
        String INSERT_SIBLING_MENU = "insertSiblingMenu"; //$NON-NLS-1$
        
        /** Name of insert association menu. */
        String INSERT_ASSOCIATION_MENU = "insertAssociationMenu"; //$NON-NLS-1$
        
        /** Name of modeling sub-menu. */
        String MODELING_MENU = "modelingMenu"; //$NON-NLS-1$

        /** Name of refactor menu. */
        String REFACTOR_MENU = "refactorMenu"; //$NON-NLS-1$

        /** Name of group for start of new child, new sibling menu items. */
        String INSERT_START = "insertStart"; //$NON-NLS-1$

        /** Name of group for end of new child, new sibling menu items. */
        String INSERT_END = "insertEnd"; //$NON-NLS-1$

        /** Name of group for start of open, edit menu items. */
        String OPEN_START = "openStart"; //$NON-NLS-1$

        /** Name of group for end of open, edit menu items. */
        String OPEN_END = "openEnd"; //$NON-NLS-1$
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public static final boolean RCP_APP;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        RCP_APP = !UiPlugin.getDefault().isProductContextSupported(Product.IDE_APPLICATION);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Obtains the ID for the help menu's about group extras. This is an RCP-only ID.
     * @return the about group extras ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getAboutGroupExtrasMarkerId() {
        return (RCP_APP ? ID_ABOUT_GROUP_EXT : null);
    }
    
    /**
     * Obtains the ID for the start of the help menu's about group. This is an RCP-only ID.
     * @return the about group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getAboutGroupStartMarkerId() {
        return (RCP_APP ? ID_ABOUT_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the file menu's close group extras.
     * @return the close group extras ID (never <code>null</code>.
     * @since 4.4
     */
    public static String getCloseGroupExtrasMarkerId() {
        return (RCP_APP ? ID_CLOSE_GROUP_EXT : IWorkbenchActionConstants.CLOSE_EXT);
    }
    
    /**
     * Obtains the ID for the start of the file menu's close group. This is an RCP-only ID.
     * @return the close group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getCloseGroupStartMarkerId() {
        return (RCP_APP ? ID_CLOSE_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the edit menu's cut group extras.
     * @return the cut group extras ID (never <code>null</code>.
     * @since 4.4
     */
    public static String getCutGroupExtrasMarkerId() {
        return (RCP_APP ? ID_CUT_GROUP_EXT : IWorkbenchActionConstants.CUT_EXT);
    }
    
    /**
     * Obtains the ID for the start of the edit menu's cut group. This is an RCP-only ID.
     * @return the cut group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getCutGroupStartMarkerId() {
        return (RCP_APP ? ID_CUT_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the edit menu's delete group extras.
     * @return the delete group extras ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getDeleteGroupExtrasMarkerId() {
        return (RCP_APP ? ID_DELETE_GROUP_EXT : null);
    }
    
    /**
     * Obtains the ID for the start of the edit menu's delete group. This is an RCP-only ID.
     * @return the delete group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getDeleteGroupStartMarkerId() {
        return (RCP_APP ? ID_DELETE_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the edit menu's end marker based on if the product is a RCP or an IDE application.
     * @return the edit menu's end marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getEditMenuEndMarkerId() {
        return (RCP_APP ? ID_EDIT_END : IWorkbenchActionConstants.EDIT_END);
    }
    
    /**
     * Obtains the ID for the edit menu's start marker based on if the product is a RCP or an IDE application.
     * @return the edit menu's start marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getEditMenuStartMarkerId() {
        return (RCP_APP ? ID_EDIT_START : IWorkbenchActionConstants.EDIT_START);
    }
    
    /**
     * Obtains the ID for the edit menu based on if the product is a RCP or an IDE application.
     * @return the edit menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getEditMenuId() {
        return (RCP_APP ? ID_EDIT_MENU : IWorkbenchActionConstants.M_EDIT);
    }
    
    /**
     * Obtains the ID for the file menu's end marker based on if the product is a RCP or an IDE application.
     * @return the file menu's end marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getFileMenuEndMarkerId() {
        return (RCP_APP ? ID_FILE_END : IWorkbenchActionConstants.FILE_END);
    }
    
    /**
     * Obtains the ID for the file menu's start marker based on if the product is a RCP or an IDE application.
     * @return the file menu's start marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getFileMenuStartMarkerId() {
        return (RCP_APP ? ID_FILE_START : IWorkbenchActionConstants.FILE_START);
    }
    
    /**
     * Obtains the ID for the file menu based on if the product is a RCP or an IDE application.
     * @return the file menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getFileMenuId() {
        return (RCP_APP ? ID_FILE_MENU : IWorkbenchActionConstants.M_FILE);
    }
    
    /**
     * Obtains the ID for the help menu's end marker based on if the product is a RCP or an IDE application.
     * @return the help menu's end marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getHelpMenuEndMarkerId() {
        return (RCP_APP ? ID_HELP_END : IWorkbenchActionConstants.HELP_END);
    }
    
    /**
     * Obtains the ID for the help menu's start marker based on if the product is a RCP or an IDE application.
     * @return the help menu's start marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getHelpMenuStartMarkerId() {
        return (RCP_APP ? ID_HELP_START : IWorkbenchActionConstants.HELP_START);
    }
    
    /**
     * Obtains the ID for the help menu based on if the product is a RCP or an IDE application.
     * @return the help menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getHelpMenuId() {
        return (RCP_APP ? ID_HELP_MENU : IWorkbenchActionConstants.M_HELP);
    }
    
    /**
     * Obtains the ID for the file menu's import group extras.
     * @return the import group extras ID (never <code>null</code>.
     * @since 4.4
     */
    public static String getImportGroupExtrasMarkerId() {
        return (RCP_APP ? ID_IMPORT_GROUP_EXT : IWorkbenchActionConstants.IMPORT_EXT);
    }
    
    /**
     * Obtains the ID for the start of the file menu's import group. This is an RCP-only ID.
     * @return the import group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getImportGroupStartMarkerId() {
        return (RCP_APP ? ID_IMPORT_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the edit menu's insert sibling submenu based on if the product is a RCP or an IDE application.
     * @return the insert sibling menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getInsertAssociationMenuId() {
        return (RCP_APP ? ID_INSERT_ASSOCIATION_MENU : EditMenu.INSERT_ASSOCIATION_MENU);
    }
    
    /**
     * Obtains the ID for the edit menu's insert child submenu based on if the product is a RCP or an IDE application.
     * @return the insert child menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getInsertChildMenuId() {
        return (RCP_APP ? ID_INSERT_CHILD_MENU : EditMenu.INSERT_CHILD_MENU);
    }
    
    /**
     * Obtains the ID for the edit menu's modeling submenu based on if the product is a RCP or an IDE application.
     * @return the modeling menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getModelingMenuId() {
        return (RCP_APP ? ID_MODELING_MENU : EditMenu.MODELING_MENU);
    }
    
    /**
     * Obtains the ID for the edit menu's insert end marker based on if the product is a RCP or an IDE application.
     * @return the insert end marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getInsertEndMarkerId() {
        return (RCP_APP ? ID_INSERT_END : EditMenu.INSERT_END);
    }
    
    /**
     * Obtains the ID for the edit menu's insert start marker based on if the product is a RCP or an IDE application.
     * @return the insert start marker ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getInsertStartMarkerId() {
        return (RCP_APP ? ID_INSERT_START : EditMenu.INSERT_START);
    }
    
    /**
     * Obtains the ID for the edit menu's insert sibling submenu based on if the product is a RCP or an IDE application.
     * @return the insert sibling menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getInsertSiblingMenuId() {
        return (RCP_APP ? ID_INSERT_SIBLING_MENU : EditMenu.INSERT_SIBLING_MENU);
    }
    
    /**
     * Obtains the ID for the menu additions marker based on if the product is a RCP or an IDE application.
     * @return the menu additions ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getMenuAdditionsMarkerId() {
        return (RCP_APP ? ID_MENU_ADDITIONS : IWorkbenchActionConstants.MB_ADDITIONS);
    }
    
    /**
     * Obtains the ID for the file menu's move group extras. This is an RCP-only ID.
     * @return the move group extras ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getMoveGroupExtrasMarkerId() {
        return (RCP_APP ? ID_MOVE_GROUP_EXT : null);
    }
    
    /**
     * Obtains the ID for the start of the file menu's move group. This is an RCP-only ID.
     * @return the move group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getMoveGroupStartMarkerId() {
        return (RCP_APP ? ID_MOVE_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the file menu's and toolbar's new groups extras.
     * @return the new group extras ID (never <code>null</code>).
     * @since 4.4
     */
    public static String getNewGroupExtrasMarkerId() {
        return (RCP_APP ? ID_NEW_GROUP_EXT : IWorkbenchActionConstants.NEW_EXT);
    }
    
    /**
     * Obtains the ID for the start of the file menu's and toolbar's new groups. This is an RCP-only ID.
     * @return the new group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getNewGroupStartMarkerId() {
        return (RCP_APP ? ID_NEW_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the edit menu's open group extras.
     * @return the open group extras ID (never <code>null</code>.
     * @since 4.4
     */
    public static String getOpenGroupExtrasMarkerId() {
        return (RCP_APP ? ID_OPEN_GROUP_EXT : EditMenu.OPEN_END);
    }
    
    /**
     * Obtains the ID for the start of the edit menu's open group based on if the product is a RCP or an IDE application.
     * @return the open group start ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getOpenGroupStartMarkerId() {
        return (RCP_APP ? ID_OPEN_GROUP_START : EditMenu.OPEN_START);
    }
    
    /**
     * Obtains the ID for the file menu's and toolbar's print groups extras.
     * @return the print group extras ID (never <code>null</code>.
     * @since 4.4
     */
    public static String getPrintGroupExtrasMarkerId() {
        return (RCP_APP ? ID_PRINT_GROUP_EXT : IWorkbenchActionConstants.PRINT_EXT);
    }
    
    /**
     * Obtains the ID for the start of the file menu's and toolbar's print groups. This is an RCP-only ID.
     * @return the print group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getPrintGroupStartMarkerId() {
        return (RCP_APP ? ID_PRINT_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the file menu's refactor submenu based on if the product is a RCP or an IDE application.
     * @return the refactor menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getRefactorMenuId() {
        return (RCP_APP ? ID_REFACTOR_MENU : EditMenu.REFACTOR_MENU);
    }
    
    /**
     * Obtains the ID for the file menu's and toolbar's save groups extras.
     * @return the save group extras ID (never <code>null</code>).
     * @since 4.4
     */
    public static String getSaveGroupExtrasMarkerId() {
        return (RCP_APP ? ID_SAVE_GROUP_EXT : IWorkbenchActionConstants.SAVE_EXT);
    }
    
    /**
     * Obtains the ID for the start of the file menu's and toolbar's save groups. This is an RCP-only ID.
     * @return the save group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getSaveGroupStartMarkerId() {
        return (RCP_APP ? ID_SAVE_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the search menu based on if the product is a RCP or an IDE application.
     * @return the search menu ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getSearchMenuId() {
        return (RCP_APP ? ID_SEARCH_MENU : "org.eclipse.search.menu"); //$NON-NLS-1$
    }
    
    /**
     * Obtains the ID for the toolbar additions marker based on if the product is a RCP or an IDE application.
     * @return the toolbar additions ID (never <code>null</code>)
     * @since 4.4
     */
    public static String getToolbarAdditionsMarkerId() {
        return (RCP_APP ? ID_TOOL_BAR_ADDITIONS : IWorkbenchActionConstants.MB_ADDITIONS);
    }
    
    /**
     * Obtains the ID for the edit menu's undo group extras.
     * @return the undo group extras ID (never <code>null</code>.
     * @since 4.4
     */
    public static String getUndoGroupExtrasMarkerId() {
        return (RCP_APP ? ID_UNDO_GROUP_EXT : IWorkbenchActionConstants.UNDO_EXT);
    }
    
    /**
     * Obtains the ID for the start of the edit menu's undo group. This is an RCP-only ID.
     * @return the undo group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getUndoGroupStartMarkerId() {
        return (RCP_APP ? ID_UNDO_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the toolbar's validate group extras. This is an RCP-only ID.
     * @return the validate group extras ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getValidateGroupExtrasMarkerId() {
        return (RCP_APP ? ID_VALIDATE_GROUP_EXT : null);
    }
    
    /**
     * Obtains the ID for the start of the toolbar's validate group. This is an RCP-only ID.
     * @return the validate group start ID or <code>null</code> if current product is not an RCP application
     * @since 4.4
     */
    public static String getValidateGroupStartMarkerId() {
        return (RCP_APP ? ID_VALIDATE_GROUP_START : null);
    }
    
    /**
     * Obtains the ID for the validate menu. This menu is only available when the current product is an RCP application.
     * @return the validate menu ID or <code>null</code> if the current product is an IDE application
     * @since 4.4
     */
    public static String getValidateMenuId() {
        return (RCP_APP ? ID_VALIDATE_MENU : null);
    }
    
    /**
     * Obtains the ID for the views menu. This menu is only available when the current product is an RCP application.
     * @return the views menu ID or <code>null</code> if the current product is an IDE application
     * @since 4.4
     */
    public static String getViewsMenuId() {
        return (RCP_APP ? ID_VIEWS_MENU : null);
    }
    
    /**
     * Indicates if the current product is an Eclipse Rich Client Platform (RCP) application. 
     * @return <code>true</code> if an RCP application; <code>false</code> otherwise.
     * @since 4.4
     */
    public static boolean isRcpApplication() {
        return RCP_APP;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Construction not allowed. */
    private ModelerActionBarIdManager() {}

}
