/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.recursion;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.visitor.SQLStringVisitor;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.modeler.mapping.ui.UiPlugin;
import com.metamatrix.modeler.mapping.ui.recursion.actions.ClearCriteria;
import com.metamatrix.modeler.mapping.ui.recursion.actions.LaunchCriteriaBuilder;
import com.metamatrix.modeler.transformation.ui.builder.CriteriaBuilder;
import com.metamatrix.modeler.transformation.validation.SqlTransformationResult;
import com.metamatrix.modeler.transformation.validation.TransformationValidator;
import com.metamatrix.query.internal.ui.builder.util.ElementViewerFactory;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * RecursionPanel
 */
public class RecursionPanel extends SashForm implements SelectionListener, UiConstants, PluginConstants {

    private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;

    private RecursionObject roRecursionObject;
    private Composite pnlOuter;
    CheckBoxContribution recurseContribution;
    private Composite pnlCountControls;
    private Spinner spinCountLimit;
    private int upperRecursionLimit = 10;
    private CLabel lblErrorIfLimitExceeded;
    private Combo cbxErrorIfLimitExceeded;
    private Button btnEditCriteria;

    boolean listenToTextChange = false;

    static final String RECURSE_QUERY_TEXT = UiConstants.Util.getString("RecursionPanel.recurseQuery.text"); //$NON-NLS-1$
    private static final String COUNT_LIMIT_TEXT = UiConstants.Util.getString("RecursionPanel.countLimit.text"); //$NON-NLS-1$
    private static final String ERROR_IF_EXCEEDED_CHECKBOX_TEXT = UiConstants.Util.getString("RecursionPanel.errorIfExceededCheckBox.text"); //$NON-NLS-1$
    private static final String LIMIT_CONDITION_TEXT = UiConstants.Util.getString("RecursionPanel.limitCondition.text"); //$NON-NLS-1$
    private static final String EDIT_LIMIT_CONDITION_TEXT = UiConstants.Util.getString("RecursionPanel.editButton.text"); //$NON-NLS-1$
    private static final String EDIT_LIMIT_CONDITION_TOOLTIP = UiConstants.Util.getString("RecursionPanel.editButton.text"); //$NON-NLS-1$

    private Composite pnlConditionControls;
    private CLabel lblLimitCondition;
    private SourceViewer svrRecursionConditionCriteria;
    private Document docRecursionConditionCriteria;
    private String EMPTY_STRING = ""; //$NON-NLS-1$
    private String DEFAULT_SELECT = "Select * from Dummy Where "; //$NON-NLS-1$

    protected final static int VERTICAL_RULER_WIDTH = 12;

    private int CHECKBOX_INDENT = VERTICAL_RULER_WIDTH;

    String sConditionSql = EMPTY_STRING;

    private LaunchCriteriaBuilder actLaunchCriteriaBuilder;
    private ClearCriteria actClearCriteria;

    private static final int LABEL_GRID_STYLE = GridData.HORIZONTAL_ALIGN_BEGINNING;
    private static final String EDIT_BUTTON_TOOLTIP = UiConstants.Util.getString("RecursionPanel.editAction.toolTip"); //$NON-NLS-1$
    private static final String CLEAR_BUTTON_TOOLTIP = UiConstants.Util.getString("RecursionPanel.clearAction.toolTip"); //$NON-NLS-1$

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public RecursionPanel( Composite parent,
                           RecursionObject roRecursionObject ) {
        super(parent, SWT.VERTICAL);
        this.roRecursionObject = roRecursionObject;

        init();
    }

    public RecursionObject getRecursionObject() {

        return roRecursionObject;
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

        // init with content if available
        if (roRecursionObject != null) {
            // getRecursionObject();
            refreshFromBusinessObject();
        } else {
            //            System.out.println("[RecursionPanel.init]  icoChoiceObject is NULL"); //$NON-NLS-1$
        }

    }

    public void setBusinessObject( RecursionObject roRecursionObject ) {
        this.roRecursionObject = roRecursionObject;

        // when the business object changes, refresh everything...
        refreshFromBusinessObject();
    }

    public void refreshFromBusinessObject() {
        listenToTextChange = false;
        //        System.out.println("[RecursionPanel.refreshFromBusinessObject] TOP"); //$NON-NLS-1$        
        docRecursionConditionCriteria.set(getRecursionObject().getRecursionCriteria());

        // Select the current error mode in the combobox, or default to first one
        String sText = getRecursionObject().getRecursionLimitErrorMode();

        if (sText != null && !sText.equals(EMPTY_STRING)) {

            int iIndex = cbxErrorIfLimitExceeded.indexOf(sText);

            if (iIndex > -1) {
                cbxErrorIfLimitExceeded.select(iIndex);
            } else {
                cbxErrorIfLimitExceeded.select(0);
            }
        } else {
            cbxErrorIfLimitExceeded.select(0);
        }

        // set the value of the spinner
        restoreRecursionLimit();
        spinCountLimit.setSelection(getRecursionObject().getRecursionLimit());

        // set the value of the checkbox
        getCheckBoxContributionForRecurseQuery().setSelection(getRecursionObject().isRecursive());

        // update the enable state of the buttons and actions
        setButtonStates();
        listenToTextChange = true;
    }

