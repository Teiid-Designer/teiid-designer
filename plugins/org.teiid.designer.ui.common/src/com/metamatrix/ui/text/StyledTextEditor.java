/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.FindReplaceAction;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.UiPlugin;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * The <code>StyledTextEditor</code> class contains and controls a {@link org.eclipse.swt.custom.StyledText} control. Local Cut,
 * Copy, Paste, SelectAll, Undo, and Redo actions are taken care of by this class. This class handles the appropriate keyboard
 * accelerators for those actions.
 * 
 * @since 5.5.3
 */
public class StyledTextEditor implements IMenuListener, KeyListener, UiConstants {

    /**
     * Identifier of the copy action.
     * 
     * @since 5.5.3
     */
    static final String COPY_ID = ActionFactory.COPY.getId();

    /**
     * Identifier of the cut action.
     * 
     * @since 5.5.3
     */
    static final String CUT_ID = ActionFactory.CUT.getId();

    /**
     * Identifier of the find/replace action.
     * 
     * @since 5.5.3
     */
    static final String FIND_ID = ActionFactory.FIND.getId();

    /**
     * Identifier of the paste action.
     * 
     * @since 5.5.3
     */
    static final String PASTE_ID = ActionFactory.PASTE.getId();

    /**
     * Identifier of the redo action.
     * 
     * @since 5.5.3
     */
    static final String REDO_ID = ActionFactory.REDO.getId();

    /**
     * Identifier of the select all action.
     * 
     * @since 5.5.3
     */
    static final String SELECT_ALL_ID = ActionFactory.SELECT_ALL.getId();

    /**
     * Identifier of the undo action.
     * 
     * @since 5.5.3
     */
    static final String UNDO_ID = ActionFactory.UNDO.getId();

    /**
     * Creates a read only text editor; the cut, paste, undo, and redo actions are not available.
     * 
     * @param parent the parent of the text widget
     * @since 5.5.3
     */
    public static StyledTextEditor createReadOnlyEditor( Composite parent,
                                                         int style ) {
        StyledTextEditor result = new StyledTextEditor(parent, style);
        result.setEditable(false);
        result.setAllowCut(false);
        result.setAllowPaste(false);
        result.setAllowUndoRedo(false);

        // need to remove the undo manager since we are not allowing undo/redo
        IUndoManager undoMgr = result.viewer.getUndoManager();

        if (undoMgr != null) {
            undoMgr.disconnect();
            result.viewer.setUndoManager(null);
        }

        return result;
    }

    /**
     * Indicates if the copy action should be included in the context menu and as an accelerator in the text widget.
     * 
     * @since 5.5.3
     */
    private boolean allowCopy = true;

    /**
     * Indicates if the cut action should be included in the context menu and as an accelerator in the text widget.
     * 
     * @since 5.5.3
     */
    private boolean allowCut = true;

    /**
     * Indicates if the find action should be included in the context menu and as an accelerator in the text widget.
     * 
     * @since 5.5.3
     */
    private boolean allowFind = true;

    /**
     * Indicates if the paste action should be included in the context menu and as an accelerator in the text widget.
     * 
     * @since 5.5.3
     */
    private boolean allowPaste = true;

    /**
     * Indicates if the select all action should be included in the context menu and as an accelerator in the text widget.
     * 
     * @since 5.5.3
     */
    private boolean allowSelectAll = true;

    /**
     * The size of the undo/redo history.
     * 
     * @since 5.5.3
     */
    private int historySize = -1;

    /**
     * Indicates if the undo and redo actions should be included in the context menu and as an accelerator in the text widget.
     * 
     * @since 5.5.3
     */
    private boolean allowUndoRedo = true;

    private ContextMenuAction copyAction;

    private ContextMenuAction cutAction;

    private ContextMenuAction findAction;

    private ContextMenuAction pasteAction;

    private ContextMenuAction redoAction;

    private ContextMenuAction selectAllAction;

    private ContextMenuAction undoAction;

