/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.draw2d.EventListenerList;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;
import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.NotificationUtilities;
import com.metamatrix.modeler.core.query.QueryValidator;
import com.metamatrix.modeler.core.query.SetQueryUtil;
import com.metamatrix.modeler.core.transaction.SourcedNotification;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.DiagramActionService;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.transformation.util.SqlMappingRootCache;
import com.metamatrix.modeler.internal.transformation.util.SqlTransformationStatusChangeEvent;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationMappingHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.Messages;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.actions.EditTransformationAction;
import com.metamatrix.modeler.transformation.ui.actions.ITransformationDiagramActionConstants;
import com.metamatrix.modeler.transformation.ui.actions.ReconcileTransformationAction;
import com.metamatrix.modeler.transformation.ui.builder.criteria.QueryCriteriaStrategy;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlEditorEvent;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlEditorPanel;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlEditorPanelWrapper;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.actions.ToggleOptimizer;
import com.metamatrix.modeler.transformation.ui.search.OpenTransformationSearchPageAction2;
import com.metamatrix.modeler.transformation.ui.util.BuilderTreeProvider;
import com.metamatrix.modeler.transformation.ui.util.TransformationUiResourceHelper;
import com.metamatrix.modeler.transformation.validation.SqlTransformationResult;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.actions.ModelerSpecialActionManager;
import com.metamatrix.modeler.ui.actions.SortableSelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.undo.IUndoManager;
import com.metamatrix.modeler.ui.viewsupport.StatusBarUpdater;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Label;

/**
 * TransformationObjectEditorPage is the class for editing Transformation Objects.
 */
