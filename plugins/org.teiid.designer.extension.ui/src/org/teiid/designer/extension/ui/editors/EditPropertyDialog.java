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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.PropertyName;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition.Type;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinitionImpl;
import org.teiid.designer.extension.properties.NamespacePrefixProvider;
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
    private final NamespacePrefixProvider namespacePrefixProvider;

    private Button btnEditDescription;
    private Button btnEditDisplayName;
    private Button btnOk;
    private Button btnRemoveDescription;
    private Button btnRemoveDisplayName;

    private Button btnAllowedValues;
    private org.eclipse.swt.widgets.List lstAllowedValues;
    private Button btnAddValue;
    private Button btnEditValue;
    private Button btnRemoveValue;
    private Button btnInitialValue;
    private Text txtInitialValue;
    private Button btnFixedValue;

    private TableViewer descriptionViewer;
    private TableViewer displayNameViewer;

    private final Collection<String> existingPropIds;

    private IManagedForm managedForm;

    /**
     * The property definition being edited or <code>null</code> when creating a new property definition.
     */
    private ModelExtensionPropertyDefinition propDefnBeingEdited;
    private ModelExtensionPropertyDefinition propDefn;

    private ScrolledForm scrolledForm;

    private final ErrorMessage advancedError;
    private final ErrorMessage allowedValuesError;
    private final ErrorMessage displayNameError;
    private final ErrorMessage descriptionError;
    private final ErrorMessage indexedError;
    private final ErrorMessage initialValueError;
    private final ErrorMessage maskedError;
    private final ErrorMessage requiredError;
    private final ErrorMessage simpleIdError;
    private final ErrorMessage typeError;

    public EditPropertyDialog( Shell shell,
                               NamespacePrefixProvider namespacePrefixProvider,
                               String metaclassName,
                               Collection<String> existingPropIds ) {
        super(shell);

        CoreArgCheck.isNotEmpty(metaclassName, "metaclassName is empty"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(namespacePrefixProvider, "namespacePrefixProvider is null"); //$NON-NLS-1$
        this.metaclassName = metaclassName;
        this.namespacePrefixProvider = namespacePrefixProvider;

        this.existingPropIds = new ArrayList<String>(existingPropIds);
        this.propDefn = new ModelExtensionPropertyDefinitionImpl();

        // errors
        this.advancedError = new ErrorMessage();
        this.allowedValuesError = new ErrorMessage();
        this.displayNameError = new ErrorMessage();
        this.descriptionError = new ErrorMessage();
        this.indexedError = new ErrorMessage();
        this.initialValueError = new ErrorMessage();
        this.maskedError = new ErrorMessage();
        this.requiredError = new ErrorMessage();
        this.simpleIdError = new ErrorMessage();
        this.typeError = new ErrorMessage();
    }

    public EditPropertyDialog( Shell shell,
                               NamespacePrefixProvider namespacePrefixProvider,
                               String metaclassName,
                               Collection<String> existingPropIds,
                               ModelExtensionPropertyDefinition propDefnBeingEdited ) {
        this(shell, namespacePrefixProvider, metaclassName, existingPropIds);
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
            this.propDefn.setRequired(this.propDefnBeingEdited.isRequired());
            this.propDefn.setSimpleId(this.propDefnBeingEdited.getSimpleId());
            this.propDefn.setType(this.propDefnBeingEdited.getType());
            this.propDefn.setNamespacePrefix(this.propDefnBeingEdited.getNamespacePrefix());

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

    private void addMessage( ErrorMessage errorMsg ) {
        if (CoreStringUtil.isEmpty(errorMsg.getMessage())) {
            this.scrolledForm.getMessageManager().removeMessage(errorMsg.getKey(), errorMsg.getControl());
        } else {
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
            section.setExpanded(false);
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
            this.descriptionError.setControl(table);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new GridLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 5;
            ((GridData)table.getLayoutData()).widthHint = (int)(getParentShell().getSize().x * .4);

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
            section.setExpanded(false);
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
            this.displayNameError.setControl(table);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            table.setLayout(new GridLayout());
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)table.getLayoutData()).heightHint = table.getItemHeight() * 5;
            ((GridData)table.getLayoutData()).widthHint = (int)(getParentShell().getSize().x * .4);

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
            body.setLayout(new GridLayout());
            body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            body.addControlListener(new ControlAdapter() {
                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                 */
                @Override
                public void controlResized( ControlEvent e ) {
                    super.controlResized(e);
                }
            });
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

        // set message
        validateAll();
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

            Label label = toolkit.createLabel(finalContainer, this.namespacePrefixProvider.getNamespacePrefix());
            label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        }

        final Text finalTxtSimpleId;

        ID: {
            toolkit.createLabel(finalContainer, Messages.simpleIdLabel);

            Text txtSimpleId = toolkit.createText(finalContainer, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            finalTxtSimpleId = txtSimpleId;
            this.simpleIdError.setControl(txtSimpleId);
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

            if (isEditMode() && (this.propDefn.getSimpleId() != null)) {
                txtSimpleId.setText(this.propDefn.getSimpleId());
            }
        }

        RUNTIME_TYPE: {
            toolkit.createLabel(finalContainer, Messages.runtimeTypeLabel);

            CCombo cbxRuntimeType = new CCombo(finalContainer, COMBO_STYLE);
            toolkit.adapt(cbxRuntimeType, true, false);
            this.typeError.setControl(cbxRuntimeType);
            cbxRuntimeType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            ((GridData)cbxRuntimeType.getLayoutData()).heightHint = cbxRuntimeType.getItemHeight() + 4;

            // populate runtime types
            for (Type type : Type.values()) {
                cbxRuntimeType.add(type.getRuntimeType());
            }

            // set selection based on property definition
            String runtimeType = this.propDefn.getRuntimeType();

            if (!CoreStringUtil.isEmpty(runtimeType)) {
                int index = cbxRuntimeType.indexOf(runtimeType);

                if (index == -1) {
                    UTIL.log(NLS.bind(Messages.editPropertyDialogInvalidRuntimeTypeMsg, runtimeType));
                } else {
                    cbxRuntimeType.select(index);
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
        }

        FLAGS: {
            Button btn = toolkit.createButton(finalContainer, Messages.editPropertyDialogAdvancedButtonText, SWT.CHECK);
            this.advancedError.setControl(btn);
            btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            ((GridData)btn.getLayoutData()).verticalIndent = 5;
            ((GridData)btn.getLayoutData()).horizontalSpan = 2;
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

            btn = toolkit.createButton(finalContainer, Messages.editPropertyDialogIndexButtonText, SWT.CHECK);
            this.indexedError.setControl(btn);
            btn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            ((GridData)btn.getLayoutData()).horizontalSpan = 2;
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
        Section section = FormUtil.createSection(this.managedForm, toolkit, body,
                                                 Messages.editPropertyDialogPropertyValueSectionTitle,
                                                 Messages.editPropertyDialogPropertyValueSectionDescription, SECTION_STYLE, true);

        final Composite finalContainer;

        SECTION: {
            Composite container = toolkit.createComposite(section);
            finalContainer = container;
            container.setLayout(new GridLayout());
            container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            section.setClient(container);
        }

        TOP: {
            Button btnRequired = toolkit.createButton(finalContainer, Messages.editPropertyDialogRequiredButtonText, SWT.CHECK);
            this.requiredError.setControl(btnRequired);
            btnRequired.setSelection(this.propDefn.isRequired());
            btnRequired.setToolTipText(Messages.editPropertyDialogRequiredButtonToolTip);
            btnRequired.addSelectionListener(new SelectionAdapter() {

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

            Button btnMasked = toolkit.createButton(finalContainer, Messages.editPropertyDialogMaskedButtonText, SWT.CHECK);
            this.maskedError.setControl(btnMasked);
            btnMasked.setSelection(this.propDefn.isMasked());
            btnMasked.setToolTipText(Messages.editPropertyDialogMaskedButtonToolTip);
            btnMasked.addSelectionListener(new SelectionAdapter() {

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
        }

        MIDDLE: {
            Composite middle = toolkit.createComposite(finalContainer);
            GridLayout middleLayout = new GridLayout(2, false);
            middleLayout.horizontalSpacing = 0;
            middleLayout.marginHeight = 0;
            middleLayout.marginWidth = 0;
            middle.setLayout(middleLayout);
            middle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            this.btnAllowedValues = toolkit.createButton(middle, Messages.editPropertyDialogAllowedValuesButtonText, SWT.CHECK);
            this.btnAllowedValues.setToolTipText(Messages.editPropertyDialogAllowedValuesButtonToolTip);
            this.btnAllowedValues.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            ((GridData)this.btnAllowedValues.getLayoutData()).horizontalSpan = 2;

            this.lstAllowedValues = new org.eclipse.swt.widgets.List(middle, SWT.SINGLE | SWT.BORDER);
            this.allowedValuesError.setControl(this.lstAllowedValues);
            this.lstAllowedValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)this.lstAllowedValues.getLayoutData()).horizontalIndent = 20;
            ((GridData)this.lstAllowedValues.getLayoutData()).heightHint = this.lstAllowedValues.getItemHeight() * 5;
            this.lstAllowedValues.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleAllowedValueSelected();
                }
            });

            // wait until after populating list before adding selection listener
            this.btnAllowedValues.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleHasAllowedValuesSelected();
                }
            });

            Composite pnlButtons = toolkit.createComposite(middle);
            RowLayout btnLayout = new RowLayout(SWT.VERTICAL);
            btnLayout.marginLeft = 0;
            btnLayout.marginRight = 0;
            btnLayout.spacing = 0;
            btnLayout.marginTop = 0;
            btnLayout.marginBottom = 0;
            pnlButtons.setLayout(btnLayout);
            pnlButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            // configure add allowed value button
            this.btnAddValue = toolkit.createButton(pnlButtons, CoreStringUtil.Constants.EMPTY_STRING, SWT.FLAT);
            this.btnAddValue.addSelectionListener(new SelectionAdapter() {

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
            this.btnAddValue.setToolTipText(Messages.editPropertyDialogAddAllowedValueButtonToolTip);
            this.btnAddValue.setImage(Activator.getDefault().getImage(ImageIds.ADD_VALUE));

            // configure edit allowed value button
            this.btnEditValue = toolkit.createButton(pnlButtons, CoreStringUtil.Constants.EMPTY_STRING, SWT.FLAT);
            this.btnEditValue.addSelectionListener(new SelectionAdapter() {

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
            this.btnEditValue.setToolTipText(Messages.editPropertyDialogEditAllowedValueButtonToolTip);
            this.btnEditValue.setImage(Activator.getDefault().getImage(ImageIds.EDIT_VALUE));

            // configure remove allowed value button
            this.btnRemoveValue = toolkit.createButton(pnlButtons, CoreStringUtil.Constants.EMPTY_STRING, SWT.FLAT);
            this.btnRemoveValue.addSelectionListener(new SelectionAdapter() {

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
            this.btnRemoveValue.setToolTipText(Messages.editPropertyDialogRemoveAllowedValueButtonToolTip);
            this.btnRemoveValue.setImage(Activator.getDefault().getImage(ImageIds.REMOVE_VALUE));

            // populate allowed values and set initial enablements
            if (isEditMode()) {
                String[] allowedValues = this.propDefn.getAllowedValues();

                if ((allowedValues != null) && (allowedValues.length != 0)) {
                    this.btnAllowedValues.setSelection(true);
                    this.lstAllowedValues.setItems(allowedValues);
                }
            }

            boolean enable = this.btnAllowedValues.getSelection();
            this.lstAllowedValues.setEnabled(enable);
            this.btnAddValue.setEnabled(enable);
            this.btnEditValue.setEnabled(false);
            this.btnRemoveValue.setEnabled(false);
        }

        BOTTOM: {
            Composite bottom = toolkit.createComposite(finalContainer, SWT.NONE);
            bottom.setLayout(new GridLayout(2, false));
            bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            this.btnInitialValue = toolkit.createButton(bottom, Messages.editPropertyDialogInitialValueButtonText, SWT.CHECK);
            this.btnInitialValue.setToolTipText(Messages.editPropertyDialogInitialValueButtonToolTip);
            this.btnInitialValue.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleHasInitialValueSelected();
                }
            });

            this.txtInitialValue = toolkit.createText(bottom, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            this.initialValueError.setControl(this.txtInitialValue);
            this.txtInitialValue.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    handleInitialValueChanged(((Text)e.widget).getText());
                }
            });
            this.txtInitialValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            this.btnFixedValue = toolkit.createButton(bottom, Messages.editPropertyDialogFixedValueButtonText, SWT.CHECK);
            this.btnFixedValue.setToolTipText(Messages.editPropertyDialogFixedValueButtonToolTip);
            this.btnFixedValue.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
                 */
                @Override
                public void widgetSelected( SelectionEvent e ) {
                    handleHasFixedValueSelected();
                }
            });
            this.btnFixedValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            ((GridData)this.btnFixedValue.getLayoutData()).horizontalIndent = 20;
            ((GridData)this.btnFixedValue.getLayoutData()).horizontalSpan = 2;

            if (!CoreStringUtil.isEmpty(this.propDefn.getDefaultValue())) {
                this.btnInitialValue.setSelection(true);
                this.txtInitialValue.setText(this.propDefn.getDefaultValue());
            } else if (!CoreStringUtil.isEmpty(this.propDefn.getFixedValue())) {
                this.btnInitialValue.setSelection(true);
                this.btnFixedValue.setSelection(true);
                this.txtInitialValue.setText(this.propDefn.getFixedValue());
            } else {
                this.btnInitialValue.setSelection(false);
                this.btnFixedValue.setSelection(false);
                this.btnFixedValue.setEnabled(false);
            }
        }

        return section;
    }

    ModelExtensionPropertyDefinition getPropertyDefinition() {
        return this.propDefn;
    }

    String getSelectedAllowedValue() {
        int index = this.lstAllowedValues.getSelectionIndex();
        return ((index == -1) ? null : this.lstAllowedValues.getItem(index));
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
        sizeDialog(dialog.getShell(), getShell());

        if (dialog.open() == Window.OK) {
            String newAllowedValue = dialog.getAllowedValue();

            if (this.propDefn.addAllowedValue(newAllowedValue)) {
                this.lstAllowedValues.add(newAllowedValue);
            } else {
                // not sure when this would happen
                UTIL.log(NLS.bind(Messages.editPropertyDialogAllowedValueNotAddedMsg, newAllowedValue));
            }
        }
    }

    void handleAddDescription() {
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.addPropertyDescriptionDialogTitle,
                                                                 org.teiid.designer.extension.Messages.propertyDescription,
                                                                 this.propDefn.getDescriptions());
        dialog.create();
