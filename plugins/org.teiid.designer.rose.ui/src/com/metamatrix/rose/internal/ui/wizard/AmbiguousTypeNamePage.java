/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.rose.internal.ui.wizard;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.ui.celleditor.ExtendedComboBoxCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Type;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.rose.internal.IAmbiguousReference;
import com.metamatrix.rose.internal.RoseImporter;
import com.metamatrix.rose.internal.ui.IRoseUiConstants;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * The <code>AmbiguousTypeNamePage</code> is a wizard page that identifies all the the ambiguous references found. Each ambiguous
 * reference is resolved by selecting a type from a list of available types.
 */
public final class AmbiguousTypeNamePage extends AbstractWizardPage implements IRoseUiConstants {

    /** Wizard page identifier. */
    public static final String PAGE_ID = AmbiguousTypeNamePage.class.getSimpleName();

    /** Properties key prefix. */
    static final String PREFIX = I18nUtil.getPropertyPrefix(AmbiguousTypeNamePage.class);

    /** Localized string indicating that the ambiguous reference should be left unresolved. */
    static final String LEAVE_UNRESOLVED = UTIL.getString(PREFIX + "editor.leaveUnresolved"); //$NON-NLS-1$

    /** Column headers for the table. */
    private static final String[] TBL_HDRS;

    /** Index of the referencer column in the table. */
    static final int REFERENCER_COLUMN;

    /** Index of the name referenced column in the table. */
    static final int NAME_REFERENCED_COLUMN;

    /** Index of the reference type column in the table. */
    static final int REF_TYPE_COLUMN;

    /** Index of the available objects column in the table. */
    static final int AVAILABLE_OBJS_COLUMN;

    /** Identifies the column properties used by the table cell modifier. */
    static final String[] COLUMN_PROPS;

    /** Constant for unresolved type. */
    private static final Object UNRESOLVED = null;

    static {
        // set column indexes
        REFERENCER_COLUMN = 0;
        NAME_REFERENCED_COLUMN = 1;
        REF_TYPE_COLUMN = 2;
        AVAILABLE_OBJS_COLUMN = 3;

        // set column headers
        TBL_HDRS = new String[4];
        TBL_HDRS[REFERENCER_COLUMN] = UTIL.getString(PREFIX + "table.column.property"); //$NON-NLS-1$
        TBL_HDRS[NAME_REFERENCED_COLUMN] = UTIL.getString(PREFIX + "table.column.ambiguousType"); //$NON-NLS-1$
        TBL_HDRS[REF_TYPE_COLUMN] = UTIL.getString(PREFIX + "table.column.referenceType"); //$NON-NLS-1$
        TBL_HDRS[AVAILABLE_OBJS_COLUMN] = UTIL.getString(PREFIX + "table.column.matchingType"); //$NON-NLS-1$

        // column properties used by the viewer and cell modifier
        COLUMN_PROPS = new String[TBL_HDRS.length];
        COLUMN_PROPS[REFERENCER_COLUMN] = "Referencer"; //$NON-NLS-1$
        COLUMN_PROPS[NAME_REFERENCED_COLUMN] = "Name Referenced"; //$NON-NLS-1$
        COLUMN_PROPS[REF_TYPE_COLUMN] = "Reference Type"; //$NON-NLS-1$
        COLUMN_PROPS[AVAILABLE_OBJS_COLUMN] = "Available Objects"; //$NON-NLS-1$
    }

    /** Business object for page. */
    private RoseImporter importer;

    /** Table cell editor for the list of matching types. */
    private ComboCellEditor matchingTypeEditor;

    /** Sets matching type of selected property to all like ambiguous type properties. */
    private Button btnApplyToSame;

    /** Sets all selected properties to unresolved. */
    private Button btnSetUnresolved;

    /** Viewer for table. */
    private TableViewer viewer;

