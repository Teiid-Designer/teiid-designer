/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.product;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.NewWizardDropDownAction;
import org.eclipse.ui.actions.NewWizardMenu;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.internal.ide.AboutInfo;
import org.eclipse.ui.internal.ide.actions.BuildCleanAction;
import org.eclipse.ui.internal.ide.update.InstallWizardAction;
import com.metamatrix.modeler.internal.ui.actions.HiddenProjectCleanBuild;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IModelerRcpActionIds;
import com.metamatrix.modeler.ui.actions.ModelerUiActionsI18n;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.modeler.ui.product.IModelerProductContexts.ActionValues;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;

/**
 * @since 4.4
 */
public class ModelerRcpActionBarAdvisor extends ActionBarAdvisor
    implements IModelerProductContexts.Actions, IModelerProductContexts.Window, IModelerRcpActionIds {

    protected final IWorkbenchWindow window;

    protected final UiPlugin uiPlugin;

    private MenuManager newMenu;
    private NewWizardMenu newWizardMenu;

    private IAction closeAction;
    private IAction closeAllAction;

    private IAction saveAction;
    private IAction saveAsAction;
    private IAction saveAllAction;

    private IAction moveAction;
    private IAction renameResourceAction;
    private IAction refreshAction;

    private IAction printAction;

    private IAction propertiesAction;

    private IAction exitAction;

    private IAction undoAction;
    private IAction redoAction;

    private IAction cutAction;
    private IAction copyAction;
    private IAction pasteAction;
    // Clone contributed by the action service

    private IAction deleteAction;
    private IAction renameAction;

    private IAction findAction;

    // Open contributed by the action service
    // Edit contributed by the action service
    // Set Datatype contributed by the action service

    private IAction openPreferencesAction;

    private IAction validateAllAction;
    private IAction validateChangesAction;
    IAction toggleAutoBuildAction;

    private IAction validateOnSaveAction;
    private Preferences.IPropertyChangeListener prefListener;

    private IAction resetViewsAction;

    private IAction introAction;
    private IAction helpContentsAction;
    private IAction helpSearchAction;
    private IAction dynamicHelpAction;

    private IAction softwareUpdatesAction;

    private IAction diagnosticsAction;

    private IAction aboutAction;

    private StatusLineContributionItem statusLineItem;

    private IAction lockToolBarAction;
    private IAction newWizardDropDownAction;

    /**
     * Indicates if the action builder has been disposed
     */
    private boolean isDisposed = false;

    public ModelerRcpActionBarAdvisor( IActionBarConfigurer theConfigurer ) {
        super(theConfigurer);
        this.uiPlugin = UiPlugin.getDefault();
        this.window = theConfigurer.getWindowConfigurer().getWindow();
    }

    private MenuManager createEditMenu() {
        MenuManager editMenu = new MenuManager(ModelerUiActionsI18n.MainMenu_Edit, ID_EDIT_MENU);
        editMenu.add(new GroupMarker(ID_EDIT_START));

        //
        // Undo Group
        //

        editMenu.add(new GroupMarker(ID_UNDO_GROUP_START));

        if (this.undoAction != null) {
            editMenu.add(this.undoAction);
        }

        if (this.redoAction != null) {
            editMenu.add(this.redoAction);
        }

        // add marker for adding to undo grouping
        editMenu.add(new GroupMarker(ID_UNDO_GROUP_EXT));

        editMenu.add(new Separator());

        //
        // Cut Group
        //

        editMenu.add(new GroupMarker(ID_CUT_GROUP_START));

        if (this.cutAction != null) {
            editMenu.add(this.cutAction);
        }

        if (this.copyAction != null) {
            editMenu.add(this.copyAction);
        }

        if (this.pasteAction != null) {
            editMenu.add(this.pasteAction);
        }

        // add marker for adding to cut grouping
        editMenu.add(new GroupMarker(ID_CUT_GROUP_EXT));

        editMenu.add(new Separator());

        //
        // Delete Group
        //

        editMenu.add(new GroupMarker(ID_DELETE_GROUP_EXT));

        if (this.deleteAction != null) {
            editMenu.add(this.deleteAction);
        }

        if (this.renameAction != null) {
            editMenu.add(this.renameAction);
        }

        // add marker for adding to delete grouping
        editMenu.add(new GroupMarker(ID_DELETE_GROUP_EXT));

        editMenu.add(new Separator());

        //
        // Open Group
        //

        editMenu.add(new GroupMarker(ID_OPEN_GROUP_START));

        if (this.findAction != null) {
            editMenu.add(this.findAction);
        }

        // add marker for adding to open grouping
        editMenu.add(new GroupMarker(ID_OPEN_GROUP_EXT));

        //
        // Additions Group
        //

        editMenu.add(new GroupMarker(ID_MENU_ADDITIONS));

        // mark the end the menu
        editMenu.add(new GroupMarker(ID_EDIT_END));

        // add preferences so that it is always at the end of the menu
        if (this.openPreferencesAction != null) {
            editMenu.add(new Separator());
            editMenu.add(this.openPreferencesAction);
        }

        return editMenu;
    }

    private MenuManager createFileMenu() {
        MenuManager fileMenu = new MenuManager(ModelerUiActionsI18n.MainMenu_File, ID_FILE_MENU);
        fileMenu.add(new GroupMarker(ID_FILE_START));

        //
        // New Group
        //

        fileMenu.add(new GroupMarker(ID_NEW_GROUP_START));

        if (this.newMenu != null) {
            fileMenu.add(newMenu);
        }

        // add group for adding to the new grouping
        fileMenu.add(new GroupMarker(ID_NEW_GROUP_EXT));

        fileMenu.add(new Separator());

        //
        // Close Group
        //

        fileMenu.add(new GroupMarker(ID_CLOSE_GROUP_START));

        if (this.closeAction != null) {
            fileMenu.add(this.closeAction);
        }

        if (this.closeAllAction != null) {
            fileMenu.add(this.closeAllAction);
        }

        // add marker for adding to the close grouping
        fileMenu.add(new GroupMarker(ID_CLOSE_GROUP_EXT));

        // -----------------------------------------------------------------------------------------
        fileMenu.add(new Separator());

        //
        // Save Group
        //

        fileMenu.add(new GroupMarker(ID_SAVE_GROUP_START));

        if (this.saveAction != null) {
            fileMenu.add(this.saveAction);
        }

        if (this.saveAsAction != null) {
            fileMenu.add(this.saveAsAction);
        }

        if (this.saveAllAction != null) {
            fileMenu.add(this.saveAllAction);
        }

        // add marker for adding to the save grouping
        fileMenu.add(new GroupMarker(ID_SAVE_GROUP_EXT));

        // -----------------------------------------------------------------------------------------
        fileMenu.add(new Separator());

        //
        // Move Group
        //

        fileMenu.add(new GroupMarker(ID_MOVE_GROUP_START));

        if (this.moveAction != null) {
            fileMenu.add(this.moveAction);
        }

        if (this.renameResourceAction != null) {
            fileMenu.add(this.renameResourceAction);
        }

        if (this.refreshAction != null) {
            fileMenu.add(this.refreshAction);
        }

        // add marker for adding to the move grouping
        fileMenu.add(new GroupMarker(ID_MOVE_GROUP_EXT));

        // -----------------------------------------------------------------------------------------
        fileMenu.add(new Separator());

        //
        // Print Group
        //

        fileMenu.add(new GroupMarker(ID_PRINT_GROUP_START));

        if (this.printAction != null) {
            fileMenu.add(this.printAction);
        }

        // add marker for adding to the print grouping
        fileMenu.add(new GroupMarker(ID_PRINT_GROUP_EXT));

        // -----------------------------------------------------------------------------------------
        fileMenu.add(new Separator());

        //
        // Import Group
        //

        fileMenu.add(new GroupMarker(ID_IMPORT_GROUP_START));

        // import and export actions are being contributed by the modeler.ui plugin

        // add marker for adding to the import grouping
        fileMenu.add(new GroupMarker(ID_IMPORT_GROUP_EXT));

        // -----------------------------------------------------------------------------------------
        fileMenu.add(new Separator());

        fileMenu.add(new GroupMarker(ID_PROPERTIES_GROUP_START));

        if (this.propertiesAction != null) {
            fileMenu.add(this.propertiesAction);
        }

        fileMenu.add(new GroupMarker(ID_PROPERTIES_GROUP_EXT));

        //
        // Additions Group
        //

        fileMenu.add(new GroupMarker(ID_MENU_ADDITIONS));

        fileMenu.add(new GroupMarker(ID_FILE_END));

        // add exit after the end so that it always ends up at the bottom
        if (this.exitAction != null) {
            fileMenu.add(new Separator());
            fileMenu.add(this.exitAction);
        }

        return fileMenu;
    }

    private MenuManager createHelpMenu() {
        MenuManager helpMenu = new MenuManager(ModelerUiActionsI18n.MainMenu_Help, ID_HELP_MENU);
        helpMenu.add(new GroupMarker(ID_HELP_START));

        if (this.introAction != null) {
            helpMenu.add(this.introAction);
            helpMenu.add(new Separator());
        }

        if (this.helpContentsAction != null) {
            helpMenu.add(this.helpContentsAction);
        }

        if (this.helpSearchAction != null) {
            helpMenu.add(this.helpSearchAction);
        }

        if (this.dynamicHelpAction != null) {
            helpMenu.add(this.dynamicHelpAction);
        }

        if (this.softwareUpdatesAction != null) {
            helpMenu.add(new Separator());
            helpMenu.add(this.softwareUpdatesAction);
        }

        if (this.diagnosticsAction != null) {
            helpMenu.add(new Separator());
            helpMenu.add(this.diagnosticsAction);
        }

        //
        // About Group
        //

        helpMenu.add(new GroupMarker(ID_ABOUT_GROUP_START));

        helpMenu.add(new Separator());

        if (this.aboutAction != null) {
            helpMenu.add(this.aboutAction);
        }

        helpMenu.add(new GroupMarker(ID_ABOUT_GROUP_EXT));

        //
        // Additions Group
        //

        helpMenu.add(new Separator(ID_MENU_ADDITIONS));
        helpMenu.add(new GroupMarker(ID_HELP_END));

        return helpMenu;
    }

    private MenuManager createSearchMenu() {
        MenuManager searchMenu = new MenuManager(ModelerUiActionsI18n.MainMenu_Search, ID_SEARCH_MENU);
        searchMenu.add(new GroupMarker(ID_SEARCH_START));

        //
        // Additions Group
        //

        searchMenu.add(new Separator(ID_MENU_ADDITIONS));
        searchMenu.add(new GroupMarker(ID_SEARCH_END));

        return searchMenu;
    }

    private MenuManager createValidateMenu() {
        MenuManager validateMenu = new MenuManager(ModelerUiActionsI18n.MainMenu_Validate, ID_VALIDATE_MENU);
        validateMenu.add(new GroupMarker(ID_VALIDATE_START));

        if (this.validateAllAction != null) {
            validateMenu.add(this.validateAllAction);
        }

        if (this.validateChangesAction != null) {
            validateMenu.add(this.validateChangesAction);
        }

        if (this.validateOnSaveAction != null) {
            validateMenu.add(this.validateOnSaveAction);
        }

        if (this.toggleAutoBuildAction != null) {
            validateMenu.add(this.toggleAutoBuildAction);
        }

        //
        // Additions Group
        //

        validateMenu.add(new Separator(ID_MENU_ADDITIONS));

        validateMenu.add(new GroupMarker(ID_VALIDATE_END));

        return validateMenu;
    }

    private MenuManager createViewsMenu() {
        MenuManager viewsMenu = new MenuManager(ModelerUiActionsI18n.MainMenu_Views, ID_VIEWS_MENU);
        viewsMenu.add(new GroupMarker(ID_VIEWS_START));

        if (this.uiPlugin.isProductContextValueSupported(SUB_MENU, ID_OPEN_VIEW_MENU)) {
            MenuManager showViewMenuMgr = new MenuManager(ModelerUiActionsI18n.ShowViewMenu, ID_OPEN_VIEW_MENU);
            IContributionItem showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST.create(this.window);
            showViewMenuMgr.add(showViewMenu);
            viewsMenu.add(showViewMenuMgr);
        }

        if (this.resetViewsAction != null) {
            viewsMenu.add(this.resetViewsAction);
        }

        //
        // Additions Group
        //

        viewsMenu.add(new Separator(ID_MENU_ADDITIONS));
        viewsMenu.add(new GroupMarker(ID_VIEWS_END));

        return viewsMenu;
    }

    /**
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillCoolBar(org.eclipse.jface.action.ICoolBarManager)
     * @since 4.3
     */
    @Override
    protected void fillCoolBar( ICoolBarManager theCoolBar ) {
        //
        // context menu
        //

        if (this.uiPlugin.isProductContextValueSupported(COOL_BAR, ID_TOOL_BAR_CONTEXT_MENU)) {
            List actions = new ArrayList();

            if ((this.lockToolBarAction != null)
                && this.uiPlugin.isProductContextValueSupported(COOL_BAR, ActionValues.ID_LOCK_TOOL_BAR_ACTION)) {
                actions.add(this.lockToolBarAction);
            }

            if (!actions.isEmpty()) {
                IMenuManager popUpMenu = new MenuManager();

                for (int size = actions.size(), i = 0; i < size; ++i) {
                    popUpMenu.add(new ActionContributionItem((IAction)actions.get(i)));
                }

                theCoolBar.setContextMenuManager(popUpMenu);
            }
        }

        //
        // file toolbar
        //

        if (this.uiPlugin.isProductContextValueSupported(COOL_BAR, ID_FILE_TOOL_BAR)) {
            IToolBarManager fileToolBar = new ToolBarManager(theCoolBar.getStyle());
            theCoolBar.add(new ToolBarContributionItem(fileToolBar, ID_FILE_TOOL_BAR));
            fileToolBar.add(new GroupMarker(ID_FILE_START));

            //
            // New Group
            //

            fileToolBar.add(new GroupMarker(ID_NEW_GROUP_START));

            if ((this.newWizardDropDownAction != null)
                && this.uiPlugin.isProductContextValueSupported(COOL_BAR, ActionValues.ID_NEW_WIZARD_DROP_DOWN_ACTION)) {
                fileToolBar.add(this.newWizardDropDownAction);
            }

            fileToolBar.add(new GroupMarker(ID_NEW_GROUP_EXT));

            //
            // Save Group
            //

            fileToolBar.add(new GroupMarker(ID_SAVE_GROUP_START));

            if ((this.saveAction != null) && this.uiPlugin.isProductContextValueSupported(COOL_BAR, ActionValues.ID_SAVE_ACTION)) {
                fileToolBar.add(this.saveAction);
            }

            if ((this.saveAllAction != null)
                && this.uiPlugin.isProductContextValueSupported(COOL_BAR, ActionValues.ID_SAVEALL_ACTION)) {
                fileToolBar.add(this.saveAllAction);
            }

            fileToolBar.add(new GroupMarker(ID_SAVE_GROUP_EXT));

            //
            // Print Group
            //

            fileToolBar.add(new GroupMarker(ID_PRINT_GROUP_START));

            if ((this.printAction != null)
                && this.uiPlugin.isProductContextValueSupported(COOL_BAR, ActionValues.ID_PRINT_ACTION)) {
                fileToolBar.add(printAction);
            }

            fileToolBar.add(new GroupMarker(ID_PRINT_GROUP_EXT));

            //
            // Validate Group
            //

            fileToolBar.add(new GroupMarker(ID_VALIDATE_GROUP_START));
            fileToolBar.add(new GroupMarker(ID_VALIDATE_GROUP_EXT));

            //
            // Additions Group
            //

            fileToolBar.add(new GroupMarker(ID_TOOL_BAR_ADDITIONS));
            fileToolBar.add(new GroupMarker(ID_FILE_END));
        }

        //
        // search toolbar
        //

        if (this.uiPlugin.isProductContextValueSupported(COOL_BAR, ID_SEARCH_TOOL_BAR)) {
            IToolBarManager searchToolBar = new ToolBarManager(theCoolBar.getStyle());
            theCoolBar.add(new ToolBarContributionItem(searchToolBar, ID_SEARCH_TOOL_BAR));
            searchToolBar.add(new GroupMarker(ID_SEARCH_START));
            searchToolBar.add(new GroupMarker(ID_TOOL_BAR_ADDITIONS));
            searchToolBar.add(new GroupMarker(ID_SEARCH_END));
        }

        //
        // help toolbar
        //

        if (this.uiPlugin.isProductContextValueSupported(COOL_BAR, ID_HELP_TOOL_BAR)) {
            IToolBarManager helpToolBar = new ToolBarManager(theCoolBar.getStyle());
            theCoolBar.add(new ToolBarContributionItem(helpToolBar, ID_HELP_TOOL_BAR));
            helpToolBar.add(new GroupMarker(ID_HELP_START));
            helpToolBar.add(new GroupMarker(ID_TOOL_BAR_ADDITIONS));
            helpToolBar.add(new GroupMarker(ID_HELP_END));
        }

        theCoolBar.add(new GroupMarker(ID_TOOL_BAR_ADDITIONS));
    }

    /**
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
     * @since 4.4
     */
    @Override
    protected void fillMenuBar( IMenuManager theMenuBar ) {
        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_FILE_MENU)) {
            theMenuBar.add(createFileMenu());
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_EDIT_MENU)) {
            theMenuBar.add(createEditMenu());
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_SEARCH_MENU)) {
            theMenuBar.add(createSearchMenu());
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_VALIDATE_MENU)) {
            theMenuBar.add(createValidateMenu());
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_VIEWS_MENU)) {
            theMenuBar.add(createViewsMenu());
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_HELP_MENU)) {
            theMenuBar.add(createHelpMenu());
        }

        getFeatureInfos();
    }

    /**
     * @see org.eclipse.ui.application.ActionBarAdvisor#fillStatusLine(org.eclipse.jface.action.IStatusLineManager)
     * @since 4.4
     */
    @Override
    protected void fillStatusLine( IStatusLineManager theStatusLine ) {
        if (this.statusLineItem != null) {
            theStatusLine.add(this.statusLineItem);
        }
    }

    private AboutInfo[] getFeatureInfos() {
        // cannot be cached since bundle groups come and go
        List infos = new ArrayList();

        // add an entry for each bundle group
        IBundleGroupProvider[] providers = Platform.getBundleGroupProviders();

        if (providers != null) for (int i = 0; i < providers.length; ++i) {
            IBundleGroup[] bundleGroups = providers[i].getBundleGroups();

            for (int j = 0; j < bundleGroups.length; ++j) {
                infos.add(new AboutInfo(bundleGroups[j]));
            }
        }

        return (AboutInfo[])infos.toArray(new AboutInfo[infos.size()]);
    }

    Shell getShell() {
        return getActionBarConfigurer().getWindowConfigurer().getWindow().getShell();
    }

    /**
     * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.4
     */
    @Override
    protected void makeActions( IWorkbenchWindow theWindow ) {
        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_FILE_MENU)) {
            makeFileMenuActions(theWindow);
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_EDIT_MENU)) {
            makeEditMenuActions(theWindow);
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_SEARCH_MENU)) {
            makeSearchMenuActions(theWindow);
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_VALIDATE_MENU)) {
            makeValidateMenuActions(theWindow);
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_VIEWS_MENU)) {
            makeViewsMenuActions(theWindow);
        }

        if (this.uiPlugin.isProductContextValueSupported(MAIN_MENU, ID_HELP_MENU)) {
            makeHelpMenuActions(theWindow);
        }

        if (this.uiPlugin.isProductContextSupported(COOL_BAR)) {
            makeToolbarActions(theWindow);
        }

        if (this.uiPlugin.isProductContextSupported(STATUS_BAR)) {
            makeStatusBarActions(theWindow);
        }
    }

    protected void makeEditMenuActions( IWorkbenchWindow theWindow ) {
        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_UNDO_ACTION)) {
            this.undoAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_UNDO_ACTION,
                                                                                                                   theWindow);
            if (this.undoAction == null) {
                this.undoAction = ActionFactory.UNDO.create(theWindow);
            }
            register(this.undoAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_REDO_ACTION)) {
            this.redoAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_REDO_ACTION,
                                                                                                                   theWindow);
            if (this.redoAction == null) {
                this.redoAction = ActionFactory.REDO.create(theWindow);
            }
            register(this.redoAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_CUT_ACTION)) {
            this.cutAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_CUT_ACTION,
                                                                                                                  theWindow);
            if (this.cutAction == null) {
                this.cutAction = ActionFactory.CUT.create(theWindow);
            }
            register(this.cutAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_COPY_ACTION)) {
            this.copyAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_COPY_ACTION,
                                                                                                                   theWindow);
            if (this.copyAction == null) {
                this.copyAction = ActionFactory.COPY.create(theWindow);
            }
            register(this.copyAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_PASTE_ACTION)) {
            this.pasteAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_PASTE_ACTION,
                                                                                                                    theWindow);
            if (this.pasteAction == null) {
                this.pasteAction = ActionFactory.PASTE.create(theWindow);
            }
            register(this.pasteAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_DELETE_ACTION)) {
            this.deleteAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_DELETE_ACTION,
                                                                                                                     theWindow);
            if (this.deleteAction == null) {
                this.deleteAction = ActionFactory.DELETE.create(theWindow);
            }
            register(this.deleteAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_RENAME_ACTION)) {
            this.renameAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_RENAME_ACTION,
                                                                                                                     theWindow);
            if (this.renameAction == null) {
                this.renameAction = ActionFactory.RENAME.create(theWindow);
            }
            register(this.renameAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_FIND_ACTION)) {
            this.findAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_FIND_ACTION,
                                                                                                                   theWindow);
            if (this.findAction == null) {
                this.findAction = ActionFactory.FIND.create(theWindow);
            }
            register(this.findAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_OPEN_PREFERENCES_ACTION)) {
            this.openPreferencesAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_OPEN_PREFERENCES_ACTION,
                                                                                                                              theWindow);
            if (this.openPreferencesAction == null) {
                this.openPreferencesAction = ActionFactory.PREFERENCES.create(theWindow);
            }
            register(this.openPreferencesAction);
        }

    }

    protected void makeFileMenuActions( IWorkbenchWindow theWindow ) {
        if (this.uiPlugin.isProductContextValueSupported(SUB_MENU, ID_NEW_MENU)) {
            this.newMenu = new MenuManager(ModelerUiActionsI18n.NewMenu, ID_NEW_MENU);

            // -----------------------------------------------------------------------------------------
            this.newMenu.add(new Separator(ID_NEW_MENU));
            this.newWizardMenu = new NewWizardMenu(this.window, ID_NEW_MENU);

            this.newMenu.add(this.newWizardMenu);
            this.newMenu.add(new Separator(ID_MENU_ADDITIONS));
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_CLOSE_ACTION)) {
            this.closeAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_CLOSE_ACTION,
                                                                                                                    theWindow);
            if (this.closeAction == null) {
                this.closeAction = ActionFactory.CLOSE.create(theWindow);
                this.closeAction.setText(ModelerUiActionsI18n.CloseAction);
            }
            register(this.closeAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_CLOSEALL_ACTION)) {
            this.closeAllAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_CLOSEALL_ACTION,
                                                                                                                       theWindow);
            if (this.closeAllAction == null) {
                this.closeAllAction = ActionFactory.CLOSE_ALL.create(theWindow);
                this.closeAllAction.setText(ModelerUiActionsI18n.CloseAllAction);
            }
            register(this.closeAllAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_SAVE_ACTION)) {
            this.saveAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_SAVE_ACTION,
                                                                                                                   theWindow);
            if (this.saveAction == null) {
                this.saveAction = ActionFactory.SAVE.create(theWindow);
            }
            register(this.saveAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_SAVEAS_ACTION)) {
            this.saveAsAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_SAVEAS_ACTION,
                                                                                                                     theWindow);
            if (this.saveAsAction == null) {
                this.saveAsAction = ActionFactory.SAVE_AS.create(theWindow);
            }
            register(this.saveAsAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_SAVEALL_ACTION)) {
            this.saveAllAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_SAVEALL_ACTION,
                                                                                                                      theWindow);
            if (this.saveAllAction == null) {
                this.saveAllAction = ActionFactory.SAVE_ALL.create(theWindow);
            }
            register(this.saveAllAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_MOVE_ACTION)) {
            this.moveAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_MOVE_ACTION,
                                                                                                                   theWindow);
            if (this.moveAction == null) {
                this.moveAction = ActionFactory.MOVE.create(theWindow);
            }
            register(this.moveAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_RENAME_RESOURCE_ACTION)) {
            this.renameResourceAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_RENAME_RESOURCE_ACTION,
                                                                                                                             theWindow);
            if (this.renameResourceAction == null) {
                this.renameResourceAction = ActionFactory.RENAME.create(theWindow);
            }
            register(this.renameResourceAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_REFRESH_ACTION)) {
            this.refreshAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_REFRESH_ACTION,
                                                                                                                      theWindow);
            if (this.refreshAction == null) {
                this.refreshAction = ActionFactory.REFRESH.create(theWindow);
                this.refreshAction.setText(ModelerUiActionsI18n.RefreshAction);
            }
            register(this.refreshAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_PRINT_ACTION)) {
            this.printAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_PRINT_ACTION,
                                                                                                                    theWindow);
            if (this.printAction == null) {
                this.printAction = ActionFactory.PRINT.create(theWindow);
            }
            register(this.printAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_PROPERTIES_ACTION)) {
            this.propertiesAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_PROPERTIES_ACTION,
                                                                                                                         theWindow);
            if (this.propertiesAction == null) {
                this.propertiesAction = ActionFactory.PROPERTIES.create(theWindow);
                this.propertiesAction.setText(ModelerUiActionsI18n.PropertiesAction);
            }
            register(this.propertiesAction);
        }

        // -----------------------------------------------------------------------------------------

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_EXIT_ACTION)) {
            this.exitAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_EXIT_ACTION,
                                                                                                                   theWindow);
            if (this.exitAction == null) {
                IAction tempAction = ActionFactory.QUIT.create(theWindow);
                this.exitAction = new Action() {
                    @Override
                    public void run() {
                        // just close the shell. There are shell listeners in the RCP framework
                        // that close the Workbench.
                        getShell().close();
                    }
                };
                this.exitAction.setText(tempAction.getText());
                this.exitAction.setToolTipText(tempAction.getToolTipText());
                this.exitAction.setImageDescriptor(tempAction.getImageDescriptor());
                this.exitAction.setId(tempAction.getId());
            }
            register(this.exitAction);
        }
    }

    protected void makeHelpMenuActions( IWorkbenchWindow theWindow ) {
        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_WELCOME_ACTION)
            && theWindow.getWorkbench().getIntroManager().hasIntro()) {
            this.introAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_WELCOME_ACTION,
                                                                                                                    theWindow);
            if (this.introAction == null) {
                this.introAction = ActionFactory.INTRO.create(theWindow);
            }
            register(this.introAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_HELP_CONTENTS_ACTION)) {
            this.helpContentsAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_HELP_CONTENTS_ACTION,
                                                                                                                           theWindow);
            if (this.helpContentsAction == null) {
                this.helpContentsAction = ActionFactory.HELP_CONTENTS.create(theWindow);
            }
            register(this.helpContentsAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_HELP_SEARCH_ACTION)) {
            this.helpSearchAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_HELP_SEARCH_ACTION,
                                                                                                                         theWindow);
            if (this.helpSearchAction == null) {
                this.helpSearchAction = ActionFactory.HELP_SEARCH.create(theWindow);
            }
            register(this.helpSearchAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_DYNAMIC_HELP_ACTION)) {
            this.dynamicHelpAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_DYNAMIC_HELP_ACTION,
                                                                                                                          theWindow);
            if (this.dynamicHelpAction == null) {
                this.dynamicHelpAction = ActionFactory.DYNAMIC_HELP.create(theWindow);
            }
            register(this.dynamicHelpAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_SOFTWARE_UPDATES_ACTION)) {
            this.softwareUpdatesAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_SOFTWARE_UPDATES_ACTION,
                                                                                                                              theWindow);
            if (this.softwareUpdatesAction == null) {
                this.softwareUpdatesAction = new InstallWizardAction();
                this.softwareUpdatesAction.setText(ModelerUiActionsI18n.SoftwareUpdates);
                this.softwareUpdatesAction.setId(ActionValues.ID_SOFTWARE_UPDATES_ACTION);
            }
            register(this.softwareUpdatesAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_ABOUT_ACTION)) {
            this.aboutAction = ProductCustomizerMgr.getInstance().getProductCharacteristics().getRetargetableAction(ActionValues.ID_ABOUT_ACTION,
                                                                                                                    theWindow);
            if (this.aboutAction == null) {
                this.aboutAction = ActionFactory.ABOUT.create(theWindow);
            }
            register(this.aboutAction);
        }
    }

    protected void makeSearchMenuActions( IWorkbenchWindow theWindow ) {
        // no actions contributed
    }

    protected void makeStatusBarActions( IWorkbenchWindow theWindow ) {
        if (this.uiPlugin.isProductContextValueSupported(STATUS_BAR, ID_STATUS_BAR_LABEL)) {
            this.statusLineItem = new StatusLineContributionItem(ID_STATUS_BAR_LABEL);
        }
    }

    protected void makeToolbarActions( IWorkbenchWindow theWindow ) {
        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_LOCK_TOOL_BAR_ACTION)) {
            this.lockToolBarAction = ActionFactory.LOCK_TOOL_BAR.create(theWindow);
            register(this.lockToolBarAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_NEW_WIZARD_DROP_DOWN_ACTION)) {
            IWorkbenchAction innerAction = ActionFactory.NEW.create(theWindow);
            NewWizardMenu newWizardMenu = new NewWizardMenu(theWindow);
            this.newWizardDropDownAction = new NewWizardDropDownAction(theWindow, innerAction, newWizardMenu);
            this.newWizardDropDownAction.setId(ActionValues.ID_NEW_WIZARD_DROP_DOWN_ACTION); // ID is not set so set one here
            register(this.newWizardDropDownAction);
        }
    }

    protected void makeValidateMenuActions( final IWorkbenchWindow theWindow ) {
        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_VALIDATE_ALL_ACTION)) {
            if (ProductCustomizerMgr.getInstance().getProductCharacteristics().isHiddenProjectCentric()) {
                this.validateAllAction = new HiddenProjectCleanBuild(theWindow);
            } else {
                this.validateAllAction = new BuildCleanAction(theWindow);
            }
            this.validateAllAction.setId("buildClean"); //$NON-NLS-1$
            register(this.validateAllAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_VALIDATE_CHANGES_ACTION)) {
            this.validateChangesAction = IDEActionFactory.BUILD.create(theWindow);
            this.validateChangesAction.setText(ModelerUiActionsI18n.ValidateChanges);
            register(this.validateChangesAction);
        }

        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_TOGGLE_AUTO_BUILD_ACTION)) {
            this.toggleAutoBuildAction = IDEActionFactory.BUILD_AUTOMATICALLY.create(theWindow);
            this.toggleAutoBuildAction.setText(ModelerUiActionsI18n.ToggleAutoBuild);
            this.toggleAutoBuildAction.setChecked(ResourcesPlugin.getWorkspace().isAutoBuilding());
            register(this.toggleAutoBuildAction);
        }

        prefListener = new Preferences.IPropertyChangeListener() {
            public void propertyChange( Preferences.PropertyChangeEvent event ) {
                if (event.getProperty().equals(ResourcesPlugin.PREF_AUTO_BUILDING)) {
                    toggleAutoBuildAction.setChecked(ResourcesPlugin.getWorkspace().isAutoBuilding());
                }
            }
        };
        ResourcesPlugin.getPlugin().getPluginPreferences().addPropertyChangeListener(prefListener);
    }

    protected void makeViewsMenuActions( IWorkbenchWindow theWindow ) {
        if (this.uiPlugin.isProductContextValueSupported(ACTION, ActionValues.ID_RESET_VIEWS_ACTION)) {
            this.resetViewsAction = ActionFactory.RESET_PERSPECTIVE.create(theWindow);
            register(this.resetViewsAction);
        }
    }

    /**
     * Disposes any resources and unhooks any listeners that are no longer needed. Called when the window is closed.
     */
    @Override
    public void dispose() {
        if (isDisposed) return;

        isDisposed = true;

        if (prefListener != null) {
            ResourcesPlugin.getPlugin().getPluginPreferences().removePropertyChangeListener(prefListener);
            prefListener = null;
        }
        super.dispose();
    }

}
