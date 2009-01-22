/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace.udf;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.internal.actions.ClearWorkingSetAction;
import org.eclipse.ui.internal.actions.EditWorkingSetAction;
import org.eclipse.ui.internal.actions.SelectWorkingSetAction;
import org.eclipse.ui.views.framelist.BackAction;
import org.eclipse.ui.views.framelist.ForwardAction;
import org.eclipse.ui.views.framelist.UpAction;
import org.eclipse.ui.views.navigator.FilterSelectionAction;
import org.eclipse.ui.views.navigator.MainActionGroup;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.IExtensionModuleChangeListener;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.AddExistingUdfJarsAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.DeleteUdfJarsAction;
import com.metamatrix.modeler.internal.dqp.ui.workspace.actions.ImportUdfJarsAction;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.transformation.ui.udf.UdfWorkspaceManager;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.5.3
 */
public class UdfModelView extends ModelExplorerResourceNavigator implements DqpUiConstants {

    private IAction importAction;
    private IAction addAction;
    private DeleteUdfJarsAction deleteAction;

    private IExtensionModuleChangeListener moduleChangeListener;

    /**
     * The FunctionDefinitions.xmi model.
     */
    private IFile udfModelFile;

    public static final int ID_IMPORT_JARS_ACTION = 0;
    public static final int ID_ADD_JARS_ACTION = 1;
    public static final int ID_DELETE_JARS_ACTION = 2;

    /**
     * Constructs a <code>UdfModelView</code> using the current Function Definitions model. If the functions model is not found
     * one is created.
     */
    public UdfModelView() {
        this.udfModelFile = UdfWorkspaceManager.getUdfModel(true);
    }

    IFile accessUdfModel() {
        return this.udfModelFile;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    public void createPartControl( Composite parent ) {
        super.createPartControl(parent);
        setTitleToolTip(UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelView.class) + "toolTip")); //$NON-NLS-1$
        getTreeViewer().expandToLevel(2);
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#createViewer(org.eclipse.swt.widgets.Composite)
     * @since 5.5.3
     */
    @Override
    protected TreeViewer createViewer( Composite parent ) {
        TreeViewer viewer = new TreeViewer(parent);
        viewer.setUseHashlookup(true);

        initContentProvider(viewer);
        initLabelProvider(viewer);
        initFilters(viewer);
        initListeners(viewer);

        // register to receive extension module jar change events
        this.moduleChangeListener = new IExtensionModuleChangeListener() {

            public void extensionModulesChanged( com.metamatrix.modeler.dqp.config.ExtensionModuleChangeEvent event ) {
                handleExtensionsChanged();
            }
        };
        DqpPlugin.getInstance().getExtensionsHandler().addChangeListener(this.moduleChangeListener);

        return viewer;
    }

    void handleExtensionsChanged() {
        if ((getTreeViewer() != null) && !getTreeViewer().getTree().isDisposed()) {
            UiUtil.runInSwtThread(new Runnable() {
                public void run() {
                    getTreeViewer().refresh(true);
                    getTreeViewer().expandToLevel(2);
                }
            }, true);
        }
    }