    /**
     * The parent of the {@link StyledText} control. Will be <code>null</code> if the {@link StyledText} control is passed in at
     * construction.
     * 
     * @since 5.5.3
     */
    private TextViewer viewer;

    /**
     * Listeners that are notified when the context menu is going to be shown.
     * 
     * @since 5.5.3
     */
    private List menuListeners;

    /**
     * Constructs an editor that uses a text viewer configured with an undo manager using the default history limit. The cut,
     * copy, paste, select all, undo, and redo actions are all available.
     * 
     * @param parent the parent of the text widget
     * @param style the style to use for the text widget
     * @since 5.5.3
     */

    public StyledTextEditor( Composite parent,
                             int style ) {
        this(parent, style, -1, true, true, true, true, true, true);
    }

    /**
     * Constructs an editor that uses a text viewer configured with an undo manager.
     * 
     * @param parent the parent of the text widget
     * @param style the style to use for the text widget
     * @param historySize the undo/redo limit or -1 to use default value
     * @param allowCut the flag indicating if the cut action should be available
     * @param allowCopy the flag indicating if the copy action should be available
     * @param allowPaste the flag indicating if the paste action should be available
     * @param allowSelectAll the flag indicating if the select all action should be available
     * @param allowUndoRedo the flag indicating if the undo and redo actions should be available
     * @param allowFind the flag indicating if the find action should be available
     * @since 5.5.3
     */
    public StyledTextEditor( Composite parent,
                             int style,
                             int historySize,
                             boolean allowCut,
                             boolean allowCopy,
                             boolean allowPaste,
                             boolean allowSelectAll,
                             boolean allowUndoRedo,
                             boolean allowFind ) {
        this.viewer = new TextViewer(parent, style);
        this.historySize = historySize;
        this.allowCut = allowCut;
        this.allowCopy = allowCopy;
        this.allowPaste = allowPaste;
        this.allowSelectAll = allowSelectAll;
        this.allowUndoRedo = allowUndoRedo;
        this.allowFind = allowFind;

        configureTextViewer();
    }

    /**
     * Constructs an editor for the specified viewer. If the viewer already has an undo manager and/or a document set on it, those
     * will be used; otherwise new ones will be constructed. Cut, copy, paste, select all, undo, and redo actions are all
     * available.
     * 
     * @param viewer the viewer to use
     * @since 5.5.3
     */
    public StyledTextEditor( TextViewer viewer ) {
        this.viewer = viewer;
        configureTextViewer();
    }

    /**
     * @param listener the listener being registered to receive a notification when the context menu will be show
     * @since 5.5.3
     */
    public void addMenuListener( IMenuListener listener ) { // NO_UCD (Indicates this is ignored by unused code detection tool)
        if (this.menuListeners == null) {
            this.menuListeners = new ArrayList(1);
        }

        if (!this.menuListeners.contains(listener)) {
            this.menuListeners.add(listener);
        }
    }

    private void configureTextViewer() {
        if (this.historySize < 0) {
            // get eclipse undo history limit
            IPreferencesService service = Platform.getPreferencesService();

            // use the Eclipse text editor setting for history (NOT WORKING???)
            int limit = service.getInt("com.eclipse.ui.editors", //$NON-NLS-1$
                                       AbstractDecoratedTextEditorPreferenceConstants.EDITOR_UNDO_HISTORY_SIZE,
                                       20,
                                       null);
            this.historySize = limit;
        }

        IUndoManager undoManager = this.viewer.getUndoManager();

        if (undoManager == null) {
            undoManager = new TextViewerUndoManager(this.historySize);
            this.viewer.setUndoManager(undoManager);
        }

        undoManager.connect(this.viewer);

        if (viewer.getDocument() == null) {
            Document document = new Document();
            this.viewer.setDocument(document);
        }

        setDoubleClickStrategy(new DefaultTextDoubleClickStrategy());

        configureTextWidget(getTextWidget());

        this.viewer.activatePlugins();

        // focus listener to wire/unwire accelerators
        getTextWidget().addFocusListener(new FocusListener() {

            public void focusGained( FocusEvent e ) {
                handleFocusGained(e);
            }

            public void focusLost( FocusEvent e ) {
                handleFocusLost(e);
            }
        });
    }

