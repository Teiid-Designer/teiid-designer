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
package com.metamatrix.modeler.mapping.ui.editor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor.PropertyValueWrapper;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.internal.mapping.factory.TreeMappingAdapter;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerLabelProvider;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.mapping.factory.IMappableTree;
import com.metamatrix.modeler.mapping.ui.PluginConstants;
import com.metamatrix.modeler.mapping.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetFactory;

/**
 * InputSetPanel
 */
public class InputSetPanel extends SashForm implements SelectionListener, UiConstants {

    // Style Contants
    private static final int BUTTON_GRID_STYLE = GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_CENTER;

    private static final String ATTRIBUTE_COL_TEXT = UiConstants.Util.getString("InputSetObjectEditorPage.attributeCol.text"); //$NON-NLS-1$
    private static final String BINDING_COL_TEXT = UiConstants.Util.getString("InputSetObjectEditorPage.bindingCol.text"); //$NON-NLS-1$
    private static final String NEW_BUTTON_TEXT = UiConstants.Util.getString("InputSetObjectEditorPage.newButton.text"); //$NON-NLS-1$
    private static final String NEW_BUTTON_TOOLTIP = UiConstants.Util.getString("InputSetObjectEditorPage.newButton.toolTip"); //$NON-NLS-1$
    private static final String BIND_BUTTON_TEXT = UiConstants.Util.getString("InputSetObjectEditorPage.bindButton.text"); //$NON-NLS-1$
    private static final String BIND_BUTTON_TOOLTIP = UiConstants.Util.getString("InputSetObjectEditorPage.bindButton.toolTip"); //$NON-NLS-1$
    private static final String UNBIND_BUTTON_TEXT = UiConstants.Util.getString("InputSetObjectEditorPage.unbindButton.text"); //$NON-NLS-1$
    private static final String UNBIND_BUTTON_TOOLTIP = UiConstants.Util.getString("InputSetObjectEditorPage.unbindButton.toolTip"); //$NON-NLS-1$
    private static final String DELETE_BUTTON_TEXT = UiConstants.Util.getString("InputSetObjectEditorPage.deleteButton.text"); //$NON-NLS-1$
    private static final String DELETE_BUTTON_TOOLTIP = UiConstants.Util.getString("InputSetObjectEditorPage.deleteButton.toolTip"); //$NON-NLS-1$
    private static final String TREE_TOOLTIP = UiConstants.Util.getString("InputSetObjectEditorPage.tree.toolTip"); //$NON-NLS-1$

    private static final String UNDO_NEW = UiConstants.Util.getString("InputSetPanel.undoNew"); //$NON-NLS-1$
    private static final String UNDO_BIND = UiConstants.Util.getString("InputSetPanel.undoBind"); //$NON-NLS-1$
    private static final String UNDO_UNBIND = UiConstants.Util.getString("InputSetPanel.undoUnbind"); //$NON-NLS-1$
    private static final String UNDO_DELETE = UiConstants.Util.getString("InputSetPanel.undoDelete"); //$NON-NLS-1$
    private static final String UNDO_DELETE_MANY = UiConstants.Util.getString("InputSetPanel.undoDeleteMany"); //$NON-NLS-1$

    private static final String PARAM_BASE_NAME = UiConstants.Util.getString("InputSetObjectEditorPage.inputParamBaseName.text"); //$NON-NLS-1$

    private Table table;
    TableViewer tableViewer;
    private Button btnNewButton;
    private Button btnBindButton;
    private Button btnUnbindButton;
    private Button btnDeleteButton;

    private Composite pnlTreePanel;
    private TreeViewer tvTreeViewer;
    private TreeContentProvider treeContentProvider;

    InputSetAdapter isoInputSetObject;
    private BindingContentProvider cpBindingContentProvider;
    private BindingLabelProvider lpBindingLabelProvider;
    private TreeMappingAdapter mappingAdapter;
    private IMappableTree mappableTree;

    // Set column names
    String[] columnNames = new String[] {ATTRIBUTE_COL_TEXT, BINDING_COL_TEXT};

    /**
     * Constructor.
     * 
     * @param parent Parent of this control
     */
    public InputSetPanel( Composite parent ) {
        super(parent, SWT.VERTICAL);
        createControl(this);
    }

