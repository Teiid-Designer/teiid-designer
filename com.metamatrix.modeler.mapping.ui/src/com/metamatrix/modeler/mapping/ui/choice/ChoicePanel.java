/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.choice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.api.exception.MetaMatrixComponentException;
import com.metamatrix.api.exception.query.QueryParserException;
import com.metamatrix.api.exception.query.QueryResolverException;
import com.metamatrix.core.id.UUID;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.transformation.util.SymbolUUIDMappingVisitor;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.choice.IChoiceObject;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.choice.actions.ClearCriteria;
import com.metamatrix.modeler.mapping.ui.choice.actions.HideExcludedOptions;
import com.metamatrix.modeler.mapping.ui.choice.actions.LaunchCriteriaBuilder;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.query.internal.ui.builder.CriteriaBuilder;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.query.internal.ui.sqleditor.sql.ColorManager;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.query.parser.QueryParser;
import com.metamatrix.query.resolver.util.ResolverVisitor;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.lang.Criteria;
import com.metamatrix.query.sql.navigator.DeepPreOrderNavigator;
import com.metamatrix.query.sql.visitor.SQLStringVisitor;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * ChoicePanel
 */
public class ChoicePanel extends SashForm
    implements ISelectionChangedListener, SelectionListener, UiConstants, PluginConstants, INotifyChangedListener {

    /*
     *              
     *     --------------------------------------------------------------
     *                                Table
     *                                                                       [ up ]
     *                                                                       [down] 
     * 
     *     --------------------------------------------------------------
     * 
     *      [edit] [clear]  () hide exluded options  Default: [____error_____]V
     */

    private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;

    private static final String NEED_MAPPING_CLASS_TITLE = UiConstants.Util.getString("ChoicePanel.needMappingClass.title"); //$NON-NLS-1$
    private static final String NEED_MAPPING_CLASS_MSG = UiConstants.Util.getString("ChoicePanel.needMappingClass.text"); //$NON-NLS-1$

    private static final int ORDINAL_INDEX = 0;
    private static final int OPTION_NAME_INDEX = 1;
    private static final int INCLUDES_INDEX = 2;
    private static final int CRITERIA_INDEX = 3;

    private static final String LEFT_PAREN = "("; //$NON-NLS-1$
    private static final String RIGHT_PAREN = ") "; //$NON-NLS-1$
    private static final String COLON = ":"; //$NON-NLS-1$

    IChoiceObject icoChoiceObject;
    private TreeMappingAdapter mappingAdapter;
    private IMappableTree mappableTree;

    private Composite pnlEditTabOuterComposite;
    private CTabFolder tabFolder;
    private CTabItem tiEditTab;
    private CTabItem tiSummaryTab;

    private CriteriaTextViewer svrSummarySource;

    protected final static int VERTICAL_RULER_WIDTH = 12;
    private Document docChoiceCriteria;
    private String NEWLINE = "\n"; //$NON-NLS-1$
    private String INDENT = "     "; //$NON-NLS-1$
    private String DEFAULT = "Default"; //$NON-NLS-1$
    private String UNDEFINED = "<undefined>"; //$NON-NLS-1$

    private Composite pnlTableStuff;
    private Table tblOptionTable;
    TableViewer tvOptionTableViewer;

    private Composite pnlRowMoveButtons;
    private Button btnUp;
    private Button btnDown;

    // Actions and other Toolbar controls
    CLabel lblDefaultTitle;

    private ComboContribution contDefaultComboBoxContribution;
    private LaunchCriteriaBuilder actLaunchCriteriaBuilder;
    private ClearCriteria actClearCriteria;
    private HideExcludedOptions actHideExcludedOptions;
    private ChoiceViewerFilter cvfExcludeFilter;

    private TableContentProvider cpChoiceContentProvider;
    private TableLabelProvider lpChoiceLabelProvider;
    private CriteriaSourceViewerConfiguration csvcViewerConfig;
    private ColorManager colorManager;
    private List lstReservedWords;

    /* use this label provider to get appropriate text for non-table uses like
     * the summary panel and the combobox
     */
    private ModelExplorerLabelProvider melpAdHocLabelProvider;

    // Set column names
    private String[] columnNames = new String[] {ORDINAL_COL_TEXT, NAME_COL_TEXT, INCLUDE_COL_TEXT, CRITERIA_COL_TEXT};

    private static final int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;
    private static final String EDIT_TAB_TEXT = UiConstants.Util.getString("ChoicePanel.editTab.text"); //$NON-NLS-1$
    private static final String SUMMARY_TAB_TEXT = UiConstants.Util.getString("ChoicePanel.summaryTab.text"); //$NON-NLS-1$
    private static final String ORDINAL_COL_TEXT = UiConstants.Util.getString("ChoicePanel.ordinalCol.text"); //$NON-NLS-1$
    private static final String NAME_COL_TEXT = UiConstants.Util.getString("ChoicePanel.nameCol.text"); //$NON-NLS-1$
    private static final String INCLUDE_COL_TEXT = UiConstants.Util.getString("ChoicePanel.includeCol.text"); //$NON-NLS-1$
    private static final String CRITERIA_COL_TEXT = UiConstants.Util.getString("ChoicePanel.criteriaCol.text"); //$NON-NLS-1$
    private static final String UP_BUTTON_TEXT = UiConstants.Util.getString("ChoicePanel.upButton.text"); //$NON-NLS-1$
    private static final String UP_BUTTON_TOOLTIP = UiConstants.Util.getString("ChoicePanel.upButton.toolTip"); //$NON-NLS-1$
    private static final String DOWN_BUTTON_TEXT = UiConstants.Util.getString("ChoicePanel.downButton.text"); //$NON-NLS-1$
    private static final String DOWN_BUTTON_TOOLTIP = UiConstants.Util.getString("ChoicePanel.downButton.toolTip"); //$NON-NLS-1$
    private static final String EDIT_BUTTON_TOOLTIP = UiConstants.Util.getString("ChoicePanel.editButton.toolTip"); //$NON-NLS-1$
    private static final String CLEAR_BUTTON_TOOLTIP = UiConstants.Util.getString("ChoicePanel.clearButton.toolTip"); //$NON-NLS-1$
    private static final String HIDE_CHECKBOX_TOOLTIP = UiConstants.Util.getString("ChoicePanel.hideCheckbox.toolTip"); //$NON-NLS-1$
    private static final String DEFAULT_COMBOBOX_TEXT = UiConstants.Util.getString("ChoicePanel.defaultCombobox.text"); //$NON-NLS-1$
    static final String DEFAULT_COMBOBOX_TOOLTIP = UiConstants.Util.getString("ChoicePanel.defaultCombobox.toolTip"); //$NON-NLS-1$

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public ChoicePanel( Composite parent,
                        IChoiceObject ico ) {
        super(parent, SWT.VERTICAL);
        this.icoChoiceObject = ico;

        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {

        // ----------------------------------
        // Create the Controls (Top) Panel
        // ----------------------------------
        createControl(this);

        // Initialize the Button states
        setButtonStates();

        tblOptionTable.addSelectionListener(this);

        tblOptionTable.addMouseListener(new MouseListener() {
            public void mouseDown( final MouseEvent event ) {
                handleMouseEvent(event);
            }

            public void mouseUp( final MouseEvent event ) {

            }

            public void mouseDoubleClick( final MouseEvent event ) {

            }
        });

        // init with content if available
        if (icoChoiceObject != null) {
            refreshFromBusinessObject();
        } else {
            //            System.out.println("[ChoicePanel.init]  icoChoiceObject is NULL"); //$NON-NLS-1$
        }

    }

    ModelExplorerLabelProvider getAdHocLabelProvider() {
        if (melpAdHocLabelProvider == null) {
            melpAdHocLabelProvider = new ModelExplorerLabelProvider();
        }
        return melpAdHocLabelProvider;
    }

    public void setBusinessObject( IChoiceObject icoChoice ) {
        this.icoChoiceObject = icoChoice;

        // when the business object changes, refresh everything...
        refreshFromBusinessObject();
    }

    public IChoiceObject getChoiceObject() {
        return icoChoiceObject;
    }

    public IChoiceObject getChoiceObject2() {
        return icoChoiceObject;
    }

    public IChoiceObject getChoiceObject3() {
        return icoChoiceObject;
    }

    public void refreshFromBusinessObject() {
        if (!tvOptionTableViewer.getControl().isDisposed()) {
            UiUtil.runInSwtThread(new Runnable() {
                public void run() {
                    // load the table
                    tvOptionTableViewer.setInput(icoChoiceObject);

                    // load the 'default' combobox
                    getComboBoxContributionForDefault().loadItems();

                    // refresh the summary tab's text panel
                    refreshChoiceSummaryPanel();

                    setButtonStates();
                }
            }, true); // endclass runnable
        }
    }

    protected void addNotifyChangedListener() {
        ModelUtilities.addNotifyChangedListener(this);
    }

    public void removeNotifyChangedListener() {
        ModelUtilities.removeNotifyChangedListener(this);
    }

    public void notifyChanged( Notification n ) {
        refreshFromBusinessObject();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelObjectEditor#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl( Composite parent ) {

        // ------------------------------
        // Set layout for the SashForm
        // ------------------------------
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = 600;
        gridData.heightHint = 400;
        this.setLayoutData(gridData);

        // --------------------------------------------------
        // Init the weighting for the top and bottom panels
        // --------------------------------------------------
        // int[] wts = {4,1};
        // this FAILED!: this.setWeights(wts);

        tabFolder = new CTabFolder(parent, SWT.BOTTOM);
        createEditTab(tabFolder);
        createSummaryTab(tabFolder);

        tabFolder.setSelection(0);

        // not sure what should be the parent of this panel:?????
        pnlEditTabOuterComposite = new Composite(tabFolder, SWT.NONE);
        gridLayout = new GridLayout();
        pnlEditTabOuterComposite.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        // 1. Create the table
        createTableStuffPanel(pnlEditTabOuterComposite);

        // 2. add the edit panel to the edit tab
        tiEditTab.setControl(pnlEditTabOuterComposite);

        // add the summary panel to the summary tab
        tiSummaryTab.setControl(svrSummarySource.getControl());

        // 3. establish listening
        registerListeners();
    }

    private void createEditTab( CTabFolder parent ) {
        tiEditTab = new CTabItem(parent, SWT.NONE);
        tiEditTab.setText(EDIT_TAB_TEXT);
        tiEditTab.setToolTipText(EDIT_TAB_TEXT);
    }

    private void createSummaryTab( CTabFolder parent ) {
        tiSummaryTab = new CTabItem(parent, SWT.NONE);
        tiSummaryTab.setText(SUMMARY_TAB_TEXT);
        tiSummaryTab.setToolTipText(SUMMARY_TAB_TEXT);

        colorManager = new ColorManager();
        VerticalRuler verticalRuler = new VerticalRuler(VERTICAL_RULER_WIDTH);
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        lstReservedWords = getReservedWords();

        csvcViewerConfig = new CriteriaSourceViewerConfiguration(colorManager, lstReservedWords);

        svrSummarySource = new CriteriaTextViewer(parent, verticalRuler, styles, colorManager, csvcViewerConfig);

        csvcViewerConfig.setReservedWords(lstReservedWords);

        docChoiceCriteria = new Document();
        svrSummarySource.setDocument(docChoiceCriteria);
        svrSummarySource.setEditable(false);

    }

    void refreshChoiceSummaryPanel() {
        StringBuffer sbCriteria = new StringBuffer();

        // clear the document
        docChoiceCriteria.set(""); //$NON-NLS-1$

        // walk the business object and load the included options
        Iterator it = icoChoiceObject.getOrderedOptions().iterator();

        while (it.hasNext()) {
            Object oOption = it.next();

            if (icoChoiceObject.isIncluded(oOption)) {

                // construct header line
                sbCriteria.append(getNameWithOrdinal(getRowForOption(oOption)) + COLON);
                sbCriteria.append(NEWLINE);

                // construct criteria line
                sbCriteria.append(INDENT);

                String criteriaText = icoChoiceObject.getSqlCriteria(oOption);
                if (criteriaText != null && criteriaText.trim().length() > 0) {
                    sbCriteria.append(criteriaText);
                } else {
                    sbCriteria.append(UNDEFINED);
                }
                sbCriteria.append(NEWLINE);
            }
        }

        // add the default DEFAULT
        sbCriteria.append(DEFAULT + COLON);
        sbCriteria.append(NEWLINE);
        sbCriteria.append(INDENT);

        if (icoChoiceObject.getDefaultOption() != null) {
            TableRow tb = getRowForOption(icoChoiceObject.getDefaultOption());

            // the icoChoiceObject.getDefaultOption may no longer be 'included', so check for null
            if (tb != null) {
                String sName = getNameWithOrdinal(tb);
                sbCriteria.append(sName);
            } else if (!(icoChoiceObject.getDefaultErrorMode().equals(""))) { //$NON-NLS-1$
                sbCriteria.append(icoChoiceObject.getDefaultErrorMode());
            }
        } else if (!(icoChoiceObject.getDefaultErrorMode().equals(""))) { //$NON-NLS-1$
            sbCriteria.append(icoChoiceObject.getDefaultErrorMode());
        }

        docChoiceCriteria.set(sbCriteria.toString());

        // update the text stuff's list of reserved words:
        csvcViewerConfig.setReservedWords(getReservedWords());

        svrSummarySource.refresh();

    }

    private void createTableStuffPanel( Composite parent ) {
        pnlTableStuff = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        pnlTableStuff.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        pnlTableStuff.setLayoutData(gridData);

        // table
        // 1. Create the table
        createTableViewerPanel(pnlTableStuff);

        // 'row move' button panel
        // 2. Create the 'row move' button panel
        createRowMoveButtonPanel(pnlTableStuff);

    }

    /*
     * Create the TableViewerPanel 
     */
    private void createTableViewerPanel( Composite parent ) {
        // Create the table
        createTable(parent);

        // Create and setup the TableViewer
        createTableViewer();
        cpChoiceContentProvider = new TableContentProvider();
        lpChoiceLabelProvider = new TableLabelProvider();

        tvOptionTableViewer.setContentProvider(cpChoiceContentProvider);
        tvOptionTableViewer.setLabelProvider(lpChoiceLabelProvider);

    }

    /**
     * Create the Table
     */
    private void createTable( Composite parent ) {
        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

        tblOptionTable = new Table(parent, style);
        TableLayout layout = new TableLayout();
        tblOptionTable.setLayout(layout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        tblOptionTable.setLayoutData(gridData);

        tblOptionTable.setLinesVisible(true);
        tblOptionTable.setHeaderVisible(true);

        // 1st column: ordinal
        TableColumn column1 = new TableColumn(tblOptionTable, SWT.LEFT, 0);
        column1.setText(ORDINAL_COL_TEXT);
        ColumnWeightData weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        // 2nd column: name
        TableColumn column2 = new TableColumn(tblOptionTable, SWT.LEFT, 1);
        column2.setText(NAME_COL_TEXT);
        weight = new ColumnWeightData(3);
        layout.addColumnData(weight);

        // 3nd column: include
        TableColumn column3 = new TableColumn(tblOptionTable, SWT.LEFT, 2);
        column3.setText(INCLUDE_COL_TEXT);
        weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        // 4nd column: criteria
        TableColumn column4 = new TableColumn(tblOptionTable, SWT.LEFT, 3);
        column4.setText(CRITERIA_COL_TEXT);
        weight = new ColumnWeightData(7);
        layout.addColumnData(weight);
    }

    /**
     * Create the TableViewer
     */
    private void createTableViewer() {

        tvOptionTableViewer = new TableViewer(tblOptionTable);
        tvOptionTableViewer.setUseHashlookup(true);

        tvOptionTableViewer.setColumnProperties(columnNames);

        // Create the cell editors
        CellEditor[] editors = new CellEditor[columnNames.length];

        // Column 1 : Attribute not editable
        editors[0] = null;

        // Column 2 : Binding not editable
        editors[1] = null;

        // Column 3 : 'include' is editable
        editors[2] = null;

        // Column 4 : criteria not editable
        editors[3] = null;

        // Assign the cell editors to the viewer
        tvOptionTableViewer.setCellEditors(editors);

        // set up a filter to support hiding of 'not included' options
        cvfExcludeFilter = new ChoiceViewerFilter();
        tvOptionTableViewer.addFilter(cvfExcludeFilter);

    }

    /**
     * Create the 'row move' button panel
     */
    private void createRowMoveButtonPanel( Composite parent ) {
        pnlRowMoveButtons = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        pnlRowMoveButtons.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.GRAB_VERTICAL);
        pnlRowMoveButtons.setLayoutData(gridData);

        // Up button
        btnUp = WidgetFactory.createButton(pnlRowMoveButtons, UP_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnUp.setImage(UiPlugin.getDefault().getImage(PluginConstants.Images.UP_ICON));

        btnUp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                upButtonPressed();
            }
        });
        btnUp.setToolTipText(UP_BUTTON_TOOLTIP);
        btnUp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // down button
        btnDown = WidgetFactory.createButton(pnlRowMoveButtons, DOWN_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnDown.setImage(UiPlugin.getDefault().getImage(PluginConstants.Images.DOWN_ICON));

        btnDown.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                downButtonPressed();
            }
        });
        btnDown.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnDown.setToolTipText(DOWN_BUTTON_TOOLTIP);

    }

    private void registerListeners() {

    }

    void upButtonPressed() {

        int iSelectedIndex = tblOptionTable.getSelectionIndex();

        // get Choice Object's index for selected row
        Object oSelectedOption = getOptionForTableIndex(iSelectedIndex);
        int iChoiceObjectIndexForSelection = getIndexForOption(oSelectedOption);

        // get Choice Object's index for target row
        Object oTargetOption = getOptionForTableIndex(iSelectedIndex - 1);
        int iChoiceObjectIndexForTarget = getIndexForOption(oTargetOption);

        // Use the source (selection) and target indices to swap the
        // affected options, and updated the ordered list in the choice object
        List lstOrderedOptions = icoChoiceObject.getOrderedOptions();
        lstOrderedOptions = swapOptions(lstOrderedOptions, iChoiceObjectIndexForSelection, iChoiceObjectIndexForTarget);
        icoChoiceObject.setOrderedOptions(lstOrderedOptions);

        // now update the table
        tvOptionTableViewer.refresh();
        tvOptionTableViewer.getTable().setSelection(iSelectedIndex - 1);

        handleTableSelection();

        refreshChoiceSummaryPanel();

        setButtonStates();
    }

    private List swapOptions( List lstOrderedOptions,
                              int iIndexA,
                              int iIndexB ) {
        Object[] oOrderedOptions = lstOrderedOptions.toArray();

        Object oHoldOption = oOrderedOptions[iIndexA];
        oOrderedOptions[iIndexA] = oOrderedOptions[iIndexB];
        oOrderedOptions[iIndexB] = oHoldOption;

        ArrayList aryl = new ArrayList(oOrderedOptions.length);
        for (int i = 0, j = oOrderedOptions.length; i < j; i++) {
            aryl.add(oOrderedOptions[i]);
        }

        return aryl;
    }

    void downButtonPressed() {

        int iSelectedIndex = tblOptionTable.getSelectionIndex();

        // get Choice Object's index for selected row
        Object oSelectedOption = getOptionForTableIndex(iSelectedIndex);
        int iChoiceObjectIndexForSelection = getIndexForOption(oSelectedOption);

        // get Choice Object's index for target row
        Object oTargetOption = getOptionForTableIndex(iSelectedIndex + 1);
        int iChoiceObjectIndexForTarget = getIndexForOption(oTargetOption);

        // Use the source (selection) and target indices to swap the
        // affected options, and updated the ordered list in the choice object
        List lstOrderedOptions = icoChoiceObject.getOrderedOptions();
        lstOrderedOptions = swapOptions(lstOrderedOptions, iChoiceObjectIndexForSelection, iChoiceObjectIndexForTarget);
        icoChoiceObject.setOrderedOptions(lstOrderedOptions);

        // now update the table
        tvOptionTableViewer.refresh();
        tvOptionTableViewer.getTable().setSelection(iSelectedIndex + 1);

        refreshChoiceSummaryPanel();

        setButtonStates();
    }

    private Object getOptionForTableIndex( int iIndex ) {
        //        System.out.println("[ChoicePanel.getOptionForTableIndex] iIndex: " + iIndex ); //$NON-NLS-1$
        TableItem ti = tblOptionTable.getItem(iIndex);
        TableRow trRow = (TableRow)ti.getData();
        Object oOption = trRow.getOption();
        //        System.out.println("[ChoicePanel.getOptionForTableIndex] About to return oOption: " + oOption.toString() ); //$NON-NLS-1$
        return oOption;
    }

    private int getIndexForOption( Object oOption ) {
        // return icoChoiceObject.getOrderedOptions().indexOf( oOption );
        return icoChoiceObject.getOrderedOptions().indexOf(oOption);
    }

    private Object getOptionForNameWithOrdinal( String sNameWithOrdinal ) {
        for (int i = 0; i < tblOptionTable.getItemCount(); i++) {

            TableItem ti = tblOptionTable.getItem(i);
            TableRow trRow = (TableRow)ti.getData();

            if (getNameWithOrdinal(trRow).equals(sNameWithOrdinal)) {
                return trRow.getOption();
            }
        }

        return null;
    }

    TableRow getRowForOption( Object oOption ) {

        for (int i = 0; i < tblOptionTable.getItemCount(); i++) {

            TableItem ti = tblOptionTable.getItem(i);
            TableRow trRow = (TableRow)ti.getData();

            if (trRow.isIncluded() && trRow.getOption() == oOption) {
                return trRow;
            }
        }
        // System.out.println("[ChoicePanel.getRowForOption] About to return null for option: " + oOption );
        return null;
    }

    /*
     * Constructs an identifying string from the ordinal and the option name   
     */
    public String getNameWithOrdinal( TableRow trRow ) {
        String sName = ""; //$NON-NLS-1$

        if (trRow != null) {
            Object oOption = trRow.getOption();
            String sOrdinal = trRow.getColumnText(ORDINAL_INDEX);
            String sOptionName = getAdHocLabelProvider().getText(oOption);

            String sCombinedName = LEFT_PAREN + sOrdinal + RIGHT_PAREN + sOptionName;
            sName = sCombinedName;
        }

        return sName;
    }

    private void handleTableSelection() {
        setButtonStates();
    }

    void defaultComboboxPressed() {
        /*
         * 1. Retrieve the selected string 
         * 2. Determine if it is from the 'ErrorMode' group or not.  If not it belongs
         *    to an option.
         * 3. Update the 'error mode' or 'default' option (as appropriate) on the Choice Object.
         * 
         */
        Combo cbx = getComboBoxContributionForDefault().getControl();
        int iSelectedIndex = cbx.getSelectionIndex();

        if (iSelectedIndex < 0) {
            return;
        }

        String sDefault = cbx.getItem(iSelectedIndex);

        if (getComboBoxContributionForDefault().isFoundInIncludedOptions(sDefault)) {

            // set the default option; clear the default error mode
            icoChoiceObject.setDefaultOption(getOptionForNameWithOrdinal(sDefault));

            icoChoiceObject.setDefaultErrorMode(""); //$NON-NLS-1$
        } else if (getComboBoxContributionForDefault().isFoundInErrorModeValues(sDefault)) {
            // set the default error mode; clear the default option
            icoChoiceObject.setDefaultErrorMode(sDefault);
            icoChoiceObject.setDefaultOption(null);
        } else {

            // clear BOTH the default option and the default error mode
            icoChoiceObject.setDefaultOption(null);
            icoChoiceObject.setDefaultErrorMode(""); //$NON-NLS-1$
        }

        // refresh the summary tab's text panel
        refreshChoiceSummaryPanel();

    }

    private List getReservedWords() {

        ArrayList aryl = new ArrayList();

        Combo cbx = getComboBoxContributionForDefault().getControl();
        if (cbx == null || cbx.isDisposed()) {
            return aryl;
        }

        for (int i = 0; i < cbx.getItemCount(); i++) {
            aryl.add(cbx.getItem(i));
        }

        aryl.add(DEFAULT + COLON);
        return aryl;
    }

    List getIncludedOptions() {
        ArrayList arylIncluded = new ArrayList();

        Iterator it = icoChoiceObject.getOrderedOptions().iterator();

        while (it.hasNext()) {
            Object oOption = it.next();
            if (icoChoiceObject.isIncluded(oOption)) {
                arylIncluded.add(oOption);
            }
        }

        return arylIncluded;
    }

    public void contributeToolbarActions( ToolBarManager toolBarMgr ) {

        toolBarMgr.removeAll();

        toolBarMgr.add(new LabelContribution(DEFAULT_COMBOBOX_TEXT));
        toolBarMgr.add(getComboBoxContributionForDefault());
        toolBarMgr.add(new Separator());
        toolBarMgr.add(getLaunchCriteriaBuilderAction());
        toolBarMgr.add(getClearCriteriaBuilderAction());
        toolBarMgr.add(getHideExcludedOptionsAction());

        toolBarMgr.update(true);

        setButtonStates();
    }

    ComboContribution getComboBoxContributionForDefault() {
        //        System.out.println("[ChoicePanel.getComboBoxContributionForDefault] TOP"); //$NON-NLS-1$
        if (contDefaultComboBoxContribution == null) {
            contDefaultComboBoxContribution = new ComboContribution();
        }
        return contDefaultComboBoxContribution;
    }

    private LaunchCriteriaBuilder getLaunchCriteriaBuilderAction() {

        if (actLaunchCriteriaBuilder == null) {
            actLaunchCriteriaBuilder = new LaunchCriteriaBuilder(this);
            actLaunchCriteriaBuilder.setToolTipText(EDIT_BUTTON_TOOLTIP);

        }
        return actLaunchCriteriaBuilder;
    }

    private ClearCriteria getClearCriteriaBuilderAction() {

        if (actClearCriteria == null) {
            actClearCriteria = new ClearCriteria(this);
            actClearCriteria.setToolTipText(CLEAR_BUTTON_TOOLTIP);
        }
        return actClearCriteria;
    }

    private HideExcludedOptions getHideExcludedOptionsAction() {

        if (actHideExcludedOptions == null) {
            actHideExcludedOptions = new HideExcludedOptions(this);
            actHideExcludedOptions.setToolTipText(HIDE_CHECKBOX_TOOLTIP);
        }
        return actHideExcludedOptions;
    }

    public boolean canLaunchCriteriaBuilder() {
        // set to true if: exactly 1 table row is selected

        return tvOptionTableViewer.getTable().getSelectionCount() > 0;

    }

    public void launchCriteriaBuilder() {

        MappingClass mc = mappingAdapter.getMappingClass(icoChoiceObject.getChoice());
        if (mc == null) {
            EObject parent = icoChoiceObject.getParent();
            while (parent != null && mc == null) {
                mc = mappingAdapter.getMappingClass(parent);
                parent = parent.eContainer();
            }
        }

        // if we cannnot find a Mapping Class, warn the user and quit
        if (mc == null) {
            Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
            MessageDialog.openError(shell, NEED_MAPPING_CLASS_TITLE, NEED_MAPPING_CLASS_MSG);

            return;
        }

        /*  
         * jh note: BuilderTreeProvider must always be created in this method, 
         *          and NEVER maintained in an instance variable between uses.
         *          This is because its constructor modifies the state of the
         *          static class ElementViewerFactory.
         */
        new BuilderTreeProvider();

        ElementViewerFactory.setCriteriaStrategy(new ChoiceCriteriaStrategy());

        List lstMappingClassWrapper = new ArrayList(1);
        // Get all mapping classes about the choice MC
        lstMappingClassWrapper.addAll(getParentMappingClasses(mc));
        // Add the choice's MC
        lstMappingClassWrapper.add(mc);
        ElementViewerFactory.setViewerInput(lstMappingClassWrapper);
        CriteriaBuilder builder = getCriteriaBuilder();

        // launch Criteria Builder with the selected language object or with
        // null to start off with undefined language object

        // get the sql string, if any for currently selected option
        String sSql = icoChoiceObject.getSqlCriteria(getSelectedOption());

        if (sSql != null && !sSql.trim().equals("")) { //$NON-NLS-1$
            builder.setLanguageObject(getCriteria(mc, sSql));
        } else {
            builder.setLanguageObject(null);
        }

        // -------------------------------------------------------------------------
        // Display the Dialog
        // -------------------------------------------------------------------------
        int status = builder.open();

        // -------------------------------------------------------------------------
        // Insert or Replace when Dialog is OK'd, do nothing if cancelled
        // -------------------------------------------------------------------------
        if (status == Window.OK) {

            // retrieve the new sql from the criteria builder
            LanguageObject newCriteria = builder.getLanguageObject();
            final String sqlCriteria = SQLStringVisitor.getSQLString(newCriteria);
            // Recreate the query object so that the references within the new criteria (that the user entered in the criteria
            // builder dialog) get resolved. Can't just persist the command since the command is created as a result of the
            // validation process, not the other way around.
            newCriteria = getCriteria(mc, sqlCriteria);
            updateCriteriaForSelectedRow(sqlCriteria, newCriteria);
        }

        getLaunchCriteriaBuilderAction().selectionChanged();

    }

    // Private method designed to acces the TreeMappingAdapter framework and return
    // the list of parent or previous mapping classes and staging tables that can be used in
    // the choice criteria. Basically uses the same logic as the Input Set Editor.
    private Collection getParentMappingClasses( MappingClass mappingClass ) {
        if (mappingAdapter != null) {
            Collection parentMappingClasses = mappingAdapter.getParentMappingClasses(mappingClass, this.mappableTree, false);
            if (parentMappingClasses != null && !parentMappingClasses.isEmpty()) return parentMappingClasses;
        }

        return Collections.EMPTY_LIST;
    }

    public void setMappingAdapters( TreeMappingAdapter adapter,
                                    IMappableTree tree ) {
        this.mappingAdapter = adapter;
        this.mappableTree = tree;

        // Create the UUID-version or non-UUID-version of the criteria, depending on what we're starting with.
        MappingClass mc = this.mappingAdapter.getMappingClass(this.icoChoiceObject.getChoice());
        if (mc == null) {
            for (EObject eParent = this.icoChoiceObject.getParent(); eParent != null && mc == null; eParent = eParent.eContainer()) {
                mc = this.mappingAdapter.getMappingClass(eParent);
            }
        }
        if (mc != null) {
            final SymbolUUIDMappingVisitor visitor = new SymbolUUIDMappingVisitor();
            for (final Iterator iter = this.icoChoiceObject.getOrderedOptions().iterator(); iter.hasNext();) {
                final Object option = iter.next();
                final String criteriaText = this.icoChoiceObject.getCriteria(option);
                if (criteriaText == null || criteriaText.trim().length() == 0) {
                    continue;
                }
                final Criteria criteria = getCriteria(mc, criteriaText);
                if (criteriaText.indexOf(UUID.PROTOCOL) >= 0) {
                    visitor.convertToUUID(false);
                    DeepPreOrderNavigator.doVisit(criteria, visitor);
                    this.icoChoiceObject.setSqlCriteria(option, criteria.toString());
                } else {
                    this.icoChoiceObject.setSqlCriteria(option, criteriaText);
                    visitor.convertToUUID(true);
                    DeepPreOrderNavigator.doVisit(criteria, visitor);
                    this.icoChoiceObject.setCriteria(option, criteria.toString());
                }
            }
            // Refresh the already-displayed table.
            this.tvOptionTableViewer.refresh();
        }
    }

    private Criteria getCriteria( final MappingClass mappingClass,
                                  final String theCriteria ) {

        // validate/resolve to make sure LanguageObjects have metadata IDs.
        SqlTransformationMappingRoot mappingRoot = (SqlTransformationMappingRoot)TransformationHelper.getTransformationMappingRoot(mappingClass);
        TransformationValidator validator = new TransformationValidator(mappingRoot, false);

        QueryParser parser = new QueryParser();
        Criteria crit = null;
        try {
            crit = parser.parseCriteria(theCriteria);
        } catch (QueryParserException err) {
            // ignore
        }

        QueryMetadataInterface metadata = validator.getQueryMetadata();

        try {
            ResolverVisitor.resolveLanguageObject(crit, metadata);
        } catch (QueryResolverException err) {
            // ignore
        } catch (MetaMatrixComponentException err) {
            // ignore
        }

        return crit;
    }

    private CriteriaBuilder getCriteriaBuilder() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder(shell);
        criteriaBuilder.create();

        return criteriaBuilder;
    }

    public boolean canClearCriteria() {
        // set to true if: exactly 1 table row is selected, and it has criteria
        final Object option = getSelectedOption();
        if (option != null) {
            final String criteria = icoChoiceObject.getSqlCriteria(option);
            if (criteria != null) {
                return criteria.trim().length() > 0;
            }
        }
        return false;
    }

    public Object getSelectedOption() {

        Object oOption = null;

        TableItem[] tiArray = tblOptionTable.getSelection();

        if (tiArray != null && tiArray.length > 0) {

            TableItem tiSelected = tiArray[0];
            TableRow trRow = (TableRow)tiSelected.getData();
            oOption = trRow.getOption();
        }

        return oOption;
    }

    public void clearCriteria() {
        updateCriteriaForSelectedRow("", null); //$NON-NLS-1$
    }

    public void updateCriteriaForSelectedRow( final String sCriteria,
                                              final LanguageObject criteria ) {

        int iSelectedIndex = tvOptionTableViewer.getTable().getSelectionIndex();
        if (iSelectedIndex < 0) {
            iSelectedIndex = 0;
        }
        Object oOption = getOptionForTableIndex(iSelectedIndex);

        icoChoiceObject.setSqlCriteria(oOption, sCriteria);
        if (criteria != null) {
            final SymbolUUIDMappingVisitor visitor = new SymbolUUIDMappingVisitor();
            visitor.convertToUUID(true);
            DeepPreOrderNavigator.doVisit(criteria, visitor);
            icoChoiceObject.setCriteria(oOption, criteria.toString());
        }

        tvOptionTableViewer.refresh();
        tvOptionTableViewer.getTable().setSelection(iSelectedIndex);

        refreshChoiceSummaryPanel();

        getClearCriteriaBuilderAction().selectionChanged();

    }

    public boolean canHideExcludedOptions() {
        // set to true if: Always true; but its behavior toggles (see hideExcludeOptions)

        return true;
    }

    public void hideExcludedOptions() {
        /*
         * if excluded options are currently being shown, hide them
         */

        cvfExcludeFilter.setFilterActive(true);
        tvOptionTableViewer.refresh();
        setButtonStates();

    }

    public void showExcludedOptions() {
        /*
         * if excluded options are currently being hidden, show them
         */
        cvfExcludeFilter.setFilterActive(false);
        tvOptionTableViewer.refresh();
        setButtonStates();

    }

    public void selectionChanged( SelectionChangedEvent event ) {
        setButtonStates();

        getLaunchCriteriaBuilderAction().selectionChanged();
        getClearCriteriaBuilderAction().selectionChanged();

    }

    /**
     * Set the enabled/disabled states of the Buttons.
     */
    void setButtonStates() {
        ClearCriteria clearAction = getClearCriteriaBuilderAction();
        LaunchCriteriaBuilder builderAction = getLaunchCriteriaBuilderAction();

        if (ModelObjectUtilities.isReadOnly(getChoiceObject().getChoice())) {
            // buttons
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);

            // toolbar actions
            clearAction.setEnabled(false);
            builderAction.setEnabled(false);

            // toolbar combobox
            if (getComboBoxContributionForDefault().getControl() != null
                && !getComboBoxContributionForDefault().getControl().isDisposed()) {
                getComboBoxContributionForDefault().getControl().setEnabled(false);
            }

            // table
            tblOptionTable.setEnabled(false);
        } else {
            // buttons
            int iSelectedIndex = tvOptionTableViewer.getTable().getSelectionIndex();

            if (iSelectedIndex > -1) {
                // a row is selected
                btnUp.setEnabled(iSelectedIndex > 0);
                btnDown.setEnabled(iSelectedIndex < (tvOptionTableViewer.getTable().getItemCount() - 1));
            } else {
                // no row selected
                btnUp.setEnabled(false);
                btnDown.setEnabled(false);
            }

            // toolbar actions
            clearAction.selectionChanged();
            builderAction.selectionChanged();

            // toolbar combobox
            if (getComboBoxContributionForDefault().getControl() != null
                && !getComboBoxContributionForDefault().getControl().isDisposed()) {
                getComboBoxContributionForDefault().getControl().setEnabled(true);
            }

            // table
            tblOptionTable.setEnabled(true);
        }
    }

    public void widgetSelected( SelectionEvent e ) {
        if (e.getSource() == tblOptionTable) {
            handleTableSelection();
        } else if (e.getSource() == tabFolder) {

            CTabItem tiCurrSelection = tabFolder.getSelection();

            // update the current editor to match the tab selected
            if (tiCurrSelection == tiEditTab) {

            } else if (tiCurrSelection == tiSummaryTab) {
                // refresh the summary tab's text panel
                refreshChoiceSummaryPanel();

            }
        }

    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    void handleMouseEvent( MouseEvent theEvent ) {

        // 0. bail out if no content yet
        if (icoChoiceObject == null) {
            return;
        }

        // 1. capture row and column
        int selectedColumn = -1;
        int selectedRow = -1;

        TableItem[] selection = tblOptionTable.getSelection();

        if (selection.length > 0) {
            for (int numCols = tblOptionTable.getColumnCount(), i = 0; i < numCols; i++) {
                Rectangle bounds = selection[0].getBounds(i);

                if (bounds.contains(theEvent.x, theEvent.y)) {
                    selectedColumn = i;
                    break;
                }
            }

            selectedRow = tblOptionTable.getSelectionIndex();
        }
        //        System.out.println( "[ChoicePanel.HandleMouseEvent] Selected column="   //$NON-NLS-1$
        // + selectedColumn
        //                           + ", row = "  //$NON-NLS-1$
        // + selectedRow );

        // 2. if 'included' column clicked, apply to metadata
        if (selectedColumn == INCLUDES_INDEX) {

            // resolve which option is selected
            TableItem[] tiArray = tblOptionTable.getSelection();
            TableItem tiSelected = tiArray[0];
            TableRow trRow = (TableRow)tiSelected.getData();
            Object oOption = trRow.getOption();

            // reverse the 'includes' state of that option
            if (icoChoiceObject.isIncluded(oOption)) {

                icoChoiceObject.setIncluded(oOption, false);

                // when changing an option from included, to disincluded, this might affect
                // the state of the defaults, so run the combobox response method
                // first, apply this to the metadata...
                // if this option is the default option, null out the default option
                if (icoChoiceObject.getDefaultOption() == oOption) {
                    icoChoiceObject.setDefaultOption(null);
                    getComboBoxContributionForDefault().getControl().clearSelection();
                }

                // Probably do not need this: defaultComboboxPressed();
            } else {
                //              System.out.println( "[ChoicePanel.HandleMouseEvent] About to set Includes to TRUE" );   //$NON-NLS-1$
                icoChoiceObject.setIncluded(oOption, true);
            }

            // reload the combobox
            getComboBoxContributionForDefault().loadItems();

        }

        // refresh the table and restore its selection state
        tvOptionTableViewer.refresh();
        tvOptionTableViewer.getTable().setSelection(selectedRow);

        // update the text stuff's list of reserved words:
        csvcViewerConfig.setReservedWords(getReservedWords());

        // refresh the summary tab's text panel
        refreshChoiceSummaryPanel();
    }

    class ComboContribution extends ControlContribution {
        Combo cbx = null;

        public ComboContribution() {
            super("myId"); //$NON-NLS-1$
        }

        /**
         * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createControl( Composite parent ) {

            cbx = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);
            cbx.setToolTipText(DEFAULT_COMBOBOX_TOOLTIP);

            cbx.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    defaultComboboxPressed();
                }
            });
            loadItems();
            return cbx;
        }

        public Combo getControl() {
            return cbx;
        }

        public void loadItems() {

            // 1. capture current default selection as string
            // no, we already have a reference to cbx:
            Combo cbx = getComboBoxContributionForDefault().getControl();

            if (cbx == null || cbx.isDisposed()) {
                //                System.out.println("[ChoicePanel$ComboContribution.loadItems] cbx is NULL, so we are bailing out..."); //$NON-NLS-1$
                return;
            }

            // if the cbx has a selection, capture it before and restore it after the reload
            // 1. capture it
            String sCurrentDefault = ""; //$NON-NLS-1$

            // Actually, let's get it from the ChoiceObject, right?
            String sErrorMode = icoChoiceObject.getDefaultErrorMode();
            Object oOption = icoChoiceObject.getDefaultOption();

            String sDefaultOption = ""; //$NON-NLS-1$
            if (oOption != null) {
                TableRow tb = getRowForOption(oOption);

                // the icoChoiceObject.getDefaultOption may no longer be 'included', so check for null
                if (tb != null) {
                    sDefaultOption = getNameWithOrdinal(tb);
                } else {
                    sDefaultOption = ""; //$NON-NLS-1$
                }
            }

            if (sDefaultOption != null && !sDefaultOption.equals("")) { //$NON-NLS-1$
                sCurrentDefault = sDefaultOption;
            } else if (sErrorMode != null && !sErrorMode.equals("")) { //$NON-NLS-1$
                sCurrentDefault = sErrorMode;
            } else {
                sCurrentDefault = ""; //$NON-NLS-1$
            }

            // 2. reload the combobox
            cbx.removeAll();
            loadIncludedOptions();
            loadErrorModeValues();

            // 3. restore selection (note: the reload may have moved this item in the list)
            int iCurrentIndex = -1;

            // determine CURRENT index for this string
            if (!sCurrentDefault.equals("")) { //$NON-NLS-1$

                for (int i = 0; i < cbx.getItemCount(); i++) {
                    if (cbx.getItem(i).equals(sCurrentDefault)) {
                        //                        System.out.println("[ChoicePanel.loadItems] Found sCurrentDefault in cbx at: " + i ); //$NON-NLS-1$
                        iCurrentIndex = i;
                        break;
                    }
                }
            }

            // if it is still there, select it
            if (iCurrentIndex != -1) {
                //                System.out.println("[ChoicePanel.loadItems] About to set 'selected' in cbx to: " + iCurrentIndex ); //$NON-NLS-1$
                cbx.select(iCurrentIndex);
            }
        }

        private void loadIncludedOptions() {
            Iterator it = getIncludedOptions().iterator();

            while (it.hasNext()) {
                Object oOption = it.next();

                cbx.add(getNameWithOrdinal(getRowForOption(oOption)));
            }
        }

        private void loadErrorModeValues() {

            String[] saErrorModeVals = icoChoiceObject.getValidErrorModeValues();

            for (int i = 0; i < saErrorModeVals.length; i++) {

                cbx.add(saErrorModeVals[i]);
            }
        }

        public boolean isFoundInIncludedOptions( String s ) {
            Iterator it = getIncludedOptions().iterator();

            while (it.hasNext()) {
                Object oOption = it.next();

                if (getNameWithOrdinal(getRowForOption(oOption)).equals(s)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isFoundInErrorModeValues( String s ) {

            String[] saErrorModeVals = icoChoiceObject.getValidErrorModeValues();

            for (int i = 0; i < saErrorModeVals.length; i++) {
                if (saErrorModeVals[i].equals(s)) {
                    return true;
                }
            }
            return false;
        }

    }

    class LabelContribution extends ControlContribution {
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
            lblDefaultTitle = WidgetFactory.createLabel(parent, LABEL_GRID_STYLE, sText);
            return lblDefaultTitle;
        }

    }

    class TableContentProvider implements IStructuredContentProvider {

        IChoiceObject icoChoiceObject;

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {
        }

        /**
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements( Object theInputElement ) {

            icoChoiceObject = (IChoiceObject)theInputElement;

            Object[] result = null;
            List lstOptions = icoChoiceObject.getOrderedOptions();

            if ((lstOptions != null) && !lstOptions.isEmpty()) {
                //                    System.out.println("[TableContentProvider.getElements] lstOptions is NOT null or empty"); //$NON-NLS-1$

                int numRows = lstOptions.size();
                result = new Object[numRows];
                int iOrdinal = 0;
                int iOrdinalColContent = 0;

                for (int i = 0; i < numRows; i++) {
                    Object oOption = lstOptions.get(i);

                    // calc ordinal (ordinals only assigned to included options)
                    if (icoChoiceObject.isIncluded(oOption)) {
                        iOrdinalColContent = ++iOrdinal;
                    } else {
                        iOrdinalColContent = -1;
                    }

                    result[i] = new TableRow(oOption, iOrdinalColContent, getAdHocLabelProvider().getText(oOption),
                                             icoChoiceObject.isIncluded(oOption), icoChoiceObject.getSqlCriteria(oOption));

                }
            } else {
                //                    System.out.println("[TableContentProvider.getElements] lstOptions IS NULL or EMPTY"); //$NON-NLS-1$
            }

            return ((lstOptions == null) || lstOptions.isEmpty()) ? new Object[0] : result;
        }

        /**
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         *      java.lang.Object)
         */
        public void inputChanged( Viewer theViewer,
                                  Object theOldInput,
                                  Object theNewInput ) {

            if (theOldInput != null) {
                // do any required cleanup
            }

            icoChoiceObject = (IChoiceObject)theNewInput;
            if (theNewInput != null) {
                theViewer.refresh();
            }
        }

    }

    class TableLabelProvider extends ModelExplorerLabelProvider implements ITableLabelProvider {

        Image imgCheckedCheckBox = com.metamatrix.ui.UiPlugin.getDefault().getImage(com.metamatrix.ui.UiConstants.Images.CHECKED_CHECKBOX);

        Image imgUncheckedCheckBox = com.metamatrix.ui.UiPlugin.getDefault().getImage(com.metamatrix.ui.UiConstants.Images.UNCHECKED_CHECKBOX);

        public Image getColumnImage( Object theElement,
                                     int theIndex ) {
            TableRow trRow = (TableRow)theElement;

            Image imgResult = null;

            switch (theIndex) {

                case OPTION_NAME_INDEX:
                    Object oOption = trRow.getOption();
                    imgResult = super.getImage(oOption);
                    break;

                case INCLUDES_INDEX:
                    if (trRow.isIncluded() == true) {
                        imgResult = imgCheckedCheckBox;
                    } else {
                        imgResult = imgUncheckedCheckBox;
                    }

                    break;

            }
            return imgResult;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object theElement,
                                     int theColumnIndex ) {

            TableRow row = (TableRow)theElement;
            return row.getColumnText(theColumnIndex);
        }

    }

    class TableRow {

        private Object oOption;
        private int iOrdinal;
        private String sOptionName;
        private boolean bIncludes;
        private String sCriteria;

        public TableRow( Object oOption,
                         int iOrdinal,
                         String sOptionName,
                         boolean bIncludes,
                         String sCriteria ) {

            this.oOption = oOption;
            this.iOrdinal = iOrdinal;
            this.sOptionName = sOptionName;
            this.bIncludes = bIncludes;
            this.sCriteria = sCriteria;
        }

        public Object getOption() {
            return oOption;
        }

        public int getOrdinal() {
            return iOrdinal;
        }

        public String getOptionName() {
            return sOptionName;
        }

        public boolean isIncluded() {
            return bIncludes;
        }

        public String getCriteria() {
            return sCriteria;
        }

        public String getColumnText( int theIndex ) {
            String result = ""; //$NON-NLS-1$

            switch (theIndex) {
                case ORDINAL_INDEX:
                    if (iOrdinal == -1) {
                        result = ""; //$NON-NLS-1$
                    } else {
                        result = String.valueOf(iOrdinal);
                    }
                    break;

                case OPTION_NAME_INDEX:
                    result = sOptionName;
                    break;

                case INCLUDES_INDEX:
                    // Note: 'includes' handled by the labelProvider
                    // and a mouse listener, so just use "" here:
                    result = ""; //$NON-NLS-1$
                    break;

                case CRITERIA_INDEX:
                    result = sCriteria;
                    break;
            }

            if (result == null) {
                result = ""; //$NON-NLS-1$
            }

            return result;
        }

        public Object getValue( int theIndex ) {
            String result = "unknown"; //$NON-NLS-1$
            Object oResult = result;

            switch (theIndex) {
                case ORDINAL_INDEX:
                    oResult = new Integer(iOrdinal);
                    break;

                case OPTION_NAME_INDEX:
                    oResult = sOptionName;
                    result = sOptionName;
                    break;

                case INCLUDES_INDEX:
                    oResult = new Boolean(bIncludes);
                    break;

                case CRITERIA_INDEX:
                    oResult = sCriteria;
                    break;
            }

            if (result == null) {

                result = ""; //$NON-NLS-1$
            }

            return oResult;
        }
    }

    public class ChoiceViewerFilter extends ViewerFilter {

        private boolean bFilterActive = false;

        public void setFilterActive( boolean bFilterActive ) {
            this.bFilterActive = bFilterActive;
        }

        /**
         * Returns whether the given element makes it through this filter.
         * 
         * @param viewer the viewer
         * @param parentElement the parent element
         * @param element the element
         * @return <code>true</code> if element is included in the filtered set, and <code>false</code> if excluded
         */
        @Override
        public boolean select( Viewer viewer,
                               Object parentElement,
                               Object element ) {

            if (element instanceof TableRow) {

                TableRow trRow = (TableRow)element;

                if (bFilterActive) {

                    // since we are filtering, only add if included
                    if (icoChoiceObject.isIncluded(trRow.getOption())) {
                        return true;
                    }
                    return false;
                }
                // since we are not filtering, add it unconditionally
                return true;
            }
            return true;
        }

    }
}
