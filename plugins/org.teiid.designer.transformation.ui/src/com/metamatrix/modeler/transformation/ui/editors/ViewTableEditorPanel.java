/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.transformation.ui.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.teiid.core.types.DataTypeManager;
import org.teiid.designer.relational.RelationalConstants;
import org.teiid.designer.relational.model.RelationalColumn;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.relational.ui.UiPlugin;
import com.metamatrix.modeler.transformation.model.RelationalViewTable;
import com.metamatrix.modeler.transformation.ui.Messages;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.modeler.transformation.ui.wizards.sqlbuilder.SQLTemplateDialog;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.table.ComboBoxEditingSupport;
import com.metamatrix.ui.text.StyledTextEditor;

/*
 *  ViewTableEditorPanel - this class creates the tabbed panel which is used
 *  for creating view tables, complete with SQL transformation. 
 */
public class ViewTableEditorPanel implements RelationalConstants {
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    IFile modelFile;
    IStatus currentStatus;
    IDialogStatusListener statusListener;

    private RelationalViewTable viewTable;

    // Tabbed Folder and Tabs
    TabFolder tabFolder;
    TabItem generalPropertiesTab;
    TabItem columnsTab;
    TabItem sqlTab;

    // Table Property Tab Controls
    Button supportsUpdateCB;
    Text modelNameText, nameText;
    StyledTextEditor descriptionTextEditor;

    // Table Column Tab Controls
    Button addColumnButton, deleteColumnButton, upColumnButton, downColumnButton;
    TableViewer columnsViewer;

    // Table SQL Text Tab Controls
    SqlTextViewer sqlTextViewer;
    Document sqlDocument;

    boolean synchronizing = false;

    /*
     * Constructor
     * @param parent the parent composite
     * @param viewTable the RelationalViewTable object
     * @param modelFile the model
     * @param statusListener listener for status events
     */
    public ViewTableEditorPanel( Composite parent,
                                 RelationalViewTable viewTable,
                                 IFile modelFile,
                                 IDialogStatusListener statusListener ) {
        this.viewTable = viewTable;
        this.modelFile = modelFile;
        this.statusListener = statusListener;

        createPanel(parent);
        validate();
        initializeUi();

        this.nameText.setFocus();
    }

    protected void setStatus( IStatus status ) {
        currentStatus = status;

        statusListener.notifyStatusChanged(currentStatus);
    }

    /*
     * Create the Tabbed Panel
     */
    public void createPanel( Composite parent ) {
        new Label(parent, SWT.NONE);
        {
            Text helpText = new Text(parent, SWT.WRAP | SWT.READ_ONLY);
            helpText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
            helpText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
            helpText.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
            ((GridData)helpText.getLayoutData()).horizontalSpan = 1;
            ((GridData)helpText.getLayoutData()).heightHint = 40;
            ((GridData)helpText.getLayoutData()).widthHint = 450;
            helpText.setText(Messages.createRelationalViewHelpText);
        }
        tabFolder = new TabFolder(parent, SWT.TOP | SWT.BORDER);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        ((GridData)tabFolder.getLayoutData()).heightHint = 250;

        createGeneralPropertiesTab(tabFolder);
        createColumnsTab(tabFolder);
        createSQLTab(tabFolder);
    }