    @Override
    protected void handleKeyPressed( KeyEvent theEvent ) {
        if (theEvent.character == SWT.DEL) {
            if (deleteAction != null && deleteAction.isApplicable(getTreeViewer().getSelection())) {
                deleteAction.setSelection(getTreeViewer().getSelection());
                deleteAction.run();
            }
        } else {
            super.handleKeyPressed(theEvent);
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     * @since 5.5.3
     */
    @Override
    protected void fillContextMenu( IMenuManager menu ) {
        super.fillContextMenu(menu);

        // remove new submenu
        if (menu.find(ActionFactory.NEW.getId()) != null) {
            menu.remove(ActionFactory.NEW.getId());
        }

        // remove refactor submenu
        if (menu.find(ModelerActionBarIdManager.getRefactorMenuId()) != null) {
            menu.remove(ModelerActionBarIdManager.getRefactorMenuId());
        }

        // remove import
        if (menu.find(IMPORT_ACTION_ID) != null) {
            menu.remove(IMPORT_ACTION_ID);
        }

        // remove export
        if (menu.find(EXPORT_ACTION_ID) != null) {
            menu.remove(EXPORT_ACTION_ID);
        }
    }

    private IAction getImportUdfJarsAction() {
        if (this.importAction == null) {
            this.importAction = new ImportUdfJarsAction();
        }

        return this.importAction;
    }

    private IAction getAddUdfJarsAction() {
        if (this.addAction == null) {
            this.addAction = new AddExistingUdfJarsAction();
        }

        return this.addAction;
    }

    private IAction getDeleteUdfJarsAction() {
        if (this.deleteAction == null) {
            this.deleteAction = new DeleteUdfJarsAction();
        }

        return this.deleteAction;
    }

    public IAction getAction( int actionID ) {
        if (actionID == ID_ADD_JARS_ACTION) {
            return getAddUdfJarsAction();
        } else if (actionID == ID_DELETE_JARS_ACTION) {
            return getDeleteUdfJarsAction();
        } else if (actionID == ID_IMPORT_JARS_ACTION) {
            return getImportUdfJarsAction();
        }

        return null;
    }

    /**
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#getInitialInput()
     * @since 5.5.3
     */
    @Override
    protected IAdaptable getInitialInput() {
        return this.udfModelFile.getProject();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.views.navigator.ResourceNavigator#initFilters(org.eclipse.jface.viewers.TreeViewer)
     */
    @Override
    protected void initFilters( TreeViewer viewer ) {
        viewer.addFilter(new ViewerFilter() {

            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                // don't show any folders
                if (element instanceof IContainer) {
                    return false;
                } else if (element instanceof UdfJarFolder || element instanceof UdfJarWrapper) {
                    return true;
                }

                // if file only show the UDF model
                if (element instanceof IFile) {
                    return accessUdfModel().equals(element);
                }

                // show all model contents
                return true;
            }
        });
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator#makeActions()
     * @since 5.5.3
     */
    @Override
    protected void makeActions() {
        setActionGroup(new UdfModelActionGroup(this));
    }

    /**
     * Customization to the <code>IActionBars</code> after the superclass has populated them.
     * 
     * @param actionBars the action bars being configured
     * @since 5.5.3
     */
    void postFillActionBars( IActionBars actionBars ) {
        //
        // Customize toolbar by removing back, forward, and up actions
        //

        IToolBarManager toolBarMgr = actionBars.getToolBarManager();
        List<IContributionItem> removedItems = new ArrayList<IContributionItem>();

        for (IContributionItem item : toolBarMgr.getItems()) {
            if (item instanceof ActionContributionItem) {
                IAction action = ((ActionContributionItem)item).getAction();

                if ((action instanceof BackAction) || (action instanceof ForwardAction) || (action instanceof UpAction)) {
                    removedItems.add(item);
                    continue;
                }
            }
        }

        for (IContributionItem item : removedItems) {
            toolBarMgr.remove(item);
        }

        toolBarMgr.update(true);

        //
        // Customize menu by removing working set-related actions and filters action
        //

        IMenuManager menuMgr = actionBars.getMenuManager();
        removedItems = new ArrayList<IContributionItem>();

        for (IContributionItem item : menuMgr.getItems()) {
            if (item instanceof ActionContributionItem) {
                IAction action = ((ActionContributionItem)item).getAction();

                if ((action instanceof SelectWorkingSetAction) || (action instanceof ClearWorkingSetAction)
                    || (action instanceof EditWorkingSetAction) || (action instanceof FilterSelectionAction)) {
                    removedItems.add(item);
                    continue;
                }
            }
        }

        for (IContributionItem item : removedItems) {
            menuMgr.remove(item);
        }

        menuMgr.update(true);
    }

    /**
     * Customization to the <code>IActionBars</code> before the superclass has populated them.
     * 
     * @param actionBars the action bars being configured
     * @since 5.5.3
     */
    void preFillActionBars( IActionBars actionBars ) {
        //
        // Customize toolbar by adding sort model and refresh actions
        //

        IToolBarManager toolBarMgr = actionBars.getToolBarManager();
        toolBarMgr.add(getSortModelContentsAction());
        toolBarMgr.add(getRefreshAction());
        toolBarMgr.add(getImportUdfJarsAction());
        toolBarMgr.add(getAddUdfJarsAction());

        //
        // Customize menu by adding show imports action
        //

        IMenuManager menuMgr = actionBars.getMenuManager();
        ShowImportsAction action = new ShowImportsAction();
        action.setChecked(false);
        action.run(); // need to run to get the content provider to turn off imports
        menuMgr.add(action);
    }

    @Override
    protected void handleSelectionChanged( SelectionChangedEvent theEvent ) {
        super.handleSelectionChanged(theEvent);
        if (deleteAction == null) {
            getDeleteUdfJarsAction();
        }

        deleteAction.selectionChanged(theEvent);
    }

    @Override
    public void updateTitle() {
        setContentDescription(StringUtil.Constants.EMPTY_STRING);
    }

    /**
     * The <code>UdfModelActionGroup</code> provids the hook to customize the toolbar, menu, and context menu.
     * 
     * @since 5.5.3
     */
    private class UdfModelActionGroup extends MainActionGroup {

        public UdfModelActionGroup( UdfModelView navigator ) {
            super(navigator);
        }

        /**
         * @see org.eclipse.ui.views.navigator.MainActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
         * @since 5.5.3
         */
        @Override
        public void fillActionBars( IActionBars actionBars ) {
            preFillActionBars(actionBars);
            super.fillActionBars(actionBars);
            postFillActionBars(actionBars);
        }
    }

}
