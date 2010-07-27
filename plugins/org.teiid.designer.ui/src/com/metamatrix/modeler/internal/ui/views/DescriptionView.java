/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.views;

import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.eclipse.ui.views.properties.PropertySheet;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.Annotation;
import com.metamatrix.metamodels.core.AnnotationContainer;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertyDescriptor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.product.IProductCharacteristics;
import com.metamatrix.ui.text.StyledTextEditor;

public class DescriptionView extends ModelerView
    implements ISelectionListener, FocusListener, INotifyChangedListener, EventObjectListener, KeyListener, IPartListener,
    IUndoManager, ITextEditorExtension2 {

    /**
     * The object whose description is being shown. Will either be an {@link EObject}, {@link ModelResource}, or <code>null</code>
     * .
     */
    private Object currentObject;

    private ModelResource currentModel;

    /**
     * Provides text widget with cut, copy, paste, select all, undo, and redo context menu and accelerator key support
     * 
     * @since 5.5.3
     */
    private StyledTextEditor textEditor;

    private void addListeners() {
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        window.getPartService().addPartListener(this);
        window.getSelectionService().addSelectionListener(this);
        ModelUtilities.addNotifyChangedListener(this);

        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, this);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    private void asyncClear( final boolean enabled ) {
        Display.getDefault().asyncExec(new Runnable() {

            public void run() {
                clear(enabled);
            }
        });
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#canRedo()
     * @since 5.5
     */
    public boolean canRedo() {
        return this.textEditor.getUndoManager().redoable();
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#canUndo()
     * @since 5.5
     */
    public boolean canUndo() {
        return this.textEditor.getUndoManager().undoable();
    }

    void clear( boolean enabled ) {
        if (!this.textEditor.isDisposed()) {
            setText(CoreStringUtil.Constants.EMPTY_STRING);
            this.textEditor.setEditable(enabled);
            this.textEditor.resetUndoRedoHistory();
            this.textEditor.getTextWidget().traverse(SWT.TRAVERSE_PAGE_PREVIOUS);
            setBackgroundColor(enabled);
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPartControl( Composite parent ) {
        super.createPartControl(parent);
        createTextEditor(parent);
        addListeners();
    }

    /**
     * @param parent the parent of the text widget
     * @since 5.5.3
     */
    private void createTextEditor( Composite parent ) {
        this.textEditor = new StyledTextEditor(parent, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
        this.textEditor.getTextWidget().addFocusListener(this);
        this.textEditor.getTextWidget().addKeyListener(this);
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.views.ModelerView#dispose()
     * @since 5.5
     */
    @Override
    public void dispose() {
        IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
        workbenchWindow.getSelectionService().removeSelectionListener(this);
        // defect 17949 - remove as part listener when disposed.
        workbenchWindow.getPartService().removePartListener(this);
        ModelUtilities.removeNotifyChangedListener(this);

        try {
            UiPlugin.getDefault().getEventBroker().removeListener(ModelResourceEvent.class, this);
        } catch (EventSourceException e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        this.textEditor.dispose();

        super.dispose();
    }

    /**
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     * @since 5.5
     */
    public void focusGained( FocusEvent e ) {
        // refresh();
    }

    /**
     * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
     * @since 5.5
     */
    public void focusLost( FocusEvent e ) {
        saveChangedObjectDescription();
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.views.ModelerView#getAdapter(java.lang.Class)
     * @since 5.5
     */
    @Override
    public Object getAdapter( Class key ) {
        if (key.equals(IFindReplaceTarget.class) && this.textEditor.getTextWidget().isFocusControl()) {
            return this.textEditor.getTextViewer().getFindReplaceTarget();
        }

        return super.getAdapter(key);
    }

    /**
     * @return the currently selected {@link EObject}'s or {@link ModelResource}'s description
     * @since 5.5
     */
    String getCurrentObjectDescription() {
        if (this.currentObject instanceof EObject) {
            return ModelObjectUtilities.getDescription((EObject)this.currentObject);
        } else if (this.currentObject instanceof ModelResource) {
            return ModelUtilities.getModelDescription(this.currentModel);
        } else {
            // jh Defect 22197 - support selection of a VDB
            if (getProductCharacteristics().getObjectInfo(IProductCharacteristics.DESCRIPTION, this.currentObject) != null) {
                return (String)getProductCharacteristics().getObjectInfo(IProductCharacteristics.DESCRIPTION, this.currentObject);
            }
        }

        return null;
    }

    private IProductCharacteristics getProductCharacteristics() {
        return ProductCustomizerMgr.getInstance().getProductCharacteristics();
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getRedoLabel()
     * @since 5.5.3
     */
    public String getRedoLabel() {
        return UiConstants.Util.getString(I18nUtil.getPropertyPrefix(DescriptionView.class) + "redoLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getUndoLabel()
     * @since 5.5.3
     */
    public String getUndoLabel() {
        return UiConstants.Util.getString(I18nUtil.getPropertyPrefix(DescriptionView.class) + "undoLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.views.ModelerView#getUndoManager()
     * @since 5.5.3
     */
    @Override
    protected IUndoManager getUndoManager() {
        return this;
    }

    /**
     * @param obj1 the first object being compared
     * @param obj2 the second object being compared
     * @return <code>true</code> if the objects are equal and both not <code>null</code>
     * @since 5.5.3
     */
    private boolean haveSameState( Object obj1,
                                   Object obj2 ) {
        if (obj1 == obj2) {
            return ((obj1 != null) && (obj2 != null));
        }

        if (obj1 == null) {
            return (obj2 == null);
        }

        if (obj2 == null) {
            return false;
        }

        return obj1.equals(obj2);
    }

    /**
     * @return <code>true</code> if the model editor is open for the current object
     * @since 5.5.3
     */
    private boolean isEditorOpen() {
        if ((this.currentObject != null) && (this.currentModel != null)) {
            IFile modelFile = (IFile)this.currentModel.getResource();
            return (ModelEditorManager.isOpen(modelFile));
        }

        return false;
    }

    /**
     * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
     * @since 5.5
     */
    public void keyPressed( KeyEvent e ) {
        if ((e.character != 0) && (e.character != SWT.ESC)) {
            // a 'real' key has been pressed; do something:
            if (!this.textEditor.isEditable() && (this.currentObject != null)) {
                // not editible, see if it should be:
                openEditorIfNeeded();
            } // endif
        } // endif
    }

    /**
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     * @since 5.5
     */
    public void keyReleased( KeyEvent e ) {
        // do nothing
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     * @since 5.5
     */
    public void notifyChanged( Notification notification ) {
        boolean descriptionChanged = false;

        // if the target of the notification is this object's annotation, refresh the display
        if (notification instanceof SourcedNotification) {
            Collection nList = ((SourcedNotification)notification).getNotifications();
            Notification nextNotification = null;
            EObject eObj = null;

            for (Iterator iter = nList.iterator(); iter.hasNext();) {
                nextNotification = (Notification)iter.next();
                eObj = NotificationUtilities.getEObject(nextNotification);

                if (eObj == null) {
                    Resource resource = NotificationUtilities.getResource(nextNotification);

                    if ((resource != null) && (this.currentModel != null)) {
                        try {
                            if (this.currentModel.getEmfResource().equals(resource)) {
                                descriptionChanged = true;
                                break;
                            }
                        } catch (ModelWorkspaceException e) {
                            UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
                        }
                    }
                } else if (eObj instanceof Annotation) {
                    EObject target = ((Annotation)eObj).getAnnotatedObject();

                    if ((target != null) && target.equals(this.currentObject)) {
                        descriptionChanged = true;
                        break;
                    }
                } else if (eObj instanceof AnnotationContainer) {
                    if (NotificationUtilities.isAdded(nextNotification)) {
                        EObject[] newChildren = NotificationUtilities.getAddedChildren(nextNotification);

                        for (int iChild = 0; iChild < newChildren.length; iChild++) {
                            if (newChildren[iChild] instanceof Annotation) {
                                EObject target = ((Annotation)newChildren[iChild]).getAnnotatedObject();

                                if ((target != null) && target.equals(this.currentObject)) {
                                    descriptionChanged = true;
                                    break;
                                }
                            }
                        }
                    } else if (NotificationUtilities.isRemoved(nextNotification)) {
                        EObject[] oldChildren = NotificationUtilities.getRemovedChildren(nextNotification);

                        for (int iChild = 0; iChild < oldChildren.length; iChild++) {
                            if (oldChildren[iChild] instanceof Annotation) {
                                EObject target = ((Annotation)oldChildren[iChild]).getAnnotatedObject();

                                if (target != null && target.equals(this.currentObject)) {
                                    descriptionChanged = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (descriptionChanged) {
                    break;
                }
            }
        } else {
            EObject target = NotificationUtilities.getEObject(notification);
            if (target instanceof Annotation) {
                target = ((Annotation)target).getAnnotatedObject();

                if ((target != null) && target.equals(this.currentObject)) {
                    descriptionChanged = true;
                }
            }
        }

        if (descriptionChanged) {
            refresh();
        }
    }

    /**
     * Should only be called if current object and model are not <code>null</code>.
     * 
     * @since 5.5.3
     */
    private void openEditorIfNeeded() {
        // we only need to worry about the readonly status if the file is not currently open,
        // and its underlying IResource is not read only
        if (!isEditorOpen() && !this.currentModel.getResource().getResourceAttributes().isReadOnly()) {
            final IFile modelFile = (IFile)this.currentModel.getResource();
            Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();

            // may want to change these text strings eventually:
            if (MessageDialog.openQuestion(shell,
                                           ModelObjectPropertyDescriptor.OPEN_EDITOR_TITLE,
                                           ModelObjectPropertyDescriptor.OPEN_EDITOR_MESSAGE)) {
                // load and activate, not async (to prevent multiple dialogs from coming up):
                // Changed to use method that insures Object editor mode is on
                ModelEditorManager.openInEditMode(modelFile, true, UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);

                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        setFocus();
                    }
                });
            }
        }
    }

    /**
     * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
     * @since 5.5
     */
    public void partActivated( IWorkbenchPart part ) {
    }

    /**
     * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
     * @since 5.5
     */
    public void partBroughtToTop( IWorkbenchPart part ) {
    }

    /**
     * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
     * @since 5.5
     */
    public void partClosed( IWorkbenchPart part ) {
        if ((part instanceof ModelEditor) && !this.textEditor.isDisposed()) {
            saveChangedObjectDescription();

            // if the current object's editor just closed null clear state
            if (!isEditorOpen()) {
                this.currentObject = null;
                this.currentModel = null;
            }

            // every time a ModelEditor closes, refresh state:
            UiUtil.runInSwtThread(new Runnable() {

                public void run() {
                    refresh();
                }
            }, true);
        }
    }

    /**
     * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
     * @since 5.5
     */
    public void partDeactivated( IWorkbenchPart part ) {
    }

    /**
     * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
     * @since 5.5
     */
    public void partOpened( IWorkbenchPart part ) {
    }

    /**
     * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
     * @since 4.2
     */
    public void processEvent( EventObject obj ) {
        ModelResourceEvent event = (ModelResourceEvent)obj;

        if (event.getType() == ModelResourceEvent.RELOADED) {
            asyncClear(false);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#redo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5.3
     */
    public void redo( IProgressMonitor monitor ) {
        this.textEditor.getUndoManager().redo();

        // since the redo action on the edit menu was run we need to save changes here since this view lost focus
        saveChangedObjectDescription();

        // work done
        monitor.done();
    }

    /**
     * Refreshes the enabled state and content of the GUI controls.
     * 
     * @since 5.5.3
     */
    void refresh() {
        Runnable work = null;

        if (this.currentObject == null) {
            work = new Runnable() {

                public void run() {
                    clear(false);
                }
            };
        } else {
            work = new Runnable() {

                public void run() {
                    setText(getCurrentObjectDescription());
                    updateReadOnlyState();
                }
            };
        }

        UiUtil.runInSwtThread(work, true);
    }

    /**
     * Saves the text editor content to the {@link EObject}'s description if necessary.
     * 
     * @since 5.5
     */
    private void saveChangedObjectDescription() {
        // nothing to save if no current object
        if (this.currentObject == null) {
            return;
        }

        String currentDescription = this.textEditor.getText();
        String savedDescription = null;

        if (this.currentObject instanceof EObject) {
            savedDescription = ModelObjectUtilities.getDescription((EObject)this.currentObject);

            if (!haveSameState(currentDescription, savedDescription)) {
                ModelObjectUtilities.setDescription((EObject)this.currentObject, currentDescription, this);
            }
        } else if (this.currentObject instanceof ModelResource) {
            savedDescription = ModelUtilities.getModelDescription((ModelResource)this.currentObject);

            if (!haveSameState(currentDescription, savedDescription)) {
                ModelUtilities.setModelDescription((ModelResource)this.currentObject, currentDescription);
            }
        } else if (this.currentObject != null) {
            // jh Defect 22197 - support selection of a VDB
            getProductCharacteristics().setObjectInfo(IProductCharacteristics.DESCRIPTION, this.currentObject, currentDescription);
        }
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 5.5
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        if ((part != this) && !(part instanceof PropertySheet)) {
            setCurrentObject(SelectionUtilities.getSelectedObject(selection));
        }
    }

    private void setBackgroundColor( boolean readOnly ) {
        if (!this.textEditor.isDisposed()) {
            // default takes care of having no selection
            int colorCode = SWT.COLOR_WIDGET_BACKGROUND;

            if (currentModel != null) {
                // change color to match enabled or disabled color.
                // we need to be more precise than ReadOnly here, since something
                // could technically be read-only but not open in an editor
                if (!ModelUtilities.isReadOnly(currentModel)) {
                    // writable, always show enabled:
                    colorCode = SWT.COLOR_WHITE;
                } else if (!currentModel.isReadOnly()) {
                    // writeable, but must not be open in editor; show enabled:
                    colorCode = SWT.COLOR_WHITE;
                } else {
                    // currentMR must be read-only
                    colorCode = SWT.COLOR_WIDGET_BACKGROUND;
                } // endif
            } else if (currentObject != null) {
                Boolean bIsReadOnly = (Boolean)getProductCharacteristics().getObjectInfo(IProductCharacteristics.IS_READONLY,
                                                                                         currentObject);

                if (bIsReadOnly != null) {
                    // since we default to readOnly, only need to mod color if NOT readOnly
                    if (!bIsReadOnly.booleanValue()) {
                        colorCode = SWT.COLOR_WHITE;
                    }
                }
            }

            // apply the color if needed
            Color newColor = UiUtil.getSystemColor(colorCode);

            if (!this.textEditor.getTextWidget().getBackground().equals(newColor)) {
                this.textEditor.getTextWidget().setBackground(newColor);
            }
        } // endif

    }

    private void setCurrentObject( Object object ) {
        // before setting new current object save current
        saveChangedObjectDescription();

        Object savedObject = this.currentObject;
        this.currentObject = null;
        this.currentModel = null;

        try {
            if (object instanceof EObject) {
                EObject eObj = (EObject)object;

                if (ModelObjectUtilities.supportsDescription(eObj)) {
                    this.currentObject = object;
                    this.currentModel = ModelUtilities.getModelResourceForModelObject(eObj);
                }
            } else if ((object instanceof IFile) && ModelUtilities.supportsModelDescription((IResource)object)) {
                this.currentModel = ModelUtil.getModelResource((IFile)object, false);
                this.currentObject = this.currentModel;
            } else {
                // jh Defect 22197 - support selection of a VDB
                if (getProductCharacteristics().getObjectInfo(IProductCharacteristics.DESCRIPTION, object) != null) {
                    this.currentObject = object;
                }
            }
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getClass().getName());
        }

        refresh();

        // reset history only if a different object is selected so that the text set during the refresh can't be undone
        if (!haveSameState(savedObject, this.currentObject)) {
            this.textEditor.resetUndoRedoHistory();
        }
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 5.5
     */
    @Override
    public void setFocus() {
        if (!this.textEditor.isDisposed()) {
            this.textEditor.setFocus();
        }
    }

    void setText( String newDescription ) {
        if (!this.textEditor.isDisposed()) {
            String newText = (newDescription == null ? CoreStringUtil.Constants.EMPTY_STRING : newDescription);

            // change the text if necessary
            if (!newText.equals(this.textEditor.getText())) {
                this.textEditor.setText(newText);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#undo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5.3
     */
    public void undo( IProgressMonitor monitor ) {
        this.textEditor.getUndoManager().undo();

        // since the undo action on the edit menu was run we need to save changes here since this view lost focus
        saveChangedObjectDescription();

        // work done
        monitor.done();
    }

    void updateReadOnlyState() {
        if (!this.textEditor.isDisposed()) {
            boolean readOnly = true;

            if (this.currentModel != null) {
                readOnly = ModelUtilities.isReadOnly(this.currentModel);
            } else if (this.currentObject != null) {
                // do prod characteristices check
                Boolean bIsReadOnly = (Boolean)getProductCharacteristics().getObjectInfo(IProductCharacteristics.IS_READONLY,
                                                                                         this.currentObject);

                if (bIsReadOnly != null) {
                    // since we default to readOnly, only need to mod color if NOT readOnly
                    readOnly = bIsReadOnly.booleanValue();
                }
            }

            this.textEditor.setEditable(!readOnly);
            setBackgroundColor(readOnly);
        }
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#isEditorInputModifiable()
     * @since 5.5.3
     */
    public boolean isEditorInputModifiable() {
        return this.textEditor.isEditable();
    }

    /**
     * @see org.eclipse.ui.texteditor.ITextEditorExtension2#validateEditorInputState()
     * @since 5.5.3
     */
    public boolean validateEditorInputState() {
        return false;
    }
}