    /**
     * Configure the context menu and key accelerators.
     * 
     * @param styledText the widget being configured
     * @since 5.5.3
     */
    private void configureTextWidget( StyledText styledText ) {
        styledText.addKeyListener(this);

        MenuManager menuMgr = new MenuManager(null, null);
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(this);

        styledText.setMenu(menuMgr.createContextMenu(styledText));
    }

    /**
     * Disposes of this editor's registered listeners.
     * 
     * @since 5.5.3
     */
    public void dispose() {
        if ((getTextWidget() != null) && !getTextWidget().isDisposed()) {
            getTextWidget().removeKeyListener(this);
        }
    }

    /**
     * Obtains the action with the specified ID and then sets the enabled state of that action. Must call the
     * <code>isAllowXXX</code> method to make sure the action is permitted.
     * 
     * @param id the identifier of the action being requested
     * @return the action or <code>null</code> if no action found
     * @since 5.5.3
     */
    private IAction getAction( String id ) {
        if (id.equals(COPY_ID)) {
            CoreArgCheck.isTrue(isAllowCopy(), "Editor does not allow Copy"); //$NON-NLS-1$

            if (this.copyAction == null) {
                this.copyAction = new ContextMenuAction(COPY_ID);
            }

            this.copyAction.setEnabledState();
            return this.copyAction;
        }

        if (id.equals(CUT_ID)) {
            CoreArgCheck.isTrue(isAllowCut(), "Editor does not allow Cut"); //$NON-NLS-1$

            if (this.cutAction == null) {
                this.cutAction = new ContextMenuAction(CUT_ID);
            }

            this.cutAction.setEnabledState();
            return this.cutAction;
        }

        if (id.equals(FIND_ID)) {
            CoreArgCheck.isTrue(isAllowFind(), "Editor does not allow Find"); //$NON-NLS-1$

            if (this.findAction == null) {
                this.findAction = new ContextMenuAction(FIND_ID);
            }

            this.findAction.setEnabledState();
            return this.findAction;
        }

        if (id.equals(PASTE_ID)) {
            CoreArgCheck.isTrue(isAllowPaste(), "Editor does not allow Paste"); //$NON-NLS-1$

            if (this.pasteAction == null) {
                this.pasteAction = new ContextMenuAction(PASTE_ID);
            }

            this.pasteAction.setEnabledState();
            return this.pasteAction;
        }

        if (id.equals(REDO_ID)) {
            CoreArgCheck.isTrue(isAllowUndoRedo(), "Editor does not allow Redo"); //$NON-NLS-1$

            if (this.redoAction == null) {
                this.redoAction = new ContextMenuAction(REDO_ID);
            }

            this.redoAction.setEnabledState();
            return this.redoAction;
        }

        if (id.equals(SELECT_ALL_ID)) {
            CoreArgCheck.isTrue(isAllowSelectAll(), "Editor does not allow Select All"); //$NON-NLS-1$

            if (this.selectAllAction == null) {
                this.selectAllAction = new ContextMenuAction(SELECT_ALL_ID);
            }

            this.selectAllAction.setEnabledState();
            return this.selectAllAction;
        }

        if (id.equals(UNDO_ID)) {
            CoreArgCheck.isTrue(isAllowUndoRedo(), "Editor does not allow Redo"); //$NON-NLS-1$

            if (this.undoAction == null) {
                this.undoAction = new ContextMenuAction(UNDO_ID);
            }

            this.undoAction.setEnabledState();
            return this.undoAction;
        }

        return null;
    }

    /**
     * @return the document
     * @since 5.5.3
     */
    public IDocument getDocument() {
        return this.viewer.getDocument();
    }

    /**
     * @return the text content
     * @since 5.5.3
     */
    public String getText() {
        return this.viewer.getDocument().get();
    }

