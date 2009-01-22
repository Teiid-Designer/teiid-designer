/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.editor;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.EditorPart;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.INavigationSupported;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelEditorPageOutline;
import com.metamatrix.modeler.webservice.WebServicePlugin;
import com.metamatrix.modeler.webservice.ui.util.WebServiceUiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * This page is really just a "dummy" page that always displays the web services procedure editor.
 * 
 * @since 5.0.1
 */
public final class OperationEditorPage extends EditorPart implements ModelEditorPage, INavigationSupported {

    public static final String PACKAGE_DIAGRAM_TYPE_ID = "packageDiagramType"; //$NON-NLS-1$

    private ModelResource resrc;
    private IWorkbenchWindow wdw;
    private List workspaceSelectionListeners = new ArrayList();
    private Object editingObj;
    private Control ctrl, selectedCtrl;
    private OperationEditorSelectionHandler selectionHandler;
    private OperationEditorNotifyChangedListener notifyChangedListener;
    private ModelEditor meParentEditor;
    private boolean editorOpened = false;

    public void addWorkspaceSelectionListener( ISelectionListener listener ) {
        this.workspaceSelectionListeners.add(listener);
    }

    /**
     * @return <code>true</code> if the file being edited is a web services model.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canDisplay(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    public boolean canDisplay( IEditorInput input ) {
        // System.out.println("OperationEditorPage.canDisplay()"); //$NON-NLS-1$
        if (input instanceof IFileEditorInput) {
            IFileEditorInput fileInput = (IFileEditorInput)input;
            try {
                this.resrc = ModelUtilities.getModelResourceForIFile(fileInput.getFile(), false);
                // Set default editing object
                if (this.resrc.getPrimaryMetamodelDescriptor() != null
                    && WebServicePackage.eNS_URI.equals(this.resrc.getPrimaryMetamodelDescriptor().getNamespaceURI())) {
                    return true;
                }
            } catch (ModelWorkspaceException err) {
                WebServicePlugin.Util.log(err);
            }
        }
        return false;
    }

    public IResource getEditorResource() {
        if (this.getEditorInput() instanceof IFileEditorInput) {
            IFileEditorInput fileInput = (IFileEditorInput)this.getEditorInput();
            if (fileInput != null) {
                return fileInput.getFile();
            }
        }
        return null;
    }

    /**
     * @return <code>true</code>.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#canOpenContext(java.lang.Object)
     * @since 5.0.1
     */
    public boolean canOpenContext( Object input ) {
        if (input instanceof Diagram /*&& ((Diagram)input).getType().equals(PACKAGE_DIAGRAM_TYPE_ID)*/) {
            return false;
        }
        return true;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 5.0.1
     */
    @Override
    public void createPartControl( Composite parent ) {
        // System.out.println("OperationEditorPage.createPartControl()"); //$NON-NLS-1$
        // Set page's control for use later
        this.selectedCtrl = this.ctrl = parent;
        // Add listener to update UI appropriately as tabs are selected
        ((CTabFolder)parent.getParent()).addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent event ) {
                updateUi(((CTabItem)event.item).getControl());
            }
        });
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.0.1
     */
    @Override
    public void doSave( IProgressMonitor monitor ) {
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 5.0.1
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * @return <code>null</code>.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getControl()
     * @since 5.0.1
     */
    public Control getControl() {
        return this.ctrl;
    }

    /**
     * @return <code>null</code>.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getActionBarContributor()
     * @since 5.0.1
     */
    public AbstractModelEditorPageActionBarContributor getActionBarContributor() {
        return null;
    }

    /**
     * @return <code>ISelectionChangedListener</code>.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionChangedListener()
     * @since 5.0.1
     */
    public ISelectionChangedListener getModelObjectSelectionChangedListener() {
        return getSelectionHandler();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getModelObjectSelectionProvider()
     * @since 5.0.1
     */
    public ISelectionProvider getModelObjectSelectionProvider() {
        return getSelectionHandler();
    }

    /**
     * @return <code>null</code>.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getNotifyChangedListener()
     * @since 5.0.1
     */
    public INotifyChangedListener getNotifyChangedListener() {
        // Lazily create the selection handler
        if (this.notifyChangedListener == null) {
            this.notifyChangedListener = new OperationEditorNotifyChangedListener();
        }
        return this.notifyChangedListener;
    }

    /**
     * @return <code>null</code>.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#getOutlineContribution()
     * @since 5.0.1
     */
    public ModelEditorPageOutline getOutlineContribution() {
        return null;
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    @Override
    public void init( IEditorSite site,
                      IEditorInput input ) {
        // System.out.println("OperationEditorPage.init()"); //$NON-NLS-1$
        setSite(site);
        setInput(input);
        this.wdw = site.getWorkbenchWindow();
    }

    /**
     * @return <code>false</code>.
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     * @since 5.0.1
     */
    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * @return <code>true</code>.
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#isSelectedFirst(org.eclipse.ui.IEditorInput)
     * @since 5.0.1
     */
    public boolean isSelectedFirst( IEditorInput input ) {
        // Can always return true since canDisplay only returns true when editing a WS model.
        return true;
    }

    /**
     * @return <code>false</code>.
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 5.0.1
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openComplete()
     * @since 5.0.1
     */
    public void openComplete() {
        // System.out.println("OperationEditorPage.openComplete()"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#initializeEditorPage()
     * @since 5.0.2
     */
    public void initializeEditorPage() {
        setEditingObject(null);
    }

    private void setEditingObject( Object someInput ) {
        if (someInput == null) {
            IEditorInput input = getEditorInput();
            if (input instanceof IFileEditorInput) {
                Object obj;
                try {
                    obj = ModelUtilities.getModelResourceForIFile(((IFileEditorInput)input).getFile(), false).getModelAnnotation();
                    if (obj != null) {
                        this.editingObj = WebServiceUiUtil.getOperation(obj);
                        // Since we couldn't find an operation, try finding an Interface.
                        if (this.editingObj == null) {
                            this.editingObj = WebServiceUiUtil.getFirstInterface(obj);
                        }
                    }
                } catch (ModelWorkspaceException err) {
                    WidgetUtil.showError(err);
                    UiConstants.Util.log(err);
                }
            }
        } else {
            this.editingObj = WebServiceUiUtil.getOperation(someInput);
            // Since we couldn't find an operation, try finding an Interface.
            if (this.editingObj == null) {
                this.editingObj = WebServiceUiUtil.getFirstInterface(someInput);
            }
        }
    }

    /**
     * Delegates to {@link #openContext(Object, boolean) openContext(input, false)}.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#openContext(java.lang.Object)
     * @since 5.0.1
     */
    public void openContext( Object input ) {
        openContext(input, false);
    }

    public void openContext( Object input,
                             boolean forceRefresh ) {

        // System.out.println("OperationEditorPage.internalOpenContext(" + input + ")"); //$NON-NLS-1$ //$NON-NLS-2$

        autoSelect();

        setEditingObject(input);

        openObjectEditor();
    }

    private void openObjectEditor() {
        if (this.editingObj == null) {
            setEditingObject(null);
        }
        editorOpened = true;
        // System.out.println("OperationEditorPage.openObjectEditor()"); //$NON-NLS-1$
        // Make object editor appear as if it is the content of the editor page
        int hgt = 0;
        Control editorCtrl = getControl();
        for (Composite parent = getControl().getParent(); parent != null; parent = parent.getParent()) {
            Rectangle rect = parent.computeTrim(0, 0, 0, 0);
            hgt += rect.y + rect.height; // rect.y will be negative; add this to remove top margin
            // Find splitter that separates editor pages from the object editor
            if (parent instanceof SashForm) {
                SashForm splitter = (SashForm)parent;
                // Turn off painting while manipulating the UI
                splitter.setRedraw(false);
                try {
                    // Open object editor
                    ModelEditorManager.getModelEditorForFile((IFile)getEditorResource(), false).editModelObject(this.editingObj,
                                                                                                                null);
                    // Flip-flop controls so main editor tabs stay at bottom of editor
                    Control[] controls = splitter.getChildren();
                    Control objEditorCtrl = (controls[0] == editorCtrl ? controls[1] : controls[0]);
                    if (controls[0] == editorCtrl) {
                        editorCtrl.moveBelow(objEditorCtrl);
                    }
                    // Make sure object editor is visible (if not, the splitter will also not have a sash child)
                    if (!objEditorCtrl.isVisible()) {
                        objEditorCtrl.setVisible(true);
                        splitter.layout();
                        controls = splitter.getChildren();
                    }
                    // Change control weights so that editor area is "minimized" to only show tabs
                    splitter.setWeights(new int[] {editorCtrl.getSize().y + objEditorCtrl.getSize().y - hgt, hgt}); // new
                    // Make sash invisible so user can't move it
                    if (controls.length > 2) {
                        controls[2].setVisible(false);
                    }
                } finally {
                    // Turn painting back on
                    splitter.setRedraw(true);
                }
                break;
            }
            editorCtrl = parent;
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#preDispose()
     * @since 5.0.1
     */
    public void preDispose() {
        if (this.wdw != null) {
            for (Iterator iter = this.workspaceSelectionListeners.iterator(); iter.hasNext();) {
                ISelectionListener listener = (ISelectionListener)iter.next();
                this.wdw.getSelectionService().removeSelectionListener(listener);
                iter.remove();
            } // for
        }
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 5.0.1
     */
    public void processEvent( EventObject event ) {
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 5.0.1
     */
    @Override
    public void setFocus() {
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setLabelProvider(org.eclipse.jface.viewers.ILabelProvider)
     * @since 5.0.1
     */
    public void setLabelProvider( ILabelProvider provider ) {
    }

    /**
     * Delegates to {@link EditorPart#setPartName(String)}.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#setTitleText(java.lang.String)
     * @since 5.0.1
     */
    public void setTitleText( String title ) {
        setPartName(title);
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPage#updateReadOnlyState(boolean)
     * @since 5.0.1
     */
    public void updateReadOnlyState( boolean isReadOnly ) {
    }

    void updateUi( Control control ) {
        // System.out.println("OperationEditorPage.updateUi()"); //$NON-NLS-1$
        if (control == this.selectedCtrl) {
            if (!editorOpened) {
                openObjectEditor();
            }
            return;
        }
        if (control == this.ctrl) {
            openObjectEditor();
        } else if (this.selectedCtrl == this.ctrl) {
            // Restore order of controls to what they were before opening the object editor.
            Control ctrlAncestor = getControl();
            for (Composite parent = getControl().getParent(); parent != null; parent = parent.getParent()) {
                if (parent instanceof SashForm) {
                    // Turn off painting while manipulating the UI
                    parent.setRedraw(false);
                    try {
                        // Restore original control order
                        Control[] controls = parent.getChildren();
                        if (controls[1] == ctrlAncestor) {
                            controls[0].moveBelow(ctrlAncestor);
                        }
                        // Restore sash visibility
                        if (controls.length > 2) {
                            controls[2].setVisible(true);
                        }
                        ModelEditorManager.closeObjectEditor();
                    } finally {
                        // Turn painting back on
                        parent.setRedraw(true);
                    }
                    break;
                }
                ctrlAncestor = parent;
            }
        }
        this.selectedCtrl = control;
    }

    public OperationEditorSelectionHandler getSelectionHandler() {
        // Lazily create the selection handler
        if (this.selectionHandler == null) {
            this.selectionHandler = new OperationEditorSelectionHandler();
        }
        return this.selectionHandler;
    }

    private void autoSelect() {
        ModelEditorManager.autoSelectEditor(meParentEditor, this);
    }

    public IMarker createMarker() {
        return null;
    }

    public void setParent( ModelEditor theMeParent ) {
        this.meParentEditor = theMeParent;
    }
}
