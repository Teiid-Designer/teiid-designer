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
package com.metamatrix.modeler.transformation.ui.reconciler.datatype;

import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;
import com.metamatrix.modeler.internal.transformation.util.RuntimeTypeConverter;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.transformation.util.TransformationSqlHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.DatatypeSelectionDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.PluginConstants;
import com.metamatrix.modeler.transformation.ui.UiConstants;
import com.metamatrix.modeler.transformation.ui.UiPlugin;
import com.metamatrix.modeler.transformation.ui.reconciler.Binding;
import com.metamatrix.modeler.transformation.ui.reconciler.BindingLabelProvider;
import com.metamatrix.modeler.transformation.ui.reconciler.BindingList;
import com.metamatrix.modeler.transformation.ui.reconciler.ColorManager;
import com.metamatrix.query.sql.symbol.AggregateSymbol;
import com.metamatrix.query.sql.symbol.AliasSymbol;
import com.metamatrix.query.sql.symbol.Constant;
import com.metamatrix.query.sql.symbol.ElementSymbol;
import com.metamatrix.query.sql.symbol.Expression;
import com.metamatrix.query.sql.symbol.ExpressionSymbol;
import com.metamatrix.query.sql.symbol.Function;
import com.metamatrix.query.sql.symbol.SingleElementSymbol;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.widget.Label;
import com.metamatrix.ui.table.TableSizeAdapter;

/**
 * Panel used by the DatatypeReconcilerDialog to assist in displaying and editing/fixing datatype conflicts for mapped or bound
 * attributes and SQL symbols
 * 
 * @since 5.0
 */
public class DatatypeReconcilerPanel extends SashForm implements ISelectionChangedListener, PluginConstants.Images {

    private static final String BINDINGS_TABLE_TITLE_TEXT = UiConstants.Util.getString("DatatypeReconciler.table.title"); //$NON-NLS-1$
    private static final String BINDINGS_TABLE_ATTR_COL_TEXT = UiConstants.Util.getString("DatatypeReconciler.table.attrCol.title"); //$NON-NLS-1$
    private static final String BINDINGS_TABLE_SQL_COL_TEXT = UiConstants.Util.getString("DatatypeReconciler.table.sqlCol.title"); //$NON-NLS-1$
    private static final String DIALOG_STATUS_TITLE = UiConstants.Util.getString("DatatypeReconciler.statusTitle"); //$NON-NLS-1$

    private static final String RESOLVE_ATTR_GROUP_NAME = UiConstants.Util.getString("DatatypeReconciler.resolveAttr.groupName"); //$NON-NLS-1$
    private static final String RESOLVE_ATTR_GROUP_LOCKED_NAME = UiConstants.Util.getString("DatatypeReconciler.resolveAttr.groupLockedName"); //$NON-NLS-1$
    private static final String RESOLVE_SQL_GROUP_NAME = UiConstants.Util.getString("DatatypeReconciler.resolveSql.groupName"); //$NON-NLS-1$

    private static final String CONVERT_ALL_ATTR_BUTTON = UiConstants.Util.getString("DatatypeReconciler.convertAllAttrButton.text"); //$NON-NLS-1$
    private static final String CONVERT_ALL_SQL_BUTTON = UiConstants.Util.getString("DatatypeReconciler.convertAllSqlButton.text"); //$NON-NLS-1$
    private static final String CONVERT_SELECTED_ATTR_BUTTON = UiConstants.Util.getString("DatatypeReconciler.convertSelectedAttrButton.text"); //$NON-NLS-1$
    private static final String CONVERT_SELECTED_SQL_BUTTON = UiConstants.Util.getString("DatatypeReconciler.convertSelectedSqlButton.text"); //$NON-NLS-1$

    private static final String ALL_RESOLVED_MESSAGE = UiConstants.Util.getString("DatatypeReconciler.statusMessage.allResolved"); //$NON-NLS-1$
    private static final String ONE_OR_MORE_UNRESOLVED_MESSAGE = UiConstants.Util.getString("DatatypeReconciler.statusMessage.unresolved"); //$NON-NLS-1$

