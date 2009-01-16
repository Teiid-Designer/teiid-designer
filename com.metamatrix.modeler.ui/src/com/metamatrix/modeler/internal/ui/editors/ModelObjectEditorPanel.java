/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */
package com.metamatrix.modeler.internal.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.PageBook;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.ui.actions.ActionService;

/**
 * ModelObjectEditorPanel is a container for ModelObjectEditor controls to be placed beneath the ModelEditor.
 */
public class ModelObjectEditorPanel implements IAdaptable, FocusListener, IPropertyListener {

    static final String RESTORE = UiConstants.Util.getString("ModelObjectEditorPanel.restore"); //$NON-NLS-1$
    static final String MAXIMIZE = UiConstants.Util.getString("ModelObjectEditorPanel.maximize"); //$NON-NLS-1$
    static final String CLOSE = UiConstants.Util.getString("ModelObjectEditorPanel.close"); //$NON-NLS-1$
    private static final char DIRTY_CHAR = '*';

    private MultiPageModelEditor modelEditor;

    CLabel titleLabel;
    ToolBar toolBar;
    private ToolBarManager toolBarMgr;
    private MenuManager menuMgr;
    boolean isZoomed = false;
    private Action closeAction;

    private ViewForm control;
    private ArrayList editorList = new ArrayList();

    // private ToolBarManager toolBarManager;
    private PageBook pageBook;
    ModelObjectEditorPage activeEditor;

    private List focusListeners = new ArrayList();

    public ModelObjectEditorPanel( MultiPageModelEditor modelEditor,
                                   Composite parent,
                                   int style ) {
        this.modelEditor = modelEditor;
        createControl(parent);
        modelEditor.addPropertyListener(this);
    }

    /**
     * creates the content of this panel
     */
    public void createControl( Composite parent ) {
        if (control != null && !control.isDisposed()) return;

        // Create the ViewForm, which is the outer control of this panel
        // to hold the title label, the toolbar, and the pagebook
        control = new ViewForm(parent, SWT.BORDER);
        control.setLayoutData(new GridData(GridData.FILL_BOTH));
        control.marginWidth = 0;
        control.marginHeight = 0;

        // Create a title bar
        createTitleBar();

        // Create a tool bar
        createToolBar();

        // Create a PageBook for holding controls
        createPageBook();

        // Only include the ISV toolbar and the content in the tab list.
        // All actions on the System toolbar should be accessible on the pane menu.
        if (control.getContent() == null) {
            // content can be null if view creation failed
            control.setTabList(new Control[] {toolBar});
        } else {
            control.setTabList(new Control[] {toolBar, control.getContent()});
        }
    }

    protected void createPageBook() {
        pageBook = new PageBook(control, SWT.NULL);
        // pageBook.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        control.setContent(pageBook);
        setVisible(false);
    }

    private void addModelObjectEditor( ModelObjectEditorPage editor ) {
        if (!editorList.contains(editor)) {
            editor.createControl(pageBook);
            editorList.add(editor);
        }
    }

    /**
     * Show the specified ModelObjectEditorPage in this panel, editing the specified object. The MultiPageModelEditor has already
     * determined that the page can edit the object.
     * 
     * @param editor
     * @param object
     */
    public void activateModelObjectEditor( ModelObjectEditorPage editor,
                                           Object object ) {
        // first, make sure the current editor is willing to deactivate
        if (deactivate(false)) {
            // see if we are just sending a new object to the same editor
            if (editor == activeEditor) {
                activeEditor.edit(object);
                toolBarMgr.update(true);
                updateTitles();
            } else {
                // we need to close the existing editor and activate a new one
                addModelObjectEditor(editor);
                editor.edit(object);
                activeEditor = editor;
                pageBook.showPage(editor.getControl());
                editor.contributeToolbarActions(toolBarMgr);
                if (editor.canClose()) {
                    toolBarMgr.add(closeAction);
                }
                toolBarMgr.update(true);
                updateTitles();
                activeEditor.addPropertyListener(this);
            }
        }
        setVisible(true);
    }

