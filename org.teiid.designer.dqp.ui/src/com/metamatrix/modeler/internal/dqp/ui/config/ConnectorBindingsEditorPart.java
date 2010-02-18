/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.config;

import java.io.File;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.execution.VdbExecutionValidator;
import com.metamatrix.modeler.dqp.internal.execution.WorkspaceProblemsExecutionValidatorImpl;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.actions.VdbExecutor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.vdb.ui.editor.IVdbEditorPage;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.edit.loader.VDBConstants;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;

/**
 * @since 4.3
 */
public class ConnectorBindingsEditorPart extends EditorPart implements IChangeListener, IVdbEditorPage, DqpUiConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ConnectorBindingsEditorPart.class);

    private static final String TXN_AUTOWRAP_OPTIMISTIC = "OPTIMISTIC"; //$NON-NLS-1$
    private static final String TXN_AUTOWRAP_PESSIMISTIC = "PESSIMISTIC"; //$NON-NLS-1$
    private static final String TXN_AUTOWRAP_OFF = "OFF"; //$NON-NLS-1$
    private static final String[] TXN_AUTOWRAP_OPTIONS = new String[] {TXN_AUTOWRAP_OPTIMISTIC, TXN_AUTOWRAP_PESSIMISTIC,
        TXN_AUTOWRAP_OFF};

    static final String getString( String theKey ) {
        return UTIL.getStringOrKey(PREFIX + theKey);
    }

    Button btnExecute;

    private Combo comboTxnAutoWrap;

    CLabel lblImage;

    StyledText lblStatus;

    private Composite pnlMain;

    private ConnectorBindingsPanel pnlBindings;

    private IChangeListener changeListener;

    protected boolean dirty;

    private VdbExecutor executor;

    protected File vdbFile;

    protected VdbEditingContext vdbContext;

    private boolean executionPropsChanged;

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    public void createPartControl( Composite theParent ) {
        final ScrolledComposite scroller = new ScrolledComposite(theParent, SWT.V_SCROLL | SWT.H_SCROLL);
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

        this.pnlMain = WidgetFactory.createPanel(scroller, SWT.NONE, GridData.FILL_BOTH);
        scroller.setContent(this.pnlMain);

        Display display = this.pnlMain.getDisplay();
        FormToolkit toolkit = null;
        toolkit = new FormToolkit(display);
        toolkit.setBackground(pnlMain.getBackground());

        Group group = WidgetFactory.createGroup(this.pnlMain, getString("statusGroup"), //$NON-NLS-1$
                                                GridData.FILL_HORIZONTAL,
                                                1,
                                                3);
        this.btnExecute = WidgetFactory.createButton(group, getString("btnExecute")); //$NON-NLS-1$
        this.btnExecute.setEnabled(false);
        this.btnExecute.setToolTipText(getString("btnExecute.tip")); //$NON-NLS-1$
        this.btnExecute.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleExecuteSelected();
            }
        });
        this.lblImage = WidgetFactory.createLabel(group);

        this.lblStatus = new StyledText(group, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
        this.lblStatus.setText(getString("lblStatus")); //$NON-NLS-1$
        this.lblStatus.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        this.lblStatus.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.lblStatus.setForeground(UiUtil.getSystemColor(SWT.COLOR_DARK_BLUE));

        group = WidgetFactory.createGroup(this.pnlMain, getString("executionOptionsGroup"), //$NON-NLS-1$
                                          GridData.FILL_HORIZONTAL,
                                          1,
                                          2);

        StyledText lblCombo = new StyledText(group, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
        lblCombo.setText("Transaction AutoWrap: "); //$NON-NLS-1$
        lblCombo.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        this.comboTxnAutoWrap = WidgetFactory.createCombo(group, SWT.READ_ONLY);
        this.comboTxnAutoWrap.setItems(TXN_AUTOWRAP_OPTIONS);
        this.comboTxnAutoWrap.setToolTipText(getString("comboTxnAutoWrap.tip")); //$NON-NLS-1$       

        // Get Transaction AutoWrap state from properties
        String txnAutoWrap = null;
        if (this.vdbContext != null) {
            txnAutoWrap = this.vdbContext.getExecutionProperties().getProperty(VDBConstants.VDBElementNames.ExecutionProperties.Properties.TXN_AUTO_WRAP);
        }

        // Init the combo box (If not already set, use the default)
        if (txnAutoWrap == null) {
            txnAutoWrap = TXN_AUTOWRAP_OPTIMISTIC;
        }
        // Set UI component
        String[] items = this.comboTxnAutoWrap.getItems();
        for (int i = 0; i < items.length; i++) {
            String itemStr = items[i];
            if (itemStr.equalsIgnoreCase(txnAutoWrap)) {
                this.comboTxnAutoWrap.select(i);
                break;
            }
        }

        // Add listener for changes
        this.comboTxnAutoWrap.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleTxnAutoWrapChanged();
            }
        });

        group = WidgetFactory.createGroup(this.pnlMain, getString("bindingsGroup"), //$NON-NLS-1$
                                          GridData.FILL_BOTH);
        if (vdbContext != null && !vdbContext.isReadOnly()) {
            StyledText txtInstructions = new StyledText(group, SWT.WRAP | SWT.READ_ONLY | SWT.MULTI);
            txtInstructions.setText(getString("editInstructionsMsg")); //$NON-NLS-1$
            txtInstructions.setBackground(UiUtil.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            txtInstructions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        this.pnlBindings = null;
        if (this.vdbContext != null) {
            this.pnlBindings = new ConnectorBindingsPanel(group, this.vdbFile, this.vdbContext);
        }

        // ========= GUI finish-up:

        // Size with a fixed width and a bit more than the kids' height:
        Point pt = this.pnlMain.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        int miny = pt.y + 45; // add a little extra to keep all label text visible.
        scroller.setMinWidth(400);
        scroller.setMinHeight(miny);
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);

        this.pnlBindings.addChangeListener(this);
        this.dirty = isDirty();
        updateExecutionStatus();

    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    @Override
    public void doSave( IProgressMonitor theMonitor ) {
        this.pnlBindings.save();
        executionPropsChanged = false;
        this.dirty = isDirty();
        updateExecutionStatus();
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#doSaveAs()
     * @since 4.3
     */
    @Override
    public void doSaveAs() {
    }

    VdbExecutor getExecutor() {
        return this.executor;
    }

    /**
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.IVdbEditorPage#getSelectionListener()
     * @since 4.3
     */
    public ISelectionListener getSelectionListener() {
        return null;
    }

    protected void handleExecuteSelected() {
        // Verify that the connector bindings editor is saved (i.e. save if needed)
        // save any editors that are dirty
        getSite().getShell().getDisplay().syncExec(new Runnable() {
            public void run() {
                IWorkbenchPage page = UiUtil.getWorkbenchPage();
                IEditorPart[] dirtyParts = page.getDirtyEditors();
                if (dirtyParts.length > 0) {
                    for (int i = 0; i < dirtyParts.length; i++) {
                        if (dirtyParts[i].getTitle().indexOf("Connector Bindings") > -1) { //$NON-NLS-1$
                            dirtyParts[i].doSave(new NullProgressMonitor());
                        } else if (dirtyParts[i].getEditorInput().getName().equals(vdbFile.getName())) {
                            dirtyParts[i].doSave(new NullProgressMonitor());
                        }
                    }
                }

                IStatus status = getExecutor().execute(null, false);

                if (status.getCode() != VdbExecutor.LICENSE_PROBLEM_CODE) {
                    ErrorDialog.openError(null, getString("executionProblemDialog.title"), //$NON-NLS-1$
                                          getString("executionProblemDialog.msg"), //$NON-NLS-1$
                                          status);
                }
            }
        });
    }

    /**
     * @since 4.3
     */
    void handleTxnAutoWrapChanged() {
        int selectedIndex = this.comboTxnAutoWrap.getSelectionIndex();
        String txnAutowrapStr = this.comboTxnAutoWrap.getItem(selectedIndex);
        if (txnAutowrapStr.equalsIgnoreCase(TXN_AUTOWRAP_OPTIMISTIC)) {
            MessageDialog.openInformation(null, getString("txnAutoWrapInfoDialog.title"), //$NON-NLS-1$
                                          getString("txnAutoWrapInfoDialog.optimistic.msg")); //$NON-NLS-1$                      
        } else if (txnAutowrapStr.equalsIgnoreCase(TXN_AUTOWRAP_PESSIMISTIC)) {
            MessageDialog.openInformation(null, getString("txnAutoWrapInfoDialog.title"), //$NON-NLS-1$
                                          getString("txnAutoWrapInfoDialog.pessimistic.msg")); //$NON-NLS-1$                      
        } else if (txnAutowrapStr.equalsIgnoreCase(TXN_AUTOWRAP_OFF)) {
            MessageDialog.openInformation(null, getString("txnAutoWrapInfoDialog.title"), //$NON-NLS-1$
                                          getString("txnAutoWrapInfoDialog.off.msg")); //$NON-NLS-1$                      
        }
        if (this.vdbContext != null) {
            this.vdbContext.setExecutionProperty(VDBConstants.VDBElementNames.ExecutionProperties.Properties.TXN_AUTO_WRAP,
                                                 txnAutowrapStr);
        }
        executionPropsChanged = true;
        this.pnlBindings.setFocus();
        this.stateChanged(null);
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
     * @since 4.3
     */
    @Override
    public void init( IEditorSite theSite,
                      IEditorInput theInput ) throws PartInitException {
        super.setInput(theInput);

        // this is called before createPartControl(Composite)
        if (!(theInput instanceof IFileEditorInput) || !ModelUtilities.isVdbFile(((IFileEditorInput)theInput).getFile())
            || !((IFileEditorInput)theInput).getFile().exists()) {
            throw new PartInitException(getString("errorInvalidInput")); //$NON-NLS-1$
        }

        this.vdbFile = ((IFileEditorInput)theInput).getFile().getLocation().toFile();

        super.setSite(theSite);
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isDirty()
     * @since 4.3
     */
    @Override
    public boolean isDirty() {
        return (this.pnlBindings == null) ? false : this.pnlBindings.hasVdbDefnChanges() || executionPropsChanged;
    }

    /**
     * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
     * @since 4.3
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.IVdbEditorPage#preDispose()
     * @since 4.3
     */
    public void preDispose() {
        // Case 5285 - release connections on close
        this.executor.closeAllConnections();

        if ((this.pnlBindings != null) && !this.pnlBindings.isDisposed()) {
            this.pnlBindings.removeChangeListener(this);
        }

        if (this.vdbContext != null) {
            this.vdbContext.removeChangeListener(this.changeListener);
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.IVdbEditorPage#setDisplayName(java.lang.String)
     * @since 4.3
     */
    public void setDisplayName( String theName ) {
        setPartName(theName);
    }

    /**
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     * @since 4.3
     */
    @Override
    public void setFocus() {
        this.pnlBindings.setFocus();
        updateExecutionStatus();
    }

    /**
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.IVdbEditorPage#setVdbEditingContext(com.metamatrix.vdb.edit.VdbEditingContext)
     * @since 4.3
     */
    public void setVdbEditingContext( VdbEditingContext theEditingContext ) {
        Assertion.isNotNull(theEditingContext);
        Assertion.isInstanceOf(theEditingContext, InternalVdbEditingContext.class, getString("vdbStateError")); //$NON-NLS-1$

        this.vdbContext = theEditingContext;
        // setup to listen for changes to context so that we can update the execution state.
        this.changeListener = new IChangeListener() {
            public void stateChanged( IChangeNotifier theSource ) {
                updateExecutionStatus();
            }
        };
        this.vdbContext.addChangeListener(this.changeListener);

        this.executor = new VdbExecutor(this.vdbContext, getValidator());
    }

    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 4.3
     */
    public void stateChanged( IChangeNotifier theSource ) {
        if (!this.dirty) {
            this.dirty = true;
            if (this.vdbContext != null) {
                this.vdbContext.setModified();
            }
            firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
        }

        updateExecutionStatus();
    }

    protected void updateExecutionStatus() {
        if (!btnExecute.isDisposed()) {

            // remove change listener so we don't update after VDB is closed
            if ((this.vdbContext != null) && !this.vdbContext.isOpen()) {
                this.vdbContext.removeChangeListener(this.changeListener);
            }

            final IStatus status = executor.canExecute();

            this.btnExecute.getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (!btnExecute.isDisposed()) {
                        btnExecute.setEnabled(status.getSeverity() != IStatus.ERROR);
                        lblImage.setImage(UiUtil.getStatusImage(status));
                        lblImage.update(); // to redraw image

                        // set status message
                        String msg = null;

                        if (status.getCode() == VdbExecutionValidator.NO_DEF_FILE_ERROR_CODE) {
                            // this is a new VDB. User shouldn't know about DEF files so use another message
                            msg = getString("noDefFileUserMsg"); //$NON-NLS-1$
                        } else {
                            msg = (status.isOK() ? getString("okStatusMsg") : status.getMessage()); //$NON-NLS-1$
                            if (status.isOK()) {
                                lblStatus.setForeground(UiUtil.getSystemColor(SWT.COLOR_DARK_BLUE));
                            } else {
                                lblStatus.setForeground(UiUtil.getSystemColor(SWT.COLOR_DARK_YELLOW));
                            }
                        }

                        lblStatus.setText(msg);
                    }
                }
            });
        }
    }

    /**
     * @see com.metamatrix.modeler.internal.vdb.ui.editor.IVdbEditorPage#updateReadOnlyState(boolean)
     * @since 4.3
     */
    public void updateReadOnlyState( boolean theReadOnlyFlag ) {
        if (this.pnlBindings != null) {
            this.pnlBindings.setReadonly(theReadOnlyFlag);
        }
    }

    protected VdbExecutionValidator getValidator() {
        return new WorkspaceProblemsExecutionValidatorImpl();
    }
}