public class TransformationObjectEditorPage
    implements ModelObjectEditorPage, UiConstants, ITransformationDiagramActionConstants, IAdaptable, EventObjectListener,
    INotifyChangedListener, SelectionListener, IUndoManager {

    private static final String SQL_UPDATE_TXN_DESCRIPTION = getString("sqlUpdateTxnDescription"); //$NON-NLS-1$

    private static final String BLANK = ""; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String COMMA_SPACE = ", "; //$NON-NLS-1$
    private static final String RIGHT_PARENTH = ")"; //$NON-NLS-1$
    private static final String NAME = getString("name"); //$NON-NLS-1$
    private static final String TITLE_TEXT = "title.text"; //$NON-NLS-1$
    private static final String TITLE_TOOLTIP = "title.toolTip"; //$NON-NLS-1$
    private static final String SELECT_TAB_TEXT = getString("selectTab.text"); //$NON-NLS-1$
    private static final String SELECT_TAB_TOOLTIP = getString("selectTab.toolTip"); //$NON-NLS-1$
    private static final String UPDATE_TAB_TEXT = getString("updateTab.text"); //$NON-NLS-1$
    private static final String UPDATE_TAB_TOOLTIP = getString("updateTab.toolTip"); //$NON-NLS-1$

    private static final String SELECT_SQL_MSG = getString("selectSqlMsg.text"); //$NON-NLS-1$
    private static final String INSERT_SQL_MSG = getString("insertSqlMsg.text"); //$NON-NLS-1$
    private static final String UPDATE_SQL_MSG = getString("updateSqlMsg.text"); //$NON-NLS-1$
    private static final String DELETE_SQL_MSG = getString("deleteSqlMsg.text"); //$NON-NLS-1$
    private static final String IS_VALID_MSG = getString("isValidMsg.text"); //$NON-NLS-1$

    private static final String INSERT_TAB_TEXT = getString("insertTab.text"); //$NON-NLS-1$
    private static final String INSERT_TAB_TOOLTIP = getString("insertTab.toolTip"); //$NON-NLS-1$

    private static final String DELETE_TAB_TEXT = getString("deleteTab.text"); //$NON-NLS-1$
    private static final String DELETE_TAB_TOOLTIP = getString("deleteTab.toolTip"); //$NON-NLS-1$

    private static final String SAVE_PENDING_TITLE = getString("savePendingChanges.title"); //$NON-NLS-1$
    private static final String SAVE_PENDING_MSG = getString("savePendingChanges.msg"); //$NON-NLS-1$
    private static final String SQL_CHANGES_PENDING_MSG = getString("sqlChangesPending.msg"); //$NON-NLS-1$

    private static final String USE_DEFAULT_CHECKBOX_TEXT = getString("useDefaultCheckbox.text"); //$NON-NLS-1$
    private static final String USE_DEFAULT_CHECKBOX_TOOLTIP = getString("useDefaultCheckbox.toolTip"); //$NON-NLS-1$

    // strings for warning message
    private static final String UPDATE_SQL_TYPE = getString("update.sqlType"); //$NON-NLS-1$
    private static final String INSERT_SQL_TYPE = getString("insert.sqlType"); //$NON-NLS-1$
    private static final String DELETE_SQL_TYPE = getString("delete.sqlType"); //$NON-NLS-1$

    private static final String LOST_SQL_TITLE = getString("lostSql.title"); //$NON-NLS-1$

    private static final String IS_VALID_AND_RECONCILABLE = getString("isValidAndReconcilableMsg"); //$NON-NLS-1$
    private static final String IS_VALID_NOT_RECONCILABLE = getString("isValidNotReconcilableMsg"); //$NON-NLS-1$
    private static final String QUERY_SIZE_MISMATCH_MSG = getString("querySizeMismatchMsg"); //$NON-NLS-1$
    private static final String QUERY_SIZE_MISMATCH_NO_PROJECTED_SYMBOLS_MSG = getString("querySizeMismatchNoProjectedSymbolsMsg"); //$NON-NLS-1$
    private static final String QUERY_NAME_MISMATCH_MSG = getString("queryNameMismatchMsg"); //$NON-NLS-1$
    private static final String QUERY_TYPE_MISMATCH_MSG = getString("queryTypeMismatchMsg"); //$NON-NLS-1$
    private static final String COMMAND_HAS_REFERENCES_MSG = getString("commandHasReferencesMsg"); //$NON-NLS-1$
    private static final String NUMBER_REFERENCES_MSG = getString("numberReferencesMsg"); //$NON-NLS-1$

    private static final String TRANSFORMATION_AUTO_ADJUSTED_MSG = getString("transformationAutoAdjusted"); //$NON-NLS-1$
    static final String SUPPORTS_UPDATE_TEXT = getString("supportsUpdatesCheckBox.text"); //$NON-NLS-1$
    private static final String CURSOR_AT_TEXT = getString("cursorAt.text"); //$NON-NLS-1$
    private static final String PREVIEW_DATA_TOOLTIP = getString("previewDataTooltip"); //$NON-NLS-1$

    public static final String TRANSACTIONS = "modelerTransactions"; //$NON-NLS-1$
    public static final String THIS_CLASS = "TransformationObjectEditorPage"; //$NON-NLS-1$
    
    private static final Image ERROR_IMAGE = UiPlugin.getDefault().getImage(UiConstants.Images.ERROR);
    private static final Image WARNING_IMAGE = UiPlugin.getDefault().getImage(UiConstants.Images.WARNING);
    private static final Image NOT_ALLOWED_IMAGE = UiPlugin.getDefault().getImage(UiConstants.Images.NOT_ALLOWED);

    private static String getString( String key ) {
        return UiConstants.Util.getString(THIS_CLASS + '.' + key);
    }

    private static String getString( String key,
                                     String parameter ) {
        return UiConstants.Util.getString(THIS_CLASS + '.' + key, parameter);
    }

    SqlEditorPanelWrapper sqlEditorPanelWrapper;
    private QueryValidator validator;

    private Composite parent;
    private CTabFolder tabFolderWithUpdateTabs;

    Composite objEditorParent;
    private StackLayout objEditorParentLayout;

    private Composite sqlSelectPanelForUpdate;
    private Composite sqlSelectPanelForNoUpdate;

    private Composite sqlOuterUpdatePanel;
    private Composite sqlOuterInsertPanel;
    private Composite sqlOuterDeletePanel;

    private Object currentItem;

    private CTabItem selectTabForUpdate;
    CTabItem insertTab;
    CTabItem updateTab;
    CTabItem deleteTab;

    private SqlEditorPanel currentSqlEditor;

    private SqlEditorPanel sqlSelectEditorForUpdate;
    private SqlEditorPanel sqlSelectEditorForNoUpdate;
    private SqlEditorPanel sqlInsertEditor;
    private SqlEditorPanel sqlUpdateEditor;
    private SqlEditorPanel sqlDeleteEditor;

    private SqlPanelDropTargetListener seSelectUpdateDropListener;
    private SqlPanelDropTargetListener seSelectNoUpdateDropListener;

    Button chkUseDefaultForInsert;
    Label useDefaultForInsertLabel;
    Button chkUseDefaultForUpdate;
    Label useDefaultForUpdateLabel;
    Button chkUseDefaultForDelete;
    Label useDefaultForDeleteLabel;

    private boolean bUseDefaultForInsert;
    private boolean bUseDefaultForUpdate;
    private boolean bUseDefaultForDelete;

    private boolean targetAllowsUpdates = false;

    private SqlTransformationMappingRoot currentMappingRoot;

    private boolean isDirty = false;

    private ModelObjectEditorPage override;
    
    private ModelEditor parentModelEditor;

    /** List of listeners registered for this panels events */
    private List eventListeners;

    private EventListenerList propListeners = new EventListenerList();

    // Actions and other Toolbar controls
    CheckBoxContribution chkSupportsUpdatesContribution;
    CLabel cursorPositionLabel;
    private LabelContribution lblCursorPositionContribution;
    private ToggleOptimizer toggleOptimizerAction;
    private SortableSelectionAction previewDataAction;
    private Action searchTransformationsAction;
    private EditTransformationAction editTransformationAction;

    private boolean noUpdatesAllowed = false;
    private boolean currentReadonlyState = false;
    
    private boolean deactivated = true;

    // SelectionListener for CheckBox controls
    private SelectionListener checkBoxListener = new SelectionListener() {
        public void widgetDefaultSelected( SelectionEvent e ) {
            handleCheckBoxStateChanged(e);
        }

        public void widgetSelected( SelectionEvent e ) {
            handleCheckBoxStateChanged(e);
        }
    };

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canClose()
     * @since 5.0.1
     */
    public boolean canClose() {
        return true;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {

        this.parent = parent;

        objEditorParent = new Composite(parent, SWT.NONE);
        objEditorParentLayout = new StackLayout();

        objEditorParent.setLayout(objEditorParentLayout);

        GridData gridData0 = new GridData(GridData.FILL_BOTH);

        objEditorParent.setLayoutData(gridData0);

        createCTabFolderWithUpdateTabs(objEditorParent);
        createSqlControl(objEditorParent);

        GridData gridData2 = new GridData(GridData.FILL_BOTH);
        if (tabFolderWithUpdateTabs != null) {
            tabFolderWithUpdateTabs.setLayoutData(gridData2);
        }

        objEditorParentLayout.topControl = sqlSelectPanelForNoUpdate;

        // establish listening
        registerListeners();
    }

    private Object getTopControl() {
        return objEditorParentLayout.topControl;
    }

    public ModelEditor getParentModelEditor() {
    	return this.parentModelEditor;
    }

    private void createSqlControl( Composite parent ) {
        createSelectControlForNoUpdate(parent);
        setCurrentSqlEditor(sqlSelectEditorForNoUpdate);
    }

    private void createCTabFolderWithUpdateTabs( Composite parent ) {
        if (!allowsMultipleTabs()) {
            return;
        }

        tabFolderWithUpdateTabs = new CTabFolder(parent, SWT.BOTTOM);

        // --------------------------------------------
        // Create Tabs
        // --------------------------------------------
        createSelectTabForUpdate(tabFolderWithUpdateTabs);

        createUpdateTab(tabFolderWithUpdateTabs);
        createInsertTab(tabFolderWithUpdateTabs);
        createDeleteTab(tabFolderWithUpdateTabs);
        tabFolderWithUpdateTabs.setSelection(selectTabForUpdate);

        tabFolderWithUpdateTabs.setLayoutData(createTextViewGridData());

        addCheckBoxListeners(selectTabForUpdate);
    }

    /**
     * Create the Select Tab
     * 
     * @param tabFolder the CTabFolder parent
     */
    private void createSelectTabForUpdate( CTabFolder tabFolder ) {
        // Set overall grid layout
        GridLayout glOuterGridLayout = new GridLayout();

        // build the SELECT tab
        sqlSelectPanelForUpdate = new Composite(tabFolder, SWT.NONE);
        sqlSelectPanelForUpdate.setLayout(glOuterGridLayout);
        sqlSelectPanelForUpdate.setLayoutData(createTextViewGridData());
        sqlSelectEditorForUpdate = createEditorPanel(sqlSelectPanelForUpdate, QueryValidator.SELECT_TRNS);
        sqlSelectEditorForUpdate.setPanelType(UiConstants.SQLPanels.UPDATE_SELECT);

        sqlSelectEditorForUpdate.setEditable(true);

        selectTabForUpdate = new CTabItem(tabFolder, SWT.NONE);
        selectTabForUpdate.setControl(sqlSelectPanelForUpdate);
        selectTabForUpdate.setText(SELECT_TAB_TEXT);
        selectTabForUpdate.setToolTipText(SELECT_TAB_TOOLTIP);

        seSelectUpdateDropListener = createDropTargetListener(sqlSelectEditorForUpdate, currentMappingRoot);

        if (seSelectUpdateDropListener != null) {

            DropTarget dropTarget = new DropTarget(sqlSelectEditorForUpdate.getTextViewer().getTextWidget(), DND.DROP_COPY
                                                                                                             | DND.DROP_MOVE);
            dropTarget.setTransfer(seSelectUpdateDropListener.getTransfers());
            dropTarget.addDropListener(seSelectUpdateDropListener);
        }

        // remove the undo caused by setting the text the first time
        resetUndoManager(sqlSelectEditorForUpdate);
    }

    /**
     * Create the Select control
     * 
     * @param parent the parent
     */
    private void createSelectControlForNoUpdate( Composite parent ) {
        // Set overall grid layout
        GridLayout glOuterGridLayout = new GridLayout();

        // build the SELECT tab
        sqlSelectPanelForNoUpdate = new Composite(parent, SWT.NONE);
        sqlSelectPanelForNoUpdate.setLayout(glOuterGridLayout);
        sqlSelectPanelForNoUpdate.setLayoutData(createTextViewGridData());
        sqlSelectEditorForNoUpdate = createSelectEditorForNoUpdate(this.sqlSelectPanelForNoUpdate);
        sqlSelectEditorForNoUpdate.setPanelType(UiConstants.SQLPanels.SELECT);
        sqlSelectEditorForNoUpdate.setEditable(true);
        seSelectNoUpdateDropListener = createDropTargetListener(sqlSelectEditorForNoUpdate, currentMappingRoot);

        if (seSelectNoUpdateDropListener != null) {

            DropTarget dropTarget = new DropTarget(sqlSelectEditorForNoUpdate.getTextViewer().getTextWidget(), DND.DROP_COPY
                                                                                                               | DND.DROP_MOVE);
            dropTarget.setTransfer(seSelectNoUpdateDropListener.getTransfers());
            dropTarget.addDropListener(seSelectNoUpdateDropListener);
        }

        // remove the undo caused by setting the text the first time
        resetUndoManager(sqlSelectEditorForNoUpdate);
    }

    protected SqlEditorPanel createSelectEditorForNoUpdate( Composite parent ) {
        return createEditorPanel(parent, QueryValidator.SELECT_TRNS);
    }

    /**
     * Create the Update Tab
     * 
     * @param tabFolder the CTabFolder parent
     */
    private void createUpdateTab( CTabFolder tabFolder ) {
        GridLayout glEditorGridLayout = new GridLayout();
        glEditorGridLayout.numColumns = 2;

        // create a composite to hold the controls and the textviewer
        sqlOuterUpdatePanel = new Composite(tabFolder, SWT.NONE);
        sqlOuterUpdatePanel.setLayout(glEditorGridLayout);
        sqlOuterUpdatePanel.setLayoutData(createTextViewGridData());

        // create/add the controls
        chkUseDefaultForUpdate = WidgetFactory.createCheckBox(sqlOuterUpdatePanel, USE_DEFAULT_CHECKBOX_TEXT, true);
        chkUseDefaultForUpdate.setToolTipText(USE_DEFAULT_CHECKBOX_TOOLTIP);
        useDefaultForUpdateLabel = WidgetFactory.createLabel(sqlOuterUpdatePanel);
        useDefaultForUpdateLabel.setText(Messages.DefaultUpdateMessageOK);

        sqlUpdateEditor = createEditorPanel(sqlOuterUpdatePanel, QueryValidator.UPDATE_TRNS);
        sqlUpdateEditor.setPanelType(UiConstants.SQLPanels.UPDATE_UPDATE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        sqlUpdateEditor.setLayoutData(gd);

        updateTab = new CTabItem(tabFolder, SWT.NONE);
        updateTab.setControl(sqlOuterUpdatePanel);

        if (TransformationHelper.isUpdateAllowed(currentMappingRoot)) {
            updateTab.getControl().setEnabled(true);

            // try this:
            sqlUpdateEditor.setEditable(true);
            sqlUpdateEditor.getTextViewer().getTextWidget().setEnabled(true);

            new DropTarget(sqlUpdateEditor.getTextViewer().getTextWidget(), DND.DROP_NONE);
        }

        updateTab.setText(UPDATE_TAB_TEXT);
        updateTab.setToolTipText(UPDATE_TAB_TOOLTIP);

        // remove the undo caused by setting the text the first time
        resetUndoManager(sqlUpdateEditor);
    }

    /**
     * Create the Insert Tab
     * 
     * @param tabFolder the CTabFolder parent
     */
    private void createInsertTab( CTabFolder tabFolder ) {
        GridLayout glEditorGridLayout = new GridLayout();
        glEditorGridLayout.numColumns = 2;
        // create a composite to hold the controls and the textviewer
        sqlOuterInsertPanel = new Composite(tabFolder, SWT.NONE);
        sqlOuterInsertPanel.setLayout(glEditorGridLayout);

        sqlOuterInsertPanel.setLayoutData(createTextViewGridData());

        // create/add the controls
        chkUseDefaultForInsert = WidgetFactory.createCheckBox(sqlOuterInsertPanel, USE_DEFAULT_CHECKBOX_TEXT, true);
        chkUseDefaultForInsert.setToolTipText(USE_DEFAULT_CHECKBOX_TOOLTIP);
        useDefaultForInsertLabel = WidgetFactory.createLabel(sqlOuterInsertPanel);
        useDefaultForInsertLabel.setText(Messages.DefaultUpdateMessageOK);

        sqlInsertEditor = createEditorPanel(sqlOuterInsertPanel, QueryValidator.INSERT_TRNS);
        sqlInsertEditor.setPanelType(UiConstants.SQLPanels.UPDATE_INSERT);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        sqlInsertEditor.setLayoutData(gd);

        insertTab = new CTabItem(tabFolder, SWT.NONE);
        insertTab.setControl(sqlOuterInsertPanel);

        if (TransformationHelper.isInsertAllowed(currentMappingRoot)) {
            insertTab.getControl().setEnabled(true);
            sqlInsertEditor.setEditable(true);
            sqlInsertEditor.getTextViewer().getTextWidget().setEnabled(true);

            new DropTarget(sqlInsertEditor.getTextViewer().getTextWidget(), DND.DROP_NONE);
        }

        insertTab.setText(INSERT_TAB_TEXT);
        insertTab.setToolTipText(INSERT_TAB_TOOLTIP);

        // remove the undo caused by setting the text the first time
        resetUndoManager(sqlInsertEditor);
    }

    /**
     * Create the Delete Tab
     * 
     * @param tabFolder the CTabFolder parent
     */
    private void createDeleteTab( CTabFolder tabFolder ) {
        GridLayout glEditorGridLayout = new GridLayout();
        glEditorGridLayout.numColumns = 2;
        // create a composite to hold the controls and the textviewer
        sqlOuterDeletePanel = new Composite(tabFolder, SWT.NONE);
        sqlOuterDeletePanel.setLayout(glEditorGridLayout);

        sqlOuterDeletePanel.setLayoutData(createTextViewGridData());

        // create/add the controls
        chkUseDefaultForDelete = WidgetFactory.createCheckBox(sqlOuterDeletePanel, USE_DEFAULT_CHECKBOX_TEXT, true);
        chkUseDefaultForDelete.setToolTipText(USE_DEFAULT_CHECKBOX_TOOLTIP);
        useDefaultForDeleteLabel = WidgetFactory.createLabel(sqlOuterDeletePanel);
        useDefaultForDeleteLabel.setText(Messages.DefaultUpdateMessageOK);

        sqlDeleteEditor = createEditorPanel(sqlOuterDeletePanel, QueryValidator.DELETE_TRNS);
        sqlDeleteEditor.setPanelType(UiConstants.SQLPanels.UPDATE_DELETE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        sqlDeleteEditor.setLayoutData(gd);

        deleteTab = new CTabItem(tabFolder, SWT.NONE);
        deleteTab.setControl(sqlOuterDeletePanel);

        if (TransformationHelper.isDeleteAllowed(currentMappingRoot)) {
            deleteTab.getControl().setEnabled(true);
            sqlDeleteEditor.getTextViewer().setEditable(false);

            sqlDeleteEditor.setEditable(false);

            sqlDeleteEditor.getTextViewer().getTextWidget().setEnabled(false);

            new DropTarget(sqlDeleteEditor.getTextViewer().getTextWidget(), DND.DROP_NONE);
        }
        
        getSqlEditorForItem(deleteTab).setEditable(false);

        deleteTab.setText(DELETE_TAB_TEXT);
        deleteTab.setToolTipText(DELETE_TAB_TOOLTIP);

        // remove the undo caused by setting the text the first time
        resetUndoManager(sqlDeleteEditor);
    }

    /**
     * Create a SqlEditorPanel in the supplied Composite.
     * 
     * @param cmpSqlPanel the composite that it is contained in
     * @return the SqlEditorPanel
     */
    protected SqlEditorPanel createEditorPanel( Composite cmpSqlPanel,
                                                int queryType ) {

        List aList = SqlEditorPanel.getDefaultActionList();
        customizeActionList(aList);

        SqlEditorPanel sepNewSqlEditor = new SqlEditorPanel(cmpSqlPanel, validator, queryType, aList);

        sepNewSqlEditor.setLayoutData(createTextViewGridData());

        // add listening
        sepNewSqlEditor.addEventListener(this);

        return sepNewSqlEditor;
    }

    /**
     * Create GridData for Text Panel
     * 
     * @return GridData
     */
    private GridData createTextViewGridData() {
        GridData gridData = new GridData(GridData.FILL_BOTH);

        return gridData;
    }

    /**
     * Register this class to listen for Metadata Change Notifications and Tab Selections
     */
    private void registerListeners() {
    	deactivated = false;
        ModelUtilities.addNotifyChangedListener(this);
        SqlMappingRootCache.addEventListener(this);
        if (tabFolderWithUpdateTabs != null) {
            tabFolderWithUpdateTabs.addSelectionListener(this);
        }
    }

    /**
     * Remove the checkBox listener from the checkBox controls
     */
    private void removeCheckBoxListeners( Object item ) {
        if (item == insertTab) {
            if (!chkUseDefaultForInsert.isDisposed()) {
                chkUseDefaultForInsert.removeSelectionListener(checkBoxListener);
            }
        } else if (item == updateTab) {
            if (!chkUseDefaultForUpdate.isDisposed()) {
                chkUseDefaultForUpdate.removeSelectionListener(checkBoxListener);
            }
        } else if (item == deleteTab) {
            if (!chkUseDefaultForDelete.isDisposed()) {
                chkUseDefaultForDelete.removeSelectionListener(checkBoxListener);
            }
        }
    }

    /**
     * Add the checkBox listener to all checkBox controls
     */
    private void addCheckBoxListeners( Object item ) {
        if (item == insertTab) {
            if (!chkUseDefaultForInsert.isDisposed()) {
                chkUseDefaultForInsert.addSelectionListener(checkBoxListener);
            }
        } else if (item == updateTab) {
            if (!chkUseDefaultForUpdate.isDisposed()) {
                chkUseDefaultForUpdate.addSelectionListener(checkBoxListener);
            }
        } else if (item == deleteTab) {
            if (!chkUseDefaultForDelete.isDisposed()) {
                chkUseDefaultForDelete.addSelectionListener(checkBoxListener);
            }
        }
    }

    protected void showMessageArea( boolean doShow ) {
        getSqlEditorPanelWrapper().showMessageArea(doShow);
    }

    /**
     * Get the SqlEditorPanel wrapper
     * 
     * @return the SqlEditorPanelWrapper
     */
    private SqlEditorPanelWrapper getSqlEditorPanelWrapper() {
        // create the editor panel wrapper:
        if (sqlEditorPanelWrapper == null) {
            List aList = SqlEditorPanel.getDefaultActionList();
            customizeActionList(aList);
            sqlEditorPanelWrapper = new SqlEditorPanelWrapper(parent, null, aList);

            sqlEditorPanelWrapper.setCurrentSqlEditorPanel(getCurrentSqlEditor());
        }

        Display display = this.getControl().getDisplay();
        if (display != null) {
            display.asyncExec(new Runnable() {
                public void run() {
                    sqlEditorPanelWrapper.refreshFontManager();
                }
            });
        }

        return sqlEditorPanelWrapper;
    }

    /**
     * Set the current SqlEditorPanel
     * 
     * @return the current SqlEditorPanel
     */
    private void setCurrentSqlEditor( SqlEditorPanel sep ) {

        if (this.currentSqlEditor != sep) {
            this.currentSqlEditor = sep;
            getSqlEditorPanelWrapper().setCurrentSqlEditorPanel(currentSqlEditor);
        }
    }

    /**
     * Get the current SqlEditorPanel
     * 
     * @return the current SqlEditorPanel
     */
    public SqlEditorPanel getCurrentSqlEditor() {
        return this.currentSqlEditor;
    }

    /**
     * Determine if currently selected tab is the SELECT tab
     * 
     * @return 'true' if the selected tab is SELECT, 'false' if not.
     */
    public boolean isCurrentTabSelect() {
        // update the current editor to match the tab selected
        Object selectedItem = getSelectedItem();
        return (selectedItem == this.sqlSelectPanelForNoUpdate || selectedItem == this.selectTabForUpdate);
    }

    /**
     * Determine if current editor has pending changes
     * 
     * @return 'true' if has pending changes, 'false' if not.
     */
    public boolean hasPendingChanges() {
        boolean result = false;
        SqlEditorPanel currentEditor = getCurrentSqlEditor();
        if (currentEditor != null && currentEditor.hasPendingChanges()) {
            result = true;
        }
        return result;
    }

    public StyledText getTextWidget() {
        return currentSqlEditor.getTextViewer().getTextWidget();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getControl()
     */
    public Control getControl() {
        return this.objEditorParent;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitle()
     */
    public String getTitle() {
        return getString(TITLE_TEXT, getTransformationName());
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitleToolTip()
     */
    public String getTitleToolTip() {
        return getString(TITLE_TOOLTIP, getTransformationName());
    }

    protected String getTransformationName() {
        return NAME;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getTitleImage()
     */
    public Image getTitleImage() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#canEdit(java.lang.Object, org.eclipse.ui.IEditorPart)
     * @since 5.0.1
     */
    public boolean canEdit( Object modelObject,
                            IEditorPart editor ) {
        if (this.override != null && this.override.canEdit(modelObject, editor)) {
            return false;
        }
        if (modelObject != null) {
            if (modelObject instanceof InputSet || !(editor instanceof DiagramEditor)) {
                return false;
            }
            if (TransformationHelper.isOperation(modelObject)) {
                return false;
            }
            if (TransformationHelper.isSqlTransformationMappingRoot(modelObject)) {
                EObject target = ((SqlTransformationMappingRoot)modelObject).getTarget();
                if (TransformationHelper.isOperation(target)) {
                    return false;
                }
                return true;
            } else if ((TransformationHelper.isVirtualSqlTable(modelObject) || TransformationHelper.isSqlVirtualProcedure(modelObject))) {
                // One last check here, because XMLDocuments are SqlTableAspect eObjects. Must be a valid t-target
                if (TransformationHelper.isValidSqlTransformationTarget(modelObject)
                    && TransformationHelper.getMappingRoot((EObject)modelObject, false, true) != null) return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#edit(org.eclipse.emf.ecore.EObject)
     */
    public void edit( Object rootOrVirtualSqlTable ) {
        Object obj = getEditableObject(rootOrVirtualSqlTable);
        if (obj instanceof SqlTransformationMappingRoot) {
            SqlTransformationMappingRoot workingMappingRoot = (SqlTransformationMappingRoot)obj;
            // Wire listeners
            if( deactivated ) {
            	registerListeners();
            }
            currentMappingRoot = workingMappingRoot;
            this.validator = new TransformationValidator(currentMappingRoot, false);

            // establish builder tree
            /*  
             * jh note: BuilderTreeProvider must always be created in this method, 
             *          and NEVER maintained in an instance variable between uses.
             *          This is because its constructor modifies the state of the
             *          static class ElementViewerFactory.
             */
            new BuilderTreeProvider();

            ElementViewerFactory.setCriteriaStrategy(new QueryCriteriaStrategy());

            // Inits the useDefault states
            setUseDefaultStates(currentMappingRoot);

            // Refresh Tab selection
            refreshTabs();

            // Set the Editor Panel contents based on selectedTab
            setEditorContent(this.currentItem, true, this, true, false);

            objEditorParent.layout();

            setEditorFocus(this.currentSqlEditor);
        } else if (obj == null) {
            // -------------------------------------------------------------------------------------------------------------------
            // DEFECT 23230
            // If NULL is the input, we assume this is a "clear" editor call.
            // -------------------------------------------------------------------------------------------------------------------
            // Need to clear the SQL
            this.currentMappingRoot = null;
            this.validator = null;
            clearSqlEditors();
        }

        // we don't want the user to be able to undo the first time the document text was set
        resetUndoManager(this.getCurrentSqlEditor());

        // All actions to update state based on setting of contents
        updateActions();
    }

    /**
     * Refresh tabs, ensuring that selected tab is compatible with target 'allowsUpdate' property
     */
    void refreshTabs() {
        if (!isEditorValid()) return;

        // Get target supportsUpdates state
        this.targetAllowsUpdates = getTargetAllowsUpdates();

        // target allowsUpdates
        if (!noUpdatesAllowed && targetAllowsUpdates) {
            Object selectedItem = getSelectedItem();
            // Use Update tab folder
            objEditorParentLayout.topControl = tabFolderWithUpdateTabs;
            // update the current editor to match the tab selected
            if (selectTabForUpdate != null && selectedItem == selectTabForUpdate && sqlSelectEditorForUpdate != null) {
                setCurrentSqlEditor(sqlSelectEditorForUpdate);
                tabFolderWithUpdateTabs.setSelection(selectTabForUpdate);
            } else if (insertTab != null && selectedItem == insertTab && sqlInsertEditor != null) {
                setCurrentSqlEditor(sqlInsertEditor);
                tabFolderWithUpdateTabs.setSelection(insertTab);
            } else if (updateTab != null && selectedItem == updateTab && sqlUpdateEditor != null) {
                setCurrentSqlEditor(sqlUpdateEditor);
                tabFolderWithUpdateTabs.setSelection(updateTab);
            } else if (deleteTab != null && selectedItem == deleteTab && sqlDeleteEditor != null) {
                setCurrentSqlEditor(sqlDeleteEditor);
                tabFolderWithUpdateTabs.setSelection(deleteTab);
            } else {
                setCurrentSqlEditor(sqlSelectEditorForUpdate);
                tabFolderWithUpdateTabs.setSelection(selectTabForUpdate);
            }
            // Update current transform for drop listeners
            if (seSelectUpdateDropListener != null && sqlSelectEditorForUpdate != null) {
                seSelectUpdateDropListener.setTransformation(currentMappingRoot);
            }
            // target doesnt allow updates - selectTab, regardless of current selection
        } else {
            objEditorParentLayout.topControl = sqlSelectPanelForNoUpdate;
            setCurrentSqlEditor(sqlSelectEditorForNoUpdate);
            if (seSelectNoUpdateDropListener != null) {
                seSelectNoUpdateDropListener.setTransformation(currentMappingRoot);
            }
        }

        // Update currentItem variable
        this.currentItem = getSelectedItem();
    }

    private void setEditorFocus( final SqlEditorPanel editor ) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                if (editor != null) {
                    TextViewer tv = editor.getTextViewer();

                    if (tv != null) {
                        StyledText st = tv.getTextWidget();

                        if (st != null && !st.isDisposed()) {
                            boolean gotFocus = st.setFocus();
                            if (gotFocus) {
                                editor.setCaretOffset(0);
                                handleCursorPositionChanged();
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Method provides quick way to refresh the current editor content and insure that sql strings are all up to date with model
     * 
     * @since 4.2
     */
    public void refreshEditorContent() {
        setEditorContent(getSelectedItem(), false, this, true, false);
    }

    private void resetUndoManager( SqlEditorPanel editor ) {
        editor.resetUndoRedoHistory();
    }

    private void resetAllUndoManagers() {
        if (this.sqlSelectEditorForUpdate != null) resetUndoManager(this.sqlSelectEditorForUpdate);
        if (this.sqlSelectEditorForNoUpdate != null) resetUndoManager(this.sqlSelectEditorForNoUpdate);
        if (this.sqlInsertEditor != null) resetUndoManager(this.sqlInsertEditor);
        if (this.sqlUpdateEditor != null) resetUndoManager(this.sqlUpdateEditor);
        if (this.sqlDeleteEditor != null) resetUndoManager(this.sqlDeleteEditor);
    }

    /**
     * Set the Editor Panel based on the selectedTab
     * 
     * @param int the selected tab.
     */
    public void setEditorContent( Object item,
                                  boolean reconcileTarget,
                                  Object txnSource,
                                  boolean overwriteDirty,
                                  boolean sourceIsEvent ) {
        int cmdType = getCommandTypeForItem(item);
        boolean showMessage = false;

        if (isResourceValid() && getControl() != null && !getControl().isDisposed()) {
            // Get the current SQL for the Selected Tab and set on QueryEditorPanel
            String sqlString = TransformationHelper.getSqlString(currentMappingRoot, cmdType);
            // Get valid status for the transformation
            boolean isValid = TransformationHelper.isValid(currentMappingRoot, cmdType);
            
            //System.out.println(" TOEP.setEditorContent()  QUERY VALID = " + isValid);

            if( sourceIsEvent ) {
            	showMessage = TransformationHelper.setSqlString(currentMappingRoot, sqlString, cmdType, false, txnSource);
            }
            if( !showMessage && !isValid ) {
            	showMessage = true;
            }
            
            // If the command is a SetQuery (UNION), update the reconciled status on the editorPanel
            // Go ahead and check the command regardless if it's resolved or not.
            Command newCommand = TransformationHelper.getCommand(currentMappingRoot, cmdType);
            if (newCommand instanceof SetQuery) {
                updateEditorSetQueryStates((SetQuery)newCommand);
            }

            // Update the external groups for SqlEditor Builder tree
            Collection externalGroups = getExternalBuilderGroups(currentMappingRoot);
            // Set external Symbols on the SqlEditorPanel
            currentSqlEditor.setExternalBuilderGroups(externalGroups);

            SqlEditorPanel editor = getSqlEditorForItem(item);

            // Create TransformationValidator and set validator on all editors
            resetValidators();

            boolean isSelectCached = SqlMappingRootCache.containsStatus(currentMappingRoot, QueryValidator.SELECT_TRNS);
            SqlTransformationResult selectStatus = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,
                                                                                                  QueryValidator.SELECT_TRNS,
                                                                                                  true,
                                                                                                  null);
            // Create an existing status to pass to the SQL Editor Panel to prevent re-validation of SQL if already validated
            SqlTransformationResult existingStatus = selectStatus;

            // If the tab is NOT SELECT TAB, then get the status of that command type so we can pass it to the SQL Editor Panel
            if (cmdType != QueryValidator.SELECT_TRNS) {
                existingStatus = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot, cmdType, true, null);
            }

            boolean statusExists = selectStatus != null;

            if (overwriteDirty || !editor.hasPendingChanges()) {
                // we are authorized to overwrite, or there are no pending changes:
                editor.setText(sqlString, this, !statusExists, existingStatus);

                // don't want this text to be undoable
                // resetUndoManager(editor);

                // Set Message and Message Visibility on EditorPanel
                setEditorMessage(item);

            } // endif

            // Get Allows properties and setCheckBoxes accordingly
            setCheckBoxStates(currentMappingRoot, item);

            // Set editable state based on allows checkbox for this tab
            setEditableStatus(item);

            // Get the Target for this MappingRoot
            EObject targetGroup = TransformationHelper.getTransformationTarget(currentMappingRoot);

            boolean allowsUpdates = TransformationHelper.tableSupportsUpdate(targetGroup);

            // Reconcile targetAttributes first
            // Now we need to check read-only status here..
            if (!ModelObjectUtilities.isReadOnly(currentMappingRoot) && !(txnSource instanceof ReconcileTransformationAction)) {
                if (reconcileTarget) {
                    // defect 16739 - Alert user if we've automatically changed something:
                    int changeCode = TransformationMappingHelper.reconcileTargetAttributes(currentMappingRoot, this).getCode();
                    if (changeCode == TransformationMappingHelper.TRANSFORMATION_CHANGED) {
                        // a change occurred:
                        SqlEditorPanelWrapper sepw = getSqlEditorPanelWrapper();
                        // make sure there is no other message of note:
                        if (!sepw.isMessageAreaVisible()) {
                            // not currently visible, show the new message:
                            sepw.setMessage(TRANSFORMATION_AUTO_ADJUSTED_MSG);
                            sepw.showMessageArea(true);
                        } // endif
                    } // endif
                }

                if (allowsUpdates) {
                    if (!sourceIsEvent || !isSelectCached) {
                        // Get the current SQL for the Selected Tab and set on QueryEditorPanel
                        String sString = TransformationHelper.getSqlString(currentMappingRoot, QueryValidator.INSERT_TRNS);
                        boolean change_1 = TransformationHelper.setSqlString(currentMappingRoot,
                                                                             sString,
                                                                             QueryValidator.INSERT_TRNS,
                                                                             false,
                                                                             txnSource);

                        sString = TransformationHelper.getSqlString(currentMappingRoot, QueryValidator.UPDATE_TRNS);
                        boolean change_2 = TransformationHelper.setSqlString(currentMappingRoot,
                                                                             sString,
                                                                             QueryValidator.UPDATE_TRNS,
                                                                             false,
                                                                             txnSource);

                        sString = TransformationHelper.getSqlString(currentMappingRoot, QueryValidator.DELETE_TRNS);
                        boolean change_3 = TransformationHelper.setSqlString(currentMappingRoot,
                                                                             sString,
                                                                             QueryValidator.DELETE_TRNS,
                                                                             false,
                                                                             txnSource);

                        if (change_1 || change_2 || change_3) showMessage = true;
                    } else {
                        if (!SqlMappingRootCache.containsStatus(currentMappingRoot, QueryValidator.INSERT_TRNS)) {
                            String sString = TransformationHelper.getSqlString(currentMappingRoot, QueryValidator.INSERT_TRNS);
                            showMessage = TransformationHelper.setSqlString(currentMappingRoot,
                                                                            sString,
                                                                            QueryValidator.INSERT_TRNS,
                                                                            false,
                                                                            txnSource);
                        }
                        if (!SqlMappingRootCache.containsStatus(currentMappingRoot, QueryValidator.UPDATE_TRNS)) {
                            String sString = TransformationHelper.getSqlString(currentMappingRoot, QueryValidator.UPDATE_TRNS);
                            boolean show = TransformationHelper.setSqlString(currentMappingRoot,
                                                                             sString,
                                                                             QueryValidator.UPDATE_TRNS,
                                                                             false,
                                                                             txnSource);
                            if (!showMessage && show) showMessage = true;
                        }
                        if (!SqlMappingRootCache.containsStatus(currentMappingRoot, QueryValidator.DELETE_TRNS)) {
                            String sString = TransformationHelper.getSqlString(currentMappingRoot, QueryValidator.DELETE_TRNS);
                            boolean show = TransformationHelper.setSqlString(currentMappingRoot,
                                                                             sString,
                                                                             QueryValidator.DELETE_TRNS,
                                                                             false,
                                                                             txnSource);
                            if (!showMessage && show) showMessage = true;
                        }
                    }
                    if (showMessage) {
                        // a change occurred:
                        SqlEditorPanelWrapper sepw = getSqlEditorPanelWrapper();
                        // make sure there is no other message of note:
                        if (!sepw.isMessageAreaVisible()) {
                            // not currently visible, show the new message:
                            sepw.setMessage(TRANSFORMATION_AUTO_ADJUSTED_MSG);
                            sepw.showMessageArea(true);
                        }
                    }
                }
            }

            // Notify Listeners of Status
            if (isValid) {
                notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_VALIDATABLE));
            } else if (TransformationHelper.isResolvable(currentMappingRoot, cmdType)) {
                notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_RESOLVABLE));
            } else if (TransformationHelper.isParsable(currentMappingRoot, cmdType)) {
                notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_PARSABLE));
            } else {
                notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_NOT_PARSABLE));
            }
            setDirty(editor.hasPendingChanges());
            if( allowsUpdates ) {
            	final SqlTransformationResult currentSelectStatus = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.SELECT_TRNS, true, null);
	            if( selectStatus != null ) {
	            	UiUtil.runInSwtThread(new Runnable() {
                        public void run() {
                        	updateUpdateTabs(currentSelectStatus);
                        }
                    }, true);
	            }
            }

            updateReadOnlyState();
        }
    }
    
    private void updateUpdateTabs(SqlTransformationResult validationResult) {
    	// ASSUME SUPPORTS UPDATE == TRUE
    	if( validationResult == null ) return; // DO NOTHING
    	
    	// -------------
    	// WORK ON EACH TAB TYPE
    	// -------------
    	
    	// -----------------------------
    	// SELECT TAB
    	// -----------------------------
    	boolean selectSqlOk = validationResult.isOkToUpdate(QueryValidator.SELECT_TRNS);
    	
    	if( selectSqlOk ) { // NO ERRORS WITH TYPE = QueryValidator.ALL_UPDATE_SQL_PROBLEM
    		this.selectTabForUpdate.setImage(null);
    		this.selectTabForUpdate.setToolTipText(null);
    	} else { // ERRORS FOUND WITH TYPE = QueryValidator.ALL_UPDATE_SQL_PROBLEM
    		this.selectTabForUpdate.setImage(UiPlugin.getDefault().getImage(UiConstants.Images.WARNING));
    		IStatus firstStatus = validationResult.getUpdateStatusList(QueryValidator.SELECT_TRNS).iterator().next();
    		this.selectTabForUpdate.setToolTipText(firstStatus.getMessage());
    	}
    	
    	
    	insertTab.setImage(null);
    	updateTab.setImage(null);
    	deleteTab.setImage(null);
    	// -----------------------------
    	// UPDATE TAB
    	// -----------------------------

    	// If USE DEFAULT is Checked, decorate with NOT_ALLOWED else check for specific UPDATE errors, then decorate with error or NONE
    	// and if error, set the message based on first Update Error.
    	// But if SELECT has ERRORS FOUND WITH TYPE = QueryValidator.ALL_UPDATE_SQL_PROBLEM then set message from the SELECT status if NO Update Errors.
    	String toolTipText = null;
    	Image displayImage = null;
    	String tabText = INSERT_TAB_TEXT;
    	if( isUseDefault(QueryValidator.INSERT_TRNS) ) {
    		tabText = INSERT_TAB_TEXT + " (default)"; //$NON-NLS-1$
    		// Now we check for "insert" default errors and warnings
    		IStatus displayStatus = null;
    		if( !validationResult.isOkToUpdate(QueryValidator.INSERT_TRNS) ) {
    			displayStatus = validationResult.getUpdateStatusList(QueryValidator.INSERT_TRNS).iterator().next();
    		} else if(!selectSqlOk) {
    			displayStatus = validationResult.getUpdateStatusList(QueryValidator.SELECT_TRNS).iterator().next();
    		}
    		if( displayStatus != null ) {
    			toolTipText = displayStatus.getMessage();
    			displayImage = NOT_ALLOWED_IMAGE;
    			useDefaultForInsertLabel.setText(Messages.DefaultUpdateMessageAmbigious);
    		} else {
    			useDefaultForInsertLabel.setText(Messages.DefaultUpdateMessageOK);
    		}
    	} else {
    		// Now we check for "insert" SQL errors and warnings
    		SqlTransformationResult insertResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot, QueryValidator.INSERT_TRNS, true, null);

    		IStatus displayStatus = null;
    		if( insertResult != null && !insertResult.getStatusList().isEmpty() ) {
    			displayStatus = insertResult.getStatusList().iterator().next();
    		}

    		if( displayStatus != null ) {
	    		displayImage = getSeverityImage(insertResult.getMaxSeverity());
	    		toolTipText = displayStatus.getMessage();
    		}
    	}
    	
    	this.insertTab.setImage(displayImage);
    	this.insertTab.setToolTipText(toolTipText);
    	this.insertTab.setText(tabText);
    	
    	// -----------------------------
    	// INSERT TAB
    	// -----------------------------
    	displayImage = null;
    	toolTipText = null;
    	tabText = UPDATE_TAB_TEXT;
    	if( isUseDefault(QueryValidator.UPDATE_TRNS) ) {
    		tabText = UPDATE_TAB_TEXT + " (default)"; //$NON-NLS-1$
    		// Now we check for "insert" default errors and warnings
    		IStatus displayStatus = null;
    		if( !validationResult.isOkToUpdate(QueryValidator.UPDATE_TRNS) ) {
    			displayStatus = validationResult.getUpdateStatusList(QueryValidator.UPDATE_TRNS).iterator().next();
    		} else if(!selectSqlOk) {
    			displayStatus = validationResult.getUpdateStatusList(QueryValidator.SELECT_TRNS).iterator().next();
    		}
    		if( displayStatus != null ) {
    			toolTipText = displayStatus.getMessage();
    			displayImage = NOT_ALLOWED_IMAGE;
    			useDefaultForUpdateLabel.setText(Messages.DefaultUpdateMessageAmbigious);
    		} else {
    			useDefaultForUpdateLabel.setText(Messages.DefaultUpdateMessageOK);
    		}
    	} else {
    		// Now we check for "insert" SQL errors and warnings
    		SqlTransformationResult updateResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot, QueryValidator.UPDATE_TRNS, true, null);

    		IStatus displayStatus = null;
    		
    		if( updateResult != null && !updateResult.getStatusList().isEmpty()) {
    			displayStatus = updateResult.getStatusList().iterator().next();
    		}

    		if( displayStatus != null ) {
	    		displayImage = getSeverityImage(updateResult.getMaxSeverity());
	    		toolTipText = displayStatus.getMessage();
            }
    	}
    	
    	this.updateTab.setImage(displayImage);
    	this.updateTab.setToolTipText(toolTipText);
    	this.updateTab.setText(tabText);
    	
    	// -----------------------------
    	// DELETE TAB
    	// -----------------------------
    	displayImage = null;
    	toolTipText = null;
    	tabText = DELETE_TAB_TEXT;
    	if( isUseDefault(QueryValidator.DELETE_TRNS) ) {
    		tabText = DELETE_TAB_TEXT + " (default)"; //$NON-NLS-1$
    		// Now we check for "insert" default errors and warnings
    		IStatus displayStatus = null;
    		if( !validationResult.isOkToUpdate(QueryValidator.DELETE_TRNS) ) {
    			displayStatus = validationResult.getUpdateStatusList(QueryValidator.DELETE_TRNS).iterator().next();
    		} else if( !selectSqlOk) {
    			displayStatus = validationResult.getUpdateStatusList(QueryValidator.SELECT_TRNS).iterator().next();
    		}
    		if( displayStatus != null ) {
    			toolTipText = displayStatus.getMessage();
    			displayImage = NOT_ALLOWED_IMAGE;
    			useDefaultForDeleteLabel.setText(Messages.DefaultUpdateMessageAmbigious);
    		} else {
    			useDefaultForDeleteLabel.setText(Messages.DefaultUpdateMessageOK);
    		}
    	} else {
    		// Now we check for "insert" SQL errors and warnings
    		SqlTransformationResult deleteResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot, QueryValidator.DELETE_TRNS, true, null);

    		IStatus displayStatus = null;
    		if( deleteResult != null && !deleteResult.getStatusList().isEmpty()) {
    			displayStatus = deleteResult.getStatusList().iterator().next();
    		}

    		if( displayStatus != null ) {
	    		displayImage = getSeverityImage(deleteResult.getMaxSeverity());
	    		toolTipText = displayStatus.getMessage();
    		}
    	}
    	
    	this.deleteTab.setImage(displayImage);
    	this.deleteTab.setToolTipText(toolTipText);
    	this.deleteTab.setText(tabText);
    }
    
    private boolean isUseDefault(int updateType) {
    	SqlTransformation helper = ((SqlTransformation)currentMappingRoot.getHelper());
    	if( helper == null ) {
    		return false;
    	}
    	
    	switch( updateType ) {
	    	case QueryValidator.INSERT_TRNS: return helper.isInsertSqlDefault();
	    	case QueryValidator.UPDATE_TRNS: return helper.isUpdateSqlDefault();
	    	case QueryValidator.DELETE_TRNS: return helper.isDeleteSqlDefault();
    	}
    	return false;
    }
    
    private Image getSeverityImage(int severity) {
    	if( severity == IStatus.ERROR ) {
			return ERROR_IMAGE;
		} else if( severity == IStatus.WARNING){
			return WARNING_IMAGE;
		}
    	return null;
    }

    /**
     * Set editable status for the current editor
     * 
     * @param item
     */
    private void setEditableStatus( Object item ) {
        boolean isReadOnly = false;
        if (currentMappingRoot != null && ModelObjectUtilities.isReadOnly(currentMappingRoot)) {
            isReadOnly = true;
        }
        if (isReadOnly) {
            getSqlEditorForItem(item).setEditable(false);
        } else {
            if (item == selectTabForUpdate || item == sqlSelectPanelForNoUpdate) {
                getSqlEditorForItem(item).setEditable(true);
            } else if (item == insertTab || item == updateTab || item == deleteTab) {
                boolean allowed = isEnableSelected(item);
                boolean useDefault = isUseDefaultSelected(item);
                if (allowed && !useDefault) {
                    getSqlEditorForItem(item).setEditable(true);
                } else {
                    getSqlEditorForItem(item).setEditable(false);
                }
            }
        }
    }

    /**
     * Set transformation editor message for the current editor
     * 
     * @param item
     */
    private void setEditorMessage( Object item ) {
        int cmdType = getCommandTypeForItem(item);
        
        // This is the "Editor message panel" at the bottom of each SQL Editor.
        // We need to Display the panel if ERRORS/Warnings exist for the specific cmdType
        String message = null;
        boolean showMessage = false;
        

        boolean isTargetValid = TransformationHelper.isTargetValid(currentMappingRoot, cmdType);
        //boolean isValid = TransformationHelper.isValid(currentMappingRoot, cmdType);

        switch( cmdType ) {
	        case QueryValidator.SELECT_TRNS: {
	        	SqlTransformationResult statusResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.SELECT_TRNS, true, null);
	        	if( !isTargetValid ) {
	        		IStatus status = TransformationHelper.getTargetValidStatus(currentMappingRoot, cmdType);
	                if (status != null) {
	                    message = status.getMessage();
	                    showMessage = true;
	                }
	        	} else if( statusResult != null && statusResult.getMaxSeverity() > IStatus.OK ) {
	        		message = statusResult.getFullMessage();
	        		showMessage = true;
	        	}
	        } break;
	        case QueryValidator.INSERT_TRNS: {
	        	if( !isUseDefault(QueryValidator.INSERT_TRNS)) {
		        	SqlTransformationResult statusResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.INSERT_TRNS, true, null);
		        	if( statusResult != null && statusResult.getMaxSeverity() > IStatus.OK ) {
		        		message = statusResult.getFullMessage();
		        		showMessage = true;
		        	}
	        	} else {
	        		SqlTransformationResult statusResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.SELECT_TRNS, true, null);
	        		if( statusResult != null && statusResult.getUpdateMaxSeverity(QueryValidator.INSERT_TRNS) > IStatus.OK ) {
		        		message = statusResult.getUpdateFullMessage(QueryValidator.INSERT_TRNS);
		        		showMessage = true;
		        	}
	        	}
	        } break;
	        case QueryValidator.UPDATE_TRNS: {
	        	if( !isUseDefault(QueryValidator.UPDATE_TRNS)) {
		        	SqlTransformationResult statusResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.UPDATE_TRNS, true, null);
		        	if( statusResult != null && statusResult.getMaxSeverity() > IStatus.OK ) {
		        		message = statusResult.getFullMessage();
		        		showMessage = true;
		        	}
	        	} else {
	        		SqlTransformationResult statusResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.SELECT_TRNS, true, null);
	        		if( statusResult != null && statusResult.getUpdateMaxSeverity(QueryValidator.UPDATE_TRNS) > IStatus.OK ) {
		        		message = statusResult.getUpdateFullMessage(QueryValidator.UPDATE_TRNS);
		        		showMessage = true;
		        	}
	        	}
	        } break;
	        case QueryValidator.DELETE_TRNS: {
	        	if( !isUseDefault(QueryValidator.DELETE_TRNS)) {
		        	SqlTransformationResult statusResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.DELETE_TRNS, true, null);
		        	if( statusResult != null && statusResult.getMaxSeverity() > IStatus.OK ) {
		        		message = statusResult.getFullMessage();
		        		showMessage = true;
		        	}
	        	} else {
	        		SqlTransformationResult statusResult = SqlMappingRootCache.getSqlTransformationStatus(currentMappingRoot,QueryValidator.SELECT_TRNS, true, null);
	        		if( statusResult != null && statusResult.getUpdateMaxSeverity(QueryValidator.DELETE_TRNS) > IStatus.OK ) {
		        		message = statusResult.getUpdateFullMessage(QueryValidator.DELETE_TRNS);
		        		showMessage = true;
		        	}
	        	}
	        } break;
	        
        }
        
        if( message != null ) {
	        currentSqlEditor.setMessage(message);
	        currentSqlEditor.showMessageArea(showMessage);
        } else if( isTargetValid ){
        	setMessageDisplayForValidSQL();
        }
    }

    /**
     * Update the External groups for the supplied TransformationMappingRoot. The external Groups must be supplied for the
     * Builders. Example of this is the InputSets for Mapping Classes, since the inputSets are no longer in the SQL FROM
     * 
     * @param transMappingRoot the TransformationMappingRoot
     */
    private Collection getExternalBuilderGroups( Object transMappingRoot ) {
        Collection externalGroups = null;
        if (transMappingRoot != null && TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            EObject targetGroup = TransformationHelper.getTransformationLinkTarget((EObject)transMappingRoot);
            if (targetGroup instanceof MappingClass) {
                externalGroups = new ArrayList(1);
                EObject inputSet = ((MappingClass)targetGroup).getInputSet();
                externalGroups.add(inputSet);
            } else if (TransformationHelper.isSqlProcedure(targetGroup)) {
                // If the procedure has any IN or INOUT parameters, add it
                List inParams = TransformationHelper.getInAndInoutParameters(targetGroup);
                if (!inParams.isEmpty()) {
                    externalGroups = new ArrayList(1);
                    externalGroups.add(targetGroup);
                }
            }
        }
        if (externalGroups == null) {
            externalGroups = Collections.EMPTY_LIST;
        }
        return externalGroups;
    }

    /**
     * @param item
     * @return The command type enumeration for the supplied item
     */
    private int getCommandTypeForItem( Object item ) {
        int type = QueryValidator.SELECT_TRNS;
        if (item == insertTab) {
            type = QueryValidator.INSERT_TRNS;
        } else if (item == updateTab) {
            type = QueryValidator.UPDATE_TRNS;
        } else if (item == deleteTab) {
            type = QueryValidator.DELETE_TRNS;
        }
        return type;
    }

    /**
     * @param item
     * @return The SqlEditorPanel instance for the supplied item
     */
    private SqlEditorPanel getSqlEditorForItem( Object item ) {
        if (noUpdatesAllowed) {
            return sqlSelectEditorForNoUpdate;
        }

        SqlEditorPanel editor = null;

        if (item == sqlSelectPanelForNoUpdate) {
            editor = sqlSelectEditorForNoUpdate;
        } else if (item == selectTabForUpdate) {
            editor = sqlSelectEditorForUpdate;
        } else if (item == insertTab) {
            editor = sqlInsertEditor;
        } else if (item == updateTab) {
            editor = sqlUpdateEditor;
        } else if (item == deleteTab) {
            editor = sqlDeleteEditor;
        }
        return editor;
    }

    public String getObjectText() {
        String result = null;
        if (currentMappingRoot != null) {
            result = StatusBarUpdater.formatEObjectMessage(currentMappingRoot);
        }
        return result;
    }

    private boolean isEditorValid() {
        return getControl() != null && !getControl().isDisposed();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#deactivate()
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
            Util.log(err);
        }

        ModelUtilities.removeNotifyChangedListener(this);
        SqlMappingRootCache.removeEventListener(this);

        // clear all undo histories
        resetAllUndoManagers();
        
        deactivated = true;
        
        // -----------------------------------------------------------------------------------------------------------------------
        // DEFECT 23230
        // Added this call to clean up the SQL Editor SQL text so when re-opened, the text will be reset and validated correctly
        // -----------------------------------------------------------------------------------------------------------------------
        edit(null);

        
        return true;
    }

    // -----------------------------------------------------------------------------------------------------------------------
    // DEFECT 23230
    // convienence method to set the sql edtior's text to NULL. Initial use is during deactivate() call.
    // -----------------------------------------------------------------------------------------------------------------------
    private void clearSqlEditors() {
        if (!parent.isDisposed()) {
            if (sqlSelectEditorForNoUpdate != null) {
                sqlSelectEditorForNoUpdate.clear();
            }
            if (sqlSelectEditorForUpdate != null) {
                sqlSelectEditorForUpdate.clear();
            }
            if (sqlInsertEditor != null) {
                sqlInsertEditor.clear();
            }
            if (sqlUpdateEditor != null) {
                sqlUpdateEditor.clear();
            }
            if (sqlDeleteEditor != null) {
                sqlDeleteEditor.clear();
            }

            if (sqlEditorPanelWrapper != null) {
                sqlEditorPanelWrapper.clear();
            }
        }

        if (sqlSelectEditorForNoUpdate != null) {
            sqlSelectEditorForNoUpdate.setQueryValidator(null);
        }
        if (sqlSelectEditorForUpdate != null) {
            sqlSelectEditorForUpdate.setQueryValidator(null);
        }
        if (sqlInsertEditor != null) {
            sqlInsertEditor.setQueryValidator(null);
        }
        if (sqlUpdateEditor != null) {
            sqlUpdateEditor.setQueryValidator(null);
        }
        if (sqlDeleteEditor != null) {
            sqlDeleteEditor.setQueryValidator(null);
        }

        if (sqlEditorPanelWrapper != null) {
            sqlEditorPanelWrapper.setQueryValidator(null);
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#addPropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void addPropertyListener( IPropertyListener listener ) {
        propListeners.addListener(IPropertyListener.class, listener);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#removePropertyListener(org.eclipse.ui.IPropertyListener)
     */
    public void removePropertyListener( IPropertyListener listener ) {
        propListeners.removeListener(IPropertyListener.class, listener);
    }

    /**
     * Handler method for transformation status change. Basically, this does a refresh when the status of the current query
     * changes. If the allowsUpdate status of the target changes, this will result in a tab change.
     */
    void handleTransformationStatusChangeEvent( final boolean reconcileTarget,
                                                final Object txnSource,
                                                final boolean overwriteDirty ) {

        if (txnSource != this && !(txnSource instanceof SqlEditorPanel)) {
            if (this.validator != null) {
                boolean didSetText = false;
                // synchronize the SELECT for noUpdates / Updates first...
                if (isCurrentTabSelect()) {
                    if (objEditorParentLayout.topControl == tabFolderWithUpdateTabs && sqlSelectEditorForNoUpdate != null) {
                        sqlSelectEditorForNoUpdate.setQueryValidator(this.validator);
                        // Call setEditorContent() instead of setText() to force validation.
                        // We want to do this because the txnSource is NOT this T-Editor and we need to assume that the SQL has
                        // changed and we need to re-set the Editor from the SQL T-Root object and it's SQL
                        setEditorContent(getSelectedItem(), reconcileTarget, txnSource, overwriteDirty, true);
                        didSetText = true;

                    } else if (sqlSelectEditorForUpdate != null) {
                        sqlSelectEditorForUpdate.setQueryValidator(this.validator);
                        // Call setEditorContent() instead of setText() to force validation.
                        // We want to do this because the txnSource is NOT this T-Editor and we need to assume that the SQL has
                        // changed and we need to re-set the Editor from the SQL T-Root object and it's SQL
                        UiUtil.runInSwtThread(new Runnable() {
                            public void run() {
                                setEditorContent(getSelectedItem(), reconcileTarget, txnSource, overwriteDirty, true);
                            }
                        }, true);

                        didSetText = true;
                    }
                }

                // Refresh Tab selection
                // FIX for Invalid SWT Thread Access
                // put on SWT thread
                UiUtil.runInSwtThread(new Runnable() {
                    public void run() {
                        refreshTabs();
                    }
                }, true);

                // defect 15131: if dirty, keep the editor dirty.
                // ONLY set content if we hadn't already done so above
                if (!didSetText) {
                    setEditorContent(getSelectedItem(), reconcileTarget, txnSource, overwriteDirty, true);
                }
                // FIX for Invalid SWT Thread Access. Was coming through processEvent() listener
                // put on SWT thread
                UiUtil.runInSwtThread(new Runnable() {
                    public void run() {
                        objEditorParent.layout();
                    }
                }, true);

            }
        } else {
            setEditorMessage(getSelectedItem());
        }

        // Allow actions to update state based on changes in SQL and validation status
        updateActions();
    }

    /**
     * Create TransformationValidator with current root and set validator on all editors
     * 
     * @since 5.0
     */
    private void resetValidators() {
        this.validator = new TransformationValidator(currentMappingRoot, false);

        if (sqlSelectEditorForUpdate != null) {
            sqlSelectEditorForUpdate.setQueryValidator(this.validator);
        }
        if (sqlSelectEditorForNoUpdate != null) {
            sqlSelectEditorForNoUpdate.setQueryValidator(this.validator);
        }
        if (sqlSelectEditorForUpdate != null) {
            sqlSelectEditorForUpdate.setQueryValidator(this.validator);
        }
        if (sqlInsertEditor != null) {
            sqlInsertEditor.setQueryValidator(this.validator);
        }
        if (sqlUpdateEditor != null) {
            sqlUpdateEditor.setQueryValidator(this.validator);
        }
        if (sqlDeleteEditor != null) {
            sqlDeleteEditor.setQueryValidator(this.validator);
        }
    }

    /**
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged( Notification notification ) {
        if (!isEditorValid()) return;

        // Check for SourcedNotification (we should only get SourcedNotifications)
        if (notification instanceof SourcedNotification) {
            Object source = ((SourcedNotification)notification).getSource();
            Collection notifications = ((SourcedNotification)notification).getNotifications();
            handleNotifications(notifications, source);
        } else { // handle single Notification
            Collection notifications = new ArrayList(1);
            notifications.add(notification);
            handleNotifications(notifications, null);
        }
    }

    /**
     * Notifications handler. Gathers all like notifications and handles them together. Only the relevant notifications will be
     * processed by this listener. Currently the only notification that is handle here is "allowsUpdates" changes to the target
     * tables.
     * 
     * @param notifications the collection of all notifications
     */
    public void handleNotifications( Collection notifications,
                                     final Object txnSource ) {
        // --------------------------------------------------------
        // Do a first-pass - ignore irrelevant notifications
        // --------------------------------------------------------
        Collection validNotifications = getValidNotifications(notifications);
        // -------------------------------------------------
        // Process remaining valid notifications
        // Dont respond to events caused by this class
        // -------------------------------------------------
        if (!validNotifications.isEmpty() && !(txnSource instanceof TransformationObjectEditorPage)) {
//            if (Thread.currentThread() == getControl().getDisplay().getThread()) {
//                handleTransformationStatusChangeEvent(true, txnSource, true); // should this be true? I'm not sure when this
//                // method is called...
//            } else {
//                getControl().getDisplay().syncExec(new Runnable() {
//                    public void run() {
//                        handleTransformationStatusChangeEvent(true, txnSource, true); // should this be true? I'm not sure when
//                        // this method is called...
//                    }
//                });
//            }
        }
    }

    /**
     * Do a first pass to remove totally irrelevant notifications. This editorPage is only interested in 1) changes to the target
     * table 'allowUpdates' feature
     * 
     * @param notifications the collection of all notifications
     * @return new collection of relevant notifications
     */
    private Collection getValidNotifications( Collection notifications ) {
        Collection validNotifications = new ArrayList(notifications.size());
        Iterator iter = notifications.iterator();
        while (iter.hasNext()) {
            Notification notification = (Notification)iter.next();
            // Only need to keep change notifications
            if (NotificationUtilities.isChanged(notification)) {
                // If the changedObject is one of the following, process it
                // (1) the target Virtual Group
                final Object changedObject = ModelerCore.getModelEditor().getChangedObject(notification);

                if (changedObject != null && changedObject instanceof EObject) {

                    // jh Defect 21479 - accept notifications for sql mods
                    // (this fixes Undo Clear Transformation so that SQL is restored)
                    if (changedObject instanceof SqlTransformation) {
                        validNotifications.add(notification);
                        continue;
                    }

                    // ChangedObject is current MappingRoot or SqlTransformation
                    if (isCurrentTransformationLinkTarget((EObject)changedObject)) {
                        // Get the target allowsUpdate State - see if it's different than editor state
                        boolean targetSupportsUpdateState = TransformationHelper.tableSupportsUpdate((EObject)changedObject);
                        // If different, then respond to the notification
                        if (targetSupportsUpdateState != this.targetAllowsUpdates) {
                            validNotifications.add(notification);
                            continue;
                        }
                    }
                }
            }
        }
        return validNotifications;
    }

    /**
     * Helper method to determine if the supplied EObject is the current SqlTransformation link target
     * 
     * @param eObj the EObject to test
     * @return 'true' if it's the current SqlTransformation link target, 'false' if not.
     */
    private boolean isCurrentTransformationLinkTarget( EObject eObj ) {
        boolean result = false;
        if (currentMappingRoot != null) {
            EObject linkTarget = TransformationHelper.getTransformationLinkTarget(currentMappingRoot);
            if (linkTarget != null && linkTarget.equals(eObj)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Get the currently selected item
     * 
     * @return the selected item
     */
    private Object getSelectedItem() {
        Object item = getTopControl();
        if (item instanceof CTabFolder) {
            CTabFolder folder = (CTabFolder)item;
            CTabItem ti = folder.getSelection();
            if (ti != null) {
                return ti;
            }
            return folder.getItem(0);
        }
        return item;
    }

    /**
     * Determine if the current transformation target allows Updates
     * 
     * @return 'true' if the target allows updates, 'false' if not. from other model views.
     * @param theMenuMgr the context menu being contributed to
     */
    boolean getTargetAllowsUpdates() {
        boolean allowsUpdates = false;

        // Get current transformation target
        EObject targetEObj = TransformationHelper.getTransformationTarget(currentMappingRoot);

        // get target allowsUpdates state
        if (targetEObj != null) {
            allowsUpdates = TransformationHelper.tableSupportsUpdate(targetEObj);
        }
        return allowsUpdates;
    }

    /**
     * Set the value for current transformation target allows Updates 'true' if the target allows updates, 'false' if not.
     * 
     * @param allowsUPdates
     */
    private void setTargetAllowsUpdates( boolean allowsUpdates ) {
        // Get current transformation target
        EObject targetEObj = TransformationHelper.getTransformationTarget(currentMappingRoot);
        // get target allowsUpdates state
        if (targetEObj != null) {
            TransformationHelper.setTableSupportsUpdate(targetEObj, allowsUpdates);
            resetAllUndoManagers();
        }
    }

    /**
     * Offers the editor a chance to contribute actions which will be made available to context menus from other model views.
     * 
     * @param theMenuMgr the context menu being contributed to
     */
    public void contributeExportedActions( IMenuManager theMenuMgr ) {
        IAction action = null;
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        DiagramActionService service = (DiagramActionService)DiagramUiPlugin.getDefault().getActionService(window.getActivePage());

        // -----------------------------------------
        // Contribute the AddToSqlSelect Action
        // -----------------------------------------
        try {
            IEditorPart editorPart = window.getActivePage().getActiveEditor();
            if (editorPart instanceof ModelEditor) {
                ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
                String actionKey = DiagramActionService.constructKey(DiagramActions.ADD_TO_SQL_SELECT, editorPage);
                if (service.isRegistered(actionKey)) {
                    action = service.getAction(actionKey);
                }
            }
        } catch (final CoreException err) {
            Util.log(err);
        }

        if (action != null) {
            // check to see if menu is edit menu or just a context menu
            if (theMenuMgr.find(ModelerActionBarIdManager.getMenuAdditionsMarkerId()) == null) {
                // must be a context menu. just add to end.
                theMenuMgr.add(action);
            } else {
                // edit menu. add before end marker.
                theMenuMgr.insertBefore(ModelerActionBarIdManager.getMenuAdditionsMarkerId(), action);
            }
        }

        // -----------------------------------------
        // Contribute the AddJoinExpression Action
        // -----------------------------------------
        try {
            IEditorPart editorPart = window.getActivePage().getActiveEditor();
            if (editorPart instanceof ModelEditor) {
                ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
                String actionKey = DiagramActionService.constructKey(DiagramActions.ADD_JOIN_EXPRESSION, editorPage);
                if (service.isRegistered(actionKey)) {
                    action = service.getAction(actionKey);
                }
            }
        } catch (final CoreException err) {
            Util.log(err);
        }

        if (action != null) {
            // check to see if menu is edit menu or just a context menu
            if (theMenuMgr.find(ModelerActionBarIdManager.getMenuAdditionsMarkerId()) == null) {
                // must be a context menu. just add to end.
                theMenuMgr.add(action);
            } else {
                // edit menu. add before end marker.
                theMenuMgr.insertBefore(ModelerActionBarIdManager.getMenuAdditionsMarkerId(), action);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.IEditorActionExporter#getAdditionalModelingActions(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public List<IAction> getAdditionalModelingActions( ISelection selection ) {
        List<IAction> addedActions = new ArrayList<IAction>();
        IAction action = null;
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        DiagramActionService service = (DiagramActionService)DiagramUiPlugin.getDefault().getActionService(window.getActivePage());

        // -----------------------------------------
        // Contribute the AddToSqlSelect Action
        // -----------------------------------------
        try {
            IEditorPart editorPart = window.getActivePage().getActiveEditor();
            if (editorPart instanceof ModelEditor) {
                ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
                String actionKey = DiagramActionService.constructKey(DiagramActions.ADD_TO_SQL_SELECT, editorPage);
                if (service.isRegistered(actionKey)) {
                    action = service.getAction(actionKey);
                }
            }
        } catch (final CoreException err) {
            Util.log(err);
        }

        if (action != null && action.isEnabled()) {
            addedActions.add(action);
        }

        // -----------------------------------------
        // Contribute the AddJoinExpression Action
        // -----------------------------------------
        action = null;
        try {
            IEditorPart editorPart = window.getActivePage().getActiveEditor();
            if (editorPart instanceof ModelEditor) {
                ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
                String actionKey = DiagramActionService.constructKey(DiagramActions.ADD_JOIN_EXPRESSION, editorPage);
                if (service.isRegistered(actionKey)) {
                    action = service.getAction(actionKey);
                }
            }
        } catch (final CoreException err) {
            Util.log(err);
        }

        if (action != null && action.isEnabled()) {
            addedActions.add(action);
        }
        return addedActions;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#contributeToolbarActions(org.eclipse.jface.action.ToolBarManager)
     */
    public void contributeToolbarActions( ToolBarManager toolBarMgr ) {

        if (toolBarMgr == null) return;

        int iAction = 0; // Keep track of separator locations

        if (this.currentMappingRoot != null) {
            boolean addedButton = false;

            if (this.currentMappingRoot != null) {
                SortableSelectionAction actualPreviewDataAction = ModelerSpecialActionManager.getAction(com.metamatrix.modeler.ui.UiConstants.Extensions.PREVIEW_DATA_ACTION_ID);
                if (actualPreviewDataAction != null) {
                    // This is a special case. Preview action lives in dqp.ui plugin. However we need a separate instanceo of it
                    // to better manage
                    // it's enablement state. So we provided a clone() method to get a generic copy of it.
                    // This is done through the modeler.ui's SortableSelectionAction which is an abstract class that implements a
                    // default no-op
                    // clone() method that is overridden by the PreviewTableDataContextAction
                    previewDataAction = actualPreviewDataAction.getClone();
                    // Override the image and tooltip since it's behavior is specific to Transformations
                    previewDataAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.PREVIEW_VIRTUAL_DATA_ICON));
                    previewDataAction.setToolTipText(PREVIEW_DATA_TOOLTIP);
                    // Set the selection to be the mapping root target (i.e. virtual table, operation, whatever
                    previewDataAction.setSelection(new StructuredSelection(currentMappingRoot.getTarget()));
                    toolBarMgr.add(previewDataAction);
                    toolBarMgr.add(new Separator());
                    iAction++;
                    addedButton = true;
                    updateActions();
                }
            }

            searchTransformationsAction = new OpenTransformationSearchPageAction2();
            toolBarMgr.add(searchTransformationsAction);
            iAction++;
            addedButton = true;

            if (!TransformationHelper.isOperation(currentMappingRoot.getTarget())) {
                editTransformationAction = new EditTransformationAction();
                if (isResourceValid()) {
                    editTransformationAction.setSelection(new StructuredSelection(getTargetResource()));
                }
                toolBarMgr.add(editTransformationAction);
                iAction++;
                addedButton = true;
            }

            if (addedButton) {
                toolBarMgr.add(new Separator());
            }
        }
        toolBarMgr.add(getLabelContributionForCursorPosition());
        toolBarMgr.add(new Separator());

        // We don't want to contribute this if the object is a Mapping Class or Staging Table
        if (this.currentMappingRoot != null && !TransformationHelper.isMappingClass(currentMappingRoot.getTarget())
            && !TransformationHelper.isStagingTable(currentMappingRoot.getTarget())
            && !TransformationHelper.isOperation(currentMappingRoot.getTarget())
            && !TransformationHelper.isSqlProcedure(currentMappingRoot.getTarget())) {
            toolBarMgr.add(getCheckBoxContributionForSupportsUpdates());
            toolBarMgr.add(new Separator());
        }

        // Put separators after the following actions
        List separatorLocs = getSeparatorLocations();

        IAction reconcileAction = contributeReconcileAction();
        if (reconcileAction != null) {
            toolBarMgr.add(reconcileAction);
            iAction++;
        }

        // Get the Actions from the SqlPanel -- sqlEditorPanelWrapper
        List sqlActions = getSqlEditorPanelWrapper().getActions();

        // Add the Actions
        Iterator iter = sqlActions.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof ToggleOptimizer) {
                this.toggleOptimizerAction = (ToggleOptimizer)obj;
            }
            // Add the next action
            toolBarMgr.add((Action)obj);

            // Determine if we need a separator after it
            iAction++;
            if (separatorLocs.contains(new Integer(iAction))) {
                toolBarMgr.add(new Separator());
            }
        }
    }

    /* Method should be called any time the content or focus of this editor has changed, as well as when the validation status of the
     * SQL could have changed.
     * 
     */
    private void updateActions() {
        // update the preview data action
        if (previewDataAction != null && currentMappingRoot != null) {
            ISelection newSelection = new StructuredSelection(currentMappingRoot.getTarget());
            previewDataAction.setSelection(newSelection);
            if (isDirty() && previewDataAction.isEnabled()) {
                previewDataAction.setEnabled(false);
            } else {
                previewDataAction.setEnabled(previewDataAction.isApplicable(newSelection));
            }
        }
        if (editTransformationAction != null && isResourceValid()) {
            editTransformationAction.setSelection(new StructuredSelection(getTargetResource()));
        }
    }

    /**
     * @return The newly created reconcile action if created; <code>null</code> otherwise.
     * @since 5.0.1
     */
    protected IAction contributeReconcileAction() {
        IAction reconcileAction = null;
        IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        DiagramActionService service = (DiagramActionService)DiagramUiPlugin.getDefault().getActionService(window.getActivePage());
        try {
            IEditorPart editorPart = window.getActivePage().getActiveEditor();
            if (editorPart instanceof ModelEditor) {
                ModelEditorPage editorPage = (ModelEditorPage)((ModelEditor)editorPart).getCurrentPage();
                String actionKey = DiagramActionService.constructKey(DiagramActions.RECONCILE_TRANSFORMATION, editorPage);
                reconcileAction = service.getAction(actionKey);
                if (reconcileAction instanceof ReconcileTransformationAction) {
                    ((ReconcileTransformationAction)reconcileAction).setTransObjectEditorPage(this);
                }
            }
        } catch (CoreException err) {
            Util.log(err);
        }
        return reconcileAction;
    }

    CheckBoxContribution getCheckBoxContributionForSupportsUpdates() {
        //      System.out.println("[ChoicePanel.getComboBoxContributionForDefault] TOP"); //$NON-NLS-1$
        if (chkSupportsUpdatesContribution == null) {
            chkSupportsUpdatesContribution = new CheckBoxContribution(SUPPORTS_UPDATE_TEXT);
            chkSupportsUpdatesContribution.setToolTipText(getString("supportsUpdatesCheckBox.toolTip")); //$NON-NLS-1$
        }
        return chkSupportsUpdatesContribution;
    }

    void handleSupportsUpdatesCheckBoxChanged() {

        // jh Defect 21528: Do save first, if appropriate
        doSave(false);

        setTargetAllowsUpdates(getCheckBoxContributionForSupportsUpdates().getSelection());
        getCheckBoxContributionForSupportsUpdates().getControl().update();

        // refresh content as select editor was clearing the first time button was checked
        refreshEditorContent();
    }

    private LabelContribution getLabelContributionForCursorPosition() {
        if (lblCursorPositionContribution == null) {
            lblCursorPositionContribution = new LabelContribution("Cursor at (0, 0)         "); //$NON-NLS-1$
        }
        return lblCursorPositionContribution;
    }

    private String getCurrentCursorPosition() {
        String sPosition = CURSOR_AT_TEXT;
        int column = 0;
        int row = 0;
        if (currentSqlEditor != null) {
            column = 1 + currentSqlEditor.getCaretXPosition();
            row = 1 + currentSqlEditor.getCaretYPosition();
        }

        sPosition = sPosition + row + COMMA_SPACE + column + RIGHT_PARENTH;

        return sPosition;
    }

    void handleCursorPositionChanged() {
        getLabelContributionForCursorPosition().setText(getCurrentCursorPosition());
    }

    /**
     * Builds list of the Separator locations for the toolbar. The separator will be placed following the action count (eg,
     * separator at 2 will be placed after the second action)
     */
    protected List getSeparatorLocations() {
        List separatorLocs = new ArrayList(6);
        separatorLocs.add(new Integer(3));
        separatorLocs.add(new Integer(5));
        separatorLocs.add(new Integer(6));
        separatorLocs.add(new Integer(8));
        separatorLocs.add(new Integer(10));
        return separatorLocs;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#doSave()
     */
    public void doSave( boolean isClosing ) {
        if (this.parent.isDisposed()) return;

        boolean saveChanges = true;
        SqlEditorPanel theEditor = getCurrentSqlEditor();

        if (theEditor.hasPendingChanges()) {
            if (isClosing) {
                saveChanges = MessageDialog.openQuestion(parent.getShell(), SAVE_PENDING_TITLE, SAVE_PENDING_MSG);
            }

            if (saveChanges) {
                // Set text without source so that it propagates back and sets the metadata
                theEditor.setQueryValidator(this.validator);
                theEditor.setText(theEditor.getText());
            } else {
                // need to let property change listeners that the changes have been thrown away
                setDirty(false);
            }
        }
    }

    public Object getAdapter( Class key ) {
        if (key.equals(IFindReplaceTarget.class) && (this.currentSqlEditor != null)) {
            return this.currentSqlEditor.getTextViewer().getFindReplaceTarget();
        }

        if (key.equals(IUndoManager.class)) {
            return this;
        }

        return null;
    }

    // ==========================================================
    // SelectionListener Interface
    // ==========================================================

    public void widgetSelected( SelectionEvent e ) {
        Object eventSource = e.getSource();

        // ---------------------------------------
        // Tab Selection
        // ---------------------------------------
        if (eventSource == getTopControl()) {
            handleTabSelectionChanged();
        }
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    /**
     * Handler method for Tab Selection
     */
    private void handleTabSelectionChanged() {
        Object previousItem = this.currentItem;
        Object selectedItem = getSelectedItem();

        // Check whether the tab selection is different
        if (selectedItem != previousItem) {
            // ---------------------------------------------------
            // Save Current Tab Sql first, so changes aren't lost
            // ---------------------------------------------------
            if (currentSqlEditor.hasPendingChanges()) {
                // Get Previous Editor SQL First
                String previousSql = this.currentSqlEditor.getText();
                int cmdType = getCommandTypeForItem(previousItem);
                if (TransformationHelper.isUserSqlDifferent(previousSql, currentMappingRoot, cmdType)) {
                    TransformationHelper.setSqlString(currentMappingRoot, previousSql, cmdType, true, this);
                }
            }

            if (!this.targetAllowsUpdates) {
                selectedItem = sqlSelectPanelForNoUpdate;
                setCurrentSqlEditor(sqlSelectEditorForNoUpdate);
            } else {
                // update the current editor to match the tab selected
                if (selectedItem == selectTabForUpdate) {
                    setCurrentSqlEditor(sqlSelectEditorForUpdate);
                } else if (selectedItem == insertTab) {
                    setCurrentSqlEditor(sqlInsertEditor);
                } else if (selectedItem == updateTab) {
                    setCurrentSqlEditor(sqlUpdateEditor);
                } else if (selectedItem == deleteTab) {
                    setCurrentSqlEditor(sqlDeleteEditor);
                }
            }

            // Update tab variable
            this.currentItem = selectedItem;
            setEditorContent(currentItem, true, this, true, false);

            this.currentSqlEditor.getTextViewer().getTextWidget().setSelection(0);
            this.currentSqlEditor.getTextViewer().getTextWidget().setFocus();
            handleCursorPositionChanged();
        }

    }

    /**
     * handler for check-box state changes
     * 
     * @param e the selection event
     */
    void handleCheckBoxStateChanged( SelectionEvent e ) {
        Object eventSource = e.getSource();
        Object selectedItem = getSelectedItem();
        // Get Selected States
        boolean allowed = isEnableSelected(selectedItem);
        boolean useDefault = isUseDefaultSelected(selectedItem);

        // Update class variable
        if (selectedItem == insertTab) {
            bUseDefaultForInsert = useDefault;
        } else if (selectedItem == updateTab) {
            bUseDefaultForUpdate = useDefault;
        } else if (selectedItem == deleteTab) {
            bUseDefaultForDelete = useDefault;
        }

        // Set enabled states based on selections
        setCheckBoxEnabledStates(selectedItem);

        // boolean abortedUseDefault = false;
        if (eventSource == chkUseDefaultForInsert || eventSource == chkUseDefaultForUpdate
                   || eventSource == chkUseDefaultForDelete) {
            // abortedUseDefault = handleUseDefaultCheckBoxChanged(eventSource,useDefault,allowed);
            handleUseDefaultCheckBoxChanged(eventSource, useDefault, allowed);
        }

        setEditableStatus(selectedItem);
    }

    /**
     * handler for useDefault check-box state changes
     * 
     * @param eventSource the source of the event
     * @param useDefault UseDefault state
     * @param isAllowed the isAllowed state
     * @return 'true' if aborted
     */
    private boolean handleUseDefaultCheckBoxChanged( Object eventSource,
                                                     boolean useDefault,
                                                     boolean isAllowed ) {
        Object selectedItem = getSelectedItem();
        boolean abortUseDefault = false;
        // ------------------------------
        // Current Tab is Insert
        // ------------------------------
        if ((selectedItem == insertTab && eventSource == chkUseDefaultForInsert)
            || (selectedItem == updateTab && eventSource == chkUseDefaultForUpdate)
            || (selectedItem == deleteTab && eventSource == chkUseDefaultForDelete)) {

            String typeStr = BLANK;
            int cmdType = QueryValidator.SELECT_TRNS;
            if (selectedItem == insertTab) {
                typeStr = INSERT_SQL_TYPE;
                cmdType = QueryValidator.INSERT_TRNS;
            } else if (selectedItem == updateTab) {
                typeStr = UPDATE_SQL_TYPE;
                cmdType = QueryValidator.UPDATE_TRNS;
            } else if (selectedItem == deleteTab) {
                typeStr = DELETE_SQL_TYPE;
                cmdType = QueryValidator.DELETE_TRNS;
            }
            // UseDefault was selected, set useDefault flag and SqlString
            if (useDefault) {
                // Warn user that choosing the default will wipe out the current values
                boolean shouldReplace = shouldReplaceSqlText(typeStr);
                // Use the Default
                if (shouldReplace) {
                    // Set the UseDefault flag
                    if (cmdType == QueryValidator.INSERT_TRNS) {
                        TransformationHelper.setInsertSqlDefault(currentMappingRoot, true, false, this);
                        if(  chkUseDefaultForInsert.getSelection() ) {
                        	useDefaultForInsertLabel.setText(Messages.DefaultUpdateMessageOK);
                        }
                    } else if (cmdType == QueryValidator.UPDATE_TRNS) {
                        TransformationHelper.setUpdateSqlDefault(currentMappingRoot, true, false, this);
                        if(  chkUseDefaultForInsert.getSelection() ) {
                        	useDefaultForUpdateLabel.setText(Messages.DefaultUpdateMessageOK);
                        }
                    } else if (cmdType == QueryValidator.DELETE_TRNS) {
                        TransformationHelper.setDeleteSqlDefault(currentMappingRoot, true, false, this);
                        if(  chkUseDefaultForInsert.getSelection() ) {
                        	useDefaultForDeleteLabel.setText(Messages.DefaultUpdateMessageOK);
                        }
                    }
                    SqlMappingRootCache.invalidateStatus(currentMappingRoot, true, this);
                    
                    setEditorContent(selectedItem, true, this, true, false);
                } else {
                    // re-enable the check-box
                    abortUseDefault = true;
                    removeCheckBoxListeners(selectedItem);
                    if (selectedItem == insertTab) {
                        chkUseDefaultForInsert.setSelection(false);
                    } else if (selectedItem == updateTab) {
                        chkUseDefaultForUpdate.setSelection(false);
                    } else if (selectedItem == deleteTab) {
                        chkUseDefaultForDelete.setSelection(false);
                    }

                    addCheckBoxListeners(selectedItem);
                }
                // UseDefault was de-selected, set the properties from the editorPanel
            } else if (!useDefault) {
                // Set the UseDefault flag
                if (cmdType == QueryValidator.INSERT_TRNS) {
                    TransformationHelper.setInsertSqlDefault(currentMappingRoot, false, false, this);
                    useDefaultForInsertLabel.setText(Messages.DefaultUpdateMessageOverride);
                } else if (cmdType == QueryValidator.UPDATE_TRNS) {
                    TransformationHelper.setUpdateSqlDefault(currentMappingRoot, false, false, this);
                    useDefaultForUpdateLabel.setText(Messages.DefaultUpdateMessageOverride);
                } else if (cmdType == QueryValidator.DELETE_TRNS) {
                    TransformationHelper.setDeleteSqlDefault(currentMappingRoot, false, false, this);
                    useDefaultForDeleteLabel.setText(Messages.DefaultUpdateMessageOverride);
                }
                // This gets the text from the EditorPanel and sets properties
                SqlMappingRootCache.invalidateStatus(currentMappingRoot, true, this);
                String sql = currentSqlEditor.getText();
                TransformationHelper.setSqlString(currentMappingRoot, sql, cmdType, false, this);
                setEditorContent(selectedItem, true, this, true, false);
            }
        }
        return abortUseDefault;
    }

    /**
     * Update the reconcilable states on the transformation editor based on the supplied SetQuery. The SetQuery passed in is
     * resolvable.
     * 
     * @param unionQuery the SetQuery
     */
    private void updateEditorSetQueryStates( SetQuery unionQuery ) {
        List queries = SetQueryUtil.getQueryList(unionQuery);
        int nQueries = queries.size();
        List reconciledList = new ArrayList(nQueries);
        for (int i = 0; i < nQueries; i++) {
            QueryCommand qCommand = (QueryCommand)queries.get(i);
            boolean nameMatchReqd = false;
            if (i == 0) {
                nameMatchReqd = true;
            }
            boolean isReconciled = TransformationMappingHelper.targetAndCommandReconcile(currentMappingRoot,
                                                                                         qCommand,
                                                                                         nameMatchReqd);
            reconciledList.add(new Boolean(isReconciled));
        }
        getCurrentSqlEditor().setSetQueryReconciledStates(reconciledList);

    }

    /**
     * Set UseDefault boolean states based on the status of the sqlStrings. If the sql is null or empty, the useDefault state is
     * set to true;
     * 
     * @param transMappingRoot the SqlTransformationMappingRoot
     */
    private void setUseDefaultStates( SqlTransformationMappingRoot transMappingRoot ) {
        if (transMappingRoot != null) {
            // Set Insert UseDefault state
            if (TransformationHelper.isInsertSqlDefault(transMappingRoot)) {
                bUseDefaultForInsert = true;
            } else {
                bUseDefaultForInsert = false;
            }
            // Set Update UseDefault state
            if (TransformationHelper.isUpdateSqlDefault(transMappingRoot)) {
                bUseDefaultForUpdate = true;
            } else {
                bUseDefaultForUpdate = false;
            }
            // Set Delete UseDefault state
            if (TransformationHelper.isDeleteSqlDefault(transMappingRoot)) {
                bUseDefaultForDelete = true;
            } else {
                bUseDefaultForDelete = false;
            }
        }
    }

    /**
     * Set the check-box states for the supplied mappingRoot and item.
     * 
     * @param transMappingRoot The MappingRoot
     * @param item The select, update, insert or delete item
     */
    private void setCheckBoxStates( Object transMappingRoot,
                                    Object item ) {
        if (TransformationHelper.isSqlTransformationMappingRoot(transMappingRoot)) {
            // Remove check-box Listeners
            removeCheckBoxListeners(item);

            // Get the command type for the tab
            int cmdType = getCommandTypeForItem(item);

            // -------------------------------------------------------------------
            // Set useDefault selection states for the statement type.
            // -------------------------------------------------------------------
            if (cmdType == QueryValidator.INSERT_TRNS) {
                if (TransformationHelper.isInsertSqlDefault((EObject)transMappingRoot)) {
                    bUseDefaultForInsert = true;
                } else {
                    bUseDefaultForInsert = false;
                }
                if (!chkUseDefaultForInsert.isDisposed()) {
                    chkUseDefaultForInsert.setSelection(bUseDefaultForInsert);
                }
            } else if (cmdType == QueryValidator.UPDATE_TRNS) {
                if (TransformationHelper.isUpdateSqlDefault((EObject)transMappingRoot)) {
                    bUseDefaultForUpdate = true;
                } else {
                    bUseDefaultForUpdate = false;
                }
                if (!chkUseDefaultForUpdate.isDisposed()) {
                    chkUseDefaultForUpdate.setSelection(bUseDefaultForUpdate);
                }
            } else if (cmdType == QueryValidator.DELETE_TRNS) {
                if (TransformationHelper.isDeleteSqlDefault((EObject)transMappingRoot)) {
                    bUseDefaultForDelete = true;
                } else {
                    bUseDefaultForDelete = false;
                }
                if (!chkUseDefaultForDelete.isDisposed()) {
                    chkUseDefaultForDelete.setSelection(bUseDefaultForDelete);
                }
            }
            // -------------------------------------------------------------------
            // Disable useDefault check-boxes if type not allowed
            // -------------------------------------------------------------------
            setCheckBoxEnabledStates(item);

            // Add check-box listeners
            addCheckBoxListeners(item);

            setSupportsUpdatesCheckBoxState();
        }
    }

    private void setSupportsUpdatesCheckBoxState() {
        // FIX for Invalid SWT Thread Access
        // put on SWT thread
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                getCheckBoxContributionForSupportsUpdates().setEnabled(true);
                getCheckBoxContributionForSupportsUpdates().setSelection(getTargetAllowsUpdates());
            }
        }, true);
    }

    /**
     * This just sets the correct check-box enabled states based on selection state of the enable checkboxes.
     */
    private void setCheckBoxEnabledStates( final Object item ) {
        // FIX for Invalid SWT Thread Access
        // put on SWT thread
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                boolean enableState = isEnableSelected(item);
                // Set useDefault INSERT CheckBox State
                if (item == insertTab && !chkUseDefaultForInsert.isDisposed()) {
                    chkUseDefaultForInsert.setEnabled(enableState);
                    // Set useDefault UPDATE CheckBox State
                } else if (item == updateTab && !chkUseDefaultForUpdate.isDisposed()) {
                    chkUseDefaultForUpdate.setEnabled(enableState);
                    // Set useDefault DELETE CheckBox State
                } else if (item == deleteTab && !chkUseDefaultForDelete.isDisposed()) {
                    chkUseDefaultForDelete.setEnabled(enableState);
                }
            }
        }, true);
    }

    /**
     * Determine if the enabled check-box is selected for this item.
     * 
     * @param item
     * @param true if useDefault is selected.
     */
    boolean isEnableSelected( Object item ) {
        boolean result = false;
        if (item == insertTab) {
            result = true;
        } else if (item == updateTab) {
            result = true;
        } else if (item == deleteTab) {
            result = true;
        }
        return result;
    }

    /**
     * Determine if the useDefault check-box is selected for this item.
     * 
     * @param item the type to check
     * @param true if useDefault is selected.
     */
    private boolean isUseDefaultSelected( Object item ) {
        boolean result = false;
        if (item == insertTab) {
            result = chkUseDefaultForInsert.getSelection();
        } else if (item == updateTab) {
            result = chkUseDefaultForUpdate.getSelection();
        } else if (item == deleteTab) {
            result = chkUseDefaultForDelete.getSelection();
        }
        return result;
    }

    private boolean shouldReplaceSqlText( String typeStr ) {
        MessageBox box = new MessageBox(parent.getShell(), SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
        box.setMessage(getString("lostSqlText.text", typeStr));  //$NON-NLS-1$
        box.setText(LOST_SQL_TITLE);

        Boolean bShouldDropSqlText = new Boolean(box.open() == SWT.YES);

        return bShouldDropSqlText.booleanValue();
    }

    /**
     * Method that handles Events from the SqlEditorPanel.
     * 
     * @param e the EventObject
     */
    public void processEvent( final EventObject e ) {
        if (!isEditorValid()) return;

        // -----------------------------------------
        // SqlEditorEvent from QueryEditor Panel
        // -----------------------------------------

        if (isEditorValid()) {
            if (e instanceof SqlEditorEvent && currentMappingRoot != null) {
                handleSqlEditorEvent((SqlEditorEvent)e);
            } else if (e instanceof SqlTransformationStatusChangeEvent) {
                SqlTransformationStatusChangeEvent stsce = (SqlTransformationStatusChangeEvent)e;
                EObject eventMappingRoot = stsce.getMappingRoot();
                // Defect 23295: Adding check here to make sure eventMappingRoot isn't "stale". We invalidate the SqlRoot cache
                // when
                // closing/unloading projects/models and the eResource() == NULL in these cases. Was resulting in in-advertent
                // validations on stale mapping roots.
                if (eventMappingRoot != null && eventMappingRoot.eResource() != null
                    && ModelerCore.getModelEditor().equals(eventMappingRoot, currentMappingRoot)) {
                    handleTransformationStatusChangeEvent(false, e.getSource(), stsce.isOverwriteDirty());
                }
            }
        }
    }

    /**
     * handle Events received from the QueryEditorPanel
     * 
     * @param qeEvent the QueryEditor event
     */
    private void handleSqlEditorEvent( SqlEditorEvent sqlEvent ) {
        // Only respond if the event was initiated by the SqlEditorPanel
        Object eventSource = sqlEvent.getSource();
        int eventType = sqlEvent.getType();
        if (eventSource instanceof SqlEditorPanel) {
            // ----------------------------------------------------------------
            // Query Changes Pending Event from EditorPanel
            // ----------------------------------------------------------------
            if (eventType == SqlEditorEvent.CARET_CHANGED) {
                handleCursorPositionChanged();
            } else if (eventType == SqlEditorEvent.CHANGES_PENDING) {
                handleSqlEditorChangesPending();

            } else {
                // ------------------------------------------------------------------------
                // Query Event from EditorPanel - SQL Validatable, Resolvable or Parsable
                // ------------------------------------------------------------------------
                if (eventType == SqlEditorEvent.VALIDATABLE || eventType == SqlEditorEvent.RESOLVABLE
                    || eventType == SqlEditorEvent.PARSABLE) {
                    handleSqlEditorCommandEvent(sqlEvent.getCommand(), sqlEvent.getSQLString(), eventType, eventSource);
                    // ----------------------------------------------------------------
                    // Query Changed Event from EditorPanel
                    // ----------------------------------------------------------------
                } else if (eventType == SqlEditorEvent.CHANGED) {
                    handleSqlEditorChanged(sqlEvent.getSQLString(), eventSource);
                }
            }
        } else if (eventType == SqlEditorEvent.CHANGES_PENDING) {
            handleSqlEditorChangesPending();
        }

    }

    /**
     * handle the case when a validatable, resolvable, or parsable query is received from the editor panel
     * 
     * @param command the new Command language object
     * @param eventType the EventType received
     * @param eventSource the source of the event
     */
    private void handleSqlEditorCommandEvent( final Command command,
    										  final String sqlString,
                                              int eventType,
                                              Object eventSource ) {
        Object selectedItem = getSelectedItem();
        int cmdType = getCommandTypeForItem(selectedItem);

        // ------------------------------------------------------------------------------------
        // Update the SQL (if necessary) and reconcile Target attributes
        // ------------------------------------------------------------------------------------
        // start txn
        boolean requiredStart = ModelerCore.startTxn(false, false, SQL_UPDATE_TXN_DESCRIPTION, this);
        boolean succeeded = false;
        try {
            // Use this as the source - Handlers will not recognize SqlEditorPanel
            TransformationHelper.setSqlString(currentMappingRoot, sqlString, cmdType, false, this);

            // Reconcile the mapping root Inputs / Attributes / etc to conform to the SQL
            // (TransformationNotificationListener ignores sql Change generated by this panel)
            TransformationMappingHelper.reconcileMappingsOnSqlChange(currentMappingRoot, this);
            succeeded = true;
        } finally {
            // if we started the txn, commit it.
            if (requiredStart) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        // If the command is a SetQuery (UNION), update the reconciled status on the editorPanel
        // Go ahead and check the command regardless if it's resolved or not.
        Command newCommand = TransformationHelper.getCommand(currentMappingRoot, cmdType);
        if (newCommand instanceof SetQuery) {
            updateEditorSetQueryStates((SetQuery)newCommand);
        }

        // Set editor message area
        updateMessagePanel();

        setDirty(false);

        // Fire status event - based on QueryEditorEvent type
        if (eventType == SqlEditorEvent.VALIDATABLE) {
            notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_VALIDATABLE));
        } else if (eventType == SqlEditorEvent.RESOLVABLE) {
            notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_RESOLVABLE));
        } else if (eventType == SqlEditorEvent.PARSABLE) {
            notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_PARSABLE));
        }
    }

    /**
     * handle the case when a resolvable query is received from the editor panel
     * 
     * @param sqlString the the new sqlString from the editor
     * @param eventSource the source of the event
     */
    private void handleSqlEditorChanged( String sqlString,
                                         Object eventSource ) {
        int cmdType = getCommandTypeForItem(getSelectedItem());

        // ------------------------------------------------------------------------------------
        // Update the SQL - only if different than current User SQL
        // ------------------------------------------------------------------------------------
        if (TransformationHelper.isUserSqlDifferent(sqlString, currentMappingRoot, cmdType)) {
            // start txn
            boolean requiredStart = ModelerCore.startTxn(false, SQL_UPDATE_TXN_DESCRIPTION, this);
            boolean succeeded = false;
            try {
                // Use this as source - Handlers will not recognize SqlEditorPanel
                TransformationHelper.setSqlString(currentMappingRoot, sqlString, cmdType, false, this);

                // Reconcile the mapping root Inputs / Attributes / etc to conform to the SQL
                // (TransformationNotificationListener ignores sql Change generated by this panel)
                // This is here to handle case of empty or initial "SELECT * FROM" query - will clear sources
                TransformationMappingHelper.reconcileMappingsOnSqlChange(currentMappingRoot, this);
                succeeded = true;
            } finally {
                // if we started the txn, commit it.
                if (requiredStart) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }
        }

        // Open the Editor MessagePanel
        getSqlEditorPanelWrapper().showMessageArea(true);
        setDirty(false);
        notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_NOT_PARSABLE));
    }

    /**
     * handle the case when a changes pending notification is received from the editor panel
     */
    private void handleSqlEditorChangesPending() {
        getSqlEditorPanelWrapper().setMessage(SQL_CHANGES_PENDING_MSG);
        getSqlEditorPanelWrapper().showMessageArea(true);
        setDirty(true);
        notifyEventListeners(new QueryEditorStatusEvent(this, QueryEditorStatusEvent.QUERY_HAS_PENDING_CHANGES));
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#canRedo()
     * @since 5.5.3
     */
    public boolean canRedo() {
        return getCurrentSqlEditor().getUndoManager().redoable();
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#canUndo()
     * @since 5.5.3
     */
    public boolean canUndo() {
        return getCurrentSqlEditor().getUndoManager().undoable();
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getRedoLabel()
     * @since 5.5.3
     */
    public String getRedoLabel() {
        return getString("redoMenuLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#getUndoLabel()
     * @since 5.5.3
     */
    public String getUndoLabel() {
        return getString("undoMenuLabel"); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#redo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5.3
     */
    public void redo( IProgressMonitor monitor ) {
        getCurrentSqlEditor().getUndoManager().redo();
        monitor.done();
    }

    /**
     * @see com.metamatrix.modeler.ui.undo.IUndoManager#undo(org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5.3
     */
    public void undo( IProgressMonitor monitor ) {
        getCurrentSqlEditor().getUndoManager().undo();
        monitor.done();
    }

    // -------------------------------------------------------------------------
    // Methods to Register, UnRegister, Notify Listeners to this Editors Events
    // -------------------------------------------------------------------------
    /**
     * This method will register the listener for all SqlEditorEvents
     * 
     * @param listener the listener to be registered
     */
    public void addEventListener( EventObjectListener listener ) {
        if (eventListeners == null) {
            eventListeners = new ArrayList();
        }
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    /**
     * This method will un-register the listener for all SqlEditorEvents
     * 
     * @param listener the listener to be un-registered
     */
    public void removeEventListener( EventObjectListener listener ) {
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * This method will notify the registered listeners of a SqlEditorEvent
     */
    private void notifyEventListeners( EventObject event ) {
        // If target is a Virtual Procedure, we need to disable the optimizer button on the SQL panel
        if (toggleOptimizerAction != null) {
            toggleOptimizerAction.setAllowOptimization(true);
        }
        if (eventListeners != null) {
            Iterator iterator = eventListeners.iterator();
            while (iterator.hasNext()) {
                EventObjectListener listener = (EventObjectListener)iterator.next();
                if (listener != null) {
                    listener.processEvent(event);
                }
            }
        }
    }

    /**
     * Set SqlEditorPanel Message For a Command. The EditorPanel Message and it's visibility depend on the command, the current
     * Object and the tab that is currently selected. If the command is null, that means the SQL couldnt be validated, and the
     * EditorPanel Message is not changed (the default editor message is used).
     */
    private void setMessageDisplayForValidSQL() {
        Object selectedItem = getSelectedItem();
        // ------------------------------------------------------------------------
        // InsertTab showing, display Insert Valid Message and close messagePanel
        // ------------------------------------------------------------------------
        if (selectedItem == insertTab) {
            getSqlEditorPanelWrapper().setMessage(INSERT_SQL_MSG + SPACE + IS_VALID_MSG);
            getSqlEditorPanelWrapper().showMessageArea(false);
            // ------------------------------------------------------------------------
            // UpdateTab showing, display Update Valid Message and close messagePanel
            // ------------------------------------------------------------------------
        } else if (selectedItem == updateTab) {
            getSqlEditorPanelWrapper().setMessage(UPDATE_SQL_MSG + SPACE + IS_VALID_MSG);
            getSqlEditorPanelWrapper().showMessageArea(false);
            // ------------------------------------------------------------------------
            // DeleteTab showing, display Delete Valid Message and close messagePanel
            // ------------------------------------------------------------------------
        } else if (selectedItem == deleteTab) {
            getSqlEditorPanelWrapper().setMessage(DELETE_SQL_MSG + SPACE + IS_VALID_MSG);
            getSqlEditorPanelWrapper().showMessageArea(false);
            // ------------------------------------------------------------------------
            // SelectTab showing, check target vs SQL projected symbols
            // ------------------------------------------------------------------------
            // Current MetaObject defines a stored Query
        } else {
            // Start of Message for Stored Query is different
            String sqlTypeMsg = SELECT_SQL_MSG;

            // Compare the SQL ProjectedSymbols vs the TargetGroup
            int cmdType = getCommandTypeForItem(selectedItem);
            boolean[] statusArray = TransformationMappingHelper.compareQueryTargetAndSQLOutput(currentMappingRoot, cmdType);
            boolean targetAndSQLOutSizesOK = statusArray[0];
            boolean targetAndSQLOutNamesOK = statusArray[1];
            boolean targetAndSQLOutTypesOK = statusArray[2];
            // Check the command for References
            int refCount = TransformationSqlHelper.getReferenceCount(currentMappingRoot, cmdType);

            // If all checks are OK, the SQL is Fully Reconciled and NO References
            if (targetAndSQLOutSizesOK && targetAndSQLOutNamesOK && targetAndSQLOutTypesOK && refCount == 0) {
                getSqlEditorPanelWrapper().setMessage(sqlTypeMsg + SPACE + IS_VALID_AND_RECONCILABLE);
                getSqlEditorPanelWrapper().showMessageArea(false);
                // If any check is not OK, refine the message further
            } else {
                StringBuffer buff = new StringBuffer(sqlTypeMsg + SPACE + IS_VALID_NOT_RECONCILABLE);
                // Add Query Output Status (if invalid)
                if (!targetAndSQLOutSizesOK) {
                    if (cmdType == QueryValidator.SELECT_TRNS) {
                        Command cmd = TransformationHelper.getCommand(currentMappingRoot, cmdType);
                        if (cmd.getProjectedSymbols().size() == 0) {
                            buff.append("\n" + QUERY_SIZE_MISMATCH_NO_PROJECTED_SYMBOLS_MSG); //$NON-NLS-1$
                        } else {
                            buff.append("\n" + QUERY_SIZE_MISMATCH_MSG); //$NON-NLS-1$
                        }
                    } else {
                        buff.append("\n" + QUERY_SIZE_MISMATCH_MSG); //$NON-NLS-1$
                    }
                } else if (!targetAndSQLOutNamesOK) {
                    buff.append("\n" + QUERY_NAME_MISMATCH_MSG); //$NON-NLS-1$
                } else if (!targetAndSQLOutTypesOK) {
                    buff.append("\n" + QUERY_TYPE_MISMATCH_MSG); //$NON-NLS-1$
                }
                // Add Message if there are references
                if (refCount > 0) {
                    buff.append("\n" + COMMAND_HAS_REFERENCES_MSG); //$NON-NLS-1$
                    buff.append("\n" + NUMBER_REFERENCES_MSG + refCount); //$NON-NLS-1$
                }
                getSqlEditorPanelWrapper().setMessage(buff.toString());
                getSqlEditorPanelWrapper().showMessageArea(true);
            }
        }
    }

    /**
     * method that the model object editor can use during setFocus() when it touches the resource file and verifies the read-only
     * state of the file. We have no other way to update the editor.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#updateReadOnlyState(boolean)
     * @since 4.2
     */
    public void updateReadOnlyState() {
        if (currentMappingRoot != null) {
            boolean isReadOnly = ModelObjectUtilities.isReadOnly(currentMappingRoot);
            if (isReadOnly != this.currentReadonlyState) {
                this.currentReadonlyState = isReadOnly;
                if (getCurrentSqlEditor() != null) {
                    getSqlEditorPanelWrapper().updateReadOnlyState(isReadOnly);
                    setEditableStatus(currentItem); // defect 13821 - this call respects the state of the checkboxes, unlike the
                    // old call
                }
                // Need to notify actions that need to enable/disable
                notifyEventListeners(new SqlTransformationStatusChangeEvent(currentMappingRoot, this));
            }
            getCheckBoxContributionForSupportsUpdates().setEnabled(!isReadOnly);
            // Update check boxes on insert/update/delete tabs
            updateReadOnlyStateOfCheckBoxes(isReadOnly);
        }
    }

    /*  Update check boxes on insert/update/delete tabs */
    private void updateReadOnlyStateOfCheckBoxes( final boolean isReadOnly ) {
        UiUtil.runInSwtThread(new Runnable() {
            public void run() {
                if (chkUseDefaultForInsert != null) chkUseDefaultForInsert.setEnabled(!isReadOnly);
                if (chkUseDefaultForUpdate != null) chkUseDefaultForUpdate.setEnabled(!isReadOnly);
                if (chkUseDefaultForDelete != null) chkUseDefaultForDelete.setEnabled(!isReadOnly);
            }
        }, true);

    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isDirty()
     */
    public boolean isDirty() {
        return this.isDirty;
    }

    protected void setDirty( boolean isDirty ) {
        this.isDirty = isDirty;

        Iterator listeners = propListeners.getListeners(IPropertyListener.class);

        while (listeners.hasNext()) {
            ((IPropertyListener)listeners.next()).propertyChanged(this, IEditorPart.PROP_DIRTY);
        }

        // Allow actions to disable if they require complete/validate SQL
        updateActions();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isEditingObject(java.lang.Object)
     * @since 4.2
     */
    public boolean isEditingObject( Object modelObject ) {
        if (currentMappingRoot != null && modelObject != null) {
            if (modelObject instanceof InputSet) {
                return false;
            }
            if (TransformationHelper.isSqlTransformationMappingRoot(modelObject)) {
                if (modelObject.equals(currentMappingRoot)) return true;
            } else if ((TransformationHelper.isVirtualSqlTable(modelObject) || TransformationHelper.isSqlVirtualProcedure(modelObject))
                       && !TransformationHelper.isXmlDocument(modelObject)) {
                // get the tRoot object and compare...
                SqlTransformationMappingRoot workingMappingRoot = (SqlTransformationMappingRoot)TransformationHelper.getMappingRoot((EObject)modelObject);
                if (workingMappingRoot != null && workingMappingRoot.equals(currentMappingRoot)) return true;
            }
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#getEditableObject(java.lang.Object)
     * @since 4.2
     */
    public Object getEditableObject( Object modelObject ) {
        // check edit object type and set mapping root.
        SqlTransformationMappingRoot workingMappingRoot = null;

        EObject targetObject = null;

        if (modelObject instanceof Diagram) {
            targetObject = ((Diagram)modelObject).getTarget();
        } else {
            targetObject = (EObject)modelObject;
        }

        // Check to see if this is an XML Service model
        if (targetObject != null) {
            if (targetObject.eResource() != null && TransformationUiResourceHelper.isSqlTransformationResource(targetObject)) {
                if (TransformationHelper.isSqlTransformationMappingRoot(targetObject)) workingMappingRoot = (SqlTransformationMappingRoot)targetObject;
                else if (TransformationHelper.isVirtualSqlTable(targetObject)
                         || TransformationHelper.isSqlVirtualProcedure(targetObject)) {
                    // One last check here, because XMLDocuments are SqlTableAspect eObjects. Must be a valid t-target
                    if (TransformationHelper.isValidSqlTransformationTarget(targetObject)) workingMappingRoot = (SqlTransformationMappingRoot)TransformationHelper.getMappingRoot(targetObject);
                }

                if (workingMappingRoot != null) return workingMappingRoot;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#isResourceValid()
     * @since 4.2
     */
    public boolean isResourceValid() {
        if (currentMappingRoot != null) {
            if (currentMappingRoot.eResource() != null) {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(currentMappingRoot);
                if (mr != null) return true;
            }
        }
        return false;
    }

    private IResource getTargetResource() {
        if (currentMappingRoot != null) {
            if (currentMappingRoot.eResource() != null) {
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(currentMappingRoot);

                return mr.getResource();
            }
        }
        return null;
    }

    /**
     * @return Returns the currentMappingRoot.
     * @since 5.0.1
     */
    public SqlTransformationMappingRoot getCurrentMappingRoot() {
        return this.currentMappingRoot;
    }

    /**
     * Does nothing.
     * 
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#initialize(com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor)
     * @since 5.0.1
     */
    public void initialize( MultiPageModelEditor editor ) {
    	if( editor instanceof ModelEditor ) {
    		this.parentModelEditor = (ModelEditor)editor;
    	}
    }
    
    public void updateMessagePanel() {
    	setEditorMessage(getSelectedItem());
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditorPage#setOverride(com.metamatrix.modeler.ui.editors.ModelObjectEditorPage)
     * @since 5.0.1
     */
    public void setOverride( ModelObjectEditorPage editor ) {
        this.override = editor;
    }

    class CheckBoxContribution extends ControlContribution {
        private Button chkSupportsUpdates;
        private String toolTip;
        
        // Style Contants
        private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;
        Combo cbx = null;

        public CheckBoxContribution( String id ) {
            super(id);
        }

        /**
         * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createControl( Composite parent ) {

            chkSupportsUpdates = WidgetFactory.createCheckBox(parent, SUPPORTS_UPDATE_TEXT, BUTTON_GRID_STYLE);
            if( toolTip != null ) {
            	chkSupportsUpdates.setToolTipText(toolTip);
            }
            chkSupportsUpdates.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    handleSupportsUpdatesCheckBoxChanged();
                }
            });

            // defect 15601 -- keep track of disposal; we will need to recreate the
            // entire contribution when this happens.
            // I call this solution evil and messy, but it matches pretty well how
            // the rest of the class is written.
            chkSupportsUpdates.addDisposeListener(new DisposeListener() {
                public void widgetDisposed( DisposeEvent e ) {
                    chkSupportsUpdatesContribution = null;
                }
            });

            chkSupportsUpdates.setEnabled(!currentReadonlyState);
            chkSupportsUpdates.setSelection(getTargetAllowsUpdates());

            return chkSupportsUpdates;
        }

        public Control getControl() {
            return chkSupportsUpdates;
        }

        public void setSelection( boolean b ) {
            if (chkSupportsUpdates != null) {
                chkSupportsUpdates.setSelection(b);
            } // endif
        }

        public boolean getSelection() {
            if (chkSupportsUpdates != null) {
                return chkSupportsUpdates.getSelection();
            } // endif

            // not initialized yet, default to 'true'
            return true;
        }

        public void setEnabled( boolean enabled ) {
            if (chkSupportsUpdates != null) {
                chkSupportsUpdates.setEnabled(enabled);
            } // endif
        }
        
        public void setToolTipText(String text) {
        	toolTip = text;
        }
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

    public boolean isNoUpdatesAllowed() {
        return this.noUpdatesAllowed;
    }

    public void setNoUpdatesAllowed( boolean theNoUpdatesAllowed ) {
        this.noUpdatesAllowed = theNoUpdatesAllowed;
    }

    protected List customizeActionList( List initialActionList ) {
        return initialActionList;
    }

    protected boolean allowsMultipleTabs() {
        return true;
    }

    protected SqlPanelDropTargetListener createDropTargetListener( SqlEditorPanel sqlPanel,
                                                                   SqlTransformationMappingRoot transformation ) {
        return new SqlPanelDropTargetListener(sqlPanel, transformation, this);
    }
}
