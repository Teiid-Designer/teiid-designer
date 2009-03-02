/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.editors;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.EventListenerList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.XQueryTransformation;
import com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.util.TransformationUiResourceHelper;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * @since 5.0.1
 */
public class XQueryObjectEditorPage implements ModelObjectEditorPage, UiConstants, IAdaptable, ITextEditorExtension2 {

    private static final String THIS_CLASS = "XQueryObjectEditorPage"; //$NON-NLS-1$
    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String TOOLTIP = getString("tooltip"); //$NON-NLS-1$
    private static final String CURSOR_AT_TEXT = getString("cursorAt.text"); //$NON-NLS-1$

    private static String getString( String str ) {
        return UiConstants.Util.getString(THIS_CLASS + '.' + str);
    }

    /**
     * Provides text widget with cut, copy, paste, select all, undo, and redo context menu and accelerator key support
     * 
     * @since 5.5.3
     */
    private StyledTextEditor textEditor;

    CLabel cursorPositionLabel;
    private LabelContribution lblCursorPositionContribution;

    private int caretOffset = -1;
    private int caretXPosition = 0;
    private int caretYPosition = 0;

    private XQueryTransformation transformation;
    private boolean isDirty = false;

    private EventListenerList propListeners = new EventListenerList();

    private Action saveAction;

    /**
     * @since 5.0.1
     */
    public XQueryObjectEditorPage() {
        super();
    }

