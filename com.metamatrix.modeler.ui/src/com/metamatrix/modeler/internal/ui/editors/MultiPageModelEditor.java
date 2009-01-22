/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Item;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;

/**
 * MultiPageModelEditor is a specialization of EditorPart that contains a TabFolder for multiple editor pages, plus a splitter for
 * showing and hiding panels beneath the editor pages. This class encapsulates the creation of these components from the
 * ModelEditor, which manages the content of the tabs.
 */
public abstract class MultiPageModelEditor extends EditorPart implements IGotoMarker {

    /** The panel for ModelObjectEditors. */
    protected ModelObjectEditorPanel editorContainer;

    /** The tab folder containing ModelEditorPages. */
    CTabFolder tabFolder;

    /** A splitter for the ModelObjectEditorPanel to reside beneath this editor's ViewForm */
    private SashForm splitter;

    /** The container widget for this multi-page editor's tabFolder */
    private ViewForm viewForm;

    /**
     * List of nested editors. Element type: IEditorPart. Need to hang onto them here, in addition to using get/setData on the
     * items, because dispose() needs to access them, but widgetry has already been disposed at that point.
     */
    private List nestedEditors = new ArrayList(3);

    private List allEditors = new ArrayList(3);

    private List objectEditors;
    private HashMap objectEditorMap = new HashMap();
    // ----------------------------
    // Defect 22844 - requires a state variable to allow overriding/ignoring the internalSetFocus() method
    // otherwise, the OperationObjectEditorPage gets focus when it shouldn't
    // ----------------------------
    private boolean ignoreInternalFocus = false;

    /**
     * Creates an empty multi-page editor with no pages.
     */
    protected MultiPageModelEditor() {
        super();
    }

    /**
     * Creates and adds a new page containing the given control to this multi-page editor. The control may be <code>null</code>,
     * allowing it to be created and set later using <code>setControl</code>.
     * 
     * @param control the control, or <code>null</code>
     * @return the index of the new page
     * @see #setControl
     */
    public int addPage( Control control ) {
        CTabItem item = createItem();
        item.setControl(control);
        return getPageCount() - 1;
    }

    /**
     * Creates the pages of this multi-page editor. Subclasses must implement this method.
     */
    abstract protected void createPages();

    /**
     * Determines if this Editor's resource is dirty. Subclasses must implement this method.
     */
    abstract protected boolean isResourceDirty();

    /**
     * Notifies this multi-page editor that the page with the given id has been activated. This method is called when the user
     * selects a different tab.
     * <p>
     * The <code>MultiPageEditorPart</code> implementation of this method sets focus to the new page, and notifies the action bar
     * contributor (if there is one). This checks whether the action bar contributor is an instance of
     * <code>MultiPageEditorActionBarContributor</code>, and, if so, calls <code>setActivePage</code> with the active nested
     * editor. This also fires a selection change event if required.
     * </p>
     * <p>
     * Subclasses must implement this method.
     * </p>
     * 
     * @param newPageIndex the index of the activated page
     */
    abstract protected void pageChange( int newPageIndex );

    /**
     * Callback to notify a subclass that the specified IEditorPart has been lazily loaded into the tab pane for the first time.
     * Allows the subclass to wire up selection and any other necessary initialization features.
     * 
     * @param editor
     */
    abstract protected void initializeEditor( IEditorPart editor );

    /**
     * Creates and adds a new tab page to this multi-page editor for the specified IEditorPart. This method does not call
     * IEditorPart.createPartControl, which allows the lazy creation of components only when the tab is selected.
     * 
     * @param editor the nested editor
     * @param input the input for the nested editor
     * @return the index of the new page
     * @exception PartInitException if a new page could not be created
     * @see #handlePropertyChange the handler for property change events from the nested editor
     */
    public int addPage( final IEditorPart editor,
                        final IEditorInput input ) throws PartInitException {

        final IEditorSite site = createSite(editor);
        // call init first so that if an exception is thrown, we have created no new widgets
        editor.init(site, input);
        // create item for page
        final CTabItem item = createItem();
        Composite parent = new Composite(getTabFolder(), SWT.NONE);
        parent.setLayout(new FillLayout());
        item.setControl(parent);

        // set the editor as data on the item so it can be found by the tab's SelectionListener
        item.setData(editor);

        allEditors.add(editor);

        return getPageCount() - 1;
    }

