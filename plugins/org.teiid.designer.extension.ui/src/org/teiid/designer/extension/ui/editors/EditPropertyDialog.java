/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;
import static org.teiid.designer.extension.ui.UiConstants.Form.COMBO_STYLE;
import static org.teiid.designer.extension.ui.UiConstants.Form.SECTION_STYLE;
import static org.teiid.designer.extension.ui.UiConstants.Form.TEXT_STYLE;
import static org.teiid.designer.extension.ui.UiConstants.Form.VIEWER_STYLE;
import static org.teiid.designer.extension.ui.UiConstants.ImageIds.MED_EDITOR;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.PropertyName;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Type;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinitionImpl;
import org.teiid.designer.extension.properties.Translation;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import org.teiid.designer.extension.ui.UiConstants.ImageIds;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * The <code>EditPropertyDialog</code> is used to create or edit a property definition.
 */
final class EditPropertyDialog extends FormDialog {

    private final String metaclassName;
    private final String namespacePrefix;

    private Button btnEditDescription;
    private Button btnEditDisplayName;
    private Button btnOk;
    private Button btnRemoveDescription;
    private Button btnRemoveDisplayName;

    private Button btnFixedValue;
    private Text txtFixedValue;
    private Button btnVariableValue;
    private Button btnAddValue;
    private Button btnEditValue;
    private Button btnRemoveValue;
    private Button btnToggleDefaultValue;

    private TableViewer descriptionViewer;
    private TableViewer displayNameViewer;
    private TableViewer valuesViewer;

    private final Collection<String> existingPropIds;

    private IManagedForm managedForm;

    /**
     * The property definition being edited or <code>null</code> when creating a new property definition.
     */
    private ModelExtensionPropertyDefinition propDefnBeingEdited;
    private ModelExtensionPropertyDefinitionImpl propDefn;

    private ScrolledForm scrolledForm;

    private final ErrorMessage advancedError;
    private final ErrorMessage allowedValuesError;
    private final ErrorMessage defaultValueError;
    private final ErrorMessage displayNameError;
    private final ErrorMessage descriptionError;
    private final ErrorMessage fixedValueError;
    private final ErrorMessage indexedError;
    private final ErrorMessage maskedError;
    private final ErrorMessage requiredError;
    private final ErrorMessage runtimeTypeError;
    private final ErrorMessage simpleIdError;
    private final ErrorMessage typeError;

