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
import org.teiid.designer.relational.model.RelationalParameter;
import org.teiid.designer.relational.ui.util.RelationalUiUtil;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.relational.ui.UiConstants;
import com.metamatrix.modeler.relational.ui.UiPlugin;
import com.metamatrix.modeler.transformation.model.RelationalViewProcedure;
import com.metamatrix.modeler.transformation.ui.Messages;
import com.metamatrix.modeler.transformation.ui.editors.sqleditor.SqlTextViewer;
import com.metamatrix.modeler.transformation.ui.wizards.sqlbuilder.SQLTemplateDialog;
import com.metamatrix.ui.graphics.ColorManager;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.util.WidgetUtil;
import com.metamatrix.ui.table.ComboBoxEditingSupport;
import com.metamatrix.ui.text.StyledTextEditor;

public class ViewProcedureEditorPanel  implements RelationalConstants {
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    IFile modelFile;
    IStatus currentStatus;
    IDialogStatusListener statusListener;

    private RelationalViewProcedure viewProcedure;

    // Tabbed Folder and Tabs
    TabFolder tabFolder;
    TabItem generalPropertiesTab;
    TabItem columnsTab;
    TabItem sqlTab;

    // Table Property Tab Controls
    Text modelNameText, nameText;
    StyledTextEditor descriptionTextEditor;