    private void restoreRecursionLimit() {
        // set the value of the spinner
        upperRecursionLimit = ModelerCore.getTransformationPreferences().getUpperRecursionLimit();
        upperRecursionLimit = Math.max(upperRecursionLimit, getRecursionObject().getRecursionLimit());
        spinCountLimit.setMinimum(1);
        spinCountLimit.setMaximum(upperRecursionLimit);
        spinCountLimit.setToolTipText(UiConstants.Util.getString("RecursionPanel.limitSpinner.toolTip", //$NON-NLS-1$
                                                                 spinCountLimit.getMinimum(),
                                                                 spinCountLimit.getMaximum()));
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

        pnlOuter = new Composite(this, SWT.NONE);
        gridLayout = new GridLayout();
        pnlOuter.setLayout(gridLayout);
        gridLayout.numColumns = 1;

        // 1. Create the count controls panel
        createCountControlsPanel(pnlOuter);

        // 2. Create the condition panel
        createConditionControlsPanel(pnlOuter);

    }

    private void createCountControlsPanel( Composite parent ) {
        pnlCountControls = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        pnlCountControls.setLayout(gridLayout);
        gridLayout.numColumns = 4;

        // 'count limit' label
        // lblCountLimit =
        WidgetFactory.createLabel(pnlCountControls, LABEL_GRID_STYLE, COUNT_LIMIT_TEXT);

        // 'count limit' spinner
        spinCountLimit = new Spinner(pnlCountControls, SWT.NONE);

        // Reset from preferences
        restoreRecursionLimit();

        spinCountLimit.addModifyListener(new ModifyListener() {
            public void modifyText( ModifyEvent theEvent ) {
                if (listenToTextChange) handleSpinnerChanged();
            }
        });

        // 'error if exceeded' label
        lblErrorIfLimitExceeded = WidgetFactory.createLabel(pnlCountControls, LABEL_GRID_STYLE, ERROR_IF_EXCEEDED_CHECKBOX_TEXT);
        GridData gridData3 = new GridData();
        gridData3.horizontalIndent = 7;
        lblErrorIfLimitExceeded.setLayoutData(gridData3);

        // 'error if exceeded' checkbox
        createErrorIfCountExceededCombobox(pnlCountControls);

    }