    /**
     * Provides access to the text editor.
     * 
     * @since 5.5.3
     */
    StyledTextEditor accessTextEditor() {
        return this.textEditor;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canClose()
     * @since 5.0.1
     */
    public boolean canClose() {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 5.0.1
     */
    public void createControl( Composite parent ) {
        int style = SWT.WRAP | SWT.V_SCROLL;
        this.textEditor = new StyledTextEditor(parent, style);

        this.textEditor.addDocumentListener(new IDocumentListener() {
            public void documentAboutToBeChanged( DocumentEvent event ) {
            }

            public void documentChanged( DocumentEvent event ) {
                setDirty(true);
            }
        });

        StyledText text = this.textEditor.getTextWidget();
        CaretListener listener = new CaretListener();
        text.addSelectionListener(listener);
        text.addKeyListener(listener);
        text.addMouseListener(listener);
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @since 5.5
     */
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IFindReplaceTarget.class) && this.textEditor.getTextWidget().isFocusControl()) {
            return this.textEditor.getTextViewer().getFindReplaceTarget();
        }

        if (adapter.equals(IUndoManager.class) && this.textEditor.getTextWidget().isFocusControl()) {
            return this;
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getControl()
     * @since 5.0.1
     */
    public Control getControl() {
        return this.textEditor.getTextWidget();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitle()
     * @since 5.0.1
     */
    public String getTitle() {
        return TITLE;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitleToolTip()
     * @since 5.0.1
     */
    public String getTitleToolTip() {
        return TOOLTIP;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitleImage()
     * @since 5.0.1
     */
    public Image getTitleImage() {
        return null;
    }

    /**
     * This method return true if input is of type TransformationDiagram for XQueryProcedure, XQueryProcedure or an
     * XQueryTransformationMappingRoot
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
     * @since 5.0.1
     */
    public boolean canEdit( Object modelObject,
                            IEditorPart editor ) {
        boolean result = false;

        if (modelObject == null || !(editor instanceof DiagramEditor)) {
            result = false;
        }
        Object editableObject = getEditableObject(modelObject);

        if (TransformationHelper.isXQueryTransformationMappingRoot(editableObject)) {
            result = true;
        }

        return result;
    }

    /**
     * This editor can handle inputs of type TransformationDiagram for XQueryProcedure, XQueryProcedure or an
     * XQueryTransformationMappingRoot
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#edit(java.lang.Object)
     * @since 5.0.1
     */
    public void edit( Object modelObject ) {
        Object editableObject = getEditableObject(modelObject);

        if (TransformationHelper.isXQueryTransformationMappingRoot(editableObject)) {
            XQueryTransformationMappingRoot root = (XQueryTransformationMappingRoot)editableObject;
            MappingHelper helper = TransformationHelper.getMappingHelper(root);
            if (helper instanceof XQueryTransformation) {
                this.transformation = (XQueryTransformation)helper;
                String expression = this.transformation.getExpression();
                if (expression != null) {
                    this.textEditor.setText(expression);
                } else {
                    this.textEditor.setText(""); //$NON-NLS-1$
                }

                Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                        accessTextEditor().setFocus();
                        handleCursorPositionChanged();
                    }
                });

                updateReadOnlyState();

                // we don't want the user to be able to undo the first time the document text was set
                this.textEditor.resetUndoRedoHistory();
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#deactivate()
     * @since 5.0.1
     */
    public boolean deactivate() {
        // this editor is being closed so perform save
        // Defect 17115 involved a problem where the doSave() call resulted in an NPE
        // As a result, the page was not removed as a listener and it continued to recieve
        // events and trying to process them.
        // Fix is to catch the exception, log it and continue on with deactivation.
        try {
            doSave(true);
        } catch (Exception err) {
            UiConstants.Util.log(err);
        }

        this.textEditor.resetUndoRedoHistory();

        return true;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#doSave(boolean)
     * @since 5.0.1
     */
    public void doSave( boolean isClosing ) {
        if (!this.textEditor.isDisposed() && this.transformation != null) {
            boolean requiredStart = ModelerCore.startTxn(false, true, "Set XQuery Expression", this); //$NON-NLS-1$
            boolean succeeded = false;
            try {
                this.transformation.setExpression(this.textEditor.getText());
                succeeded = true;
            } finally {
                // If we start txn, commit it
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }
        setDirty(false);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isDirty()
     * @since 5.0.1
     */
    public boolean isDirty() {
        return this.isDirty;
    }

    void setDirty( boolean flag ) {
        if (isDirty != flag) {
            this.isDirty = flag;

            Iterator listeners = propListeners.getListeners(IPropertyListener.class);
            while (listeners.hasNext()) {
                ((IPropertyListener)listeners.next()).propertyChanged(this, IEditorPart.PROP_DIRTY);
            }
        }

        if (saveAction != null) {
            saveAction.setEnabled(flag);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#addPropertyListener(org.eclipse.ui.IPropertyListener)
     * @since 5.0.1
     */
    public void addPropertyListener( IPropertyListener listener ) {
        propListeners.addListener(IPropertyListener.class, listener);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#removePropertyListener(org.eclipse.ui.IPropertyListener)
     * @since 5.0.1
     */
    public void removePropertyListener( IPropertyListener listener ) {
        propListeners.removeListener(IPropertyListener.class, listener);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     * @since 5.0.1
     */
    public void contributeToolbarActions( ToolBarManager toolBarMgr ) {

        if (toolBarMgr == null) return;

        toolBarMgr.add(getLabelContributionForCursorPosition());
        toolBarMgr.add(new Separator());

        saveAction = new Action(getString("SaveAction.lable"), UiPlugin.getDefault().getImageDescriptor(Images.SAVE)) { //$NON-NLS-1$
            @Override
            public void run() {
                doSave(false);
            }
        };
        saveAction.setDisabledImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.SAVE_DISABLED));
        saveAction.setToolTipText(getString("SaveAction.tooltip")); //$NON-NLS-1$
        saveAction.setEnabled(false);

        toolBarMgr.add(saveAction);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#updateReadOnlyState()
     * @since 5.0.1
     */
    public void updateReadOnlyState() {
        if (this.transformation != null) {
            if (ModelObjectUtilities.isReadOnly(transformation)) {
                this.textEditor.setEditable(false);
            } else {
                this.textEditor.setEditable(true);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isEditingObject(java.lang.Object)
     * @since 5.0.1
     */
    public boolean isEditingObject( Object modelObject ) {
        boolean result = false;

        // Only continue if current transformation root and input object are non-null
        if (transformation != null && modelObject != null) {
            // Get the editable object
            Object editableObject = getEditableObject(modelObject);
            // Return true if the editable object is an xquery root AND it is the same as current xquery root
            if (TransformationHelper.isXQueryTransformationMappingRoot(editableObject)) {
                result = editableObject == this.transformation;
            }
        }
        return result;
    }

    /**
     * This method returns an XQueryTransformationMappingRoot or NULL inputs that will return this root are Transformation Diagram
     * of an XQueryProcedure, an XQuery Procedure or an XQueryTransformationMappingRoot
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getEditableObject(java.lang.Object)
     * @since 5.0.1
     */
    public Object getEditableObject( Object modelObject ) {
        XQueryTransformationMappingRoot workingRoot = null;

        EObject targetObject = null;

        if (modelObject instanceof Diagram) {
            targetObject = ((Diagram)modelObject).getTarget();
        } else {
            targetObject = (EObject)modelObject;
        }

        // Check to see if this is an XML Service model
        if (targetObject != null) {
            if (TransformationUiResourceHelper.isXQueryTransformationResource(targetObject)) {
                if (TransformationHelper.isXQueryTransformationMappingRoot(targetObject)) workingRoot = (XQueryTransformationMappingRoot)targetObject;
                else if (TransformationHelper.isXQueryProcedure(targetObject)) {
                    workingRoot = (XQueryTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(targetObject);
                }
            }
        }

        return workingRoot;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isResourceValid()
     * @since 5.0.1
     */
    public boolean isResourceValid() {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#contributeExportedActions(org.eclipse.jface.action.IMenuManager)
     * @since 5.0.1
     */
    public void contributeExportedActions( IMenuManager theMenuMgr ) {
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions( ISelection selection ) {
        // jhTODO: do we have any actions? I do not think so...
        return Collections.EMPTY_LIST;
    }

    void handleCursorPositionChanged() {
        getLabelContributionForCursorPosition().setText(getCurrentCursorPosition());
    }

    private LabelContribution getLabelContributionForCursorPosition() {
        if (lblCursorPositionContribution == null) {
            lblCursorPositionContribution = new LabelContribution("Cursor at (1000, 1000)"); //$NON-NLS-1$
        }
        return lblCursorPositionContribution;
    }

    private String getCurrentCursorPosition() {
        String sPosition = CURSOR_AT_TEXT;
        int column = 0;
        int row = 0;
        if (this.textEditor.getTextWidget() != null) {
            column = 1 + caretXPosition;
            row = 1 + caretYPosition;
        }

        sPosition = sPosition + row + ',' + ' ' + column + ')';

        return sPosition;
    }

    void captureCaretInfo() {
        StyledText text = this.textEditor.getTextWidget();
        caretOffset = text.getCaretOffset();
        caretYPosition = text.getLineAtOffset(caretOffset);
        caretXPosition = caretOffset - text.getOffsetAtLine(caretYPosition);
        handleCursorPositionChanged();
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#initialize(com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor)
     * @since 5.0.1
     */
    public void initialize( MultiPageModelEditor editor ) {
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#setOverride(com.metamatrix.modeler.ui.editors.ModelObjectEditorPage)
     * @since 5.0.1
     */
    public void setOverride( ModelObjectEditorPage editor ) {
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

    class LabelContribution extends ControlContribution {
        private static final int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;
        Combo cbx = null;
        String sText;

        public LabelContribution( String sText ) {
            super("myId"); //$NON-NLS-1$
            this.sText = sText;
        }

        /**
         * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createControl( Composite parent ) {
            cursorPositionLabel = WidgetFactory.createLabel(parent, LABEL_GRID_STYLE, sText);
            return cursorPositionLabel;
        }

        public void setText( String text ) {
            cursorPositionLabel.setText(text);
        }

    }

    class CaretListener implements SelectionListener, KeyListener, MouseListener {
        public void widgetSelected( SelectionEvent e ) {
            captureCaretInfo();
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
            captureCaretInfo();
        }

        public void selectionChanged( SelectionChangedEvent e ) {
        }

        public void mouseUp( MouseEvent e ) {
            captureCaretInfo();
        }

        public void mouseDown( MouseEvent e ) {
        }

        public void mouseDoubleClick( MouseEvent e ) {
            captureCaretInfo();
        }

        public void keyPressed( KeyEvent e ) {
        }

        public void keyReleased( KeyEvent e ) {
            captureCaretInfo();
        }

    }

}