    /**
     * Constructs a <code>AmbiguousTypeNamePage</code> wizard page using the specified business object.
     * 
     * @param theImporter the wizard business object
     * @since 4.1
     */
    public AmbiguousTypeNamePage( RoseImporter theImporter ) {
        super(PAGE_ID, UTIL.getString(PREFIX + "title")); //$NON-NLS-1$
        this.importer = theImporter;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     * @since 4.1
     */
    public void createControl( Composite theParent ) {
        final int COLUMNS = 1;

        //
        // Create main container
        //

        Composite pnlMain = WidgetFactory.createPanel(theParent);
        pnlMain.setLayout(new GridLayout(COLUMNS, false));
        setControl(pnlMain);

        createMainPanelContents(pnlMain, COLUMNS);
    }

    /**
     * Constructs the main panel of the wizard page.
     * 
     * @param theParent the parent UI container
     * @param theColumns the number of columns in the parent
     * @since 4.1
     */
    private void createMainPanelContents( Composite theParent,
                                          int theColumns ) {
        final int COLUMNS = 1;
        ViewForm viewForm = WidgetFactory.createViewForm(theParent, SWT.BORDER, GridData.FILL_BOTH, theColumns);
        viewForm.setTopLeft(WidgetFactory.createLabel(viewForm, UTIL.getString(PREFIX + "label.viewForm"))); //$NON-NLS-1$
        createViewFormContents(viewForm, COLUMNS);
    }

    /**
     * Constructs the contents of the <code>ViewForm</code> holding the ambiguous references table.
     * 
     * @param theViewForm the container
     * @param theColumns the number of columns in the view form control
     * @since 4.1
     */
    private void createViewFormContents( ViewForm theViewForm,
                                         int theColumns ) {
        // contents of view form is a panel
        final int COLUMNS = 2;
        Composite pnl = WidgetFactory.createPanel(theViewForm, SWT.NONE, GridData.FILL_BOTH, theColumns, COLUMNS);
        theViewForm.setContent(pnl);

        // contents of panel is a table and a button panel

        int style = SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION;
        this.viewer = WidgetFactory.createTableViewer(pnl, style);
        this.viewer.setContentProvider(new TableContentProvider());
        this.viewer.setLabelProvider(new TableLabelProvider());
        this.viewer.setColumnProperties(COLUMN_PROPS);

        Table tbl = this.viewer.getTable();
        tbl.setHeaderVisible(true);
        tbl.setLinesVisible(true);
        tbl.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleRowSelected();
            }
        });

        ILabelProvider typeLabelProvider = new LabelProvider() {
            @Override
            public String getText( Object theElement ) {
                return super.getText(theElement);
            }
        };

        CellEditor[] cellEditors = new CellEditor[TBL_HDRS.length];
        this.matchingTypeEditor = new ComboCellEditor(tbl, Collections.EMPTY_LIST, typeLabelProvider);

        // create columns & pack columns
        for (int i = 0; i < TBL_HDRS.length; i++) {
            TableColumn col = new TableColumn(tbl, SWT.LEFT);
            col.setText(TBL_HDRS[i]);

            cellEditors[i] = (i == AVAILABLE_OBJS_COLUMN) ? this.matchingTypeEditor : null;
        }

        this.viewer.setCellEditors(cellEditors);
        this.viewer.setCellModifier(new ICellModifier() {

            public Object getValue( final Object element,
                                    final String property ) {
                Object result = null;

                if (property.equals(COLUMN_PROPS[AVAILABLE_OBJS_COLUMN])) {
                    final IAmbiguousReference ref = (IAmbiguousReference)element;
                    result = ref.getReferencedObject();
                    if (result == null && ref.isResolved()) {
                        result = LEAVE_UNRESOLVED;
                    }
                }

                return result;
            }

            public boolean canModify( Object theElement,
                                      String theProperty ) {
                return theProperty.equals(COLUMN_PROPS[AVAILABLE_OBJS_COLUMN]);
            }

            public void modify( Object theElement,
                                String theProperty,
                                Object theValue ) {
                if (theProperty.equals(COLUMN_PROPS[AVAILABLE_OBJS_COLUMN])) {
                    setReferencedObject(theElement, theProperty, theValue);
                }
            }
        });

        packTableColumns();

        // create button panel

        Composite pnlButtons = WidgetFactory.createPanel(pnl, GridData.VERTICAL_ALIGN_CENTER);

        // apply to same button
        this.btnApplyToSame = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.applyToSame"), //$NON-NLS-1$
                                                         GridData.FILL_HORIZONTAL);
        this.btnApplyToSame.setEnabled(false);
        this.btnApplyToSame.setToolTipText(UTIL.getString(PREFIX + "button.applyToSame.tip")); //$NON-NLS-1$
        this.btnApplyToSame.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleApplyToSame();
            }
        });

        // set unresolved button
        this.btnSetUnresolved = WidgetFactory.createButton(pnlButtons, UTIL.getString(PREFIX + "button.setUnresolved"), //$NON-NLS-1$
                                                           GridData.FILL_HORIZONTAL);
        this.btnSetUnresolved.setEnabled(false);
        this.btnSetUnresolved.setToolTipText(UTIL.getString(PREFIX + "button.setUnresolved.tip")); //$NON-NLS-1$
        this.btnSetUnresolved.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theEvent ) {
                handleSetUnresolved();
            }
        });
    }

    /**
     * Gets the widget containing all the available types.
     * 
     * @return the widget
     * @since 4.1
     */
    private CCombo getEditorControl() {
        return (CCombo)this.matchingTypeEditor.getControl();
    }

    /**
     * Gets the importer used by this page.
     * 
     * @return the importer
     * @since 4.1
     */
    RoseImporter getImporter() {
        return this.importer;
    }

    /**
     * Handler for when the apply to same button is selected.
     * 
     * @since 4.1
     */
    void handleApplyToSame() {
        // should only be here if one selected row that has a type assigned
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        IAmbiguousReference selectedRef = (IAmbiguousReference)selection.getFirstElement();
        Object selectedType = selectedRef.getReferencedObject();

        // assign type to others refs with same name
        Object[] ambiguousRefs = ((IStructuredContentProvider)this.viewer.getContentProvider()).getElements(this);

        for (int i = 0; i < ambiguousRefs.length; i++) {
            IAmbiguousReference ref = (IAmbiguousReference)ambiguousRefs[i];

            if ((ref != selectedRef) && ref.getName().equals(selectedRef.getName())) {
                this.importer.resolveAmbiguousReference(ref, selectedType);
                this.viewer.refresh(ref);
            }
        }

        setPageStatus();
    }

    /**
     * Handler for when a row in the ambiguous references table is selected/deselected.
     * 
     * @since 4.1
     */
    void handleRowSelected() {
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();

        if (!selection.isEmpty()) {
            IAmbiguousReference ref = (IAmbiguousReference)selection.getFirstElement();

            if (selection.size() == 1) {
                List types = ref.getAvailableObjects();
                int size = types.size() + 1; // add one for the leave unresolved choice
                String[] items = new String[size];

                // copy to array for the cell editor to use
                for (int i = 0; i < (size - 1); i++) {
                    items[i] = ((Type)types.get(i)).getQualifiedName();
                }

                items[size - 1] = LEAVE_UNRESOLVED;
                this.matchingTypeEditor.setItems(items);
                this.matchingTypeEditor.setList(Arrays.asList(items));

                // select current choice in editor
                if (ref.isResolved()) {
                    int index = -1;
                    Type resolvedType = (Type)ref.getReferencedObject();

                    if (resolvedType == null) {
                        index = size - 1;
                    } else {
                        String name = resolvedType.getQualifiedName();
                        index = Arrays.asList(items).indexOf(name);
                    }

                    getEditorControl().select(index);
                }
            }
        }

        // enablement of buttons
        setButtonStatus(selection);
    }

    /**
     * Handler for when the set unresolved button is selected.
     * 
     * @since 4.1
     */
    void handleSetUnresolved() {
        // should only get here if one or more rows selected
        IStructuredSelection selection = (IStructuredSelection)this.viewer.getSelection();
        List ambiguousRefs = selection.toList();

        for (int size = ambiguousRefs.size(), i = 0; i < size; i++) {
            IAmbiguousReference ref = (IAmbiguousReference)ambiguousRefs.get(i);
            getImporter().resolveAmbiguousReference(ref, UNRESOLVED);
            this.viewer.refresh(ref);
        }

        setPageStatus();
    }

    /**
     * Packs all table columns. Should be called when table data is loaded.
     * 
     * @since 4.1
     */
    private void packTableColumns() {
        Table tbl = this.viewer.getTable();
        for (int i = 0; i < TBL_HDRS.length; tbl.getColumn(i++).pack()) {

        }
    }

    /**
     * Sets the enabled stat of the apply to same button and the set unresolved button.
     * 
     * @param theSelection the selection determining button enablement
     * @since 4.1
     */
    private void setButtonStatus( IStructuredSelection theSelection ) {
        // set the enable status of the Apply To Same button
        boolean enable = false;

        if (theSelection.size() == 1) {
            IAmbiguousReference ref = (IAmbiguousReference)theSelection.getFirstElement();
            enable = (ref.getReferencedObject() != null);
        }

        this.btnApplyToSame.setEnabled(enable);

        // set the enable status of the Leave Unresolved button
        this.btnSetUnresolved.setEnabled(!theSelection.isEmpty());
    }

    /**
     * Sets the pages status message.
     * 
     * @since 4.1
     */
    private void setPageStatus() {
        boolean allResolved = getImporter().ambiguousReferencesResolved();

        // set message
        if (allResolved) {
            setErrorMessage(null);
            setMessage(UTIL.getString(PREFIX + "msg.pageComplete")); //$NON-NLS-1$
        } else {
            setErrorMessage(UTIL.getString(PREFIX + "msg.pageIncomplete")); //$NON-NLS-1$
        }

        // set complete status
        setPageComplete(allResolved);
    }

    /**
     * Handler for when an ambiguous reference is edited.
     * 
     * @param theElement the table item representing one ambiguous reference
     * @param theProperty the table column ID
     * @param theValue not used
     * @since 4.1
     */
    void setReferencedObject( Object theElement,
                              String theProperty,
                              Object theValue ) {
        if (theElement instanceof TableItem) {
            int index = getEditorControl().getSelectionIndex();

            if (index != -1) {
                IAmbiguousReference ref = (IAmbiguousReference)((TableItem)theElement).getData();
                List availableTypes = ref.getAvailableObjects();
                String selectedType = getEditorControl().getItem(index);

                Object newValue = (selectedType.equals(LEAVE_UNRESOLVED) ? UNRESOLVED : availableTypes.get(index));
                getImporter().resolveAmbiguousReference(ref, newValue);

                setPageStatus();
                setButtonStatus((IStructuredSelection)this.viewer.getSelection());

                this.viewer.refresh(ref);
            }
        }
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#setVisible(boolean)
     * @since 4.1
     */
    @Override
    public void setVisible( boolean theShowFlag ) {
        if (theShowFlag) {
            // list should not be empty. if it is then this page shouldn't be displayed.
            CoreArgCheck.isTrue(!this.importer.getAmbiguousReferences().isEmpty(), "Importer discovered ambiguous references"); //$NON-NLS-1$

            // load viewer
            this.viewer.setInput(this);
            setPageStatus();
            packTableColumns();
        }

        super.setVisible(theShowFlag);
    }

    class TableContentProvider implements IStructuredContentProvider {

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
            return getImporter().getAmbiguousReferences().toArray();
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

    class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         * @since 4.1
         */
        public Image getColumnImage( Object theElement,
                                     int theColumnIndex ) {
            return null;
        }

        /**
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         * @since 4.1
         */
        public String getColumnText( Object theElement,
                                     int theIndex ) {
            String result = null;

            if (theElement instanceof IAmbiguousReference) {
                IAmbiguousReference ref = (IAmbiguousReference)theElement;

                if (theIndex == REFERENCER_COLUMN) {
                    result = ((NamedElement)ref.getReferencer()).getQualifiedName();
                } else if (theIndex == NAME_REFERENCED_COLUMN) {
                    result = ref.getName();
                } else if (theIndex == REF_TYPE_COLUMN) {
                    result = ref.getType();
                } else if (theIndex == AVAILABLE_OBJS_COLUMN) {
                    Type type = (Type)ref.getReferencedObject();

                    if (type == null) {
                        result = (ref.isResolved()) ? LEAVE_UNRESOLVED : ""; //$NON-NLS-1$
                    } else {
                        result = type.getQualifiedName();
                    }
                } else {
                    // should not happen
                    CoreArgCheck.isTrue(false, UTIL.getString(PREFIX + "msg.unknownObjectType", //$NON-NLS-1$
                                                              new Object[] {theElement.getClass().getName()}));
                }
            } else {
                // should not happen
                CoreArgCheck.isTrue(false, UTIL.getString(PREFIX + "msg.unknownObjectType", //$NON-NLS-1$
                                                          new Object[] {theElement.getClass().getName()}));
            }

            return result;
        }

    }

    /**
     * @since 4.2
     */
    private class ComboCellEditor extends ExtendedComboBoxCellEditor {

        ComboCellEditor( final Composite parent,
                         final List list,
                         final ILabelProvider provider ) {
            super(parent, list, provider);
        }

        /**
         * @since 4.2
         */
        void setList( final List list ) {
            this.list = list;
        }
    }
}
