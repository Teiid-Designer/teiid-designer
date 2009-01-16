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
package com.metamatrix.rose.internal.ui.wizard;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.rose.internal.IMessage;
import com.metamatrix.rose.internal.IUnit;
import com.metamatrix.rose.internal.RoseImporter;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.rose.internal.ui.RoseUiPlugin;
import com.metamatrix.rose.internal.ui.util.RoseImporterUiUtils;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.StatusLabel;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * TargetModelPage
 */
public final class TargetModelPage extends AbstractWizardPage implements IRoseUiConstants, IRoseUiConstants.Images {

    /** Wizard page identifier. */
    public static final String PAGE_ID = TargetModelPage.class.getSimpleName();

    /** Properties key prefix. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(TargetModelPage.class);

    /** Column headers for the table. */
    private static final String[] TBL_HDRS;

    /** Index of the name column in the table. */
    static final int UNIT_COLUMN;

    /** Index of the error column in the table. */
    static final int ERROR_COLUMN;

    /** Index of the folder column in the table. */
    static final int FOLDER_COLUMN;

    /** Index of the model column in the table. */
    static final int MODEL_COLUMN;

    static {
        // set column indexes
        UNIT_COLUMN = 0;
        ERROR_COLUMN = 1;
        FOLDER_COLUMN = 2;
        MODEL_COLUMN = 3;

        // set column headers
        TBL_HDRS = new String[4];
        TBL_HDRS[UNIT_COLUMN] = UTIL.getString(PREFIX + "table.column.unit"); //$NON-NLS-1$
        TBL_HDRS[ERROR_COLUMN] = ""; //$NON-NLS-1$
        TBL_HDRS[FOLDER_COLUMN] = UTIL.getString(PREFIX + "table.column.folder"); //$NON-NLS-1$
        TBL_HDRS[MODEL_COLUMN] = UTIL.getString(PREFIX + "table.column.model"); //$NON-NLS-1$
    }

    private Button btnSelectAll;

    private Button btnSelectChildren;

    private Button btnSyncPaths;

    private Button btnUnselectAll;

    private TargetModelEditor editor;

    private StatusLabel lblStatusMsg;

    private TableViewer viewer;

    private ViewForm viewForm;

    /** Business object for widget. */
    private RoseImporter importer;

    /** Action to show/hide editor. */
    private IAction showEditorAction;

