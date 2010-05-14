/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.editor;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.EditorPart;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.editors.IRevertable;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.ui.UiConstants.Images;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.viewsupport.StatusInfo;
import com.metamatrix.ui.internal.widget.Dialog;
import com.metamatrix.ui.internal.widget.MessageLabel;
import com.metamatrix.ui.text.ScaledFontManager;
import com.metamatrix.ui.text.StyledTextEditor;

/**
 * The page of the VDB Editor for setting WSDL custom properties.
 * 
 * @since 4.2
 */
public class VdbEditorWsdlPage extends EditorPart
    implements Images, VdbUiConstants, CoreStringUtil.Constants, IRevertable, IGotoMarker, IUndoManager {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(VdbEditorWsdlPage.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$

    private static final String MESSAGE = getString("message"); //$NON-NLS-1$
    private static final String DISABLED_MESSAGE = getString("disableMessage"); //$NON-NLS-1$
    private static final String NAMESPACE_LABEL = getString("namespaceLabel"); //$NON-NLS-1$
    //    private static final String DEFAULT_NAMESPACE_LABEL = getString("defaultNamespaceLabel"); //$NON-NLS-1$
    //    private static final String NONE = getString("none"); //$NON-NLS-1$
    private static final String VIEW_WSDL_MESSAGE = getString("viewWsdlMessage"); //$NON-NLS-1$
    private static final String VIEW_WSDL_BUTTON = getString("viewWsdlButton"); //$NON-NLS-1$
    private static final String INVALID_URI_MESSAGE = getString("invalidUri"); //$NON-NLS-1$
    private static final IStatus INVALID_URI_STATUS = new StatusInfo(UiConstants.PLUGIN_ID, IStatus.ERROR, INVALID_URI_MESSAGE);

    //    private static final String SPACE = "  "; //$NON-NLS-1$

    /**
     * @since 4.2
     */
    static String getString( final String id ) {
        return Util.getString(I18N_PREFIX + id);
    }

    VdbEditor editor;
    // private CLabel defaultNamespaceLabel;
    private MessageLabel statusLabel;
    ScrolledComposite enabledPanel;
    Composite disabledPanel;
    SashForm splitter;

    transient boolean reloading;

    /**
     * Provides text widget with cut, copy, paste, select all, undo, and redo context menu and accelerator key support
     * 
     * @since 5.5.3
     */
    private StyledTextEditor textEditor;

    /**
     * @since 4.2
     */
    public VdbEditorWsdlPage( final VdbEditor editor ) {
        this.editor = editor;
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

    void checkTargetNamespace() {
        setControlState();
        if (resetForReadOnly()) {
            MessageDialog.openWarning(null, getString("readOnlyVDBDialogTitle"), getString("readOnlyVDBDialogMessage")); //$NON-NLS-1$ //$NON-NLS-2$
            reloading = true;
            // this.textEditor.setText(editor.getContext().getVdbWsdlGenerationOptions().getTargetNamespaceUri());
            reloading = false;
            updateDefaultNamespaceLabel();
            return;
        }
        final String uri = this.textEditor.getText();
        if (uri == null || uri.length() == 0) statusLabel.setErrorStatus(null);
        // this.editor.getContext().getVdbWsdlGenerationOptions().setTargetNamespaceUri(null);
        // } else if (this.editor.getContext().getVdbWsdlGenerationOptions().isValidUri(uri)) {
        // statusLabel.setErrorStatus(null);
        // this.editor.getContext().getVdbWsdlGenerationOptions().setTargetNamespaceUri(uri);
        else statusLabel.setErrorStatus(INVALID_URI_STATUS);
        // try {
        // this.editor.getContext().getVdbWsdlGenerationOptions().setTargetNamespaceUri(uri);
        // } catch (final IllegalArgumentException e) {
        // // the uri is invalid. Already set the error status.
        // }
        updateDefaultNamespaceLabel();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.2
     */
    @Override
    public void createPartControl( final Composite parent ) {
        splitter = new SashForm(parent, SWT.VERTICAL);
        final GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(gid);

        // defect 16219 -- add scrollbars:
        enabledPanel = new ScrolledComposite(splitter, SWT.H_SCROLL | SWT.V_SCROLL);
        enabledPanel.setExpandHorizontal(true);
        enabledPanel.setExpandVertical(true);

        // tweak the scroll bars to give better scrolling behavior:
        ScrollBar bar = enabledPanel.getHorizontalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif
        bar = enabledPanel.getVerticalBar();
        if (bar != null) {
            bar.setIncrement(12);
            bar.setPageIncrement(60);
        } // endif

        final Composite pg = WidgetFactory.createPanel(enabledPanel, SWT.NONE, GridData.FILL_BOTH, 1, 2);
        enabledPanel.setContent(pg);

        WidgetFactory.createWrappingLabel(pg, GridData.FILL_HORIZONTAL, 2, MESSAGE);
        WidgetFactory.createWrappingLabel(pg, GridData.FILL_HORIZONTAL, 2, NAMESPACE_LABEL);

        createTextEditor(pg);

        statusLabel = new MessageLabel(pg);
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        statusLabel.setLayoutData(gd);

        // this.defaultNamespaceLabel = WidgetFactory.createLabel(pg, GridData.FILL_HORIZONTAL, 2);

        WidgetFactory.createLabel(pg, GridData.FILL_HORIZONTAL, 2, ""); //$NON-NLS-1$
        WidgetFactory.createLabel(pg, GridData.FILL_HORIZONTAL, 2, VIEW_WSDL_MESSAGE);
        final Button wsdlButton = WidgetFactory.createButton(pg, VIEW_WSDL_BUTTON);
        wsdlButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected( final SelectionEvent e ) {
            }

            public void widgetSelected( final SelectionEvent e ) {
                viewWsdl();
            }
        });

        updateDefaultNamespaceLabel();

        enabledPanel.setMinSize(pg.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        disabledPanel = WidgetFactory.createPanel(splitter, SWT.NONE, GridData.FILL_BOTH, 1, 2);
        WidgetFactory.createLabel(disabledPanel, GridData.FILL_HORIZONTAL, 2, ""); //$NON-NLS-1$
        WidgetFactory.createWrappingLabel(disabledPanel, GridData.FILL_HORIZONTAL, 2, DISABLED_MESSAGE);

        setControlState();
    }

    /**
     * @param parent the parent of the text widget
     * @since 5.5.3
     */
    private void createTextEditor( final Composite parent ) {
        this.textEditor = new StyledTextEditor(parent, SWT.BORDER);

        this.textEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        // this.textEditor.setText(editor.getContext().getVdbWsdlGenerationOptions().getTargetNamespaceUri());

        // don't let the initial text be undone
        this.textEditor.resetUndoRedoHistory();

        // defect 15915 -- listen for more than just key events.
        // note: add listener placed after setText for a reason!
        this.textEditor.getDocument().addDocumentListener(new IDocumentListener() {
            /**
             * @see org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged(org.eclipse.jface.text.DocumentEvent)
             * @since 5.5
             */
            public void documentAboutToBeChanged( final DocumentEvent event ) {
            }

            public void documentChanged( final DocumentEvent event ) {
                if (!reloading) checkTargetNamespace();
            }
        });
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     * @since 4.3
     */
    @Override
    public void dispose() {
        this.textEditor.dispose();
        super.dispose();
    }

    public void doRevertToSaved() {
        // defect 18303 - make sure open and visible:
        if (editor.getVdb() == null || disabledPanel == null || disabledPanel.isDisposed()) return;

        reloading = true;
        setControlState(); // note that this already runs an asyncExec
        // final VdbWsdlGenerationOptions vwgo = editor.getContext().getVdbWsdlGenerationOptions();
        // this.textEditor.setText(vwgo.getTargetNamespaceUri());
        updateDefaultNamespaceLabel();
        reloading = false;
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.2
     */
    @Override
    public void doSave( final IProgressMonitor monitor ) {
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.ui.ISaveablePart#doSaveAs()
     * @since 4.2
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(java.lang.Class)
     * @since 5.5.3
     */
    @Override
    public Object getAdapter( final Class adapter ) {
        if (adapter.equals(IFindReplaceTarget.class) && this.textEditor.getTextWidget().isFocusControl()) return this.textEditor.getTextViewer().getFindReplaceTarget();

        if (adapter.equals(IUndoManager.class)) {
            if (this.textEditor.getTextWidget().isFocusControl()) return this;

            return null;
        }

        return super.getAdapter(adapter);
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getRedoLabel()
     * @since 5.5
     */
    public String getRedoLabel() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbEditorWsdlPage.class) + "redoLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getUndoLabel()
     * @since 5.5
     */
    public String getUndoLabel() {
        return Util.getString(I18nUtil.getPropertyPrefix(VdbEditorWsdlPage.class) + "undoLabel"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.ide.IGotoMarker#gotoMarker(org.eclipse.core.resources.IMarker)
     */
    public void gotoMarker( final IMarker marker ) {
    }

    /**
     * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 4.2
     */
    @Override
    public void init( final IEditorSite site,
                      final IEditorInput input ) {
        setSite(site);
        setInput(input);
        setPartName(TITLE);
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isDirty()
     * @since 4.2
     */
    @Override
    public boolean isDirty() {
        return this.editor.getVdb().isModified();
    }

    /**
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     * @since 4.2
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#redo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5
     */
    public void redo( final IProgressMonitor monitor ) {
        this.textEditor.getUndoManager().redo();
        monitor.done();
    }

    private boolean resetForReadOnly() {
        final boolean isReadOnly = false;
        // if (this.editor.getVdb() != null) isReadOnly = this.editor.getContext().isReadOnly();

        return isReadOnly;
    }

    private void setControlState() {
        getSite().getShell().getDisplay().asyncExec(new Runnable() {

            public void run() {
                // final VdbWsdlGenerationOptions options = editor.getContext().getVdbWsdlGenerationOptions();
                // if (options != null && options.canWsdlBeGenerated()) {
                // disabledPanel.setVisible(false);
                // enabledPanel.setVisible(true);
                // } else {
                // disabledPanel.setVisible(true);
                // enabledPanel.setVisible(false);
                // }
                splitter.layout();

                final boolean isEnabled = !((IFileEditorInput)editor.getEditorInput()).getFile().isReadOnly();
                accessTextEditor().getTextWidget().setEnabled(isEnabled);
                if (isEnabled) accessTextEditor().setBackground(UiUtil.getSystemColor(SWT.COLOR_WHITE));
                else accessTextEditor().setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            }
        });
    }

    /**
     * Checks the file state and sets the controls
     * 
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     * @since 4.2
     */
    @Override
    public void setFocus() {
        setControlState();
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#undo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5
     */
    public void undo( final IProgressMonitor monitor ) {
        this.textEditor.getUndoManager().undo();
        monitor.done();
    }

    private void updateDefaultNamespaceLabel() {
        // final String defaultNS_URI = this.editor.getContext().getVdbWsdlGenerationOptions().getDefaultNamespaceUri();
        // if (defaultNS_URI != null && defaultNS_URI.length() > 0) this.defaultNamespaceLabel.setText(DEFAULT_NAMESPACE_LABEL +
        // SPACE
        // + defaultNS_URI);
        // else this.defaultNamespaceLabel.setText(DEFAULT_NAMESPACE_LABEL + SPACE + NONE);
    }

    void viewWsdl() {
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {

            @Override
            public void execute( final IProgressMonitor theMonitor ) throws InvocationTargetException {
                try {
                    // final String wsdl = editor.getContext().getVdbWsdlGenerationOptions().getWsdlAsString(theMonitor);
                    editor.getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {

                        public void run() {
                            // new WsdlDialog(editor.getSite().getShell(), wsdl).open();
                        }
                    });
                } catch (final Exception err) {
                    editor.getEditorSite().getShell().getDisplay().asyncExec(new Runnable() {

                        public void run() {
                            MessageDialog.openError(editor.getSite().getShell(), getString("errorTitle"), getString("errorMessage")); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                    });
                    throw new InvocationTargetException(err);
                }
            }
        };
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
        } catch (final InterruptedException e) {
        } catch (final InvocationTargetException e) {
            VdbUiConstants.Util.log(e.getTargetException());
        }
    }
}

class WsdlDialog extends Dialog {

    private static final String TITLE = VdbUiConstants.Util.getString("VdbEditorWsdlPage.WsdlDialog.title"); //$NON-NLS-1$

    private StyledText text;
    private final String wsdlString;

    /**
     * Construct an instance of ModelStatisticsDialog.
     */
    public WsdlDialog( final Shell shell,
                       final String wsdlString ) {
        super(shell, TITLE);
        this.wsdlString = wsdlString;
    }

    /**
     * @see org.eclipse.jface.window.Window#create()
     */
    @Override
    public void create() {
        setShellStyle(getShellStyle() | SWT.RESIZE);
        super.create();
        super.getShell().setText(TITLE);
    }

    /**
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar( final Composite parent ) {
        final Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        okButton.setFocus();
    }

    /**
     * @see org.eclipse.jface.window.Window#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea( final Composite parent ) {
        final Composite composite = (Composite)super.createDialogArea(parent);
        // add controls to composite as necessary

        text = new StyledText(composite, SWT.V_SCROLL);
        final GridData gd = new GridData(GridData.FILL_BOTH);
        text.setLayoutData(gd);

        text.setEditable(false);
        text.setWordWrap(true);
        text.setTabs(4);

        final StyleRange bodyRange = new StyleRange();
        bodyRange.start = 0;
        bodyRange.length = wsdlString.length();
        final ScaledFontManager fontManager = new ScaledFontManager();
        text.setFont(fontManager.createFontOfSize(10));

        text.setText(wsdlString);
        text.setStyleRange(bodyRange);

        super.setSizeRelativeToScreen(75, 70);

        return composite;
    }
}
