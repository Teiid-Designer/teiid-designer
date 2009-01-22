/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.navigation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySourceProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.relationship.NavigationContext;
import com.metamatrix.modeler.relationship.NavigationContextException;
import com.metamatrix.modeler.relationship.NavigationContextInfo;
import com.metamatrix.modeler.relationship.NavigationHistory;
import com.metamatrix.modeler.relationship.NavigationNode;
import com.metamatrix.modeler.relationship.RelationshipPlugin;
import com.metamatrix.modeler.relationship.ui.UiConstants;
import com.metamatrix.modeler.relationship.ui.navigation.actions.BackDropDownAction;
import com.metamatrix.modeler.relationship.ui.navigation.actions.EditWrapperAction;
import com.metamatrix.modeler.relationship.ui.navigation.actions.ForwardDropDownAction;
import com.metamatrix.modeler.relationship.ui.navigation.actions.NavigateToAction;
import com.metamatrix.modeler.relationship.ui.navigation.actions.OpenWrapperAction;
import com.metamatrix.modeler.relationship.ui.navigation.actions.PropertiesAction;
import com.metamatrix.modeler.relationship.ui.navigation.actions.RefreshAction;
import com.metamatrix.modeler.relationship.ui.navigation.actions.SetFocusAction;
import com.metamatrix.modeler.relationship.ui.navigation.selection.INavigationDoubleClickListener;
import com.metamatrix.modeler.relationship.ui.navigation.selection.NavigationDoubleClickEvent;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionProvider;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * NavigationView
 */
