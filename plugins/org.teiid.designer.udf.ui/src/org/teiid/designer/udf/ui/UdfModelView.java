/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.udf.ui;

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
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerResourceNavigator;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * @since 5.5.3
 */
public class UdfModelView extends ModelExplorerResourceNavigator {

    /**
     * The FunctionDefinitions.xmi model.
     */
    private IFile udfModelFile;

    /**
     * Constructs a <code>UdfModelView</code> using the current Function Definitions model. If the functions model is not found
     * one is created.
     * 
     * @since 5.5.3
     */
    public UdfModelView() {
        this.udfModelFile = UdfWorkspaceManager.getUdfModel();
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
        setTitleToolTip(UdfUiPlugin.UTIL.getString(I18nUtil.getPropertyPrefix(UdfModelView.class) + "toolTip")); //$NON-NLS-1$
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
    public void updateTitle() {
        setContentDescription(CoreStringUtil.Constants.EMPTY_STRING);
    }

    /**
     * The <code>UdfModelActionGroup</code> provides the hook to customize the toolbar, menu, and context menu.
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
