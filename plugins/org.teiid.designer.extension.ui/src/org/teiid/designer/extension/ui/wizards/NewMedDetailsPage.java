/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.wizards;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.definition.ValidationStatus;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Activator;
import org.teiid.designer.extension.ui.Messages;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.ui.internal.util.WidgetFactory;
import com.metamatrix.ui.internal.wizard.AbstractWizardPage;

/**
 * @since 5.5
 */
public class NewMedDetailsPage extends AbstractWizardPage {

    private static final int COLUMN_COUNT = 2;
    private static final String DEFAULT_METAMODEL_NAME = "Relational"; //$NON-NLS-1$
    private static final String DEFAULT_VERSION = "1"; //$NON-NLS-1$
    private static final String TOKEN_ERROR = "[Error] "; //$NON-NLS-1$
    private static final String TOKEN_WARNING = "[Warning] "; //$NON-NLS-1$
    private static final String TOKEN_INFO = "[Info] "; //$NON-NLS-1$
    private static final String CR = "\n"; //$NON-NLS-1$

    private Text namespacePrefixText, namespaceUriText, versionText, descriptionText, statusTextBox;
    private Combo cbxMetamodelUris;
    private TableViewer modelTypesViewer;
    private Set<String> supportedModelTypes = new HashSet<String>();
    private ModelExtensionDefinition initialMed;

    public NewMedDetailsPage( ModelExtensionDefinition initialMed ) {
        super(NewMedDetailsPage.class.getSimpleName(), Messages.newMedDetailsPageTitle);
        this.initialMed = initialMed;
    }

    public void createControl( Composite theParent ) {

        Composite pnlMain = WidgetFactory.createPanel(theParent, SWT.NONE, GridData.FILL_BOTH);
        pnlMain.setLayout(new GridLayout(COLUMN_COUNT, false));
        setControl(pnlMain);
        createMainPanel(pnlMain);

        validatePage();
    }

