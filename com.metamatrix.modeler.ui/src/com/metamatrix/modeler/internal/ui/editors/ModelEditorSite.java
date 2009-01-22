/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import java.util.ArrayList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.PopupMenuExtender;

/**
 * ModelEditorSite is an IEditorSite layer between the ModelEditor multi-page editor and the ModelEditorPage IEditorParts that it
 * contains. Each ModelEditorPage has a ModelEditorSite. This class was overridden because ModelEditorSelectionProvider handles
 * the selection for all ModelEditorPages.
 */
@SuppressWarnings( "deprecation" )
public class ModelEditorSite implements IEditorSite {

    /**
     * The nested editor.
     */
    private IEditorPart editor;

    /**
     * The multi-page editor.
     */
    private MultiPageModelEditor multiPageEditor;

    /**
     * The selection provider; <code>null</code> if none.
     * 
     * @see #setSelectionProvider
     */
    private ISelectionProvider selectionProvider = null;

    /**
     * The selection change listener, initialized lazily; <code>null</code> if not yet created.
     */
    private ISelectionChangedListener selectionChangedListener = null;

    /**
     * The list of popup menu extenders; <code>null</code> if none registered.
     */
    private ArrayList menuExtenders;

    /**
     * Creates a site for the given page nested within the given multi-page editor.
     * 
     * @param editor the multi-page editor
     * @param page the nested editor
     */
    public ModelEditorSite( MultiPageModelEditor editor,
                            IEditorPart page ) {
        this.multiPageEditor = editor;
        this.editor = page;
    }

