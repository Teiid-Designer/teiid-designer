/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.outline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.actions.SortModelContentsAction;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.actions.ModelerSpecialActionManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorPageOutline;

/**
 * ModelOutlinePage is the ContentOutlinePage for the ModelEditor. It contains a PageBook which can display a TreeViewer of the
 * Model, plus any other controls that are contributed by ModelEditorPage extensions.
 */
public class ModelOutlinePage extends ContentOutlinePage {

    private static final ImageDescriptor blankIcon = UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.BLANK_ICON);

    ModelEditor modelEditor;
    private PageBook pageBook;
    private ModelEditorPageOutline currentViewer;

    /** the ModelOutlineTree, which is always available in the ContentOutlinePage */
    ModelOutlineTreeViewer viewer;

    /** map of key=ModelEditorPage classname, value = ModelEditorPageOutline instance */
    private HashMap contributionMap = new HashMap();

    /** list of ModelEditorPageOutlines that have been initialized, to facility lazy loading */
    private ArrayList initList = new ArrayList();

    /** the action for showing the ModelOutlineTreeViewer */
    private IAction modelOutlineAction;

    /** the action for showing the active ModelEditorPage's outline contribution */
    private IAction contributionAction;

    /** action allowing user to directly refresh tree */
    IAction refreshAction;

    /** action allowing user to directly refresh tree */
    private IAction collapseAllAction;

    /** set to true once this object has been fully initialized and placed in the Workbench. */
    private boolean hasInitialized = false;

    /** needed for key listening */
    private KeyAdapter kaKeyAdapter;

    public ModelOutlinePage( ModelEditor editor ) {
        super();
        this.modelEditor = editor;
    }

    @Override
    public void init( IPageSite pageSite ) {
        super.init(pageSite);
    }

    /**
     * Called by the ModelEditor when instantiating ModelEditorPage extensions, this method adds a page to the page book for the
     * ModelEditorPageOutline, and an action on the toolbar to show/hide it.
     */
    public void addOutlineContribution( ModelEditorPage page ) {
        ModelEditorPageOutline contribution = page.getOutlineContribution();
        if (contribution != null) {
            final String id = page.getClass().getName();
            // store the contribution in a HashMap so we can enable/disable based on the active ModelEditorPage
            contributionMap.put(id, contribution);
        }
    }

    @Override
    public void createControl( Composite parent ) {
        // register global actions
        ModelerActionService service = getModelerActionService();

        service.registerDefaultGlobalActions(getSite().getActionBars());

        pageBook = new PageBook(parent, SWT.NONE);

        // the ModelOutlineTreeViewer is always available, no matter what ModelEditorPage is showing
        viewer = new ModelOutlineTreeViewer(this.modelEditor);
        final String id = viewer.getClass().getName();
        viewer.init(getSite());
        viewer.createControl(pageBook);

        // Preview Data Action from DQP Ui. If Exists, place in toolbar
        IAction previewAction = ModelerSpecialActionManager.getAction(UiConstants.Extensions.PREVIEW_DATA_ACTION_ID);
        if (previewAction != null) {
            getSite().getActionBars().getToolBarManager().add(previewAction);
            getSite().getActionBars().getToolBarManager().add(new Separator());
        }

        IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
        modelOutlineAction = new Action() {
            @Override
            public void run() {
                showPage(id);
                refreshAction.setEnabled(true);
            }
        };

        modelOutlineAction.setToolTipText(viewer.getToolTipText());
        modelOutlineAction.setImageDescriptor(viewer.getIcon());

        contributionAction = new Action() {
            @Override
            public void run() {
                showPage(modelEditor.getCurrentPage().getClass().getName());
                refreshAction.setEnabled(false);
            }
        };

        contributionAction.setImageDescriptor(blankIcon);
        contributionAction.setEnabled(false);
        contributionAction.setChecked(false);

        tbm.add(contributionAction);
        tbm.add(modelOutlineAction);
        tbm.add(new Separator());

        tbm.add(new SortModelContentsAction(viewer.getTree()));

        // add refresh tree action
        refreshAction = new Action() {
            @Override
            public void run() {
                viewer.getTree().refresh();
            }
        };
        refreshAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.REFRESH_ICON));
        refreshAction.setToolTipText(UiConstants.Util.getString("ModelOutlinePage.refreshAction.tooltip")); //$NON-NLS-1$
        refreshAction.setText(UiConstants.Util.getString("ModelOutlinePage.refreshAction.text")); //$NON-NLS-1$
        tbm.add(refreshAction);
        refreshAction.setEnabled(true);

        // add refresh tree action
        refreshAction = new Action() {
            @Override
            public void run() {
                viewer.getTree().refresh();
            }
        };

        tbm.add(new Separator());

        collapseAllAction = new Action() {
            @Override
            public void run() {
                viewer.getTree().collapseAll();
            }
        };

        collapseAllAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.COLLAPSE_ALL_ICON));
        collapseAllAction.setToolTipText(UiConstants.Util.getString("ModelOutlinePage.collapseAllAction.tooltip")); //$NON-NLS-1$
        collapseAllAction.setText(UiConstants.Util.getString("ModelOutlinePage.collapseAllAction.text")); //$NON-NLS-1$
        tbm.add(collapseAllAction);
        collapseAllAction.setEnabled(true);

        hasInitialized = true;

        if (this.modelEditor.getCurrentPage() instanceof ModelEditorPage) {
            setActiveEditorPage((ModelEditorPage)this.modelEditor.getCurrentPage());
        }

        showPage(viewer.getClass().getName());
    }

    private ModelerActionService getModelerActionService() {
        return (ModelerActionService)UiPlugin.getDefault().getActionService(getSite().getPage());
    }

    /**
     * Called by ModelEditor to indicate that a different ModelEditorPage has become activated in the multi-page editor tab panel.
     * This class responds by looking up the outline page contribution for that ModelEditorPage and, if one exists, making it
     * available in the outline.
     * 
     * @param page the just-activated page in the ModelEditor.
     */
    // swjTODO: figure out the logic that should fire if the page changes while a contribution is showing.
    public void setActiveEditorPage( ModelEditorPage page ) {
        if (hasInitialized) {
            ModelEditorPageOutline contribution = (ModelEditorPageOutline)contributionMap.get(page.getClass().getName());
            if (contribution != null) {
                contributionAction.setToolTipText(contribution.getToolTipText());
                contributionAction.setImageDescriptor(contribution.getIcon());
                contributionAction.setEnabled(contribution.isEnabled());
                refreshAction.setEnabled(false);
            } else {
                contributionAction.setEnabled(false);
                contributionAction.setToolTipText(null);
                contributionAction.setImageDescriptor(blankIcon);
                showPage(viewer.getClass().getName());
                refreshAction.setEnabled(false);
            }
        }
    }

    @Override
    public void dispose() {
        // dispose all contributions to this panel
        for (Iterator iter = contributionMap.values().iterator(); iter.hasNext();) {
            ((ModelEditorPageOutline)iter.next()).dispose();
        }
        if (viewer != null) {
            viewer.dispose();
        }
        super.dispose();
    }

    @Override
    public Control getControl() {
        return pageBook;
    }

    public Object getCurrentViewer() {
        return currentViewer;
    }

    @Override
    public void setFocus() {
        // we cannot call super's setFocus, since its viewer is not initialized
        // (we never called super.createControl)
        if (currentViewer != null) {
            currentViewer.getControl().setFocus();
        } // endif
    }

    /**
     * Called by the toolbar actions, determines which outline page to show.
     * 
     * @param id the class name of either the ModelOutlineTreeViewer, or the active ModelEditorPage who's contribution to this
     *        panel should be shown.
     */
    protected void showPage( String id ) {
        if (!hasInitialized) {
            return;
        }

        Control pageToShow = null;

        if (id.equals(ModelOutlineTreeViewer.class.getName())) {
            contributionAction.setChecked(false);
            modelOutlineAction.setChecked(true);
            pageToShow = viewer.getControl();
            currentViewer = viewer;
        } else {
            ModelEditorPageOutline contribution = (ModelEditorPageOutline)contributionMap.get(id);
            if (contribution != null) {
                boolean isEnabled = true;

                // make sure the contribution's Control has been created.
                if (!initList.contains(id)) {
                    // only create the outline's control once it is about to be viewed.
                    contribution.createControl(pageBook);
                    initList.add(id);
                    isEnabled = contribution.isEnabled();
                }

                if (isEnabled) {
                    contributionAction.setChecked(true);
                    modelOutlineAction.setChecked(false);
                    pageToShow = contribution.getControl();
                    currentViewer = contribution;
                } else {
                    contributionAction.setChecked(false);
                    contributionAction.setEnabled(false);
                }

            }
        }

        if (pageToShow != null) {

            initKeyListener();

            pageBook.showPage(pageToShow);
        }
    }

    /**
     * Create the KeyListener for capturing DEL and F2 (rename?)
     */
    private void initKeyListener() {
        // create the adapter
        if (kaKeyAdapter == null) {

            kaKeyAdapter = new KeyAdapter() {
                @Override
                public void keyReleased( KeyEvent event ) {
                    handleKeyEvent(event);

                }
            };
        }

        // add the adapter as a listener
        if (currentViewer != null) {
            currentViewer.getControl().removeKeyListener(kaKeyAdapter);
            currentViewer.getControl().addKeyListener(kaKeyAdapter);

        }
    }

    /**
     * On certain keys execute certain actions
     */
    void handleKeyEvent( KeyEvent event ) {
        if (event.stateMask != 0) return;

        if (event.keyCode == SWT.F2) {
            // rename action
            try {

                IAction actRename = getModelerActionService().getAction(ActionFactory.RENAME.getId());
                if (actRename != null && actRename.isEnabled()) {
                    actRename.run();
                }
            } catch (CoreException ce) {

            }

        } else if (event.character == SWT.DEL) {
            // delete action
            IAction actDelete = this.getSite().getActionBars().getGlobalActionHandler(ActionFactory.DELETE.getId());
            if (actDelete != null && actDelete.isEnabled()) {
                actDelete.run();
            }
        }
    }

}
