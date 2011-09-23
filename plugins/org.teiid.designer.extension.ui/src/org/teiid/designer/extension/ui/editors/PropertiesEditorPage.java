/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.EditorIds.MED_PROPERTIES_PAGE;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.CHECK_MARK;

import java.util.Arrays;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public class PropertiesEditorPage extends MedEditorPage {

    private Button btnAddMetaclass;
    private Button btnAddProperty;

    private Button btnEditProperty;
    private Button btnRemoveMetaclass;
    private Button btnRemoveProperty;

    private StructuredViewer metaclassViewer;
    private TableViewer propertyViewer;

    private final ErrorMessage metaclassError;
    private final ErrorMessage propertyError;

    public PropertiesEditorPage( FormEditor medEditor,
                                 ModelExtensionDefinition med ) {
        super(medEditor, MED_PROPERTIES_PAGE, Messages.medEditorPropertiesPageTitle, med);
        this.metaclassError = new ErrorMessage();
        this.propertyError = new ErrorMessage();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#createBody(org.eclipse.swt.widgets.Composite,
     *      org.eclipse.ui.forms.widgets.FormToolkit)
     */
    @Override
    protected void createBody( Composite body,
                               FormToolkit toolkit ) {
        body.setLayout(FormUtil.createFormGridLayout(false, 2));

        Composite left = toolkit.createComposite(body, SWT.NONE);
        left.setLayout(FormUtil.createFormPaneGridLayout(false, 1));
        left.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite right = toolkit.createComposite(body, SWT.NONE);
        right.setLayout(FormUtil.createFormPaneGridLayout(false, 1));
        right.setLayoutData(new GridData(GridData.FILL_BOTH));

        Section metaclassSection = createExtendedMetaclassSection(left, toolkit);
        Section propertiesSection = createPropertiesSection(right, toolkit);
        propertiesSection.descriptionVerticalSpacing = metaclassSection.getTextClientHeightDifference();

        // set error message controls
        this.metaclassError.widget = this.metaclassViewer.getControl();
        this.propertyError.widget = this.propertyViewer.getControl();

        // populate UI
        this.metaclassViewer.setInput(this);
    }

    private void configureColumn( TableViewerColumn viewerColumn,
                                  int columnIndex,
                                  String headerText,
                                  boolean resizable ) {
        viewerColumn.setLabelProvider(new PropertyLabelProvider(columnIndex));

        TableColumn column = viewerColumn.getColumn();
        column.setText(headerText);
        column.setMoveable(false);
        column.setResizable(resizable);
        column.pack();
    }

    private Section createExtendedMetaclassSection( Composite parent,
                                                    FormToolkit toolkit ) {
        Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
        section.setText(Messages.overviewPageExtendedMetaclassTitle);
        section.setDescription(Messages.overviewPageExtendedMetaclassDescription);
        section.setLayout(FormUtil.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite container = toolkit.createComposite(section);
        container.setLayout(FormUtil.createSectionClientGridLayout(false, 2));
        section.setClient(container);
        toolkit.paintBordersFor(container);

        // configure viewer
        this.metaclassViewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
        Control control = this.metaclassViewer.getControl();
        control.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.metaclassViewer.setContentProvider(new IStructuredContentProvider() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                return getModelExtensionDefinition().getExtendedMetaclasses();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {
                // nothing to do
            }
        });
        this.metaclassViewer.setLabelProvider(new LabelProvider());
        this.metaclassViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleMetaclassSelected();
            }
        });

        // configure buttons
        Button[] buttons = FormUtil.createButtonsContainer(container, toolkit, new String[] { Messages.addButton,
                Messages.removeButton });

        // configure add button
        this.btnAddMetaclass = buttons[0];
        this.btnAddMetaclass.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleAddMetaclass();
            }
        });
        this.btnAddMetaclass.setToolTipText(Messages.propertiesPageAddMetaclassButtonToolTip);

        // configure remove button
        this.btnRemoveMetaclass = buttons[1];
        this.btnRemoveMetaclass.setEnabled(false);
        this.btnRemoveMetaclass.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleRemoveMetaclass();
            }
        });
        this.btnRemoveMetaclass.setToolTipText(Messages.propertiesPageRemoveMetaclassButtonToolTip);

        return section;
    }

    private Section createPropertiesSection( Composite parent,
                                             FormToolkit toolkit ) {
        Section section = toolkit.createSection(parent, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
        section.setDescription(Messages.propertiesPageExtensionPropertiesDescription);
        section.setText(Messages.propertiesPageExtensionPropertiesTitle);
        section.setLayout(FormUtil.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite container = toolkit.createComposite(section);
        container.setLayout(FormUtil.createSectionClientGridLayout(false, 2));
        toolkit.paintBordersFor(container);
        section.setClient(container);

        // configure viewer
        this.propertyViewer = new TableViewer(container, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);
        final Table table = this.propertyViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayout(new TableLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        this.propertyViewer.setContentProvider(new IStructuredContentProvider() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#dispose()
             */
            @Override
            public void dispose() {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
             */
            @Override
            public Object[] getElements( Object inputElement ) {
                String metaclass = getSelectedMetaclass();

                if (CoreStringUtil.isEmpty(metaclass)) {
                    return new Object[0];
                }

                return getModelExtensionDefinition().getPropertyDefinitions(metaclass).toArray();
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
             *      java.lang.Object)
             */
            @Override
            public void inputChanged( Viewer viewer,
                                      Object oldInput,
                                      Object newInput ) {
                // nothing to do
            }
        });
        this.propertyViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handlePropertySelected();
            }
        });

        // create table columns
        TableViewerColumn column = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.SIMPLE_ID, ColumnHeaders.SIMPLE_ID, true);

        column = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.RUNTIME_TYPE, ColumnHeaders.RUNTIME_TYPE, true);

        column = new TableViewerColumn(this.propertyViewer, SWT.CENTER);
        configureColumn(column, ColumnIndexes.REQUIRED, ColumnHeaders.REQUIRED, false);

        column = new TableViewerColumn(this.propertyViewer, SWT.CENTER);
        configureColumn(column, ColumnIndexes.MODIFIABLE, ColumnHeaders.MODFIFIABLE, false);

        column = new TableViewerColumn(this.propertyViewer, SWT.CENTER);
        configureColumn(column, ColumnIndexes.ADVANCED, ColumnHeaders.ADVANCED, false);

        column = new TableViewerColumn(this.propertyViewer, SWT.CENTER);
        configureColumn(column, ColumnIndexes.MASKED, ColumnHeaders.MASKED, false);

        column = new TableViewerColumn(this.propertyViewer, SWT.CENTER);
        configureColumn(column, ColumnIndexes.INDEXED, ColumnHeaders.INDEXED, false);

        column = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.DEFAULT_VALUE, ColumnHeaders.DEFAULT_VALUE, true);

        column = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.ALLOWED_VALUES, ColumnHeaders.ALLOWED_VALUES, true);

        column = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
        configureColumn(column, ColumnIndexes.DISPLAY_NAME, ColumnHeaders.DISPLAY_NAME, true);

        final TableViewerColumn lastColumn = new TableViewerColumn(this.propertyViewer, SWT.LEFT);
        configureColumn(lastColumn, ColumnIndexes.DESCRIPTION, ColumnHeaders.DESCRIPTION, true);

        // size last column to stretch to the width of the table
        table.addControlListener(new ControlAdapter() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
             */
            @Override
            public void controlResized( ControlEvent e ) {
                lastColumn.getColumn().setWidth(table.getSize().x);
            }
        });

        // configure buttons
        Button[] buttons = FormUtil.createButtonsContainer(container, toolkit, new String[] { Messages.addButton,
                Messages.editButton, Messages.removeButton });

        // configure add button
        this.btnAddProperty = buttons[0];
        this.btnAddProperty.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleAddProperty();
            }
        });
        this.btnAddProperty.setToolTipText(Messages.propertiesPageAddPropertyButtonToolTip);

        // configure edit button
        this.btnEditProperty = buttons[1];
        this.btnEditProperty.setEnabled(false);
        this.btnEditProperty.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleEditProperty();
            }
        });
        this.btnEditProperty.setToolTipText(Messages.propertiesPageEditPropertyButtonToolTip);

        // configure remove button
        this.btnRemoveProperty = buttons[2];
        this.btnRemoveProperty.setEnabled(false);
        this.btnRemoveProperty.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
             */
            @Override
            public void widgetSelected( SelectionEvent e ) {
                handleRemoveProperty();
            }
        });
        this.btnRemoveProperty.setToolTipText(Messages.propertiesPageRemovePropertyButtonToolTip);

        return section;
    }

    String getSelectedMetaclass() {
        IStructuredSelection selection = (IStructuredSelection)this.metaclassViewer.getSelection();
        return (selection.isEmpty() ? null : (String)selection.getFirstElement());
    }

    ModelExtensionPropertyDefinition getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)this.propertyViewer.getSelection();
        return (selection.isEmpty() ? null : (ModelExtensionPropertyDefinition)selection.getFirstElement());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return Messages.medEditorPropertiesPageToolTip;
    }

    void handleAddMetaclass() {
        ModelExtensionDefinition med = getModelExtensionDefinition();
        AddMetaclassDialog dialog = new AddMetaclassDialog(null, Arrays.asList(med.getExtendedMetaclasses()));

        if (dialog.open() == Window.OK) {
            // TODO add to med
            // TODO add to metaclass table
            String metaclassName = dialog.getMetaclassName();
//            med.addPropertyDefinition(metaclassName, propDefn);
        }
    }

    void handleAddProperty() {
        // TODO implement handleAddProperty
    }

    void handleEditProperty() {
        // TODO implement handleEditProperty
    }

    void handleMetaclassSelected() {
        boolean enableRemove = (getSelectedMetaclass() != null);

        if (this.btnRemoveMetaclass.getEnabled() != enableRemove) {
            this.btnRemoveMetaclass.setEnabled(enableRemove);
        }

        // alert property viewer the selection changed
        this.propertyViewer.setInput(this);
        WidgetUtil.pack(this.propertyViewer);
    }

    void handlePropertySelected() {
        boolean enable = (getSelectedProperty() != null);

        if (this.btnRemoveProperty.getEnabled() != enable) {
            this.btnRemoveProperty.setEnabled(enable);
        }

        if (this.btnEditProperty.getEnabled() != enable) {
            this.btnEditProperty.setEnabled(enable);
        }
    }

    void handleRemoveMetaclass() {
        // TODO implement handleRemoveMetaclass
    }

    void handleRemoveProperty() {
        // TODO implement handleRemoveProperty
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.ui.editors.MedEditorPage#updateAllMessages()
     */
    @Override
    protected void updateAllMessages() {
        validateMetaclasses();
        validateProperties();
    }

    void validateMetaclasses() {
        String[] metaclassNames = getModelExtensionDefinition().getExtendedMetaclasses();
        this.metaclassError.message = ModelExtensionDefinitionValidator.validateMetaclassNames(Arrays.asList(metaclassNames));
        updateMessage(this.metaclassError);
    }

    void validateProperties() {
        // TODO implement validateMetaclasses
        updateMessage(this.propertyError);
    }

    interface ColumnHeaders {
        String ADVANCED = Messages.advancedPropertyAttributeColumnHeader;
        String ALLOWED_VALUES = Messages.allowedValuesPropertyAttributeColumnHeader;
        String DEFAULT_VALUE = Messages.defaultValuePropertyAttributeColumnHeader;
        String DESCRIPTION = Messages.descriptionPropertyAttributeColumnHeader;
        String DISPLAY_NAME = Messages.displayNamePropertyAttributeColumnHeader;
        String INDEXED = Messages.indexedPropertyAttributeColumnHeader;
        String MASKED = Messages.maskedPropertyAttributeColumnHeader;
        String MODFIFIABLE = Messages.modifiablePropertyAttributeColumnHeader;
        String REQUIRED = Messages.requiredPropertyAttributeColumnHeader;
        String RUNTIME_TYPE = Messages.runtimeTypePropertyAttributeColumnHeader;
        String SIMPLE_ID = Messages.simpleIdPropertyAttributeColumnHeader;
    }

    interface ColumnIndexes {
        int ADVANCED = 4;
        int ALLOWED_VALUES = 8;
        int DEFAULT_VALUE = 7;
        int DESCRIPTION = 10;
        int DISPLAY_NAME = 9;
        int INDEXED = 6;
        int MASKED = 5;
        int MODIFIABLE = 3;
        int REQUIRED = 2;
        int RUNTIME_TYPE = 1;
        int SIMPLE_ID = 0;
    }

    class PropertyLabelProvider extends ColumnLabelProvider {

        private final int columnIndex;

        public PropertyLabelProvider( final int columnIndex ) {
            this.columnIndex = columnIndex;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            ModelExtensionPropertyDefinition propDefn = (ModelExtensionPropertyDefinition)element;
            boolean enabled = false;

            if ((ColumnIndexes.ADVANCED == this.columnIndex) && propDefn.isAdvanced()) {
                enabled = true;
            } else if ((ColumnIndexes.INDEXED == this.columnIndex) && propDefn.shouldBeIndexed()) {
                enabled = true;
            } else if ((ColumnIndexes.MASKED == this.columnIndex) && propDefn.isMasked()) {
                enabled = true;
            } else if ((ColumnIndexes.MODIFIABLE == this.columnIndex) && propDefn.isModifiable()) {
                enabled = true;
            } else if ((ColumnIndexes.REQUIRED == this.columnIndex) && propDefn.isRequired()) {
                enabled = true;
            }

            return (enabled ? Activator.getDefault().getImage(CHECK_MARK) : null);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            ModelExtensionPropertyDefinition propDefn = (ModelExtensionPropertyDefinition)element;

            if (ColumnIndexes.SIMPLE_ID == this.columnIndex) {
                return propDefn.getSimpleId();
            }

            if (ColumnIndexes.DEFAULT_VALUE == this.columnIndex) {
                return propDefn.getDefaultValue();
            }

            if (ColumnIndexes.DESCRIPTION == this.columnIndex) {
                return propDefn.getDescription();
            }

            if (ColumnIndexes.RUNTIME_TYPE == this.columnIndex) {
                return propDefn.getRuntimeType();
            }

            if (ColumnIndexes.DISPLAY_NAME == this.columnIndex) {
                return propDefn.getDisplayName();
            }

            return null;
        }

    }
}