    private void createErrorIfCountExceededCombobox( Composite parent ) {

        cbxErrorIfLimitExceeded = new Combo(parent, SWT.READ_ONLY | SWT.BORDER);

        cbxErrorIfLimitExceeded.setToolTipText(ERROR_IF_EXCEEDED_CHECKBOX_TEXT);

        cbxErrorIfLimitExceeded.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                handleCountExceededComboboxPressed();
            }
        });

        // load ErrorModeValues
        String[] saErrorModeVals = getRecursionObject().getValidErrorModeValues();

        for (int i = 0; i < saErrorModeVals.length; i++) {

            cbxErrorIfLimitExceeded.add(saErrorModeVals[i]);
        }

    }

    void handleCountExceededComboboxPressed() {

        int iSelectedIndex = cbxErrorIfLimitExceeded.getSelectionIndex();

        if (iSelectedIndex < 0) {
            return;
        }

        String sErrorMode = cbxErrorIfLimitExceeded.getItem(iSelectedIndex);

        getRecursionObject().setRecursionLimitErrorMode(sErrorMode);

    }

    void handleRecursiveQueryCheckBoxChanged() {
        getRecursionObject().setRecursive(getCheckBoxContributionForRecurseQuery().getSelection());
        getCheckBoxContributionForRecurseQuery().getControl().update();

        setButtonStates();
    }

    void handleSpinnerChanged() {
        int newValue = spinCountLimit.getSelection();
        
        if (newValue != this.roRecursionObject.getRecursionLimit()) {
            getRecursionObject().setRecursionLimit(newValue);
        }
    }

    private void createConditionControlsPanel( Composite parent ) {
        pnlConditionControls = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        pnlConditionControls.setLayout(gridLayout);
        gridLayout.numColumns = 3;

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        pnlConditionControls.setLayoutData(gridData);

        // 'limit condition' label
        lblLimitCondition = WidgetFactory.createLabel(pnlConditionControls, LABEL_GRID_STYLE, LIMIT_CONDITION_TEXT);

        GridData gridData3 = new GridData();
        gridData3.horizontalIndent = CHECKBOX_INDENT;
        lblLimitCondition.setLayoutData(gridData3);

        // 'edit criteria' button
        btnEditCriteria = WidgetFactory.createButton(pnlConditionControls, EDIT_LIMIT_CONDITION_TEXT /*, BUTTON_GRID_STYLE */);
        btnEditCriteria.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                handleEditButtonClicked();
            }
        });

        btnEditCriteria.setToolTipText(EDIT_LIMIT_CONDITION_TOOLTIP);
        GridData gridData4 = new GridData();
        gridData4.horizontalIndent = 5;
        btnEditCriteria.setLayoutData(gridData4);

        // 'filler' label for edit button row
        CLabel lblFiller = WidgetFactory.createLabel(pnlConditionControls, LABEL_GRID_STYLE, EMPTY_STRING);
        GridData gridData5 = new GridData(GridData.FILL_BOTH);
        gridData5.grabExcessHorizontalSpace = true;
        lblFiller.setLayoutData(gridData5);

        // create the SourceViewer for criteria
        VerticalRuler verticalRuler = new VerticalRuler(VERTICAL_RULER_WIDTH);
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        svrRecursionConditionCriteria = new SourceViewer(pnlConditionControls, verticalRuler, styles);

        // create the document
        docRecursionConditionCriteria = new Document();
        svrRecursionConditionCriteria.setDocument(docRecursionConditionCriteria);
        svrRecursionConditionCriteria.setEditable(false);
        svrRecursionConditionCriteria.setRangeIndicator(new DefaultRangeIndicator());

        GridData gridData2 = new GridData(GridData.FILL_BOTH);
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.FILL;

        gridData2.horizontalSpan = 3;

        gridData2.grabExcessHorizontalSpace = true;
        gridData2.grabExcessVerticalSpace = true;

        svrRecursionConditionCriteria.getControl().setLayoutData(gridData2);

    }

    void handleEditButtonClicked() {
        launchCriteriaBuilder();
    }

    public void contributeToolbarActions( ToolBarManager toolBarMgr ) {

        toolBarMgr.removeAll();

        toolBarMgr.add(getCheckBoxContributionForRecurseQuery());
        toolBarMgr.add(new Separator());
        toolBarMgr.add(getLaunchCriteriaBuilderAction());
        toolBarMgr.add(getClearCriteriaBuilderAction());

        toolBarMgr.update(true);

        getCheckBoxContributionForRecurseQuery().setEnabled(true);
        getCheckBoxContributionForRecurseQuery().setSelection(getRecursionObject().isRecursive());

        setButtonStates();

    }

    /**
     * Set the enabled/disabled states of the Buttons.
     */
    private void setButtonStates() {
        if (ModelObjectUtilities.isReadOnly(getRecursionObject().getMappingClass())) {
            spinCountLimit.setEnabled(false);
            cbxErrorIfLimitExceeded.setEnabled(false);
            if (getCheckBoxContributionForRecurseQuery().getControl() != null) {
                getCheckBoxContributionForRecurseQuery().getControl().setEnabled(false);
            }
            btnEditCriteria.setEnabled(false);
            getClearCriteriaBuilderAction().selectionChanged();
            getLaunchCriteriaBuilderAction().selectionChanged();
            return;
        }

        if (getCheckBoxContributionForRecurseQuery().getSelection() == false) {
            spinCountLimit.setEnabled(false);
            cbxErrorIfLimitExceeded.setEnabled(false);
        } else {
            spinCountLimit.setEnabled(true);
            cbxErrorIfLimitExceeded.setEnabled(true);
        } // endif

        spinCountLimit.update();
        cbxErrorIfLimitExceeded.update();

        getClearCriteriaBuilderAction().selectionChanged();
        getLaunchCriteriaBuilderAction().selectionChanged();

        // after updating the Launch Criteria action, set the button enabled state based on it:
        btnEditCriteria.setEnabled(canLaunchCriteriaBuilder());

    }

    private CheckBoxContribution getCheckBoxContributionForRecurseQuery() {

        // CheckBoxContribution
        if (recurseContribution == null) {
            recurseContribution = new CheckBoxContribution(RECURSE_QUERY_TEXT);
        }
        return recurseContribution;
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

    public boolean canLaunchCriteriaBuilder() {
        boolean enable = !ModelObjectUtilities.isReadOnly(getRecursionObject().getMappingClass());

        // if not readonly model ok to launch if mapping class has columns
        if (enable) {
            enable = !getRecursionObject().getMappingClass().getColumns().isEmpty();
        }

        return enable;
    }

    /**
     * This method launches the Criteria Builder Dialog. If the current SQLTextPanel caret is currently within a criteria, the
     * Criteria Builder is launched with the supplied criteria. The supplied criteria is replaced with the modified criteria when
     * the builder is dismissed. If the caret is not within a criteria, the Criteria Builder is launched without a criteria. The
     * resulting criteria is inserted into the editor panel.
     * 
     * @param index the index to launch the builder from.
     */
    public void launchCriteriaBuilder() {
        MappingClass mc = getRecursionObject().getMappingClass();

        /*  
         * jh note: BuilderTreeProvider must always be created in this method, 
         *          and NEVER maintained in an instance variable between uses.
         *          This is because its constructor modifies the state of the
         *          static class ElementViewerFactory.
         */
        new BuilderTreeProvider();

        ElementViewerFactory.setCriteriaStrategy(new RecursionCriteriaStrategy());

        List<MappingClass> lstMappingClassWrapper = new ArrayList<MappingClass>(1);
        lstMappingClassWrapper.add(mc);

        ElementViewerFactory.setViewerInput(lstMappingClassWrapper);
        CriteriaBuilder builder = getCriteriaBuilder();

        // launch Criteria Builder with the selected language object or with
        // null to start off with undefined language object

        // get the sql string, if any for currently selected option
        String sSql = getRecursionObject().getRecursionCriteria();

        // set the language object
        if (sSql != null && !sSql.trim().equals(EMPTY_STRING)) {
            builder.setLanguageObject(getCommand(sSql).getCriteria());
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

            String sCriteriaString = SQLStringVisitor.getSQLString(newCriteria);
            // this: updateCriteriaForSelectedRow( criteriaString );
            docRecursionConditionCriteria.set(sCriteriaString);

            getRecursionObject().setRecursionCriteria(sCriteriaString);
        }

        getLaunchCriteriaBuilderAction().selectionChanged();

        setButtonStates();
    }

    private Query getCommand( String sSql ) {
        String sCommand = DEFAULT_SELECT + sSql;
        SqlTransformationResult result = TransformationValidator.parseSQL(sCommand);
        return (Query)result.getCommand();
    }

    private CriteriaBuilder getCriteriaBuilder() {
        Shell shell = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
        CriteriaBuilder criteriaBuilder = new CriteriaBuilder(shell);
        criteriaBuilder.create();

        return criteriaBuilder;
    }

    public boolean canClearCriteria() {

        if (ModelObjectUtilities.isReadOnly(getRecursionObject().getMappingClass())) {
            return false;
        }
        if (docRecursionConditionCriteria.get().equals(EMPTY_STRING)) {
            return false;
        }
        return true;
    }

    public void clearCriteria() {
        //       System.out.println("[RecursionPanel.clearCriteria] " ); //$NON-NLS-1$
        docRecursionConditionCriteria.set(EMPTY_STRING);
        getRecursionObject().setRecursionCriteria(EMPTY_STRING);

        setButtonStates();
    }

    public void widgetSelected( SelectionEvent e ) {
        //      System.out.println("[RecursionPanel.widgetSelected] e.getSource() is: " + e.getSource() ); //$NON-NLS-1$

        // may not need a class-level widgetSelected

    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    class CheckBoxContribution extends ControlContribution {
        private Button chkRecurseQuery;
        Combo cbx = null;

        public CheckBoxContribution( String id ) {
            super(id);
        }

        /**
         * @see org.eclipse.jface.action.ControlContribution#createControl(org.eclipse.swt.widgets.Composite)
         */
        @Override
        protected Control createControl( Composite parent ) {

            chkRecurseQuery = WidgetFactory.createCheckBox(parent, RECURSE_QUERY_TEXT, BUTTON_GRID_STYLE);

            chkRecurseQuery.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected( final SelectionEvent event ) {
                    handleRecursiveQueryCheckBoxChanged();
                }
            });

            // defect 15601 -- keep track of disposal; we will need to recreate the
            // entire contribution when this happens.
            // I call this solution evil and messy, but it matches pretty well how
            // the rest of the class is written.
            chkRecurseQuery.addDisposeListener(new DisposeListener() {
                public void widgetDisposed( DisposeEvent e ) {
                    recurseContribution = null;
                }
            });

            chkRecurseQuery.setEnabled(true);

            if (getRecursionObject() != null) {
                chkRecurseQuery.setSelection(getRecursionObject().isRecursive());
            } else {
                chkRecurseQuery.setSelection(true);
            }

            return chkRecurseQuery;
        }

        public Control getControl() {
            return chkRecurseQuery;
        }

        public void setSelection( boolean b ) {
            if (chkRecurseQuery != null) {
                chkRecurseQuery.setSelection(b);
            } // endif
        }

        public boolean getSelection() {
            if (chkRecurseQuery != null) {
                return chkRecurseQuery.getSelection();
            } // endif

            // not initialized yet, default to 'true'
            return true;
        }

        public void setEnabled( boolean enabled ) {
            if (chkRecurseQuery != null) {
                chkRecurseQuery.setEnabled(enabled);
            } // endif
        }
    }
}
