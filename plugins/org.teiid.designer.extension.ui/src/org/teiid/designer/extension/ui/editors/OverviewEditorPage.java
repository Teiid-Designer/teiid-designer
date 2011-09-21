/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui.editors;

import static org.teiid.designer.extension.ui.UiConstants.EditorIds.MED_OVERVIEW_PAGE;

import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionValidator;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;
import org.teiid.designer.extension.ui.Messages;

import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.modeler.internal.ui.forms.FormUtil;

/**
 * 
 */
public final class OverviewEditorPage extends FormPage {

    private CCombo cbxMetamodelUris;

    private String descriptionValidationMsg;

    private Form form;
    private ModelExtensionDefinition med;
    private String metamodelUriValidationMsg;
    private String namespacePrefixValidationMsg;
    private String namespaceUriValidationMsg;
    private String resourcePathValidationMsg;

    private Text txtDescription;
    private Text txtMetamodelUri;
    private Text txtNamespacePrefix;
    private Text txtNamespaceUri;
    private Text txtResourcePath;
    private Text txtVersion;
    private String versionValidationMsg;

    public OverviewEditorPage( FormEditor medEditor ) {
        super(medEditor, MED_OVERVIEW_PAGE, Messages.medEditorOverviewPageTitle);
    }

    public OverviewEditorPage( FormEditor medEditor,
                               ModelExtensionDefinition med ) {
        this(medEditor);
        this.med = med;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
     */
    @SuppressWarnings("unused")
    @Override
    protected void createFormContent( IManagedForm managedForm ) {
        FormToolkit toolkit = managedForm.getToolkit();

        ScrolledForm scrolledForm = managedForm.getForm();
        scrolledForm.setText(Messages.medEditorOverviewPageTitle);

        this.form = scrolledForm.getForm();
        toolkit.decorateFormHeading(this.form);

        Composite body = scrolledForm.getBody();
        body.setLayout(FormUtil.createFormGridLayout(false, 2));

        Section section = toolkit.createSection(body, ExpandableComposite.NO_TITLE | ExpandableComposite.TITLE_BAR);
        section.setLayout(FormUtil.createClearGridLayout(false, 1));
        section.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        Composite container = toolkit.createComposite(section);
        container.setLayout(FormUtil.createSectionClientGridLayout(false, 2));
        section.setClient(container);

        NAMESPACE_PREFIX: {
            toolkit.createLabel(container, Messages.namespacePrefixLabel);

            this.txtNamespacePrefix = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER);
            this.txtNamespacePrefix.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
            this.txtNamespacePrefix.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateNamespacePrefix();
                    updateMessage();
                }
            });
        }

        final Text txtNamespaceUri;

        NAMESPACE_URI: {
            toolkit.createLabel(container, Messages.namespaceUriLabel);

            this.txtNamespaceUri = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER);
            txtNamespaceUri = this.txtNamespaceUri;
            this.txtNamespaceUri.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
            this.txtNamespaceUri.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateNamespaceUri();
                    updateMessage();
                }
            });
        }

        METAMODEL_URI: {
            toolkit.createLabel(container, Messages.extendedMetamodelUriLabel);

            this.cbxMetamodelUris = new CCombo(container, SWT.FLAT | SWT.READ_ONLY | SWT.BORDER);
            toolkit.adapt(this.cbxMetamodelUris, true, false);

            // populate URIs
            Set<String> items = ExtensionPlugin.getInstance().getRegistry().getExtendableMetamodelUris();
            this.cbxMetamodelUris.setItems(items.toArray(new String[items.size()]));
            this.cbxMetamodelUris.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateMetamodelUri();
                }
            });
            final CCombo cbx = this.cbxMetamodelUris;
            this.cbxMetamodelUris.addControlListener(new ControlAdapter() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ControlAdapter#controlResized(org.eclipse.swt.events.ControlEvent)
                 */
                @Override
                public void controlResized( ControlEvent e ) {
                    cbx.setSize(txtNamespaceUri.getSize());
                }
            });
        }

        RESOURCE_PATH: {
            toolkit.createLabel(container, Messages.resourcePathLabel);

            this.txtResourcePath = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.READ_ONLY | SWT.BORDER);
            this.txtResourcePath.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
            ((GridData)this.txtResourcePath.getLayoutData()).verticalIndent += ((GridLayout)container.getLayout()).verticalSpacing;
            this.txtResourcePath.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateResourcePath();
                    updateMessage();
                }
            });
        }

        VERSION: {
            toolkit.createLabel(container, Messages.versionLabel);

            this.txtVersion = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER);
            this.txtVersion.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
            this.txtVersion.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateVersion();
                    updateMessage();
                }
            });

            if (this.med == null) {
                this.txtVersion.setText(Integer.toString(ModelExtensionDefinitionHeader.DEFAULT_VERSION));
            } else {
                this.txtVersion.setText(Integer.toString(this.med.getVersion()));
            }
        }

        DESCRIPTION: {
            Label label = toolkit.createLabel(container, Messages.descriptionLabel);
            label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

            this.txtDescription = toolkit.createText(container, CoreStringUtil.Constants.EMPTY_STRING, SWT.BORDER | SWT.MULTI
                    | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
            this.txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
            this.txtDescription.addModifyListener(new ModifyListener() {

                /**
                 * {@inheritDoc}
                 * 
                 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
                 */
                @Override
                public void modifyText( ModifyEvent e ) {
                    validateDescription();
                    updateMessage();
                }
            });
        }
    }

    private ModelExtensionRegistry getRegistry() {
        return ExtensionPlugin.getInstance().getRegistry();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
     */
    @Override
    public String getTitleToolTip() {
        return Messages.medEditorOverviewPageToolTip;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.forms.editor.FormPage#setFocus()
     */
    @Override
    public void setFocus() {
        if (this.txtNamespacePrefix != null) {
            this.txtNamespacePrefix.setFocus();
        }
    }

    void updateMessage() {
        String message = null;
        int messageType = IMessageProvider.NONE;

        if (!CoreStringUtil.isEmpty(this.resourcePathValidationMsg)) {
            message = this.resourcePathValidationMsg;
        } else if (!CoreStringUtil.isEmpty(this.namespacePrefixValidationMsg)) {
            message = this.namespacePrefixValidationMsg;
        } else if (!CoreStringUtil.isEmpty(this.namespaceUriValidationMsg)) {
            message = this.namespaceUriValidationMsg;
        } else if (!CoreStringUtil.isEmpty(this.metamodelUriValidationMsg)) {
            message = this.metamodelUriValidationMsg;
        } else if (!CoreStringUtil.isEmpty(this.versionValidationMsg)) {
            message = this.versionValidationMsg;
        } else if (!CoreStringUtil.isEmpty(this.descriptionValidationMsg)) {
            message = this.descriptionValidationMsg;
        }

        if (!CoreStringUtil.isEmpty(message)) {
            messageType = IMessageProvider.ERROR;
        }

        // only set message if different than current message
        if ((this.form.getMessageType() != messageType) || !CoreStringUtil.equals(message, this.form.getMessage())) {
            this.form.setMessage(message, messageType);
        }
    }

    private void validateAll() {
        validateDescription();
        validateMetamodelUri();
        validateNamespacePrefix();
        validateNamespaceUri();
        validateResourcePath();
        validateVersion();
    }

    void validateDescription() {
        this.descriptionValidationMsg = ModelExtensionDefinitionValidator.validateDescription(this.txtDescription.getText());
    }

    void validateMetamodelUri() {
        this.metamodelUriValidationMsg = ModelExtensionDefinitionValidator.validateMetamodelUri(this.cbxMetamodelUris.getText(),
                                                                                                getRegistry());
    }

    void validateNamespacePrefix() {
        this.namespacePrefixValidationMsg = ModelExtensionDefinitionValidator.validateNamespacePrefix(this.txtNamespacePrefix.getText());
    }

    void validateNamespaceUri() {
        this.namespaceUriValidationMsg = ModelExtensionDefinitionValidator.validateNamespaceUri(this.txtNamespaceUri.getText());
    }

    void validateResourcePath() {
        this.resourcePathValidationMsg = ModelExtensionDefinitionValidator.validateResourcePath(this.txtResourcePath.getText());
    }

    void validateVersion() {
        this.resourcePathValidationMsg = ModelExtensionDefinitionValidator.validateVersion(this.txtVersion.getText());
    }

}