    /**
     * @return the text widget or <code>null</code> if a viewer is being used
     * @since 5.5.3
     */
    public StyledText getTextWidget() {
        return this.viewer.getTextWidget();
    }

    /**
     * @return the text viewer or <code>null</code> if one is not being used
     * @since 5.5.3
     */
    public TextViewer getTextViewer() {
        return this.viewer;
    }

    /**
     * Obtains the <code>IUndoManager</code> of this adapter's {@link TextViewer}.
     * 
     * @return the undo manager or <code>null</code> if not set on the viewer
     * @since 5.5.3
     */
    public IUndoManager getUndoManager() {
        return this.viewer.getUndoManager();
    }

    /**
     * @param e the focus gained event
     * @since 5.5.3
     */
    protected void handleFocusGained( FocusEvent e ) {
        if (this.findAction != null) {
            this.findAction.setAccelerator(SWT.CTRL | 'F');
        }

        if (this.selectAllAction != null) {
            this.selectAllAction.setAccelerator(SWT.CTRL | 'A');
        }

        if (this.undoAction != null) {
            this.undoAction.setAccelerator(SWT.CTRL | 'Z');
            this.redoAction.setAccelerator(SWT.CTRL | 'Y');
        }
    }

    /**
     * @param e the focus lost event
     * @since 5.5.3
     */
    protected void handleFocusLost( FocusEvent e ) {
        if (this.findAction != null) {
            this.findAction.setAccelerator(0);
        }

        if (this.selectAllAction != null) {
            this.selectAllAction.setAccelerator(0);
        }

        if (this.undoAction != null) {
            this.undoAction.setAccelerator(0);
            this.redoAction.setAccelerator(0);
        }
    }

    /**
     * @return <code>true</code> if there is an undo manager
     * @since 5.5.3
     */
    boolean hasUndoManager() {
        return (getUndoManager() != null);
    }

    /**
     * @return <code>true</code> if the copy action is allowed in the context menu and as an accelerator in the text widget
     * @since 5.5.3
     */
    private boolean isAllowCopy() {
        return this.allowCopy;
    }

    /**
     * @return <code>true</code> if the cut action is allowed in the context menu and as an accelerator in the text widget
     * @since 5.5.3
     */
    private boolean isAllowCut() {
        return this.allowCut;
    }

    /**
     * @return <code>true</code> if the find action is allowed in the context menu and as an accelerator in the text widget
     * @since 5.5.3
     */
    private boolean isAllowFind() {
        return this.allowFind;
    }

    /**
     * @return <code>true</code> if the paste action is allowed in the context menu and as an accelerator in the text widget
     * @since 5.5.3
     */
    private boolean isAllowPaste() {
        return this.allowPaste;
    }

    /**
     * @return <code>true</code> if the select all action is allowed in the context menu and as an accelerator in the text widget
     * @since 5.5.3
     */
    private boolean isAllowSelectAll() {
        return this.allowSelectAll;
    }

    /**
     * @return <code>true</code> if the undo and redo actions are allowed in the context menu and as an accelerator in the text
     *         widget
     * @since 5.5.3
     */
    private boolean isAllowUndoRedo() {
        return this.allowUndoRedo && hasUndoManager();
    }

    /**
     * @return <code>true</code> if underlying text control has is disposed
     * @since 5.5.3
     */
    public boolean isDisposed() {
        Control control = this.viewer.getTextWidget();
        return ((control == null) || control.isDisposed());
    }

    /**
     * @return <code>true</code> if the text content can be changed
     * @since 5.5.3
     */
    public boolean isEditable() {
        return this.viewer.isEditable();
    }

    /**
     * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
     * @since 5.5.3
     */
    public void keyPressed( KeyEvent e ) {
        // nothing to do
    }

    /**
     * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
     * @since 5.5.3
     */
    public void keyReleased( KeyEvent e ) {
        IAction action = null;
        int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);