    // Table Parameter Tab Controls
    Button addParameterButton, deleteParameterButton, upParameterButton, downParameterButton;
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
    public ViewProcedureEditorPanel( Composite parent,
                                 RelationalViewProcedure viewTable,
                                 IFile modelFile,
                                 IDialogStatusListener statusListener ) {
        this.viewProcedure = viewTable;
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
        createParametersTab(tabFolder);
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
     * Create the target Parameters Tab
     */
    void createParametersTab( TabFolder folderParent ) {
        Composite thePanel = createParameterTablePanel(folderParent);

        this.columnsTab = new TabItem(folderParent, SWT.NONE);
        this.columnsTab.setControl(thePanel);
        this.columnsTab.setText(Messages.parametersLabel);
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
        if (viewProcedure.getName() != null) {
            if (WidgetUtil.widgetValueChanged(this.nameText, viewProcedure.getName())) {
                this.nameText.setText(viewProcedure.getName());
            }
        } else {
            if (WidgetUtil.widgetValueChanged(this.nameText, EMPTY_STRING)) {
                this.nameText.setText(EMPTY_STRING);
            }
        }

        // generalPropertiesTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.TABLE,
        // viewTable.getModelType(),
        // viewTable.getStatus()));

        // --------------------------------
        // Target Parameters Tab Controls
        // --------------------------------
        this.columnsViewer.getTable().removeAll();
        IStatus maxStatus = Status.OK_STATUS;
        for (RelationalParameter row : viewProcedure.getParameters()) {
            if (row.getStatus().getSeverity() > maxStatus.getSeverity()) {
                maxStatus = row.getStatus();
            }
            this.columnsViewer.add(row);
        }
        columnsTab.setImage(RelationalUiUtil.getRelationalImage(TYPES.PARAMETER, viewProcedure.getModelType(), maxStatus));

        // --------------------------------
        // SQL Display Tab Controls
        // --------------------------------
        String sql = viewProcedure.getTransformationSQL();
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

                viewProcedure.setName(value);
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
					viewProcedure.setDescription(descriptionTextEditor.getText());
				}
			});
        }

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
     * Create the Target Parameters tab panel
     */
    Composite createParameterTablePanel( Composite parent ) {

        Composite thePanel = WidgetFactory.createPanel(parent, SWT.NONE, 1, 1);
        thePanel.setLayout(new GridLayout(1, false));
        GridData groupGD = new GridData(GridData.FILL_HORIZONTAL);
        groupGD.heightHint = 300;
        thePanel.setLayoutData(groupGD);

        Composite buttonPanel = WidgetFactory.createPanel(thePanel, SWT.NONE, 1, 4);
        buttonPanel.setLayout(new GridLayout(4, false));
        GridData panelGD = new GridData();
        buttonPanel.setLayoutData(panelGD);

        addParameterButton = new Button(buttonPanel, SWT.PUSH);
        addParameterButton.setText(Messages.addLabel);
        addParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addParameterButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                viewProcedure.createParameter();
                handleParametersChanged();
            }

        });

        deleteParameterButton = new Button(buttonPanel, SWT.PUSH);
        deleteParameterButton.setText(Messages.deleteLabel);
        deleteParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        deleteParameterButton.setEnabled(false);
        deleteParameterButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                RelationalParameter parameter = null;

                IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
                for (Object obj : selection.toArray()) {
                    if (obj instanceof RelationalParameter) {
                    	parameter = (RelationalParameter)obj;
                        break;
                    }
                }
                if (parameter != null) {
                    viewProcedure.removeParameter(parameter);
                    deleteParameterButton.setEnabled(false);
                    handleParametersChanged();
                }
            }

        });

        upParameterButton = new Button(buttonPanel, SWT.PUSH);
        upParameterButton.setText(Messages.moveUpLabel);
        upParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        upParameterButton.setEnabled(false);
        upParameterButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                RelationalParameter info = null;

                IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
                for (Object obj : selection.toArray()) {
                    if (obj instanceof RelationalParameter) {
                        info = (RelationalParameter)obj;
                        break;
                    }
                }
                if (info != null) {
                    int selectedIndex = columnsViewer.getTable().getSelectionIndex();
                    viewProcedure.moveParameterUp(info);
                    handleParametersChanged();
                    columnsViewer.getTable().select(selectedIndex - 1);
                    downParameterButton.setEnabled(viewProcedure.canMoveParameterDown(info));
                    upParameterButton.setEnabled(viewProcedure.canMoveParameterUp(info));

                }
            }

        });

        downParameterButton = new Button(buttonPanel, SWT.PUSH);
        downParameterButton.setText(Messages.moveDownLabel);
        downParameterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        downParameterButton.setEnabled(false);
        downParameterButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected( SelectionEvent e ) {
                RelationalParameter info = null;

                IStructuredSelection selection = (IStructuredSelection)columnsViewer.getSelection();
                for (Object obj : selection.toArray()) {
                    if (obj instanceof RelationalParameter) {
                        info = (RelationalParameter)obj;
                        break;
                    }
                }
                if (info != null) {
                    int selectedIndex = columnsViewer.getTable().getSelectionIndex();
                    viewProcedure.moveParameterDown(info);
                    handleParametersChanged();
                    columnsViewer.getTable().select(selectedIndex + 1);
                    downParameterButton.setEnabled(viewProcedure.canMoveParameterDown(info));
                    upParameterButton.setEnabled(viewProcedure.canMoveParameterUp(info));

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
        column.getColumn().setText(Messages.parameterNameLabel + "          "); //$NON-NLS-1$
        column.setEditingSupport(new ParameterNameEditingSupport(this.columnsViewer));
        column.setLabelProvider(new ParameterDataLabelProvider(0));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.dataTypeLabel + "          "); //$NON-NLS-1$
        column.setLabelProvider(new ParameterDataLabelProvider(1));
        column.setEditingSupport(new DatatypeEditingSupport(this.columnsViewer));
        column.getColumn().pack();

        column = new TableViewerColumn(this.columnsViewer, SWT.LEFT);
        column.getColumn().setText(Messages.lengthLabel);
        column.setLabelProvider(new ParameterDataLabelProvider(2));
        column.setEditingSupport(new ParameterWidthEditingSupport(this.columnsViewer));
        column.getColumn().pack();

        if (this.viewProcedure != null) {
            for (RelationalParameter row : this.viewProcedure.getParameters()) {
                this.columnsViewer.add(row);
            }
        }

        this.columnsViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                IStructuredSelection sel = (IStructuredSelection)event.getSelection();

                if (sel.isEmpty()) {
                    deleteParameterButton.setEnabled(false);
                    upParameterButton.setEnabled(false);
                    downParameterButton.setEnabled(false);
                } else {
                    boolean enable = true;
                    Object[] objs = sel.toArray();
                    RelationalParameter columnInfo = null;
                    for (Object obj : objs) {
                        if (!(obj instanceof RelationalParameter)) {
                            enable = false;
                            break;
                        }
                        columnInfo = (RelationalParameter)obj;
                    }
                    if (objs.length == 0) {
                        enable = false;
                    }
                    deleteParameterButton.setEnabled(enable);
                    if (enable) {
                        upParameterButton.setEnabled(viewProcedure.canMoveParameterUp(columnInfo));
                        downParameterButton.setEnabled(viewProcedure.canMoveParameterDown(columnInfo));
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
                                                                         SQLTemplateDialog.ALL_TEMPLATES);
                if (templateDialog.open() == Window.OK) {
                	String sql = templateDialog.getSQL();
                    viewProcedure.setTransformationSQL(sql);
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
				viewProcedure.setTransformationSQL(sqlTextViewer.getTextWidget().getText());
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
    
    void handleParametersChanged() {
        this.columnsViewer.getTable().removeAll();
        IStatus maxStatus = Status.OK_STATUS;
        for (RelationalParameter row : viewProcedure.getParameters()) {
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
        this.viewProcedure.validate();

        IStatus currentStatus = this.viewProcedure.getStatus();
        if (currentStatus.isOK()) {
            setStatus(Status.OK_STATUS);
        } else {
            setStatus(currentStatus);
        }

    }

    class ParameterDataLabelProvider extends ColumnLabelProvider {

        private final int columnNumber;

        public ParameterDataLabelProvider( int columnNumber ) {
            this.columnNumber = columnNumber;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            if (element instanceof RelationalParameter) {
                switch (this.columnNumber) {
                    case 0:
                        return ((RelationalParameter)element).getName();
                    case 1:
                        return ((RelationalParameter)element).getDatatype();
                    case 2:
                        return Integer.toString(((RelationalParameter)element).getLength());
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

    class ParameterNameEditingSupport extends EditingSupport {

        private TextCellEditor editor;

        /**
         * Create a new instance of the receiver.
         * 
         * @param viewer
         */
        public ParameterNameEditingSupport( ColumnViewer viewer ) {
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
            if (element instanceof RelationalParameter) {
                return ((RelationalParameter)element).getName();
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
            if (element instanceof RelationalParameter) {
                String oldValue = ((RelationalParameter)element).getName();
                String newValue = (String)value;
                if (newValue != null && newValue.length() > 0 && !newValue.equalsIgnoreCase(oldValue)) {
                    ((RelationalParameter)element).setName(newValue);
                    columnsViewer.refresh(element);
                    handleParametersChanged();
                }
            }
        }

    }

    class ParameterWidthEditingSupport extends EditingSupport {

        private TextCellEditor editor;

        /**
         * Create a new instance of the receiver.
         * 
         * @param viewer
         */
        public ParameterWidthEditingSupport( ColumnViewer viewer ) {
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
            if (element instanceof RelationalParameter) {
                return Integer.toString(((RelationalParameter)element).getLength());
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
            if (element instanceof RelationalParameter) {
                int oldValue = ((RelationalParameter)element).getLength();
                int newValue = oldValue;
                try {
                    newValue = Integer.parseInt((String)value);
                } catch (NumberFormatException ex) {
                    return;
                }
                if (newValue != oldValue) {
                    ((RelationalParameter)element).setLength(newValue);
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
            return ((RelationalParameter)element).getDatatype();
        }

        @Override
        protected String[] refreshItems( Object element ) {
            return datatypes;
        }

        @Override
        protected void setElementValue( Object element,
                                        String newValue ) {
            ((RelationalParameter)element).setDatatype(newValue);
        }
    }

}