    /**
     * Adds the given FocusListener to our list of listeners.
     * 
     * @param listener the IFontChangeListener to be added
     */
    public void addFocusListener( FocusListener listener ) {
        //        System.out.println("[MOEP.addFocusListener] adding listener: " + listener ); //$NON-NLS-1$
        focusListeners.add(listener);
    }

    /**
     * Removes the given FocusListener from our list of listeners.
     * 
     * @param listener the IFontChangeListener to be removed
     */
    public void removeFocusListener( FocusListener listener ) {
        //        System.out.println("[MOEP.removeFocusListener] removing listener: " + listener ); //$NON-NLS-1$
        focusListeners.remove(listener);
    }

    /**
     * Notifies listeners that the zoom level has changed.
     */
    public void firefocusGained( FocusEvent fe ) {
        //        System.out.println("[MOEP.fireFocusGained] focus list: " + focusListeners ); //$NON-NLS-1$
        Iterator iter = focusListeners.iterator();
        while (iter.hasNext())
            ((FocusListener)iter.next()).focusGained(fe);

        updateForFocus();
    }

    /**
     * Notifies listeners that the zoom level has changed.
     */
    public void firefocusLost( FocusEvent fe ) {
        //        System.out.println("[MOEP.firefocusLost] focus list: " + focusListeners ); //$NON-NLS-1$
        Iterator iter = focusListeners.iterator();
        while (iter.hasNext())
            ((FocusListener)iter.next()).focusLost(fe);
    }

    public boolean hasFocus() {
        return hasFocus(this.control);
    }

    private boolean hasFocus( Control control ) {
        if (control.isFocusControl()) {
            return true;
        }

        if (control instanceof Composite) {
            Control[] kids = ((Composite)control).getChildren();

            for (int i = 0; i < kids.length; ++i) {
                if (hasFocus(kids[i])) {
                    return true;
                }
            }
        }

        return false;
    }

    public void focusLost( FocusEvent fe ) {
        //        System.out.println("[ModelObjectEditorPanel.focusLost]"); //$NON-NLS-1$
        firefocusLost(fe);
    }

    public void focusGained( FocusEvent fe ) {
        //        System.out.println("[ModelObjectEditorPanel.focusGained]"); //$NON-NLS-1$
        firefocusGained(fe);
    }

    public void updateForFocus() {
        if (getActiveEditor() != null) {
            getActiveEditor().updateReadOnlyState();
        }
    }

    /**
     * Added here to provide IFindReplaceTarget adapter.
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class key ) {
        Object oResult = null;

        if (key.equals(IFindReplaceTarget.class)) {

            /*
             * 1. get current page from 'ModelEditor'. if it is the diagram editor, a. if current page is the 'Source' tab and it
             * has focus, return the Source Tab's TextViewer ELSE b. if the optional TransformationObjectEditorPage is the
             * 'activeEditor', if this editor's TextViewer has focus return the TOEP's TextViewer
             */

            // 2. If we did not find one yet, try looking at the TransformationObjectEditorPage
            if (activeEditor != null && activeEditor instanceof IAdaptable) {
                oResult = ((IAdaptable)activeEditor).getAdapter(IFindReplaceTarget.class);
            }

