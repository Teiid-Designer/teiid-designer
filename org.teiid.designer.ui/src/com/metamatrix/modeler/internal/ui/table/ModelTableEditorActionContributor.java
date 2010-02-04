/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.actions.ModelerGlobalActionsMap;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.actions.GlobalActionsMap;

/**
 * ModelTableEditorActionContributor
 */
public class ModelTableEditorActionContributor extends AbstractModelEditorPageActionBarContributor
                                               implements IModelerActionConstants,
                                                          UiConstants {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    
    // actions map is needed since we want to override the default print action
    private ModelerGlobalActionsMap actionsMap;
    
    private Collection menuManagers = new ArrayList();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    
    public ModelTableEditorActionContributor(ModelEditorPage thePage) {
        super(thePage);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     */
    @Override
    public void createContextMenu() {
        ModelTableEditor tableEditor = (ModelTableEditor)getEditorPage();
        
        // create context menu for each tab in the editor
        Map tableViewers = tableEditor.getTableViewerMap();
        
        if ((tableViewers != null) && !tableViewers.isEmpty()) {
            Set tabEntries = tableViewers.entrySet();
            Iterator itr = tabEntries.iterator();
            
            // Memory leak Defect 22290 requires that we remove this class as a listener to ALL the menu managers that were created
            // here. So let's cache them up when created.
            while (itr.hasNext()) {
                Map.Entry entry = (Map.Entry)itr.next();
                TableViewer viewer = (TableViewer)entry.getValue();
                Object newManager = createContextMenu(entry.getKey().toString() + ContextMenu.MENU_ID_SUFFIX, viewer.getControl());
                menuManagers.add(newManager);
            }
        }
    }
    
    void addContextMenu(Control control, String controlName) {
        Object newManager = createContextMenu(controlName + ContextMenu.MENU_ID_SUFFIX, control);
        // Memory leak Defect 22290 requires that we remove this class as a listener to ALL the menu managers that were created
        // here. So let's cache them up when created.
        menuManagers.add(newManager);
    }
    
    /** 
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
     * @since 5.0
     */
    @Override
    public void contributeToMenu(IMenuManager theMenuManager) {
        // this method will be called once so make menubar contributions here.
        // visibility of these contributions are controlled by the Eclipse framework
        IContributionItem item = theMenuManager.find(ModelerActionBarIdManager.getEditMenuId());
        
        if ((item != null) && (item instanceof IMenuManager)) {
            IMenuManager editMenu = (IMenuManager)item;
            
            // first see if the item already exists. another contributor of this type could have
            // already added these items. assume if you find one all are present.
            Object temp = getEditColumnVisibilityAndOrderItem(theMenuManager);
            
            if (temp == null) {
                editMenu.insertAfter(ModelerActionBarIdManager.getCutGroupExtrasMarkerId(),
                                     new ActionContributionItem(getEditColumnVisibilityAndOrderAction()));
                editMenu.insertAfter(getEditColumnVisibilityAndOrderAction().getId(),
                                     new ActionContributionItem(getInsertRowsAction()));
                editMenu.insertAfter(getInsertRowsAction().getId(), 
                                     new ActionContributionItem(getRefreshTableAction()));
            }
        }
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#pageActivated()
     */
    @Override
    public void pageActivated() {
        // set main actionbar contributions to be visible
        setActionsVisible(true);
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#pageDeactivated()
     */
    @Override
    public void pageDeactivated() {
        // set main actionbar contributions to be hidden
        setActionsVisible(false);
    }

    /**
     * Helper method to show/hide action contributions made to the main actionbars.
     * @param theShowFlag the flag indicating if the actions should be visible or not
     */
    private void setActionsVisible(boolean theShowFlag) {
        IActionBars actionBars = getActionBars();
        
        if (actionBars != null) {
            IContributionManager mgr = actionBars.getMenuManager();
            mgr = (IMenuManager)mgr.find(ModelerActionBarIdManager.getEditMenuId());
            IContributionItem item = getEditColumnVisibilityAndOrderItem(mgr);
            
            if (item != null) {
                item.setVisible(theShowFlag);
            }
            
            item =  getClipboardPasteItem(mgr);
            
            if (item != null) {
                item.setVisible(theShowFlag);
            }
            
            item =  getInsertRowsItem(mgr);
            
            if (item != null) {
                item.setVisible(theShowFlag);
            }
            
            item =  getRefreshTableItem(mgr);
            
            if (item != null) {
                item.setVisible(theShowFlag);
            }
            
            mgr.update(true);
            mgr = actionBars.getToolBarManager();
            
            item =  getClipboardPasteItem(mgr);
            
            if (item != null) {
                item.setVisible(theShowFlag);
            }
            
            item =  getInsertRowsItem(mgr);
            
            if (item != null) {
                item.setVisible(theShowFlag);
            }
            
            mgr.update(true);
        }
    }

    @Override
    public void menuAboutToShow(IMenuManager theMenuMgr) {
        super.menuAboutToShow(theMenuMgr);
        theMenuMgr.add(getEditColumnVisibilityAndOrderAction());
        theMenuMgr.add(getInsertRowsAction()); 
        theMenuMgr.add(getRefreshTableAction());
    }
    
    private IAction getClipboardPasteAction() {
        IAction action = null;
        ActionService service = getActionService();
        
        try {
            action = service.getAction(TableEditorActions.CLIPBOARD_PASTE);
        } catch (CoreException theException) {
            Util.log(theException);
        }
        
        return action;
    }

    private IContributionItem getClipboardPasteItem(IContributionManager theMgr) {
        return theMgr.find(getClipboardPasteAction().getId());
    }
    
    private IAction getInsertRowsAction() {
        IAction action = null;
        ActionService service = getActionService();
        
        try {
            action = service.getAction(TableEditorActions.INSERT_ROWS);
        } catch (CoreException theException) {
            Util.log(theException);
        }
        
        return action;
    }
    
    private IContributionItem getInsertRowsItem(IContributionManager theMgr) {
        return theMgr.find(getInsertRowsAction().getId());
    }
    
    private IAction getEditColumnVisibilityAndOrderAction() {
        IAction action = null;
        ActionService service = getActionService();
        
        try {
            action = service.getAction(TableEditorActions.EDIT_COLUMNS);
        } catch (CoreException theException) {
            Util.log(theException);
        }
        
        return action;        
    }

    private IContributionItem getEditColumnVisibilityAndOrderItem(IContributionManager theMgr) {
        return theMgr.find(getEditColumnVisibilityAndOrderAction().getId());
    }
    
    private IAction getPrintAction() {
        IAction action = null;
        ActionService service = getActionService();
        
        try {
            action = service.getAction( TableEditorActions.PRINT );
        } catch (CoreException theException) {
            Util.log(theException);
        }
        
        return action;
    }
    
    private IAction getRefreshTableAction() {
        IAction action = null;
        ActionService service = getActionService();
        
        try {
            action = service.getAction(TableEditorActions.REFRESH_TABLE);
        } catch (CoreException theException) {
            Util.log(theException);
        }
        
        return action;
    }
    
    private IContributionItem getRefreshTableItem(IContributionManager theMgr) {
        return theMgr.find(getRefreshTableAction().getId());
    }
    
    /** 
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     * @since 5.0
     */
    @Override
    public void contributeToToolBar(IToolBarManager theToolBarMgr) {
        // method called once by the Eclipse framework. make sure action hasn't already been added
        // by another contributor instance of this type
        Object temp = getClipboardPasteItem(theToolBarMgr);
        
        if (temp == null) {
            theToolBarMgr.add(new ActionContributionItem(getClipboardPasteAction()));
        }
        temp = getInsertRowsItem(theToolBarMgr);
        
        if (temp == null) {
            theToolBarMgr.add(new ActionContributionItem(getInsertRowsAction()));
        }
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#getGlobalActions()
     */
    @Override
    public GlobalActionsMap getGlobalActions() {
        if ( actionsMap == null ) {
            
            actionsMap = new ModelerGlobalActionsMap();
            actionsMap.reset();

            actionsMap.put( IModelerActionConstants.EclipseGlobalActions.PRINT, 
                            getPrintAction() );
            
            // jh Defect 19246: Wire the Table Paste action to the global PASTE
            //                  so that CTRL-V will work in the Table Editor.
            actionsMap.put( IModelerActionConstants.EclipseGlobalActions.PASTE, 
                            getClipboardPasteAction() );  
        }
        
        return actionsMap;  
    }

    /**
     *  
     * @see org.eclipse.ui.IEditorActionBarContributor#dispose()
     * @since 5.0
     */
    @Override
    public void dispose() {
        // Memory leak Defect 22290 requires that we remove this class as a listener to ALL the menu managers that were created
        // here.
        for( Iterator iter = menuManagers.iterator(); iter.hasNext(); ) {
            MenuManager nextManager = (MenuManager)iter.next();
            nextManager.removeMenuListener(this);
        }
        super.dispose();
    }
    
}
