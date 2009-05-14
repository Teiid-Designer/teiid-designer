/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.util.List;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.TextActionHandler;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * TreeViewerRenameAction is an inline tree editor for any TreeViewer containing EObjects. It cannot be obtained directly from the
 * ActionService by class, since each instance of this action must be provided with a TreeViewer.
 */
public class TreeViewerRenameAction extends RenameAction {

    private TreeEditor treeEditor;
    Tree navigatorTree;
    private TreeViewer treeViewer;
    Text textEditor;
    Composite textEditorParent;
    private TextActionHandler textActionHandler;
    private TreeItem[] cachedSelection;

    /** the object whose name is being edited inline */
    EObject currentObject;
    /** if the transaction for this rename should produce a significant undo action */
    boolean isSignificant = false;

    /**
     * Construct an instance of TreeViewerRenameAction.
     */
    public TreeViewerRenameAction() {
        super();
    }

    /**
     * Set the TreeViewer that this Rename action will run on.
     * 
     * @param treeViewer
     * @param labelProvider
     */
    public void setTreeViewer( TreeViewer treeViewer,
                               ILabelProvider labelProvider ) {
        if (treeViewer != null) {
            this.treeViewer = treeViewer;
            navigatorTree = treeViewer.getTree();
            treeEditor = new TreeEditor(navigatorTree);
            // this.labelProvider = labelProvider;

            // this listener is needed since a right-click in the tree when the editor is active does
            // NOT fire a focus event and the editor remains active.
            navigatorTree.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown( MouseEvent theEvent ) {
                    if (theEvent.button == 3) {
                        if (isTextEditorActive()) {
                            saveChangesAndDispose((EObject)getSelectedObject());
                        }
                    }
                }
            });
        }
    }

    /**
     * <p>
     * </p>
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged( final IWorkbenchPart part,
                                  final ISelection selection ) {
        super.selectionChanged(part, selection);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void doRun() {
        if (preRun()) {
            final EObject currentObject = (EObject)getSelectedObject();

            cachedSelection = navigatorTree.getSelection();

            // Defect 23413 - we only need one or the other or both
            if (currentObject != null || cachedSelection != null) {
                Display.getCurrent().asyncExec(new Runnable() {
                    public void run() {
                        if (navigatorTree != null) {
                            isSignificant = true;
                            renameInline(currentObject);
                        } else {
                            // should never happen, so just log it.
                            RuntimeException e = new RuntimeException();
                            UiConstants.Util.log(IStatus.ERROR, e, "TreeViewerRenameAction doRun() called on a null Tree"); //$NON-NLS-1$
                        }
                    }
                });
            }
        }
    }

    /**
     * Overloaded to allow newly created objects to be named initially without generating an Undo. This method must be run
     * programatically, since it does not get called via Action.
     */
    public void doRun( final boolean generateUndo ) {
        preRun();

        final EObject currentObject = (EObject)getSelectedObject();
        cachedSelection = navigatorTree.getSelection();

        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                if (navigatorTree != null) {
                    isSignificant = generateUndo;
                    if (ModelerCore.getModelEditor().hasName(currentObject)) {
                        renameInline(currentObject);
                    }
                } else {
                    // should never happen, log it jsut in case.
                    RuntimeException e = new RuntimeException();
                    UiConstants.Util.log(IStatus.ERROR, e, "TreeViewerRenameAction doRun() called on a null Tree"); //$NON-NLS-1$
                }
            }
        });
    }

    void renameInline( final EObject eObject ) {

        EObject editingEObject = eObject;

        // Defect 22944 - Make sure the renaming object is the current/cached selection
        // Defect 23282 - Added cachedSelection == null or zero length check

        // Defect 23413 - had to tweak one more time because the select was being changed to the Diagram under an xml document
        // And in some cases, the eObject is coming in NULL too.
        // Basically use whichever one is
        if (eObject != null && cachedSelection != null && cachedSelection.length == 1
            && cachedSelection[0].getData() instanceof EObject && eObject == (EObject)cachedSelection[0].getData()) {
            // DO NOTHING, THIS IS OK
        } else if (eObject != null && (cachedSelection == null || cachedSelection.length == 0)) {
            IStructuredSelection selection = new StructuredSelection(eObject);
            treeViewer.setSelection(selection);
            cachedSelection = navigatorTree.getSelection();
        } else if (eObject != null && cachedSelection.length == 1 && (cachedSelection[0].getData() instanceof File)) {
            IStructuredSelection selection = new StructuredSelection(eObject);
            treeViewer.setSelection(selection);
            cachedSelection = navigatorTree.getSelection();
        } else if (cachedSelection != null && cachedSelection[0] != null
                   && (eObject == null || cachedSelection[0].getData() != eObject)) {
            IStructuredSelection selection = new StructuredSelection(cachedSelection[0]);
            editingEObject = (EObject)cachedSelection[0].getData();
            treeViewer.setSelection(selection);
        } else {
            return;
        }

        // Make sure text editor is created only once. Simply reset text
        // editor when action is executed more than once. Fixes bug 22269.
        if (textEditorParent == null) {
            createTextEditor(editingEObject);
        }
        String name = ModelerCore.getModelEditor().getName(editingEObject);
        if (name != null) {
            textEditor.setText(name);
        }

        // Open text editor with initial size.
        textEditorParent.setVisible(true);
        Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        textSize.x += textSize.y; // Add extra space for new characters.
        Point parentSize = textEditorParent.getSize();
        textEditor.setBounds(2, 1, Math.min(textSize.x, parentSize.x - 4), parentSize.y - 2);
        textEditorParent.redraw();
        textEditor.selectAll();
        textEditor.setFocus();
    }

    private void createTextEditor( final EObject eObj ) {
        // Create text editor parent. This draws a nice bounding rect.
        textEditorParent = createEditorParent();
        textEditorParent.setVisible(false);
        textEditorParent.addListener(SWT.Paint, new Listener() {
            public void handleEvent( Event e ) {
                Point textSize = textEditor.getSize();
                Point parentSize = textEditorParent.getSize();
                e.gc.drawRectangle(0, 0, Math.min(textSize.x + 4, parentSize.x - 1), parentSize.y - 1);
            }
        });

        // Create inner text editor.
        textEditor = new Text(textEditorParent, SWT.NONE);
        textEditorParent.setBackground(textEditor.getBackground());
        textEditor.addListener(SWT.Modify, new Listener() {
            public void handleEvent( Event e ) {
                Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                textSize.x += textSize.y; // Add extra space for new characters.
                Point parentSize = textEditorParent.getSize();
                textEditor.setBounds(2, 1, Math.min(textSize.x, parentSize.x - 4), parentSize.y - 2);
                textEditorParent.redraw();
            }
        });
        textEditor.addListener(SWT.Traverse, new Listener() {
            public void handleEvent( Event event ) {

                // Workaround for Bug 20214 due to extra
                // traverse events
                switch (event.detail) {
                    case SWT.TRAVERSE_ESCAPE:
                        // Do nothing in this case
                        disposeTextWidget();
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                    case SWT.TRAVERSE_RETURN:
                        saveChangesAndDispose(eObj);
                        event.doit = true;
                        event.detail = SWT.TRAVERSE_NONE;
                        break;
                }
            }
        });
        textEditor.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost( FocusEvent fe ) {
                saveChangesAndDispose(eObj);
            }
        });

        if (textActionHandler != null) textActionHandler.addText(textEditor);
    }

    Composite createEditorParent() {
        Tree tree = navigatorTree;
        Composite result = new Composite(tree, SWT.NONE);
        // Now let's make sure the target eObject is selected
        TreeItem[] selectedItems = cachedSelection; // tree.getSelection();
        if (selectedItems.length > 0) {
            treeEditor.horizontalAlignment = SWT.LEFT;
            treeEditor.grabHorizontal = true;
            treeEditor.setEditor(result, selectedItems[0]);
        }
        return result;
    }

    /**
     * Save the changes and dispose of the text widget.
     * 
     * @param resource - the resource to move.
     */
    void saveChangesAndDispose( EObject resource ) {
        // Cache the resource to avoid selection loss since a selection of
        // another item can trigger this method
        currentObject = resource;
        final String newName = textEditor.getText();
        // Run this in an async to make sure that the operation that triggered
        // this action is completed. Otherwise this leads to problems when the
        // icon of the item being renamed is clicked (i.e., which causes the rename
        // text widget to lose focus and trigger this method).
        Runnable query = new Runnable() {
            public void run() {
                // Dispose the text widget regardless
                disposeTextWidget();
                // String oldName = labelProvider.getText(currentObject);
                // Need to check only the "Name" here. Not the visible text
                // (i.e. Columns will include datatype info ..... someName: string(25))
                if (currentObject != null) {
                    String oldName = ModelerCore.getModelEditor().getName(currentObject);
                    if (!newName.equals(oldName) && currentObject != null) {
                        String undoLabel = UiConstants.Util.getString("RenameAction.undoLabel", oldName); //$NON-NLS-1$
                        boolean started = ModelerCore.startTxn(isSignificant, undoLabel, this);
                        boolean succeeded = false;
                        try {
                            if (!DatatypeUtilities.renameSqlColumn(currentObject, newName)) {
                                if (newName.length() > 0) {
                                    ModelerCore.getModelEditor().rename(currentObject, newName);
                                }
                            }
                            succeeded = true;
                        } catch (ModelerCoreException e) {
                            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
                        } finally {
                            if (started) {
                                if (succeeded) {
                                    ModelerCore.commitTxn();
                                } else {
                                    ModelerCore.rollbackTxn();
                                }
                            }
                        }
                    }
                }
                currentObject = null;
            }
        };
        navigatorTree.getShell().getDisplay().asyncExec(query);
    }

    /**
     * Indicates if the text editor is currently active and not disposed.
     * 
     * @return <code>true</code>if active; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean isTextEditorActive() {
        return (this.textEditor != null);
    }

    /**
     * Close the text widget and reset the editorText field.
     */
    void disposeTextWidget() {
        if (textActionHandler != null) textActionHandler.removeText(textEditor);

        if (textEditorParent != null) {
            textEditorParent.dispose();
            textEditorParent = null;
            textEditor = null;
            treeEditor.setEditor(null, null);
        }
    }

    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling the run at the last
     * minute. This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        if (requiresEditorForRun()) {
            List allSelectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            if (allSelectedEObjects != null && !allSelectedEObjects.isEmpty()) {
                EObject eObject = (EObject)allSelectedEObjects.get(0);
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);
                if (mr != null) {
                    ModelEditorManager.open(eObject, true, UiConstants.ObjectEditor.REFRESH_EDITOR_IF_OPEN);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
}