    /**
     * Dispose the contributions.
     */
    public void dispose() {
        if (menuExtenders != null) {
            for (int i = 0; i < menuExtenders.size(); i++) {
                ((PopupMenuExtender)menuExtenders.get(i)).dispose();
            }
            menuExtenders = null;
        }
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IEditorSite</code> method returns <code>null</code>,
     * since nested editors do not have their own action bar contributor.
     */
    public IEditorActionBarContributor getActionBarContributor() {
        return null;
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IEditorSite</code> method forwards to the multi-page
     * editor to return the action bars.
     */
    public IActionBars getActionBars() {
        return multiPageEditor.getEditorSite().getActionBars();
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor to return the decorator manager.
     * 
     * @deprecated use IWorkbench.getDecoratorManager()
     */
    @Deprecated
    public ILabelDecorator getDecoratorManager() {
        return getWorkbenchWindow().getWorkbench().getDecoratorManager().getLabelDecorator();
    }

    /**
     * Returns the nested editor.
     * 
     * @return the nested editor
     */
    public IEditorPart getEditor() {
        return editor;
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method returns an empty string
     * since the nested editor is not created from the registry.
     */
    public String getId() {
        return ""; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IWorkbenchPartSite#getKeyBindingService()
     */
    public IKeyBindingService getKeyBindingService() {
        return null;
    }

    /**
     * Returns the multi-page editor.
     * 
     * @return the multi-page editor
     */
    public MultiPageModelEditor getMultiPageEditor() {
        return multiPageEditor;
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor to return the workbench page.
     */
    public IWorkbenchPage getPage() {
        return getMultiPageEditor().getSite().getPage();
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method returns an empty string
     * since the nested editor is not created from the registry.
     */
    public String getPluginId() {
        return ""; //$NON-NLS-1$
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method returns an empty string
     * since the nested editor is not created from the registry.
     */
    public String getRegisteredName() {
        return ""; //$NON-NLS-1$
    }

    /**
     * Returns the selection changed listener which listens to the nested editor's selection changes, and calls
     * <code>handleSelectionChanged</code>.
     * 
     * @return the selection changed listener
     */
    private ISelectionChangedListener getSelectionChangedListener() {
        if (selectionChangedListener == null) {
            selectionChangedListener = new ISelectionChangedListener() {
                public void selectionChanged( SelectionChangedEvent event ) {
                    ModelEditorSite.this.handleSelectionChanged(event);
                }
            };
        }
        return selectionChangedListener;
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method returns the selection
     * provider set by <code>setSelectionProvider</code>.
     */
    public ISelectionProvider getSelectionProvider() {
        return selectionProvider;
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor to return the shell.
     */
    public Shell getShell() {
        return getMultiPageEditor().getSite().getShell();
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor to return the workbench window.
     */
    public IWorkbenchWindow getWorkbenchWindow() {
        return getMultiPageEditor().getSite().getWorkbenchWindow();
    }

    /**
     * Handles a selection changed event from the nested editor. The default implementation gets the selection provider from the
     * multi-page editor's site, and calls <code>fireSelectionChanged</code> on it (only if it is an instance of
     * <code>MultiPageSelectionProvider</code>), passing a new event object.
     * <p>
     * Subclasses may extend or reimplement this method.
     * </p>
     * 
     * @param event the event
     */
    protected void handleSelectionChanged( SelectionChangedEvent event ) {
        ISelectionProvider parentProvider = getMultiPageEditor().getSite().getSelectionProvider();
        if (parentProvider instanceof ModelEditorSelectionProvider) {
            SelectionChangedEvent newEvent = new SelectionChangedEvent(parentProvider, event.getSelection());
            ((ModelEditorSelectionProvider)parentProvider).fireSelectionChanged(newEvent);
        }
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor for registration.
     */
    public void registerContextMenu( String menuID,
                                     MenuManager menuMgr,
                                     ISelectionProvider selProvider ) {
        if (menuExtenders == null) {
            menuExtenders = new ArrayList(1);
        }
        menuExtenders.add(new PopupMenuExtender(menuID, menuMgr, selProvider, editor));
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor for registration.
     */
    public void registerContextMenu( MenuManager menuManager,
                                     ISelectionProvider selectionProvider ) {
        getMultiPageEditor().getSite().registerContextMenu(menuManager, selectionProvider);
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor for registration.
     */
    public void registerContextMenu( final String menuId,
                                     final MenuManager menuManager,
                                     final ISelectionProvider selectionProvider,
                                     final boolean includeEditorInput ) {
        registerContextMenu(menuId, menuManager, selectionProvider);
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method forwards to the
     * multi-page editor for registration.
     */
    public void registerContextMenu( final MenuManager menuManager,
                                     final ISelectionProvider selectionProvider,
                                     final boolean includeEditorInput ) {
        registerContextMenu(menuManager, selectionProvider);
    }

    /**
     * The <code>MultiPageEditorSite</code> implementation of this <code>IWorkbenchPartSite</code> method remembers the selection
     * provider, and also hooks a listener on it, which calls <code>handleSelectionChanged</code> when a selection changed event
     * occurs.
     * 
     * @see #handleSelectionChanged
     */
    public void setSelectionProvider( ISelectionProvider provider ) {
        ISelectionProvider oldSelectionProvider = selectionProvider;
        selectionProvider = provider;
        if (oldSelectionProvider != null) {
            oldSelectionProvider.removeSelectionChangedListener(getSelectionChangedListener());
        }
        if (selectionProvider != null) {
            selectionProvider.addSelectionChangedListener(getSelectionChangedListener());
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPartSite#getPart()
     * @since 5.0.1
     */
    public IWorkbenchPart getPart() {
        return this.multiPageEditor;
    }

    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.services.IServiceLocator#getService(java.lang.Class)
     */
    public Object getService( Class api ) {
        return getMultiPageEditor().getEditorSite().getService(api);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.services.IServiceLocator#hasService(java.lang.Class)
     */
    public boolean hasService( Class api ) {
        return false;
    }
}

// /* (non-Javadoc)
// * @see org.eclipse.ui.IWorkbenchSite#getSelectionProvider()
// */
// public ISelectionProvider getSelectionProvider() {
// return modelEditor.getSite().getSelectionProvider();
// }
//
// /* (non-Javadoc)
// * @see org.eclipse.ui.IWorkbenchSite#setSelectionProvider(org.eclipse.jface.viewers.ISelectionProvider)
// */
// public void setSelectionProvider(ISelectionProvider provider) {
// super.setSelectionProvider(provider);
// }
//
// /* (non-Javadoc)
// * @see org.eclipse.ui.IEditorSite#getActionBarContributor()
// */
// public IEditorActionBarContributor getActionBarContributor() {
// return modelEditor.getEditorSite().getActionBarContributor();
// }