    /**
     * Initialize the specified IEditorPart and create it's control inside the specified CTabItem. This method allows the
     * ModelEditor to completely initialize a page without knowing it's tab item.
     * 
     * @param editor
     */
    protected void initializePage( IEditorPart editor ) {
        // Find the item and call the other method....
        CTabItem[] items = getTabFolder().getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getData() == editor) {
                initializePage(editor, items[i]);
                break;
            }
        }
    }

    /**
     * Initialize the specified IEditorPart and create it's control inside the specified CTabItem. This method allows the
     * MultiPageModelEditor to lazily load individual IEditorParts as they are selected in the Modeler. This also hooks a property
     * change listener on the nested editor.
     * 
     * @param editor
     * @param item
     */
    protected void initializePage( IEditorPart editor,
                                   CTabItem item ) {
        try {
            Composite parent = (Composite)item.getControl();
            // call init first so that if an exception is thrown, we have created no new widgets
            editor.createPartControl(parent);
            editor.addPropertyListener(new IPropertyListener() {

                public void propertyChanged( Object source,
                                             int propertyId ) {
                    MultiPageModelEditor.this.handlePropertyChange(propertyId);
                }
            });
            // remember the editor, as both data on the item, and in the list of editors (see field comment)
            item.setData(editor);
            nestedEditors.add(editor);
            initializeEditor(editor);
            parent.layout();
        } catch (Exception e) {
            // catch any Exception that occurred initializing a ModelEditorPage so that
            // it can be removed and other pages function normally

            String message = UiConstants.Util.getString("MultiPageModelEditor.pageInitializationErrorMessage", //$NON-NLS-1$
                                                        editor.getClass().getName());
            UiConstants.Util.log(IStatus.ERROR, e, message);

            String title = UiConstants.Util.getString("MultiPageModelEditor.pageInitializationErrorTitle"); //$NON-NLS-1$
            MessageDialog.openError(getSite().getShell(), title, message);
        }

    }

    protected boolean hasInitialized( IEditorPart editor ) {
        return nestedEditors.contains(editor);
    }

    /**
     * Find and load all ModelObjectEditor extensions.
     */
    protected void initializeObjectEditors() {
        // System.out.println("MultiPageModelEditor.initializeObjectEditors()");

        // get the ModelEditorPage extension point from the plugin class
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.ExtensionPoints.ModelObjectEditorPage.ID);
        // get the all extensions to the ModelEditorPage extension point
        IExtension[] extensions = extensionPoint.getExtensions();
        objectEditors = new ArrayList(extensions.length);

        // make executable extensions for every CLASSNAME
        for (int i = extensions.length - 1; i >= 0; --i) {
            IConfigurationElement[] elements = extensions[i].getConfigurationElements();
            Object extension = null;
            for (int j = 0; j < elements.length; ++j) {

                try {

                    extension = elements[j].createExecutableExtension(UiConstants.ExtensionPoints.ModelObjectEditorPage.CLASSNAME);
                    if (extension instanceof ModelObjectEditorPage) {
                        objectEditors.add(extension);
                        String id = elements[j].getAttribute(UiConstants.ExtensionPoints.ModelObjectEditorPage.CLASSNAME);
                        objectEditorMap.put(id, extension);

                        // fire property changes in the ModelObjectEditorPage as if they were coming from
                        // this editor. this will cause the editor framework to ask this editor if it is
                        // dirty.
                        ((ModelObjectEditorPage)extension).addPropertyListener(new IPropertyListener() {

                            public void propertyChanged( Object theSource,
                                                         final int thePropId ) {
                                UiUtil.runInSwtThread(new Runnable() {

                                    public void run() {
                                        changeProperty(thePropId);
                                    }
                                }, true);

                            }
                        });
                    }
                } catch (Exception e) {
                    // catch any Exception that occurred initializing a ModelEditorPage so that
                    // it can be removed and other pages function normally

                    String message = UiConstants.Util.getString("MultiPageModelEditor.pageInitializationErrorMessage", //$NON-NLS-1$
                                                                elements[j].getAttribute(UiConstants.ExtensionPoints.ModelObjectEditorPage.CLASSNAME));
                    UiConstants.Util.log(IStatus.ERROR, e, message);

                    String title = UiConstants.Util.getString("MultiPageModelEditor.pageInitializationErrorTitle"); //$NON-NLS-1$
                    MessageDialog.openError(getSite().getShell(), title, message);

                }
            }
        }

        // Initialize object editors
        for (Iterator iter = this.objectEditors.iterator(); iter.hasNext();) {
            ((ModelObjectEditorPage)iter.next()).initialize(this);
        } // for
    }

    void changeProperty( int thePropId ) {
        firePropertyChange(thePropId);
    }

    /**
     * Determine if any of the ModelObjectEditorPage extensions registered with this class want to edit the specified object
     * 
     * @param object
     * @return true if any extension can edit this object.
     */
    public boolean canEditModelObject( Object object ) {
        if (objectEditors != null) {
            for (Iterator iter = objectEditors.iterator(); iter.hasNext();) {
                ModelObjectEditorPage objectEditor = (ModelObjectEditorPage)iter.next();
                if (objectEditor.canEdit(object, getActiveEditor())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine if any of the ModelObjectEditorPage extensions registered with this class can find an editable object for the
     * input object.
     * 
     * @param object
     * @return true if any extension can edit this object.
     */
    public Object getEditableObject( Object object ) {
        if (objectEditors != null) {
            Object editableObject = null;
            for (Iterator iter = objectEditors.iterator(); iter.hasNext();) {
                ModelObjectEditorPage objectEditor = (ModelObjectEditorPage)iter.next();
                editableObject = objectEditor.getEditableObject(object);
                if (editableObject != null) {
                    return editableObject;
                }
            }
        }
        return null;
    }

    /**
     * Edit the specified object using the ModelObjectEditorPage extensions registered with this class for the specified object
     * 
     * @param object the object to be displayed in the edit panel
     * @param the id of a particular object editor to display; may be null.
     */
    public void editModelObject( Object object,
                                 String editorId ) {
        if (editorId != null) {
            ModelObjectEditorPage objectEditor = (ModelObjectEditorPage)objectEditorMap.get(editorId);
            if (objectEditor != null && objectEditor.canEdit(object, getActiveEditor())) {
                this.editorContainer.activateModelObjectEditor(objectEditor, object);
                return;
            }
        }

        if (objectEditors != null) {
            ModelObjectEditorPage editorToOpen = null;
            ArrayList canOpenList = new ArrayList(objectEditors.size());

            for (Iterator iter = objectEditors.iterator(); iter.hasNext();) {
                ModelObjectEditorPage objectEditor = (ModelObjectEditorPage)iter.next();
                if (objectEditor.canEdit(object, getActiveEditor())) {
                    editorToOpen = objectEditor;
                    canOpenList.add(objectEditor);
                }
            }

            if (canOpenList.size() > 1) {

                // swjTODO: show a list and let the user pick. For now, log an exception and continue.
                RuntimeException ex = new RuntimeException("More than one ModelObjectEditorPage found for " + object.toString()); //$NON-NLS-1$
                UiConstants.Util.log(ex);

                if (editorToOpen != null) {
                    this.editorContainer.activateModelObjectEditor(editorToOpen, object);
                }
            } else {
                if (editorToOpen != null) {
                    this.editorContainer.activateModelObjectEditor(editorToOpen, object);
                }
            }
        }
    }

    /**
     * Obtain the current ModelObjectEditorPage displayed in this editor, or null if no such page is currently open.
     * 
     * @return
     */
    public ModelObjectEditorPage getActiveObjectEditor() {
        return this.editorContainer.getActiveEditor();
    }

    /**
     * Request that this Editor's ModelObjectEditorPanel be closed.
     * 
     * @return true if there is no editor open or if the editor closed successfully. Returns false when the editor has vetoed the
     *         close, perhaps because it is holding state.
     */
    public boolean closeObjectEditor() {
        return this.editorContainer.close();
    }

    /**
     * Creates an empty container. Creates a CTabFolder with no style bits set, and hooks a selection listener which calls
     * <code>pageChange()</code> whenever the selected tab changes.
     * 
     * @return a new container
     */
    private void createContainer( Composite parent ) {
        splitter = new SashForm(parent, SWT.VERTICAL);
        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(gid);

        viewForm = new ViewForm(splitter, SWT.BORDER);
        viewForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        tabFolder = new CTabFolder(viewForm, SWT.BOTTOM);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        // add a SelectionListener to the tab folder that can lazily initialize editors
        tabFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( final SelectionEvent e ) {
                // System.out.println("MultiPageModelEditor.createContainer()$SelectionAdapter.widgetSelected"); //$NON-NLS-1$
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

                    public void run() {
                        CTabItem item = (CTabItem)e.item;
                        IEditorPart editor = (IEditorPart)item.getData();
                        // see if this tab's IEditorPart has been initialized
                        if (!hasInitialized(editor)) {
                            initializePage(editor, item);
                        }
                        int newPageIndex = tabFolder.indexOf(item);
                        pageChange(newPageIndex);
                    }
                });
            }
        });

        viewForm.setContent(tabFolder);

        editorContainer = new ModelObjectEditorPanel(this, splitter, SWT.NULL);
        splitter.setWeights(new int[] {3, 2});
    }

    /**
     * Creates a new CTabItem with no style bits set.
     * 
     * @return the new item
     */
    private CTabItem createItem() {
        CTabItem item = new CTabItem(getTabFolder(), SWT.NONE);
        return item;
    }

    /**
     * The <code>MultiPageEditor</code> implementation of this <code>IWorkbenchPart</code> method creates the control for the
     * multi-page editor by calling <code>createContainer</code>, then <code>createPages</code>. Subclasses should implement
     * <code>createPages</code> rather than overriding this method.
     */
    @Override
    public final void createPartControl( Composite parent ) {
        // System.out.println("MultiPageModelEditor.createPartControl()"); //$NON-NLS-1$
        createContainer(parent);
        createPages();
        // set the active page (page 0 by default), unless it has already been done
        if (getActivePage() == -1) setActivePage(0);

        initializeObjectEditors();

        new DropTarget(parent, DND.DROP_NONE);

    }

    /**
     * Creates the site for the given nested editor. The <code>MultiPageEditorPart</code> implementation of this method creates an
     * instance of <code>MultiPageEditorSite</code>. Subclasses may reimplement to create more specialized sites.
     * 
     * @param editor the nested editor
     * @return the editor site
     */
    protected IEditorSite createSite( IEditorPart editor ) {
        return new ModelEditorSite(this, editor);
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this <code>IWorkbenchPart</code> method disposes all nested editors.
     * Subclasses may extend.
     */
    @Override
    public void dispose() {
        preDisposeEditors();

        if (editorContainer != null) {
            editorContainer.dispose();
        }

        for (int i = 0; i < nestedEditors.size(); ++i) {
            IEditorPart editor = (IEditorPart)nestedEditors.get(i);
            disposePart(editor);
        }
        // Memory leak Defect 22290 - Need to tell the ModelEditorSite to dispose also so we can clean up the PopupMenuExtender
        // menu listeners that are holding on to our IEditorPart objects.
        for (Iterator iter = allEditors.iterator(); iter.hasNext();) {
            IEditorPart editorPart = (IEditorPart)iter.next();
            if (editorPart.getSite() instanceof ModelEditorSite) {
                ((ModelEditorSite)editorPart.getSite()).dispose();
            }
        }

        nestedEditors.clear();
        allEditors.clear();
    }

    /*
     * Private method used to tell any tab'd editors that haven't been initialized to clean up caches, threads etc...
     */
    private void preDisposeEditors() {
        Iterator iter = getAllEditors().iterator();
        IEditorPart editor = null;
        while (iter.hasNext()) {
            editor = (IEditorPart)iter.next();
            // see if this tab's IEditorPart has been initialized
            if (editor instanceof ModelEditorPage) {
                ((ModelEditorPage)editor).preDispose();
            }
        }

    }

    /**
     * Returns the active nested editor if there is one.
     * <p>
     * Subclasses should not override this method
     * </p>
     * 
     * @return the active nested editor, or <code>null</code> if none
     */
    public IEditorPart getActiveEditor() {
        int index = getActivePage();
        if (index != -1) return getEditor(index);
        return null;
    }

    /**
     * Returns the index of the currently active page, or -1 if there is no active page.
     * <p>
     * Subclasses should not override this method
     * </p>
     * 
     * @return the index of the active page, or -1 if there is no active page
     */
    protected int getActivePage() {
        CTabFolder tabFolder = getTabFolder();
        if (tabFolder != null && !tabFolder.isDisposed()) return tabFolder.getSelectionIndex();
        return -1;
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
     * @since 5.5
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IUndoManager.class)) {
            return getUndoManager();
        }

        return super.getAdapter(adapter);
    }

    /**
     * Obtains the current <code>IUndoManager</code> for this editor.
     * 
     * @return the undo manager or <code>null</code>
     * @since 5.5.3
     */
    protected IUndoManager getUndoManager() {
        return null;
    }

    /**
     * Returns the composite control containing this multi-page editor's pages. This should be used as the parent when creating
     * controls for the individual pages. That is, when calling <code>addPage(Control)</code>, the passed control should be a
     * child of this container.
     * <p>
     * Warning: Clients should not assume that the container is any particular subclass of Composite. The actual class used may
     * change in order to improve the look and feel of multi-page editors. Any code making assumptions on the particular subclass
     * would thus be broken.
     * </p>
     * <p>
     * Subclasses should not override this method
     * </p>
     * 
     * @return the composite, or <code>null</code> if <code>createPartControl</code> has not been called yet
     */
    protected Composite getContainer() {
        return splitter;
    }

    /**
     * Returns the control for the given page index, or <code>null</code> if no control has been set for the page. The page index
     * must be valid.
     * <p>
     * Subclasses should not override this method
     * </p>
     * 
     * @param pageIndex the index of the page
     * @return the control for the specified page, or <code>null</code> if none has been set
     */
    protected Control getControl( int pageIndex ) {
        return getItem(pageIndex).getControl();
    }

    /**
     * Returns the editor for the given page index. The page index must be valid.
     * 
     * @param pageIndex the index of the page
     * @return the editor for the specified page, or <code>null</code> if the specified page was not created with
     *         <code>addPage(IEditorPart,IEditorInput)</code>
     */
    protected IEditorPart getEditor( int pageIndex ) {
        Item item = getItem(pageIndex);
        if (item != null) {
            Object data = item.getData();
            if (data instanceof IEditorPart) {
                return (IEditorPart)data;
            }
        }
        return null;
    }

    /**
     * Returns the tab item for the given page index (page index is 0-based). The page index must be valid.
     * 
     * @param pageIndex the index of the page
     * @return the tab item for the given page index
     */
    protected CTabItem getItem( int pageIndex ) {
        return getTabFolder().getItem(pageIndex);
    }

    /**
     * Returns the number of pages in this multi-page editor.
     * 
     * @return the number of pages
     */
    protected int getPageCount() {
        CTabFolder folder = getTabFolder();
        // May not have been created yet, or may have been disposed.
        if (folder != null && !folder.isDisposed()) return folder.getItemCount();
        return 0;
    }

    /**
     * Returns the image for the page with the given index, or <code>null</code> if no image has been set for the page. The page
     * index must be valid.
     * 
     * @param pageIndex the index of the page
     * @return the image, or <code>null</code> if none
     */
    protected Image getPageImage( int pageIndex ) {
        return getItem(pageIndex).getImage();
    }

    /**
     * Returns the text label for the page with the given index. Returns the empty string if no text label has been set for the
     * page. The page index must be valid.
     * 
     * @param pageIndex the index of the page
     * @return the text label for the page
     */
    protected String getPageText( int pageIndex ) {
        return getItem(pageIndex).getText();
    }

    /**
     * Returns the tab folder containing this multi-page editor's pages.
     * 
     * @return the tab folder, or <code>null</code> if <code>createPartControl</code> has not been called yet
     */
    public CTabFolder getTabFolder() {
        return tabFolder;
    }

    /**
     * Handles a property change notification from a nested editor. The default implementation simply forwards the change to
     * listeners on this multi-page editor by calling <code>firePropertyChange</code> with the same property id. For example, if
     * the dirty state of a nested editor changes (property id <code>IEditorPart.PROP_DIRTY</code>), this method handles it by
     * firing a property change event for <code>IEditorPart.PROP_DIRTY</code> to property listeners on this multi-page editor.
     * <p>
     * Subclasses may extend or reimplement this method.
     * </p>
     * 
     * @param propertyId the id of the property that changed
     */
    protected void handlePropertyChange( int propertyId ) {
        firePropertyChange(propertyId);
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this <code>IEditorPart</code> method sets its site to the given
     * site, its input to the given input, and the site's selection provider to a <code>MultiPageSelectionProvider</code>.
     * Subclasses may extend this method.
     */
    @Override
    @SuppressWarnings( "unused" )
    public void init( IEditorSite site,
                      IEditorInput input ) throws PartInitException {
        setSite(site);
        setInput(input);
    }

    /**
     * The <code>MultiPageEditorPart</code> implementation of this <code>IEditorPart</code> method returns whether the contents of
     * any of this multi-page editor's nested editors have changed since the last save. Pages created with
     * <code>addPage(Control)</code> are ignored.
     * <p>
     * Subclasses may extend or reimplement this method.
     * </p>
     */
    @Override
    public boolean isDirty() {
        // use nestedEditors to avoid SWT requests; see bug 12996
        for (Iterator i = nestedEditors.iterator(); i.hasNext();) {
            IEditorPart editor = (IEditorPart)i.next();
            if (editor.isDirty()) {
                return true;
            }
        }

        // if no page editors are dirty see if the model object editor is dirty
        if ((editorContainer != null) && (editorContainer.getActiveEditor() != null)
            && editorContainer.getActiveEditor().isDirty()) {
            return true;
        }

        return isResourceDirty();
    }

    private void disposePart( final IWorkbenchPart part ) {
        SafeRunner.run(new SafeRunnable() {

            public void run() {
                part.dispose();
            }

            @Override
            public void handleException( Throwable e ) {
                // Exception has already being logged by Core. Do nothing.
            }
        });
    }

    /**
     * Removes the page with the given index from this multi-page editor. The controls for the page are disposed of; if the page
     * has an editor, it is disposed of too. The page index must be valid.
     * 
     * @param pageIndex the index of the page
     * @see #addPage
     */
    public void removePage( int pageIndex ) {
        Assert.isTrue(pageIndex >= 0 && pageIndex < getPageCount());
        // get editor (if any) before disposing item
        IEditorPart editor = getEditor(pageIndex);
        // dispose item before disposing editor, in case there's an exception in editor's dispose
        getItem(pageIndex).dispose();
        // dispose editor (if any)
        if (editor != null) {
            nestedEditors.remove(editor);
            disposePart(editor);
        }
    }

    /**
     * Sets the currently active page.
     * 
     * @param pageIndex the index of the page to be activated; the index must be valid
     */
    protected void setActivePage( int pageIndex ) {
        Assert.isTrue(pageIndex >= 0 && pageIndex < getPageCount());
        getTabFolder().setSelection(pageIndex);
    }

    /**
     * Sets the control for the given page index. The page index must be valid.
     * 
     * @param pageIndex the index of the page
     * @param control the control for the specified page, or <code>null</code> to clear the control
     */
    protected void setControl( int pageIndex,
                               Control control ) {
        getItem(pageIndex).setControl(control);
    }

    /**
     * The <code>MultiPageEditor</code> implementation of this <code>IWorkbenchPart</code> method sets focus on the active nested
     * editor, if there is one.
     * <p>
     * Subclasses may extend or reimplement.
     * </p>
     */
    @Override
    public void setFocus() {
        if (getSite().getWorkbenchWindow().getActivePage() == null) {
            getSite().getWorkbenchWindow().addPageListener(new IPageListener() {

                public void pageOpened( IWorkbenchPage page ) {
                }

                public void pageClosed( IWorkbenchPage page ) {
                }

                public void pageActivated( IWorkbenchPage page ) {
                    // System.out.println("MultiPageModelEditor.setFocus()$IPageListener.pageActivated"); //$NON-NLS-1$
                    page.getWorkbenchWindow().removePageListener(this);
                    // ----------------------------
                    // Defect 22844 - isIgnoreInternalFocus() then don't call internalSetFocus() method
                    // This cleans up simple selection causing focus to OperationEditorPage way too often
                    // ----------------------------
                    if (!isIgnoreInternalFocus()) {
                        internalSetFocus();
                    } else {
                        setIgnoreInternalFocus(true);
                    }
                }
            });
        } else {
            // ----------------------------
            // Defect 22844 - isIgnoreInternalFocus() then don't call internalSetFocus() method
            // This cleans up simple selection causing focus to OperationEditorPage way too often
            // ----------------------------
            if (!isIgnoreInternalFocus()) {
                internalSetFocus();
            } else {
                setIgnoreInternalFocus(true);
            }
        }
    }

    public void selectPage( IEditorPart editor ) {
        // Find the item and call the other method....
        CTabItem[] items = getTabFolder().getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getData() == editor) {
                CTabItem item = items[i];
                getTabFolder().setSelection(item);
                Event event = new Event();
                event.widget = getTabFolder();
                event.item = item;
                event.type = SWT.Selection;
                getTabFolder().notifyListeners(SWT.Selection, event);
                break;
            }
        }
    }

    public boolean isPageSelected( ModelEditorPage page ) {
        // Find the item and call the other method....
        CTabItem currentSelectedTab = getTabFolder().getSelection();

        if (currentSelectedTab != null && currentSelectedTab.getData() instanceof ModelEditorPage) {
            return currentSelectedTab.getData() == page;
        }
        return false;
    }

    void internalSetFocus() {
        // Select appropriate editor
        CTabItem[] items = getTabFolder().getItems();
        for (int ndx = items.length; --ndx >= 0;) {
            CTabItem item = items[ndx];
            ModelEditorPage editor = (ModelEditorPage)item.getData();
            if (ndx == 0 || editor.isSelectedFirst(getEditorInput())) {
                getTabFolder().setSelection(item);
                Event event = new Event();
                event.widget = getTabFolder();
                event.item = item;
                event.type = SWT.Selection;
                getTabFolder().notifyListeners(SWT.Selection, event);
                break;
            }
        }

        int index = getActivePage();
        if (index != -1) {
            setFocus(index);
        }
    }

    /**
     * Sets focus to the control for the given page. If the page has an editor, this calls its <code>setFocus()</code> method.
     * Otherwise, this calls <code>setFocus</code> on the control for the page.
     * 
     * @pageIndex the index of the page
     */
    private void setFocus( int pageIndex ) {
        if (pageIndex < 0 || pageIndex >= getPageCount()) return;
        IEditorPart editor = getEditor(pageIndex);
        if (editor != null) {
            // if( editor instanceof ModelEditorPage ) {
            // ((ModelEditorPage)editor).setInitialFocus();
            // } else {
            // editor.setFocus();
            // }
        } else {
            Control control = getControl(pageIndex);
            if (control != null) {
                control.setFocus();
            }
        }
    }

    /**
     * Sets the image for the page with the given index, or <code>null</code> to clear the image for the page. The page index must
     * be valid.
     * 
     * @param pageIndex the index of the page
     * @param image the image, or <code>null</code>
     */
    protected void setPageImage( int pageIndex,
                                 Image image ) {
        getItem(pageIndex).setImage(image);
    }

    /**
     * Sets the text label for the page with the given index. The page index must be valid. The text label must not be null.
     * 
     * @param pageIndex the index of the page
     * @param text the text label
     */
    protected void setPageText( int pageIndex,
                                String text ) {
        getItem(pageIndex).setText(text);
    }

    /**
     * Sets the text label for the page with the given index. The page index must be valid. The text label must not be null.
     * 
     * @param pageIndex the index of the page
     * @param text the text for the tooltip
     */
    protected void setPageToolTipText( int index,
                                       String text ) {
        getTabFolder().getItem(index).setToolTipText(text);
    }

    /**
     * Expands or restores the ObjectEditor panel from the bottom of the splitter to the entire width & height of the editor site.
     * 
     * @param hideTabs
     */
    void zoomObjectEditor( boolean hideTabs ) {
        if (tabFolder != null && !tabFolder.isDisposed()) {

            if (hideTabs) {
                tabFolder.getParent().setVisible(false);
            } else {
                tabFolder.getParent().setVisible(true);
            }
            splitter.layout();

        }
    }

    /**
     * Called by ModelEditor to allow this object to notify any active ModelObjectEditor that it needs to save it's state.
     */
    final protected void preSave( boolean isClosing ) {
        this.editorContainer.saveEditorState(isClosing);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker( IMarker marker ) {

        // until there's a better way, just send the marker to all open ModelEditorPages
        for (Iterator iter = this.nestedEditors.iterator(); iter.hasNext();) {
            ModelEditorPage editor = (ModelEditorPage)iter.next();
            IDE.gotoMarker(editor, marker);
        }
    }

    /**
     * @return Returns the allEditors.
     * @since 4.2
     */
    public List getAllEditors() {
        return this.allEditors;
    }

    /**
     * @return Returns the nestedEditors.
     * @since 4.2
     */
    public List getNestedEditors() {
        return new ArrayList(this.nestedEditors);
    }

    public List getObjectEditors() {
        return this.objectEditors;
    }

    /**
     * Method provides individual editors and ModelEditor a way to override the internalSetFocus() call inside this class. See
     * Defect 22844
     * 
     * @return ignoreInternalFocus
     * @since 5.0.2
     */
    public boolean isIgnoreInternalFocus() {
        return this.ignoreInternalFocus;
    }

    /**
     * Method provides individual editors and ModelEditor a way to override the internalSetFocus() call inside this class. See
     * Defect 22844
     * 
     * @param theIgnoreInternalFocus
     * @since 5.0.2
     */
    public void setIgnoreInternalFocus( boolean theIgnoreInternalFocus ) {
        this.ignoreInternalFocus = theIgnoreInternalFocus;
    }

    public void updateReadOnlyState() {
        if (this.objectEditors != null) {
            for (Iterator iter = this.objectEditors.iterator(); iter.hasNext();) {
                ((ModelObjectEditorPage)iter.next()).updateReadOnlyState();
            }
        }
    }
}