    private static final String EDIT_TXT = UiConstants.Util.getString("DatatypeReconciler.edit.text"); //$NON-NLS-1$
    private static final String VIRTUAL_TARGET_ATTRIBUTE_TXT = UiConstants.Util.getString("DatatypeReconciler.virtualTargetAttribute.text"); //$NON-NLS-1$
    private static final String RUNTIME_TYPE_TXT = UiConstants.Util.getString("DatatypeReconciler.runtimeType.text"); //$NON-NLS-1$
    private static final String SQL_SYMBOL_TXT = UiConstants.Util.getString("DatatypeReconciler.sqlSymbol.text"); //$NON-NLS-1$
    private static final String CONVERTED_SYMBOL_TXT = UiConstants.Util.getString("DatatypeReconciler.convertedSymbol.text"); //$NON-NLS-1$

    // Set the table column property names
    private final String ATTRIBUTE_COLUMN = "attribute"; //$NON-NLS-1$
    private final String SQL_COLUMN = "sql"; //$NON-NLS-1$
    // Set column names
    private String[] columnNames = new String[] {ATTRIBUTE_COLUMN, SQL_COLUMN};

    private DatatypeReconcilerDialog datatypeReconcilerDialog = null;

    // Original BindingList that was passed in
    private BindingList originalBindingList;

    // Working Binding List - includes type info
    BindingList bindingList;

    private boolean targetLocked;

    private ColorManager colorManager;
    private Table table;
    private TableViewer tableViewer;

    // Push Buttons
    private Button convertAllAttrsButton, convertAllSqlButton;
    private Button convertSelectedAttrButton, convertSelectedSqlButton;
    private Button showDatatypeDialogButton;

    private CLabel attributeLabel;
    private CLabel attrRuntimeTypeLabel;
    private CLabel sqlSymbolLabel;
    private CLabel symbolRuntimeTypeLabel;
    private CLabel symbolConversionLabel;
    private CLabel attrDatatypeLabel;
    private CLabel symbolWarningLabel;

    private EObject chooserDatatype = null;
    private Group attrGroup = null;
    private Group sqlGroup = null;

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public DatatypeReconcilerPanel( Composite parent,
                                    DatatypeReconcilerDialog dialog,
                                    BindingList bindingList,
                                    boolean targetLocked,
                                    ColorManager colorManager ) {
        super(parent, SWT.VERTICAL);
        this.originalBindingList = bindingList;
        this.targetLocked = targetLocked;
        this.colorManager = colorManager;
        this.datatypeReconcilerDialog = dialog;
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
        gridLayout.marginLeft = 20;
        GridData gridData = new GridData(GridData.FILL_BOTH);
        this.setLayoutData(gridData);

        // Init the symbol arrays from bindings
        this.bindingList = createBindingList(originalBindingList);

        // ----------------------------------
        // Create the Table Viewer Panel
        // ----------------------------------
        createTableViewerPanel(this);

        // ------------------------------------------
        // Create Attribute and SQL Control Panels
        // ------------------------------------------
        createControlsPanel(this);

        // --------------------------------------------------
        // Init the weighting for the top and bottom panels
        // --------------------------------------------------
        int[] wts = {5, 8};
        this.setWeights(wts);

        // Initialize the message area at the top of the dialog
        datatypeReconcilerDialog.setTitle(DIALOG_STATUS_TITLE);
        updateMessageArea();

        // Listen for TableSelection from the Tables
        tableViewer.addSelectionChangedListener(this);
        selectFirstTypeConflict();
    }

    /**
     * Create the Datatype BindingList from the supplied BindingList
     * 
     * @param bindings the bindingList from which to init the arrays
     */
    private BindingList createBindingList( BindingList bindings ) {
        BindingList newBindingList = new BindingList();

        if (bindings != null && bindings.size() > 0) {
            // Set the array values from the supplied bindingList
            for (int i = 0; i < bindings.size(); i++) {
                Binding binding = bindings.get(i);
                newBindingList.add(binding);
            }
        }

        return newBindingList;
    }

    /**
     * Updates the Message Area at the top of the containing dialog, based upon the current status of the panel
     */
    private void updateMessageArea() {
        int statusType = IMessageProvider.NONE;
        String message = ALL_RESOLVED_MESSAGE;
        if (this.bindingList.hasTypeConflict()) {
            message = ONE_OR_MORE_UNRESOLVED_MESSAGE;
            statusType = IMessageProvider.ERROR;
        }
        datatypeReconcilerDialog.setMessage(message, statusType);
    }