        if (this.isAllowCopy() && (accelerator == getAction(COPY_ID).getAccelerator())) {
            action = getAction(COPY_ID);
        } else if (this.isAllowCut() && (accelerator == getAction(CUT_ID).getAccelerator())) {
            action = getAction(CUT_ID);
        } else if (this.isAllowFind() && (accelerator == getAction(FIND_ID).getAccelerator())) {
            action = getAction(FIND_ID);
        } else if (this.isAllowPaste() && (accelerator == getAction(PASTE_ID).getAccelerator())) {
            action = getAction(PASTE_ID);
        } else if (this.isAllowUndoRedo() && (accelerator == getAction(REDO_ID).getAccelerator())) {
            action = getAction(REDO_ID);
        } else if (this.isAllowSelectAll() && (accelerator == getAction(SELECT_ALL_ID).getAccelerator())) {
            action = getAction(SELECT_ALL_ID);
        } else if (this.isAllowUndoRedo() && (accelerator == getAction(UNDO_ID).getAccelerator())) {
            action = getAction(UNDO_ID);
        }

        if ((action != null) && action.isEnabled()) {
            action.run();
        }
    }

    /**
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     * @since 5.5.3
     */
    public void menuAboutToShow( IMenuManager menuMgr ) {
        if (this.isAllowCut()) {
            menuMgr.add(getAction(CUT_ID));
        }

        if (this.isAllowCopy()) {
            menuMgr.add(getAction(COPY_ID));
        }

        if (this.isAllowPaste()) {
            menuMgr.add(getAction(PASTE_ID));
        }

        if (this.isAllowSelectAll()) {
            menuMgr.add(getAction(SELECT_ALL_ID));
        }

        if (this.isAllowUndoRedo()) {
            menuMgr.add(new Separator());
            menuMgr.add(getAction(UNDO_ID));
            menuMgr.add(getAction(REDO_ID));
        }

        if (this.isAllowFind()) {
            menuMgr.add(new Separator());
            menuMgr.add(getAction(FIND_ID));
        }

        // now give listeners a chance to contribute
        if (this.menuListeners != null) {
            for (Iterator itr = this.menuListeners.iterator(); itr.hasNext();) {
                ((IMenuListener)itr.next()).menuAboutToShow(menuMgr);
            }
        }
    }

    /**
     * @param listener the listener who will no longer be notified when the context menu is being shown
     * @since 5.5.3
     */
    public void removeMenuListener( IMenuListener listener ) { // NO_UCD (Indicates this is ignored by unused code detection tool)
        if (this.menuListeners != null) {
            this.menuListeners.remove(listener);
        }
    }

    /**
     * Clears the undo/redo history of this editor's {@link TextViewer}.
     * 
     * @since 5.5.3
     */
    public void resetUndoRedoHistory() {
        if (hasUndoManager()) {
            getUndoManager().reset();
        }
    }

    /**
     * @param allowCopy <code>true</code> if the copy action should be used in the context menu and as an accelerator in the text
     *        widget
     * @since 5.5.3
     */
    public void setAllowCopy( boolean allowCopy ) {
        this.allowCopy = allowCopy;
    }

    /**
     * @param allowCut <code>true</code> if the cut action should be used in the context menu and as an accelerator in the text
     *        widget
     * @since 5.5.3
     */
    public void setAllowCut( boolean allowCut ) {
        this.allowCut = allowCut;
    }

    /**
     * @param allowFind <code>true</code> if the find action should be used in the context menu and as an accelerator in the text
     *        widget
     * @since 5.5.3
     */
    public void setAllowFind( boolean allowFind ) {
        this.allowFind = allowFind;

        // synchronize key accelerator with focus
        if (this.findAction != null) {
            this.findAction.setAccelerator((this.allowFind && getTextWidget().isFocusControl()) ? SWT.CTRL | 'F' : 0);
        }
    }

    /**
     * @param allowPaste <code>true</code> if the paste action should be used in the context menu and as an accelerator in the
     *        text widget
     * @since 5.5.3
     */
    public void setAllowPaste( boolean allowPaste ) {
        this.allowPaste = allowPaste;
    }

    /**
     * @param allowSelectAll <code>true</code> if the select action should be used in the context menu and as an accelerator in
     *        the text widget
     * @since 5.5.3
     */
    public void setAllowSelectAll( boolean allowSelectAll ) {
        this.allowSelectAll = allowSelectAll;

        // synchronize key accelerator with focus
        if (this.selectAllAction != null) {
            this.selectAllAction.setAccelerator((this.allowSelectAll && getTextWidget().isFocusControl()) ? SWT.CTRL | 'A' : 0);
        }
    }

    /**
     * @param allowUndo <code>true</code> if the undo action should be used in the context menu and as an accelerator in the text
     *        widget
     * @since 5.5.3
     */
    public void setAllowUndoRedo( boolean allowUndoRedo ) {
        this.allowUndoRedo = allowUndoRedo && hasUndoManager();

        // clear undo/redo history if not allowed
        if (!this.allowUndoRedo && hasUndoManager()) {
            resetUndoRedoHistory();
        }

        // synchronize key accelerator with focus
        if (this.undoAction != null) {
            this.undoAction.setAccelerator((this.allowUndoRedo && getTextWidget().isFocusControl()) ? SWT.CTRL | 'Z' : 0);
            this.redoAction.setAccelerator((this.allowUndoRedo && getTextWidget().isFocusControl()) ? SWT.CTRL | 'Y' : 0);
        }
    }

    /**
     * @param color the new color to set the text widget's background
     * @since 5.5.3
     */
    public void setBackground( Color color ) {
        getTextWidget().setBackground(color);
    }

    /**
     * @param strategy the viewer's new double click strategy
     * @since 5.5.3
     */
    public void setDoubleClickStrategy( ITextDoubleClickStrategy strategy ) {
        this.viewer.setTextDoubleClickStrategy(strategy, IDocument.DEFAULT_CONTENT_TYPE);
    }

    /**
     * Sets the editable state of the editor.
     * 
     * @param editable the new editable state
     * @since 5.5.3
     */
    public void setEditable( boolean editable ) {
        getTextViewer().setEditable(editable);
    }

    /**
     * Sets keyboard focus to the text widget.
     * 
     * @since 5.5
     */
    public void setFocus() {
        getTextWidget().setFocus();
    }

    /**
     * @param layoutData the layoutData to be set on the text widget
     * @since 5.5.3
     */
    public void setLayoutData( Object layoutData ) {
        getTextWidget().setLayoutData(layoutData);
    }

    /**
     * When a text viewer is used the specified text is set on the viewer's document. Otherwise, the text widget's text is set.
     * 
     * @param text the new text content of this editor
     * @since 5.5.3
     */
    public void setText( String text ) {
        this.viewer.getDocument().set(text);
    }

    /**
     * This editor's viewer must have an undo manager or this has no effect.
     * 
     * @param limit the new undo/redo history limit
     * @since 5.5
     */
    public void setUndoRedoHistoryLimit( int limit ) {
        if (hasUndoManager()) {
            this.viewer.getUndoManager().setMaximalUndoLevel(limit);
        }
    }

    private class ContextMenuAction extends Action {

        private final String id;

        public ContextMenuAction( String id ) {
            this.id = id;

            IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
            IAction tempAction = null;
            int accelerator = 0;

            if (this.id.equals(UNDO_ID)) {
                tempAction = ActionFactory.UNDO.create(window);
                accelerator = SWT.CTRL | 'Z';
            } else if (this.id.equals(REDO_ID)) {
                tempAction = ActionFactory.REDO.create(window);
                accelerator = SWT.CTRL | 'Y';
            } else if (this.id.equals(COPY_ID)) {
                tempAction = ActionFactory.COPY.create(window);
                accelerator = SWT.CTRL | 'C';
            } else if (this.id.equals(CUT_ID)) {
                tempAction = ActionFactory.CUT.create(window);
                accelerator = SWT.CTRL | 'X';
            } else if (this.id.equals(FIND_ID)) {
                // just set some properties
                tempAction = new Action() {
                    // need to subclass but nothing to implement
                };
                tempAction.setText(Util.getString(I18nUtil.getPropertyPrefix(StyledTextEditor.class) + "findAction.label")); //$NON-NLS-1$
                tempAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.FIND_ENABLED));
                tempAction.setDisabledImageDescriptor(UiPlugin.getDefault().getImageDescriptor(Images.FIND_DISABLED));
                accelerator = SWT.CTRL | 'F';
            } else if (this.id.equals(PASTE_ID)) {
                tempAction = ActionFactory.PASTE.create(window);
                accelerator = SWT.CTRL | 'V';
            } else if (this.id.equals(SELECT_ALL_ID)) {
                tempAction = ActionFactory.SELECT_ALL.create(window);
                accelerator = SWT.CTRL | 'A';
            }

            setText(tempAction.getText());
            setImageDescriptor(tempAction.getImageDescriptor());
            setDisabledImageDescriptor(tempAction.getDisabledImageDescriptor());
            setAccelerator(accelerator);
        }

        private void find() {
            assert (this.id.equals(FIND_ID));

            FindReplaceAction action = getFindAction();

            if (action != null) {
                action.update();
                action.run();
            }
        }

        private FindReplaceAction getFindAction() {
            assert (this.id.equals(FIND_ID));

            IWorkbenchPage page = UiUtil.getWorkbenchPage();

            if (page != null) {
                IWorkbenchPart part = page.getActivePart();

                if (part != null) {
                    return new FindReplaceAction(ResourceBundle.getBundle(UiConstants.PACKAGE_ID + ".i18n"), //$NON-NLS-1$
                                                 "StyledTextEditor.findAction.", part); //$NON-NLS-1$
                }
            }

            return null;
        }

        private boolean isFindEnabled() {
            assert (this.id.equals(FIND_ID));

            FindReplaceAction action = getFindAction();

            if (action != null) {
                action.update();
                return action.isEnabled();
            }

            return false;
        }

        /**
         * @see org.eclipse.jface.action.Action#run()
         * @since 5.5
         */
        @Override
        public void run() {
            if (this.id.equals(UNDO_ID) && hasUndoManager()) {
                getUndoManager().undo();
            } else if (this.id.equals(REDO_ID) && hasUndoManager()) {
                getUndoManager().redo();
            } else if (this.id.equals(COPY_ID)) {
                getTextWidget().copy();
            } else if (this.id.equals(CUT_ID)) {
                getTextWidget().cut();
            } else if (this.id.equals(FIND_ID)) {
                find();
            } else if (this.id.equals(PASTE_ID)) {
                getTextWidget().paste();
            } else if (this.id.equals(SELECT_ALL_ID)) {
                getTextWidget().selectAll();
            }
        }

        void setEnabledState() {
            boolean enable = false;
            StyledText styledText = getTextWidget();

            if (this.id.equals(UNDO_ID)) {
                enable = (styledText.getEditable() && hasUndoManager() && getUndoManager().undoable());
            } else if (this.id.equals(REDO_ID)) {
                enable = (styledText.getEditable() && hasUndoManager() && getUndoManager().redoable());
            } else if (this.id.equals(COPY_ID)) {
                enable = (styledText.getSelectionCount() != 0);
            } else if (this.id.equals(CUT_ID)) {
                enable = (styledText.getEditable() && (styledText.getSelectionCount() != 0));
            } else if (this.id.equals(PASTE_ID)) {
                enable = (styledText.getEditable() && !SystemClipboardUtilities.isEmpty());
            } else if (this.id.equals(SELECT_ALL_ID)) {
                enable = (styledText.getText().length() != 0);
            } else if (this.id.equals(FIND_ID)) {
                enable = isFindEnabled();
            }

            setEnabled(enable);
        }
    }
}
