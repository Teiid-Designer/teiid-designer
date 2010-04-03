/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.validation.Severity;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.IRevertable;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * @since 4.0
 */
public final class VdbEditorOverviewPage extends EditorPart
    implements StringUtil.Constants, VdbUiConstants, VdbEditor.Constants, IRevertable, IGotoMarker, IUndoManager {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbEditorOverviewPage.class);

    private static final int COLUMNS = 2;

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final String NOTES_TITLE = getString("notesTitle"); //$NON-NLS-1$

    private static final String DESCRIPTION_LABEL = getString("descriptionLabel"); //$NON-NLS-1$
    private static final String NAME_LABEL = getString("nameLabel"); //$NON-NLS-1$
    private static final String STATUS_LABEL = getString("statusLabel"); //$NON-NLS-1$
    private static final String VALIDATION_LABEL = getString("validationLabel"); //$NON-NLS-1$
    private static final String STATUS_ERROR_MESSAGE = getString("errorMessage"); //$NON-NLS-1$

    public static final String NOTES_MESSAGE = getString("notesMessage"); //$NON-NLS-1$

    /**
     * @since 4.0
     */
    private static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    VdbEditor editor;
    private Control pageControl;
    private CLabel statusLabel;
    private FontMetrics fontMetrics;
    VdbEditorModelComposite modelPanel;
    private IResourceChangeListener resourceChangeListener;
    boolean isEnabled = false;

    protected transient boolean reloadingDescription;

    /**
     * Provides text widget with cut, copy, paste, select all, undo, and redo context menu and accelerator key support.
     * 
     * @since 5.5.3
     */
    private StyledTextEditor textEditor;

    /**
     * @since 4.0
     */
    VdbEditorOverviewPage( final VdbEditor editor ) {
        this.editor = editor;
        // Access Eclipse Dialog class to get static initializer to run
        Dialog.class.getName();
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

    protected int convertHeightInCharsToPixels( int chars ) {
        // test for failure to initialize for backward compatibility
        if (fontMetrics == null) return 0;
        return Dialog.convertHeightInCharsToPixels(fontMetrics, chars);
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.0
     */
    @Override
    public void createPartControl( final Composite parent ) {
        pageControl = parent;
        // Compute and store a font metric
        GC gc = new GC(parent);
        gc.setFont(parent.getFont());
        fontMetrics = gc.getFontMetrics();
        gc.dispose();

        // insert a ScrolledComposite so controls don't disappear if the panel shrinks
        final ScrolledComposite scroller = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
        scroller.setLayout(new GridLayout());

        // tweak the scroll bars to give better scrolling behavior:
        ScrollBar bar = scroller.getHorizontalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif
        bar = scroller.getVerticalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif

        final Composite pg = WidgetFactory.createPanel(scroller, SWT.NONE, GridData.FILL_BOTH, 1, COLUMNS);
        scroller.setContent(pg);

        // Upper panel: ===========================
        Vdb vdb = editor.getVdb();
        WidgetFactory.createLabel(pg, NAME_LABEL);
        WidgetFactory.createLabel(pg, vdb.getName().toString());
        WidgetFactory.createLabel(pg, DESCRIPTION_LABEL);

        createTextEditor(pg);

        WidgetFactory.createLabel(pg, STATUS_LABEL);
        this.statusLabel = WidgetFactory.createLabel(pg, GridData.HORIZONTAL_ALIGN_FILL);
        WidgetFactory.createLabel(pg, VALIDATION_LABEL);

        // Model table: ===========================
        this.modelPanel = new VdbEditorModelComposite(this.editor);
        final Control panel = this.modelPanel.createPartControl(pg);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, COLUMNS, 1);
        gd.heightHint = 180;
        gd.minimumHeight = 180;
        panel.setLayoutData(gd);

        // Lower Notes: ===========================
        Group notes = new Group(pg, SWT.NONE);
        notes.setFont(JFaceResources.getBannerFont());
        notes.setText(NOTES_TITLE);
        notes.setLayout(new GridLayout());
        notes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, COLUMNS, 1));
        FormText ft = new FormText(notes, SWT.WRAP);
        ft.setHyperlinkSettings(new HyperlinkSettings(ft.getDisplay()));
        ft.setText(NOTES_MESSAGE, true, false);
        ft.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // ========= GUI finish-up:

        // Size with a fixed width and a bit more than the kids' height:
        Point pt = pg.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        int miny = pt.y + 45; // add a little extra to keep all label text visible.
        scroller.setMinWidth(400);
        scroller.setMinHeight(miny);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);

        // pack and resize:
        panel.pack(true);
        pg.pack(true);
        modelPanel.resetColumnWidths();

        // Initialize widgets
        update();

        // don't want the initial setting of the description to be able to be undoable
        this.textEditor.resetUndoRedoHistory();

        resourceChangeListener = new IResourceChangeListener() {
            public void resourceChanged( IResourceChangeEvent event ) {
                modelPanel.refresh();
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
    }

    private void createTextEditor( Composite parent ) {
        int style = SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER;
        this.textEditor = new StyledTextEditor(parent, style);

        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 1;
        gridData.heightHint = convertHeightInCharsToPixels(4);
        gridData.minimumHeight = convertHeightInCharsToPixels(3);
        this.textEditor.setLayoutData(gridData);

        this.textEditor.getDocument().addDocumentListener(new IDocumentListener() {

            public void documentAboutToBeChanged( DocumentEvent event ) {
            }

            public void documentChanged( DocumentEvent event ) {
                if (!reloadingDescription) {
                    // only set modified if not updating programmatically:
                    descriptionModified();
                }
            }

        });
    }

    /**
     * @since 4.0
     */
    void descriptionModified() {
        if (resetForReadOnly()) {
            MessageDialog.openWarning(null, getString("readOnlyVDBDialogTitle"), getString("readOnlyVDBDialogMessage")); //$NON-NLS-1$ //$NON-NLS-2$
            // reload old description
            String description = editor.getVdb().getDescription();
            if (description == null) {
                // null string not allowed:
                description = ""; //$NON-NLS-1$
            }
            reloadingDescription = true;
            this.textEditor.setText(description);
            setEnabledState();
            reloadingDescription = false;
            return;
        }
        this.editor.getVdb().setDescription(this.textEditor.getText());
        this.editor.update();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        if (resourceChangeListener != null) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
        }

        this.textEditor.dispose();
        super.dispose();
    }

    public void doRevertToSaved() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (editor.getVdb() != null && !isDescriptionWidgetDisposed()) {
                    update();
                    setFocus();
                }
            }
        });
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.0
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
        update();
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 4.0
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
     * @since 5.5.3
     */
    @Override
    public Object getAdapter( Class adapter ) {
        if (adapter.equals(IFindReplaceTarget.class) && this.textEditor.getTextWidget().isFocusControl()) {
            return this.textEditor.getTextViewer().getFindReplaceTarget();
        }

        if (adapter.equals(IUndoManager.class)) {
            if (this.textEditor.getTextWidget().isFocusControl()) {
                return this;
            }

            return null;
        }

        return super.getAdapter(adapter);
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getRedoLabel()
     * @since 5.5
     */
    public String getRedoLabel() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbEditorOverviewPage.class) + "redoLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getUndoLabel()
     * @since 5.5
     */
    public String getUndoLabel() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbEditorOverviewPage.class) + "undoLabel"); //$NON-NLS-1$
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.IEditorPart#gotoMarker(org.eclipse.core.resources.IMarker)
     * @since 4.0
     */
    public void gotoMarker( final IMarker marker ) {
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 4.0
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) throws PartInitException {
        if (input != null && !(input instanceof IFileEditorInput)) {
            throw new PartInitException(INVALID_INPUT_MESSAGE);
        }
        setSite(site);
        setInput(input);
        setPartName(TITLE);

        // install undo/redo global actions so that Edit -> Undo and Edit -> Redo menu items work
        AbstractActionService actionService = (AbstractActionService)UiPlugin.getDefault().getActionService(site.getPage());

        try {
            site.getActionBars().setGlobalActionHandler(ActionFactory.UNDO.getId(),
                                                        actionService.getAction(ActionFactory.UNDO.getId()));
            site.getActionBars().setGlobalActionHandler(ActionFactory.REDO.getId(),
                                                        actionService.getAction(ActionFactory.REDO.getId()));
        } catch (CoreException e) {
            Util.log(e);
        }
    }

    /**
     * Convenience method to find out if the text widget is disposed.
     * 
     * @return <code>true</code> if the text widget is <code>null</code> or has been disposed
     * @since 5.5.3
     */
    boolean isDescriptionWidgetDisposed() {
        return this.textEditor.isDisposed();
    }

    /**
     * @return False.
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     * @since 4.0
     */
    @Override
    public boolean isDirty() {
        return this.editor.getVdb().isModified();
    }

    /**
     * @return False.
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 4.0
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#redo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5
     */
    public void redo( IProgressMonitor monitor ) {
        this.textEditor.getUndoManager().redo();
        monitor.done();
    }

    private boolean resetForReadOnly() {
        boolean isReadOnly = false;
        final IFile file = ((IFileEditorInput)getEditorInput()).getFile();
        isReadOnly = file.isReadOnly();

        return isReadOnly;
    }

    void setEnabledState() {
        // syncExec so that the enabled state can be determined immediately after this method is called.
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (!isDescriptionWidgetDisposed()) {
                    final IFile file = ((IFileEditorInput)getEditorInput()).getFile();
                    isEnabled = !file.isReadOnly();

                    // change description text:
                    accessTextEditor().setEditable(isEnabled);
                    if (isEnabled) {
                        accessTextEditor().setBackground(UiUtil.getSystemColor(SWT.COLOR_WHITE));
                    } else {
                        accessTextEditor().setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                    }
                    // change model panel:
                    modelPanel.setEnabledState(isEnabled);
                }
            }
        });
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 4.0
     */
    @Override
    public void setFocus() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (editor.getVdb() != null) setEnabledState();
            }
        });
    }

    /**
     * Public method used to synch the VDB outside of this editor sub-panel. if autoSave is ON, then this method forces a save,
     * else the user is asked to save the dirty editor after synchronizing. (Defect 22305)
     * 
     * @param autoSave
     */
    public void synchronizeVdb( boolean autoSave ) {
        modelPanel.synchronizeVdb(autoSave);
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#undo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5
     */
    public void undo( IProgressMonitor monitor ) {
        this.textEditor.getUndoManager().undo();
        monitor.done();
    }

    /**
     * @since 4.0
     */
    public void update() {
        if (pageControl.isDisposed()) return;
        final Vdb vdb = this.editor.getVdb();
        // update the status label
        Severity severity = Severity.OK_LITERAL;
        for (VdbModelEntry entry : vdb.getModelEntries()) {
            if (!entry.getErrors().isEmpty()) {
                severity = Severity.ERROR_LITERAL;
                break;
            }
            if (severity == null && !entry.getWarnings().isEmpty()) severity = Severity.WARNING_LITERAL;
        }
        String statusText = severity.getName();
        if (severity.getValue() == Severity.ERROR) {
            statusText = statusText + ' ' + STATUS_ERROR_MESSAGE;
        }
        this.statusLabel.setText(statusText);
        this.statusLabel.setImage(VdbEditor.getStatusImage(severity));

        // update the description
        reloadingDescription = true;
        String description = editor.getVdb().getDescription();
        if (description == null) {
            // null string not allowed:
            description = ""; //$NON-NLS-1$
        }

        // only change if different
        if (!description.equals(this.textEditor.getText())) {
            this.textEditor.setText(description);
        }
        reloadingDescription = false;

        setEnabledState();
    }
}