    /**
     * Constructs a <code>TargetModelPage</code> wizard page using the specified business object.
     * 
     * @param theImporter the wizard business object
     */
    public TargetModelPage( RoseImporter theImporter ) {
        super(PAGE_ID, UTIL.getString(PREFIX + "title")); //$NON-NLS-1$

        this.importer = theImporter;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.1
     */
    public void createControl( Composite theParent ) {
        //
        // Create main container
        //

        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        setControl(pnlMain);

        //
        // pnlMain contents
        //

        // target models view form
        this.viewForm = WidgetFactory.createViewForm(pnlMain, SWT.BORDER, GridData.FILL_BOTH, 1);
        this.viewForm.setTopLeft(WidgetFactory.createLabel(this.viewForm, UTIL.getString(PREFIX + "label.targetsViewForm"))); //$NON-NLS-1$
        createTablePanelContents(this.viewForm);

        createTableActions(WidgetFactory.createViewFormToolBar(this.viewForm));

        // editor view form
        this.editor = new TargetModelEditor(pnlMain, this.importer);
        this.editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.editor.addListener(new Listener() {
            public void handleEvent( Event theEvent ) {
                processEditorEvent(theEvent);
            }
        });
        this.editor.setDialogSettings(getDialogSettings());
    }

    private void createTableActions( IToolBarManager theToolBarMgr ) {
        this.showEditorAction = new Action("", IAction.AS_RADIO_BUTTON) { //$NON-NLS-1$
            @Override
            public void run() {
                showEditor(!getEditor().isVisible());
            }
        };

        this.showEditorAction.setImageDescriptor(RoseUiPlugin.getDefault().getImageDescriptor(TARGET_MODEL_EDITOR));
        this.showEditorAction.setToolTipText(UTIL.getString(PREFIX + "button.showEditor.tip")); //$NON-NLS-1$
        this.showEditorAction.setChecked(true);
        theToolBarMgr.add(this.showEditorAction);

        theToolBarMgr.update(true);
    }

    private void createTablePanelContents( ViewForm theViewForm ) {
        final int COLUMNS = 2;
        Composite pnl = WidgetFactory.createPanel(theViewForm, SWT.NONE, GridData.FILL_BOTH, 1, COLUMNS);
        theViewForm.setContent(pnl);

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
        this.viewer = WidgetFactory.createTableViewer(pnl, style);
        this.viewer.setContentProvider(new TargetTableContentProvider());
        this.viewer.setLabelProvider(new TargetTableLabelProvider());
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleRowSelected();
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                handleDoubleClick();
            }
        });

        Table tbl = this.viewer.getTable();
        tbl.setHeaderVisible(true);
        tbl.setLinesVisible(true);

        // create columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            TableColumn col = new TableColumn(tbl, SWT.LEFT);
            col.setText(TBL_HDRS[i]);
            col.pack();

            if (i == ERROR_COLUMN) {
                col.setResizable(false);
                col.setImage(RoseImporterUiUtils.getProblemViewImage());
            }
        }

        // create panel to hold all the buttons
        Composite pnlButtons = WidgetFactory.createPanel(pnl, GridData.VERTICAL_ALIGN_CENTER);

        // label showing selected unit's status message
        this.lblStatusMsg = new StatusLabel(pnl);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = COLUMNS;
        this.lblStatusMsg.setLayoutData(gd);

        //
        // pnlButtons content
        //

        // select all button
        this.btnSelectAll = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.selectAll"), //$NON-NLS-1$
                                                       GridData.FILL_HORIZONTAL);
        this.btnSelectAll.setEnabled(false);
        this.btnSelectAll.setToolTipText(UTIL.getString(PREFIX + "button.selectAll.tip")); //$NON-NLS-1$
        this.btnSelectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSelectAll();
            }
        });

        // unselect all button
        this.btnUnselectAll = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.unselectAll"), //$NON-NLS-1$
                                                         GridData.FILL_HORIZONTAL);
        this.btnUnselectAll.setEnabled(false);
        this.btnUnselectAll.setToolTipText(UTIL.getString(PREFIX + "button.unselectAll.tip")); //$NON-NLS-1$
        this.btnUnselectAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleUnselectAll();
            }
        });

        // select all children button
        this.btnSelectChildren = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.selectAllChildren"), //$NON-NLS-1$
                                                            GridData.FILL_HORIZONTAL);
        this.btnSelectChildren.setEnabled(false);
        this.btnSelectChildren.setToolTipText(UTIL.getString(PREFIX + "button.selectAllChildren.tip")); //$NON-NLS-1$
        this.btnSelectChildren.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSelectAllChildren();
            }
        });

        // create target paths based on unit paths button
        this.btnSyncPaths = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.synchPaths"), //$NON-NLS-1$
                                                       GridData.FILL_HORIZONTAL);
        this.btnSyncPaths.setEnabled(false);
        this.btnSyncPaths.setToolTipText(UTIL.getString(PREFIX + "button.synchPaths.tip")); //$NON-NLS-1$
        this.btnSyncPaths.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSynchronizePaths();
            }
        });
    }

    TargetModelEditor getEditor() {
        return this.editor;
    }

    RoseImporter getImporter() {
        return this.importer;
    }

    void handleDoubleClick() {
        if (!getEditor().isVisible()) {
            showEditor(true);
        }
    }

    void handleRowSelected() {
        // if uncommitted changes exist in editor offer to save prior to setting new editor input
        handleUnsavedEditor();

        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        // enable/disable editor related controls
        boolean doEdit = !selection.isEmpty();
        this.editor.setEnabled(doEdit);

        // enable select children if single selection, has kids, and at least one kid in table
        boolean enable = doEdit;

        if (enable && SelectionUtilities.isSingleSelection(selection)) {
            List kids = ((IUnit)selection.getFirstElement()).getContainedUnits();
            enable = false;

            if (!kids.isEmpty()) {
                // make sure at least one child has been selected. then it appears in table
                for (int size = kids.size(), i = 0; i < size; i++) {
                    if (((IUnit)kids.get(i)).isSelected()) {
                        enable = true;
                        break;
                    }
                }
            }
        } else {
            enable = false;
        }

        this.btnSelectChildren.setEnabled(enable);

        // sync paths button
        updateSyncPathsState();

        // load/clear editor based on selection
        if (doEdit) {
            this.editor.setInput(selection.toList());
        } else {
            this.editor.clear();
        }

        updateStatusLabel();
    }

    void handleSelectAll() {
        IStructuredContentProvider contentProvider = (IStructuredContentProvider)this.viewer.getContentProvider();
        IStructuredSelection selection = new StructuredSelection(contentProvider.getElements(this));
        this.viewer.setSelection(selection);
    }

    void handleSelectAllChildren() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        IUnit unit = (IUnit)selection.getFirstElement();
        List kids = unit.getContainedUnits();

        List newSelection = new ArrayList(kids);
        newSelection.add(unit);

        this.viewer.setSelection(new StructuredSelection(newSelection));
    }

    void handleSynchronizePaths() {
        // show confirm dialog
        if (MessageDialog.openConfirm(getShell(), UTIL.getString(PREFIX + "dialog.confirmSyncPaths.title"), //$NON-NLS-1$
                                      UTIL.getString(PREFIX + "dialog.confirmSyncPaths.message"))) { //$NON-NLS-1$
            IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
            this.importer.synchronizePathsRelativeTo((IUnit)selection.getFirstElement());
            this.viewer.refresh(true);
            updatePageStatus();
        }
    }

    public void handleUnsavedEditor() {
        if (this.editor.isDirty()) {
            String msg = null;
            List editorInput = this.editor.getInput();

            if (editorInput.size() == 1) {
                IUnit unit = (IUnit)editorInput.get(0);
                msg = UTIL.getString(PREFIX + "dialog.dirtyEditor.oneFile.msg", new Object[] {unit.getName()}); //$NON-NLS-1$
            } else {
                msg = UTIL.getString(PREFIX + "dialog.dirtyEditor.multipleFiles.msg"); //$NON-NLS-1$
            }

            if (MessageDialog.openQuestion(getShell(), UTIL.getString(PREFIX + "dialog.dirtyEditor.title"), msg)) { //$NON-NLS-1$
                this.editor.save();
            }
        }
    }

    void handleUnselectAll() {
        this.viewer.setSelection(StructuredSelection.EMPTY);
    }

    /**
     * Default target folder to current workspace selection if applicable
     * 
     * @return if at least one folder has been set
     */
    private boolean initializeTargetFolders() {
        boolean result = false;
        IStructuredSelection selection = UiUtil.getStructuredSelection();

        if (SelectionUtilities.isSingleSelection(selection)
            && !SelectionUtilities.getSelectedIResourceObjects(selection).isEmpty()) {

            Object resource = selection.getFirstElement();

            if (resource instanceof IContainer) {
                IContainer location = (IContainer)resource;
                IProject project = location.getProject();

                // make sure selection is in a model project
                try {
                    if (project.isOpen() && project.hasNature(ModelerCore.NATURE_ID)) {
                        List units = getImporter().getSelectedUnits();

                        // update folders
                        for (int size = units.size(), i = 0; i < size; i++) {
                            IUnit unit = (IUnit)units.get(i);

                            if (unit.getModelFolder() == null) {
                                this.importer.setUnitModelFolder(unit, location);

                                if (!result) {
                                    result = true;
                                }
                            }
                        }
                    }
                } catch (CoreException theException) {
                    UTIL.log(IStatus.ERROR, theException, UTIL.getString(PREFIX + "msg.resourceProblem", //$NON-NLS-1$
                                                                         new Object[] {resource.toString(),
                                                                             resource.getClass().getName()}));
                }
            }
        }

        return result;
    }

    private void packTableColumns() {
        Table tbl = this.viewer.getTable();
        for (int i = 0; i < TBL_HDRS.length; tbl.getColumn(i++).pack()) {

        }
    }

    void processEditorEvent( Event theEvent ) {
        if (theEvent.type == TargetModelEditor.CLOSED) {
            // editor is already closed but calling this method sets the button state to match
            // and layouts the container
            showEditor(false);
        } else {
            if ((theEvent.type == TargetModelEditor.FOLDER_ONLY_SAVED) || (theEvent.type == TargetModelEditor.MODEL_ONLY_SAVED)
                || (theEvent.type == TargetModelEditor.FOLDER_AND_MODEL_SAVED)) {
                // editor was saved
                // refresh each row that was edited. will always have rows selected.
                // can't use the currently selected rows in the viewer for this as they have already changed.
                List editorInput = this.editor.getInput();

                for (int size = editorInput.size(), i = 0; i < size; i++) {
                    this.viewer.refresh(editorInput.get(i), true);
                }

                packTableColumns();

                // update statuses
                updateSyncPathsState();
                updateStatusLabel();
            }
            updatePageStatus();
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        // gets the editor and table initialized along with the page status
        // and makes sure they're are no unsaved changes in the editor
        handleRowSelected();

        if (theShowFlag) {
            boolean enable = !this.importer.getSelectedUnits().isEmpty();
            this.btnSelectAll.setEnabled(enable);
            this.btnUnselectAll.setEnabled(enable);

            this.viewer.setInput(this);

            if (initializeTargetFolders()) {
                this.viewer.refresh();
            }

            updatePageStatus();
            packTableColumns();
        } else {
            this.editor.clear();
        }

        super.setVisible(theShowFlag);
    }

    void showEditor( boolean theShowFlag ) {
        this.showEditorAction.setChecked(theShowFlag);
        this.editor.setVisible(theShowFlag);
        this.editor.getParent().layout();
    }

    private void updatePageStatus() {
        // refresh page message
        String msg = null;
        int msgType = IMessage.UNSPECIFIED;
        IUnit unit = getImporter().getUnitWithMostSevereTargetProblem();

        if (unit == null) {
            msg = UTIL.getString(PREFIX + "msg.targetModelsComplete"); //$NON-NLS-1$
            msgType = IStatus.OK;
        } else {
            msg = unit.getTargetMessage();
            msgType = unit.getTargetStatus();
        }

        if (msgType == IStatus.ERROR) {
            setErrorMessage(msg);
            setPageComplete(false);
        } else {
            final String editorMsg = this.editor.getMessage();
            if (editorMsg != null) {
                setErrorMessage(editorMsg); // must clear error message
                setPageComplete(false);
            } else {
                setErrorMessage(null); // must clear error message
                setMessage(msg, msgType);
                setPageComplete(true);
            }
        }
    }

    private void updateStatusLabel() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        IUnit unit = (selection.isEmpty() || (selection.size() > 1)) ? null : (IUnit)selection.getFirstElement();
        RoseImporterUiUtils.setLabelProperties(unit, this.lblStatusMsg, false);
    }

    private void updateSyncPathsState() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        boolean enable = SelectionUtilities.isSingleSelection(selection);

        if (enable) {
            IUnit unit = (IUnit)selection.getFirstElement();

            // unit exists or it wouldn't have gotten to this wizard page
            enable = (unit.getModelFolder() != null);
        }

        this.btnSyncPaths.setEnabled(enable);
    }

    class TargetTableContentProvider implements IStructuredContentProvider {

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         * @since 4.1
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         * @since 4.1
         */
        public Object[] getElements( Object theInputElement ) {
            return getImporter().getSelectedUnits().toArray();
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         * @since 4.1
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {
        }

    }

    class TargetTableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            Image result = null;

            if (theElement instanceof IUnit) {
                if (theColumnIndex == UNIT_COLUMN) {
                    result = RoseImporterUiUtils.getUnitImage((IUnit)theElement);
                } else if (theColumnIndex == ERROR_COLUMN) {
                    result = RoseImporterUiUtils.getStatusImage((IUnit)theElement, false);
                }
            }

            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 4.1
         */
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = null;

            if (theElement instanceof IUnit) {
                IUnit unit = (IUnit)theElement;

                if (theIndex == UNIT_COLUMN) {
                    result = unit.getQualifiedName();
                } else if (theIndex == ERROR_COLUMN) {
                    result = ""; //$NON-NLS-1$
                } else if (theIndex == FOLDER_COLUMN) {
                    IContainer location = unit.getModelFolder();
                    result = (location == null) ? "" //$NON-NLS-1$
                    : location.getFullPath().makeRelative().toString();
                } else if (theIndex == MODEL_COLUMN) {
                    String modelName = unit.getModelName();
                    result = (modelName == null) ? "" : modelName; //$NON-NLS-1$
                } else {
                    // should not happen
                    Assertion.failed(UTIL.getString(PREFIX + "msg.unknownTableColumn", //$NON-NLS-1$
                                                    new Object[] {Integer.toString(theIndex)}));
                }
            } else {
                // should not happen
                Assertion.failed(UTIL.getString(PREFIX + "msg.unknownObjectType", //$NON-NLS-1$
                                                new Object[] {theElement.getClass().getName()}));
            }

            return result;
        }

    }
}