    /*
     * Create the General Properties Tab
     */
    void createGeneralPropertiesTab( TabFolder folderParent ) {
        // build the SELECT tab
        Composite thePanel = createGeneralPropertiesPanel(folderParent);

        this.generalPropertiesTab = new TabItem(folderParent, SWT.NONE);
        this.generalPropertiesTab.setControl(thePanel);
        this.generalPropertiesTab.setText(Messages.propertiesLabel);
        this.generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE, ModelType.VIRTUAL, Status.OK_STATUS));
    }

    /*
     * Create the target Columns Tab
     */
    void createColumnsTab( TabFolder folderParent ) {
        Composite thePanel = createColumnTablePanel(folderParent);

        this.columnsTab = new TabItem(folderParent, SWT.NONE);
        this.columnsTab.setControl(thePanel);
        this.columnsTab.setText(Messages.columnsLabel);
        this.columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, ModelType.VIRTUAL, Status.OK_STATUS));
    }

    /*
     * Create the SQL Tab
     */
    void createSQLTab( TabFolder folderParent ) {
        Composite thePanel = createSQLPanel(folderParent);

        this.sqlTab = new TabItem(folderParent, SWT.NONE);
        this.sqlTab.setControl(thePanel);
        this.sqlTab.setText(Messages.sqlLabel);
    }

    /*
     * Synchronize the UI controls with the RelationalViewTable object
     */
    protected void initializeUi() {
        if (synchronizing) {
            return;
        }

        synchronizing = true;

        // --------------------------------
        // General Property Tab Controls
        // --------------------------------
        if (viewTable.getName() != null) {
            if (WidgetUtil.widgetValueChanged(this.nameText, viewTable.getName())) {
                this.nameText.setText(viewTable.getName());
            }
        } else {
            if (WidgetUtil.widgetValueChanged(this.nameText, EMPTY_STRING)) {
                this.nameText.setText(EMPTY_STRING);
            }
        }

        if (viewTable.getSupportsUpdate()) {
            this.supportsUpdateCB.setSelection(true);
        } else {
            this.supportsUpdateCB.setSelection(false);
        }

        // generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE,
        // viewTable.getModelType(),
        // viewTable.getStatus()));

        // --------------------------------
        // Target Columns Tab Controls
        // --------------------------------
        this.columnsViewer.getTable().removeAll();
        IStatus maxStatus = Status.OK_STATUS;
        for (RelationalColumn row : viewTable.getColumns()) {
            if (row.getStatus().getSeverity() > maxStatus.getSeverity()) {
                maxStatus = row.getStatus();
            }
            this.columnsViewer.add(row);
        }
        columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.COLUMN, viewTable.getModelType(), maxStatus));

        // --------------------------------
        // SQL Display Tab Controls
        // --------------------------------
        String sql = viewTable.getTransformationSQL();
        sqlDocument.set(sql);

        maxStatus = Status.OK_STATUS;
        synchronizing = false;
    }

    /*
     * Create the General Properties tab panel
     */
    @SuppressWarnings("unused")
	private Composite createGeneralPropertiesPanel( Composite parent ) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 3);
        thePanel.setLayout(new GridLayout(3, false));
        GridData panelGD = new GridData(GridData.FILL_BOTH);
        panelGD.heightHint = 300;
        thePanel.setLayoutData(panelGD);

        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.modelFileLabel);

        this.modelNameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.modelNameText.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
        this.modelNameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.modelNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if( this.modelFile != null ) {
            modelNameText.setText(this.modelFile.getName());
        }
        addSpacerLabels(thePanel, 1);

        label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.nameLabel);

        this.nameText =  new Text(thePanel, SWT.BORDER | SWT.SINGLE);
        this.nameText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
        this.nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        this.nameText.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                String value = nameText.getText();
                if (value == null) {
                    value = EMPTY_STRING;
                }

                viewTable.setName(value);
                handleInfoChanged();
            }
        });
        addSpacerLabels(thePanel, 1);
        
        DESCRIPTION_GROUP: {
            final Group descGroup = WidgetFactory.createGroup(thePanel, Messages.description, GridData.FILL_HORIZONTAL, 3);
            descriptionTextEditor = new StyledTextEditor(descGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
            final GridData descGridData = new GridData(GridData.FILL_BOTH);
            descGridData.horizontalSpan = 1;
            descGridData.heightHint = 80;
            descGridData.minimumHeight = 30;
            descGridData.grabExcessVerticalSpace = true;
            descriptionTextEditor.setLayoutData(descGridData);
            descriptionTextEditor.setText(""); //$NON-NLS-1$
            descriptionTextEditor.getTextWidget().addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					viewTable.setDescription(descriptionTextEditor.getText());
				}
			});
        }
        
        this.supportsUpdateCB = new Button(thePanel, SWT.CHECK | SWT.RIGHT);
        this.supportsUpdateCB.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        this.supportsUpdateCB.setText(Messages.supportsUpdateLabel);
        this.supportsUpdateCB.addSelectionListener(new SelectionAdapter() {
            /**            		
             * {@inheritDoc}
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                viewTable.setSupportsUpdate(supportsUpdateCB.getSelection());
                handleInfoChanged();
            }
        });

        addSpacerLabels(thePanel, 2);

        addSpacerLabels(thePanel, 3);
        addSpacerLabels(thePanel, 3);

        return thePanel;
    }

    private void addSpacerLabels( Composite parent,
                                  int numSpacers ) {
        for (int i = 0; i < numSpacers; i++) {
            new Label(parent, SWT.NONE);
        }
    }

    /*
     * Create the Target Columns tab panel
     */
    Composite createColumnTablePanel( Composite parent ) {

        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        thePanel.setLayout(new GridLayout(1, false));
        GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
        groupGD.heightHint = 300;
        thePanel.setLayoutData(groupGD);

        Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 4);
        buttonPanel.setLayout(new GridLayout(4, false));
        GridData panelGD = new GridData();
        buttonPanel.setLayoutData(panelGD);

        addColumnButton = new Button(buttonPanel, SWT.PUSH);
        addColumnButton.setText(Messages.addLabel);
        addColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addColumnButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                viewTable.createColumn();
                handleColumnsChanged();
            }

        });

        deleteColumnButton = new Button(buttonPanel, SWT.PUSH);
        deleteColumnButton.setText(Messages.deleteLabel);
        deleteColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        deleteColumnButton.setEnabled(false);
        deleteColumnButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                RelationalColumn column = null;

                IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
                for (Object obj : selection.toArray()) {
                    if (obj instanceof RelationalColumn) {
                        column = (RelationalColumn)obj;
                        break;
                    }
                }
                if (column != null) {
                    viewTable.removeColumn(column);
                    deleteColumnButton.setEnabled(false);
                    handleColumnsChanged();
                }
            }

        });

        upColumnButton = new Button(buttonPanel, SWT.PUSH);
        upColumnButton.setText(Messages.moveUpLabel);
        upColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        upColumnButton.setEnabled(false);
        upColumnButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                RelationalColumn info = null;

                IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
                for (Object obj : selection.toArray()) {
                    if (obj instanceof RelationalColumn) {
                        info = (RelationalColumn)obj;
                        break;
                    }
                }
                if (info != null) {
                    int selectedIndex = columnsViewer.getTable().getSelectionIndex();
                    viewTable.moveColumnUp(info);
                    handleColumnsChanged();
                    columnsViewer.getTable().select(selectedIndex - 1);
                    downColumnButton.setEnabled(viewTable.canMoveColumnDown(info));
                    upColumnButton.setEnabled(viewTable.canMoveColumnUp(info));

                }
            }

        });

        downColumnButton = new Button(buttonPanel, SWT.PUSH);
        downColumnButton.setText(Messages.moveDownLabel);
        downColumnButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        downColumnButton.setEnabled(false);
        downColumnButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                RelationalColumn info = null;

                IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
                for (Object obj : selection.toArray()) {
                    if (obj instanceof RelationalColumn) {
                        info = (RelationalColumn)obj;
                        break;
                    }
                }
                if (info != null) {
                    int selectedIndex = columnsViewer.getTable().getSelectionIndex();
                    viewTable.moveColumnDown(info);
                    handleColumnsChanged();
                    columnsViewer.getTable().select(selectedIndex + 1);
                    downColumnButton.setEnabled(viewTable.canMoveColumnDown(info));
                    upColumnButton.setEnabled(viewTable.canMoveColumnUp(info));

                }
            }

        });

        Table columnTable = new Table(thePanel, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        columnTable.setHeaderVisible(true);
        columnTable.setLinesVisible(true);
        columnTable.setLayout(new TableLayout());
        columnTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        this.columnsViewer = new TableViewer(columnTable);

        GridData data = new GridData(GridData.FILL_BOTH);
        this.columnsViewer.getControl().setLayoutData(data);

        // create columns
        TableViewerColumn column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.columnNameLabel + "          "); //$NON-NLS-1$
        column.setEditingSupport(new ColumnNameEditingSupport(this.columnsViewer));
        column.setLabelProvider(new ColumnDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.dataTypeLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ColumnDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.columnsViewer));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ColumnDataLabelProvider(2));
        column.setEditingSupport(new ColumnWidthEditingSupport(this.columnsViewer));
        column.getColumn().pack();

        if (this.viewTable != null) {
            for (RelationalColumn row : this.viewTable.getColumns()) {
                this.columnsViewer.add(row);
            }
        }

        this.columnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection sel = (IStructuredSelection)event.getSelection();

                if (sel.isEmpty()) {
                    deleteColumnButton.setEnabled(false);
                    upColumnButton.setEnabled(false);
                    downColumnButton.setEnabled(false);
                } else {
                    boolean enable = true;
                    Object[] objs = sel.toArray();
                    RelationalColumn columnInfo = null;
                    for (Object obj : objs) {
                        if (!(obj instanceof RelationalColumn)) {
                            enable = false;
                            break;
                        }
                        columnInfo = (RelationalColumn)obj;
                    }
                    if (objs.length == 0) {
                        enable = false;
                    }
                    deleteColumnButton.setEnabled(enable);
                    if (enable) {
                        upColumnButton.setEnabled(viewTable.canMoveColumnUp(columnInfo));
                        downColumnButton.setEnabled(viewTable.canMoveColumnDown(columnInfo));
                    }

                }

            }
        });

        return thePanel;
    }

    /*
     * Create the SQL Display tab panel
     */
    Composite createSQLPanel( Composite parent ) {
        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        thePanel.setLayout(new GridLayout(1, false));
        GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
        groupGD.heightHint = 300;
        thePanel.setLayoutData(groupGD);

        Label label = new Label(thePanel, SWT.NONE);
        label.setText(Messages.sqlDescriptionLabel);

        Button templateButton = new Button(thePanel, SWT.LEFT);
        templateButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        templateButton.setText(Messages.sqlTemplateLabel);
        templateButton.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                SQLTemplateDialog templateDialog = new SQLTemplateDialog(UiUtil.getWorkbenchShellOnlyIfUiThread(),
                                                                         SQLTemplateDialog.TABLE_TEMPLATES);
                if (templateDialog.open() == Window.OK) {
                	String sql = templateDialog.getSQL();
                    viewTable.setTransformationSQL(sql);
                    sqlDocument.set(sql);
                    handleInfoChanged();
                }
            }
        });

        createSqlGroup(thePanel);
        return thePanel;
    }

    /*
     * The SQL Display area portion of the SQL Tab
     */
    private void createSqlGroup( Composite parent ) {
        Group textTableOptionsGroup = WidgetFactory.createGroup(parent, Messages.sqlGroupLabel, SWT.NONE, 2, 1);
        textTableOptionsGroup.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 120;
        gd.horizontalSpan = 2;
        textTableOptionsGroup.setLayoutData(gd);

        ColorManager colorManager = new ColorManager();
        int styles = SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.FULL_SELECTION;

        sqlTextViewer = new SqlTextViewer(textTableOptionsGroup, new VerticalRuler(0), styles, colorManager);
        sqlDocument = new Document();
        sqlTextViewer.setInput(sqlDocument);
        sqlTextViewer.getTextWidget().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				viewTable.setTransformationSQL(sqlTextViewer.getTextWidget().getText());
				handleInfoChanged();
				
			}
		});
        sqlTextViewer.setEditable(true);
        sqlDocument.set(CoreStringUtil.Constants.EMPTY_STRING);
        sqlTextViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /*
     * Handler for info changed
     */
    void handleInfoChanged() {
        if (synchronizing) {
            return;
        }
        validate();
    }
    
    void handleColumnsChanged() {
        this.columnsViewer.getTable().removeAll();
        IStatus maxStatus = Status.OK_STATUS;
        for (RelationalColumn row : viewTable.getColumns()) {
            if (row.getStatus().getSeverity() > maxStatus.getSeverity()) {
                maxStatus = row.getStatus();
            }
            this.columnsViewer.add(row);
        }
        handleInfoChanged();
    }

    /*
     * Update status based on ViewTable object validation
     */
    public void validate() {
        this.viewTable.validate();

        IStatus currentStatus = this.viewTable.getStatus();
        if (currentStatus.isOK()) {
            setStatus(Status.OK_STATUS);
        } else {
            setStatus(currentStatus);
        }

    }

    class ColumnDataLabelProvider extends ColumnLabelProvider {

        private final int columnNumber;

        public ColumnDataLabelProvider( int columnNumber ) {
            this.columnNumber = columnNumber;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            if (element instanceof RelationalColumn) {
                switch (this.columnNumber) {
                    case 0:
                        return ((RelationalColumn)element).getName();
                    case 1:
                        return ((RelationalColumn)element).getDatatype();
                    case 2:
                        return Integer.toString(((RelationalColumn)element).getLength());
                }
            }
            return EMPTY_STRING;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText( Object element ) {
            switch (this.columnNumber) {
                case 0:
                    return "Tooltip 1"; //getString("columnNameColumnTooltip"); //$NON-NLS-1$
                case 1:
                    return "Tooltip 2"; //getString("datatypeColumnTooltip"); //$NON-NLS-1$
            }
            return "unknown tooltip"; //$NON-NLS-1$
        }

        @Override
        public Image getImage( Object element ) {
            if (this.columnNumber == 0) {
                return UiPlugin.getDefault().getImage(UiConstants.Images.COLUMN_ICON);
            }
            return null;
        }

    }

    class ColumnNameEditingSupport extends EditingSupport {

        private TextCellEditor editor;

        /**
         * Create a new instance of the receiver.
         * 
         * @param viewer
         */
        public ColumnNameEditingSupport( ColumnViewer viewer ) {
            super(viewer);
            this.editor = new TextCellEditor((Composite)viewer.getControl());
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
         */
        protected boolean canEdit( Object element ) {
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
         */
        protected CellEditor getCellEditor( Object element ) {
            return editor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
         */
        protected Object getValue( Object element ) {
            if (element instanceof RelationalColumn) {
                return ((RelationalColumn)element).getName();
            }
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
         *      java.lang.Object)
         */
        protected void setValue( Object element,
                                 Object value ) {
            if (element instanceof RelationalColumn) {
                String oldValue = ((RelationalColumn)element).getName();
                String newValue = (String)value;
                if (newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
                    ((RelationalColumn)element).setName(newValue);
                    columnsViewer.refresh(element);
                    handleColumnsChanged();
                }
            }
        }

    }

    class ColumnWidthEditingSupport extends EditingSupport {

        private TextCellEditor editor;

        /**
         * Create a new instance of the receiver.
         * 
         * @param viewer
         */
        public ColumnWidthEditingSupport( ColumnViewer viewer ) {
            super(viewer);
            this.editor = new TextCellEditor((Composite)viewer.getControl());
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
         */
        protected boolean canEdit( Object element ) {
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
         */
        protected CellEditor getCellEditor( Object element ) {
            return editor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
         */
        protected Object getValue( Object element ) {
            if (element instanceof RelationalColumn) {
                return Integer.toString(((RelationalColumn)element).getLength());
            }
            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
         *      java.lang.Object)
         */
        protected void setValue( Object element,
                                 Object value ) {
            if (element instanceof RelationalColumn) {
                int oldValue = ((RelationalColumn)element).getLength();
                int newValue = oldValue;
                try {
                    newValue = Integer.parseInt((String)value);
                } catch (NumberFormatException ex) {
                    return;
                }
                if (newValue != oldValue) {
                    ((RelationalColumn)element).setLength(newValue);
                    columnsViewer.refresh(element);
                }
            }
        }

    }

    class DatatypeEditingSupport extends ComboBoxEditingSupport {

        private String[] datatypes;
        /**
         * @param viewer
         */
        public DatatypeEditingSupport( ColumnViewer viewer ) {
            super(viewer);
            Set<String> unsortedDatatypes = DataTypeManager.getAllDataTypeNames();
            Collection<String> dTypes = new ArrayList<String>();

            String[] sortedStrings = unsortedDatatypes.toArray(new String[unsortedDatatypes.size()]);
            Arrays.sort(sortedStrings);
            for (String dType : sortedStrings) {
                dTypes.add(dType);
            }

            datatypes = dTypes.toArray(new String[dTypes.size()]);

        }


        @Override
        protected String getElementValue( Object element ) {
            return ((RelationalColumn)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            ((RelationalColumn)element).setDatatype(newValue);
        }
    }

}