    /**
     * Initialize the content of the panel.
     */
    private void init() {

        // Initialize the Panels with initial bindings and sql data
        if (isoInputSetObject != null) {
            getBindingList().addChangeListener(new BindingChangeHandler());
        }

        InputParameter sampleInputParam = null;
        if (getBindingList().size() > 0) {
            sampleInputParam = (InputParameter)getBindingList().get(0).getItem();
        } else {
            sampleInputParam = TransformationFactory.eINSTANCE.createInputParameter();
        }

        // Set up table editing for the name of the input parameter in the first column
        IPropertySource propertySource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(sampleInputParam);
        IPropertyDescriptor[] properties = propertySource.getPropertyDescriptors();

        CellEditor[] cellEditors = new CellEditor[2];
        cellEditors[0] = properties[0].createPropertyEditor(table);

        columnNames[0] = properties[0].getDisplayName();
        tableViewer.setColumnProperties(columnNames);
        tableViewer.setCellEditors(cellEditors);
        tableViewer.setCellModifier(new InputParameterNameModifier(properties[0]));

        // The input for the table viewer is the BindingList
        tableViewer.setInput(getBindingList());

        // Initialize the Button states
        setButtonStates();

        if (mappingAdapter != null) {
            Collection parentMappingClasses = mappingAdapter.getParentMappingClasses(isoInputSetObject.getInputSet().getMappingClass(),
                                                                                     this.mappableTree,
                                                                                     false);
            tvTreeViewer.setInput(parentMappingClasses);
        }

        // Initialize the message area at the top of the dialog
        table.addSelectionListener(this);

    }

    public void setBusinessObject( InputSetAdapter iso ) {
        this.isoInputSetObject = iso;
        init();
    }

    public void setMappingAdapters( TreeMappingAdapter adapter,
                                    IMappableTree tree ) {
        this.mappingAdapter = adapter;
        this.mappableTree = tree;
    }

    public void refreshFromBusinessObject() {
        tableViewer.refresh(true);
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

        SashForm splitter = new SashForm(parent, SWT.HORIZONTAL);

        // 1. Create the table
        createTableViewerPanel(splitter);

        Composite outerComposite = new Composite(splitter, SWT.NONE);
        gridLayout = new GridLayout();
        outerComposite.setLayout(gridLayout);
        gridLayout.numColumns = 2;

        // 2. Create the button panel
        createControlButtonPanel(outerComposite);

        // 3. Create the tree
        createTree(outerComposite);

    }

    /**
     * Create the button panel
     */
    private void createControlButtonPanel( Composite parent ) {
        Composite buttonComposite = new Composite(parent, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        buttonComposite.setLayout(gridLayout);
        gridLayout.numColumns = 1;
        GridData gridData = new GridData(GridData.VERTICAL_ALIGN_CENTER);
        buttonComposite.setLayoutData(gridData);

        btnNewButton = WidgetFactory.createButton(buttonComposite, NEW_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnNewButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                newButtonPressed();
            }
        });
        btnNewButton.setToolTipText(NEW_BUTTON_TOOLTIP);

