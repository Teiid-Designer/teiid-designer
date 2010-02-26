/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.config;

import java.io.File;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import com.metamatrix.common.vdb.api.VDBDefn;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.internal.config.VdbDefnHelper;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.modeler.internal.dqp.ui.dialogs.NewConnectorBindingDialog;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.vdb.edit.VdbEditPlugin;
import com.metamatrix.vdb.edit.VdbEditingContext;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;
import com.metamatrix.vdb.internal.runtime.model.BasicVDBModelDefn;

/**
 * @since 4.3
 */
public final class ConnectorBindingsPanel extends Composite
    implements ControlListener, DqpUiConstants, IChangeListener, IChangeNotifier, IPropertyChangeListener {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(ConnectorBindingsPanel.class);

    private static final String COLUMN1 = "Physical Models"; //$NON-NLS-1$

    private static final String COLUMN2 = "Connector Bindings"; //$NON-NLS-1$

    private static final String[] COLUMN_PROPERTIES = new String[] {COLUMN1, COLUMN2};

    static String getString( String theKey ) {
        return UTIL.getStringOrKey(PREFIX + theKey);
    }

    private boolean hasChanges = false;

    private ListenerList listeners;

    private boolean saveOnChange;

    private final InternalVdbEditingContext vdbContext;

    private Button btnEdit;

    private Button btnUnbind;

    private Button btnShowExpertProps;

    private VdbDefinitionLabelProvider labelProvider;

    private PropertySheetPage propertyPage;

    private Table table;

    private TableViewer viewer;

    private ConnectorBindingsPropertySourceProvider sourceProvider;

    public ConnectorBindingsPanel( Composite theParent,
                                   File theVdb,
                                   VdbEditingContext theVdbContext ) throws IllegalStateException {
        super(theParent, SWT.NONE);

        Assertion.isNotNull(theVdb);
        Assertion.isNotNull(theVdbContext);
        Assertion.isInstanceOf(theVdbContext, InternalVdbEditingContext.class, getString("incorrectVdbContextClass")); //$NON-NLS-1$
        // Assertion.assertTrue(theVdb.exists());

        this.vdbContext = (InternalVdbEditingContext)theVdbContext;
        this.vdbContext.addChangeListener(this);
        this.listeners = new ListenerList(ListenerList.IDENTITY);

        this.labelProvider = new VdbDefinitionLabelProvider(this.vdbContext);

        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));

        createContents();

        // Set initial selection if table contains one or more entries

        if (table.getItemCount() > 0) {
            getViewer().setSelection(new StructuredSelection(table.getItem(0).getData()));
        }
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {
        this.listeners.add(theListener);
    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
     * @since 4.3
     */
    public void controlMoved( ControlEvent theEvent ) {
    }

    /**
     * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
     * @since 4.3
     */
    public void controlResized( ControlEvent theEvent ) {
        Composite container = table.getParent();
        Rectangle area = container.getClientArea();
        Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        int width = area.width - 2 * table.getBorderWidth();

        if (preferredSize.y > area.height) {
            // Subtract the scrollbar width from the total column width
            // if a vertical scrollbar will be required
            Point vBarSize = table.getVerticalBar().getSize();
            width -= vBarSize.x;
        }

        Point oldSize = table.getSize();
        TableColumn[] columns = table.getColumns();

        if (oldSize.x > area.width) {
            // table is getting smaller so make the columns
            // smaller first and then resize the table to
            // match the client area width
            columns[0].setWidth(width / 4);
            columns[1].setWidth(width - columns[0].getWidth());
            table.setSize(area.width, oldSize.y);
        } else {
            // table is getting bigger so make the table
            // bigger first and then make the columns wider
            // to match the client area width
            table.setSize(area.width, area.height / 4);
            columns[0].setWidth(width / 4);
            columns[1].setWidth(width - columns[0].getWidth());
        }
    }

    private void createContents() {
        SashForm splitter = new SashForm(this, SWT.VERTICAL);
        GridData gid = new GridData();
        gid.grabExcessHorizontalSpace = gid.grabExcessVerticalSpace = true;
        gid.horizontalAlignment = gid.verticalAlignment = GridData.FILL;
        splitter.setLayoutData(gid);

        createTable(splitter);
        createProperties(splitter);

        splitter.setWeights(new int[] {3, 6});
        splitter.layout();

        updateState(false);
    }

    private void createTable( Composite theParent ) {
        Composite pnlContents = new Composite(theParent, SWT.NONE) {
            // size the table to show a minimum number of rows
            @Override
            public Point computeSize( int wHint,
                                      int hHint,
                                      boolean changed ) {
                Table table = (Table)getChildren()[0];
                return super.computeSize(wHint, table.getItemHeight() * 4 + table.getHeaderHeight(), changed);
            }
        };
        pnlContents.setLayout(new GridLayout(2, false));
        pnlContents.setLayoutData(new GridData(GridData.FILL_BOTH));
        pnlContents.addControlListener(this);

        this.viewer = new TableViewer(pnlContents, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
        this.viewer.setLabelProvider(this.labelProvider);
        this.viewer.setContentProvider(new VdbDefinitionContentProvider());
        this.viewer.setColumnProperties(COLUMN_PROPERTIES);
        this.viewer.addFilter(new ViewerFilter() {
            // show physical models only
            @Override
            public boolean select( Viewer viewer,
                                   Object parentElement,
                                   Object element ) {
                if (element instanceof BasicVDBModelDefn) {
                    String modelDefnTypeName = ((BasicVDBModelDefn)element).getModelTypeName();
                    return (ModelType.PHYSICAL_LITERAL.toString().equals(modelDefnTypeName) || ModelType.MATERIALIZATION_LITERAL.toString().equals(modelDefnTypeName));
                }

                return false;
            }
        });
        this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged( SelectionChangedEvent theEvent ) {
                handleSelectionChanged();
            }
        });
        this.viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick( DoubleClickEvent theEvent ) {
                handleDoubleClick();
            }
        });

        this.table = this.viewer.getTable();
        this.table.setLayout(new TableLayout());

        TableColumn column1 = new TableColumn(this.table, SWT.NONE);
        column1.setText(getString("tablecolumn.models")); //$NON-NLS-1$

        TableColumn column2 = new TableColumn(this.table, SWT.NONE);
        column2.setText(getString("tablecolumn.bindings")); //$NON-NLS-1$

        GridData data = new GridData();
        data.grabExcessVerticalSpace = true;
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;

        this.table.setLayoutData(data);
        this.table.setFont(theParent.getFont());
        this.table.setHeaderVisible(true);

        // add button to edit binding for the selected row
        Composite pnlButtons = new Composite(pnlContents, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = layout.marginWidth = 0;
        pnlButtons.setLayout(layout);
        pnlButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        this.btnEdit = new Button(pnlButtons, SWT.PUSH);
        this.btnEdit.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        this.btnEdit.setText(getString("btnEdit.text")); //$NON-NLS-1$
        this.btnEdit.setToolTipText(getString("btnEdit.toolTip")); //$NON-NLS-1$
        this.btnEdit.setEnabled(false);
        this.btnEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theE ) {
                handleEditBinding();
            }
        });
        this.btnEdit.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent theEvent ) {
                internalDispose();
            }
        });

        this.btnUnbind = new Button(pnlButtons, SWT.PUSH);
        this.btnUnbind.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        this.btnUnbind.setText(getString("btnUnbind.text")); //$NON-NLS-1$
        this.btnUnbind.setToolTipText(getString("btnUnbind.toolTip")); //$NON-NLS-1$
        this.btnUnbind.setEnabled(false);
        this.btnUnbind.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theE ) {
                handleUnbindBinding();
            }
        });
        this.btnUnbind.addDisposeListener(new DisposeListener() {
            public void widgetDisposed( DisposeEvent theEvent ) {
                internalDispose();
            }
        });
    }

    /**
     * Handler for when the button to show/hide advanced/expert properties is clicked.
     * 
     * @since 5.0.2
     */
    void handleShowPropertiesSelected() {
        this.sourceProvider.setShowExpertProperties(this.btnShowExpertProps.getSelection());
        this.propertyPage.refresh();
    }

    private void createProperties( Composite theParent ) {
        Composite c = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH, 1, 2);
        WidgetFactory.createLabel(c, getString("lblBindingProperties")); //$NON-NLS-1$

        // toggle button to show/hide expert properties
        this.btnShowExpertProps = new Button(c, SWT.CHECK);
        this.btnShowExpertProps.setEnabled(false);
        this.btnShowExpertProps.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        this.btnShowExpertProps.setText(getString("btnShowExpertProps.text")); //$NON-NLS-1$
        this.btnShowExpertProps.setToolTipText(getString("btnShowExpertProps.tooTip")); //$NON-NLS-1$
        this.btnShowExpertProps.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected( SelectionEvent theE ) {
                handleShowPropertiesSelected();
            }
        });

        this.propertyPage = new PropertySheetPage() {
            @Override
            public void createControl( Composite parent ) {
                GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
                gd.horizontalSpan = 2;
                Composite border = new Composite(parent, SWT.BORDER);
                border.setLayoutData(gd);
                GridLayout gridLayout = new GridLayout();
                gridLayout.marginHeight = 0;
                gridLayout.marginWidth = 0;
                border.setLayout(gridLayout);
                super.createControl(border);

                // override the default sorter
                setSorter(new NoSortingPropertySorter());
            }
        };

        this.propertyPage.createControl(c);
        this.propertyPage.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        sourceProvider = new ConnectorBindingsPropertySourceProvider(this.vdbContext);
        sourceProvider.addPropertyChangeListener(this);
        sourceProvider.setEditable(true);
        this.propertyPage.setPropertySourceProvider(sourceProvider);
    }

    public VDBDefn getVdbDefn() {
        return getVdbDefnHelper().getVdbDefn();
    }

    private PropertySheetPage getPropertyPage() {
        return this.propertyPage;
    }

    private VdbDefnHelper getVdbDefnHelper() {
        return DqpPlugin.getInstance().getVdbDefnHelper(this.vdbContext);
    }

    private TableViewer getViewer() {
        return this.viewer;
    }

    void handleDoubleClick() {
        handleEditBinding();
    }

    private boolean resetForReadOnly() {
        boolean isReadOnly = false;
        if (vdbContext != null && vdbContext.isReadOnly()) {
            isReadOnly = true;
        }
        this.setReadonly(isReadOnly);
        this.btnEdit.setEnabled(!isReadOnly);
        this.btnUnbind.setEnabled(!isReadOnly);

        return isReadOnly;
    }

    void handleUnbindBinding() {
        if (resetForReadOnly()) {
            MessageDialog.openWarning(getShell(), getString("readOnlyVDBDialogTitle"), getString("readOnlyVDBDialogMessage")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        if (!(getViewer().getControl().isDisposed()) && (getVdbDefnHelper() != null)) {
            Object obj = ((IStructuredSelection)getViewer().getSelection()).getFirstElement();

            if ((obj != null) && (obj instanceof BasicVDBModelDefn)) {
                BasicVDBModelDefn modelDefn = (BasicVDBModelDefn)obj;
                Connector existingBinding = getVdbDefnHelper().getFirstConnector(modelDefn);
                if (existingBinding != null) {
                    getVdbDefnHelper().removeConnector(modelDefn, existingBinding);

                    getPropertyPage().selectionChanged(null, new StructuredSelection());

                    getViewer().refresh(modelDefn, true);
                    propertyChange(null);
                }
                // Call handleSelectionChanged() so the unbind button get's disabled correctly
                handleSelectionChanged();
            }
        }
    }
    
    private ExecutionAdmin getAdmin() {
        // TODO implement
        return null;
    }

    void handleEditBinding() {
        if (resetForReadOnly()) {
            MessageDialog.openWarning(getShell(), getString("readOnlyVDBDialogTitle"), getString("readOnlyVDBDialogMessage")); //$NON-NLS-1$ //$NON-NLS-2$
            return;
        }

        if (!(getViewer().getControl().isDisposed()) && (getVdbDefnHelper() != null)) {
            Object obj = ((IStructuredSelection)getViewer().getSelection()).getFirstElement();

            if ((obj != null) && (obj instanceof BasicVDBModelDefn)) {
                BasicVDBModelDefn modelDefn = (BasicVDBModelDefn)obj;
                Connector connector = getVdbDefnHelper().getFirstConnector(modelDefn);
                String name = ModelerDqpUtils.createNewBindingName(modelDefn);

                NewConnectorBindingDialog dialog = new NewConnectorBindingDialog(getShell(), connector, name, this.vdbContext, modelDefn);
                dialog.open();

                if (dialog.getReturnCode() == Window.OK) {
                    boolean changed = false;
                    Connector newValue = dialog.getConnector();
                    Connector oldValue = getVdbDefnHelper().getFirstConnector(modelDefn);

                    // if a different instance of a binding is the new value then the New Binding Dialog was OK'd
                    if (oldValue == null) {
                        changed = (newValue != null);
                    } else {
                        if (newValue == null) {
                            changed = true;
                        } else {
                            changed = !oldValue.getName().equals(newValue.getName());
                        }
                    }

                    // if changed update binding, refresh controls, and fire property change
                    try {
                        if (changed) {
                            if (oldValue != null) {
                                getVdbDefnHelper().removeConnector(modelDefn, oldValue);
                            }

                            if (newValue != null) {
                                // add binding to configuration if it doesn't already exist
                                if (ModelerDqpUtils.isUniqueBindingName(newValue.getName())) {
                                    getAdmin().addConnectorBinding(newValue);
                                }

                                ConnectorType type = ModelerDqpUtils.getConnectorType(newValue);
                                getVdbDefnHelper().setConnector(modelDefn, newValue, type);
                            }

                            getViewer().refresh(modelDefn, true);
                            getPropertyPage().selectionChanged(null, new StructuredSelection(modelDefn));
                            propertyChange(null); // fire event to indicate something has changed
                        }
                    } catch (Exception error) {
                        DqpUiPlugin.showErrorDialog(getShell(), error);
                    }
                }
            }
        }
    }

    void handleSelectionChanged() {
        resetForReadOnly();

        if (!getViewer().getControl().isDisposed()) {
            IStructuredSelection selection = (IStructuredSelection)getViewer().getSelection();
            Object obj = selection.getFirstElement();

            if ((obj != null) && !(obj instanceof BasicVDBModelDefn)) {
                selection = StructuredSelection.EMPTY;
            }

            getPropertyPage().selectionChanged(null, selection);
            packPropertiesPage();

            // enable/disable edit button
            this.btnEdit.setEnabled(!selection.isEmpty() && this.sourceProvider.isEditable());

            // enable/disable unbind button
            // If not bound, note and add to enablement of Unbind action
            BasicVDBModelDefn defn = (BasicVDBModelDefn)obj;
            boolean isBound = defn != null && !defn.getConnectorBindingNames().isEmpty();

            this.btnUnbind.setEnabled(!selection.isEmpty() && isBound && this.sourceProvider.isEditable());

            // enable/disable show expert properties
            this.btnShowExpertProps.setEnabled(!selection.isEmpty() && isBound);
        }
    }

    public boolean hasVdbDefnChanges() {
        return this.hasChanges;
    }

    public boolean isSaveOnChange() {
        return this.saveOnChange;
    }

    private void packPropertiesPage() {
        Tree tree = (Tree)this.propertyPage.getControl();
        TreeColumn[] treeCols = tree.getColumns();

        for (int i = 0; i < treeCols.length; ++i) {
            treeCols[i].pack();
        }
    }

    /**
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     * @since 4.3
     */
    public void propertyChange( PropertyChangeEvent theEvent ) {
        this.hasChanges = true;

        if (this.saveOnChange) {
            saveInternal();
        }

        packPropertiesPage();

        // alert listeners something has changed
        Object[] changeListeners = this.listeners.getListeners();

        for (int i = 0; i < changeListeners.length; ++i) {
            ((IChangeListener)changeListeners[i]).stateChanged(this);
        }
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
        this.listeners.remove(theListener);
    }

    private void saveInternal() {
        try {
            getVdbDefnHelper().saveDefn();
        } catch (Exception theException) {
            UTIL.log(theException);
            theException.printStackTrace();
        }
    }

    public void save() {
        if (!this.saveOnChange) {
            saveInternal();
        }

        this.hasChanges = false;
    }

    /**
     * @see org.eclipse.swt.widgets.Composite#setFocus()
     * @since 4.3
     */
    @Override
    public boolean setFocus() {
        boolean result = super.setFocus();

        updateState(true);

        return result;
    }

    public void setReadonly( boolean theReadonlyFlag ) {
        // defect 19623 - disable editing, not the table:
        sourceProvider.setEditable(!theReadonlyFlag);
        // force a re-read:
        propertyPage.setPropertySourceProvider(sourceProvider);

        // change color to match enabled or disabled color
        int colorCode = (theReadonlyFlag ? SWT.COLOR_WIDGET_BACKGROUND : SWT.COLOR_WHITE);
        Color bkg = UiUtil.getSystemColor(colorCode);
        viewer.getControl().setBackground(bkg);
        propertyPage.getControl().setBackground(bkg);
        boolean singleSelection = false;
        boolean isBound = false;
        if (!viewer.getSelection().isEmpty()) {
            StructuredSelection sel = (StructuredSelection)viewer.getSelection();
            if (sel.size() == 1) {
                singleSelection = true;
                // enable/disable unbind button
                // If not bound, note and add to enablement of Unbind action
                BasicVDBModelDefn defn = (BasicVDBModelDefn)sel.getFirstElement();
                isBound = !defn.getConnectorBindingNames().isEmpty();
            }
        }
        this.btnEdit.setEnabled(singleSelection && !theReadonlyFlag);
        this.btnUnbind.setEnabled(singleSelection && isBound && !theReadonlyFlag);

        // If !read-only, we need to re-set selection so we get enablement???
    }

    public void setSaveOnChange( boolean theSaveOnChangeFlag ) {
        this.saveOnChange = theSaveOnChangeFlag;
    }

    /**
     * The normal dispose() method was not getting called when the bindings editor was closed. Needed a way to remove listeners.
     * 
     * @since 5.5
     */
    public void internalDispose() {
        this.vdbContext.removeChangeListener(this);
    }

    /**
     * @see com.metamatrix.core.event.IChangeListener#stateChanged(com.metamatrix.core.event.IChangeNotifier)
     * @since 5.5
     */
    public void stateChanged( IChangeNotifier theSource ) {
        if (!isDisposed()) {
            updateState(true);
        }
    }

    private void updateState( boolean theUpdateDefnFlag ) {
        Object input = null;

        if (this.vdbContext != null && this.vdbContext.isOpen()) {

            try {
                VdbDefnHelper helper = getVdbDefnHelper();

                if (theUpdateDefnFlag) {
                    helper.updateToVdb(false);

                    if (VdbEditPlugin.shouldAutoBind() && helper.autoAssignBindings() && !isSaveOnChange()) {
                        this.hasChanges = true;
                        this.vdbContext.setModified();

                        // tell user something was changed
                        getDisplay().asyncExec(new Runnable() {
                            public void run() {
                                MessageDialog.openInformation(getShell(), getString("autoAssignedBindingDialog.title"), //$NON-NLS-1$
                                                              getString("autoAssignedBindingDialog.msg")); //$NON-NLS-1$
                            }
                        });
                    }
                }

                input = helper.getVdbDefn();
            } catch (Exception theException) {
                input = null;
                UTIL.log(theException);
                theException.printStackTrace();
                MessageDialog.openError(getShell(), getString("errorUpdatingVdbHelperStateDialog.title"), //$NON-NLS-1$
                                        theException.getLocalizedMessage());
            }
        }

        this.viewer.setInput(input);
    }

    /**
     * This sorter does not doing any sorting. The sorting is done when the descriptors are retrieved. Needed to override the
     * default sorter.
     * 
     * @since 5.5
     */
    class NoSortingPropertySorter extends PropertySheetSorter {
        @Override
        public int compare( IPropertySheetEntry theEntryA,
                            IPropertySheetEntry theEntryB ) {
            return 0;
        }
    }

}