public class NavigationView extends ViewPart
    implements INotifyChangedListener, IResourceChangeListener, ISelectionChangedListener, IModelerActionConstants,
    INavigationDoubleClickListener {

    private Object currentObject;
    NavigationEditor editor;
    private ModelObjectPropertySourceProvider propertySourceProvider;
    private ISelectionProvider selectionProvider;
    private NavigationStatusBarUpdater statusBarUpdater;

    private BackDropDownAction backDropDownAction;
    private ForwardDropDownAction forwardDropDownAction;
    private NavigationFocusComboBox focusCombo;

    private OpenWrapperAction openAction;
    private EditWrapperAction editAction;
    private SetFocusAction focusAction;
    private NavigateToAction navigateToAction;
    private PropertiesAction propertiesAction;
    private RefreshAction refreshAction;

    private NavigationHistory history;
    NavigationContext currentContext;
    private List listenerList = new ArrayList();

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        history = RelationshipPlugin.getSharedNavigationHistory();

        editor = new NavigationEditor(new NavigatorLabelProvider(), this);
        editor.createPartControl(parent);

        ModelUtilities.addNotifyChangedListener(this);

        this.selectionProvider = new SelectionProvider();
        getViewSite().setSelectionProvider(selectionProvider);

        createActions();
        fillToolBar();

        createContextMenu(editor.getControl());
        parent.addControlListener(new ControlListener() {
            public void controlMoved( ControlEvent e ) {
            }

            public void controlResized( ControlEvent e ) {
                if (currentContext != null) {
                    // editor.setContents(currentContext);
                    final Runnable runnable = new Runnable() {
                        public void run() {
                            // Show dialog
                            editor.refreshLayout();
                        }
                    };
                    Display.getDefault().asyncExec(runnable);
                    // editor.refreshLayout();
                }
            }

        });

        if (this.history != null && this.history.getCurrent() != null) {
            setCurrentContext(this.history.getCurrent());
        }

        // hook up our status bar manager for EObjects
        IStatusLineManager slManager = getViewSite().getActionBars().getStatusLineManager();
        statusBarUpdater = new NavigationStatusBarUpdater(slManager);

        // add this as a selection listener to the editor
        editor.getSelectionHandler().getViewer().addSelectionChangedListener(this);
        editor.getSelectionHandler().addDoubleClickListener(this);
    }

    private void createContextMenu( Control c ) {
        // construct context menu - this is populated in the menuAboutToShow(IMenuManager) method
        String contextMenuId = NavigationView.class.getName() + ContextMenu.MENU_ID_SUFFIX;
        MenuManager mgr = new MenuManager(null, contextMenuId);
        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow( IMenuManager theMenuMgr ) {
                fillContextMenu(theMenuMgr);
            }
        });

        c.setMenu(mgr.createContextMenu(c));
    }

    public void addNavigationListener( NavigationListener listener ) {
        if (listener != null && !this.listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeNavigationListener( NavigationListener listener ) {
        listenerList.remove(listener);
    }

    public NavigationHistory getNavigationHistory() {
        return this.history;
    }

    public NavigationContext getCurrentNavigationContext() {
        return this.currentContext;
    }

    public void clearHistory() {
        this.history.clearHistory();
        fireContextChanged();
    }

    public EObject resolve( final NavigationNode node ) throws CoreException {
        final Container container = ModelerCore.getModelContainer();
        return container.getEObject(node.getModelObjectUri(), true);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {
        if (editor != null) this.editor.setFocus();
    }

    // ======================================================================
    // Methods to set the context of this view

    /**
     * Set the current context to the specified NavigationContext. This method is typically called internally.
     */
    public void setCurrentContext( NavigationContext context ) {
        this.currentContext = context;
        editor.setContents(context);
        fireContextChanged();
    }

    /**
     * Set the current context to the specified NavigationContextInfo obtained from the shared NavigationHistory. This method is
     * typically called by the back/forward history actions.
     */
    public void setCurrentContext( NavigationContextInfo contextInfo ) {
        if (this.history != null) {
            try {
                setCurrentContext(this.history.selectFromHistory(contextInfo));
            } catch (NavigationContextException e) {
                UiConstants.Util.log(e);
            }
        }
    }

    /**
     * Set the current context to the specified NavigationContext. This method is typically called from elsewhere in the
     * workbench.
     */
    public void setCurrentObject( EObject obj ) {
        try {
            setCurrentContext(this.history.navigateTo(obj));
        } catch (NavigationContextException e) {
            UiConstants.Util.log(e);
        }
    }

    /**
     * Set the current context to the specified NavigationContext. This method is typically called from elsewhere in the
     * workbench.
     */
    public void setCurrentObject( NavigationNode node ) {
        try {
            setCurrentContext(this.history.navigateTo(node.getModelObjectUri()));
        } catch (NavigationContextException e) {
            UiConstants.Util.log(e);
        }
    }

    /**
     * Refresh the current context in this view.
     */
    public void refresh() {
        try {
            this.history.refresh();
            setCurrentContext(history.getCurrent());
        } catch (NavigationContextException e) {
            UiConstants.Util.log(e);
        }
    }

    @Override
    public void dispose() {
        ModelUtilities.removeNotifyChangedListener(this);

        if (propertySourceProvider != null) {
            propertySourceProvider.dispose();
        }

        super.dispose();
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {
        // if the target of the notification is this object's annotation, refresh the display
        EObject targetEObject = NotificationUtilities.getEObject(notification);
        if (targetEObject instanceof Annotation) {
            targetEObject = ((Annotation)targetEObject).getAnnotatedObject();
            if (targetEObject != null && targetEObject.equals(currentObject)) {
                setCurrentObject(targetEObject);
            }
        }
    }

    /**
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged( IResourceChangeEvent event ) {
        // swjTODO: implement
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IPropertySheetPage.class)) {
            if (propertySourceProvider == null) {
                propertySourceProvider = ModelUtilities.getPropertySourceProvider(); // new ModelObjectPropertySourceProvider();
            }

            return propertySourceProvider.getPropertySheetPage();
        }
        return super.getAdapter(adapter);
    }

    private void createActions() {
        backDropDownAction = new BackDropDownAction(this);
        forwardDropDownAction = new ForwardDropDownAction(this);
        navigateToAction = new NavigateToAction(selectionProvider);
        propertiesAction = new PropertiesAction(selectionProvider);
        focusAction = new SetFocusAction(this);
        openAction = new OpenWrapperAction();
        openAction.setEnabled(false);
        editAction = new EditWrapperAction();
        editAction.setEnabled(false);
        refreshAction = new RefreshAction(this);
    }

    private void fillToolBar() {
        IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
        IMenuManager mm = getViewSite().getActionBars().getMenuManager();

        focusCombo = new NavigationFocusComboBox(this);
        tbm.add(focusCombo.getLabel());
        tbm.add(focusCombo);

        tbm.add(backDropDownAction);
        mm.add(backDropDownAction);
        tbm.add(forwardDropDownAction);
        mm.add(forwardDropDownAction);

        tbm.add(new Separator());
        mm.add(new Separator());
        tbm.add(navigateToAction);
        mm.add(navigateToAction);
        tbm.add(propertiesAction);
        mm.add(navigateToAction);
        tbm.add(new Separator());
        mm.add(new Separator());
        tbm.add(refreshAction);
        mm.add(refreshAction);
    }

    void fillContextMenu( IMenuManager theMenuMgr ) {
        theMenuMgr.add(new Separator(UiConstants.Extensions.Navigator.CONTEXT_MENU_GROUP_0));
        theMenuMgr.add(focusAction);
        theMenuMgr.add(new Separator(UiConstants.Extensions.Navigator.CONTEXT_MENU_GROUP_1));
        theMenuMgr.add(navigateToAction);
        theMenuMgr.add(propertiesAction);
        theMenuMgr.add(new Separator(UiConstants.Extensions.Navigator.CONTEXT_MENU_GROUP_2));
        theMenuMgr.add(openAction);
        theMenuMgr.add(editAction);
        theMenuMgr.add(new Separator(ContextMenu.ADDITIONS));
    }

    private void fireContextChanged() {
        Iterator iter = listenerList.iterator();
        while (iter.hasNext()) {
            ((NavigationListener)iter.next()).navigationChanged(this.currentContext);
        }
    }

    /**
     * Hears selection from the navigation diagram and passes it on to the actions
     * 
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        openAction.selectionChanged(this, event.getSelection());
        focusAction.selectionChanged(this, event.getSelection());
        navigateToAction.selectionChanged(this, event.getSelection());
        editAction.selectionChanged(this, event.getSelection());
        propertiesAction.selectionChanged(this, event.getSelection());
        statusBarUpdater.selectionChanged(event);
    }

    public void doubleClick( NavigationDoubleClickEvent event ) {
        Object obj = SelectionUtilities.getSelectedObject(event.getSelection());
        if (obj instanceof NavigationNode) {
            setCurrentObject((NavigationNode)obj);
        }
    }
}