        btnBindButton = WidgetFactory.createButton(buttonComposite, BIND_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnBindButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                bindButtonPressed();
            }
        });
        btnBindButton.setToolTipText(BIND_BUTTON_TOOLTIP);

        btnUnbindButton = WidgetFactory.createButton(buttonComposite, UNBIND_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnUnbindButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                unbindButtonPressed();
            }
        });
        btnUnbindButton.setToolTipText(UNBIND_BUTTON_TOOLTIP);

        btnDeleteButton = WidgetFactory.createButton(buttonComposite, DELETE_BUTTON_TEXT, BUTTON_GRID_STYLE);
        btnDeleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( final SelectionEvent event ) {
                deleteButtonPressed();
            }
        });
        btnDeleteButton.setToolTipText(DELETE_BUTTON_TOOLTIP);

    }

    /**
     * Create the Table
     */
    private void createTable( Composite parent ) {
        int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

        table = new Table(parent, style);
        TableLayout layout = new TableLayout();
        table.setLayout(layout);

        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.grabExcessVerticalSpace = true;
        gridData.grabExcessHorizontalSpace = true;
        table.setLayoutData(gridData);

        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        // 1st column with attribute
        TableColumn column1 = new TableColumn(table, SWT.LEFT, 0);
        column1.setText(ATTRIBUTE_COL_TEXT);
        ColumnWeightData weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

        // 2nd column with binding
        TableColumn column2 = new TableColumn(table, SWT.LEFT, 1);
        column2.setText(BINDING_COL_TEXT);
        weight = new ColumnWeightData(1);
        layout.addColumnData(weight);

    }

    /*
     * Create the TableViewerPanel 
     */
    private void createTableViewerPanel( Composite parent ) {
        // Create the table
        createTable(parent);

        // Create and setup the TableViewer
        tableViewer = new TableViewer(table);
        tableViewer.setUseHashlookup(true);

        cpBindingContentProvider = new BindingContentProvider();
        lpBindingLabelProvider = new BindingLabelProvider();

        tableViewer.setContentProvider(cpBindingContentProvider);
        tableViewer.setLabelProvider(lpBindingLabelProvider);

        tableViewer.getTable().addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent theEvent ) {
                handleTableSelection();
            }

            public void widgetSelected( SelectionEvent theEvent ) {
                handleTableSelection();
            }
        });
    }

    /**
     * @see com.metamatrix.query.internal.ui.builder.AbstractLanguageObjectEditor#createUi(org.eclipse.swt.widgets.Composite)
     */
    protected void createTree( Composite theParent ) {
        pnlTreePanel = new Composite(theParent, SWT.NONE);
        pnlTreePanel.setLayoutData(new GridData(GridData.FILL_BOTH));
        pnlTreePanel.setLayout(new FillLayout());

        tvTreeViewer = createTreeViewer(pnlTreePanel);

        Tree tree = tvTreeViewer.getTree();
        tree.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected( SelectionEvent theEvent ) {
                handleTreeSelection();
            }

            public void widgetSelected( SelectionEvent theEvent ) {
                handleTreeSelection();
            }
        });

        tree.setToolTipText(TREE_TOOLTIP);
    }

    public TreeViewer createTreeViewer( Composite parent ) {
        TreeViewer viewer = new TreeViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL);
        treeContentProvider = new TreeContentProvider();
        viewer.setContentProvider(treeContentProvider);
        viewer.setLabelProvider(ModelUtilities.getEMFLabelProvider());
        return viewer;
    }

    /**
     * Handler for tree selection.
     */
    void handleTreeSelection() {
        setButtonStates();
    }

    void handleTableSelection() {
        setButtonStates();
        List tableSelection = SelectionUtilities.getSelectedObjects(tableViewer.getSelection());
        if (tableSelection.size() == 1) {
            BindingAdapter binding = (BindingAdapter)tableSelection.get(0);
            Object mapping = binding.getMapping();
            if (mapping != null) {
                tvTreeViewer.setSelection(new StructuredSelection(mapping));
            }
        }
    }

    BindingList getBindingList() {
        return isoInputSetObject.getBindingList();
    }

    /**
     * Handler for Bind Button
     */
    void bindButtonPressed() {
        boolean started = ModelerCore.startTxn(UNDO_BIND, this);
        boolean succeeded = false;
        try {
            // get the selected item from the tree
            ISelection is = tvTreeViewer.getSelection();
            EObject mcColumn = SelectionUtilities.getSelectedEObject(is);

            // get the selected item
            is = this.tableViewer.getSelection();
            BindingAdapter bindingAdapter = (BindingAdapter)SelectionUtilities.getSelectedObject(is);

            if (bindingAdapter != null) {
                bindingAdapter.setMapping(mcColumn);
                refreshFromBusinessObject();
            }
            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }

        }

        setButtonStates();
    }

    /**
     * Handler for Unbind Button
     */
    void unbindButtonPressed() {
        boolean started = ModelerCore.startTxn(UNDO_UNBIND, this);
        boolean succeeded = false;
        try {

            // get the selected item in the table
            ISelection is = this.tableViewer.getSelection();
            BindingAdapter bindingAdapter = (BindingAdapter)SelectionUtilities.getSelectedObject(is);

            if (bindingAdapter != null) {
                bindingAdapter.setMapping(null);
                bindingAdapter.deleteBinding();
            }
            succeeded = true;
        } catch (ModelerCoreException e) {
            UiConstants.Util.log(e);
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        refreshFromBusinessObject();
        setButtonStates();
    }

    /**
     * Handler for new Button
     */
    void newButtonPressed() {

        boolean started = ModelerCore.startTxn(UNDO_NEW, this);
        boolean succeeded = false;
        try {
            // create a new InputParameter in the InputSet
            InputParameter param = TransformationFactory.eINSTANCE.createInputParameter();
            InputSet inputSet = isoInputSetObject.getInputSet();
            param.setInputSet(inputSet);

            EObject treeSelection = SelectionUtilities.getSelectedEObject(tvTreeViewer.getSelection());
            if (treeSelection instanceof MappingClassColumn) {
                param.setName(((MappingClassColumn)treeSelection).getName());
            } else {
                treeSelection = null;
                // generate a name for the parameter
                int nameIndex = getBindingList().size();
                String name = PARAM_BASE_NAME + (++nameIndex);
                // check for name clash with an existing parameter
                boolean tryAgain = true;
                while (tryAgain) {
                    tryAgain = false;
                    for (Iterator iter = inputSet.getInputParameters().iterator(); iter.hasNext();) {
                        if (name.equals(((InputParameter)iter.next()).getName())) {
                            name = PARAM_BASE_NAME + (++nameIndex);
                            tryAgain = true;
                        }
                    }
                }
                param.setName(name);
            }

            // create a new BindingAdapter for this parameter and add it to the binding list
            BindingAdapter binding = null;

            if (treeSelection != null) {
                binding = new BindingAdapter(param, treeSelection);
            } else {
                binding = new BindingAdapter(param);
            }
            getBindingList().add(binding);
            refreshFromBusinessObject();
            setButtonStates();

            succeeded = true;
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

    }

    /**
     * Handler for delete Button
     */
    void deleteButtonPressed() {
        List tableSelection = SelectionUtilities.getSelectedObjects(tableViewer.getSelection());
        boolean started = false;
        boolean succeeded = false;
        if (tableSelection.size() == 1) {
            started = ModelerCore.startTxn(UNDO_DELETE, this);
        } else {
            started = ModelerCore.startTxn(UNDO_DELETE_MANY, this);
        }
        try {
            for (Iterator iter = tableSelection.iterator(); iter.hasNext();) {
                BindingAdapter binding = (BindingAdapter)iter.next();
                binding.delete();
                getBindingList().remove(binding);
            }
            succeeded = true;
        } catch (ModelerCoreException e) {
            UiConstants.Util.log(e);
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }
        refreshFromBusinessObject();
        setButtonStates();
    }

    /**
     * Set the enabled/disabled states of the Buttons.
     */
    private void setButtonStates() {
        if (ModelObjectUtilities.isReadOnly(isoInputSetObject.getInputSet())) {
            btnNewButton.setEnabled(false);
            btnDeleteButton.setEnabled(false);
            btnBindButton.setEnabled(false);
            btnUnbindButton.setEnabled(false);
        } else {

            // enable delete button only if something in the table is selected:
            btnDeleteButton.setEnabled(!tableViewer.getSelection().isEmpty());

            // enable the new button if a MappingClassColumn is selected in the tree
            boolean enable = false;
            EObject treeSelection = SelectionUtilities.getSelectedEObject(tvTreeViewer.getSelection());
            if (treeSelection instanceof MappingClassColumn) {
                enable = true;
            }
            btnNewButton.setEnabled(true);

            // enable the bind button only if a single valid object is selected in both the table and tree
            enable = false;
            List tableSelection = SelectionUtilities.getSelectedObjects(tableViewer.getSelection());
            if (tableSelection.size() == 1) {
                BindingAdapter binding = (BindingAdapter)tableSelection.get(0);
                if (!binding.isBound()) {
                    treeSelection = SelectionUtilities.getSelectedEObject(tvTreeViewer.getSelection());
                    if (treeSelection instanceof MappingClassColumn) {
                        enable = true;
                    }
                }
            }
            btnBindButton.setEnabled(enable);

            // enable the unbind button only if something is selected in the table, and it is bound
            enable = false;
            tableSelection = SelectionUtilities.getSelectedObjects(tableViewer.getSelection());
            if (tableSelection.size() == 1) {
                BindingAdapter binding = (BindingAdapter)tableSelection.get(0);
                if (binding.isBound()) {
                    enable = true;
                }
            }
            btnUnbindButton.setEnabled(enable);
        }

    }

    // ==========================================================
    // SelectionListener Interface
    // ==========================================================

    public void widgetSelected( SelectionEvent e ) {
        //      System.out.println("[InputSetObjectEditorPage.widgetSelected]"); //$NON-NLS-1$

    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        widgetSelected(e);
    }

    // ======================================================
    // Inner Classes
    // ======================================================

    class TreeContentProvider implements ITreeContentProvider {

        public TreeContentProvider() {
            super();
        }

        public Object[] getChildren( Object parentElement ) {
            if (parentElement instanceof MappingClass) {
                return ((MappingClass)parentElement).getColumns().toArray();
            }
            return new Object[0];
        }

        public Object getParent( Object element ) {
            if (element instanceof MappingClassColumn) {
                return ((MappingClassColumn)element).getMappingClass();
            }
            return null;
        }

        public boolean hasChildren( Object element ) {
            return (element instanceof MappingClass);
        }

        public void inputChanged( Viewer v,
                                  Object oldInput,
                                  Object newInput ) {
        }

        public void dispose() {
        }

        // Return the bindings as an array of Objects
        public Object[] getElements( Object parent ) {
            return ((Collection)parent).toArray();
        }

    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class BindingContentProvider implements IStructuredContentProvider, IBindingListViewer {

        // this will wrap the bindinglist in the Business Object (isoInputSetObject)

        public void inputChanged( Viewer v,
                                  Object oldInput,
                                  Object newInput ) {
            if (newInput != null) ((BindingList)newInput).addChangeListener(this);
            if (oldInput != null) ((BindingList)oldInput).removeChangeListener(this);
        }

        public void dispose() {
            getBindingList().removeChangeListener(this);
        }

        // Return the bindings as an array of Objects
        public Object[] getElements( Object parent ) {
            return getBindingList().getAll().toArray();
        }

        /**
         * @see IBindingListViewer#addBinding(Binding)
         */
        public void addBinding( BindingAdapter binding ) {
            tableViewer.add(binding);
        }

        /**
         * @see IBindingListViewer#addBinding(Binding)
         */
        public void insertBinding( BindingAdapter binding,
                                   int index ) {
            tableViewer.insert(binding, index);
        }

        /**
         * @see IBindingListViewer#addBindings(Object[])
         */
        public void addBindings( Object[] bindings ) {
            tableViewer.add(bindings);
        }

        /**
         * @see IBindingListViewer#removeBinding(Binding)
         */
        public void removeBinding( BindingAdapter binding ) {
            tableViewer.remove(binding);
        }

        /**
         * @see IBindingListViewer#removeBindings(Binding[])
         */
        public void removeBindings( Object[] bindings ) {
            tableViewer.remove(bindings);
        }

        /**
         * @see IBindingListViewer#updateBindings(Binding)
         */
        public void updateBinding( BindingAdapter binding ) {
            tableViewer.update(binding, null);
        }

        /**
         * @see IBindingListViewer#updateBindings(Binding)
         */
        public void refresh( boolean updateLabels ) {
            tableViewer.refresh(updateLabels);
        }

    }

    class BindingLabelProvider extends ModelExplorerLabelProvider implements ITableLabelProvider, PluginConstants.Images {

        private static final String EMPTY_STRING = ""; //$NON-NLS-1$

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText( Object element,
                                     int columnIndex ) {
            String result = EMPTY_STRING;
            BindingAdapter binding = (BindingAdapter)element;
            switch (columnIndex) {
                case 0: // InputParameter
                    Object attr = binding.getItem();
                    if (attr != null && attr instanceof EObject) {
                        result = super.getText(attr);
                    } else if (attr instanceof String) {
                        result = (String)attr;
                    }
                    break;
                case 1: // MappingClassColumn
                    Object column = binding.getMapping();
                    if (column != null && column instanceof EObject) {
                        result = super.getText(((EObject)column).eContainer()) + '.';
                        result += super.getText(column);
                    } else if (column instanceof String) {
                        result = (String)column;
                    }
                    break;
                default:
                    break;
            }
            return result;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage( Object element,
                                     int columnIndex ) {
            Image image = null;
            BindingAdapter binding = (BindingAdapter)element;
            switch (columnIndex) {
                case 0: // Attribute Column
                    Object attr = binding.getItem();
                    if (attr != null && attr instanceof EObject) {
                        image = super.getImage(attr);
                    }
                    break;
                case 1: // SQL Symbol Column
                    Object column = binding.getMapping();
                    if (column != null && column instanceof EObject) {
                        image = super.getImage(column);
                    }
                    break;
                default:
                    break;
            }
            return image;
        }
    }

    /**
     * InnerClass that acts as a proxy for the BindingList providing content for the Table. It implements the IBindingListViewer
     * interface since it must register changeListeners with the BindingList
     */
    class BindingChangeHandler implements IBindingListViewer {

        // ------------------------------------------------------------------
        // IBindingListView interface
        // ------------------------------------------------------------------

        /**
         * Update the view to reflect the fact that a binding was added to the binding list
         * 
         * @param binding
         */
        public void addBinding( BindingAdapter binding ) {
        }

        /**
         * Update the view to reflect the fact that a binding was added to the binding list
         * 
         * @param binding
         */
        public void insertBinding( BindingAdapter binding,
                                   int index ) {
        }

        /**
         * Update the view to reflect the fact that bindings were added to the binding list
         * 
         * @param bindings
         */
        public void addBindings( Object[] bindings ) {
        }

        /**
         * Update the view to reflect the fact that a binding was removed from the binding list
         * 
         * @param binding
         */
        public void removeBinding( BindingAdapter binding ) {
        }

        /**
         * Update the view to reflect the fact that bindings were removed from the binding list
         * 
         * @param binding
         */
        public void removeBindings( Object[] bindings ) {
            // Put the bound symbols back on the unmatched symbols list

        }

        /**
         * Update the view to reflect the fact that one of the bindings was modified
         * 
         * @param binding
         */
        public void updateBinding( BindingAdapter binding ) {
        }

        /**
         * Update the view to reflect the fact that one of the symbols was modified
         * 
         * @param updateLabels
         */
        public void refresh( boolean updateLabels ) {
        }
    }

    class InputParameterNameModifier implements ICellModifier {

        private String propertyName;
        private IPropertyDescriptor descriptor;

        public InputParameterNameModifier( IPropertyDescriptor descriptor ) {
            this.propertyName = columnNames[0];
            this.descriptor = descriptor;
        }

        /**
         * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object, java.lang.String)
         */
        public boolean canModify( Object element,
                                  String property ) {
            return propertyName.equals(property) && (!ModelObjectUtilities.isReadOnly(isoInputSetObject.getInputSet()));
        }

        /**
         * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object, java.lang.String)
         */
        public Object getValue( Object element,
                                String property ) {
            if (element instanceof BindingAdapter) {
                Object input = ((BindingAdapter)element).getItem();
                IPropertySource propertySource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(input);
                Object value = propertySource.getPropertyValue(descriptor.getId());
                // the real value may be inside a PropertyValueWrapper
                if (value instanceof PropertyValueWrapper) {
                    value = ((PropertyValueWrapper)value).getEditableValue(value);
                }
                return value;
            }
            IPropertySource propertySource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(element);
            return propertySource.getPropertyValue(descriptor.getId());
        }

        /**
         * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object, java.lang.String, java.lang.Object)
         */
        public void modify( Object element,
                            String property,
                            Object value ) {
            // Do not set Null or EmptyString as Name - results in badness
            if (!isNullOrEmptyString(value)) {
                if (element instanceof BindingAdapter) {
                    Object input = ((BindingAdapter)element).getItem();
                    IPropertySource propertySource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(input);
                    propertySource.setPropertyValue(descriptor.getId(), value);
                } else if (element instanceof TableItem) {
                    // the BindingAdapter is inside the TableItem
                    Object input = ((TableItem)element).getData();
                    modify(input, property, value);
                } else {
                    IPropertySource propertySource = ModelUtilities.getEmfPropertySourceProvider().getPropertySource(element);
                    propertySource.setPropertyValue(descriptor.getId(), value);
                }
            }
            refreshFromBusinessObject();
        }

        /*
         * Check whether the supplied Object is null or is a zero length string
         */
        private boolean isNullOrEmptyString( Object value ) {
            boolean result = false;
            if (value == null) {
                result = true;
            } else if (value instanceof String) {
                String strValue = (String)value;
                if (strValue.trim().length() == 0) {
                    result = true;
                }
            }
            return result;
        }

    }
}