            // 1. Check for an IFindReplaceTarget in the current editor
            if (oResult == null) {

                if (modelEditor != null && modelEditor instanceof ModelEditor) {
                    IEditorPart editorPage = ((ModelEditor)modelEditor).getCurrentPage();

                    if (editorPage != null) {
                        /*
                         * PROBLEM: We only want the following code to successfully return an adapter when the Editor involved is
                         * VISIBLE, and HAS FOCUS.
                         */
                        Object oAdapter = editorPage.getAdapter(IFindReplaceTarget.class);

                        if (oAdapter != null && oAdapter instanceof IFindReplaceTarget) {
                            oResult = oAdapter;
                        }
                    }
                }
            }

        }

        return oResult;
    }

    /**
     * @since 4.0
     */
    public ActionService getActionService() {

        IEditorPart editorPage = ((ModelEditor)modelEditor).getCurrentPage();
        if (editorPage != null) {
            return UiPlugin.getDefault().getActionService(editorPage.getSite().getPage());
        }
        return null;
    }

    /**
     * Callback from ModelEditor that a doSave is about to occur and any active ModelObjectEditor needs to save it's state to the
     * model.
     * 
     * @param isClosing true if the ModelEditor is closing, false if this is a simple save.
     */
    public void saveEditorState( boolean isClosing ) {
        if (activeEditor != null && activeEditor.isDirty()) {
            activeEditor.doSave(isClosing);
        }
    }

    /**
     * Request that the current ModelObjectEditor be deactivated and the panel be closed. Since client controls can veto this
     * request, the method returns true if the panel actually closed, false if it did not.
     * 
     * @param closePanel
     * @return
     */
    public boolean close() {
        return deactivate(true);
    }

    /**
     * Request that the current ModelObjectEditor be deactivated, and the panel optionally closed. Since client controls can veto
     * this request, the method returns true if the panel actually closed, false if it did not.
     * 
     * @param closePanel true if the panel should be closed, false if the editor is only being deactivated so that a new editor
     *        can replace it.
     * @return
     */
    boolean deactivate( boolean closePanel ) {
        boolean result = false;
        if (activeEditor != null) {
            if (activeEditor.deactivate()) {
                result = true;
                toolBarMgr.removeAll();
                if (closePanel) {
                    setVisible(false);
                }
                activeEditor.removePropertyListener(this);
                activeEditor = null;
            }
        } else {
            result = true;
            if (closePanel) {
                setVisible(false);
            }
        }
        return result;
    }

    /**
     * Create a title bar for the pane. - the view icon and title to the far left - the view toolbar appears in the middle. - the
     * view pulldown menu, pin button, and close button to the far right.
     */
    protected void createTitleBar() {
        // Only do this once.
        if (titleLabel != null) return;

        titleLabel = new CLabel(control, SWT.SHADOW_NONE);
        titleLabel.setAlignment(SWT.LEFT);
        titleLabel.setBackground(null, null);

        titleLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown( MouseEvent e ) {
                // the PaneMenu is a popup menu over the title bar of the view. it shows the following actions:
                // Restore, Move (View, TabGroup), Size (Left, Right, Top, Bottom), FastView, Maximize, and Close
                if (e.button == 3) {
                    showPaneMenu(titleLabel, new Point(e.x, e.y));
                } else if ((e.button == 1) && overImage(e.x)) {
                    showPaneMenu();
                }
            }

            @Override
            public void mouseDoubleClick( MouseEvent event ) {
                doZoom();
            }
        });
        updateTitles();
        control.setTopLeft(titleLabel);

    }

    /**
     * Shows the pane menu (system menu) for this pane.
     */
    public void showPaneMenu() {
        Rectangle bounds = titleLabel.getBounds();
        showPaneMenu(titleLabel, new Point(0, bounds.height));
    }

    protected void showPaneMenu( Control parent,
                                 Point point ) {
        if (menuMgr == null) {
            menuMgr = new MenuManager();
            menuMgr.add(new PaneContribution());
        }
        Menu aMenu = menuMgr.createContextMenu(parent);
        // open menu
        point = parent.toDisplay(point);
        aMenu.setLocation(point.x, point.y);
        aMenu.setVisible(true);
    }

    protected void createToolBar() {

        // View toolbar
        toolBar = new ToolBar(control, SWT.FLAT | SWT.WRAP);
        control.setTopRight(toolBar);
        toolBar.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDoubleClick( MouseEvent event ) {
                // 1GD0ISU: ITPUI:ALL - Dbl click on view tool cause zoom
                if (toolBar.getItem(new Point(event.x, event.y)) == null) doZoom();
            }
        });
        toolBarMgr = new PaneToolBarManager(toolBar);

        closeAction = new Action() {

            @Override
            public void run() {
                deactivate(true);
            }
        };
        closeAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CLOSE_ICON));
        closeAction.setToolTipText(CLOSE);

    }

    public void dispose() {
        deactivate(true);
        if (toolBarMgr != null) toolBarMgr.dispose();
        if (menuMgr != null) menuMgr.dispose();
    }

    /**
     * Change the zoom state of this panel
     */
    public void doZoom() {
        isZoomed = !isZoomed;
        modelEditor.zoomObjectEditor(isZoomed);
    }

    /**
     * Shows the receiver if <code>visible</code> is true otherwise hide it.
     */
    private void setVisible( boolean makeVisible ) {
        if (control != null && !control.isDisposed()) {
            if (makeVisible) {
                isZoomed = false;
                control.setVisible(true);
            } else {
                modelEditor.zoomObjectEditor(false);
                isZoomed = false;
                control.setVisible(false);
            }
            control.getParent().layout();
        }
    }

    /**
     * Update the title attributes.
     */
    public void updateTitles() {
        String text = null;
        if (activeEditor != null) {
            text = activeEditor.getTitle();
            Image image = activeEditor.getTitleImage();
            // only update and relayout if text or image has changed
            if (!text.equals(titleLabel.getText()) || image != titleLabel.getImage()) {
                titleLabel.setText(text);
                titleLabel.setImage(image);
                control.layout();
            }
            titleLabel.setToolTipText(activeEditor.getTitleToolTip());
        }
        titleLabel.setText(text);

        // XXX: Workaround for 1GCGA89: SWT:ALL - CLabel tool tip does not always update properly
        titleLabel.update();
    }

    /**
     * Return true if <code>x</code> is over the label image.
     */
    boolean overImage( int x ) {
        if (titleLabel.getImage() == null) return false;
        return x < titleLabel.getImage().getBounds().width;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
     */
    public void propertyChanged( Object source,
                                 int propId ) {
        if (propId == IEditorPart.PROP_DIRTY) {
            markDirty();
        } else if (propId == IWorkbenchPart.PROP_TITLE) {
            markDirty();
        }
    }

    private void markDirty() {
        if (titleLabel != null && !titleLabel.isDisposed()) {
            Runnable runnable = new Runnable() {

                public void run() {
                    if (titleLabel != null && !titleLabel.isDisposed()) {
                        String title = PluginConstants.EMPTY_STRING;
                        if (activeEditor != null) {
                            title = activeEditor.getTitle();
                            if (activeEditor.isDirty()) {
                                title = DIRTY_CHAR + title;
                            }
                        }
                        titleLabel.setText(title);
                    }
                }
            };
            if (Display.getDefault().getThread() == Thread.currentThread()) {
                Display.getCurrent().syncExec(runnable);
            } else {
                Display.getDefault().asyncExec(runnable);
            }
        }
    }

    /**
     * @return
     */
    public ModelObjectEditorPage getActiveEditor() {
        return activeEditor;
    }

    /**
     * @return
     */
    public MultiPageModelEditor getModelEditor() {
        return modelEditor;
    }

    /**
     * @param page
     */
    public void setActiveEditor( ModelObjectEditorPage page ) {
        activeEditor = page;
    }

    /**
     * Toolbar manager for theis panel's toolbar.
     */
    class PaneToolBarManager extends ToolBarManager {

        public PaneToolBarManager( ToolBar paneToolBar ) {
            super(paneToolBar);
        }

        @Override
        protected void relayout( ToolBar toolBar,
                                 int oldCount,
                                 int newCount ) {
            toolBar.layout();
            Composite parent = toolBar.getParent();
            parent.layout();
            if (parent.getParent() != null) parent.getParent().layout();
        }

    }

    class PaneContribution extends ContributionItem {

        @Override
        public boolean isDynamic() {
            return true;
        }

        @Override
        public void fill( Menu menu,
                          int index ) {
            MenuItem item;

            // add restore item
            item = new MenuItem(menu, SWT.NONE);
            item.setText(RESTORE);
            item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( SelectionEvent e ) {
                    if (isZoomed) doZoom();
                }
            });
            item.setEnabled(isZoomed);

            // add maximize item
            item = new MenuItem(menu, SWT.NONE);
            item.setText(MAXIMIZE);
            item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( SelectionEvent e ) {
                    doZoom();
                }
            });
            item.setEnabled(!isZoomed);

            new MenuItem(menu, SWT.SEPARATOR);

            // add close item
            item = new MenuItem(menu, SWT.NONE);
            item.setText(CLOSE);
            item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected( SelectionEvent e ) {
                    close();
                }
            });
        }
    }
}