    /**
     * Create the tableViewer Panel
     */
    private void createTableViewerPanel( Composite theParent ) {
        Composite tablePanel = new Composite(theParent, SWT.NONE);

        // Set the layout
        GridLayout gridLayout = new GridLayout();
        tablePanel.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        tablePanel.setLayoutData(gridData);

        // Table Header Label
        WidgetFactory.createLabel(tablePanel, GridData.HORIZONTAL_ALIGN_BEGINNING, BINDINGS_TABLE_TITLE_TEXT);

        // Create the table
        createTable(tablePanel);

        // Create and setup the TableViewer
        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);

        tableViewer.setColumnProperties(columnNames);
        tableViewer.setContentProvider(new BindingContentProvider());
        tableViewer.setLabelProvider(new BindingLabelProvider(true));
        // The input for the table viewer is the instance of BindingList
        tableViewer.setInput(bindingList);
        updateRowColors();
    }

    /**
     * Create the Table
     */
    private void createTable( Composite parent ) {
        int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

        table = new Table(parent, style);
        TableLayout layout = new TableLayout();
        table.setLayout(layout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1st column with attribute
        TableColumn column1 = new TableColumn(table, SWT.LEFT, 0);
        column1.setText(BINDINGS_TABLE_ATTR_COL_TEXT);
        ColumnWeightData weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        // 2nd column with sql
        TableColumn column2 = new TableColumn(table, SWT.LEFT, 1);
        column2.setText(BINDINGS_TABLE_SQL_COL_TEXT);
        weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        // add a listener to keep the table sized to it's container
        new TableSizeAdapter(table, 10);
    }

    private void createControlsPanel( Composite theParent ) {
        ScrolledComposite scroller = new ScrolledComposite(theParent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

        GridLayout scrolledLayout = new GridLayout();
        scrolledLayout.marginLeft = 5;
        scrolledLayout.marginRight = 5;
        scroller.setLayout(scrolledLayout);

        scroller.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        scroller.setExpandHorizontal(true);
        scroller.setExpandVertical(true);
        scroller.setMinWidth(900);
        scroller.setMinHeight(270);

        Group controlsGroup = WidgetFactory.createGroup(scroller, EDIT_TXT);
        GridLayout controlsLayout = new GridLayout();
        controlsLayout.marginLeft = 0;
        controlsLayout.marginRight = 0;
        controlsGroup.setLayout(controlsLayout);
        controlsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        createResolveAttributePanel(controlsGroup);
        createResolveSqlPanel(controlsGroup);

        scroller.setContent(controlsGroup);
    }

    private void createResolveAttributePanel( Composite parent ) {
        if (this.targetLocked) {
            attrGroup = WidgetFactory.createGroup(parent, RESOLVE_ATTR_GROUP_LOCKED_NAME);
        } else {
            attrGroup = WidgetFactory.createGroup(parent, RESOLVE_ATTR_GROUP_NAME);
        }
        GridLayout gridLayout = new GridLayout();
        attrGroup.setLayout(gridLayout);
        gridLayout.numColumns = 4;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginLeft = 5;
        gridLayout.marginBottom = 5;
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        attrGroup.setLayoutData(gridData);

        Binding binding = bindingList.get(0);
        Object attr = binding.getAttribute();
        // Attribute Datatype Label
        EObject datatype = null;
        attr = binding.getAttribute();
        if (TransformationHelper.isSqlColumn(attr)) {
            datatype = TransformationHelper.getSqlColumnDatatype((EObject)attr);
        }
        String datatypeText = getDatatypeText(datatype);
        Image datatypeImage = getDatatypeImage(datatype);

        attrGroup.setText(VIRTUAL_TARGET_ATTRIBUTE_TXT);

        // --------------------------------------
        // SQL Symbol Label
        // --------------------------------------
        attributeLabel = WidgetFactory.createLabel(attrGroup, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"); //$NON-NLS-1$
        GridData attrGD = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 4, 1);
        attributeLabel.setLayoutData(attrGD);
        attributeLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));

        Label rtTypeLabel = WidgetFactory.createLabel(attrGroup, RUNTIME_TYPE_TXT);
        GridData gdRT = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_BEGINNING, false, false, 2, 1);
        rtTypeLabel.setLayoutData(gdRT);
        WidgetFactory.createLabel(attrGroup, StringUtil.Constants.EMPTY_STRING);

        // --------------------------------------
        // Attribute RuntimeType Label
        // --------------------------------------
        attrRuntimeTypeLabel = WidgetFactory.createLabel(attrGroup,
                                                         datatypeText,
                                                         datatypeImage,
                                                         GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);

        // --------------------------------------
        // Attribute Type Chooser Panel
        // --------------------------------------
        // Create the changeSelectedAttribute Button
        this.convertSelectedAttrButton = WidgetFactory.createButton(attrGroup,
                                                                    CONVERT_SELECTED_ATTR_BUTTON,
                                                                    GridData.HORIZONTAL_ALIGN_BEGINNING);
        this.convertSelectedAttrButton.setEnabled(false);
        this.convertSelectedAttrButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                convertSelectedAttrPressed();
            }
        });

        // Create the changeAllAttributes Button
        this.convertAllAttrsButton = WidgetFactory.createButton(attrGroup,
                                                                CONVERT_ALL_ATTR_BUTTON,
                                                                GridData.HORIZONTAL_ALIGN_BEGINNING);
        this.convertAllAttrsButton.setEnabled(false);
        this.convertAllAttrsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                convertAllAttrsPressed();
            }
        });

        // Create the showDatatypeDialog Button
        this.showDatatypeDialogButton = WidgetFactory.createButton(attrGroup, "Change", GridData.HORIZONTAL_ALIGN_END); //$NON-NLS-1$
        this.showDatatypeDialogButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                showDatatypeDialogPressed();
            }
        });

        attrDatatypeLabel = WidgetFactory.createLabel(attrGroup, "xxxxxxxxxxxxxxxxxxxxxxx", datatypeImage, //$NON-NLS-1$
                                                      GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.GRAB_HORIZONTAL);
        if (this.targetLocked) {
            this.showDatatypeDialogButton.setEnabled(false);
        } else {
            this.showDatatypeDialogButton.setEnabled(true);
        }
    }

    private EObject getChooserDatatype() {
        return this.chooserDatatype;
    }

    private void setChooserDatatype( EObject datatype ) {
        this.chooserDatatype = datatype;

        // Update the data label text and image
        attrDatatypeLabel.setText(getDatatypeText(chooserDatatype));
        attrDatatypeLabel.setImage(getDatatypeImage(chooserDatatype));
    }

    private String getLabelText( Object object ) {
        StringBuffer sb = new StringBuffer();
        if (object != null) {
            // Get Name String
            if (object instanceof EObject) {
                sb.append(getAttributeShortName(object));
            } else if (object instanceof SingleElementSymbol) {
                String shortName = TransformationSqlHelper.getSingleElementSymbolShortName((SingleElementSymbol)object, false);
                sb.append(shortName);
            } else if (object instanceof Binding) {
                sb.append(((Binding)object).getCurrentAttrName());
            }
        }
        return sb.toString();
    }

    private String getTypeText( Object object ) {
        StringBuffer sb = new StringBuffer();
        if (object != null) {
            if (object instanceof Binding) {
                sb.append(getRuntimeTypeString(((Binding)object).getCurrentAttrDatatype()));
            } else {
                sb.append(getRuntimeTypeString(object));
            }
        }
        return sb.toString();
    }

    private Image getLabelImage( Object object ) {
        Image result = null;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getImage(object);
            } else if (object instanceof SingleElementSymbol) {
                result = getImageForSymbol((SingleElementSymbol)object);
            }
        }
        return result;
    }

    private String getDatatypeText( Object object ) {
        String result = null;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getText(object);
            }
        }
        return result;
    }

    private Image getDatatypeImage( Object object ) {
        Image result = null;
        if (object != null) {
            if (object instanceof EObject) {
                result = ModelUtilities.getEMFLabelProvider().getImage(object);
            }
        }
        return result;
    }

    /**
     * Get the Image for the SingleElementSymbol
     */
    private Image getImageForSymbol( SingleElementSymbol seSymbol ) {
        Image result = null;

        // If symbol is AliasSymbol, get underlying symbol
        if (seSymbol != null && seSymbol instanceof AliasSymbol) {
            seSymbol = ((AliasSymbol)seSymbol).getSymbol();
        }
        // ElementSymbol
        if ((seSymbol instanceof ElementSymbol)) {
            result = UiPlugin.getDefault().getImage(SYMBOL_ICON);
            // AggregateSymbol
        } else if (seSymbol instanceof AggregateSymbol) {
            result = UiPlugin.getDefault().getImage(FUNCTION_ICON);
            // ExpressionSymbol
        } else if (seSymbol instanceof ExpressionSymbol) {
            Expression expression = ((ExpressionSymbol)seSymbol).getExpression();
            if (expression != null && expression instanceof Constant) {
                result = UiPlugin.getDefault().getImage(CONSTANT_ICON);
            } else if (expression != null && expression instanceof Function) {
                result = UiPlugin.getDefault().getImage(FUNCTION_ICON);
            }
        }
        // Undefined
        if (result == null) {
            result = UiPlugin.getDefault().getImage(UNDEFINED_ICON);
        }

        return result;
    }

    private String getRuntimeTypeString( Object object ) {
        String typeStr = null;
        if (object != null) {
            typeStr = RuntimeTypeConverter.getRuntimeType(object);
        }
        if (typeStr == null) {
            typeStr = "unknown"; //$NON-NLS-1$
        }
        return typeStr;
    }

    /**
     * get the attribute short Name.
     * 
     * @param attribute the attribute, may be String or EObject
     * @return the attribute short name
     */
    private String getAttributeShortName( Object attribute ) {
        String name = null;
        if (attribute != null) {
            if (attribute instanceof String) {
                return (String)attribute;
            } else if (attribute instanceof EObject) {
                EObject eObj = (EObject)attribute;
                if (com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn(eObj)) {
                    SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect(eObj);
                    name = columnAspect.getName(eObj);
                }
            }
        }
        return name;
    }

    /**
     * Create the Panel for resolving sql symbols
     */
    private void createResolveSqlPanel( Composite parent ) {
        Group newGroup = WidgetFactory.createGroup(parent, RESOLVE_SQL_GROUP_NAME);
        GridLayout gridLayout = new GridLayout();
        newGroup.setLayout(gridLayout);
        gridLayout.numColumns = 3;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginLeft = 5;
        gridLayout.marginBottom = 5;
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        newGroup.setLayoutData(gridData);
        newGroup.setText(SQL_SYMBOL_TXT);
        // SQL Symbol Label
        // --------------------------------------
        sqlSymbolLabel = WidgetFactory.createLabel(newGroup, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"); //$NON-NLS-1$
        GridData gdSSL = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 3, 1);
        sqlSymbolLabel.setLayoutData(gdSSL);
        sqlSymbolLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLUE));

        // Symbol RuntimeType Label
        // --------------------------------------
        Label rtTypeLabel = WidgetFactory.createLabel(newGroup, RUNTIME_TYPE_TXT);
        GridData gdRT = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 2, 1);
        rtTypeLabel.setLayoutData(gdRT);
        // WidgetFactory.createLabel(newGroup, "");

        Binding binding = bindingList.get(0);
        Object symbol = binding.getCurrentSymbol();
        String text = getLabelText(symbol);
        symbolRuntimeTypeLabel = WidgetFactory.createLabel(newGroup, text, GridData.HORIZONTAL_ALIGN_BEGINNING
                                                                           | GridData.GRAB_HORIZONTAL);

        // Available Conversion Title Label
        // --------------------------------------
        Label cvLabel = WidgetFactory.createLabel(newGroup, GridData.HORIZONTAL_ALIGN_BEGINNING, CONVERTED_SYMBOL_TXT);
        GridData gdCV = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 2, 1);
        cvLabel.setLayoutData(gdCV);

        // Available Sql Conversion Text
        // --------------------------------------

        String convertedSymbol = binding.getSqlConversionText();
        int lossOfPrecIndex = convertedSymbol.indexOf('\n');
        String warningText = StringUtil.Constants.EMPTY_STRING;
        if (lossOfPrecIndex > -1) {
            warningText = convertedSymbol.substring(lossOfPrecIndex + 1, convertedSymbol.length());
            convertedSymbol = convertedSymbol.substring(0, lossOfPrecIndex);
        }

        symbolConversionLabel = WidgetFactory.createLabel(newGroup, convertedSymbol, GridData.FILL_BOTH);

        symbolWarningLabel = WidgetFactory.createLabel(newGroup, warningText, GridData.FILL_BOTH);
        GridData gdWarning = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_FILL, false, false, 3, 1);
        symbolWarningLabel.setLayoutData(gdWarning);

        // Conversion Buttons Panel
        // --------------------------------------

        // Create the changeSelectedSql Button
        this.convertSelectedSqlButton = WidgetFactory.createButton(newGroup,
                                                                   CONVERT_SELECTED_SQL_BUTTON,
                                                                   GridData.HORIZONTAL_ALIGN_BEGINNING);
        this.convertSelectedSqlButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                convertSelectedSqlPressed();
            }
        });

        // Create the changeAllSql Button
        this.convertAllSqlButton = WidgetFactory.createButton(newGroup,
                                                              CONVERT_ALL_SQL_BUTTON,
                                                              GridData.HORIZONTAL_ALIGN_BEGINNING);
        this.convertAllSqlButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                convertAllSqlPressed();
            }
        });
        sqlGroup = newGroup;
    }

    /**
     * Update the Attribute and Sql Conversion Panels when new table row is selected
     * 
     * @param binding the selected Binding
     */
    private void updateDisplaysOnTableSelection( Binding binding ) {
        // Update the Conversion Panel
        updateAttributeConversionPanel(binding);

        // Update the SqlConversion Panel
        updateSymbolConversionPanel(binding);
    }

    /**
     * Update the Attribute Conversion Panel with the provided binding info
     * 
     * @param binding the selected Binding
     */
    private void updateAttributeConversionPanel( Binding binding ) {
        if (binding != null) {
            // get the attribute from the binding
            Object attr = binding.getAttribute();

            // Update the runtimeType label text and image
            attrRuntimeTypeLabel.setText(getTypeText(binding));
            attributeLabel.setImage(getLabelImage(attr));

            String nameStr = getLabelText(attr);
            String labelStr = nameStr;
            attributeLabel.setText(labelStr);

            // Update Datatype Chooser
            EObject dtype = binding.getCurrentAttrDatatype();
            if (dtype != null) {
                setChooserDatatype(dtype);
            }
        }

        updateAttributeConversionPanelButtons(binding);

        // chooserPanel.layout();
        attrGroup.layout();
    }

    /**
     * Update the Attribute Conversion Panel button enabled states.
     */
    private void updateAttributeConversionPanelButtons( Binding binding ) {
        // ------------------------------------------
        // Set the Apply Button Enabled State
        // ------------------------------------------
        boolean enableApply = false;
        if (binding != null && !this.targetLocked) {
            // Set the Apply button enabled state. Enable if the datatypes are different.
            EObject chooserDatatype = getChooserDatatype();
            EObject bindingDatatype = binding.getCurrentAttrDatatype();
            if (chooserDatatype != null && bindingDatatype != null) {
                if (!chooserDatatype.equals(bindingDatatype)) {
                    enableApply = true;
                }
            }
        }
        // Set Apply Button enabled state
        convertSelectedAttrButton.setEnabled(enableApply);

        // ------------------------------------------
        // Set the ConvertAll Button Enabled State
        // ------------------------------------------
        // Enable ConvertAll Button if any Binding has Conflict and target group not locked
        boolean enableConvertAll = false;

        // Enable ConvertAll Button if any Binding has Conflict
        boolean hasTypeConflict = bindingList.hasTypeConflict();
        if (hasTypeConflict && !this.targetLocked) {
            enableConvertAll = true;
        }
        convertAllAttrsButton.setEnabled(enableConvertAll);
    }

    /**
     * Update the Symbol Conversion Panel with the provided binding info
     * 
     * @param binding the selected Binding
     */
    private void updateSymbolConversionPanel( Binding binding ) {
        if (binding != null) {
            // Update the runtimeType label text and image
            Object symbol = binding.getCurrentSymbol();
            if (symbol != null) {
                symbolRuntimeTypeLabel.setText(getTypeText(symbol));
                sqlSymbolLabel.setImage(getLabelImage(symbol));
                sqlSymbolLabel.setText(symbol.toString());
            }
            // Update the available conversion label
            String convertedSymbol = binding.getSqlConversionText();
            int lossOfPrecIndex = convertedSymbol.indexOf('\n');
            String warningText = StringUtil.Constants.EMPTY_STRING;
            if (lossOfPrecIndex > -1) {
                warningText = convertedSymbol.substring(lossOfPrecIndex + 1, convertedSymbol.length());
                convertedSymbol = convertedSymbol.substring(0, lossOfPrecIndex);
            }
            symbolConversionLabel.setText(convertedSymbol);
            symbolWarningLabel.setText(warningText);
        }

        updateSymbolConversionPanelButtons(binding);

        sqlGroup.layout();
    }

    /**
     * Update the Symbol Conversion Panel button enabled states.
     */
    private void updateSymbolConversionPanelButtons( Binding binding ) {
        // ------------------------------------------
        // Set the Apply Button Enabled State
        // ------------------------------------------
        boolean enableApply = false;
        if (binding != null) {
            // Enable Apply Button if current Binding has Conflict
            enableApply = binding.canConvertSqlSymbol();
        }
        // Set Apply Button enabled state
        convertSelectedSqlButton.setEnabled(enableApply);

        // ------------------------------------------
        // Set the ConvertAll Button Enabled State
        // ------------------------------------------
        // If any binding has conflict, enable
        boolean enableConvertAll = false;
        if (bindingList.hasTypeConflict()) {
            enableConvertAll = true;
        }
        // Set ConvertAll Enabled State
        convertAllSqlButton.setEnabled(enableConvertAll);
    }

    /**
     * update Row background colors, based on binding and type conflict status.
     */
    public void updateRowColors() {
        int rows = table.getItemCount();
        for (int i = 0; i < rows; i++) {
            TableItem item = table.getItem(i);
            Binding binding = bindingList.get(i);
            if (!binding.isBound() || binding.hasTypeConflict()) {
                item.setBackground(colorManager.getColor(ColorManager.UNBOUND_BACKGROUND));
            } else {
                item.setBackground(colorManager.getColor(ColorManager.BOUND_BACKGROUND));
            }
        }

    }

    /**
     * handler for convertAll Attributes Button pressed
     */
    void convertAllAttrsPressed() {
        for (int i = 0; i < bindingList.size(); i++) {
            Binding binding = bindingList.get(i);
            if (binding.hasTypeConflict() && binding.hasAttributeConversion()) {
                // accept the default attribute type
                binding.acceptAttributeConversion();
            }
        }

        // Refresh
        tableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectFirstBinding();
    }

    /**
     * handler for convertAll Sql Button pressed
     */
    void convertAllSqlPressed() {
        for (int i = 0; i < bindingList.size(); i++) {
            Binding binding = bindingList.get(i);
            // If there is a type conflict, and available conversion, use it
            if (binding.hasTypeConflict() && binding.canConvertSqlSymbol()) {
                // accept the available Sql Conversion
                binding.acceptSqlConversion();
            }
        }

        // Refresh
        tableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectFirstBinding();
    }

    /**
     * handler for convert Selected Attribute Button pressed
     */
    void convertSelectedAttrPressed() {
        // Get the selected binding - table only allows single select
        Binding binding = getSelectedBinding();
        // Set datatype on the binding
        binding.setNewAttrDatatype(getChooserDatatype());

        // Update the AttrConversion Panel
        updateAttributeConversionPanel(binding);

        // Refresh
        tableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectBinding(binding);
    }

    /**
     * handler for convert Selected Sql Button pressed
     */
    void convertSelectedSqlPressed() {
        // Get the selected binding
        Binding binding = getSelectedBinding();
        // accept the available Sql Conversion
        if (binding.canConvertSqlSymbol()) {
            binding.acceptSqlConversion();
        }
        // Update the SqlConversion Panel
        updateSymbolConversionPanel(binding);

        // Refresh table and message area
        tableViewer.refresh(true);
        updateRowColors();
        updateMessageArea();

        selectBinding(binding);
    }

    /**
     * handler for Datatype chooser dialog
     */
    void showDatatypeDialogPressed() {
        Binding binding = getSelectedBinding();
        if (binding != null) {
            Object attr = binding.getAttribute();
            if (TransformationHelper.isSqlColumn(attr)) {
                Shell shell = UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
                DatatypeSelectionDialog dialog = new DatatypeSelectionDialog(shell, (EObject)attr, "string"); //$NON-NLS-1$
                Object originalValue = this.chooserDatatype;
                Object[] selection = new Object[] {originalValue};
                selection[0] = originalValue;
                dialog.setInitialSelections(selection);

                int status = dialog.open();
                EObject newDatatype = (EObject)originalValue;
                if (status == Window.OK) {
                    Object[] result = dialog.getResult();
                    if (result.length == 0) {
                        // null out the value
                        newDatatype = null;
                    } else {
                        // return the selected value
                        newDatatype = (EObject)result[0];
                    }
                }
                // If different datatype was chosen, set it on the binding
                if (!newDatatype.equals(originalValue)) {
                    setChooserDatatype(newDatatype);
                    updateAttributeConversionPanelButtons(binding);
                }
            }
            attrGroup.layout();
            // chooserPanel.layout();
            tableViewer.refresh(true);
            updateRowColors();
            updateMessageArea();
        }
    }

    /**
     * Handler for Table Selection changed
     */
    public void selectionChanged( SelectionChangedEvent event ) {
        IStructuredSelection selection = (IStructuredSelection)event.getSelection();
        Binding binding = null;
        if (SelectionUtilities.isSingleSelection(selection)) {
            Object o = SelectionUtilities.getSelectedObject(selection);
            binding = (Binding)o;
        } else if (SelectionUtilities.isMultiSelection(selection)) {
            List objects = SelectionUtilities.getSelectedObjects(selection);
            if (objects.size() > 0) binding = (Binding)objects.get(0);

        }
        // Update Displays
        updateDisplaysOnTableSelection(binding);
    }

    /**
     * Return the current selected datatype binding
     * 
     * @return the selected Binding
     */
    public Binding getSelectedBinding() {
        Binding selectedBinding = null;
        IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
        if (selection != null) {
            Object elem = selection.getFirstElement();
            if (elem != null && elem instanceof Binding) {
                selectedBinding = (Binding)elem;
            }
        }
        return selectedBinding;
    }

    /**
     * Select the first Binding in the binding list
     */
    private void selectBinding( Binding binding ) {
        if (binding != null) {
            tableViewer.setSelection(new StructuredSelection(binding), true);
        }
    }

    /**
     * Select the first Binding in the binding list
     */
    private void selectFirstBinding() {
        if (bindingList.size() > 0) {
            Binding binding = bindingList.get(0);
            tableViewer.setSelection(new StructuredSelection(binding), true);
        }
    }

    /**
     * Select the first Binding in the binding list which has a type conflict
     */
    private void selectFirstTypeConflict() {
        Binding nextSelection = bindingList.getFirstTypeConflict();
        if (nextSelection != null) {
            tableViewer.setSelection(new StructuredSelection(nextSelection), true);
        } else {
            selectFirstBinding();
        }
    }

    /**
     * Select the next Binding which has a type conflict
     * 
     * @param binding the supplied binding
     */
    // private void selectNextTypeConflict(Binding binding) {
    // Binding nextSelection = bindingList.getNextTypeConflict(binding);
    // if(nextSelection!=null) {
    // tableViewer.setSelection(new StructuredSelection(nextSelection),true);
    // }
    // }
    /**
     * Check whether there are any modifications to the SQL Symbols
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasSqlSymbolModifications() {
        boolean result = false;
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingList.size(); i++) {
            Binding binding = bindingList.get(i);
            if (binding.sqlSymbolWasConverted()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Check whether there are any modifications to the target attribute types
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasAttributeTypeModifications() {
        boolean result = false;
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingList.size(); i++) {
            Binding binding = bindingList.get(i);
            if (binding.hasAttrTypeModification()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Check whether there are any modifications to the target attribute types or sql symbols
     * 
     * @return true if there are pending modifications, false if not.
     */
    public boolean hasModifications() {
        boolean result = false;
        if (hasSqlSymbolModifications() || hasAttributeTypeModifications()) {
            result = true;
        }
        return result;
    }

    /**
     * Accept all of the binding type modifications. This will go thru the Binding modifications and make them permanent.
     */
    public void applyBindingTypeModifications() {
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingList.size(); i++) {
            Binding originalBinding = originalBindingList.get(i);
            Binding binding = bindingList.get(i);
            // Change the Attribute Types if required
            if (binding.hasAttrTypeModification()) {
                originalBinding.setNewAttrDatatype(binding.getCurrentAttrDatatype());
            }
            // Set the SqlSymbol on the original Binding if required
            if (binding.sqlSymbolWasConverted()) {
                originalBinding.setNewSymbol(binding.getCurrentSymbol());
            }
        }
    }

    /**
     * Clear all of the binding type modifications. This will go thru the Binding modifications and make them permanent.
     */
    public void clearBindingTypeModifications() {
        // If any of the newSymbols is non-null, there are type modifications
        for (int i = 0; i < bindingList.size(); i++) {
            Binding binding = bindingList.get(i);
            // Change the Attribute Types if required
            if (binding.hasAttrTypeModification()) {
                binding.setNewAttrDatatype(null);
            }
            // Set the SqlSymbol on the original Binding if required
            if (binding.sqlSymbolWasConverted()) {
                binding.undoSqlConversion();
            }
        }
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class BindingContentProvider implements IStructuredContentProvider {

        public void inputChanged( Viewer v,
                                  Object oldInput,
                                  Object newInput ) {
        }

        public void dispose() {
        }

        // Return the bindings as an array of Objects
        public Object[] getElements( Object parent ) {
            return bindingList.getAll().toArray();
        }
    }
}
