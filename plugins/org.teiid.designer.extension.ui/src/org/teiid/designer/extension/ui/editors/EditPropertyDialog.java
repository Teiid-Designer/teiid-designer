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
    private ModelExtensionPropertyDefinitionImpl propDefn;

    private ScrolledForm scrolledForm;

    private final ErrorMessage advancedError;
    private final ErrorMessage allowedValuesError;
    private final ErrorMessage displayNameError;
    private final ErrorMessage descriptionError;
    private final ErrorMessage indexedError;
    private final ErrorMessage initialValueError;
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
        this.displayNameError = new ErrorMessage();
        this.descriptionError = new ErrorMessage();
        this.indexedError = new ErrorMessage();
        this.initialValueError = new ErrorMessage();
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

    private void addMessage( ErrorMessage errorMsg ) {
        if (CoreStringUtil.isEmpty(errorMsg.message)) {
            this.scrolledForm.getMessageManager().removeMessage(errorMsg.getKey(), errorMsg.getControl());
        } else {
            this.scrolledForm.getMessageManager().addMessage(errorMsg.getKey(), errorMsg.message, null, errorMsg.getMessageType(),
                                                             errorMsg.getControl());
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
            body.setLayout(new GridLayout(2, true));
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

            final CCombo cbxRuntimeType = new CCombo(finalContainer, COMBO_STYLE);
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
                    cbxRuntimeType.setSize(finalTxtSimpleId.getSize());
                }
            });
        }

        FLAGS: {
            Button btn = toolkit.createButton(finalContainer, Messages.editPropertyDialogAdvancedButtonText, SWT.CHECK);
            this.advancedError.widget = btn;
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
            this.indexedError.widget = btn;
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
        // TODO implement createPropertyValueSection
        // TODO assign error widgets here

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
            this.requiredError.widget = btnRequired;
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
            this.maskedError.widget = btnMasked;
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

            this.lstAllowedValues = new org.eclipse.swt.widgets.List(middle, SWT.SINGLE | SWT.BORDER);
            this.lstAllowedValues.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            ((GridData)this.lstAllowedValues.getLayoutData()).horizontalIndent = 20;
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
            this.btnAddValue.setEnabled(false);

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
            this.btnEditValue.setEnabled(false);

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
            this.btnRemoveValue.setEnabled(false);

            if (isEditMode()) {
                String[] allowedValues = this.propDefn.getAllowedValues();

                if ((allowedValues != null) && (allowedValues.length != 0)) {
                    this.lstAllowedValues.setItems(allowedValues);
                }
            }
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
                    handleFixedValueSelected();
                }
            });

            this.txtInitialValue = toolkit.createText(bottom, CoreStringUtil.Constants.EMPTY_STRING, TEXT_STYLE);
            this.initialValueError.widget = this.txtInitialValue;
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
                    handleFixedValueSelected();
                }
            });
            this.btnFixedValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
            ((GridData)this.btnFixedValue.getLayoutData()).horizontalIndent = 20;
            ((GridData)this.btnFixedValue.getLayoutData()).horizontalSpan = 2;

            // TODO set initial enabled state of buttons and text field
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
        sizeDialog(dialog.getShell(), getShell());

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
        int index = this.lstAllowedValues.getSelectionIndex();
        boolean enable = (index != -1);

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
            this.propDefn.removeAllowedValue(selectedAllowedValue);
            this.lstAllowedValues.remove(selectedAllowedValue);

            // add
            this.propDefn.addAllowedValue(changedValue);
            this.lstAllowedValues.add(changedValue);
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

    void handleFixedValueSelected() {
        String newFixedValue = this.txtInitialValue.getText();

        if (!this.btnFixedValue.getSelection()) {
            newFixedValue = CoreStringUtil.Constants.EMPTY_STRING;
        }

        this.propDefn.setFixedValue(newFixedValue);
    }

    void handleHasAllowedValuesSelected() {
        // TODO implement handleHasAllowedValuesSelected
    }

    void handleIndexedChanged( boolean newValue ) {
        this.propDefn.setIndex(newValue);
    }

    void handleInitialValueChanged( String newValue ) {
        // TODO if there are allowed values make sure new value is one of them
        if (!this.propDefn.isModifiable()) {
            this.propDefn.setFixedValue(newValue);
        }
    }

    void handleMaskedChanged( boolean newValue ) {
        this.propDefn.setMasked(newValue);
    }

    void handlePropertyChanged( PropertyChangeEvent e ) {
        String propName = e.getPropertyName();
        ErrorMessage errorMsg = null;

        if (PropertyName.ADVANCED.toString().equals(propName)) {
            this.advancedError.message = ModelExtensionDefinitionValidator.validatePropertyAdvancedAttribute(this.propDefn.isAdvanced());
            errorMsg = this.advancedError;
        } else if (PropertyName.ALLOWED_VALUES.toString().equals(propName)) {
            this.allowedValuesError.message = ModelExtensionDefinitionValidator.validatePropertyAllowedValues(this.propDefn.getRuntimeType(),
                                                                                                              this.propDefn.getAllowedValues());
            errorMsg = this.allowedValuesError;
        } else if (PropertyName.DEFAULT_VALUE.toString().equals(propName)) {
            this.initialValueError.message = ModelExtensionDefinitionValidator.validatePropertyDefaultValue(this.propDefn.getRuntimeType(),
                                                                                                            this.propDefn.getDefaultValue(),
                                                                                                            this.propDefn.getAllowedValues());
            errorMsg = this.initialValueError;
        } else if (PropertyName.DESCRIPTION.equals(propName)) {
            this.descriptionError.message = ModelExtensionDefinitionValidator.validateTranslations(org.teiid.designer.extension.Messages.propertyDescription,
                                                                                                   this.propDefn.getDescriptions());
            errorMsg = this.descriptionError;
        } else if (PropertyName.DISPLAY_NAME.equals(propName)) {
            this.displayNameError.message = ModelExtensionDefinitionValidator.validateTranslations(org.teiid.designer.extension.Messages.propertyDisplayName,
                                                                                                   this.propDefn.getDisplayNames());
            errorMsg = this.displayNameError;
        } else if (PropertyName.FIXED_VALUE.toString().equals(propName)) {
            this.initialValueError.message = ModelExtensionDefinitionValidator.validatePropertyFixedValue(this.propDefn.getRuntimeType(),
                                                                                                          this.propDefn.getFixedValue());
            errorMsg = this.initialValueError;
        } else if (PropertyName.INDEX.toString().equals(propName)) {
            this.indexedError.message = ModelExtensionDefinitionValidator.validatePropertyIndexedAttribute(this.propDefn.shouldBeIndexed());
            errorMsg = this.indexedError;
        } else if (PropertyName.MASKED.toString().equals(propName)) {
            this.maskedError.message = ModelExtensionDefinitionValidator.validatePropertyMaskedAttribute(this.propDefn.isMasked());
            errorMsg = this.maskedError;
        } else if (PropertyName.REQUIRED.toString().equals(propName)) {
            this.requiredError.message = ModelExtensionDefinitionValidator.validatePropertyRequiredAttribute(this.propDefn.isRequired());
            errorMsg = this.requiredError;
        } else if (PropertyName.SIMPLE_ID.toString().equals(propName)) {
            this.simpleIdError.message = ModelExtensionDefinitionValidator.validatePropertySimpleId(this.propDefn.getSimpleId(),
                                                                                                    this.existingPropIds);
            errorMsg = this.simpleIdError;
        } else if (PropertyName.TYPE.toString().equals(propName)) {
            this.typeError.message = ModelExtensionDefinitionValidator.validatePropertyRuntimeType(this.propDefn.getRuntimeType());
            errorMsg = this.typeError;
        } else {
            // should be handling all property changes so this shouldn't happen
            UTIL.log("Property [" + propName + "] is not being handled by property change listener");
        }

        // update message
        if (errorMsg != null) {
            addMessage(errorMsg);
        }

        // change message to default message if there is no error
        if (this.scrolledForm.getMessageType() != IMessageProvider.ERROR) {
            if (!Messages.propertyDialogMessage.equals(this.scrolledForm.getMessage())) {
                // eclipse bug keeps font foreground red after an error when setting to NONE so set to INFO first as workaround
                this.scrolledForm.setMessage(Messages.propertyDialogMessage, IMessageProvider.INFORMATION);
                this.scrolledForm.setMessage(Messages.propertyDialogMessage, IMessageProvider.NONE);
            }
        }

        // update buttons enabled state
        updateState();
    }

    void handleRemoveAllowedValue() {
        assert (getSelectedAllowedValue() != null) : "Remove allowed value button is enabled and shouldn't be"; //$NON-NLS-1$

        if (FormUtil.openQuestion(getShell(), Messages.removeAllowedValueDialogTitle, Activator.getDefault().getImage(MED_EDITOR),
                                  NLS.bind(Messages.removeAllowedValueDialogMsg, getSelectedDescription()))) {
            this.propDefn.removeAllowedValue(getSelectedAllowedValue());
            this.lstAllowedValues.remove(getSelectedAllowedValue());
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
        } catch (Exception e) {
            UTIL.log(e);
        }

        this.propDefn.setType(newType);
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