    public EditPropertyDialog( Shell shell,
                               String namespacePrefix,
                               String metaclassName,
                               Collection<String> existingPropIds ) {
        super(shell);

        CoreArgCheck.isNotNull(metaclassName, "metaclassName is null"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(namespacePrefix, "namespacePrefix is null"); //$NON-NLS-1$
        this.metaclassName = metaclassName;
        this.namespacePrefix = namespacePrefix;

        this.existingPropIds = new ArrayList<String>(existingPropIds);
        this.propDefn = new ModelExtensionPropertyDefinitionImpl();

        // errors
        this.advancedError = new ErrorMessage();
        this.allowedValuesError = new ErrorMessage();
        this.defaultValueError = new ErrorMessage();
        this.displayNameError = new ErrorMessage();
        this.descriptionError = new ErrorMessage();
        this.fixedValueError = new ErrorMessage();
        this.indexedError = new ErrorMessage();
        this.maskedError = new ErrorMessage();
        this.requiredError = new ErrorMessage();
        this.simpleIdError = new ErrorMessage();
        this.runtimeTypeError = new ErrorMessage();
        this.typeError = new ErrorMessage();
    }

    public EditPropertyDialog( Shell shell,
                               String namespacePrefix,
                               String metaclassName,
                               Collection<String> existingPropIds,
                               ModelExtensionPropertyDefinition propDefnBeingEdited ) {
        this(shell, namespacePrefix, metaclassName, existingPropIds);
        this.propDefnBeingEdited = propDefnBeingEdited;

        if (this.propDefnBeingEdited != null) {
            this.propDefnBeingEdited = propDefnBeingEdited;

            // remove the property definition being edited
            this.existingPropIds.remove(this.propDefnBeingEdited.getSimpleId());

            this.propDefn.setAdvanced(this.propDefnBeingEdited.isAdvanced());
            this.propDefn.setAllowedValues(this.propDefnBeingEdited.allowedValues());
            this.propDefn.setDefaultValue(this.propDefnBeingEdited.getDefaultValue());
            this.propDefn.setFixedValue(this.propDefnBeingEdited.getFixedValue());
            this.propDefn.setIndex(this.propDefnBeingEdited.shouldBeIndexed());
            this.propDefn.setMasked(this.propDefnBeingEdited.isMasked());
            this.propDefn.setNamespacePrefix(this.propDefnBeingEdited.getNamespacePrefix());
            this.propDefn.setRequired(this.propDefnBeingEdited.isRequired());
            this.propDefn.setSimpleId(this.propDefnBeingEdited.getSimpleId());
            this.propDefn.setType(this.propDefnBeingEdited.getType());

            if (!this.propDefnBeingEdited.getDescriptions().isEmpty()) {
                for (Translation description : this.propDefnBeingEdited.getDescriptions()) {
                    this.propDefn.addDescription(Translation.copy(description));
                }
            }

            if (!this.propDefnBeingEdited.getDisplayNames().isEmpty()) {
                for (Translation displayName : this.propDefnBeingEdited.getDisplayNames()) {
                    this.propDefn.addDisplayName(Translation.copy(displayName));
                }
            }
        }
    }

    private void addMessage( ErrorMessage errorMsg,
                             String newMsg ) {
        String oldMessage = errorMsg.message;

        if (!CoreStringUtil.equals(oldMessage, newMsg)) {
            errorMsg.message = newMsg;
            this.scrolledForm.getMessageManager().addMessage(errorMsg.getKey(), errorMsg.getMessage(), null,
                                                             errorMsg.getMessageType(), errorMsg.getControl());
        }
    }

    private void configureColumn( TableViewerColumn viewerColumn,
                                  String headerText,
                                  String headerToolTip,
                                  boolean resizable ) {
        TableColumn column = viewerColumn.getColumn();
        column.setText(headerText);
        column.setToolTipText(headerToolTip);
        column.setMoveable(false);
        column.setResizable(resizable);
        column.pack();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell(newShell);

        if (isEditMode()) {
            newShell.setText(Messages.editPropertyDialogTitle);
        } else {
            newShell.setText(Messages.addPropertyDialogTitle);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String, boolean)
     */
    @Override
    protected Button createButton( Composite parent,
                                   int id,
                                   String label,
                                   boolean defaultButton ) {
        Button btn = super.createButton(parent, id, label, defaultButton);

        if (id == IDialogConstants.OK_ID) {
            // disable OK button initially
            this.btnOk = btn;
            btn.setEnabled(false);
        }

        return btn;
    }

    @SuppressWarnings("unused")
    private Section createDescriptionSection( Composite body,
                                              FormToolkit toolkit ) {
        final Section finalSection;

        SECTION: {
            Section section = FormUtil.createSection(this.managedForm, toolkit, body,
                                                     Messages.editPropertyDialogDescriptionSectionTitle,
                                                     Messages.editPropertyDialogDescriptionSectionDescription, SECTION_STYLE, true);
            finalSection = section;

            // configure section toolbar
            Button[] buttons = FormUtil.createSectionToolBar(finalSection, toolkit,
                                                             new Image[] {
                                                                     Activator.getDefault().getImage(ImageIds.ADD_DESCRIPTION),
                                                                     Activator.getDefault().getImage(ImageIds.EDIT_DESCRIPTION),
                                                                     Activator.getDefault().getImage(ImageIds.REMOVE_DESCRIPTION) });

            // configure add button
            buttons[0].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleAddDescription();
                }
            });
            buttons[0].setToolTipText(Messages.editPropertyDialogAddDescriptionButtonToolTip);

            // configure edit button
            buttons[1].setEnabled(false);
            buttons[1].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleEditDescription();
                }
            });
            buttons[1].setToolTipText(Messages.editPropertyDialogEditDescriptionButtonToolTip);
            this.btnEditDescription = buttons[1];

            // configure remove button
            buttons[2].setEnabled(false);
            buttons[2].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleRemoveDescription();
                }
            });
            buttons[2].setToolTipText(Messages.editPropertyDialogRemoveDescriptionButtonToolTip);
            this.btnRemoveDescription = buttons[2];
        }

        final Composite finalContainer;

        CONTAINER: {
            Composite container = toolkit.createComposite(finalSection);
            finalContainer = container;
            container.setLayout(new GridLayout());
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            finalSection.setClient(container);
        }

        VIEWER: {
            Table table = toolkit.createTable(finalContainer, VIEWER_STYLE);
            this.descriptionError.widget = table;
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new GridLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 5;

            this.descriptionViewer = new TableViewer(table);
            this.descriptionViewer.setContentProvider(new IStructuredContentProvider() {

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
                    return getPropertyDefinition().getDescriptions().toArray();
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
            this.descriptionViewer.setLabelProvider(new LabelProvider());
            this.descriptionViewer.addDoubleClickListener(new IDoubleClickListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
                 */
                @Override
                public void doubleClick( DoubleClickEvent event ) {
                    handleEditDescription();
                }
            });
            this.descriptionViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                 */
                @Override
                public void selectionChanged( SelectionChangedEvent event ) {
                    handleDescriptionSelected();
                }
            });

            COLUMNS: {
                final TableViewerColumn firstColumn = new TableViewerColumn(this.descriptionViewer, SWT.LEFT);
                configureColumn(firstColumn, ColumnHeaders.LOCALE, ColumnToolTips.LOCALE, true);
                firstColumn.setLabelProvider(new TranslationLabelProvider(ColumnIndexes.LOCALE));

                final TableViewerColumn lastColumn = new TableViewerColumn(this.descriptionViewer, SWT.LEFT);
                lastColumn.setLabelProvider(new TranslationLabelProvider(ColumnIndexes.TRANSLATION));
                configureColumn(lastColumn, ColumnHeaders.TRANSLATION, ColumnToolTips.TRANSLATION, true);

                // size last column to extend to width of table
                finalContainer.addControlListener(new ControlAdapter() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                     */
                    @Override
                    public void controlResized( ControlEvent e ) {
                        lastColumn.getColumn().setWidth(finalContainer.getClientArea().width - firstColumn.getColumn().getWidth());
                    }
                });
            }

            // populate table
            this.descriptionViewer.setInput(this);
            WidgetUtil.pack(this.descriptionViewer);
        }

        return finalSection;
    }

    @SuppressWarnings("unused")
    private Section createDisplayNameSection( Composite body,
                                              FormToolkit toolkit ) {
        final Section finalSection;

        SECTION: {
            Section section = FormUtil.createSection(this.managedForm, toolkit, body,
                                                     Messages.editPropertyDialogDisplayNameSectionTitle,
                                                     Messages.editPropertyDialogDisplayNameSectionDescription, SECTION_STYLE, true);
            finalSection = section;

            // configure section toolbar
            Button[] buttons = FormUtil.createSectionToolBar(finalSection,
                                                             toolkit,
                                                             new Image[] {
                                                                     Activator.getDefault().getImage(ImageIds.ADD_DISPLAY_NAME),
                                                                     Activator.getDefault().getImage(ImageIds.EDIT_DISPLAY_NAME),
                                                                     Activator.getDefault().getImage(ImageIds.REMOVE_DISPLAY_NAME) });

            // configure add button
            buttons[0].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleAddDisplayName();
                }
            });
            buttons[0].setToolTipText(Messages.editPropertyDialogAddDisplayNameButtonToolTip);

            // configure edit button
            buttons[1].setEnabled(false);
            buttons[1].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleEditDisplayName();
                }
            });
            buttons[1].setToolTipText(Messages.editPropertyDialogEditDisplayNameButtonToolTip);
            this.btnEditDisplayName = buttons[1];

            // configure remove button
            buttons[2].setEnabled(false);
            buttons[2].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleRemoveDisplayName();
                }
            });
            buttons[2].setToolTipText(Messages.editPropertyDialogRemoveDisplayNameButtonToolTip);
            this.btnRemoveDisplayName = buttons[2];
        }

        final Composite finalContainer;

        CONTAINER: {
            Composite container = toolkit.createComposite(finalSection);
            finalContainer = container;
            container.setLayout(new GridLayout());
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            finalSection.setClient(container);
        }

        VIEWER: {
            Table table = toolkit.createTable(finalContainer, VIEWER_STYLE);
            this.displayNameError.widget = table;
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new GridLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 5;

            this.displayNameViewer = new TableViewer(table);
            this.displayNameViewer.setContentProvider(new IStructuredContentProvider() {

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
                    return getPropertyDefinition().getDisplayNames().toArray();
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
            this.displayNameViewer.setLabelProvider(new LabelProvider());
            this.displayNameViewer.addDoubleClickListener(new IDoubleClickListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
                 */
                @Override
                public void doubleClick( DoubleClickEvent event ) {
                    handleEditDisplayName();
                }
            });
            this.displayNameViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                 */
                @Override
                public void selectionChanged( SelectionChangedEvent event ) {
                    handleDisplayNameSelected();
                }
            });

            COLUMNS: {
                final TableViewerColumn firstColumn = new TableViewerColumn(this.displayNameViewer, SWT.LEFT);
                firstColumn.setLabelProvider(new TranslationLabelProvider(ColumnIndexes.LOCALE));
                configureColumn(firstColumn, ColumnHeaders.LOCALE, ColumnToolTips.LOCALE, true);

                final TableViewerColumn lastColumn = new TableViewerColumn(this.displayNameViewer, SWT.LEFT);
                lastColumn.setLabelProvider(new TranslationLabelProvider(ColumnIndexes.TRANSLATION));
                configureColumn(lastColumn, ColumnHeaders.TRANSLATION, ColumnToolTips.TRANSLATION, true);

                // size last column to extend to width of table
                finalContainer.addControlListener(new ControlAdapter() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                     */
                    @Override
                    public void controlResized( ControlEvent e ) {
                        lastColumn.getColumn().setWidth(finalContainer.getClientArea().width - firstColumn.getColumn().getWidth());
                    }
                });
            }

            // populate table
            this.displayNameViewer.setInput(this);
            WidgetUtil.pack(this.displayNameViewer);
        }

        return finalSection;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.FormDialog#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @SuppressWarnings("unused")
    @Override
    protected void createFormContent( IManagedForm managedForm ) {
        this.managedForm = managedForm;

        FORM: {
            this.scrolledForm = managedForm.getForm();
            this.scrolledForm.setText(Messages.propertyDialogTitle);
            this.scrolledForm.setImage(Activator.getDefault().getImage(ImageIds.MED_EDITOR));
            this.scrolledForm.setMessage(Messages.propertyDialogMessage, IMessageProvider.NONE);
        }

        FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(this.scrolledForm.getForm());

        final Composite finalBody;

        BODY: {
            Composite body = this.scrolledForm.getBody();
            finalBody = body;
            body.setLayout(new GridLayout(2, true));
            body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        }

        SECTIONS: {
            final Section infoSection = createInfoSection(finalBody, toolkit);
            final Section propertyValueSection = createPropertyValueSection(finalBody, toolkit);
            final Section descriptionSection = createDescriptionSection(finalBody, toolkit);
            final Section displayNameSection = createDisplayNameSection(finalBody, toolkit);

            infoSection.descriptionVerticalSpacing = propertyValueSection.getTextClientHeightDifference();
            descriptionSection.descriptionVerticalSpacing = displayNameSection.getTextClientHeightDifference();
        }

        // must be done after constructor
        this.propDefn.addListener(new PropertyChangeListener() {

            /**
             * {@inheritDoc}
             * 
             * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
             */
            @Override
            public void propertyChange( PropertyChangeEvent e ) {
                handlePropertyChanged(e);
            }
        });
    }

    @SuppressWarnings("unused")
    private Section createInfoSection( Composite body,
                                       FormToolkit toolkit ) {
        final Section finalSection;

        SECTION: {
            Section section = FormUtil.createSection(this.managedForm, toolkit, body, Messages.editPropertyDialogInfoSectionTitle,
                                                     Messages.editPropertyDialogInfoSectionDescription, SECTION_STYLE, true);
            finalSection = section;
        }

        final Composite finalContainer;

        CONTAINER: {
            Composite container = toolkit.createComposite(finalSection);
            container.setLayout(new GridLayout(2, false));
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            finalSection.setClient(container);
            finalContainer = container;
        }

        METACLASS: {
            toolkit.createLabel(finalContainer, Messages.metaclassLabel);

            Label label = toolkit.createLabel(finalContainer, this.metaclassName);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }

        NAMESPACE_PREFIX: {
            toolkit.createLabel(finalContainer, Messages.namespacePrefixLabel);

            Label label = toolkit.createLabel(finalContainer, this.namespacePrefix);
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }

        final Text finalTxtSimpleId;

        ID: {
            toolkit.createLabel(finalContainer, Messages.simpleIdLabel);

            Text txtSimpleId = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            finalTxtSimpleId = txtSimpleId;
            this.simpleIdError.widget = txtSimpleId;
            txtSimpleId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            txtSimpleId.setFocus();
            txtSimpleId.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleSimpleIdChanged(((Text)e.widget).getText());
                }
            });

            if (isEditMode()) {
                txtSimpleId.setText(this.propDefn.getSimpleId());
            }
        }

        RUNTIME_TYPE: {
            toolkit.createLabel(finalContainer, Messages.runtimeTypeLabel);

            CCombo cbxRuntimeType = new CCombo(finalContainer, COMBO_STYLE);
            toolkit.adapt(cbxRuntimeType, true, false);
            this.runtimeTypeError.widget = cbxRuntimeType;
            cbxRuntimeType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            // populate runtime types
            for (Type type : Type.values()) {
                cbxRuntimeType.add(type.getRuntimeType());
            }

            // set selection based on property definition
            if (isEditMode()) {
                String runtimeType = this.propDefn.getRuntimeType();

                if (!CoreStringUtil.isEmpty(runtimeType)) {
                    int index = cbxRuntimeType.indexOf(runtimeType);

                    if (index == -1) {
                        UTIL.log(NLS.bind(Messages.editPropertyDialogInvalidRuntimeTypeMsg, runtimeType));
                    } else {
                        cbxRuntimeType.select(index);
                    }
                }
            }

            cbxRuntimeType.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleRuntimeTypeChanged(((CCombo)e.widget).getText());
                }
            });

            cbxRuntimeType.addControlListener(new ControlAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                 */
                @Override
                public void controlResized( ControlEvent e ) {
                    ((CCombo)e.widget).setSize(finalTxtSimpleId.getSize());
                }
            });
        }

        FLAGS: {
            Section section = toolkit.createSection(finalContainer, ExpandableComposite.TITLE_BAR);
            section.setText(Messages.editPropertyDialogFlagsSectionTitle);
            section.setLayout(new GridLayout());
            section.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            ((GridData)section.getLayoutData()).horizontalSpan = 2;
            ((GridData)section.getLayoutData()).verticalIndent += ((GridLayout)finalContainer.getLayout()).verticalSpacing;

            Composite container = toolkit.createComposite(section, SWT.NONE);
            container.setLayout(new GridLayout(5, true));
            container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            section.setClient(container);

            Button btn = toolkit.createButton(container, Messages.requiredPropertyAttributeColumnHeader, SWT.CHECK);
            this.requiredError.widget = btn;
            btn.setSelection(this.propDefn.isRequired());
            btn.setToolTipText(Messages.requiredPropertyAttributeColumnHeaderToolTip);
            btn.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleRequiredChanged(((Button)e.widget).getSelection());
                }
            });

            btn = toolkit.createButton(container, Messages.advancedPropertyAttributeColumnHeader, SWT.CHECK);
            this.advancedError.widget = btn;
            btn.setSelection(this.propDefn.isAdvanced());
            btn.setToolTipText(Messages.advancedPropertyAttributeColumnHeaderToolTip);
            btn.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleAdvancedChanged(((Button)e.widget).getSelection());
                }
            });

            btn = toolkit.createButton(container, Messages.maskedPropertyAttributeColumnHeader, SWT.CHECK);
            this.maskedError.widget = btn;
            btn.setSelection(this.propDefn.isMasked());
            btn.setToolTipText(Messages.maskedPropertyAttributeColumnHeaderToolTip);
            btn.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleMaskedChanged(((Button)e.widget).getSelection());
                }
            });

            btn = toolkit.createButton(container, Messages.indexedPropertyAttributeColumnHeader, SWT.CHECK);
            this.indexedError.widget = btn;
            btn.setSelection(this.propDefn.shouldBeIndexed());
            btn.setToolTipText(Messages.indexedPropertyAttributeColumnHeaderToolTip);
            btn.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleIndexedChanged(((Button)e.widget).getSelection());
                }
            });
        }

        return finalSection;
    }

    @SuppressWarnings("unused")
    private Section createPropertyValueSection( Composite body,
                                                FormToolkit toolkit ) {
        // TODO implement createPropertyValueSection
        // TODO assign error widgets here

        Section section = FormUtil.createSection(this.managedForm, toolkit, body,
                                                 Messages.editPropertyDialogPropertyValueSectionTitle,
                                                 Messages.editPropertyDialogPropertyValueSectionDescription, SECTION_STYLE, true);

        Composite container = toolkit.createComposite(section);
        container.setLayout(new GridLayout());
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        section.setClient(container);

        TOP: {
            Composite top = toolkit.createComposite(container);
            top.setLayout(new GridLayout(2, false));
            top.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            this.btnFixedValue = toolkit.createButton(top, Messages.editPropertyDialogFixedValueButtonText, SWT.RADIO);
            this.btnFixedValue.setToolTipText(Messages.editPropertyDialogFixedValueButtonToolTip);
            this.btnFixedValue.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleFixedValueSelected();
                }
            });

            this.txtFixedValue = toolkit.createText(top, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            this.fixedValueError.widget = this.txtFixedValue;
            this.txtFixedValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            this.txtFixedValue.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleFixedValueChanged(((Text)e.widget).getText());
                }
            });

            this.btnVariableValue = toolkit.createButton(top, Messages.editPropertyDialogVariableValueButtonText, SWT.RADIO);
            this.btnVariableValue.setToolTipText(Messages.editPropertyDialogVariableValueButtonToolTip);
            this.btnVariableValue.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleVariableValueSelected();
                }
            });

            if (this.propDefn.isModifiable()) {
                this.btnVariableValue.setSelection(true);
            } else {
                this.btnFixedValue.setSelection(true);
            }
        }

        BOTTOM: {
            Section valuesSection = toolkit.createSection(container, ExpandableComposite.TITLE_BAR
                    | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT);
            valuesSection.setText(Messages.editPropertyDialogAllowedValuesSectionTitle);
            valuesSection.setLayout(new GridLayout());
            valuesSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            Button[] buttons = FormUtil.createSectionToolBar(valuesSection,
                                                             toolkit,
                                                             new Image[] { Activator.getDefault().getImage(ImageIds.ADD_VALUE),
                                                                     Activator.getDefault().getImage(ImageIds.EDIT_VALUE),
                                                                     Activator.getDefault().getImage(ImageIds.REMOVE_VALUE),
                                                                     Activator.getDefault().getImage(ImageIds.TOGGLE_DEFAULT_VALUE) });

            // configure add allowed value button
            buttons[0].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleAddAllowedValue();
                }
            });
            buttons[0].setToolTipText(Messages.editPropertyDialogAddAllowedValueButtonToolTip);

            // configure edit allowed value button
            buttons[1].setEnabled(false);
            buttons[1].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleEditAllowedValue();
                }
            });
            buttons[1].setToolTipText(Messages.editPropertyDialogEditAllowedValueButtonToolTip);
            this.btnEditValue = buttons[1];

            // configure remove allowed value button
            buttons[2].setEnabled(false);
            buttons[2].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleRemoveAllowedValue();
                }
            });
            buttons[2].setToolTipText(Messages.editPropertyDialogRemoveAllowedValueButtonToolTip);
            this.btnRemoveValue = buttons[2];

            // configure remove allowed value button
            buttons[3].setEnabled(false);
            buttons[3].addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleToggleDefaultValue();
                }
            });
            buttons[3].setToolTipText(Messages.editPropertyDialogToggleDefaultValueButtonToolTip);
            this.btnToggleDefaultValue = buttons[3];

            final Composite bottom = toolkit.createComposite(valuesSection, SWT.NONE);
            bottom.setLayout(new GridLayout());
            bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            valuesSection.setClient(bottom);

            VIEWER: {
                Table table = toolkit.createTable(bottom, VIEWER_STYLE);
                this.allowedValuesError.widget = table;
                table.setHeaderVisible(true);
                table.setLinesVisible(true);
                table.setLayout(new GridLayout());
                table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 5;

                this.valuesViewer = new TableViewer(table);
                this.valuesViewer.setContentProvider(new IStructuredContentProvider() {

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
                        return new Object[0]; // TODO implement
                    }

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
                     *      java.lang.Object, java.lang.Object)
                     */
                    @Override
                    public void inputChanged( Viewer viewer,
                                              Object oldInput,
                                              Object newInput ) {
                        // nothing to do
                    }
                });
                this.valuesViewer.setLabelProvider(new LabelProvider());
                this.valuesViewer.addDoubleClickListener(new IDoubleClickListener() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
                     */
                    @Override
                    public void doubleClick( DoubleClickEvent event ) {
                        handleEditAllowedValue();
                    }
                });
                this.valuesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
                     */
                    @Override
                    public void selectionChanged( SelectionChangedEvent event ) {
                        handleAllowedValueSelected();
                    }
                });

                COLUMNS: {
                    final TableViewerColumn firstColumn = new TableViewerColumn(this.valuesViewer, SWT.LEFT);
                    firstColumn.setLabelProvider(new AllowedValueLabelProvider(ColumnIndexes.DEFAULT_VALUE));
                    configureColumn(firstColumn, ColumnHeaders.DEFAULT_VALUE, ColumnToolTips.DEFAULT_VALUE, true);

                    final TableViewerColumn lastColumn = new TableViewerColumn(this.valuesViewer, SWT.LEFT);
                    lastColumn.setLabelProvider(new AllowedValueLabelProvider(ColumnIndexes.VALUE));
                    configureColumn(lastColumn, ColumnHeaders.VALUE, ColumnToolTips.VALUE, true);

                    // size last column to extend to width of table
                    bottom.addControlListener(new ControlAdapter() {

                        /**
                         * {@inheritDoc}
                         * 
                         * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                         */
                        @Override
                        public void controlResized( ControlEvent e ) {
                            lastColumn.getColumn().setWidth(bottom.getClientArea().width - firstColumn.getColumn().getWidth());
                        }
                    });
                }

                // populate table
                this.valuesViewer.setInput(this);
                WidgetUtil.pack(this.valuesViewer);
            }
        }

        return section;
    }

    private Set<Locale> getLocales( Set<Translation> translations ) {
        assert (translations != null) : "translations is null"; //$NON-NLS-1$
        Set<Locale> locales = new HashSet<Locale>(translations.size());

        for (Translation translation : translations) {
            locales.add(translation.getLocale());
        }

        return locales;
    }

    ModelExtensionPropertyDefinition getPropertyDefinition() {
        return this.propDefn;
    }

    String getSelectedAllowedValue() {
        IStructuredSelection selection = (IStructuredSelection)this.valuesViewer.getSelection();
        return (selection.isEmpty() ? null : (String)selection.getFirstElement());
    }

    Translation getSelectedDescription() {
        IStructuredSelection selection = (IStructuredSelection)this.descriptionViewer.getSelection();
        return (selection.isEmpty() ? null : (Translation)selection.getFirstElement());
    }

    Translation getSelectedDisplayName() {
        IStructuredSelection selection = (IStructuredSelection)this.displayNameViewer.getSelection();
        return (selection.isEmpty() ? null : (Translation)selection.getFirstElement());
    }

    void handleAddAllowedValue() {
        EditAllowedValueDialog dialog = new EditAllowedValueDialog(getShell(),
                                                                   this.propDefn.getType(),
                                                                   this.propDefn.getAllowedValues());
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            String errorMsg = null;
            String newAllowedValue = dialog.getAllowedValue();

            if (this.propDefn.addAllowedValue(newAllowedValue)) {
                // validate new allowed value
                errorMsg = ModelExtensionDefinitionValidator.validatePropertyAllowedValue(this.propDefn.getRuntimeType(),
                                                                                          newAllowedValue);

                // validate allowed value collection
                if (CoreStringUtil.isEmpty(errorMsg)) {
                    errorMsg = ModelExtensionDefinitionValidator.validatePropertyAllowedValues(this.propDefn.getRuntimeType(),
                                                                                               this.propDefn.getAllowedValues());
                }
            } else {
                // TODO value not added msg
            }

            addMessage(this.allowedValuesError, errorMsg);
        }
    }

    void handleAddDescription() {
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.addPropertyDescriptionDialogTitle,
                                                                 new ArrayList<Locale>());
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            String errorMsg = null;
            Translation newDescription = dialog.getTranslation();

            if (this.propDefn.addDescription(newDescription)) {
                errorMsg = ModelExtensionDefinitionValidator.validateTranslationLocale(newDescription.getLocale(),
                                                                                       getLocales(this.propDefn.getDescriptions()));

                if (CoreStringUtil.isEmpty(errorMsg)) {
                    errorMsg = ModelExtensionDefinitionValidator.validateTranslationText(newDescription.getTranslation());
                }
            } else {
                // shouldn't happen if the EditTranslationDialog is working correctly
                UTIL.log(NLS.bind(Messages.editPropertyDialogDescriptionNotAddedMsg, newDescription));
            }

            addMessage(this.descriptionError, errorMsg);
        }
    }

    void handleAddDisplayName() {
        String errorMsg = null;
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.addPropertyDisplayNameDialogTitle,
                                                                 new ArrayList<Locale>());
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            Translation newDisplayName = dialog.getTranslation();

            if (this.propDefn.addDisplayName(newDisplayName)) {
                errorMsg = ModelExtensionDefinitionValidator.validateTranslationLocale(newDisplayName.getLocale(),
                                                                                       getLocales(this.propDefn.getDisplayNames()));

                if (CoreStringUtil.isEmpty(errorMsg)) {
                    errorMsg = ModelExtensionDefinitionValidator.validateTranslationText(newDisplayName.getTranslation());
                }
            } else {
                // shouldn't happen if the EditTranslationDialog is working correctly
                UTIL.log(NLS.bind(Messages.editPropertyDialogDisplayNameNotAddedMsg, newDisplayName));
            }

            addMessage(this.displayNameError, errorMsg);
        }
    }

    void handleAdvancedChanged( boolean newValue ) {
        this.propDefn.setAdvanced(newValue);
    }

    void handleAllowedValueSelected() {
        IStructuredSelection selection = (IStructuredSelection)this.valuesViewer.getSelection();
        boolean enable = !selection.isEmpty();

        if (this.btnEditValue.getEnabled() != enable) {
            this.btnEditValue.setEnabled(enable);
        }

        if (this.btnRemoveValue.getEnabled() != enable) {
            this.btnRemoveValue.setEnabled(enable);
        }

        if (this.btnToggleDefaultValue.getEnabled() != enable) {
            this.btnToggleDefaultValue.setEnabled(enable);
        }

    }

    void handleDescriptionSelected() {
        IStructuredSelection selection = (IStructuredSelection)this.descriptionViewer.getSelection();
        boolean enable = !selection.isEmpty();

        if (this.btnEditDescription.getEnabled() != enable) {
            this.btnEditDescription.setEnabled(enable);
        }

        if (this.btnRemoveDescription.getEnabled() != enable) {
            this.btnRemoveDescription.setEnabled(enable);
        }
    }

    void handleDisplayNameSelected() {
        IStructuredSelection selection = (IStructuredSelection)this.descriptionViewer.getSelection();
        boolean enable = !selection.isEmpty();

        if (this.btnEditDisplayName.getEnabled() != enable) {
            this.btnEditDisplayName.setEnabled(enable);
        }

        if (this.btnRemoveDisplayName.getEnabled() != enable) {
            this.btnRemoveDisplayName.setEnabled(enable);
        }
    }

    void handleEditAllowedValue() {
        String selectedAllowedValue = getSelectedAllowedValue();
        assert (selectedAllowedValue != null) : "Edit allowed value button is enabled and shouldn't be"; //$NON-NLS-1$
        EditAllowedValueDialog dialog = new EditAllowedValueDialog(getShell(),
                                                                   this.propDefn.getType(),
                                                                   this.propDefn.getAllowedValues(),
                                                                   selectedAllowedValue);
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            String changedValue = dialog.getAllowedValue();
            String[] currentValues = this.propDefn.getAllowedValues();
            int i = 0;

            for (String allowedValue : currentValues) {
                if (allowedValue.equals(selectedAllowedValue)) {
                    currentValues[i] = changedValue;
                    break;
                }

                ++i;
            }

            String errorMsg = ModelExtensionDefinitionValidator.validatePropertyAllowedValues(this.propDefn.getRuntimeType(),
                                                                                              this.propDefn.getAllowedValues());
            addMessage(this.allowedValuesError, errorMsg);
        }
    }

    void handleEditDescription() {
        assert (getSelectedDescription() != null) : "Edit description button is enabled and shouldn't be"; //$NON-NLS-1$
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.editPropertyDescriptionDialogTitle,
                                                                 new ArrayList<Locale>(),
                                                                 getSelectedDescription());
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            Translation changedDescription = dialog.getTranslation();
            // TODO implement handleEditDescription
        }
    }

    void handleEditDisplayName() {
        assert (getSelectedDisplayName() != null) : "Edit display name button is enabled and shouldn't be"; //$NON-NLS-1$
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.editPropertyDescriptionDialogTitle,
                                                                 new ArrayList<Locale>(),
                                                                 getSelectedDisplayName());
        dialog.create();
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            Translation changedDisplayName = dialog.getTranslation();
            // TODO implement handleEditDisplayName
        }
    }

    void handleFixedValueChanged( String newValue ) {
        this.propDefn.setFixedValue(newValue);
    }

    void handleFixedValueSelected() {
        boolean enable = this.btnFixedValue.getSelection();

        if (this.txtFixedValue.getEnabled() != enable) {
            this.txtFixedValue.setEnabled(enable);

            handleFixedValueChanged(enable ? this.txtFixedValue.getText() : CoreStringUtil.Constants.EMPTY_STRING);
        }
    }

    void handleIndexedChanged( boolean newValue ) {
        this.propDefn.setIndex(newValue);
    }

    void handleMaskedChanged( boolean newValue ) {
        this.propDefn.setMasked(newValue);
    }

    void handlePropertyChanged( PropertyChangeEvent e ) {
        String propName = e.getPropertyName();
        String newMsg = null;
        ErrorMessage errorMsg = null;

        if (PropertyName.ADVANCED.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertyAdvancedAttribute(this.propDefn.isAdvanced());
            errorMsg = this.advancedError;
        } else if (PropertyName.ALLOWED_VALUES.toString().equals(propName)) {
            // TODO implement
        } else if (PropertyName.DEFAULT_VALUE.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertyDefaultValue(this.propDefn.getRuntimeType(),
                                                                                    this.propDefn.getDefaultValue(),
                                                                                    this.propDefn.getAllowedValues());
            errorMsg = this.defaultValueError;
            // TODO implement this
            // } else if (PropertyName.DESCRIPTION.equals(propName)) {
            // this.descriptionError.message =
            // ModelExtensionDefinitionValidator.validateTranslation(this.propDefn.getDescription());
            // errorMsg = this.descriptionError;
            // } else if (PropertyName.DISPLAY_NAME.equals(propName)) {
            // this.displayNameError.message =
            // ModelExtensionDefinitionValidator.validateTranslation(this.propDefn.getDisplayName());
            // errorMsg = this.displayNameError;
        } else if (PropertyName.FIXED_VALUE.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertyFixedValue(this.propDefn.getRuntimeType(),
                                                                                  this.propDefn.getFixedValue());
            errorMsg = this.fixedValueError;
        } else if (PropertyName.INDEX.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertyIndexedAttribute(this.propDefn.shouldBeIndexed());
            errorMsg = this.indexedError;
        } else if (PropertyName.MASKED.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertyMaskedAttribute(this.propDefn.isMasked());
            errorMsg = this.maskedError;
        } else if (PropertyName.REQUIRED.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertyRequiredAttribute(this.propDefn.isRequired());
            errorMsg = this.requiredError;
        } else if (PropertyName.SIMPLE_ID.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertySimpleId(this.propDefn.getSimpleId(), this.existingPropIds);
            errorMsg = this.simpleIdError;
        } else if (PropertyName.TYPE.toString().equals(propName)) {
            newMsg = ModelExtensionDefinitionValidator.validatePropertyRuntimeType(this.propDefn.getRuntimeType());
            errorMsg = this.typeError;
        } else {
            // should be handling all property changes so this shouldn't happen
            UTIL.log("Property [" + propName + "] is not being handled by property change listener");
        }

        // update message
        if (errorMsg != null) {
            addMessage(errorMsg, newMsg);
        }

        // change message to default message if there is no error
        if ((errorMsg == null) || CoreStringUtil.isEmpty(errorMsg.message)) {
            if (!Messages.propertyDialogMessage.equals(this.scrolledForm.getMessage())) {
                this.scrolledForm.setMessage(Messages.propertyDialogMessage, IMessageProvider.NONE);
            }
        }

        // update buttons enabled state
        updateState();
    }

    void handleRemoveAllowedValue() {
        // TODO implement handleRemoveAllowedValue
    }

    void handleRemoveDescription() {
        assert (getSelectedDescription() != null) : "Remove description button is enabled and shouldn't be"; //$NON-NLS-1$

        if (FormUtil.openQuestion(getShell(), Messages.removeDescriptionDialogTitle, Activator.getDefault().getImage(MED_EDITOR),
                                  NLS.bind(Messages.removeDescriptionDialogMsg, getSelectedDescription()))) {
            // TODO implement handleRemoveDescription
        }
    }

    void handleRemoveDisplayName() {
        assert (getSelectedDisplayName() != null) : "Remove display name button is enabled and shouldn't be"; //$NON-NLS-1$

        if (FormUtil.openQuestion(getShell(), Messages.removeDisplayNameDialogTitle, Activator.getDefault().getImage(MED_EDITOR),
                                  NLS.bind(Messages.removeDisplayNameDialogMsg, getSelectedDisplayName()))) {
            // TODO implement handleRemoveDisplayName
        }
    }

    void handleRequiredChanged( boolean newValue ) {
        this.propDefn.setRequired(newValue);
    }

    void handleRuntimeTypeChanged( String newValue ) {
        Type newType = null;

        try {
            newType = ModelExtensionPropertyDefinition.Utils.convertRuntimeType(newValue);
        } catch (Exception e) {
            UTIL.log(e);
        }

        this.propDefn.setType(newType);
    }

    void handleSimpleIdChanged( String newValue ) {
        this.propDefn.setSimpleId(newValue);
    }

    void handleToggleDefaultValue() {
        // TODO implement handleToggleDefaultValue
    }

    void handleVariableValueSelected() {
        boolean enable = this.btnVariableValue.getSelection();

        if (this.valuesViewer.getTable().getEnabled() != enable) {
            this.valuesViewer.getTable().setEnabled(enable);

            if (enable) {
                // TODO enable applicable toolbar buttons
                // TODO get allowed values from table and set in propDefn
            } else {
                // TODO disable toolbar buttons
                this.propDefn.setAllowedValues(null);
            }
        }
    }

    private boolean isEditMode() {
        return (this.propDefnBeingEdited != null);
    }

    private void updateState() {
        // TODO implement updateState
        boolean enable = (this.scrolledForm.getMessageType() != IMessageProvider.ERROR);

        // update UI controls
        if (enable) {
            if (isEditMode()) {
                // TODO if no changes then don't enable. need to change equals in prop defn class
                enable = this.propDefn.equals(this.propDefnBeingEdited);
            }

            if (this.btnOk.getEnabled() != enable) {
                this.btnOk.setEnabled(enable);
            }
        } else {
            if (this.btnOk.getEnabled()) {
                this.btnOk.setEnabled(false);
            }
        }
    }

    interface ColumnHeaders {
        String LOCALE = Messages.localeColumnText;
        String TRANSLATION = Messages.translationColumnText;

        // allowed value tablew
        String VALUE = Messages.allowedValueColumnText;
        String DEFAULT_VALUE = Messages.defaultValueColumnText;
    }

    interface ColumnIndexes {
        int LOCALE = 0;
        int TRANSLATION = 1;

        // allowed value tablew
        int VALUE = 0;
        int DEFAULT_VALUE = 1;
    }

    interface ColumnToolTips {
        String LOCALE = Messages.localeColumnToolTip;
        String TRANSLATION = Messages.translationColumnToolTip;

        // allowed value tablew
        String VALUE = Messages.allowedValueColumnHeaderToolTip;
        String DEFAULT_VALUE = Messages.defaultValueColumnHeaderToolTip;
    }

    class TranslationLabelProvider extends ColumnLabelProvider {

        private final int columnIndex;

        public TranslationLabelProvider( final int columnIndex ) {
            this.columnIndex = columnIndex;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            Translation translation = (Translation)element;

            if (ColumnIndexes.LOCALE == this.columnIndex) {
                return translation.getLocale().getDisplayLanguage();
            }

            if (ColumnIndexes.TRANSLATION == this.columnIndex) {
                return translation.getTranslation();
            }

            return super.getText(element);
        }

    }

    class AllowedValueLabelProvider extends ColumnLabelProvider {

        private final int columnIndex;

        public AllowedValueLabelProvider( final int columnIndex ) {
            this.columnIndex = columnIndex;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage( Object element ) {
            if (ColumnIndexes.DEFAULT_VALUE == this.columnIndex) {
                // if (((AllowedValue)element).makeDefault) {
                // return Activator.getDefault().getImage(ImageIds.CHECK_MARK);
                // }
            }

            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText( Object element ) {
            if (ColumnIndexes.VALUE == this.columnIndex) {
                // return ((AllowedValue)element).value;
            }

            return null;
        }

    }

}
