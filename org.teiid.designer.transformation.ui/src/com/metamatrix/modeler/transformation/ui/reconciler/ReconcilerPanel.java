/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.query.internal.ui.builder.ExpressionBuilder;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.query.sql.LanguageObject;
import com.metamatrix.query.sql.symbol.AliasSymbol;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.ExpressionSymbol;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.query.ui.sqleditor.SqlDisplayPanel;
import com.metamatrix.ui.graphics.GlobalUiFontManager;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * SqlEditorPanel
 */
public class ReconcilerPanel extends SashForm implements ISelectionChangedListener, SelectionListener {

    // Style Contants
    private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;

    // Button Contants
    private final String BIND_BUTTON_TEXT = UiConstants.Util.getString("ReconcilerPanel.bindButton.text"); //$NON-NLS-1$
    private final String UNBIND_BUTTON_TEXT = UiConstants.Util.getString("ReconcilerPanel.unbindButton.text"); //$NON-NLS-1$
    private final String NEW_ATTR_BUTTON_TEXT = UiConstants.Util.getString("ReconcilerPanel.newAttrButton.text"); //$NON-NLS-1$
    private final String SET_TO_NULL_BUTTON_TEXT = UiConstants.Util.getString("ReconcilerPanel.setToNullButton.text"); //$NON-NLS-1$
    private final String SET_TO_EXPRESSION_BUTTON_TOOLTIP = UiConstants.Util.getString("ReconcilerPanel.setToExpressionButton.toolTip"); //$NON-NLS-1$

    private final String SHOW_SQL_CHECKBOX_TEXT = UiConstants.Util.getString("ReconcilerPanel.showSqlCheckbox.text"); //$NON-NLS-1$
    private final String QUERY_SCOPE_TEXT = UiConstants.Util.getString("ReconcilerPanel.queryScope.text"); //$NON-NLS-1$
    private final String UNION_SCOPE_TEXT = UiConstants.Util.getString("ReconcilerPanel.unionScope.text"); //$NON-NLS-1$
    private final String UNION_SEGMENT_SCOPE_TEXT = UiConstants.Util.getString("ReconcilerPanel.unionSegmentScope.text"); //$NON-NLS-1$

    private final String DIALOG_STATUS_TITLE = UiConstants.Util.getString("ReconcilerPanel.statusTitle"); //$NON-NLS-1$

    private final String EXPRESSION = "expr"; //$NON-NLS-1$

    private QueryReconcilerHelper reconcilerHelper;
    private ReconcilerObject reconcilerObject;
    private boolean isUnion = false;
    private List builderGroups = new ArrayList();

    private Composite controlsPanel; // controlsPanel is the Top Panel of the SashForm
    private BindingsTablePanel bindingsPanel; // bindings table control
    SqlTablePanel sqlListPanel; // bindings table control
    private boolean sqlShowing = true;
    private SqlDisplayPanel sqlDisplay; // sqlArea is the Bottom Panel of the SashForm

    private ReconcilerDialog reconcilerDialog = null;

    private SashForm topSplitter;
    private Button showSqlCheckbox; // checkbox to toggle SqlDisplay

    private Button bindButton;
    private Button unbindButton;
    private Button newAttrButton;
    private Button nullButton;
    private Button expressionButton;

    private boolean isReadOnly = false;

    private Font fNewBoldFont;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     * @param dialog the dialog that this panel is in
     * @param transformationObj the transformation Mapping root object
     * @param unionQuerySegment the segment of the union query to reconcile. -1 if not a union query or no segment
     */
    public ReconcilerPanel( Composite parent,
                            ReconcilerDialog dialog,
                            Object transformationObj,
                            boolean isUnion,
                            int unionQuerySegment,
                            List builderGroups ) {
        super(parent, SWT.VERTICAL);
        if (transformationObj instanceof SqlTransformationMappingRoot) {
            this.reconcilerHelper = new QueryReconcilerHelper((SqlTransformationMappingRoot)transformationObj, unionQuerySegment);
            this.reconcilerObject = this.reconcilerHelper.getReconcilerObject();
        }
        if (transformationObj instanceof EObject) isReadOnly = ModelObjectUtilities.isReadOnly((EObject)transformationObj);
        this.isUnion = isUnion;
        this.reconcilerDialog = dialog;
        this.builderGroups.addAll(builderGroups);
        init();
    }