    private Composite createMainPanel( Composite parent ) {
        // If an initial MED was supplied, use it's properties to initialize the UI
        String nsPrefix = null;
        String nsUri = null;
        String extendedMetaclass = null;
        int version = 1;
        String description = null;
        boolean copyMode = false;
        if (this.initialMed != null) {
            copyMode = true;
            nsPrefix = this.initialMed.getNamespacePrefix();
            nsUri = this.initialMed.getNamespaceUri();
            extendedMetaclass = this.initialMed.getMetamodelUri();
            version = this.initialMed.getVersion();
            description = this.initialMed.getDescription();
        }
        // -----------------------------------------------------
        // Namespace Prefix
        // -----------------------------------------------------
        // NSPrefix Label
        WidgetFactory.createLabel(parent, Messages.namespacePrefixLabel);
        // NSPrefix Text widget
        this.namespacePrefixText = WidgetFactory.createTextField(parent, GridData.FILL_HORIZONTAL, 1);
        this.namespacePrefixText.setToolTipText(Messages.medNamespacePrefixTooltip);
        if (nsPrefix != null) this.namespacePrefixText.setText(nsPrefix);
        this.namespacePrefixText.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                nsPrefixModified();
            }
        });

        // -----------------------------------------------------
        // Namespace Uri
        // -----------------------------------------------------
        // NSUri Label
        WidgetFactory.createLabel(parent, Messages.namespaceUriLabel);
        // NSUri text widget
        this.namespaceUriText = WidgetFactory.createTextField(parent, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.namespaceUriText.setToolTipText(Messages.medNamespaceUriTooltip);
        if (nsUri != null) this.namespaceUriText.setText(nsUri);
        this.namespaceUriText.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                nsUriModified();
            }
        });

        // -----------------------------------------------------
        // Metamodel Uri
        // -----------------------------------------------------
        // MetamodelUri Label
        WidgetFactory.createLabel(parent, Messages.extendedMetamodelUriLabel);
        // MetamodelUri text widget
        this.cbxMetamodelUris = WidgetFactory.createCombo(parent, SWT.READ_ONLY, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.cbxMetamodelUris.setToolTipText(Messages.medMetamodelClassTooltip);
        // populate metamodel names
        Set<String> metamodelNames = Activator.getDefault().getExtendableMetamodelNames();
        this.cbxMetamodelUris.setItems(metamodelNames.toArray(new String[metamodelNames.size()]));

        // Set default metamodel in combo, unless one has been provided
        int mmIndx = this.cbxMetamodelUris.indexOf(DEFAULT_METAMODEL_NAME);
        if (extendedMetaclass != null && extendedMetaclass.trim().length() > 0) {
            String mmName = Activator.getDefault().getMetamodelName(extendedMetaclass);
            mmIndx = this.cbxMetamodelUris.indexOf(mmName);
        }
        this.cbxMetamodelUris.select(mmIndx);
        if (copyMode) this.cbxMetamodelUris.setEnabled(false);
        this.cbxMetamodelUris.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                handleMetamodelUriChanged();
            }
        });

        // -----------------------------------------------------
        // Supported ModelTypes
        // -----------------------------------------------------
        // MetamodelUri Label
        WidgetFactory.createLabel(parent, Messages.modelTypesLabel);
        this.modelTypesViewer = WidgetFactory.createTableViewer(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
                                                                                | SWT.BORDER);
        this.modelTypesViewer.getTable().setToolTipText(Messages.medModelTypesToolTip);
        ((GridData)this.modelTypesViewer.getTable().getLayoutData()).heightHint = this.modelTypesViewer.getTable().getItemHeight() * 3;
        this.modelTypesViewer.setContentProvider(new IStructuredContentProvider() {

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
                return getAvailableModelTypes();
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
        this.modelTypesViewer.setLabelProvider(new LabelProvider() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            @Override
            public String getText( Object element ) {
                String modelType = (String)element;
                return Activator.getDefault().getModelTypeName(modelType);
            }
        });
        this.modelTypesViewer.setInput(this);
        setModelTypeSelections();
        if (copyMode) {
            this.modelTypesViewer.getTable().setEnabled(false);
        }
        this.modelTypesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
             */
            @Override
            public void selectionChanged( SelectionChangedEvent event ) {
                handleModelTypeSelected();
            }
        });

        // -----------------------------------------------------
        // Version
        // -----------------------------------------------------
        // Version Label
        WidgetFactory.createLabel(parent, Messages.versionLabel);
        // Version text widget
        this.versionText = WidgetFactory.createTextField(parent,
                                                         GridData.HORIZONTAL_ALIGN_FILL,
                                                         COLUMN_COUNT - 1,
                                                         DEFAULT_VERSION,
                                                         SWT.READ_ONLY);
        this.versionText.setToolTipText(Messages.medVersionTooltip);
        if (version != -1) this.versionText.setText(Integer.toString(version));
        this.versionText.setText(DEFAULT_VERSION);
        this.versionText.setBackground(parent.getBackground());
        if (copyMode) this.versionText.setEnabled(false);

        // -----------------------------------------------------
        // Description
        // -----------------------------------------------------
        // Description Label
        WidgetFactory.createLabel(parent, Messages.descriptionLabel);
        // Description text widget
        this.descriptionText = WidgetFactory.createTextField(parent, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT - 1);
        this.descriptionText.setToolTipText(Messages.medDescriptionToolTip);
        if (description != null) this.descriptionText.setText(description);
        this.descriptionText.addModifyListener(new ModifyListener() {
            public void modifyText( final ModifyEvent event ) {
                descriptionModified();
            }
        });

        // -----------------------------------------------------
        // Status
        // -----------------------------------------------------
        // Status Label
        WidgetFactory.createLabel(parent, GridData.HORIZONTAL_ALIGN_FILL, COLUMN_COUNT, Messages.newMedDetailsPageStatusLabel);
        // Description text widget
        this.statusTextBox = WidgetFactory.createTextField(parent, GridData.FILL_BOTH,
                                                           COLUMN_COUNT,
                                                           null,
                                                           SWT.BORDER | SWT.MULTI | SWT.H_SCROLL);

        this.namespacePrefixText.setFocus();

        return parent;
    }

    private void setModelTypeSelections() {
        if (this.initialMed != null) {
            this.supportedModelTypes = new HashSet(this.initialMed.getSupportedModelTypes());
            if (supportedModelTypes.isEmpty()) {
                this.modelTypesViewer.getTable().selectAll();
            } else {
                this.modelTypesViewer.setSelection(new StructuredSelection(supportedModelTypes.toArray()), true);
            }
        } else {
            this.modelTypesViewer.getTable().selectAll();
        }
    }

    private Object[] getAvailableModelTypes() {
        String mmUri = null;
        if (this.initialMed != null) {
            mmUri = this.initialMed.getMetamodelUri();
        } else {
            String mmName = this.cbxMetamodelUris.getText();
            mmUri = Activator.getDefault().getMetamodelUri(mmName);
        }

        if (CoreStringUtil.isEmpty(mmUri)) {
            return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
        }

        return Activator.getDefault().getModelTypes(mmUri).toArray();
    }

    void handleModelTypeSelected() {
        supportedModelTypes.clear();
        IStructuredSelection selection = (IStructuredSelection)this.modelTypesViewer.getSelection();

        // add any new model types
        for (Object selectedModelType : selection.toArray()) {
            supportedModelTypes.add((String)selectedModelType);
        }
    }

    private void nsPrefixModified() {
        validatePage();
    }

    private void nsUriModified() {
        validatePage();
    }

    private void handleMetamodelUriChanged() {
        this.modelTypesViewer.refresh();
        this.setModelTypeSelections();
        validatePage();
    }

    private void descriptionModified() {
        validatePage();
    }

    public String getNamespacePrefix() {
        return this.namespacePrefixText.getText();
    }

    public String getNamespaceUri() {
        return this.namespaceUriText.getText();
    }

    public String getMetamodelUri() {
        String metamodelName = this.cbxMetamodelUris.getText();
        return Activator.getDefault().getMetamodelUri(metamodelName);
    }

    public String getVersion() {
        return this.versionText.getText();
    }

    public int getVersionInt() {
        int versionInt = 1;
        try {
            versionInt = Integer.parseInt(this.versionText.getText());
        } catch (Exception e) {
        }
        return versionInt;
    }

    public String getDescription() {
        return this.descriptionText.getText();
    }

    public Collection<String> getSupportedModelTypes() {
        return this.supportedModelTypes;
    }

    /**
     * Validation logic for the page. Wizard completion is allowed, even with errors - but the problems should be displayed.
     * 
     * @since 7.6
     */
    private void validatePage() {
        ValidationStatus nsPrefixStatus = ModelExtensionDefinitionValidator.validateNamespacePrefix(getNamespacePrefix(), getRegistry().getAllNamespacePrefixes());
        ValidationStatus nsUriStatus = ModelExtensionDefinitionValidator.validateNamespaceUri(getNamespaceUri(),
                                                                                              getRegistry().getAllNamespaceUris());
        ValidationStatus metaModelUriStatus = ModelExtensionDefinitionValidator.validateMetamodelUri(getMetamodelUri(),
                                                                                                     getRegistry().getExtendableMetamodelUris());
        ValidationStatus definitionStatus = ModelExtensionDefinitionValidator.validateDescription(getDescription());
        ValidationStatus versionStatus = ModelExtensionDefinitionValidator.validateVersion(getVersion());

        StringBuffer sb = new StringBuffer();
        addStatusMessage(sb, nsPrefixStatus);
        addStatusMessage(sb, nsUriStatus);
        addStatusMessage(sb, metaModelUriStatus);
        addStatusMessage(sb, versionStatus);
        addStatusMessage(sb, definitionStatus);

        this.statusTextBox.setText(sb.toString());

        setMessage(Messages.newMedDetailsPageMsg);
        setPageComplete(true);
    }

    private void addStatusMessage( StringBuffer sb,
                                   ValidationStatus status ) {
        if (status.isError()) {
            sb.append(TOKEN_ERROR + status.getMessage() + CR);
        } else if (status.isWarning()) {
            sb.append(TOKEN_WARNING + status.getMessage() + CR);
        } else if (status.isInfo()) {
            sb.append(TOKEN_INFO + status.getMessage() + CR);
        }
    }

    /**
     * @return the model extension registry (never <code>null</code>)
     */
    protected ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
    }

}