//        sizeDialog(dialog.getShell(), getShell());
        dialog.getShell().pack();

        if (dialog.open() == Window.OK) {
            Translation newDescription = dialog.getTranslation();

            if (this.propDefn.addDescription(newDescription)) {
                this.descriptionViewer.refresh();
            } else {
                // not sure when this would happen
                UTIL.log(NLS.bind(Messages.editPropertyDialogDescriptionNotAddedMsg, newDescription));
            }
        }
    }

    void handleAddDisplayName() {
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.addPropertyDisplayNameDialogTitle,
                                                                 org.teiid.designer.extension.Messages.propertyDisplayName,
                                                                 this.propDefn.getDisplayNames());
        dialog.create();
        sizeDialog(dialog.getShell(), getShell());

        if (dialog.open() == Window.OK) {
            Translation newDisplayName = dialog.getTranslation();

            if (this.propDefn.addDisplayName(newDisplayName)) {
                this.displayNameViewer.refresh();
            } else {
                // not sure when this would happen
                UTIL.log(NLS.bind(Messages.editPropertyDialogDisplayNameNotAddedMsg, newDisplayName));
            }
        }
    }

    void handleAdvancedChanged( boolean newValue ) {
        this.propDefn.setAdvanced(newValue);
    }

    void handleAllowedValueSelected() {
        boolean enable = (this.lstAllowedValues.getSelectionIndex() != -1);

        if (this.btnEditValue.getEnabled() != enable) {
            this.btnEditValue.setEnabled(enable);
        }

        if (this.btnRemoveValue.getEnabled() != enable) {
            this.btnRemoveValue.setEnabled(enable);
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
        IStructuredSelection selection = (IStructuredSelection)this.displayNameViewer.getSelection();
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
        sizeDialog(dialog.getShell(), getShell());

        if (dialog.open() == Window.OK) {
            String changedValue = dialog.getAllowedValue();

            // remove
            if (this.propDefn.removeAllowedValue(selectedAllowedValue)) {
                this.lstAllowedValues.remove(selectedAllowedValue);

                // add
                if (this.propDefn.addAllowedValue(changedValue)) {
                    this.lstAllowedValues.add(changedValue);
                }
            }
        }
    }

    void handleEditDescription() {
        assert (getSelectedDescription() != null) : "Edit description button is enabled and shouldn't be"; //$NON-NLS-1$
        Translation selectedDescription = getSelectedDescription();
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.editPropertyDescriptionDialogTitle,
                                                                 org.teiid.designer.extension.Messages.propertyDescription,
                                                                 this.propDefn.getDescriptions(),
                                                                 selectedDescription);
        dialog.create();
        sizeDialog(dialog.getShell(), getShell());

        if (dialog.open() == Window.OK) {
            Translation changedDescription = dialog.getTranslation();
            this.propDefn.removeDescription(selectedDescription);
            this.propDefn.addDescription(changedDescription);
            this.descriptionViewer.refresh();
        }
    }

    void handleEditDisplayName() {
        assert (getSelectedDisplayName() != null) : "Edit display name button is enabled and shouldn't be"; //$NON-NLS-1$
        Translation selectedDisplayName = getSelectedDisplayName();
        EditTranslationDialog dialog = new EditTranslationDialog(getShell(),
                                                                 Messages.editPropertyDescriptionDialogTitle,
                                                                 org.teiid.designer.extension.Messages.propertyDisplayName,
                                                                 this.propDefn.getDisplayNames(),
                                                                 selectedDisplayName);
        dialog.create();
        sizeDialog(dialog.getShell(), getShell());

        if (dialog.open() == Window.OK) {
            Translation changedDisplayName = dialog.getTranslation();
            this.propDefn.removeDescription(selectedDisplayName);
            this.propDefn.addDescription(changedDisplayName);
            this.descriptionViewer.refresh();
        }
    }

    void handleHasAllowedValuesSelected() {
        boolean enable = this.btnAllowedValues.getSelection();

        if (enable) {
            for (String allowedValue : this.lstAllowedValues.getItems()) {
                this.propDefn.addAllowedValue(allowedValue);
            }
        } else {
            for (String allowedValue : this.lstAllowedValues.getItems()) {
                this.propDefn.removeAllowedValue(allowedValue);
            }
        }

        if (this.lstAllowedValues.getEnabled() != enable) {
            this.lstAllowedValues.setEnabled(enable);
        }

        if (this.btnAddValue.getEnabled() != enable) {
            this.btnAddValue.setEnabled(enable);
        }

        // make sure there is an allowed value selected in order to enable
        enable = enable && (this.lstAllowedValues.getSelectionIndex() != -1);

        if (this.btnEditValue.getEnabled() != enable) {
            this.btnEditValue.setEnabled(enable);
        }

        if (this.btnRemoveValue.getEnabled() != enable) {
            this.btnRemoveValue.setEnabled(enable);
        }

        // must call these explicitly as a property changed is not generated within this method
        validateAllowedValues();
        updateState();
    }

    void handleHasFixedValueSelected() {
        boolean hasFixedValue = this.btnFixedValue.getSelection();
        String value = this.txtInitialValue.getText();

        if (hasFixedValue) {
            this.propDefn.setFixedValue(value);
            this.propDefn.setDefaultValue(null);
        } else {
            this.propDefn.setFixedValue(null);
            this.propDefn.setDefaultValue(value);
        }
    }

    void handleHasInitialValueSelected() {
        boolean hasInitialValue = this.btnInitialValue.getSelection();
        boolean valueIsFixed = this.btnFixedValue.getSelection();
        String value = this.txtInitialValue.getText();

        if (hasInitialValue) {
            if (valueIsFixed) {
                this.propDefn.setFixedValue(value);
            } else {
                this.propDefn.setDefaultValue(value);
            }
        } else {
            this.propDefn.setDefaultValue(null);
            this.propDefn.setFixedValue(null);
        }

        if (this.txtInitialValue.getEnabled() != hasInitialValue) {
            this.txtInitialValue.setEnabled(hasInitialValue);
        }

        if (this.btnFixedValue.getEnabled() != hasInitialValue) {
            this.btnFixedValue.setEnabled(hasInitialValue);
        }

        validateInitialValue(hasInitialValue, valueIsFixed);
        addMessage(this.initialValueError);
    }

    void handleIndexedChanged( boolean newValue ) {
        this.propDefn.setIndex(newValue);
    }

    void handleInitialValueChanged( String newValue ) {
        boolean shouldHaveInitialValue = this.btnInitialValue.getSelection();

        if (shouldHaveInitialValue) {
            if (this.btnFixedValue.getSelection()) {
                this.propDefn.setFixedValue(newValue);
                this.propDefn.setDefaultValue(null);
            } else {
                this.propDefn.setFixedValue(null);
                this.propDefn.setDefaultValue(newValue);
            }
        } else {
            this.propDefn.setFixedValue(null);
            this.propDefn.setDefaultValue(null);
        }
    }

    void handleMaskedChanged( boolean newValue ) {
        this.propDefn.setMasked(newValue);
    }

    void handlePropertyChanged( PropertyChangeEvent e ) {
        String propName = e.getPropertyName();

        if (PropertyName.ADVANCED.toString().equals(propName)) {
            validateAdvancedValue();
        } else if (PropertyName.ALLOWED_VALUES.toString().equals(propName)) {
            validateAllowedValues();
        } else if (PropertyName.DEFAULT_VALUE.toString().equals(propName)) {
            validateInitialValue(this.btnInitialValue.getSelection(), false);
        } else if (PropertyName.DESCRIPTION.toString().equals(propName)) {
            validateDescriptions();
        } else if (PropertyName.DISPLAY_NAME.toString().equals(propName)) {
            validateDisplayNames();
        } else if (PropertyName.FIXED_VALUE.toString().equals(propName)) {
            validateInitialValue(this.btnInitialValue.getSelection(), true);
        } else if (PropertyName.INDEX.toString().equals(propName)) {
            validateIndex();
        } else if (PropertyName.MASKED.toString().equals(propName)) {
            validateMasked();
        } else if (PropertyName.REQUIRED.toString().equals(propName)) {
            validateRequired();
        } else if (PropertyName.SIMPLE_ID.toString().equals(propName)) {
            validateSimpleId();
        } else if (PropertyName.TYPE.toString().equals(propName)) {
            validateType();
        } else {
            // shouldn't happen if handling all property changes
            assert false;
        }

        // update buttons enabled state and dialog message
        updateState();
    }

    void handleRemoveAllowedValue() {
        assert (getSelectedAllowedValue() != null) : "Remove allowed value button is enabled and shouldn't be"; //$NON-NLS-1$

        if (FormUtil.openQuestion(getShell(), Messages.removeAllowedValueDialogTitle, Activator.getDefault().getImage(MED_EDITOR),
                                  NLS.bind(Messages.removeAllowedValueDialogMsg, getSelectedDescription()))) {
            this.propDefn.removeAllowedValue(getSelectedAllowedValue());
            this.lstAllowedValues.remove(getSelectedAllowedValue());
            handleAllowedValueSelected(); // removing from ths List doesn't seem to generate a new selection event
        }
    }

    void handleRemoveDescription() {
        assert (getSelectedDescription() != null) : "Remove description button is enabled and shouldn't be"; //$NON-NLS-1$

        if (FormUtil.openQuestion(getShell(), Messages.removeDescriptionDialogTitle, Activator.getDefault().getImage(MED_EDITOR),
                                  NLS.bind(Messages.removeDescriptionDialogMsg, getSelectedDescription()))) {
            this.propDefn.removeDescription(getSelectedDescription());
            this.descriptionViewer.refresh();
        }
    }

    void handleRemoveDisplayName() {
        assert (getSelectedDisplayName() != null) : "Remove display name button is enabled and shouldn't be"; //$NON-NLS-1$

        if (FormUtil.openQuestion(getShell(), Messages.removeDisplayNameDialogTitle, Activator.getDefault().getImage(MED_EDITOR),
                                  NLS.bind(Messages.removeDisplayNameDialogMsg, getSelectedDisplayName()))) {
            this.propDefn.removeDisplayName(getSelectedDisplayName());
            this.displayNameViewer.refresh();
        }
    }

    void handleRequiredChanged( boolean newValue ) {
        this.propDefn.setRequired(newValue);
    }

    void handleRuntimeTypeChanged( String newValue ) {
        Type newType = null;

        try {
            newType = ModelExtensionPropertyDefinition.Utils.convertRuntimeType(newValue);
            this.propDefn.setType(newType);
        } catch (Exception e) {
            UTIL.log(e);
        }
    }

    void handleSimpleIdChanged( String newValue ) {
        this.propDefn.setSimpleId(newValue);
    }

    private boolean isEditMode() {
        return (this.propDefnBeingEdited != null);
    }

    private void sizeDialog( Shell dialogShell,
                             Shell parentShell ) {
        dialogShell.pack();

        Point dialogSize = dialogShell.getSize();
        Point parentSize = parentShell.getSize();

        if ((dialogSize.x > parentSize.x) || (dialogSize.y > parentSize.y)) {
            dialogShell.setSize((int)(parentSize.x * 0.8), (int)(parentSize.y * 0.8));
        }
    }

    private void updateState() {
        boolean enable = (this.scrolledForm.getMessageType() != IMessageProvider.ERROR);

        // change message to default message if there is no error
        if (enable) {
            if (!Messages.propertyDialogMessage.equals(this.scrolledForm.getMessage())) {
                // eclipse bug keeps font foreground red after an error when setting to NONE so set to INFO first as workaround
                this.scrolledForm.setMessage(Messages.propertyDialogMessage, IMessageProvider.INFORMATION);
                this.scrolledForm.setMessage(Messages.propertyDialogMessage, IMessageProvider.NONE);
            }
        }

        // update UI controls
        if (enable) {
            if (isEditMode()) {
                enable = !this.propDefn.equals(this.propDefnBeingEdited);
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

    private void validateAdvancedValue() {
        this.advancedError.setMessage(ModelExtensionDefinitionValidator.validatePropertyAdvancedAttribute(this.propDefn.isAdvanced()));
        addMessage(this.advancedError);
    }

    private void validateAll() {
        validateAdvancedValue();
        validateAllowedValues();
        validateDescriptions();
        validateDisplayNames();
        validateIndex();
        validateInitialValue(this.btnInitialValue.getSelection(), this.btnFixedValue.getSelection());
        validateMasked();
        validateRequired();
        validateSimpleId();
        validateType();
    }

    private void validateAllowedValues() {
        this.allowedValuesError.clearMessage();

        // make sure there is at least one allowed value
        if (this.btnAllowedValues.getSelection() && (this.propDefn.allowedValues().isEmpty())) {
            this.allowedValuesError.setMessage(Messages.editPropertyDialogAllowedValueRequiredMsg);
        }

        if (CoreStringUtil.isEmpty(this.allowedValuesError.getMessage())) {
            this.allowedValuesError.setMessage(ModelExtensionDefinitionValidator.validatePropertyAllowedValues(this.propDefn.getRuntimeType(),
                                                                                                               this.propDefn.getAllowedValues()));
        }

        addMessage(this.allowedValuesError);
    }

    private void validateDescriptions() {
        this.descriptionError.setMessage(ModelExtensionDefinitionValidator.validateTranslations(org.teiid.designer.extension.Messages.propertyDescription,
                                                                                                this.propDefn.getDescriptions(),
                                                                                                true));
        addMessage(this.descriptionError);
    }

    private void validateDisplayNames() {
        this.displayNameError.setMessage(ModelExtensionDefinitionValidator.validateTranslations(org.teiid.designer.extension.Messages.propertyDisplayName,
                                                                                                this.propDefn.getDisplayNames(),
                                                                                                true));
        addMessage(this.displayNameError);
    }

    private void validateIndex() {
        this.indexedError.setMessage(ModelExtensionDefinitionValidator.validatePropertyIndexedAttribute(this.propDefn.shouldBeIndexed()));
        addMessage(this.indexedError);
    }

    private void validateInitialValue( boolean shouldHaveInitialValue,
                                       boolean valueIsFixed ) {
        if (shouldHaveInitialValue) {
            if (valueIsFixed) {
                this.initialValueError.setMessage(ModelExtensionDefinitionValidator.validatePropertyFixedValue(this.propDefn.getRuntimeType(),
                                                                                                               this.propDefn.getFixedValue()));
            } else {
                this.initialValueError.setMessage(ModelExtensionDefinitionValidator.validatePropertyDefaultValue(this.propDefn.getRuntimeType(),
                                                                                                                 this.propDefn.getDefaultValue(),
                                                                                                                 this.propDefn.getAllowedValues()));
            }
        } else {
            this.initialValueError.setMessage(null);
        }

        addMessage(this.initialValueError);
    }

    private void validateMasked() {
        this.maskedError.setMessage(ModelExtensionDefinitionValidator.validatePropertyMaskedAttribute(this.propDefn.isMasked()));
        addMessage(this.maskedError);
    }

    private void validateRequired() {
        this.requiredError.setMessage(ModelExtensionDefinitionValidator.validatePropertyRequiredAttribute(this.propDefn.isRequired()));
        addMessage(this.requiredError);
    }

    private void validateSimpleId() {
        this.simpleIdError.setMessage(ModelExtensionDefinitionValidator.validatePropertySimpleId(this.propDefn.getSimpleId(),
                                                                                                 this.existingPropIds));
        addMessage(this.simpleIdError);
    }

    private void validateType() {
        this.typeError.setMessage(ModelExtensionDefinitionValidator.validatePropertyRuntimeType(this.propDefn.getRuntimeType()));
        addMessage(this.typeError);
        validateAllowedValues();
    }

    private interface ColumnHeaders {
        String LOCALE = Messages.localeColumnText;
        String TRANSLATION = Messages.translationColumnText;
    }

    private interface ColumnIndexes {
        int LOCALE = 0;
        int TRANSLATION = 1;
    }

    private interface ColumnToolTips {
        String LOCALE = Messages.localeColumnToolTip;
        String TRANSLATION = Messages.translationColumnToolTip;
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
                return translation.getLocale().toString();
            }

            if (ColumnIndexes.TRANSLATION == this.columnIndex) {
                return translation.getTranslation();
            }

            return super.getText(element);
        }

    }

}