    /**
     * Initialize the panel.
     */
    private void init() {
        // ------------------------------
        // Set layout for the SashForm
        // ------------------------------
        GridLayout gridLayout = new GridLayout();
        this.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(gridData);

        // ----------------------------------
        // Create the Controls (Top) Panel
        // ----------------------------------
        createControlsPanel();

        // ----------------------------------
        // Create the SQL (Bottom) Panel
        // ----------------------------------
        createSqlDisplayPanel();

        // --------------------------------------------------
        // Init the weighting for the top and bottom panels
        // --------------------------------------------------
        int[] wts = {4, 1};
        this.setWeights(wts);

        // Initialize the Panels with initial bindings and sql data
        if (reconcilerObject != null) {
            BindingList bList = reconcilerObject.getBindingList();
            SqlList sList = reconcilerObject.getSqlList();

            bindingsPanel.setBindingList(bList);
            sqlListPanel.setSqlList(sList);

            sqlListPanel.setAvailableSymbolNames(reconcilerObject.getAvailableElementSymbols());

            bList.addChangeListener(new BindingChangeHandler());
            sList.addChangeListener(new SqlChangeHandler());
        }

        bindingsPanel.selectFirstUnbound();
        sqlListPanel.selectIndex(0);

        // Initialize the Button states
        setButtonStates();

        String sql = reconcilerObject.getModifiedSql();
        setSqlDisplay(sql);

        // Initialize the message area at the top of the dialog
        reconcilerDialog.setTitle(DIALOG_STATUS_TITLE);
        updateMessageArea();

        bindingsPanel.setTargetLocked(reconcilerObject.isTargetLocked());
        bindingsPanel.setButtonStates();
        bindingsPanel.updateRowColors();

        // Set initial showSqlDisplay state from preferences
        IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
        boolean showSql = prefStore.getBoolean(PluginConstants.Prefs.Reconciler.SHOW_SQL_DISPLAY);
        showSqlCheckbox.setSelection(showSql);
        showSqlArea(showSql);
        // Add listener to checkbox
        showSqlCheckbox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent ev ) {
                toggleSqlButtonPressed();
            }
        });

        // Listen for TableSelection from the Tables
        bindingsPanel.addTableSelectionListener(this);
        sqlListPanel.addTableSelectionListener(this);

        // Listen for TargetLocked Checkbox Selection from the bindings Panel
        bindingsPanel.addTargetLockedCheckboxListener(this);
        updateSqlAndMessageDisplay();
    }

    private void createControlsPanel() {
        topSplitter = new SashForm(this, SWT.HORIZONTAL);

        // BindingsTablePanel is the left column of the controls Panel
        createBindingTablePanel(topSplitter);

        // controlsPanel is the buttons and list
        controlsPanel = new Composite(topSplitter, SWT.NONE);

        // Set the layout
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        controlsPanel.setLayout(gridLayout);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        controlsPanel.setLayoutData(gridData);

        // BindButtonPanel is the center column of the controls Panel
        createBindButtonPanel();

        // SqlListPanel is the right column of the controls Panel
        sqlListPanel = new SqlTablePanel(controlsPanel, isReadOnly);

        topSplitter.setWeights(new int[] {60, 40});
    }

    /**
     * create the bindingTable panel
     * 
     * @param splitter the parent splitter
     */
    private void createBindingTablePanel( SashForm splitter ) {
        boolean isPrimary = reconcilerHelper.isPrimarySelectClause();

        Composite panel = new Composite(splitter, SWT.NONE);
        // Set the layout
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        panel.setLayout(gridLayout);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        panel.setLayoutData(gridData);
        // Add SQL Type Message Here if applicable (i.e. for UNION SECONDARY SELECT CLAUSES?
        CLabel lblSecondaryMessage = null;
        if (isUnion) {
            if (isPrimary) {
                lblSecondaryMessage = WidgetFactory.createLabel(panel, UNION_SCOPE_TEXT);
            } else {
                lblSecondaryMessage = WidgetFactory.createLabel(panel, UNION_SEGMENT_SCOPE_TEXT);
            }
        } else {
            lblSecondaryMessage = WidgetFactory.createLabel(panel, QUERY_SCOPE_TEXT);
        }

        if (!isPrimary) lblSecondaryMessage.setFont(getBoldFont(lblSecondaryMessage.getFont()));

        bindingsPanel = new BindingsTablePanel(panel, isReadOnly);
        // bindingsPanel.setReconcilerObject(reconcilerObject);

        // CheckBox to show/hide sqlDisplay
        showSqlCheckbox = WidgetFactory.createCheckBox(panel, SHOW_SQL_CHECKBOX_TEXT);
    }

    private Font getBoldFont( Font f ) {
        if (fNewBoldFont == null) {
            FontData data = f.getFontData()[0];
            data.setStyle(SWT.BOLD);
            fNewBoldFont = GlobalUiFontManager.getFont(data);
        }
        return fNewBoldFont;
    }

    /**
     * Updates the Message Area at the top of the containing dialog, based upon the current status of the panel
     */
    private void updateMessageArea() {
        String message = reconcilerObject.getStatus();
        int statusType = reconcilerObject.getStatusType();
        if (statusType == IMessageProvider.NONE && reconcilerObject.hasValidModifications()) {
            message = message + "  Press OK to accept changes."; //$NON-NLS-1$
        }
        reconcilerDialog.setMessage(message, statusType);
    }

    private void createBindButtonPanel() {
        Composite bindButtonPanel = new Composite(controlsPanel, SWT.NONE);

        // Set the layout
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        bindButtonPanel.setLayout(gridLayout);

        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.CENTER;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = false;
        bindButtonPanel.setLayoutData(gridData);

        // Add buttons to the Composite
        bindButton = WidgetFactory.createButton(bindButtonPanel, BIND_BUTTON_TEXT, BUTTON_GRID_STYLE);
        bindButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                bindButtonPressed();
            }
        });
        unbindButton = WidgetFactory.createButton(bindButtonPanel, UNBIND_BUTTON_TEXT, BUTTON_GRID_STYLE);
        unbindButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                unbindButtonPressed();
            }
        });
        newAttrButton = WidgetFactory.createButton(bindButtonPanel, NEW_ATTR_BUTTON_TEXT, BUTTON_GRID_STYLE);
        newAttrButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                newAttributeButtonPressed();
            }
        });

        nullButton = WidgetFactory.createButton(bindButtonPanel, SET_TO_NULL_BUTTON_TEXT, BUTTON_GRID_STYLE);
        nullButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                setToNullButtonPressed();
            }
        });

        expressionButton = WidgetFactory.createButton(bindButtonPanel, null, BUTTON_GRID_STYLE);
        expressionButton.setImage(UiPlugin.getDefault().getImage(PluginConstants.Images.EXPRESSION_BUILDER_ICON));
        expressionButton.setToolTipText(SET_TO_EXPRESSION_BUTTON_TOOLTIP);

        expressionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                expressionButtonPressed();
            }
        });

    }

    private void createSqlDisplayPanel() {
        sqlDisplay = new SqlDisplayPanel(this);
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        sqlDisplay.setLayoutData(gridData);
        // sqlArea.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        if (this.reconcilerObject != null) {
            setSqlDisplay(this.reconcilerObject.getModifiedSql());
        }
    }

    /**
     * handles selectionChanges on the BindingsTable
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        Object source = event.getSource();
        if (source.equals(bindingsPanel.getTableViewer())) {
            bindingsPanel.setButtonStates();
        } else if (source.equals(sqlListPanel.getTableViewer())) {
            sqlListPanel.setButtonStates();
            bindingsPanel.setButtonStates();
        }
        // Set BindButton States
        setButtonStates();
    }

    /**
     * handles Locked CheckBox selection changes on the BindingsTable
     */
    public void widgetSelected( SelectionEvent event ) {
        // get the BindingTable checkbox state
        boolean isLocked = bindingsPanel.isTargetLocked();
        // Set reconciler lockState to agree with checkbox
        this.reconcilerHelper.setTargetLocked(isLocked);
        // Set BindButton States
        setButtonStates();
    }

    public void widgetDefaultSelected( SelectionEvent event ) {
    }

    public void showSqlArea( boolean show ) {
        if (show != sqlShowing) {
            if (show) {
                setMaximizedControl(null);
            } else {
                setMaximizedControl(topSplitter);
            }
            sqlShowing = !sqlShowing;
        }
    }

    public boolean isSqlAreaVisible() {
        return sqlShowing;
    }

    /**
     * Set the Text in the SQL Area.
     * 
     * @param sqlString the new SQL
     */
    private void setSqlDisplay( String sqlString ) {
        if (sqlString != null) {
            sqlDisplay.setText(sqlString);
        } else {
            sqlDisplay.setText(""); //$NON-NLS-1$
        }
    }

    /**
     * Set the enabled/disabled states of the Bind Buttons.
     */
    private void setButtonStates() {
        List selectedBindings = bindingsPanel.getSelectedBindings();
        List selectedSymbols = sqlListPanel.getSelectedSymbols();
        if (!isReadOnly) {
            bindButton.setEnabled(reconcilerObject.shouldEnableBind(selectedBindings, selectedSymbols));
            unbindButton.setEnabled(reconcilerObject.shouldEnableUnbind(selectedBindings));
            newAttrButton.setEnabled(true);
            nullButton.setEnabled(!selectedBindings.isEmpty());
            expressionButton.setEnabled(!selectedBindings.isEmpty());
        } else {
            bindButton.setEnabled(false);
            unbindButton.setEnabled(false);
            newAttrButton.setEnabled(false);
            nullButton.setEnabled(false);
            expressionButton.setEnabled(false);
        }
    }

    /**
     * Handler for Bind Button
     */
    void bindButtonPressed() {
        List selectedBindings = bindingsPanel.getSelectedBindings();
        List selectedSymbols = sqlListPanel.getSelectedSymbols();

        // Save last list selections
        Binding lastSelectedBinding = getLastBinding(selectedBindings);
        int lastSelectedIndex = getLastSymbolIndex(selectedSymbols);

        // do the bind
        reconcilerObject.bind(selectedBindings, selectedSymbols);

        String sql = reconcilerObject.getModifiedSql();
        setSqlDisplay(sql);

        updateMessageArea();

        // Reselect based on last selections
        bindingsPanel.selectNextUnbound(lastSelectedBinding);
        sqlListPanel.selectIndex(lastSelectedIndex);
        bindingsPanel.updateRowColors();
        // Reset the button states in the sqlListPanel
        sqlListPanel.setButtonStates();
    }

    /**
     * Handler for Unbind Button
     */
    void unbindButtonPressed() {
        List selectedBindings = bindingsPanel.getSelectedBindings();

        // Save last binding in selection list
        Binding lastSelection = getLastBinding(selectedBindings);

        // Do the unbind
        reconcilerObject.unbind(selectedBindings);

        String sql = reconcilerObject.getModifiedSql();
        setSqlDisplay(sql);

        updateMessageArea();

        // Reset the table selection
        bindingsPanel.selectNextBound(lastSelection);
        bindingsPanel.updateRowColors();
        // Reset the button states in the sqlListPanel
        sqlListPanel.setButtonStates();
    }

    /**
     * Handler for new Attribute Button
     */
    void newAttributeButtonPressed() {
        List selectedSqlList = sqlListPanel.getSelectedSymbols();
        // Create new bindings
        reconcilerObject.createNewBindings(selectedSqlList);

        String sql = reconcilerObject.getModifiedSql();
        setSqlDisplay(sql);

        updateMessageArea();
        bindingsPanel.updateRowColors();
    }

    private Binding getLastBinding( List bindings ) {
        Binding last = null;
        if (bindings.size() > 0) {
            last = (Binding)bindings.get(bindings.size() - 1);
        }
        return last;
    }

    private int getLastSymbolIndex( List symbols ) {
        SqlList sqlList = sqlListPanel.getSqlList();
        int indexOfLast = 0;
        if (sqlList.size() > 0) {
            SingleElementSymbol symbol = sqlList.getSymbolAt(sqlList.size() - 1);
            indexOfLast = sqlList.indexOf(symbol);
        }
        return indexOfLast;
    }

    /**
     * Check whether there are any required mods to the SQL or targetGroup.
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasValidModifications() {
        return reconcilerObject.hasValidModifications();
    }

    /**
     * Apply any required mods.
     * 
     * @param uIndex index of the segment to work on (in the case of a union), -1 if non-union or no segment
     * @param txnSource the transaction source
     */
    public void applyAllModifications( int uIndex,
                                       Object txnSource ) {
        reconcilerHelper.applyAllModifications(uIndex, txnSource);
    }

    public void applyPreModifications( Object txnSource ) {
        reconcilerHelper.applyPreModifications(txnSource);
    }

    public boolean hasPreModifications() {
        return reconcilerHelper.hasPreModifications();
    }

    public String getModifiedSql() {
        return reconcilerObject.getModifiedSql();
    }

    /**
     * Handler for Show/Hide Sql Area Button
     */
    void toggleSqlButtonPressed() {
        // Get current state
        boolean isCurrentlyVisible = isSqlAreaVisible();
        // Set new state opposite of current
        showSqlArea(!isCurrentlyVisible);
        // Save new state to preferences
        IPreferenceStore prefStore = UiPlugin.getDefault().getPreferenceStore();
        prefStore.setValue(PluginConstants.Prefs.Reconciler.SHOW_SQL_DISPLAY, !isCurrentlyVisible);
        UiPlugin.getDefault().savePluginPreferences();
    }

    /**
     * Update the SQL using the current bindings, refresh the SqlDisplay
     */
    void updateSqlAndMessageDisplay() {
        reconcilerObject.updateCommandFromBindings();

        String sql = reconcilerObject.getModifiedSql();
        setSqlDisplay(sql);

        updateMessageArea();
    }

    /**
     * Handler for Resolve Type Button
     */
    void setToNullButtonPressed() {

        if (reconcilerObject != null) {
            // Do the unbind
            List selectedBindings = bindingsPanel.getSelectedBindings();

            reconcilerObject.unbind(bindingsPanel.getSelectedBindings());

            // Need to crate an Expression Symbol (constant = NULL) name = "expr"
            List symbolsList = new ArrayList(selectedBindings.size());
            for (int i = 0; i < selectedBindings.size(); i++) {
                Constant nullConstant = new Constant(null, String.class);
                ExpressionSymbol nullExpression = new ExpressionSymbol(EXPRESSION, nullConstant);
                symbolsList.add(nullExpression);
            }
            reconcilerObject.bind(selectedBindings, symbolsList);

            bindingsPanel.updateRowColors();
        }

        bindingsPanel.getBindingList().refresh(true);

        setButtonStates();
    }

    private Shell getCurrentShell() {
        return UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    void expressionButtonPressed() {
        if (reconcilerObject != null) {
            // load the Groups that are in context into the ElementViewerFactory
            if (!builderGroups.isEmpty()) ElementViewerFactory.setViewerInput(builderGroups);

            // Get Expression Builder
            ExpressionBuilder expressionBuilder = new ExpressionBuilder(getCurrentShell());
            expressionBuilder.create();

            List selBindings = bindingsPanel.getSelectedBindings();
            LanguageObject startingLO = null;
            if (selBindings.size() == 1) {
                SingleElementSymbol expSymbol = null;
                SingleElementSymbol symbol = ((Binding)selBindings.get(0)).getCurrentSymbol();
                if (symbol instanceof AliasSymbol) {
                    expSymbol = ((AliasSymbol)symbol).getSymbol();
                    if (expSymbol instanceof ExpressionSymbol) {
                        startingLO = ((ExpressionSymbol)expSymbol).getExpression();
                    }
                } else if (symbol instanceof ExpressionSymbol) {
                    startingLO = ((ExpressionSymbol)symbol).getExpression();
                }
            }

            expressionBuilder.setLanguageObject(startingLO);

            // -------------------------------------------------------------------------
            // Display the Dialog
            // -------------------------------------------------------------------------
            int status = expressionBuilder.open();

            // -------------------------------------------------------------------------
            // Insert or Replace when Dialog is OK'd, do nothing if cancelled
            // -------------------------------------------------------------------------
            if (status == Window.OK) {
                LanguageObject langObj = expressionBuilder.getLanguageObject();

                // Do the unbind
                List selectedBindings = bindingsPanel.getSelectedBindings();

                reconcilerObject.unbind(bindingsPanel.getSelectedBindings());

                // Need to crate an Expression Symbol (constant = NULL) name = "expr"
                List symbolsList = new ArrayList(1);
                if (langObj instanceof Expression) {
                    ExpressionSymbol newExpression = new ExpressionSymbol(EXPRESSION, (Expression)langObj);
                    symbolsList.add(newExpression);

                    reconcilerObject.bind(selectedBindings, symbolsList);

                    bindingsPanel.getBindingList().refresh(true);

                    bindingsPanel.updateRowColors();
                    setButtonStates();
                }
            }
        }
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class BindingChangeHandler implements IBindingListViewer {

        /**
         * Update the view to reflect the fact that a binding was added to the binding list
         * 
         * @param binding
         */
        public void addBinding( Binding binding ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that a binding was added to the binding list
         * 
         * @param binding
         */
        public void insertBinding( Binding binding,
                                   int index ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that bindings were added to the binding list
         * 
         * @param bindings
         */
        public void addBindings( Object[] bindings ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that a binding was removed from the binding list
         * 
         * @param binding
         */
        public void removeBinding( Binding binding ) {
            // Put the bound symbol back on the unmatched symbols list
            Object sqlSymbol = binding.getCurrentSymbol();
            if (sqlSymbol != null && sqlSymbol instanceof SingleElementSymbol) {
                SingleElementSymbol seSymbol = (SingleElementSymbol)sqlSymbol;
                sqlListPanel.addSymbol(seSymbol);
            }
            sqlListPanel.selectIndex(0);
        }

        /**
         * Update the view to reflect the fact that bindings were removed from the binding list
         * 
         * @param binding
         */
        public void removeBindings( Object[] bindings ) {
            // Put the bound symbols back on the unmatched symbols list
            for (int i = 0; i < bindings.length; i++) {
                Binding binding = (Binding)bindings[i];
                Object sqlSymbol = binding.getCurrentSymbol();
                if (sqlSymbol != null && sqlSymbol instanceof SingleElementSymbol) {
                    SingleElementSymbol seSymbol = (SingleElementSymbol)sqlSymbol;
                    sqlListPanel.addSymbol(seSymbol);
                }
            }
            sqlListPanel.selectIndex(0);
        }

        /**
         * Update the view to reflect the fact that one of the bindings was modified
         * 
         * @param binding
         */
        public void updateBinding( Binding binding ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that one of the symbols was modified
         * 
         * @param updateLabels
         */
        public void refresh( boolean updateLabels ) {
            updateSqlAndMessageDisplay();
        }
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class SqlChangeHandler implements ISqlListViewer {

        /**
         * Update the view to reflect the fact that a symbol was added to the symbol list
         * 
         * @param symbol
         */
        public void addSymbol( SingleElementSymbol symbol ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that a symbol was added to the symbol list
         * 
         * @param symbol
         */
        public void insertSymbol( SingleElementSymbol symbol,
                                  int index ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that symbols were added to the symbol list
         * 
         * @param symbols
         */
        public void addSymbols( Object[] symbols ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that a symbol was removed from the symbol list
         * 
         * @param symbol
         */
        public void removeSymbol( SingleElementSymbol symbol ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that symbols were removed from the symbol list
         * 
         * @param symbol
         */
        public void removeSymbols( Object[] symbols ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that one of the symbols was modified
         * 
         * @param symbol
         */
        public void updateSymbol( SingleElementSymbol symbol ) {
            updateSqlAndMessageDisplay();
        }

        /**
         * Update the view to reflect the fact that one of the symbols was modified
         * 
         * @param updateLabels
         */
        public void refresh( boolean updateLabels ) {
            updateSqlAndMessageDisplay();
        }
    }

    /**
     * We may have created a bold font here if UNION Secondary Query Need to dispose of it to clear up the resource
     * 
     * @see org.eclipse.swt.widgets.Widget#dispose()
     * @since 4.2
     */
    @Override
    public void dispose() {
        if (fNewBoldFont != null && !fNewBoldFont.isDisposed()) fNewBoldFont.dispose();

        super.dispose();
    }

    public void preDispose() {
        if (hasValidModifications()) {
            sqlListPanel.preDispose(!isUnion);
        }
    }
}
